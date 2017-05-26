package com.inledco.fluvalsmart.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.LeScanRecord;
import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.adapter.ScanAdapter;
import com.inledco.fluvalsmart.bean.DevicePrefer;
import com.inledco.fluvalsmart.bean.SelectDevice;
import com.inledco.fluvalsmart.constant.ConstVal;
import com.inledco.fluvalsmart.util.BleUtil;
import com.inledco.fluvalsmart.util.DeviceUtil;
import com.inledco.fluvalsmart.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ScanActivity extends BaseActivity
{
    private static final String TAG = "ScanActivity";
    private static final int BLE_SCAN_PERIOD = 20000;

    private AppCompatImageButton scan_ib_back;
    private ToggleButton scan_tb_scan;
    private ProgressBar scanpbscanning;
    private RecyclerView scanrvshow;
    private AppCompatImageButton scanbtncfm;

    private Set<String> macSet;
    private ArrayList<String> mScanMacs;
    private Map<String, String > map;

    private ArrayList<SelectDevice> mDevices;
    private ScanAdapter mScanAdapter;
    private Handler mHandler;


    /**
     * 蓝牙扫描回调方法
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan ( final BluetoothDevice device, final int rssi, final byte[] scanRecord )
        {
            String mac = device.getAddress();
            map.put( mac, device.getName() );
            //如果设备列表已存在该地址或已经连接过该地址 则不再继续连接
            if ( !macSet.contains( mac ) && !mScanMacs.contains( mac ))
            {
                BleUtil.connectDevice( mac );
                LeScanRecord record = LeScanRecord.parseFromBytes( scanRecord );
                Log.e( TAG, "onLeScan: " + DataUtil.byteArrayToHex( scanRecord ) );
            }
        }
    };


    private BleCallBack mBleCallBack = new BleCallBack()
    {
        /**
         * 连接成功回调方法
         * @param s 连接的设备的mac地址
         */
        @Override
        public void onConnected ( final String s )
        {
            Log.e( TAG, "onConnected: " + s );
            if ( !mScanMacs.contains( s ) )
            {
                mScanMacs.add( s );
                mDevices.add( new SelectDevice( false,
                                                false,
                                                new DevicePrefer( (byte) 0x00, (byte) 0x00, s, map.get( s )) ) );
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        mScanAdapter.notifyItemInserted( mDevices.size()-1 );
                    }
                } );
            }
        }

        /**
         * 连接超时回调方法
         * @param s 连接的设备的mac地址
         */
        @Override
        public void onConnectTimeout ( String s )
        {
            Log.e( TAG, "onConnectTimeout: " + s );
        }

        /**
         * 连接错误回调方法
         * @param s   连接设备的mac地址
         * @param i   当前状态
         * @param i1  新状态
         */
        @Override
        public void onConnectionError ( String s, int i, int i1 )
        {
            Log.e( TAG, "onConnectionError: " + s + "  status = " + i + "  newStatus = " + i1 );
        }

        /**
         * 断开连接回调方法
         * @param s   断开连接的设备的mac地址
         */
        @Override
        public void onDisconnected ( final String s )
        {
            Log.e( TAG, "onDisconnected: " + s );
        }

        /**
         * 发现设备的回调方法 此方法调用后才可进行数据交互
         * @param s  发现设备的mac地址
         */
        @Override
        public void onServicesDiscovered ( final String s )
        {
            Log.e( TAG, "onServicesDiscovered: " + s );
            mHandler.postDelayed( new Runnable() {
                @Override
                public void run ()
                {
                    BleUtil.getMfrData( s );
                }
            }, 500);
        }

        /**
         * 设备丢失的回调方法
         * @param s   丢失设备的mac地址
         * @param i   状态
         */
        @Override
        public void onServicesUndiscovered ( String s, int i )
        {
            Log.e( TAG, "onServicesUndiscovered: " + s + "  status = " + i );
        }

        @Override
        public void onRegRead ( final String s, String s1, int i, int i1 )
        {
            Log.e( TAG, "onRegRead: " + s + '\t' + s1.length() + '\t' + i + '\t' + i1 );
            super.onRegRead( s, s1, i, i1 );
            byte[] mfr = DataUtil.hexToByteArray( s1.replace( " ", "" ) );
            byte mainType;
            byte subType;
            if ( mfr == null || mfr.length < 2)
            {
                mainType = 0x00;
                subType = 0x00;
            }
            else
            {
                mainType = mfr[0];
                subType = mfr[1];
            }
            Log.e( TAG, "onRegRead: " + DeviceUtil.getDeviceType( mainType, subType ) );
            for ( int k = 0; k < mDevices.size(); k++ )
            {
                if ( mDevices.get( k ).getPrefer().getDeviceMac().equals( s ) )
                {
                    mDevices.get( k ).getPrefer().setMainType( mainType );
                    mDevices.get( k ).getPrefer().setSubType( subType );
                    mDevices.get( k ).setSelectable( true );
                    BleUtil.disconnectDevice( s );
                    final int idx = k;
                    runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            mScanAdapter.notifyItemChanged( idx );
                        }
                    } );
                    break;
                }
            }
        }

        @Override
        public void onCharacteristicChanged ( String s, byte[] bytes )
        {
            super.onCharacteristicChanged( s, bytes );
            Log.e( TAG, "onCharacteristicChanged: " );
        }

        @Override
        public void onCharacteristicChanged ( String s,
                                              BluetoothGattCharacteristic bluetoothGattCharacteristic )
        {
            super.onCharacteristicChanged( s, bluetoothGattCharacteristic );
            Log.e( TAG, "onCharacteristicChanged: "  );
        }

        @Override
        public void onCharacteristicRead ( String s, byte[] bytes, int i )
        {
            super.onCharacteristicRead( s, bytes, i );
            Log.e( TAG, "onCharacteristicRead: 1" );
        }

        @Override
        public void onCharacteristicRead ( String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i )
        {
            super.onCharacteristicRead( s, bluetoothGattCharacteristic, i );
            Log.e( TAG, "onCharacteristicRead: 2" );
        }

        @Override
        public void onCharacteristicWrite ( String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i )
        {
            super.onCharacteristicWrite( s, bluetoothGattCharacteristic, i );
            Log.e( TAG, "onCharacteristicWrite: " );
        }

        @Override
        public void onDescriptorRead ( String s, BluetoothGattDescriptor bluetoothGattDescriptor, int i )
        {
            super.onDescriptorRead( s, bluetoothGattDescriptor, i );
            Log.e( TAG, "onDescriptorRead: " );
        }

        @Override
        public void onNotifyStateRead ( UUID uuid, UUID uuid1, boolean b )
        {
            super.onNotifyStateRead( uuid, uuid1, b );
            Log.e( TAG, "onNotifyStateRead: " );
        }
    };

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_scan );

        initView();
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        initData();
        initEvent();
        startScan();
    }

    @Override
    protected void onPause ()
    {
        super.onPause();
        stopScan();
        BleUtil.disconnectAll();
        BleUtil.mBleService.removeBleCallBack( mBleCallBack );
    }

    private Runnable mScanRunnable = new Runnable() {
        @Override
        public void run ()
        {
            scan_tb_scan.setChecked( false );
        }
    };

    /**
     * 开始扫描ble设备
     */
    private void startScan ()
    {
        map.clear();
        mScanMacs.clear();
        mDevices.clear();
        BleUtil.disconnectAll();
        mHandler.postDelayed( mScanRunnable, BLE_SCAN_PERIOD );
        BleUtil.mAdapter.startLeScan( mLeScanCallback );
    }

    /**
     * 停止扫描ble设备
     */
    private void stopScan ()
    {
        BleUtil.mAdapter.stopLeScan( mLeScanCallback );
        mHandler.removeCallbacks( mScanRunnable );
        BleUtil.disconnectAll();
    }


    private void initView ()
    {
        scanrvshow = (RecyclerView) findViewById( R.id.scan_rv_show );
        scanpbscanning = (ProgressBar) findViewById( R.id.scan_pb_scanning );
        scan_tb_scan = (ToggleButton) findViewById( R.id.scan_tb_scan );
        scan_ib_back = (AppCompatImageButton) findViewById( R.id.scan_ib_back );
        scanbtncfm  = (AppCompatImageButton) findViewById( R.id.scan_btn_cfm );

        scanrvshow.setLayoutManager( new LinearLayoutManager( ScanActivity.this,
                                                              LinearLayoutManager.VERTICAL,
                                                              false) );
    }

    private void initData()
    {
        BleUtil.mBleService.addBleCallBack( mBleCallBack );
        macSet = getSharedPreferences( ConstVal.DEV_PREFER_FILENAME, Context.MODE_PRIVATE ).getAll().keySet();
        Log.e( TAG, "initData: macSet" + macSet );
        mScanMacs = new ArrayList<>();
        map = new HashMap<>();
        mDevices = new ArrayList<>();
        mHandler = new Handler()
        {
            @Override
            public void handleMessage ( Message msg )
            {
                super.handleMessage( msg );
                switch ( msg.what )
                {
                    case 0:
                        scanbtncfm.setVisibility( View.GONE );
                        for ( SelectDevice dev : mDevices )
                        {
                            if ( dev.isSelected() )
                            {
                                scanbtncfm.setVisibility( View.VISIBLE );
                                break;
                            }
                        }
                        break;
                }
            }
        };
        mScanAdapter = new ScanAdapter( ScanActivity.this, mHandler, mDevices );
        scanrvshow.setAdapter( mScanAdapter );
    }

    private void initEvent()
    {
        //返回键点击事件
        scan_ib_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                finish();
            }
        } );

        //确认按键点击事件
        scanbtncfm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v )
            {
                for ( SelectDevice dev : mDevices )
                {
                    if ( dev.isSelected() )
                    {
                        PreferenceUtil.setObjectToPrefer( ScanActivity.this,
                                                          ConstVal.DEV_PREFER_FILENAME,
                                                          dev.getPrefer(),
                                                          dev.getPrefer().getDeviceMac());
                    }
                    finish();
                }
            }
        } );

        scan_tb_scan.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged ( CompoundButton buttonView, boolean isChecked )
            {
                if ( isChecked )
                {
                    startScan();
                    scanpbscanning.setVisibility( View.VISIBLE );
                }
                else
                {
                    stopScan();
                    scanpbscanning.setVisibility( View.GONE );
                }
            }
        } );
        scan_tb_scan.setChecked( true );
    }
}
