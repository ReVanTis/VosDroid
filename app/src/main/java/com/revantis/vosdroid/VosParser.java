package com.revantis.vosdroid;
import android.util.Log;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Created by ReVanTis on 2014/08/19.
 * 本类主要用于将vos格式的文件中的信息提取出来，提供给游戏用于生成note和声音
 */
public class VosParser
{

    public InputStream is;
    public List<VosSegment> segments;
	public List<VosChannel> channels;
    public String title="empyt";
    private int title_length;
    public String artist="empyt";
    private int artist_length;
	public String comment="empyt";
	private int comment_length;
    public String author="empyt";
    private int author_length;
	int filesize;
    int header;
	int pos;
	int musictype;
	int musictype_ex;
	int timelength;
	int level;
    public VosParser(InputStream in)
    {
	    is=in;
	    segments=new ArrayList<VosSegment>();
	    channels=new ArrayList<VosChannel>();
    }
    public void Parse()
    {
        /*流程可以概括如下
        I.4字节03 00 00 00头部
        II.Segments
            1. XX XX XX XX 4字节地址,Big Endian int
            2. gb2312 String 16字节的名字
        III
            1.INF Segment 基本信息
                1) XX 1字节，标题长度
                2) 上述长度的标题内容
                3) XX 1字节，艺术家长度
                4) 上述长度的艺术家内容
                5) XX 1字节，评论长度以及评论，通常为00，若不为0则考虑形式同上
                6) XX 1字节，作者长度
                7) 上述长度的作者内容

                8) XX，1字节 音乐类型
                    01 "Pop"
                    02 "New Age"
                    03 "Techno"
                    04 "Rock"
                    05 "SoundTrack"
                    06 "Game&Anime"
                    07 "Jazz"
                    08 "CenturyEnd"
                    09 "Classical"
                    0a "Other"
                9) XX，1字节，额外音乐类型。
                10) XX XX XX XX 总时间长度，单位毫秒(ms)，Big Endian int
                11) 1023字节00
        */

        byte[] intbuffer=new byte[4];
        try
        {
	        pos=0;
	        filesize=is.available();
	        Log.d("parse","filesize:"+filesize+" b=0x"+Integer.toHexString(filesize)+"b");
            pos+=is.read(intbuffer,0,4);
            header = VosByte.byte2int(intbuffer);
            if(header!=3)
	            throw new Exception("not vos file excetion");
            while(true)
            {
                VosSegment this_segment=new VosSegment();
                pos+=is.read(this_segment.addr,0,4);
                pos+=is.read(this_segment.name,0,16);
	            Log.d("parse","segment:"+this_segment.getname()+"addr:0x"+Integer.toHexString(this_segment.getaddr()));
                if(this_segment.getname().compareTo("EOF")!=0)
                {
	                segments.add(this_segment);
                }
	            else
                {
	                segments.add(this_segment);
	                break;
                }
            }
	        //dealing with INF segment here
	        byte[] bytebuffer=new byte[1];
	        byte[] infobuffer=new byte[255];


	        //title info
	        pos+=is.read(bytebuffer,0,1);
	        title_length=bytebuffer[0];
	        if(title_length!=0)
	        {
		        pos += is.read(infobuffer, 0, title_length);
		        title = new String(infobuffer);
		        title=title.trim();
	        }
	        //artist info
	        pos+=is.read(bytebuffer,0,1);
	        artist_length=bytebuffer[0];
	        if(artist_length!=0)
	        {
		        pos += is.read(infobuffer, 0, artist_length);
		        artist= new String(infobuffer);
		        artist=artist.trim();
	        }
	        //comment
	        pos+=is.read(bytebuffer,0,1);
	        comment_length=bytebuffer[0];
	        if(comment_length!=0)
	        {
		        pos += is.read(infobuffer, 0, comment_length);
		        comment= new String(infobuffer);
		        comment=comment.trim();
	        }
			//author
	        pos+=is.read(bytebuffer,0,1);
	        author_length=bytebuffer[0];
	        if(author_length!=0)
	        {
		        pos += is.read(infobuffer, 0, author_length);
		        author= new String(infobuffer);
		        author=author.trim();
	        }
	        //music type
	        pos+=is.read(bytebuffer,0,1);
	        musictype=bytebuffer[0];

	        //extended music type
	        pos+=is.read(bytebuffer,0,1);
	        musictype_ex=bytebuffer[0];

	        //timelength
	        pos+=is.read(intbuffer,0,4);
	        timelength=VosByte.byte2int(intbuffer);
			pos+= is.read(bytebuffer,0,1);
	        level=bytebuffer[0];

	        Log.d("title",title);
	        Log.d("artist",artist);
	        Log.d("comment",comment);
	        Log.d("author",author);
	        Log.d("musictype",musictype+"");
	        Log.d("musictype_ex",musictype_ex+"");
	        Log.d("timelength",timelength+"");
	        Log.d("level",level+"");
			//00*1023
	        for(int i=0;i<1023;i++)
	        {
		        pos+=is.read(bytebuffer,0,1);
		        if(bytebuffer[0]!=0)
		        {
			        throw new Exception("1023 00 segment, non zero detected at:"+Integer.toHexString(pos));
		        }
	        }
	        Log.d("parse","after 1023,pos=0x"+Integer.toHexString(pos));
			/*
            2.INF Segment 轨道信息
                1) XX XX XX XX int midi乐器编号
                2) XX XX XX XX int 本轨道note总数
                3) 00 * 14 14个字节的00,一说某版本是12字节
                4) 13* note数，每个note 13字节，note说明见VosNote.java文件
            */
	        // channel info here
	        while(true)
	        {
		        VosChannel channel=new VosChannel();
		        if(pos>=segments.get(1).getaddr())
		        {
			        break;
		        }
				pos+=is.read(intbuffer,0,4);
				channel.instrument=VosByte.byte2int(intbuffer);
		        pos+=is.read(intbuffer,0,4);
		        channel.notecount=VosByte.byte2int(intbuffer);
		        for(int i=0;i<14;i++)
		        {
			        pos+=is.read(bytebuffer,0,1);
			        if(bytebuffer[0]!=0)
				        throw new Exception("parse channel, 14 00 non zero detected");
		        }
		        for(int i=0;i<channel.notecount;i++)
		        {
			        VosNote note = new VosNote();
			        byte[] notebuffer=new byte[13];
			        pos+=is.read(notebuffer,0,13);
			        if(pos>segments.get(1).getaddr())
			        {
				        throw new Exception("parse notes, addr exception");
			        }
			        note.Parse(notebuffer);
			        channel.notes.add(note);
		        }
		        Log.d("parsenote","note"+channel.notes.size()+"parsed");
		        channels.add(channel);
		        Log.d("parsechannel","channel"+channels.size()+" parsed");
	        }
	        Log.d("parse","after channels,pos=0x"+Integer.toHexString(pos));
	        //MID segment
	        //TODO 在这里处理MID segment
        }
        catch (Exception e)
        {
			Log.e("parse:",e.getMessage());
        }
    }
}
