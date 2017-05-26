package com.inledco.fluvalsmart.bean;

/**
 * 扫描蓝牙设备界面 可选择的设备
 * Created by liruya on 2016/10/25.
 */

public class SelectDevice
{
    private boolean mSelected;
    private boolean mSelectable;
    private DevicePrefer mPrefer;

    public SelectDevice ( boolean selected, boolean selectable, DevicePrefer prefer )
    {
        mSelected = selected;
        mSelectable = selectable;
        mPrefer = prefer;
    }

    public boolean isSelected ()
    {
        return mSelected;
    }

    public void setSelected ( boolean selected )
    {
        mSelected = selected;
    }

    public boolean isSelectable ()
    {
        return mSelectable;
    }

    public void setSelectable ( boolean selectable )
    {
        mSelectable = selectable;
    }

    public DevicePrefer getPrefer ()
    {
        return mPrefer;
    }

    public void setPrefer ( DevicePrefer prefer )
    {
        mPrefer = prefer;
    }
}
