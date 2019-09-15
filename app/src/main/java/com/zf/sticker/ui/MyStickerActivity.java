package com.zf.sticker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.zf.sticker.R;
import com.zf.sticker.adapter.MyStickerAdapter;
import com.zf.sticker.bean.MyStickerItem;
import com.zf.sticker.event.DeleteStickerEvent;
import com.zf.sticker.util.Constant;
import com.zf.sticker.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Mac on 18/2/8.
 */

public class MyStickerActivity extends Activity {
    private String mStoreDir;

    private static final String TAG = "MyStickerActivity";
    private RecyclerView mRecyclerView;
    private MyStickerAdapter mMyStickerAdapter;
    private List<MyStickerItem> mMyStickerItemList = new LinkedList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sticker);

        mStoreDir = FileUtil.getStoreDir(this);

        initData();

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.id_my_sticker_rv);
        mMyStickerAdapter = new MyStickerAdapter(this,mMyStickerItemList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyStickerAdapter);

        findViewById(R.id.id_my_sticker_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                File storeDir = new File(mStoreDir);
                if (!storeDir.exists()) {
                    return;
                }
                File[] stickerFileDirList = storeDir.listFiles();
                for (File stickerFileDir: stickerFileDirList){
                    List<String> pathList = FileUtil.getAllFilePath(stickerFileDir.getPath());
                    if (pathList.size() > 0){
                        MyStickerItem myStickerItem = new MyStickerItem();
                        myStickerItem.setName(stickerFileDir.getName());
                        myStickerItem.setPath(pathList.get(0));
                        mMyStickerItemList.add(myStickerItem);
                    }
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onNext(Object o) {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onComplete() {
                        initView();
                    }
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteSticker(final DeleteStickerEvent deleteStickerEvent){
        Log.d(TAG, "onDeleteSticker: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.ensure_delete)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSticker(deleteStickerEvent.getName(), deleteStickerEvent.getPosition());
                    }
                });
        builder.show();
    }

    private void deleteSticker(final String name, final int position){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                FileUtil.deleteFile(new File(mStoreDir + "/" + name));
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Object o) {
                    }
                    @Override
                    public void onComplete() {
                        mMyStickerItemList.remove(position);
                        mMyStickerAdapter.notifyDataSetChanged();
                        //刷新注册了该事件的界面
                        EventBus.getDefault().post(Constant.EVENT_DELETE_STICKER);
                    }

                });

    }
}
