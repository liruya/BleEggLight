package com.inledco.fluvalsmart.util;

import android.graphics.Color;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 */

public class DeviceUtil
{
    //灯具类别--单色灯,蓝白双色灯,色温灯,RGB彩灯,RGBW彩灯,RGBWM彩灯
    public static final byte LIGHT_MAINID = 0x01;
    public static final short LIGHT_ID_SINGLE = 0x0101;
    public static final short LIGHT_ID_BW = 0x0102;
    public static final short LIGHT_ID_CT = 0x0103;
    public static final short LIGHT_ID_RGB = 0x0104;
    public static final short LIGHT_ID_RGBW = 0x0105;
    public static final short LIGHT_ID_RGBWM = 0x0106;
    public static final short LIGHT_ID_HAGEN_STRIP_III = 0x0111;
    public static final String LIGHT_TYPE_SINGLE = "Single Light";
    public static final String LIGHT_TYPE_BW = "White & Blue Light";
    public static final String LIGHT_TYPE_CT = "Color Temp Light";
    public static final String LIGHT_TYPE_RGB = "RGB Light";
    public static final String LIGHT_TYPE_RGBW = "RGBW Strip II";
    public static final String LIGHT_TYPE_RGBWM = "RGBWM Light";
    public static final String LIGHT_TYPE_HAGEN_STRIP_III = "Hagen Strip III";

    //插座类别--墙壁插座
    public static final byte SOCKET_MAINID = 0x02;
    public static final short SOCKET_ID_SINGLE = 0x0201;
    public static final short SOCKET_ID_DOUBLE = 0x0202;
    public static final String SOCKET_TYPE_SINGLE = "Socket _1_Channel";
    public static final String SOCKET_TYPE_Double = "Socket _2_Channel";

    private static Map<Short, String> mDeviceMap;
    private static Map<Short, Integer> mIconOnMap;
    private static Map<Short, Integer> mIconOffMap;
    private static Map<Short, Integer> mChannelColor;
    private static Map<Short, Integer> mCHannelName;

    /**
     * 初始化设备ID,类型映射表
     */
    public static void initDeviceMap()
    {
        mDeviceMap = new HashMap<>();
        mIconOnMap = new HashMap<>();
        mIconOffMap = new HashMap<>();
        mChannelColor = new HashMap<>();
        mCHannelName = new HashMap<>();

        mDeviceMap.put( LIGHT_ID_SINGLE, LIGHT_TYPE_SINGLE );
        mDeviceMap.put( LIGHT_ID_BW, LIGHT_TYPE_BW );
        mDeviceMap.put( LIGHT_ID_CT, LIGHT_TYPE_CT );
        mDeviceMap.put( LIGHT_ID_RGB, LIGHT_TYPE_RGB);
        mDeviceMap.put( LIGHT_ID_RGBW, LIGHT_TYPE_RGBW );
        mDeviceMap.put( LIGHT_ID_RGBWM, LIGHT_TYPE_RGBWM );
        mDeviceMap.put( LIGHT_ID_HAGEN_STRIP_III, LIGHT_TYPE_HAGEN_STRIP_III );
        mDeviceMap.put( SOCKET_ID_SINGLE, SOCKET_TYPE_SINGLE);
        mDeviceMap.put( SOCKET_ID_DOUBLE, SOCKET_TYPE_Double);

        mIconOnMap.put( LIGHT_ID_SINGLE, R.drawable.ic_light_blue );
        mIconOnMap.put( LIGHT_ID_BW, R.drawable.ic_light_blue );
        mIconOnMap.put( LIGHT_ID_CT, R.drawable.ic_light_blue );
        mIconOnMap.put( LIGHT_ID_RGB, R.drawable.ic_light_blue );
        mIconOnMap.put( LIGHT_ID_RGBW, R.mipmap.ic_light_rgbw_ii );
        mIconOnMap.put( LIGHT_ID_RGBWM, R.drawable.ic_light_blue );
        mIconOnMap.put( LIGHT_ID_HAGEN_STRIP_III, R.mipmap.ic_light_strip_iii);
        mIconOnMap.put( SOCKET_ID_SINGLE, R.drawable.ic_socket_blue );
        mIconOnMap.put( SOCKET_ID_DOUBLE, R.drawable.ic_socket_blue );

        mIconOffMap.put( LIGHT_ID_SINGLE, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_BW, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_CT, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_RGB, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_RGBW, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_RGBWM, R.drawable.ic_light_gray );
        mIconOffMap.put( LIGHT_ID_HAGEN_STRIP_III, R.drawable.ic_light_gray );
        mIconOffMap.put( SOCKET_ID_SINGLE, R.drawable.ic_socket_gray );
        mIconOffMap.put( SOCKET_ID_DOUBLE, R.drawable.ic_socket_gray );

        mChannelColor.put( LIGHT_ID_SINGLE, Color.DKGRAY );
    }

