package com.revantis.vosdroid.lib;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;

import java.io.*;
import java.util.*;

/**
 * Created by ReVanTis on 2014/08/19.
 * 本类主要用于将vos格式的文件中的信息提取出来，提供给游戏用于生成note和声音
 */
public class VosParser
{
	public int progress=0;
	public String MessageString="";
    public InputStream is;
    public List<VosSegment> Segments;
	public List<VosChannel> Channels;
    public String Title ="empyt";
    private int TitleLength;
    public String Artist ="empyt";
    private int ArtistLength;
	public String Comment ="empyt";
	private int CommentLength;
    public String Author ="empyt";
    private int AuthorLength;
	int VosFileSize;
    int Header;
	int Pos;
	int MusicType;
	int MusicTypeEx;
	int VosTimeLength;
	int Level;
	public MidiFile midiFile;
	public List<VosPlayNote> playNote;
	public List<Double> Tick2MS;
	public void init()
	{
		Segments =new ArrayList<VosSegment>();
		Channels =new ArrayList<VosChannel>();
		playNote = new ArrayList<VosPlayNote>();
	}
    public VosParser(InputStream in)
    {
	    init();
	    is=in;
    }
	public VosParser(File in) throws FileNotFoundException
	{
		init();
		is=new FileInputStream(in);
	}
	public void SaveMidiFile(File midiFileToWrite) throws  Exception
	{
		midiFile.writeToFile(midiFileToWrite);
	}
    public void Parse() throws Exception
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
		//parsing vos file
	    MessageString = "Crafting Cello";
	    progress=0;
        byte[] intbuffer=new byte[4];
        try
        {
	        Pos =0;
	        VosFileSize =is.available();
            Pos +=is.read(intbuffer,0,4);
            Header = VosByte.byte2int(intbuffer);
            if(Header !=3)
	            throw new Exception("not vos file excetion");
            while(true)
            {
                VosSegment this_segment=new VosSegment();
                Pos +=is.read(this_segment.addr,0,4);
                Pos +=is.read(this_segment.name,0,16);
	            Segments.add(this_segment);
                if(this_segment.getname().charAt(0)=='E'&&this_segment.getname().charAt(1)=='O'&&this_segment.getname().charAt(2)=='F')
                {
	                break;
                }
	            else if((this_segment.name[0]==(byte)0xB1&&this_segment.name[1]==(byte)0xE0&&this_segment.name[2]==(byte)0xD0&&this_segment.name[3]==(byte)0xB4))
                {
	                break;
                }
            }
	        //dealing with INF segment here
	        byte[] bytebuffer=new byte[1];
	        byte[] infobuffer=new byte[255];


	        //title info
	        Pos +=is.read(bytebuffer,0,1);
	        TitleLength =bytebuffer[0];
	        if(TitleLength !=0)
	        {
		        Pos += is.read(infobuffer, 0, TitleLength);
		        Title = new String(infobuffer);
		        Title = Title.trim();
	        }
	        //artist info
	        Pos +=is.read(bytebuffer,0,1);
	        ArtistLength =bytebuffer[0];
	        if(ArtistLength !=0)
	        {
		        Pos += is.read(infobuffer, 0, ArtistLength);
		        Artist = new String(infobuffer);
		        Artist = Artist.trim();
	        }
	        //comment
	        Pos +=is.read(bytebuffer,0,1);
	        CommentLength =bytebuffer[0];
	        if(CommentLength !=0)
	        {
		        Pos += is.read(infobuffer, 0, CommentLength);
		        Comment = new String(infobuffer);
		        Comment = Comment.trim();
	        }
			//author
	        Pos +=is.read(bytebuffer,0,1);
	        AuthorLength =bytebuffer[0];
	        if(AuthorLength !=0)
	        {
		        Pos += is.read(infobuffer, 0, AuthorLength);
		        Author = new String(infobuffer);
		        Author = Author.trim();
	        }
	        //music type
	        Pos +=is.read(bytebuffer,0,1);
	        MusicType =bytebuffer[0];

	        //extended music type
	        Pos +=is.read(bytebuffer,0,1);
	        MusicTypeEx =bytebuffer[0];

	        //timelength
	        Pos +=is.read(intbuffer,0,4);
	        VosTimeLength=VosByte.byte2int(intbuffer);
			Pos += is.read(bytebuffer,0,1);
	        Level =bytebuffer[0];

			//00*1023
	        for(int i=0;i<1023;i++)
	        {
		        Pos +=is.read(bytebuffer,0,1);
		        if(bytebuffer[0]!=0)
		        {
			        throw new Exception("1023 00 segment, non zero detected at:"+Integer.toHexString(Pos));
		        }
	        }
			/*
            2.INF Segment 轨道信息
                1) XX XX XX XX int midi乐器编号
                2) XX XX XX XX int 本轨道note总数
                3) 00 * 14 14个字节的00,一说某版本是12字节
                4) 13* note数，每个note 13字节，note说明见VosNote.java文件
            */
	        // channel info here
	        progress=50;
	        while(true)
	        {
		        VosChannel channel=new VosChannel();
		        if(Pos >= Segments.get(1).getaddr())
		        {
			        break;
		        }
				Pos +=is.read(intbuffer,0,4);
				channel.instrument=VosByte.byte2int(intbuffer);
		        Pos +=is.read(intbuffer,0,4);
		        channel.notecount=VosByte.byte2int(intbuffer);
		        for(int i=0;i<14;i++)
		        {
			        Pos +=is.read(bytebuffer,0,1);
			        if(bytebuffer[0]!=0)
				        throw new Exception("parse channel, 14 00 non zero detected");
		        }
		        for(int i=0;i<channel.notecount;i++)
		        {
			        VosNote note = new VosNote();
			        byte[] notebuffer=new byte[13];
			        Pos +=is.read(notebuffer,0,13);
			        if(Pos > Segments.get(1).getaddr())
			        {
				        throw new Exception("parse notes, addr exception");
			        }
			        note.Parse(notebuffer);
			        channel.notes.add(note);
		        }
		        Channels.add(channel);
	        }
	        //generating midi file
	        MessageString = "Calling Orchestra to Come";
	        progress=0;
	        //MID segment
	        //生成临时文件，供midi库调用。
	        int MidiHeaderLength=is.available();
	        byte[] MidiHeaderByte=new byte[MidiHeaderLength];
			Pos+=is.read(MidiHeaderByte,0,MidiHeaderLength);
	        ByteArrayInputStream MidiHeaderByteStream=new ByteArrayInputStream(MidiHeaderByte);
			midiFile=new MidiFile(MidiHeaderByteStream);
	        List<Tempo> tempoEvent=new ArrayList<Tempo>();
			for(int i=0;i<midiFile.getTracks().size();i++)
			{
				Iterator<MidiEvent> it=midiFile.getTracks().get(i).getEvents().iterator();
				while(it.hasNext())
				{
					MidiEvent tempEvent=it.next();
					if(tempEvent instanceof Tempo)
					{
						tempoEvent.add((Tempo)tempEvent);
					}
				}
			}
			progress=10;
			//adding notes to midi
	        for(int i=0;i<16;i++)//16 = total channels
	        {
		        progress+=5;
		        long deltatimeticks=midiFile.getResolution();
		        if(Channels.get(i).notecount==0)
			        continue;
		        else
		        {
			        VosChannel tempChannel = Channels.get(i);
			        class sortBySequencer implements Comparator<VosNote>
			        {
				        @Override
				        public int compare(VosNote vosNote, VosNote vosNote2) {
					        if(vosNote.sequencer>=vosNote2.sequencer)
						        return 1;
					        else return -1;
				        }
			        }
			        Collections.sort(tempChannel.notes,new sortBySequencer());
			        MidiTrack midiTrack =new MidiTrack();
			        MidiEvent changeInstrument = new ProgramChange(0,i,tempChannel.instrument);
			        midiTrack.insertEvent(changeInstrument);
			        for(int j=0;j<tempChannel.notes.size();j++)
			        {
				        VosNote NoteToConvert = Channels.get(i).notes.get(j);
				        long thisNotedeltatime = (((NoteToConvert.sequencer*deltatimeticks)%0x300==0)?0:1)
						        + NoteToConvert.sequencer* deltatimeticks/0x300;
				        long thisNoteDuration = (((NoteToConvert.duration* deltatimeticks)%0x2E4==0)?0:1)
				                + NoteToConvert.duration*deltatimeticks /0x2E4;
				        MidiEvent thisNoteOn=new NoteOn(thisNotedeltatime,NoteToConvert.channel,NoteToConvert.pitch,NoteToConvert.volume);
				        MidiEvent thisNoteOff=new NoteOn(thisNotedeltatime+thisNoteDuration,NoteToConvert.channel,NoteToConvert.pitch,0/*NoteToConvert.volume*/);
				        midiTrack.insertEvent(thisNoteOn);
				        midiTrack.insertEvent(thisNoteOff);
			        }
			        midiFile.addTrack(midiTrack);
		        }
	        }
	        class sortBySequencer implements Comparator<VosNote>
	        {
		        @Override
		        public int compare(VosNote vosNote, VosNote vosNote2) {
			        if(vosNote.sequencer>=vosNote2.sequencer)
				        return 1;
			        else return -1;
		        }
	        }
	        Collections.sort(Channels.get(16).notes,new sortBySequencer());
	        progress=100;
			//calibrating time in milliseconds!
	        MessageString = "Calibrating Show Time with RGO";
	        progress=0;
	        Tick2MS = new ArrayList<Double>();
	        double curentMS=0;
	        long length=midiFile.getLengthInTicks();
	        for(long i=0;i<length;i++)
	        {
		        progress=(int)((float)i* 100f/(length)*1f );
		        int j=0;
		        for(;j<=tempoEvent.size();j++)
		        {
			        if(j==tempoEvent.size())
			        {
				        double ms;
				        try {

					        ms = VosByte.getTickPerMS(midiFile.getResolution(), tempoEvent.get(j - 1).getMpqn());
				        }
				        catch (ArrayIndexOutOfBoundsException e)
				        {
					        ms=VosByte.getTickPerMS(midiFile.getResolution(), 500000);//default tempo=120,mpqn=50,0000
				        }
				        curentMS+=ms;
				        Tick2MS.add(curentMS);
				        break;
			        }
			        if(i<tempoEvent.get(j).getTick())
			        {
				        double ms;
				        try {

					        ms = VosByte.getTickPerMS(midiFile.getResolution(), tempoEvent.get(j - 1).getMpqn());
				        }
				        catch (ArrayIndexOutOfBoundsException e)
				        {
					        ms=VosByte.getTickPerMS(midiFile.getResolution(), 500000);
				        }
				        curentMS+=ms;
				        Tick2MS.add(curentMS);
				        break;
			        }
		        }
	        }
	        //generating notes to play
	        progress=0;
	        MessageString = "Handing Out Music Scores to Orchestra";
	        for(int i=0;i<Channels.get(16).notes.size();i++)
	        {
		        progress=(int) ( (i*50f)/Channels.get(16).notes.size());
		        playNote.add(new VosPlayNote(Channels.get(16).notes.get(i)));
	        }
	        for(int i=0;i<playNote.size();i++)
	        {
		        progress=50+(int)(i*50f/playNote.size());
		        playNote.get(i).Tranform(midiFile.getResolution());
		        int NoteStartTick =playNote.get(i).TimeinTick;
		        if(NoteStartTick>=midiFile.getLengthInTicks())
			        NoteStartTick=(int)midiFile.getLengthInTicks()-1;
		        playNote.get(i).Time=Tick2MS.get(NoteStartTick);

		        int NoteEndTick = playNote.get(i).TimeinTick+playNote.get(i).DurationTimeinTick;
		        if(NoteEndTick>=midiFile.getLengthInTicks())
			        NoteEndTick=(int)midiFile.getLengthInTicks()-1;
				try {
					playNote.get(i).DurationTime=Tick2MS.get(NoteEndTick)-playNote.get(i).Time;
				}
				catch ( ArrayIndexOutOfBoundsException e)
		        {
			        playNote.get(i).DurationTime=Tick2MS.get((int)midiFile.getLengthInTicks()-1)-playNote.get(i).Time;
		        }
	        }
        }
        catch (Exception e)
        {
	        throw e;
        }
    }

}
