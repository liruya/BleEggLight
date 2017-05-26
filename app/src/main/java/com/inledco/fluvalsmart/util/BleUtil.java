package com.inledco.fluvalsmart.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.ble.ble.constants.BleRegConstants;
import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.MessageEvent;
import com.inledco.fluvalsmart.constant.DebugConfig;
import com.inledco.fluvalsmart.prefer.Setting;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * ble工具类
 * Created by liruya on 2016/8/26.
 */
public class BleUtil
{
    private static final String TAG = "BleUtil";

    public static final int REQUEST_ENABLE_BLE = 1;

    private static long msc = System.currentTimeMillis();

    public static BluetoothAdapter mAdapter;
    public static BleService mBleService;
    private static BleCallBack mBleCallBack = new BleCallBack() {
        @Override
        public void onConnected ( String s )
        {
            super.onConnected( s );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onConnected: " + s );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_CONNECTED, s ) );
        }

        @Override
        public void onConnectTimeout ( String s )
        {
            super.onConnectTimeout( s );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onConnectTimeout: " + s );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_CONNECT_TIMEOUT, s ) );
        }

        @Override
        public void onConnectionError ( String s, int i, int i1 )
        {
            super.onConnectionError( s, i, i1 );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onConnectionError: " + s + "\t" + i + "\t" + i1 );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_CONNECTION_ERROR, s ) );
        }

        @Override
        public void onDisconnected ( String s )
        {
            super.onDisconnected( s );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onDisconnected: " + s );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_DISCONNECTED, s ) );
        }

        @Override
        public void onServicesDiscovered ( String s )
        {
            super.onServicesDiscovered( s );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onServicesDiscovered: " + s );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_SERVICES_DISCOVERED, s) );
        }

        @Override
        public void onServicesUndiscovered ( String s, int i )
        {
            super.onServicesUndiscovered( s, i );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onServicesUndiscovered: " + s + "\t" + i );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_SERVICES_UNDISCOVERED, s ) );
        }

        @Override
        public void onCharacteristicRead ( String s, byte[] bytes, int i )
        {
            super.onCharacteristicRead( s, bytes, i );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onCharacteristicRead: " + s + "\t" + DataUtil.byteArrayToHex( bytes ) + "\t" + i );
            }
        }

        @Override
        public void onCharacteristicRead ( String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i )
        {
            super.onCharacteristicRead( s, bluetoothGattCharacteristic, i );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onCharacteristicRead: " + s + "\t" + bluetoothGattCharacteristic.getStringValue( 0 ) + "\t" + i );
            }
        }

        @Override
        public void onRegRead ( String s, String s1, int i, int i1 )
        {
            super.onRegRead( s, s1, i, i1 );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onRegRead: " + s + "\t" + s1  + "\t" + i + "\t" + i1);
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_REG_READ, s) );
        }

        @Override
        public void onCharacteristicChanged ( String s, byte[] bytes )
        {
            super.onCharacteristicChanged( s, bytes );
            if ( DebugConfig.mDebugEnabled )
            {
                Log.e( TAG, "onCharacteristicChanged: " + s + "\t" + DataUtil.byteArrayToHex( bytes ) );
            }
            long ct = System.currentTimeMillis();
            if ( ct - msc > 100 )
            {
                CommUtil.mRcvBytes.clear();
            }
            msc = ct;
            for ( byte b : bytes )
            {
                CommUtil.mRcvBytes.add( b );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_RECEIVE, s ) );
        }

        @Override
        public void onCharacteristicChanged ( String s, BluetoothGattCharacteristic bluetoothGattCharacteristic )
        {
            super.onCharacteristicChanged( s, bluetoothGattCharacteristic );
        }

        @Override
        public void onCharacteristicWrite ( String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i )
        {
            super.onCharacteristicWrite( s, bluetoothGattCharacteristic, i );
            if ( DebugConfig.mDebugEnabled )
            {
//                Log.e( TAG, "onCharacteristicWrite: " + s + "\t" + bluetoothGattCharacteristic.getStringValue( 0 ) + "\t" + i );
            }
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_SEND, s ) );
        }
    };

    private static final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected ( ComponentName name, IBinder service )
        {
            mBleService = (( BleService.LocalBinder ) service).getService( mBleCallBack );
            mBleService.setDecode( true );
            //必须调用初始化方法
            mBleService.initialize();
            Log.e( TAG, "onServiceConnected: " );
            EventBus.getDefault().post( new MessageEvent( MessageEvent.MSG_EVT_BLE_INIT ) );
        }

        /**
         * BLE服务断开连接
         * @param name 名称
         */
        @Override
        public void onServiceDisconnected ( ComponentName name )
        {
            mBleService = null;
        }
    };

    /**
     * 连接设备
     * @param mac  设备地址
     */
    public static void connectDevice(String mac)
    {
        mBleService.connect( mac, false );
    }

    /**
     * 断开设备连接
     * @param mac   设备地址
     */
    public static void disconnectDevice(String mac)
    {
        mBleService.disconnect( mac );
    }

    /**
     * 读取自定义广播数据
     * @param mac   设备地址
     */
    public static void getMfrData(String mac)
    {
        mBleService.readReg( mac, BleRegConstants.REG_ADV_MFR_SPC );
    }

    public static void setMfrData( String mac, byte[] dat )
    {
        mBleService.setAdvMfr( mac, DataUtil.byteArrayToHex( dat ) );
    }

    public static void renameDevice( String mac, String name )
    {
        mBleService.setSlaverName( mac, name );
    }

    public static boolean checkConnect(String mac)
    {
        for ( int i = 0; i < mBleService.getConnectedDevices().size(); i++ )
        {
            BluetoothDevice dev = (BluetoothDevice) mBleService.getConnectedDevices().get( i );
            if ( dev.getAddress().equals( mac ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 打开BLE
     * @return  true成功 false失败
     */
    public static boolean checkBleSupported(Context context)
    {
        if ( context.getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH_LE ) )
        {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if ( mAdapter != null )
            {
                return true;
            }
        }
        Toast.makeText( context, R.string.ble_no_support, Toast.LENGTH_SHORT ).show();
        if ( context instanceof Activity )
        {
            ( (Activity) context ).finish();
        }
        return false;
    }

    /**
     * 绑定ble服务
     * @param context
     */
    public static void bindBleService(Context context)
    {
        context.bindService( new Intent( context, BleService.class ),
                             mServiceConnection, Context.BIND_AUTO_CREATE );
    }

    /**
     * 解除ble服务绑定
     * @param context
     */
    public static void unbindBleService(Context context)
    {
        context.unbindService( mServiceConnection );
    }

    /**
     * 断开所有连接
     */
    public static void disconnectAll()
    {
        ArrayList<BluetoothDevice> devices = (ArrayList<BluetoothDevice>) mBleService.getConnectedDevices();
        for ( int i = 0; i < devices.size(); i++ )
        {
            mBleService.disconnect( devices.get( i ).getAddress() );
        }
    }

    /**
     * 打开ble
     * @return
     */
    public static boolean openBle()
    {
        //如果BLE已经打开 发送消息 启动扫描
        if ( mAdapter.isEnabled() )
        {
            return true;
        }
        else if ( Setting.mBleEnabled && mAdapter.enable() )
        {
            //如果蓝牙未打开
            //如果用户已授权app打开蓝牙,且打开BLE成功
            return true;
        }
        return false;
    }

    /**
     * 关闭ble
     */
    public static void closeBle()
    {
        if ( mAdapter != null )
        {
            mAdapter.disable();
        }
    }
}
