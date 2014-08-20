package com.revantis.vosdroid;
import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Created by ReVanTis on 2014/08/19.
 */
public class VosParser
{
    static public int byte2int(byte[] bytes )
    {
        //Big Endian Byte2Int Conversion
	    //大端4byte转换为int
        int rt;
        if(bytes.length==4)
        {
	        rt = java.nio.ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
            return rt;
        }
        else
        {
            return -1;
        }

    }
    public InputStream is;
    public List<VosSegment> segments;
    public String title;
    private int title_lenth;
    public String artist;
    private int artist_lenth;
    public String author;
    private int author_lenth;
	int length;
    int header;
	int pos;
    public VosParser(InputStream in)
    {
	    is=in;
	    segments=new ArrayList<VosSegment>();
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
                10) XX XX XX XX 总时间长度，Big Endian int
                11) 1023字节00
        */

        byte[] intbuffer=new byte[4];
        try
        {
	        pos=0;
	        length=is.available();
            pos+=is.read(intbuffer,0,4);
            header = byte2int(intbuffer);
            if(header!=3)
	            throw new Exception("not vos file excetion");
            while(true)
            {
                VosSegment this_segment=new VosSegment();
                pos+=is.read(this_segment.addr,0,4);
                pos+=is.read(this_segment.name,0,16);
                if(this_segment.getname().compareTo("EOF")!=0)
                {
	                segments.add(this_segment);
					continue;
                }
	            else
                {
	                segments.add(this_segment);
	                break;
                }
            }
        }
        catch (Exception e)
        {

        }

        /*
            2.INF Segment 轨道信息
            TODO：完成上一部分以后记得补全这里。
         */
    }
}
