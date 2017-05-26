package com.inledco.fluvalsmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.BaseDevice;
import com.inledco.fluvalsmart.impl.OnRecyclerItemClickListener;
import com.inledco.fluvalsmart.util.DeviceUtil;

import java.util.ArrayList;

/**
 * Created by liruya on 2016/10/26.
 */

public class DeviceAdapter extends RecyclerView.Adapter< DeviceAdapter.DeviceViewHolder >
{
    private Context mContext;
    private ArrayList< BaseDevice > mDevices;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public DeviceAdapter ( Context context, ArrayList< BaseDevice > devices )
    {
        mContext = context;
        mDevices = devices;
    }

    public void setOnRecyclerItemClickListener ( OnRecyclerItemClickListener listener )
    {
        mOnRecyclerItemClickListener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
    {
        DeviceViewHolder holder = new DeviceViewHolder( LayoutInflater.from( mContext )
                                                                      .inflate( R.layout.item_device, parent, false ) );
        return holder;
    }

    @Override
    public void onBindViewHolder ( DeviceViewHolder holder, final int position )
    {
        BaseDevice device = mDevices.get( position );
        holder.iv_icon.setImageResource( DeviceUtil.getDeviceIconOn( device.getDevicePrefer()
                                                                           .getDevID() ) );
        holder.tv_name.setText( device.getDevicePrefer()
                                      .getDeviceName() );
        holder.itemView.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                mOnRecyclerItemClickListener.onRecyclerItemClickListener( position );
            }
        } );
    }

    @Override
    public int getItemCount ()
    {
        return mDevices == null ? 0 : mDevices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv_icon;
        private TextView tv_name;

        public DeviceViewHolder ( View itemView )
        {
            super( itemView );
            iv_icon = (ImageView) itemView.findViewById( R.id.item_devicon );
            tv_name = (TextView) itemView.findViewById( R.id.item_devname );
        }
    }
}
