package com.inledco.fluvalsmart.prefer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.DisplayMetrics;

import java.util.Locale;

import static com.inledco.fluvalsmart.util.BleUtil.mAdapter;

/**
 * Created by Administrator on 2016/10/19.
 * 保存在本地的参数数据类
 */

public class Setting
{
    public static final String SET_BLE_ENABLED = "BLE_ENABLED";
    public static final String SET_EXIT_TURNOFF_BLE = "EXIT_TURNOFF_BLE";
    public static final String SET_EXIT_TIP = "EXIT_TIP";
    public static final String SET_LANGUAGE = "LANGUAGE";
    public static final String LANGUAGE_AUTO = "auto";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_GERMANY = "de";
    public static final String LANGUAGE_FRENCH = "fr";

    public static boolean mBleEnabled;
    public static boolean mExitTurnOffBle;
    public static boolean mExitTip;
    public static String mLang;

    /**
     * 初始化ble设置
     * @param context
     */
    public static void initSetting (Context context )
    {
        SharedPreferences defaultSet = PreferenceManager.getDefaultSharedPreferences( context );
        mBleEnabled = defaultSet.getBoolean( SET_BLE_ENABLED, false );
        mExitTurnOffBle = defaultSet.getBoolean( SET_EXIT_TURNOFF_BLE, false );
        mExitTip = defaultSet.getBoolean( SET_EXIT_TIP, true );
        mLang = defaultSet.getString( SET_LANGUAGE, LANGUAGE_AUTO );
    }

    public static void changeAppLanguage( Context context )
    {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        switch ( mLang )
        {
            case LANGUAGE_ENGLISH:
                config.locale = Locale.ENGLISH;
                break;
            case LANGUAGE_GERMANY:
                config.locale = Locale.GERMANY;
                break;
            case LANGUAGE_FRENCH:
                config.locale = Locale.FRENCH;
                break;
            default:
                config.locale = Locale.getDefault();
                break;
        }
        res.updateConfiguration( config, dm );
    }

    /**
     * 退出时关闭蓝牙确认窗口
     * @param context
     */
    public static void showDialog( final Context context)
    {
        final boolean[] show = new boolean[]{false};
        final SharedPreferences defaultSet = PreferenceManager.getDefaultSharedPreferences(
        context );
        final SharedPreferences.Editor editor = defaultSet.edit();
        AlertDialog.Builder builder= new AlertDialog.Builder( context );
        builder.setTitle( "关闭蓝牙？" );
        builder.setMultiChoiceItems( new CharSequence[]{"不再提示"},
                                     new boolean[]{true},
                                     new DialogInterface.OnMultiChoiceClickListener()
                                     {
                                         @Override
                                         public void onClick ( DialogInterface dialog,
                                                               int which,
                                                               boolean isChecked )
                                         {
                                             show[which] = !isChecked;
                                         }
                                     } );
        builder.setPositiveButton( "是", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick ( DialogInterface dialog, int which )
            {
                Setting.mExitTip = show[0];
                Setting.mExitTurnOffBle = true;
                editor.putBoolean( SET_EXIT_TURNOFF_BLE,  Setting.mExitTurnOffBle);
                editor.putBoolean( SET_EXIT_TIP, Setting.mExitTip );
                SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
                mAdapter.disable();
                if ( context instanceof Activity )
                {
                    ( (Activity) context ).finish();
                }
            }
        } );

        builder.setNegativeButton( "否", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick ( DialogInterface dialog, int which )
            {
                Setting.mExitTip = show[0];
                Setting.mExitTurnOffBle = false;
                editor.putBoolean( SET_EXIT_TURNOFF_BLE, Setting.mExitTurnOffBle);
                editor.putBoolean( SET_EXIT_TIP, Setting.mExitTip );
                SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
                dialog.dismiss();
                if ( context instanceof Activity )
                {
                    ( (Activity) context ).finish();
                }
            }
        } );
        builder.show();
    }
}
