package com.allever.sticker.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.allever.sticker.R;

/**
 *
 * @author Allever
 * @date 18/2/23
 */

public class ApplyDialog extends BaseDialog{
    private LinearLayout mLinearChoosePic;
    private LinearLayout mLinearCamera;
    private View.OnClickListener mChooseListener;
    private View.OnClickListener mCameraListener;

    public ApplyDialog(Activity activity) {
        super(activity);
    }
    public ApplyDialog(Activity activity, View.OnClickListener chooseClickListener, View.OnClickListener cameraClickListener){
        super(activity);
        mChooseListener = chooseClickListener;
        mCameraListener = cameraClickListener;
    }

    @Override
    public void show() {
        super.show(true);
    }

    @Override
    public View getDialogView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_apply,null);
        mLinearChoosePic = (LinearLayout)view.findViewById(R.id.id_dialog_apply_ll_choose);
        mLinearCamera = (LinearLayout)view.findViewById(R.id.id_dialog_apply_ll_camera);
        mLinearChoosePic.setOnClickListener(mChooseListener);
        mLinearCamera.setOnClickListener(mCameraListener);
        return view;
    }
}
