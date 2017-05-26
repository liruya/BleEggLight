package com.inledco.fluvalsmart.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.inledco.fluvalsmart.R;
import com.inledco.fluvalsmart.bean.DevicePrefer;
import com.inledco.fluvalsmart.constant.ConstVal;
import com.inledco.fluvalsmart.util.BleUtil;
import com.inledco.fluvalsmart.util.PreferenceUtil;

/**
 * Created by liruya on 2016/11/25.
 */

public class RenameDialog
{
    public static void showDialog ( final Context context, final DevicePrefer prefer )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from( context ).inflate( R.layout.dialog_rename, null );
        AppCompatButton btn_cancel = (AppCompatButton) view.findViewById( R.id.rename_cancel );
        AppCompatButton btn_rename = (AppCompatButton) view.findViewById( R.id.rename_confirm );
        final AppCompatEditText newname = (AppCompatEditText) view.findViewById( R.id.rename_newname );
        newname.setText( prefer.getDeviceName() );
        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View view )
            {
                dialog.dismiss();
            }
        } );
        btn_rename.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View view )
            {
                if ( TextUtils.isEmpty( newname.getText() ) )
                {
                    newname.setError( context.getString( R.string.error_input_empty ) );
                }
                else if ( newname.getText().toString().equals( prefer.getDeviceName() ) )
                {
                    dialog.dismiss();
                }
                else
                {
                    prefer.setDeviceName( newname.getText().toString() );
                    BleUtil.renameDevice( prefer.getDeviceMac(), prefer.getDeviceName() );
                    PreferenceUtil.setObjectToPrefer( context, ConstVal.DEV_PREFER_FILENAME, prefer, prefer.getDeviceMac() );
                    dialog.dismiss();
                }
            }
        } );
        dialog.setView( view );
        dialog.setCanceledOnTouchOutside( false );
        dialog.show();
    }
}
