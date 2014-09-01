package com.revantis.vosdroid.lib;

/**
 * Created by ReVanTis on 2014/08/27.
 */
public class VosPlayNote extends  VosNote
{
	public double Time;
	public double DurationTime;
	public long TimeinTick;//in ticks
	public long DurationTimeinTick;//in ticks
	public int Track;
	public boolean LongNote;
	public int Color;
//	enum NoteColor
//	{
//		RED,ORANGE,YELLOW,GREENYELLOW,GREEN,CYAN,TURQUOISE,BLUE,NAVY,PURPLE,VIOLET,PINK,SNOW,ANTIQUE,BISQUE,PEACHPUFF;
//	}
	public VosPlayNote()
	{

	}
	public VosPlayNote(VosNote notes)
	{
		this.duration=notes.duration;
		this.sequencer=notes.sequencer;
		this.channel=notes.channel;
		this.keyboard= notes.keyboard;
		this.pitch=notes.pitch;
		this.volume=notes.volume;
		this.type=notes.type;
	}
	@Override
	public void Parse(byte[] raw) throws Exception
	{
		super.Parse(raw);
	}
	public void Tranform(long resolution)
	{
		TimeinTick = (((sequencer*resolution)%0x300==0)?0:1)
				+ sequencer* resolution/0x300;
		DurationTimeinTick = (((duration* resolution)%0x2E4==0)?0:1)
				+ duration*resolution /0x2E4;
		Track =((keyboard>>4)-0x8)&0xF;
		Color=(keyboard&0x0000000F);
		if( (type&0x80)==0x80 )
		{
			LongNote=true;
		}
		else LongNote=false;
	}
}
