package com.inledco.fluvalsmart.util;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 获取网络数据工具类
 * Created by liruya on 2016/9/8.
 */
public class OkHttpUtil
{
    private static final String TAG = "OkHttpUtil";
    private static final OkHttpClient httpClient = new OkHttpClient();

    public static void enqueue( final int code, Request request)
    {
        httpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure ( okhttp3.Call call, IOException e )
            {

            }

            @Override
            public void onResponse ( okhttp3.Call call, Response response ) throws IOException
            {
                final String json = response.body().string();
            }

        });
    }
}
