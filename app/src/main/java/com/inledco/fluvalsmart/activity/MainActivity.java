package com.inledco.fluvalsmart.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.adapter.MainPageAdapter;
import com.inledco.fluvalsmart.bean.MessageEvent;
import com.inledco.fluvalsmart.fragment.DeviceFragment;
import com.inledco.fluvalsmart.fragment.NewsFragment;
import com.inledco.fluvalsmart.fragment.UserFragment;
import com.inledco.fluvalsmart.prefer.Setting;
import com.inledco.fluvalsmart.util.BleUtil;
import com.inledco.fluvalsmart.util.DeviceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
{
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private RadioGroup mainrgshow;
    private ViewPager main_vp_show;
    private MenuItem menuItemBleSearch;

    //双击back退出标志位
    private boolean mExiting;
    private ArrayList< Fragment > frags;
    private MainPageAdapter mMainPageAdapter;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        if ( BleUtil.checkBleSupported( MainActivity.this ) )
        {
            Setting.initSetting( MainActivity.this );
            EventBus.getDefault().register( MainActivity.this );
            BleUtil.bindBleService( MainActivity.this );
            initView();
            DeviceUtil.initDeviceMap();
            //            initData();
            //            initEvent();
        }
    }

    @Override
    protected void onResume ()
    {
        if ( !BleUtil.openBle() )
        {
            //如果未授权app打开BLE或打开BLE失败 则申请权限打开app
            Intent intent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( intent, BleUtil.REQUEST_ENABLE_BLE );
        }
        super.onResume();
    }

    @Override
    protected void onPause ()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy ()
    {
        BleUtil.unbindBleService( MainActivity.this );
        EventBus.getDefault().unregister( MainActivity.this );
        BleUtil.disconnectAll();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data )
    {
        if ( requestCode == BleUtil.REQUEST_ENABLE_BLE && resultCode == Activity.RESULT_CANCELED )
        {
            finish();
            return;
        }
//        Setting.mBleEnabled = true;
//        SharedPreferences defaultSet = PreferenceManager.getDefaultSharedPreferences(
//        MainActivity.this );
//        SharedPreferences.Editor editor = defaultSet.edit();
//        editor.putBoolean( SET_BLE_ENABLED, Setting.mBleEnabled );
//        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
        super.onActivityResult( requestCode, resultCode, data );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        menuItemBleSearch = menu.findItem( R.id.menu_search_ble );
        menuItemBleSearch.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick ( MenuItem item )
            {
                Intent intent = new Intent( MainActivity.this, ScanActivity.class );
                startActivity( intent );
                return false;
            }
        } );
        return true;
    }

    @Override
    public void onConfigurationChanged ( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        Log.e( TAG, "onConfigurationChanged: " );
    }

    @Subscribe
    public void onEvent( MessageEvent msgEvt)
    {
        switch ( msgEvt.getMsg() )
        {
            case MessageEvent.MSG_EVT_BLE_INIT:
                initData();
                initEvent();
                break;
        }
    }

    private void initView ()
    {
        mainrgshow = (RadioGroup) findViewById( R.id.main_rg_show );
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "" );
        setSupportActionBar( toolbar );

        main_vp_show = (ViewPager) findViewById( R.id.main_vp_show );
    }

    private void initData()
    {
        frags = new ArrayList<>();
        frags.add( new DeviceFragment() );
        frags.add( new NewsFragment() );
        frags.add( new UserFragment() );
        mMainPageAdapter = new MainPageAdapter( getSupportFragmentManager(), frags );
        main_vp_show.setAdapter( mMainPageAdapter );
    }

    private void initEvent()
    {
        main_vp_show.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled ( int position, float positionOffset, int positionOffsetPixels )
            {

            }

            @Override
            public void onPageSelected ( int position )
            {
                switch ( position )
                {
                    case 0:
                        mainrgshow.check( R.id.main_rb_device );
                        menuItemBleSearch.setVisible( true );
                        break;

                    case 1:
                        mainrgshow.check( R.id.main_rb_news );
                        menuItemBleSearch.setVisible( false );
                        break;
                    case 2:
                        mainrgshow.check( R.id.main_rb_setting);
                        menuItemBleSearch.setVisible( false );
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged ( int state )
            {

            }
        } );

        mainrgshow.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged ( RadioGroup group, int checkedId )
            {
                switch ( checkedId )
                {
                    case R.id.main_rb_device:
                        main_vp_show.setCurrentItem( 0 );
                        break;

                    case R.id.main_rb_news:
                        main_vp_show.setCurrentItem( 1 );
                        break;

                    case R.id.main_rb_setting:
                        main_vp_show.setCurrentItem( 2 );
                        break;
                }
            }
        } );
        mainrgshow.check( R.id.main_rb_device );
    }

    /**
     * back按键关闭app时 弹出确认关闭蓝牙dialog
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event )
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK )
        {
            if ( !mExiting )
            {
                mExiting = true;
                new Handler().postDelayed( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        mExiting = false;
                    }
                }, 1500 );
                Toast.makeText( MainActivity.this, "再按一下退出应用", Toast.LENGTH_SHORT ).show();
            }
            else
            {
                //退出时是否提示
                if ( Setting.mExitTip )
                {
                    Setting.showDialog( MainActivity.this );
                }
                else
                {
                    //如果退出时不提示 且设置为退出关闭BLE
                    if ( Setting.mExitTurnOffBle )
                    {
                        BleUtil.closeBle();
                    }
                    finish();
                }
            }
        }
        return true;
    }
}
