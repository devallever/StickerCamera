package com.allever.sticker.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.allever.sticker.R;

/**
 * Created by Mac on 18/2/11.
 */

public class DialogUtil {

    public static ProgressDialog createProgerssDialog(Context context, String message, boolean cancleAble){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancleAble);
        return progressDialog;
    }

    public static AlertDialog createProgressAlertDialog(Context context,String msg, boolean canCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress,null,false);
        ((TextView)view.findViewById(R.id.id_dialog_tv_msg)).setText(msg);
        builder.setView(view);
        builder.setCancelable(canCancel);
        return builder.create();
    }

    public static AlertDialog createProgressAlertDialog(Context context,int msg, boolean canCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress,null,false);
        ((TextView)view.findViewById(R.id.id_dialog_tv_msg)).setText(msg);
        builder.setView(view);
        builder.setCancelable(canCancel);
        return builder.create();
    }

}
