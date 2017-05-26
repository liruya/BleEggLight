package com.inledco.fluvalsmart.bean;

import java.io.Serializable;

/**
 * Created by liruya on 2016/11/23.
 */

public class LightAuto implements Serializable
{
    private static final long serialVersionUID = 7284666673318500458L;
    private RampTime mSunrise;
    private byte[] mDayBright;
    private RampTime mSunset;
    private byte[] mNightBright;

    public LightAuto ( RampTime sunrise, byte[] dayBright, RampTime sunset, byte[] nightBright )
    {
        mSunrise = sunrise;
        mDayBright = dayBright;
        mSunset = sunset;
        mNightBright = nightBright;
    }

    public RampTime getSunrise ()
    {
        return mSunrise;
    }

    public void setSunrise ( RampTime sunrise )
    {
        mSunrise = sunrise;
    }

    public byte[] getDayBright ()
    {
        return mDayBright;
    }

    public void setDayBright ( byte[] dayBright )
    {
        mDayBright = dayBright;
    }

    public RampTime getSunset ()
    {
        return mSunset;
    }

    public void setSunset ( RampTime sunset )
    {
        mSunset = sunset;
    }

    public byte[] getNightBright ()
    {
        return mNightBright;
    }

    public void setNightBright ( byte[] nightBright )
    {
        mNightBright = nightBright;
    }
}
