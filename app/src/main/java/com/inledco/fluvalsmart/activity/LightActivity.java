package com.inledco.fluvalsmart.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.DevicePrefer;
import com.inledco.fluvalsmart.bean.LightManual;
import com.inledco.fluvalsmart.bean.MessageEvent;
import com.inledco.fluvalsmart.dialog.RenameDialog;
import com.inledco.fluvalsmart.util.BleUtil;
import com.inledco.fluvalsmart.util.CommUtil;
import com.inledco.fluvalsmart.view.ColorPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LightActivity extends BaseActivity
{
    private static final String TAG = "LightActivity";

    private AppCompatImageButton lightibback;
    private AppCompatTextView lighttvname;
    private AppCompatImageButton lightibedit;
    private AppCompatImageButton lightibfind;
    private CheckableImageButton light_cib_rgb;
    private CheckableImageButton light_cib_ct;
    private CheckableImageButton light_cib_power;
    private View disconView;
    private ViewStub light_vs_discon;
    private AppCompatTextView discon_info;
    private ProgressBar pb_recon;
    private ViewStub light_vs_rgb;
    private ViewStub light_vs_ct;
    private View view_rgb;
    private View view_ct;
    private ColorPicker rgb_cp;
    private AppCompatSeekBar rgb_sb_brt;
    private AppCompatSeekBar ct_sb_ct;
    private AppCompatSeekBar ct_sb_brt;

    private DevicePrefer mPrefer;
    private LightManual mLight;
    private String mac;
    private Handler mHandler;

    private long msc;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_light );
        Intent intent = getIntent();
        mPrefer = (DevicePrefer) intent.getSerializableExtra( "DevicePrefer" );
        Log.e( TAG, "onCreate: " );

        EventBus.getDefault()
                .register( this );
        initView();
        initEvent();
        msc = System.currentTimeMillis();
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        //        EventBus.getDefault().register( this );
        initData();
        Log.e( TAG, "onResume: " );
    }

    @Override
    protected void onPause ()
    {
        super.onPause();
        Log.e( TAG, "onPause: " );
    }

    @Override
    protected void onStop ()
    {
        super.onStop();
        Log.e( TAG, "onStop: " );
    }

    @Override
    protected void onDestroy ()
    {
        BleUtil.disconnectAll();
        EventBus.getDefault()
                .unregister( this );
        super.onDestroy();
    }

    @Subscribe
    public void onEvent ( MessageEvent msgEvt )
    {
        switch ( msgEvt.getMsg() )
        {
            case MessageEvent.MSG_EVT_BLE_DISCONNECTED:
            case MessageEvent.MSG_EVT_BLE_CONNECT_TIMEOUT:
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        light_vs_discon.setVisibility( View.VISIBLE );
                        discon_info.setVisibility( View.VISIBLE );
                        pb_recon.setVisibility( View.GONE );
                    }
                } );
                break;
            case MessageEvent.MSG_EVT_BLE_SERVICES_DISCOVERED:
                mHandler.postDelayed( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        CommUtil.syncDeviceTime( mPrefer.getDeviceMac() );
                    }
                }, 500 );
                break;
            case MessageEvent.MSG_EVT_BLE_RECEIVE:
                Object object = CommUtil.decodeLight( CommUtil.mRcvBytes, mPrefer.getDevID() );
                if ( object != null && object instanceof LightManual )
                {
                    mLight = (LightManual) object;
                    runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            if ( mLight.isOnRGB() )
                            {
                                light_cib_rgb.setChecked( true );
                                light_cib_ct.setChecked( false );
                                view_rgb.setVisibility( View.VISIBLE );
                                view_ct.setVisibility( View.GONE );
                            }
                            else
                            {
                                if ( mLight.isOnCT() )
                                {
                                    light_cib_rgb.setChecked( false );
                                    light_cib_ct.setChecked( true );
                                    view_ct.setVisibility( View.VISIBLE );
                                    view_rgb.setVisibility( View.GONE );
                                }
                            }
                            rgb_cp.setSelectedColor( Color.argb( 0xFF,
                                                                 mLight.getRed() * 255 / 32,
                                                                 mLight.getGreen() * 255 / 32,
                                                                 mLight.getBlue() * 255 / 32 ) );
                            rgb_sb_brt.setProgress( mLight.getRGB_Brt() );
                            ct_sb_ct.setProgress( mLight.getCT() );
                            ct_sb_brt.setProgress( mLight.getCT_Brt() );
                            light_cib_power.setChecked( mLight.isOnRGB() || mLight.isOnCT() );
                        }
                    } );
                }
                break;
        }
    }

    private void ctrlRGB ()
    {
        byte[] bytes = new byte[]{ CommUtil.FRM_HDR,
                                   CommUtil.CMD_CTRL,
                                   CommUtil.LED_RGB,
                                   (byte) mLight.getRed(),
                                   (byte) mLight.getGreen(),
                                   (byte) mLight.getBlue(),
                                   (byte) mLight.getRGB_Brt(),
                                   0x00 };
        bytes[7] = CommUtil.getCRC( bytes, 7 );
        CommUtil.setLed( mac, bytes );
    }

    private void ctrlCT ()
    {
        byte[] bytes = new byte[]{ CommUtil.FRM_HDR, CommUtil.CMD_CTRL, CommUtil.LED_CT, (byte) mLight.getCT(), (byte) mLight.getCT_Brt(), 0x00 };
        bytes[5] = CommUtil.getCRC( bytes, 5 );
        CommUtil.setLed( mac, bytes );
    }

    private void initView ()
    {
        lightibfind = (AppCompatImageButton) findViewById( R.id.light_ib_find );
        lightibedit = (AppCompatImageButton) findViewById( R.id.light_ib_edit );
        lighttvname = (AppCompatTextView) findViewById( R.id.light_tv_name );
        lightibback = (AppCompatImageButton) findViewById( R.id.light_ib_back );
        light_cib_rgb = (CheckableImageButton) findViewById( R.id.light_cib_rgb );
        light_cib_ct = (CheckableImageButton) findViewById( R.id.light_cib_ct );
        light_cib_power = (CheckableImageButton) findViewById( R.id.light_cib_power );
        light_vs_discon = (ViewStub) findViewById( R.id.light_vs_dicon );
        disconView = light_vs_discon.inflate();
        disconView.setVisibility( View.GONE );
        discon_info = (AppCompatTextView) disconView.findViewById( R.id.discon_tv_info );
        pb_recon = (ProgressBar) disconView.findViewById( R.id.discon_pb_connecting );
        light_vs_rgb = (ViewStub) findViewById( R.id.light_vs_rgb );
        light_vs_ct = (ViewStub) findViewById( R.id.light_vs_ct );

        light_cib_rgb.setChecked( true );

        view_rgb = light_vs_rgb.inflate();
        rgb_cp = (ColorPicker) view_rgb.findViewById( R.id.rgb_cp );
        rgb_sb_brt = (AppCompatSeekBar) view_rgb.findViewById( R.id.rgb_sb_brt );

        view_ct = light_vs_ct.inflate();
        ct_sb_ct = (AppCompatSeekBar) view_ct.findViewById( R.id.ct_sb_ct );
        ct_sb_brt = (AppCompatSeekBar) view_ct.findViewById( R.id.ct_sb_brt );
        view_ct.setVisibility( View.GONE );
    }

    private void initData ()
    {
        mac = mPrefer.getDeviceMac();
        lighttvname.setText( mPrefer.getDeviceName() );
        mHandler = new Handler();
        mHandler.postDelayed( new Runnable()
        {
            @Override
            public void run ()
            {
                CommUtil.syncDeviceTime( mPrefer.getDeviceMac() );
            }
        }, 500 );
    }

    private void initEvent ()
    {
        lightibback.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                finish();
            }
        } );

        light_cib_rgb.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                if ( mLight != null && !light_cib_rgb.isChecked() )
                {
                    CommUtil.turnOnRGB( mac );
                }
            }
        } );

        light_cib_ct.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                if ( mLight != null && !light_cib_ct.isChecked() )
                {
                    CommUtil.turnOnCT( mac );
                }
            }
        } );

        light_cib_power.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                if ( mLight != null )
                {
                    if ( mLight.isOnRGB() || mLight.isOnCT() )
                    {
                        CommUtil.turnOffLed( mac );
                    }
                    else
                    {
                        if ( light_cib_rgb.isChecked() )
                        {
                            CommUtil.turnOnRGB( mac );
                        }
                        else
                        {
                            if ( light_cib_ct.isChecked() )
                            {
                                CommUtil.turnOnCT( mac );
                            }
                        }
                    }
                }
            }
        } );

        rgb_cp.setOnColorChangeListener( new ColorPicker.OnColorChangeListener()
        {
            @Override
            public void onColorChanged ( int r, int g, int b )
            {
                if ( mLight.isOnRGB() )
                {
                    long t = System.currentTimeMillis();
                    if ( t - msc > 32 )
                    {
                        mLight.setRed( ( r + 4 ) / 8 );
                        mLight.setGreen( ( g + 4 ) / 8 );
                        mLight.setBlue( ( b + 4 ) / 8 );
                        ctrlRGB();
                        msc = t;
                    }
                }
            }
        } );

        rgb_sb_brt.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged ( SeekBar seekBar, int i, boolean b )
            {
                if ( b && mLight.isOnRGB() )
                {
                    long t = System.currentTimeMillis();
                    if ( t - msc > 32 )
                    {
                        mLight.setRGB_Brt( seekBar.getProgress() );
                        ctrlRGB();
                        msc = t;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch ( SeekBar seekBar )
            {

            }

            @Override
            public void onStopTrackingTouch ( SeekBar seekBar )
            {
                if ( mLight.isOnRGB() )
                {
                    mLight.setRGB_Brt( seekBar.getProgress() );
                    mHandler.postDelayed( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            ctrlRGB();
                        }
                    }, 32 );
                }
            }
        } );

        ct_sb_ct.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged ( SeekBar seekBar, int i, boolean b )
            {
                if ( b && mLight.isOnCT() )
                {
                    long t = System.currentTimeMillis();
                    if ( t - msc > 32 )
                    {
                        mLight.setCT( seekBar.getProgress() );
                        ctrlCT();
                        msc = t;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch ( SeekBar seekBar )
            {

            }

            @Override
            public void onStopTrackingTouch ( SeekBar seekBar )
            {
                if ( mLight.isOnCT() )
                {
                    mLight.setCT( seekBar.getProgress() );
                    mHandler.postDelayed( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            ctrlCT();
                        }
                    }, 32 );
                }
            }
        } );

        ct_sb_brt.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged ( SeekBar seekBar, int i, boolean b )
            {
                if ( b && mLight.isOnCT() )
                {
                    long t = System.currentTimeMillis();
                    if ( t - msc > 32 )
                    {
                        mLight.setCT_Brt( seekBar.getProgress() );
                        ctrlCT();
                        msc = t;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch ( SeekBar seekBar )
            {

            }

            @Override
            public void onStopTrackingTouch ( SeekBar seekBar )
            {
                if ( mLight.isOnCT() )
                {
                    mLight.setCT_Brt( seekBar.getProgress() );
                    mHandler.postDelayed( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            ctrlCT();
                        }
                    }, 32 );
                }
            }
        } );

        lightibfind.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                CommUtil.findDevice( mac );
            }
        } );

        lightibedit.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                RenameDialog.showDialog( LightActivity.this, mPrefer );
            }
        } );

        discon_info.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                BleUtil.connectDevice( mac );
                discon_info.setVisibility( View.GONE );
                pb_recon.setVisibility( View.VISIBLE );
            }
        } );
    }
}
