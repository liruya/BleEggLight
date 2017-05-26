package com.inledco.fluvalsmart.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/20.
 */

public class DevicePrefer implements Serializable
{
    private static final long serialVersionUID = -3359140079103510542L;
    private byte mMainType;
    private byte mSubType;
    private String mDeviceMac;
    private String mDeviceName;
    private String mTank;

    public DevicePrefer ( String deviceMac, String deviceName )
    {
        mDeviceMac = deviceMac;
        mDeviceName = deviceName;
    }

    public DevicePrefer ( byte mainType, byte subType, String deviceMac, String deviceName )
    {
        mMainType = mainType;
        mSubType = subType;
        mDeviceMac = deviceMac;
        mDeviceName = deviceName;
    }

    public DevicePrefer ( byte mainType, byte subType, String deviceMac, String deviceName, String tank )
    {
        mMainType = mainType;
        mSubType = subType;
        mDeviceMac = deviceMac;
        mDeviceName = deviceName;
        mTank = tank;
    }

    public byte getMainType ()
    {
        return mMainType;
    }

    public void setMainType ( byte mainType )
    {
        mMainType = mainType;
    }

    public byte getSubType ()
    {
        return mSubType;
    }

    public void setSubType ( byte subType )
    {
        mSubType = subType;
    }

    public String getDeviceName ()
    {
        return mDeviceName;
    }

    public void setDeviceName ( String deviceName )
    {
        mDeviceName = deviceName;
    }

    public String getDeviceMac ()
    {
        return mDeviceMac;
    }

    public void setDeviceMac ( String deviceMac )
    {
        mDeviceMac = deviceMac;
    }

    public String getTank ()
    {
        return mTank;
    }

    public void setTank ( String tank )
    {
        mTank = tank;
    }

    public short getDevID()
    {
        short devid = (short) ( ( mMainType << 8) | mSubType);
        return devid;
    }
}
