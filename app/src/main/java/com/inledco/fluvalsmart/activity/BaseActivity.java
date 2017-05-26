package com.inledco.fluvalsmart.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.inledco.fluvalsmart.prefer.Setting;

public class BaseActivity extends AppCompatActivity
{

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Setting.initSetting( BaseActivity.this );
        Setting.changeAppLanguage( BaseActivity.this );
    }
}
