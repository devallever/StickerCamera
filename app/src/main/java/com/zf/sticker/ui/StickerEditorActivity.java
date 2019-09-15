package com.zf.sticker.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zf.sticker.CustomStickerIconEvent;
import com.zf.sticker.R;
import com.zf.sticker.bean.Sticker;
import com.zf.sticker.adapter.StickerFragmentPagerAdapter;
import com.zf.sticker.bean.StickerData;
import com.zf.sticker.util.Constant;
import com.zf.sticker.util.DialogUtil;
import com.zf.sticker.util.FileUtil;
import com.bumptech.glide.Glide;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StickerEditorActivity extends AppCompatActivity implements StickerFragment.OnStickerClickListener,
        View.OnClickListener{

    public static final String EXTRA_SOURCE_URI = "source_uri";

    private String mStoreDir;

    private static final String TAG = "StickerEditorActivity";
    private boolean mIsViewPagerShow = false;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private RelativeLayout mRelativeLayoutRoot;
    private StickerFragmentPagerAdapter mStickerFragmentPagerAdapter;
    private List<StickerFragment> mStickerFragmentList = new ArrayList<>();
    private Uri mSourceUri;
    private ImageView mImageView;
    private StickerView mStickerView;
    private ImageView iv_store;
    private LinearLayout mLlBack;
    private LinearLayout mLlSave;

    private int mStickerContainerWidth;
    private int mStickerContainerHeight;

    private static boolean isShowAd = false;

    protected AlertDialog mProgressDialog;

    private boolean mIsActivityVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_editor);

        mStoreDir = FileUtil.getStoreDir(this);

        EventBus.getDefault().register(this);

        mSourceUri = getIntent().getParcelableExtra(EXTRA_SOURCE_URI);

        initData();

        initView();

        initDialog();

        //获取从商店下载的贴纸
        getStickerFromFile();

