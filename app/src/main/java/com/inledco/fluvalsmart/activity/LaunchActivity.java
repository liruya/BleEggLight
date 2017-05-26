package com.inledco.fluvalsmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.inledco.fluvalsmart.R;

public class LaunchActivity extends AppCompatActivity
{

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_launch );

        //启动页面1s后进入主页面
        new Handler().postDelayed( new Runnable()
        {
            @Override
            public void run ()
            {
                startActivity( new Intent( LaunchActivity.this, MainActivity.class ) );
                finish();
            }
        }, 1000 );
    }
}
