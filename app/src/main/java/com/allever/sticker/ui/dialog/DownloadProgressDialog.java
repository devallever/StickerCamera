package com.allever.sticker.ui.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allever.sticker.R;


/**
 *
 * @author allever
 * @date 17-10-20
 */

public class DownloadProgressDialog extends  BaseDialog{
    private ProgressBar mProgressBar;
    private TextView mTvCancel;
    private TextView mTvPauseContinue;
    private TextView mTvTips;
    private boolean isPause = false;

    private View.OnClickListener mCancelClickListener;
    private View.OnClickListener mPauseStartClickListener;


    public DownloadProgressDialog(Activity activity){
        super(activity);
    }
    public DownloadProgressDialog(Activity activity, View.OnClickListener cancelClickListener, View.OnClickListener pauseStartClickListener){
        super(activity);
        mCancelClickListener = cancelClickListener;
        mPauseStartClickListener = pauseStartClickListener;
    }


    @Override
    public void show() {
        super.show(true);
    }

    @Override
    public View getDialogView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_download,null);
        mProgressBar = (ProgressBar)view.findViewById(R.id.id_dialog_download_progress_bar);
        mTvCancel = (TextView) view.findViewById(R.id.id_dialog_download_tv_cancel);
        mTvCancel.setOnClickListener(mCancelClickListener);
        mTvPauseContinue = (TextView)view.findViewById(R.id.id_dialog_download_tv_pause_continue);
        mTvPauseContinue.setOnClickListener(mPauseStartClickListener);
        mTvTips = (TextView) view.findViewById(R.id.id_dialog_download_tv_tips);
        mProgressBar.setMax(100);
        return view;
    }

    public void setProgress(int progerss, String msg){
        mProgressBar.setMax(100);
        mProgressBar.setProgress(progerss);
        mTvTips.setText(msg);
    }

    /**
     * 点击暂停继续按钮时调用*/
    public void setState(boolean isPause){
        this.isPause = isPause;
        if (isPause){
            mTvPauseContinue.setText(R.string.continues);
        }else {
            mTvPauseContinue.setText(R.string.pause);
        }
    }

    public boolean isPause(){
        return isPause;
    }

    /**
     * 点击取消按钮时调用*/
    public void cancel(){
        isPause = true;
    }




}
