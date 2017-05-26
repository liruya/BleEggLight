package com.inledco.fluvalsmart.bean;

/**
 * Created by liruya on 2016/10/31.
 */

public class ReceiveEvent
{
    private byte[] mBytes;

    public ReceiveEvent ( byte[] bytes )
    {
        mBytes = bytes;
    }

    public byte[] getBytes ()
    {
        return mBytes;
    }

    public void setBytes ( byte[] bytes )
    {
        mBytes = bytes;
    }
}
