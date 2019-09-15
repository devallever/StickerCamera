package com.zf.sticker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zf.sticker.ControllerEnum;
import com.zf.sticker.R;
import com.zf.sticker.util.Constant;
import com.bumptech.glide.Glide;
import com.zf.sticker.util.PermissionUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Mac on 18/1/3.
 */

public class ShareActivity extends Activity implements View.OnClickListener{

    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 0x02;
    private static final int REQUEST_CODE_PERMISSION_SETTING_MANUALLY = 0x03;

    public static final String EXTRA_IMAGE_PATH = "image_path";


    private String mImagePath;

    private ImageView mIvDisplay;

    private LinearLayout mLlBack;

    private TextView mTvHome;

    private ImageView mBtnShareMore;
    private ImageView mBtnFacebook;
    private ImageView mBtnTwitter;
    private ImageView mBtnLine;
    private ImageView mBtnWhatsapp;

    private static boolean isShowAd = false;

    private boolean mIsActivityVisible = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

        //初始化控件及设置监听器
        initView();

//        //如果是第一次启动，则加载并显示广告
//        if(!isShowAd) {
//            isShowAd = true;
//
//            //初始化广告
//            MobService.getIns().startService(this, Constant.AD_DATA,new AdFactory());
//
//            //显示插屏
//            MobService.getIns().loadInterstitalAd(this, "mobinter", new IMobAdListener() {
//                @Override
//                public void onAdLoaded(IMobAd mobAd) {
//                    if(mIsActivityVisible && mobAd != null){
//                        mobAd.showAd();
//                    }
//                }
//
//                @Override
//                public void onAdFailedToLoad() {
//                    com.mob.tool.Utils.printInfo("广告轮询失败");
//                }
//            });
//
//            //加载并显示banner
//            MobService.getIns().loadBanner(this, "mobban", new IMobAdListener() {
//                @Override
//                public void onAdLoaded(IMobAd mobAd) {
//                    if (mIsActivityVisible && mobAd != null){
//                        mobAd.showAd();
//                    }
//                }
//
//                @Override
//                public void onAdFailedToLoad() {
//                    com.mob.tool.Utils.printInfo("Banner广告轮询失败");
//                }
//            });
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobService.getIns().onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobService.getIns().onResume(this);
        mIsActivityVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsActivityVisible = false;
    }

    private void initView(){
        mIvDisplay = (ImageView)findViewById(R.id.id_share_iv);
        Glide.with(this).load(new File(mImagePath)).into(mIvDisplay);

        mTvHome = findViewById(R.id.id_share_tv_home);
        mTvHome.setOnClickListener(this);
        mLlBack = findViewById(R.id.id_share_ll_back);
        mLlBack.setOnClickListener(this);

        mBtnShareMore = (ImageView)findViewById(R.id.id_share_btn_more);
        mBtnShareMore.setOnClickListener(this);
        mBtnFacebook = (ImageView)findViewById(R.id.id_share_btn_facebook);
        mBtnFacebook.setOnClickListener(this);
        mBtnTwitter = (ImageView)findViewById(R.id.id_share_btn_twtter);
        mBtnTwitter.setOnClickListener(this);
        mBtnLine = (ImageView)findViewById(R.id.id_share_btn_line);
        mBtnLine.setOnClickListener(this);
        mBtnWhatsapp = (ImageView)findViewById(R.id.id_share_btn_whatsapp);
        mBtnWhatsapp.setOnClickListener(this);

        RelativeLayout rlEditNext = findViewById(R.id.id_share_rl_edit_next);
        rlEditNext.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_share_ll_back:
                finish();
                break;
            case R.id.id_share_tv_home:
                backHome();
                break;
            case R.id.id_share_btn_facebook:
                shareSingle(Constant.PKG_FACEBOOK);
                break;
            case R.id.id_share_btn_twtter:
                shareSingle(Constant.PKG_TWTTER);
                break;
            case R.id.id_share_btn_line:
                shareSingle(Constant.PKG_LINE);
                break;
            case R.id.id_share_btn_whatsapp:
                shareSingle(Constant.PKG_WHATSAPP);
                break;
            case R.id.id_share_btn_more:
                shareMore();
                break;
            case R.id.id_share_rl_edit_next:
                //如果具有读取存储权限，则打开相册选择图片
                if (PermissionUtil.hasPermission(this,
                        PermissionUtil.PERMISSION_READ_EXTRNAL_STORAGE,
                        PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)){
                    ControllerEnum.getIns().chooseImageFromGallery(this, REQUEST_CODE_PICK_IMAGE);
                }else {
                    //没有权限，则申请相应权限，后回调onRequestPermissionsResult()
                    PermissionUtil.requestPermission(this,
                            PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE,
                            REQUEST_CODE_PERMISSION_STORAGE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (PermissionUtil.hasPermission(this,PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    //用户已授权，则打开相册选择图片
                    ControllerEnum.getIns().chooseImageFromGallery(this, REQUEST_CODE_PICK_IMAGE);

                }else {
                    //如果用户选择了不在提示,则弹出手动设置权限的对话框
                    if (PermissionUtil.hasAlwaysDeniedPermission(this,
                            PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE)){
                        ControllerEnum.getIns().openPermissionSetting(this,
                                REQUEST_CODE_PERMISSION_SETTING_MANUALLY);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //先进行resultCode判断，避免每个requestCode都有进行判断
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_PICK_IMAGE:
                    /**
                     * 如何用户选择了图片，则打开剪裁界面
                     * 注意：打开剪裁界面后，该Activity不能销毁，否则从谷歌照片选择图片后的Uri是没有权限
                     * SecurityException: Permission Denial +
                     * * com.google.android.apps.photos.contentprovider.impl.MediaContentProvider* requires the provider be exported, or grantUriPermission()
                    */
                    CropActivity.startSelf(this, data.getData());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        backHome();
        super.onBackPressed();
    }

    private void backHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void shareSingle(String containAppPackage){
        Uri imageUri = Uri.fromFile(new File(mImagePath));
        Intent fbIntent = new Intent(Intent.ACTION_SEND);
        fbIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        fbIntent.setType("image/*");

        //判断手机是否安装该应用
        boolean existApp = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(fbIntent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().contains(containAppPackage)) {
                fbIntent.setPackage(info.activityInfo.packageName);
                existApp = true;
            }
        }

        //如果已安装，则调用分享，否则提示未安装
        if (existApp){
            startActivity(fbIntent);
        }else {
            Toast.makeText(this,R.string.not_install_app,Toast.LENGTH_SHORT).show();
        }
    }

    private void shareMore(){
        Uri imageUri = Uri.fromFile(new File(mImagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_to)));
    }

    public static void startSelf(Context context, String path){
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareActivity.EXTRA_IMAGE_PATH,path);
        context.startActivity(intent);
    }
}