    /**
     * 由设备id获取设备类型
     * @param devid
     * @return
     */
    public static String getDeviceType( short devid )
    {
        if ( mDeviceMap == null )
            return null;
        if ( mDeviceMap.containsKey( devid ) )
        {
            return mDeviceMap.get( devid );
        }
        return null;
    }

    /**
     *
     * @param mid   主id
     * @param sid   子id
     * @return
     */
    public static String getDeviceType( byte mid, byte sid )
    {
        if ( mDeviceMap == null )
            return null;
        short devid = (short) ( ( mid << 8) | sid);
        if ( mDeviceMap.containsKey( devid ) )
        {
            return mDeviceMap.get( devid );
        }
        return null;
    }

    public static int getDeviceIconOn( byte mid, byte sid )
    {
        int resid = R.drawable.ic_ble_blue;
        if ( mIconOnMap == null )
            return resid;
        short devid = (short) ( ( mid << 8) | sid);
        if ( mIconOnMap.containsKey( devid ) )
        {
            return mIconOnMap.get( devid );
        }
        return resid;
    }

    public static int getDeviceIconOn( short devid )
    {
        int resid = R.drawable.ic_ble_blue;
        if ( mIconOnMap == null )
            return resid;
        if ( mIconOnMap.containsKey( devid ) )
        {
            return mIconOnMap.get( devid );
        }
        return resid;
    }

    public static int getDeviceIconOff( byte mid, byte sid )
    {
        int resid = R.drawable.ic_ble_gray;
        if ( mIconOffMap == null )
            return resid;
        short devid = (short) ( ( mid << 8) | sid);
        if ( mIconOffMap.containsKey( devid ) )
        {
            return mIconOffMap.get( devid );
        }
        return resid;
    }

    public static int getDeviceIconOff( short devid )
    {
        int resid = R.drawable.ic_ble_gray;
        if ( mIconOffMap == null )
            return resid;
        if ( mIconOffMap.containsKey( devid ) )
        {
            return mIconOffMap.get( devid );
        }
        return resid;
    }

    public static Channel[] getLightChannel( short devid )
    {
        Channel[] channels = null;
        switch ( devid )
        {
            case LIGHT_ID_SINGLE:
                channels = new Channel[]{new Channel( "White", 0xFFF07D0C, (short) 0 )};
                break;

            case LIGHT_ID_BW:
                channels = new Channel[]{ new Channel( "Blue", 0xFF0000FF, (short) 0 ),
                                          new Channel( "White", 0xFFF07D0C, (short) 0 )};
                break;

            case LIGHT_ID_CT:
                channels = new Channel[]{new Channel( "CW", 0xFFF07D0C, (short) 0 ),
                                         new Channel( "WW", 0xFFF07D0C, (short) 0 )};
                break;

            case LIGHT_ID_RGB:
                channels = new Channel[]{new Channel( "Red", 0xFFFF0000, (short) 0 ),
                                         new Channel( "Green", 0xFF00FF00, (short) 0 ),
                                         new Channel( "Blue", 0xFF0000FF, (short) 0 )};
                break;

            case LIGHT_ID_RGBW:
                channels = new Channel[]{new Channel( "Red", 0xFFFF0000, (short) 0 ),
                                         new Channel( "Green", 0xFF00FF00, (short) 0 ),
                                         new Channel( "Blue", 0xFF0000FF, (short) 0 ),
                                         new Channel( "White", 0xFFF07D0C, (short) 0 )};
                break;

            case LIGHT_ID_RGBWM:
                channels = new Channel[]{new Channel( "Red", 0xFFFF0000, (short) 0 ),
                                         new Channel( "Green", 0xFF00FF00, (short) 0 ),
                                         new Channel( "Blue", 0xFF0000FF, (short) 0 ),
                                         new Channel( "White", 0xFFF07D0C, (short) 0 ),
                                         new Channel( "MMMM", 0xFFF07D0C, (short) 0 )};
                break;

            case LIGHT_ID_HAGEN_STRIP_III:
                channels = new Channel[]{new Channel( "Red", 0xFFFF0000, (short) 0 ),
                                         new Channel( "Green", 0xFF00FF00, (short) 0 ),
                                         new Channel( "Blue", 0xFF0000FF, (short) 0 ),
                                         new Channel( "White", 0xFFFFCE3B, (short) 0 )};
                break;
        }
        return channels;
    }

