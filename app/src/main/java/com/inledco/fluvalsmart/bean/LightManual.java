package com.inledco.fluvalsmart.bean;

import java.io.Serializable;

/**
 * Created by liruya on 2016/11/23.
 */

public class LightManual implements Serializable
{
    private static final long serialVersionUID = -5609114933945473748L;
    private boolean mOnRGB;
    private boolean mOnCT;
    private int mRed;
    private int mGreen;
    private int mBlue;
    private int mRGB_Brt;
    private int mCT;
    private int mCT_Brt;

    public LightManual ( boolean onRGB, boolean onCT, int red, int green, int blue, int RGB_Brt, int CT, int CT_Brt )
    {
        mOnRGB = onRGB;
        mOnCT = onCT;
        mRed = red;
        mGreen = green;
        mBlue = blue;
        mRGB_Brt = RGB_Brt;
        mCT = CT;
        mCT_Brt = CT_Brt;
    }

    public int getBlue ()
    {
        return mBlue;
    }

    public void setBlue ( int blue )
    {
        mBlue = blue;
    }

    public int getCT ()
    {
        return mCT;
    }

    public void setCT ( int CT )
    {
        mCT = CT;
    }

    public int getCT_Brt ()
    {
        return mCT_Brt;
    }

    public void setCT_Brt ( int CT_Brt )
    {
        mCT_Brt = CT_Brt;
    }

    public int getGreen ()
    {
        return mGreen;
    }

    public void setGreen ( int green )
    {
        mGreen = green;
    }

    public boolean isOnCT ()
    {
        return mOnCT;
    }

    public void setOnCT ( boolean onCT )
    {
        mOnCT = onCT;
    }

    public boolean isOnRGB ()
    {
        return mOnRGB;
    }

    public void setOnRGB ( boolean onRGB )
    {
        mOnRGB = onRGB;
    }

    public int getRed ()
    {
        return mRed;
    }

    public void setRed ( int red )
    {
        mRed = red;
    }

    public int getRGB_Brt ()
    {
        return mRGB_Brt;
    }

    public void setRGB_Brt ( int RGB_Brt )
    {
        mRGB_Brt = RGB_Brt;
    }
}
