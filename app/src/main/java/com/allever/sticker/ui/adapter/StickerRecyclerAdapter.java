package com.allever.sticker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.allever.sticker.R;
import com.allever.sticker.bean.Sticker;
import com.bumptech.glide.Glide;

/**
 *
 * @author Allever
 * @date 2017/12/31
 */

public class StickerRecyclerAdapter extends RecyclerView.Adapter<StickerRecyclerAdapter.StickerViewHolder> {

    private Context mContext;
    private Sticker mSticker;

    public StickerRecyclerAdapter(Context context, Sticker sticker){
        mContext = context;
        mSticker = sticker;
    }
    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sticker,parent,false);
        StickerViewHolder stickerViewHolder = new StickerViewHolder(view);
        return stickerViewHolder;
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder, int position) {
        if (mSticker.getResId()!= null){
            Glide.with(mContext).load(mSticker.getResId()[position]).into(holder.imageView);
        }else {
            Glide.with(mContext).load(mSticker.getStickerPathList().get(position)).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        if (mSticker.getResId()!= null){
            return mSticker.getResId().length;
        }else {
            return mSticker.getStickerPathList().size();
        }

    }

    class StickerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public StickerViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.id_item_sticker_iv);
        }
    }
}
