package com.allever.sticker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.allever.sticker.R;

import java.util.List;

/**
 *
 * @author Allever
 * @date 18/2/11
 */

public class StickerDetailRecyclerAdapter extends RecyclerView.Adapter<StickerDetailRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<String> mPathList;
    public StickerDetailRecyclerAdapter(Context context, List<String> pathList){
        mContext = context;
        mPathList = pathList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sticker_detail,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(mContext).load(mPathList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.id_item_sticker_detail_iv);
        }
    }
}
