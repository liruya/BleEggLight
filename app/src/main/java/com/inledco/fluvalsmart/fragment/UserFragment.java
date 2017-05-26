package com.inledco.fluvalsmart.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.activity.MainActivity;
import com.inledco.fluvalsmart.prefer.Setting;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends BaseFragment
{

    private SwitchCompat setting_auth_ble;
    private SwitchCompat setting_exit_close_ble;
    private AppCompatTextView setting_lang;
    private LinearLayout setting_item_lang;
    private AppCompatTextView setting_profile;
    private AppCompatTextView setting_um;
    private AppCompatTextView setting_about;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_user, null );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    private void initView ( View view )
    {
        setting_about = (AppCompatTextView) view.findViewById( R.id.setting_about );
        setting_um = (AppCompatTextView) view.findViewById( R.id.setting_um );
        setting_profile = (AppCompatTextView) view.findViewById( R.id.setting_profile );
        setting_item_lang = (LinearLayout) view.findViewById( R.id.setting_item_lang );
        setting_lang = (AppCompatTextView) view.findViewById( R.id.setting_lang );
        setting_exit_close_ble = (SwitchCompat) view.findViewById( R.id.setting_exit_close_ble );
        setting_auth_ble = (SwitchCompat) view.findViewById( R.id.setting_auth_ble );
    }

    private void initData()
    {
        setting_auth_ble.setChecked( Setting.mBleEnabled );
        setting_exit_close_ble.setChecked( Setting.mExitTurnOffBle );
        switch ( Setting.mLang )
        {
            case Setting.LANGUAGE_ENGLISH:
                setting_lang.setText( R.string.setting_lang_english );
                break;

            case Setting.LANGUAGE_GERMANY:
                setting_lang.setText( R.string.setting_lang_germany );
                break;

            case Setting.LANGUAGE_FRENCH:
                setting_lang.setText( R.string.setting_lang_french );
                break;

            default:
                setting_lang.setText( R.string.mode_auto );
                break;
        }
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.setLocale( Locale.getDefault() );
        config.locale = Locale.ENGLISH;
        resources.updateConfiguration(config, dm);
    }

    private void initEvent()
    {
        setting_item_lang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View view )
            {
                showLangDialog();
            }
        } );
    }

    private void showLangDialog()
    {
        int idx = 0;
        final String[] sl = new String[]{Setting.mLang};
        final String[] ll = new String[]{ Setting.LANGUAGE_AUTO,
                                          Setting.LANGUAGE_ENGLISH,
                                          Setting.LANGUAGE_GERMANY,
                                          Setting.LANGUAGE_FRENCH};
        final CharSequence[] langs = new CharSequence[]{ getString( R.string.mode_auto ),
                                                         getString( R.string.setting_lang_english ),
                                                         getString( R.string.setting_lang_germany ),
                                                         getString( R.string.setting_lang_french ) };
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setTitle( R.string.setting_language );
        if ( sl != null )
        {
            for ( int i = 0; i < ll.length; i++ )
            {
                if ( sl[0].equals( ll[i] ) )
                {
                    idx = i;
                    break;
                }
            }
        }
        builder.setSingleChoiceItems( langs,
                                      idx,
                                      new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialogInterface, int i )
            {
                sl[0] = ll[i];
                Log.e( TAG, "onClick: " + sl[0] );
            }
        } );
        builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialogInterface, int i )
            {
                dialogInterface.dismiss();
            }
        } );
        builder.setPositiveButton( R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialogInterface, int i )
            {
                if ( !Setting.mLang.equals( sl[0] ) )
                {
                    Setting.mLang = sl[0];
                    SharedPreferences defaultSet = PreferenceManager.getDefaultSharedPreferences( getContext() );
                    SharedPreferences.Editor editor = defaultSet.edit();
                    editor.putString( Setting.SET_LANGUAGE, Setting.mLang );
                    SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
                    Setting.changeAppLanguage( getContext() );
                    Intent intent = new Intent( getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                }
                dialogInterface.dismiss();
            }
        } );
        builder.setCancelable( false );
        builder.show();
    }
}
