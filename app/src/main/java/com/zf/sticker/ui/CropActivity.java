package com.zf.sticker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.zf.sticker.R;
import com.zf.sticker.util.DialogUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Allever
 * @date 18/1/2
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CropActivity";

    public static final String EXTRA_SOURCE_URI = "source_uri";

    private static final int MESSAGE_CROPPING_TIMEOUT = 10000;
    private static final int CROPPING_DELAY = 10000;

    private Uri mSourceUri;

    private RectF mFrameRect = null;

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    private AlertDialog mCroppingDialog;

    private ImageView mIvSave;

    private CropImageView mCropImageView;

    private boolean mIsCropTimeOut = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_CROPPING_TIMEOUT:
                    mIsCropTimeOut = true;
                    mCroppingDialog.dismiss();
                    mIvSave.setClickable(true);
                    Toast.makeText(CropActivity.this, getResources().getString(R.string.crop_fail_tips),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    //加载剪裁控件的回调接口
    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
            Log.d(TAG, "mLoadCallback onSuccess: ");
        }

        @Override public void onError(Throwable e) {
            Log.d(TAG, "mLoadCallback onError: " + e);
        }
    };

    //剪裁图片的回调接口
    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(final Bitmap cropped) {
            //如果超时后才回调成功，则不做任何处理
            if (mIsCropTimeOut){
                return;
            }
            //成功剪裁图片后，保存剪裁后的图片
            Log.d(TAG, "onSuccess: crop success");
            mCropImageView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(CropActivity.this, mCompressFormat), mSaveCallback);
        }

        @Override public void onError(Throwable e) {
            mCroppingDialog.dismiss();
            mIvSave.setClickable(true);
        }
    };


    //保存剪裁后图片的回调接口
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
            //go to PhotoEditor
            mHandler.removeMessages(MESSAGE_CROPPING_TIMEOUT);
            mCroppingDialog.dismiss();
            Log.d(TAG, "onSuccess: save success");
            StickerEditorActivity.startSelf(CropActivity.this, outputUri);
            finish();
        }

        @Override public void onError(Throwable e) {
            mCroppingDialog.dismiss();
            mIvSave.setClickable(true);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        mSourceUri = getIntent().getParcelableExtra(EXTRA_SOURCE_URI);

        initView();

        initDialog();
    }

    private void initView(){
        //初始化剪裁控件
        mCropImageView = (CropImageView)findViewById(R.id.id_crop_image_view);
        mCropImageView.load(mSourceUri)
                .initialFrameRect(mFrameRect)
                .useThumbnail(true)
                .execute(mLoadCallback);

        //各种剪裁模式
        findViewById(R.id.id_crop_btn_free).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_1_1).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_4_3).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_3_4).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_16_9).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_9_16).setOnClickListener(this);
        findViewById(R.id.id_crop_btn_rotate).setOnClickListener(this);

        //保存、取消
        mIvSave = (ImageView) findViewById(R.id.id_crop_btn_save);
        mIvSave.setOnClickListener(this);
        findViewById(R.id.id_crop_iv_cancel).setOnClickListener(this);
    }

    private void initDialog(){
        mCroppingDialog = DialogUtil.createProgressAlertDialog(this,R.string.cropping,false);
    }

    /**
     * 创建一个用于保存剪裁图片的 Uri
     * */
    public Uri createSaveUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "sticker" + title;
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            Log.d(TAG, "createNewUri: file length = " + file.length());
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
            default:
                break;
        }
        return "png";
    }

    /**
     * 获取保存剪裁后图片的路径
     * 该图片不需要用户知道，应保存为缓存文件
     * */
    public String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = null;
        extStorageDir = new File(getExternalCacheDir().getPath());

        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/stickercameracrop");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_crop_btn_rotate:
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.id_crop_btn_free:
                mCropImageView.setCropMode(CropImageView.CropMode.FREE);
                break;
            case R.id.id_crop_btn_1_1:
                mCropImageView.setCropMode(CropImageView.CropMode.SQUARE);
                break;
            case R.id.id_crop_btn_4_3:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                break;
            case R.id.id_crop_btn_3_4:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                break;
            case R.id.id_crop_btn_16_9:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                break;
            case R.id.id_crop_btn_9_16:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                break;
            case R.id.id_crop_btn_save:
                //剪裁图片并保存剪裁后的图片，后续操作在回调接口中完成
                mCropImageView.crop(mSourceUri).execute(mCropCallback);

                //正在剪裁时，保存按钮为不可点击状态
                mIvSave.setClickable(false);
                if(!mCroppingDialog.isShowing()){
                    mCroppingDialog.show();
                }
                //发送一个超时消息，如果超时，则认为剪裁失败
                mHandler.sendEmptyMessageDelayed(MESSAGE_CROPPING_TIMEOUT, CROPPING_DELAY);
                break;
            case R.id.id_crop_iv_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    public static void startSelf(Activity context, Uri uri){
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_SOURCE_URI, uri);
        context.startActivity(intent);
    }
}
