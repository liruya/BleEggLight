package com.inledco.fluvalsmart.util;

import com.inledco.fluvalsmart.bean.LightManual;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 与硬件设备通信工具类
 * Created by liruya on 2016/8/26.
 */
public class CommUtil
{
    private static final String TAG = "CommUtil";

    private static final int MAX_SEND_BYTES = 17;

    public static final byte FRM_HDR = 0x68;
    public static final byte LED_OFF = 0x00;
    public static final byte LED_RGB = 0x01;
    public static final byte LED_CT = 0x02;
    public static final byte CMD_SWITCH = 0x03;
    public static final byte CMD_CTRL = 0x04;
    public static final byte CMD_READ = 0x05;
    public static final byte CMD_TIMER = 0x06;
    public static final byte CMD_SETTIMER = 0x07;
    public static final byte CMD_READTIME = 0x0D;
    public static final byte CMD_SYNCTIME = 0x0E;
    public static final byte CMD_FIND = 0x0F;

    public static ArrayList<Byte> mRcvBytes = new ArrayList<>();

    /**
     * 计算异或校验值
     * @param array   数组
     * @param len     长度
     * @return        返回校验值
     */
    public static byte getCRC(byte[] array, int len)
    {
        byte crc = 0x00;
        for ( int i = 0; i < len; i++ )
        {
            crc ^= array[i];
        }
        return crc;
    }

    /**
     *
     * @param bytes
     * @param len
     * @return
     */
    public static byte getCRC(ArrayList<Byte> bytes, int len)
    {
        byte crc = 0x00;
        for ( int i = 0; i < len; i++ )
        {
            crc ^= bytes.get( i );
        }
        return crc;
    }

    /**
     * 打开LED RGB
     * @param mac 地址
     */
    public static void turnOnRGB(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_SWITCH, LED_RGB, FRM_HDR^CMD_SWITCH^LED_RGB}, true );
    }

    /**
     * 打开LED CT
     * @param mac 地址
     */
    public static void turnOnCT(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_SWITCH, LED_CT, FRM_HDR^CMD_SWITCH^LED_CT}, true );
    }

    /**
     * 关闭LED
     * @param mac 地址
     */
    public static void turnOffLed(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_SWITCH, LED_OFF, FRM_HDR^CMD_SWITCH^LED_OFF}, true );
    }

    /**
     * 设置LED运行参数
     * @param mac       地址
     * @param value     要设置的值
     */
//    public static void setLed(String mac, short[] value)
//    {
//        byte[] txs = new byte[3+value.length*2];
//        txs[0] = FRM_HDR;
//        txs[1] = CMD_CTRL;
//        for ( int i = 0; i < value.length; i++ )
//        {
//            txs[2+2*i] = (byte) ( value[i] >> 8 );
//            txs[3+2*i] = (byte) ( value[i] & 0xFF );
//        }
//        txs[txs.length-1] = getCRC( txs, txs.length-1);
//        BleUtil.mBleService.send( mac, txs, true );
//    }

    /**
     * 设置led
     * @param mac
     * @param bytes
     */
    public static void setLed(String mac, byte[] bytes)
    {
        //FRM_HDR CMD_CTRL rgb_on r g b brt xor
        BleUtil.mBleService.send( mac, bytes, true );
    }

    /**
     * 读取设备运行状态
     * @param mac 地址
     */
    public static void readDevice(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_READ, FRM_HDR^CMD_READ}, true );
    }

    public static Object decodeLight ( ArrayList<Byte> bytes, short devid )
    {
        LightManual lightManual = null;
        int len = bytes.size();
        if ( bytes.get( 0 ) == FRM_HDR && bytes.get( 1 ) == CMD_READ && len == 10 && getCRC( bytes, len - 1 ) == bytes.get( len-1 ) )
        {
            switch ( bytes.get( 2 ) )
            {
                case 0x00:
                    lightManual = new LightManual( false,
                                                   false,
                                                   bytes.get( 3 ),
                                                   bytes.get( 4 ),
                                                   bytes.get( 5 ),
                                                   bytes.get( 6 ),
                                                   bytes.get( 7 ),
                                                   bytes.get( 8 ) );
                    break;

                case 0x01:
                    lightManual = new LightManual( true,
                                                   false,
                                                   bytes.get( 3 ),
                                                   bytes.get( 4 ),
                                                   bytes.get( 5 ),
                                                   bytes.get( 6 ),
                                                   bytes.get( 7 ),
                                                   bytes.get( 8 ) );
                    break;

                case 0x02:
                    lightManual = new LightManual( false,
                                                   true,
                                                   bytes.get( 3 ),
                                                   bytes.get( 4 ),
                                                   bytes.get( 5 ),
                                                   bytes.get( 6 ),
                                                   bytes.get( 7 ),
                                                   bytes.get( 8 ) );
                    break;

                case 0x03:

                    break;
            }
            return lightManual;
        }
        else
        {

        }
        return null;
    }

    public static void findDevice(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_FIND, FRM_HDR^CMD_FIND}, true );
    }

    public static void syncDeviceTime(String mac)
    {
        Calendar calendar = Calendar.getInstance();
        byte year = (byte) (calendar.get( Calendar.YEAR ) - 2000);
        byte month = (byte) calendar.get( Calendar.MONTH );
        byte day = (byte) calendar.get( Calendar.DAY_OF_MONTH );
        byte wk = (byte) (calendar.get( Calendar.DAY_OF_WEEK ) - 1);
        byte hour = (byte) calendar.get( Calendar.HOUR_OF_DAY );
        byte minute = (byte) calendar.get( Calendar.MINUTE );
        byte second = (byte) calendar.get( Calendar.SECOND );
        byte[] ct = new byte[]{ FRM_HDR,
                                CMD_SYNCTIME,
                                year,
                                month,
                                day,
                                wk,
                                hour,
                                minute,
                                second,
                                (byte) ( FRM_HDR ^ CMD_SYNCTIME ^ year ^ month ^ day ^ wk ^ hour ^ minute ^ second) };
        BleUtil.mBleService.send( mac, ct, true );
    }

    public static void readDeviceTime(String mac)
    {
        BleUtil.mBleService.send( mac, new byte[]{FRM_HDR, CMD_READTIME, FRM_HDR^CMD_READTIME}, true );
    }
}
