package com.zf.sticker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.DownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zf.sticker.R;
import com.zf.sticker.event.DownloadEvent;
import com.zf.sticker.util.Constant;
import com.zf.sticker.util.FileUtil;
import com.zf.sticker.util.ZipUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Mac on 18/2/24.
 */

public class DownloadStickerService extends Service {

    private static final String TAG = "DownloadStickerService";

    private static final String ACTION_CLICK = "ACTION_CLICK";

    private String mStoreDir;
    private String mStickerName;
    private String mUrl;

    private MyBroadcastReceiver mMyBroadcastReceiver;

    private boolean mDownloading = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CLICK);
        registerReceiver(mMyBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMyBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        mStoreDir = FileUtil.getStoreDir(this);
        mStickerName = intent.getStringExtra(Constant.EXTRA_STICKER_NAME);
        mUrl = intent.getStringExtra(Constant.EXTRA_STICKER_DOWNLOAD_URL);
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        initDownload();
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private FileDownloadListener mFileDownloadListener;
    private int mTaskId;

    private void initDownload(){
        mDownloading = true;
        FileDownloader.setup(this);
        mFileDownloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int progress = (int) (((float)soFarBytes/(float)totalBytes)*100);
                mNotificationManager.notify(mTaskId, getNotification(progress,true));
            }
            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {}
            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int progress = (int) (((float)soFarBytes/(float)totalBytes)*100);
                EventBus.getDefault().post(new DownloadEvent(progress));

                mNotificationManager.notify(mTaskId, getNotification(progress,true));
            }
            @Override
            protected void blockComplete(BaseDownloadTask task) {}
            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {}
            @Override
            protected void completed(BaseDownloadTask task) {
                mNotificationManager.notify(mTaskId, getDefaultNotification(mStickerName + ": " + getResources().getString(R.string.download_finish)));
                unZip();

            }
            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int progress = (int) (((float)soFarBytes/(float)totalBytes)*100);
                mNotificationManager.notify(mTaskId, getNotification(progress,false));
            }
            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                mNotificationManager.notify(mTaskId, getDefaultNotification(getResources().getString(R.string.download_fail)));
                EventBus.getDefault().post(Constant.EVENT_DOWNLOAD_ERROR);
                e.printStackTrace();
                stopSelf();
            }
            @Override
            protected void warn(BaseDownloadTask task) {}
        };
    }

    private void startDownload(){
        DownloadTask mDownloadTask = (DownloadTask) FileDownloader.getImpl().create(mUrl);
        mTaskId = mDownloadTask.setPath(mStoreDir + "/" + mStickerName + ".zip" ,false)
                .setListener(mFileDownloadListener)
                .start();

    }

    private void unZip(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                String fileName = FileUtil.getFileNameByUrl(mUrl);
                try {
                    ZipUtil.unZipFolder(mStoreDir + "/" + fileName, mStoreDir);
                    FileUtil.deleteFile(new File(mStoreDir + "/" + fileName));
                    emitter.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onError(Throwable e) {
                        mNotificationManager.notify(mTaskId, getDefaultNotification(getResources().getString(R.string.download_fail)));
                        EventBus.getDefault().post(Constant.EVENT_DOWNLOAD_ERROR);
                        stopSelf();
                    }
                    @Override
                    public void onNext(Object o) {}
                    @Override
                    public void onComplete() {
                        //刷新贴纸详细信息界面
                        EventBus.getDefault().post(Constant.EVENT_DOWNLOAD_FINISH);
                        //刷新商店 添加贴纸界面
                        EventBus.getDefault().post(Constant.EVENT_ADD_STICKER);
                        stopSelf();
                    }
                });
    }

    private NotificationManager mNotificationManager;
    private Notification getNotification(int progress, boolean downloading){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.noti_download);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_128));
        builder.setAutoCancel(true);
        if (progress > 0){
            remoteViews.setTextViewText(R.id.id_noti_download_tv_tips, mStickerName + ": " + progress + "%");
        }
        remoteViews.setProgressBar(R.id.id_noti_download_progress_bar,100,  progress,false);

        if (downloading){
            remoteViews.setImageViewResource(R.id.id_noti_download_iv_pause_start, R.drawable.ic_pause);
        }else {
            remoteViews.setImageViewResource(R.id.id_noti_download_iv_pause_start, R.drawable.ic_play);
        }

        Intent intent = new Intent(ACTION_CLICK);
        PendingIntent pauseStartIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.id_noti_download_iv_pause_start, pauseStartIntent);
        builder.setCustomContentView(remoteViews);
        builder.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT));

        return builder.build();
    }

    private Notification getDefaultNotification(String msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_128));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(msg);
        builder.setAutoCancel(true);
        return builder.build();
    }

    public PendingIntent getDefalutIntent(int flags){
        return PendingIntent.getBroadcast(this, 1, new Intent(), flags);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDownloading){
                FileDownloader.getImpl().pause(mTaskId);
            }else {
                startDownload();
            }
            mDownloading = !mDownloading;
        }
    }
}
