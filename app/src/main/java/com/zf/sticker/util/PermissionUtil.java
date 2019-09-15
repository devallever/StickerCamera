package com.zf.sticker.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;


import com.zf.sticker.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mac on 18/2/6.
 */

public class PermissionUtil {
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_EXTRNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static boolean hasPermission(Context context, String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    public static boolean hasPermission(Context context, List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                continue;
            }
            result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result != AppOpsManagerCompat.MODE_ALLOWED){
                return false;
            }
        }
        return true;
    }

    public static boolean hasAlwaysDeniedPermission(Activity activity, String... deniedPermissions) {
        return hasAlwaysDeniedPermission(activity,Arrays.asList(deniedPermissions));
    }

    public static boolean hasAlwaysDeniedPermission(Activity activity, List<String> deniedPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return false;
        }

        if (deniedPermissions.size() == 0) {
            return false;
        }

        for (String permission : deniedPermissions) {
            boolean rationale = activity.shouldShowRequestPermissionRationale(permission);
            if (!rationale) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAlwaysDeniedPermission(Activity activity, String deniedPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return false;
        }
        boolean rationale = activity.shouldShowRequestPermissionRationale(deniedPermissions);
        if (!rationale){
            return true;
        }
        return false;
    }

    public static void openPermissionManually(final Activity activity, final int requestCode, DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.need_some_permission);
        builder.setTitle(R.string.warm_tips);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, requestCode);
            }
        });
        builder.setNegativeButton(R.string.cancel, cancelListener);
        builder.show();
    }

    public static void requestPermission(Activity activity, String permission, int requestCode){
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }


}
