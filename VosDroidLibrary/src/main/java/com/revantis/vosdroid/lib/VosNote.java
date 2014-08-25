package com.revantis.vosdroid.lib;

import java.util.Arrays;

/**
 * Created by ReVanTis on 2014/08/20.
 */
public class VosNote {
	public int sequencer;
	public int duration;
	public byte channel;
	public byte pitch;
	public byte volume;
	public byte keyboard;
	public byte type;
	public void Parse(byte[] raw) throws Exception
	{
		if(raw.length!=13)
		{
			throw new Exception("note size not 13b exception");
		}
		else
		{
			/*
			0. XX XX XX XX 音序
			4. XX XX XX XX 音长
			8. XX 音轨
			9. XX 音高
			a. XX 音量
			b. XX 键位
			c. XX 标志位
			// Bit 0-3: color,?assuming RGB?
			// Bit 4-6: corresponding key (do, re, mi, fa, so, la, ti)
			// Bit 7: set for notes played by the user
			// Bit 8: set for long notes
			*/
			byte[] seq=Arrays.copyOfRange(raw,0,4);
			byte[] dur=Arrays.copyOfRange(raw,4,8);
			sequencer=VosByte.byte2int(seq);
			duration=VosByte.byte2int(dur);
			channel=raw[8];
			pitch=raw[9];
			volume=raw[10];
			keyboard=raw[11];
			type=raw[12];
		}
	}
}
