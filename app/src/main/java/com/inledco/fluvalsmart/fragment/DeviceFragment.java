package com.inledco.fluvalsmart.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.activity.LightActivity;
import com.inledco.fluvalsmart.adapter.DeviceAdapter;
import com.inledco.fluvalsmart.bean.BaseDevice;
import com.inledco.fluvalsmart.bean.DevicePrefer;
import com.inledco.fluvalsmart.bean.MessageEvent;
import com.inledco.fluvalsmart.constant.ConstVal;
import com.inledco.fluvalsmart.impl.OnRecyclerItemClickListener;
import com.inledco.fluvalsmart.util.BleUtil;
import com.inledco.fluvalsmart.util.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static com.inledco.fluvalsmart.util.PreferenceUtil.getObjectFromPrefer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends BaseFragment
{
    private static final String TAG = "DeviceFragment";

    private RecyclerView devicervshow;

    private ArrayList<BaseDevice> mDevices;
    private DeviceAdapter mDeviceAdapter;

    private PopupWindow mPopupWindow;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_device, null );
        initView( view );
        initEvent();
        return view;
    }

    @Override
    public void onResume ()
    {
        super.onResume();
        EventBus.getDefault().register( this );
        initData();
    }

    @Override
    public void onPause ()
    {
        super.onPause();
        EventBus.getDefault().unregister( this );
        Log.e( TAG, "onPause: " );
    }

    @Override
    public void onStop ()
    {
        super.onStop();
        Log.e( TAG, "onStop: " );
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent ( final MessageEvent msgEvt )
    {
        switch ( msgEvt.getMsg() )
        {
            case MessageEvent.MSG_EVT_BLE_CONNECTED:
                break;

            case MessageEvent.MSG_EVT_BLE_CONNECT_TIMEOUT:
                if ( mPopupWindow != null && mPopupWindow.isShowing() )
                {
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            mPopupWindow.dismiss();
                            Toast.makeText( getContext(), R.string.connect_failed, Toast.LENGTH_SHORT )
                                 .show();
                        }
                    } );
                }
                break;

            case MessageEvent.MSG_EVT_BLE_SERVICES_DISCOVERED:
                if ( mPopupWindow != null && mPopupWindow.isShowing() )
                {
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            mPopupWindow.dismiss();
                            Toast.makeText( getContext(), R.string.connect_success, Toast.LENGTH_SHORT )
                                 .show();
                            DevicePrefer prefer = (DevicePrefer) PreferenceUtil.getObjectFromPrefer( getContext(),
                                                                                                     ConstVal.DEV_PREFER_FILENAME,
                                                                                                     msgEvt.getMac() );
                            if ( prefer != null )
                            {
                                Intent intent = new Intent( getContext(), LightActivity.class );
                                intent.putExtra( "DevicePrefer", prefer );
                                startActivity( intent );
                            }
                        }
                    } );
                }
                break;
        }
    }

    private void initView ( View view )
    {
        devicervshow = (RecyclerView) view.findViewById( R.id.device_rv_show );
        devicervshow.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
    }

    private void initData()
    {
        mDevices = new ArrayList<>();
        SharedPreferences sp = getContext().getSharedPreferences( ConstVal.DEV_PREFER_FILENAME,
                                                                  Context.MODE_PRIVATE );
        for ( String key : sp.getAll()
                             .keySet() )
        {
            DevicePrefer prefer = (DevicePrefer) getObjectFromPrefer( getContext(), ConstVal.DEV_PREFER_FILENAME, key );
            mDevices.add( new BaseDevice( prefer, false) );
        }
        mDeviceAdapter = new DeviceAdapter( getContext(), mDevices );
        devicervshow.setAdapter( mDeviceAdapter );
        mDeviceAdapter.setOnRecyclerItemClickListener( new OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClickListener ( int position )
            {
                BaseDevice device = mDevices.get( position );
                BleUtil.connectDevice( device.getDevicePrefer().getDeviceMac() );
                if ( mPopupWindow == null )
                {
                    View popView = LayoutInflater.from( getContext() ).inflate( R.layout.popup_connecting, null );
                    popView.setOnKeyListener( new View.OnKeyListener() {
                        @Override
                        public boolean onKey ( View view, int i, KeyEvent keyEvent )
                        {
                            return false;
                        }
                    } );
                    mPopupWindow = new PopupWindow( popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true );
                    mPopupWindow.setTouchable( false );
                    mPopupWindow.setOutsideTouchable( false );
                    mPopupWindow.setBackgroundDrawable( getResources().getDrawable( R.drawable.shape_popup) );
                }
                mPopupWindow.showAtLocation( getActivity().findViewById( R.id.activity_main ), Gravity.CENTER, 0, 0 );
            }
        } );
    }

    private void initEvent()
    {
    }

}
