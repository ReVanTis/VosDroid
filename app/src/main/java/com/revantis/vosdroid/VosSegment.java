package com.revantis.vosdroid;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Created by ReVanTis on 2014/08/19.
 */
public class VosSegment
{
    public byte[] addr=new byte[4];
    public byte[] name=new byte[16];
    public VosSegment()
    {

    }
    public String getname()
    {
        //TODO:这里注意可能出现的编码问题.
        String rt=new String(name);
	    rt=rt.trim();
        return rt;
    }
    public Integer getaddr()
    {
	    int rt = java.nio.ByteBuffer.wrap(addr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return rt;
    }
}
