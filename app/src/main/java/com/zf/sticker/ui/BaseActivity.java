//package com.zf.sticker.ui;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.Toast;
//
//import com.zf.sticker.AdFactory;
//import com.zf.sticker.util.Constant;
//import com.mob.main.IMobAd;
//import com.mob.main.IMobAdListener;
//import com.mob.main.MobService;
//
///**
// * Created by Mac on 18/1/2.
// */
//
//public class BaseActivity extends AppCompatActivity {
//    private static IMobAd mInterMobAd = null;
//    private static IMobAd mBannerMobAd = null;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //初始化广告
//        MobService.getIns().startService(this, Constant.AD_DATA, new AdFactory());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobService.getIns().onPause(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        MobService.getIns().onResume(this);
//    }
//
//    protected void showToast(String msg){
//        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
//    }
//
//    protected void showBannerAd(){
//        MobService.getIns().loadBanner(this, "mobban", new IMobAdListener() {
//            @Override
//            public void onAdLoaded(IMobAd mobAd) {
//                mobAd.showAd();
//                com.mob.tool.Utils.printInfo("成功拿到Banner广告");
//                mBannerMobAd = mobAd;
//            }
//
//            @Override
//            public void onAdFailedToLoad() {
//                com.mob.tool.Utils.printInfo("Banner广告轮询失败");
//            }
//        });
//
//    }
//
//    protected void showInterAd(String adTag){
//        MobService.getIns().loadInterstitalAd(this, adTag, new IMobAdListener() {
//            @Override
//            public void onAdLoaded(IMobAd mobAd) {
//                mobAd.showAd();
//                com.mob.tool.Utils.printInfo("成功拿到Inter广告");
//                mInterMobAd = mobAd;
//            }
//
//            @Override
//            public void onAdFailedToLoad() {
//                com.mob.tool.Utils.printInfo("Inter广告轮询失败");
//            }
//        });
//    }
//
//
//
//}