//        if(!isShowAd) {
//            isShowAd = true;
//            //初始化广告
//            MobService.getIns().startService(this, Constant.AD_DATA,new AdFactory());
//            //显示banner
//            MobService.getIns().loadBanner(this, "mobban", new IMobAdListener() {
//                @Override
//                public void onAdLoaded(IMobAd mobAd) {
//                    if (mIsActivityVisible && mobAd != null){
//                        mobAd.showAd();
//                        com.mob.tool.Utils.printInfo("成功拿到Banner广告");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initDialog(){
        mProgressDialog = DialogUtil.createProgressAlertDialog(this, R.string.saving, false);
    }

    private void getStickerFromFile(){
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                List<StickerData> stickerDataList = new ArrayList<>();
                File storeDir = new File(mStoreDir);
                if (!storeDir.exists()){
                    emitter.onComplete();
                }

                //根据文件路径获取贴纸名称和每个贴纸的路径
                //每一页贴纸放在相应文件夹下
                //数组大小也就是文件夹数量
                File[] stickerFileDirList = storeDir.listFiles();
                for (File stickerFileDir: stickerFileDirList){
                    StickerData stickerData = new StickerData();
                    stickerData.setStickerName(stickerFileDir.getName());
                    stickerData.setStickerPaths(FileUtil.getAllFilePath(stickerFileDir.getPath()));
                    stickerDataList.add(stickerData);
                }

                //viewpager增加一个页面，页面数据为Sticker
                for (StickerData stickerData: stickerDataList){
                    if (stickerData.getStickerPaths().size() > 0){
                        Sticker sticker = new Sticker();
                        sticker.setTypePath(stickerData.getStickerPaths().get(0));
                        sticker.setStickerPathList(stickerData.getStickerPaths());
                        StickerFragment stickerFragment = new StickerFragment(StickerEditorActivity.this,sticker);
                        mStickerFragmentList.add(stickerFragment);
                    }else {
                        continue;
                    }
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(List<String> strings) {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                        //回到主线程刷新界面
                        mStickerFragmentPagerAdapter = new StickerFragmentPagerAdapter(getSupportFragmentManager(),mStickerFragmentList);
                        mViewPager.setAdapter(mStickerFragmentPagerAdapter);
                        mTabLayout.setupWithViewPager(mViewPager);
                        resetTabLayout();
                    }
                });
    }

    private void initData(){
        mStickerFragmentList.clear();

        Sticker stickerA = new Sticker();
        stickerA.setResId(getArrayFromResId(R.array.a_stickers));
        stickerA.setTypeResId(R.drawable.a_type);
        StickerFragment stickerFragmentA = new StickerFragment(this,stickerA);

        Sticker stickerB = new Sticker();
        stickerB.setResId(getArrayFromResId(R.array.b_stickers));
        stickerB.setTypeResId(R.drawable.b_type);
        StickerFragment stickerFragmentB = new StickerFragment(this,stickerB);

        Sticker stickerC = new Sticker();
        stickerC.setResId(getArrayFromResId(R.array.c_stickers));
        stickerC.setTypeResId(R.drawable.c_type);
        StickerFragment stickerFragmentC = new StickerFragment(this,stickerC);

        Sticker stickerD = new Sticker();
        stickerD.setResId(getArrayFromResId(R.array.d_stidkers));
        stickerD.setTypeResId(R.drawable.d_type);
        StickerFragment stickerFragmentD = new StickerFragment(this,stickerD);

        Sticker stickerF = new Sticker();
        stickerF.setResId(getArrayFromResId(R.array.f_stidkers));
        stickerF.setTypeResId(R.drawable.f_type);
        StickerFragment stickerFragmentF = new StickerFragment(this,stickerF);

        Sticker stickerG = new Sticker();
        stickerG.setResId(getArrayFromResId(R.array.g_stidkers));
        stickerG.setTypeResId(R.drawable.g_type);
        StickerFragment stickerFragmentG = new StickerFragment(this,stickerG);

        Sticker stickerH = new Sticker();
        stickerH.setResId(getArrayFromResId(R.array.h_stidkers));
        stickerH.setTypeResId(R.drawable.h_type);
        StickerFragment stickerFragmentH = new StickerFragment(this,stickerH);

        //设置ViewPager数据
        mStickerFragmentList.add(stickerFragmentH);
        mStickerFragmentList.add(stickerFragmentG);
        mStickerFragmentList.add(stickerFragmentF);
        mStickerFragmentList.add(stickerFragmentA);
        mStickerFragmentList.add(stickerFragmentB);
        mStickerFragmentList.add(stickerFragmentC);
        mStickerFragmentList.add(stickerFragmentD);

    }

    private void initView(){
        mLlSave = (LinearLayout) findViewById(R.id.id_sticker_editor_ll_save);
        mLlSave.setOnClickListener(this);
        mLlBack = (LinearLayout) findViewById(R.id.id_sticker_editor_ll_back);
        mLlBack.setOnClickListener(this);
        iv_store = (ImageView) findViewById(R.id.id_sticker_editor_iv_store);
        iv_store.setOnClickListener(this);

        mRelativeLayoutRoot= (RelativeLayout)findViewById(R.id.id_sticker_editor_rl_root);
        mTabLayout = (TabLayout)findViewById(R.id.id_sticker_editor_tab_layout_sticker_type);

        mViewPager = (ViewPager)findViewById(R.id.id_sticker_editor_view_pager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //新的tab被选中时调用
                if (!mIsViewPagerShow){
                    showViewPager();
                }
            }

            //点击下一个tab之后，上一个tab退出被选中状态之前需要做的动作
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //当同一个tab被重新点击时调用
                if (!mIsViewPagerShow) {
                    showViewPager();
                }
            }
        });

        mImageView = (ImageView)findViewById(R.id.id_sticker_editor_iv);
        getStickerViewContainerWH();
        Glide.with(this).load(mSourceUri).into(mImageView);
    }

    private Bitmap uri2Bitmap(Uri uri){
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }



    private void initStickerView(){
        mStickerView = (StickerView)findViewById(R.id.id_sticker_editor_stick_view);
        //getStickerViewContainerWH();
        Bitmap bitmap = uri2Bitmap(mSourceUri);
        ViewGroup.LayoutParams layoutParams = mStickerView.getLayoutParams();
        Log.d(TAG, "initStickerView: mStickerContainerWidth = " + mStickerContainerWidth);
        Log.d(TAG, "initStickerView: mStickerContainerHeight = " + mStickerContainerHeight);

        Log.d(TAG, "initStickerView: bitmap width = " + bitmap.getWidth());
        Log.d(TAG, "initStickerView: bitmap height = " + bitmap.getHeight());
        if (bitmap.getWidth() >= bitmap.getHeight()){
            Log.d(TAG, "initStickerView: width > height");
            layoutParams.width = mStickerContainerWidth;
            layoutParams.height = (int) (((float)mStickerContainerWidth / bitmap.getWidth()) * bitmap.getHeight());
            Log.d(TAG, "initStickerView: layoutParmas.h = " + layoutParams.height);
        }else {
            Log.d(TAG, "initStickerView: width <= height");
            layoutParams.height = mStickerContainerHeight;
            layoutParams.width = (int) (((float)mStickerContainerHeight / bitmap.getHeight()) * bitmap.getWidth());
        }
        Log.d(TAG, "initStickerView: sticker.width = " + layoutParams.width);
        Log.d(TAG, "initStickerView: sticker.height = "+ layoutParams.height);
        mStickerView.setLayoutParams(layoutParams);
        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }
            @Override
            public void onStickerClicked(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                Log.d(TAG, "onStickerClicked");
                if (mStickerView.isLocked()){
                    mStickerView.setLocked(false);
                }
                hideViewPager();
            }
            @Override
            public void onStickerDeleted(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {}
            @Override
            public void onStickerDragFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {}
            @Override
            public void onStickerZoomFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {}
            @Override
            public void onStickerFlipped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {}
            @Override
            public void onStickerDoubleTapped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {}
        });

        mStickerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mStickerView.setLocked(false);
                return false;
            }
        });

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.mipmap.sticker_option_close),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.mipmap.sticker_option_scale),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.mipmap.sticker_option_flip),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapStickerIcon heartIcon =
                new BitmapStickerIcon(ContextCompat.getDrawable(this, R.mipmap.sticker_option_favorite),
                        BitmapStickerIcon.LEFT_BOTTOM);
        heartIcon.setIconEvent(new CustomStickerIconEvent());

        mStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));

        mStickerView.setBackgroundColor(Color.WHITE);
        mStickerView.setLocked(false);
        mStickerView.setConstrained(true);

        //界面的跟布局，触摸到跟布局任意位置，则隐藏viewpager
        mRelativeLayoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: click root layout");
                if (!mStickerView.isLocked()){
                    mStickerView.setLocked(true);
                }
                hideViewPager();
            }
        });
    }

    private void getStickerViewContainerWH(){
        final RelativeLayout stickerContainer = (RelativeLayout)findViewById(R.id.id_sticker_editor_rl_sticker_container);
        stickerContainer.post(new Runnable(){
            @Override
            public void run() {
                mStickerContainerWidth= stickerContainer.getMeasuredWidth();
                mStickerContainerHeight = stickerContainer.getMeasuredHeight();
                Log.d(TAG, "run: mStickerContainerWidth  = " + mStickerContainerWidth);
                Log.d(TAG, "run: mStickerContainerHeight = "+ mStickerContainerHeight);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initStickerView();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_sticker_editor_ll_save:
                if (!mProgressDialog.isShowing()){
                    mProgressDialog.show();
                }
                mLlSave.setClickable(false);
                saveImage();
                break;
            case R.id.id_sticker_editor_ll_back:
                finish();
                break;
            case R.id.id_sticker_editor_iv_store:
                StoreActivity.startSelf(this, true);
                break;
            default:
                break;
        }
    }

    private void saveImage(){
        File file = FileUtil.getNewFile(this, "stickercamera");
        if (file != null) {
            FileUtil.saveImageFile(file, mStickerView.createBitmap());
            //goto save success Activity
            ShareActivity.startSelf(this, file.getPath());
            Toast.makeText(this,"save success ",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        } else {
            Toast.makeText(this, "the file is null", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
            mLlSave.setClickable(true);
        }
    }

    private void resetTabLayout() {
//        /**
//         * 使用tablayout + viewpager时注意 如果设置了setupWithViewPager
//         * 则需要重新执行下方对每个条目赋值
//         * 否则会出现icon文字不显示的bug
//         */
//        for (int i=0;i<mStickerFragmentList.size();i++){
//            mTabLayout.getTabAt(i).setText(mStickerFragmentList.get(i).getSticker().getTitle());
//        }
        //为每个tab设置图片
        for (int i=0;i<mTabLayout.getTabCount();i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab!=null){
                String typePath = mStickerFragmentList.get(i).getSticker().getTypePath();
                if (typePath == null){
                    tab.setCustomView(getTabView(mStickerFragmentList.get(i).getSticker().getTypeResId()));
                }else {
                    tab.setCustomView(getTabView(typePath));
                }

            }
        }
    }


    private View getTabView(int stickerTypeResId){
        View view = LayoutInflater.from(this).inflate(R.layout.item_sticker_type,null,false);
        ImageView imageView = (ImageView)view.findViewById(R.id.id_item_sticker_type_iv);
        imageView.setImageResource(stickerTypeResId);
        return view;
    }

    private View getTabView(String stickerTypePath){
        View view = LayoutInflater.from(this).inflate(R.layout.item_sticker_type,null,false);
        ImageView imageView = (ImageView)view.findViewById(R.id.id_item_sticker_type_iv);
        Glide.with(this).load(stickerTypePath).into(imageView);
        return view;
    }

    @Override
    public void onStickClick(int resId, String path) {
        //mImageView.setImageResource(resId);
        //add sticker
        Log.d(TAG, "onStickClick: ");
        Drawable drawable = null;
        DrawableSticker drawableSticker;
        if(path == null){
            drawable = ContextCompat.getDrawable(this,resId);
            //BitmapDrawable bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(),resId));
            Log.d(TAG, "onStickClick: resid = " + resId);
            Log.d(TAG, "onStickClick: drawable.getIntrinsicWidth = " + drawable.getIntrinsicWidth());
            Log.d(TAG, "onStickClick: drawable.getIntrinsicWidth = " + drawable.getIntrinsicHeight());
            drawableSticker = new DrawableSticker(drawable);
        }else {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Log.d(TAG, "onStickClick: bitmap.width = " + bitmap.getWidth());
            Log.d(TAG, "onStickClick: bitmap.height = " + bitmap.getHeight());
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            Log.d(TAG, "onStickClick: bitmapDrawable.getIntrinsicWidth = " + bitmapDrawable.getIntrinsicWidth());
            Log.d(TAG, "onStickClick: bitmapDrawable.getIntrinsicWidth = " + bitmapDrawable.getIntrinsicHeight());
            Log.d(TAG, "onStickClick: path = " + path);


            Drawable drawableFromPath = Drawable.createFromPath(path);
            Log.d(TAG, "onStickClick: drawableFromPath.getIntrinsicWidth = " + drawableFromPath.getIntrinsicWidth());
            Log.d(TAG, "onStickClick: drawableFromPath.getIntrinsicWidth = " + drawableFromPath.getIntrinsicHeight());
            drawableSticker = new DrawableSticker(drawableFromPath);
        }

        Log.d(TAG, "onStickClick: drawable width = " + drawableSticker.getDrawable().getIntrinsicWidth());
        Log.d(TAG, "onStickClick: drawable height = " + drawableSticker.getDrawable().getIntrinsicHeight());

        float scaleFactor, widthScaleFactor, heightScaleFactor;
        widthScaleFactor = (float) mStickerContainerWidth / drawableSticker.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) mStickerContainerHeight / drawableSticker.getDrawable().getIntrinsicHeight();
        Log.d(TAG, "onStickClick: widthScaleFactor = " + widthScaleFactor);
        Log.d(TAG, "onStickClick: heightScaleFactor = " + heightScaleFactor);
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;
        if (path == null){
            drawableSticker.getMatrix()
                    .postScale(scaleFactor / 2, scaleFactor / 2, mStickerContainerWidth / 2, mStickerContainerHeight/ 2);
        }else {
            drawableSticker.getMatrix()
                    .postScale(scaleFactor / 18, scaleFactor /18, mStickerContainerWidth / 18, mStickerContainerHeight/ 18);
        }


        mStickerView.addSticker(drawableSticker);
    }


    private void showViewPager(){
        mViewPager.setVisibility(View.VISIBLE);
        mIsViewPagerShow = true;
    }

    private void hideViewPager(){
        mViewPager.setVisibility(View.INVISIBLE);
        mIsViewPagerShow = false;
    }

    private int[] getArrayFromResId(int resId){
        TypedArray array = this.getResources().obtainTypedArray(resId);

        int len = array.length();
        int[] intArray = new int[array.length()];

        for(int i = 0; i < len; i++){
            intArray[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return intArray;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStickerOption(String event){
        if (Constant.EVENT_DELETE_STICKER.equals(event) || Constant.EVENT_ADD_STICKER.equals(event)){
            //TODO 
            Log.d(TAG, "onStickerOption: ");
            hideViewPager();
            initData();
            getStickerFromFile();
        }

    }

    public static void startSelf(Context context, Uri uri){
        Intent intent = new Intent(context, StickerEditorActivity.class);
        intent.putExtra(StickerEditorActivity.EXTRA_SOURCE_URI, uri);
        context.startActivity(intent);
    }

}
