package com.revantis.vosdroid.lib;

import java.nio.ByteOrder;

/**
 * Created by ReVanTis on 2014/08/20.
 */
public  class VosByte {
	static public double getTickPerMS(long TickPerBeat ,long MicrosecondsPerBeat)
	{
		return (double)MicrosecondsPerBeat/(double)TickPerBeat/(double)1000;
	}
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
}
