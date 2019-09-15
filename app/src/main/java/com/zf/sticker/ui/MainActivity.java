package com.zf.sticker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.util.Utils;
import com.zf.sticker.ControllerEnum;
import com.zf.sticker.R;
import com.zf.sticker.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Allever on 2018/1/1.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    private static final int REQUEST_PICK_IMAGE = 1001;
    private static final int REQUEST_SAF_PICK_IMAGE = 1002;
    private static final int RESULD_CODE_TAKE_PHOTO = 1004;
    private static final int REQUEST_PERMISSION = 1005;
    private static final int REQUEST_CODE_PERMISSION_SETTING = 1006;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1007;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 1008;

    private ImageView btn_pick_img;
    private ImageView btn_camera;
    private ImageView btn_store;

    private Uri mImageUri;

    private List<String> mRequestPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestPermissionList.add(PermissionUtil.PERMISSION_CAMERA);
        mRequestPermissionList.add(PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        mRequestPermissionList.add(PermissionUtil.PERMISSION_READ_EXTRNAL_STORAGE);

        initView();

        checkPermission();
    }

    private void initView(){
        btn_pick_img = (ImageView)findViewById(R.id.id_main_btn_pick_img);
        btn_pick_img.setOnClickListener(this);
        btn_camera = (ImageView)findViewById(R.id.id_main_btn_camera);
        btn_camera.setOnClickListener(this);
        btn_store = (ImageView)findViewById(R.id.id_main_btn_store);
        btn_store.setOnClickListener(this);
    }

    /**
     * 检查权限
     * */
    private void checkPermission(){
        //如果没有相应的权限，则申请权限
        if (!PermissionUtil.hasPermission(this,mRequestPermissionList)){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    /**
     * 手动打开权限管理
     * */
    private void openPermissionSetting(){
        PermissionUtil.openPermissionManually(this, REQUEST_CODE_PERMISSION_SETTING, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION:
                if (PermissionUtil.hasPermission(this,mRequestPermissionList)) {
                    //允许
                }else {
                    if (PermissionUtil.hasAlwaysDeniedPermission(this,mRequestPermissionList)){
                        openPermissionSetting();
                    }
                }
                break;
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (PermissionUtil.hasPermission(this,PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    //允许
                    ControllerEnum.getIns().chooseImageFromGallery(this, REQUEST_PICK_IMAGE);
                }else {
                    if (PermissionUtil.hasAlwaysDeniedPermission(this,PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)){
                        openPermissionSetting();
                    }
                }

                break;
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (PermissionUtil.hasPermission(this,PermissionUtil.PERMISSION_CAMERA)) {
                    //允许
                    mImageUri = ControllerEnum.getIns().openCamera(this, RESULD_CODE_TAKE_PHOTO);
                }else {
                    if (PermissionUtil.hasAlwaysDeniedPermission(this,PermissionUtil.PERMISSION_CAMERA)){
                        openPermissionSetting();
                    }
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_main_btn_pick_img:
                //如果有存储权限，则打开相册选择图片，否则申请存储权限
                if (PermissionUtil.hasPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)){
                    ControllerEnum.getIns().chooseImageFromGallery(this, REQUEST_PICK_IMAGE);
                }else {
                    PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_STORAGE);
                }
                break;
            case R.id.id_main_btn_camera:
                //如果有相机权限，则打开相机，否则申请相机权限
                if (PermissionUtil.hasPermission(this, PermissionUtil.PERMISSION_CAMERA)){
                    mImageUri = ControllerEnum.getIns().openCamera(this, RESULD_CODE_TAKE_PHOTO);
                }else {
                    PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_CAMERA, REQUEST_CODE_PERMISSION_CAMERA);
                }
                break;
            case R.id.id_main_btn_store:
                StoreActivity.startSelf(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    CropActivity.startSelf(this,result.getData());
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    CropActivity.startSelf(this, Utils.ensureUriPermission(MainActivity.this, result));
                    break;
                case RESULD_CODE_TAKE_PHOTO:
                    CropActivity.startSelf(this,mImageUri);
                    break;
                default:
                    break;
            }
        }
    }

    private long mPrevClickBackTime = -1;
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (mPrevClickBackTime == -1 || currentTime - mPrevClickBackTime > 3000) {
            mPrevClickBackTime = currentTime;
            Toast.makeText(this, "Press again to exit",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Process.killProcess(Process.myPid());
    }
}
