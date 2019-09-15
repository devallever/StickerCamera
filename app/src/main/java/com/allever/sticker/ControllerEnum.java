package com.allever.sticker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.allever.sticker.util.PermissionUtil;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Allever
 * @date 18/5/25
 */

public enum ControllerEnum {
    INSTANCE;
    public static ControllerEnum getIns(){
        return INSTANCE;
    }

    public Uri openCamera(Activity activity, int requestCode){
        if (activity == null){
            return null;
        }

        Uri imageUri = null;
        File outputImage = new File(activity.getExternalCacheDir(),"output_image.jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        //如果版本号在24或以上，则调用新方式获取uri
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        activity.startActivityForResult(cameraIntent, requestCode);
        return imageUri;
    }

    /**
     * 从相册中选择一张图片后， 回调Activity的onActivityResult方法进行处理
     * */
    public void chooseImageFromGallery(Activity activity, int requestCode){
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(albumIntent, requestCode);
    }

    /**
     * 手动打开权限设置界面，手动添加权限, 回调Activity的onActivityResult方法进行处理
     * */
    public void openPermissionSetting(Activity activity, int requestCode){
        PermissionUtil.openPermissionManually(activity, requestCode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
