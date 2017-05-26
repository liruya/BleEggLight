package com.inledco.fluvalsmart.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.SelectDevice;
import com.inledco.fluvalsmart.util.DeviceUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ScanAdapter extends RecyclerView.Adapter< ScanAdapter.ScanViewHolder >
{
    private Context mContext;
    private Handler mHandler;
    private ArrayList< SelectDevice > mSelectDevices;

    public ScanAdapter ( Context context, Handler handler, ArrayList< SelectDevice > selectDevices )
    {
        mContext = context;
        mHandler = handler;
        mSelectDevices = selectDevices;
    }

    @Override
    public ScanViewHolder onCreateViewHolder ( ViewGroup parent, int viewType )
    {
        ScanViewHolder holder = new ScanViewHolder( LayoutInflater.from( mContext )
                                                                  .inflate( R.layout.item_scan, null ) );
        return holder;
    }

    @Override
    public void onBindViewHolder ( ScanViewHolder holder, final int position )
    {
        SelectDevice device = mSelectDevices.get( position );
        holder.iv_icon.setImageResource( DeviceUtil.getDeviceIconOn( device.getPrefer().getMainType(), device.getPrefer().getSubType() ) );
        holder.tv_name.setText( device.getPrefer().getDeviceName() );
        holder.tv_type.setText( DeviceUtil.getDeviceType( device.getPrefer().getMainType(), device.getPrefer().getSubType() ) );
        if ( device.isSelectable() )
        {
            holder.cb_sel.setVisibility( View.VISIBLE );
            holder.cb_sel.setChecked( device.isSelected() );
            holder.cb_sel.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged ( CompoundButton buttonView, boolean isChecked )
                {
                    mSelectDevices.get( position ).setSelected( isChecked );
                    mHandler.sendEmptyMessage( 0 );
                }
            } );
        }
        else
        {
            holder.cb_sel.setChecked( false );
            holder.cb_sel.setVisibility( View.GONE );
        }
    }

    @Override
    public int getItemCount ()
    {
        return mSelectDevices == null ? 0 : mSelectDevices.size();
    }

    /**
     * ViewHolder类
     */
    class ScanViewHolder extends RecyclerView.ViewHolder
    {
        private AppCompatImageView iv_icon;
        private AppCompatTextView tv_name;
        private AppCompatTextView tv_type;
        private AppCompatCheckBox cb_sel;

        public ScanViewHolder ( View itemView )
        {
            super( itemView );
            iv_icon = (AppCompatImageView) itemView.findViewById( R.id.item_scan_iv_icon );
            tv_name = (AppCompatTextView) itemView.findViewById( R.id.item_scan_tv_name );
            tv_type = (AppCompatTextView) itemView.findViewById( R.id.item_scan_tv_type );
            cb_sel = (AppCompatCheckBox) itemView.findViewById( R.id.item_scan_cb_sel );
        }
    }
}