    public static int[] getThumb( short devid )
    {
        int[] thumbs = null;
        switch ( devid )
        {
            case LIGHT_ID_SINGLE:
                thumbs = new int[]{R.drawable.ic_thumb_red};
                break;
            case LIGHT_ID_BW:
                thumbs = new int[]{R.drawable.ic_thumb_blue, R.drawable.ic_thumb_white};
                break;
            case LIGHT_ID_CT:
                thumbs = new int[]{R.drawable.ic_thumb_white, R.drawable.ic_thumb_white};
                break;
            case LIGHT_ID_RGB:
                thumbs = new int[]{R.drawable.ic_thumb_red, R.drawable.ic_thumb_green, R.drawable.ic_thumb_blue};
                break;
            case LIGHT_ID_RGBW:
                thumbs = new int[]{R.drawable.ic_thumb_red, R.drawable.ic_thumb_green, R.drawable.ic_thumb_blue, R.drawable.ic_thumb_white};
                break;
            case LIGHT_ID_RGBWM:
                thumbs = new int[]{R.drawable.ic_thumb_red, R.drawable.ic_thumb_green, R.drawable.ic_thumb_blue, R.drawable.ic_thumb_white, R.drawable.ic_thumb_white};
                break;
            case LIGHT_ID_HAGEN_STRIP_III:
                thumbs = new int[]{R.drawable.ic_thumb_red, R.drawable.ic_thumb_green, R.drawable.ic_thumb_blue, R.drawable.ic_thumb_white};
                break;
        }
        return thumbs;
    }

    public static int[] getSeekbar( short devid )
    {
        int[] seekBars = null;
        switch ( devid )
        {
            case LIGHT_ID_SINGLE:
                seekBars = new int[]{R.drawable.custom_seekebar_white};
                break;
            case LIGHT_ID_BW:
                seekBars = new int[]{R.drawable.custom_seekebar_blue, R.drawable.custom_seekebar_white};
                break;
            case LIGHT_ID_CT:
                seekBars = new int[]{R.drawable.custom_seekebar_white, R.drawable.custom_seekebar_white};
                break;
            case LIGHT_ID_RGB:
                seekBars = new int[]{R.drawable.custom_seekebar_red, R.drawable.custom_seekebar_green, R.drawable.custom_seekebar_blue};
                break;
            case LIGHT_ID_RGBW:
                seekBars = new int[]{R.drawable.custom_seekebar_red, R.drawable.custom_seekebar_green, R.drawable.custom_seekebar_blue, R.drawable.custom_seekebar_white};
                break;
            case LIGHT_ID_RGBWM:
                seekBars = new int[]{R.drawable.custom_seekebar_red, R.drawable.custom_seekebar_green, R.drawable.custom_seekebar_blue, R.drawable.custom_seekebar_white, R.drawable.custom_seekebar_white};
                break;
            case LIGHT_ID_HAGEN_STRIP_III:
                seekBars = new int[]{R.drawable.custom_seekebar_red, R.drawable.custom_seekebar_green, R.drawable.custom_seekebar_blue, R.drawable.custom_seekebar_white};
                break;
        }
        return seekBars;
    }

    public static byte[] getDayBright( short devid )
    {
        byte[] brts = null;
        switch ( devid )
        {
            case LIGHT_ID_RGBW:
                brts = new byte[]{100, 100, 100, 100};
                break;
            case LIGHT_ID_HAGEN_STRIP_III:
                brts = new byte[]{100, 100, 100, 100};
                break;
        }

        return brts;
    }

    public static byte[] getNightBright( short devid )
    {
        byte[] brts = null;
        switch ( devid )
        {
            case LIGHT_ID_RGBW:
                brts = new byte[]{0, 0, 5, 0};
                break;
            case LIGHT_ID_HAGEN_STRIP_III:
                brts = new byte[]{0, 0, 5, 0};
                break;
        }

        return brts;
    }
}