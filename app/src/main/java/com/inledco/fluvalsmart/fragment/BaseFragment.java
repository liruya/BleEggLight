package com.inledco.fluvalsmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.inledco.fluvalsmart.prefer.Setting;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment
{

    @Override
    public void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Setting.changeAppLanguage( getContext() );
    }

}
