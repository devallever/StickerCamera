package com.zf.sticker.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zf.sticker.R;
import com.zf.sticker.bean.StoreStickerItem;

import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Mac on 18/2/8.
 */

public class StickerStoreAdapter extends RecyclerView.Adapter<StickerStoreAdapter.MyViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private Context mContext;
    private List<StoreStickerItem> mStoreStickerItemList;
    private View mHeaderView;


    public StickerStoreAdapter(Context context, List<StoreStickerItem> storeStickerItemList){
        mContext = context;
        mStoreStickerItemList = storeStickerItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER){
            return new MyViewHolder(mHeaderView);
        }
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_sticker, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        int realPos = getRealPosition(holder);
        StoreStickerItem storeStickerItem = mStoreStickerItemList.get(realPos);
        Glide.with(mContext).load(storeStickerItem.getUrl()).into(holder.iv_sticker_type);

        if (storeStickerItem.isDownloaded()){
            Glide.with(mContext).load(R.drawable.downloaded).into(holder.iv_download_flag);
        }else {
            Glide.with(mContext).load(R.drawable.download).into(holder.iv_download_flag);

        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mStoreStickerItemList.size() : mStoreStickerItemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null){
            return TYPE_NORMAL;
        }
        if(position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_sticker_type;
        ImageView iv_download_flag;
        public MyViewHolder(View itemView){
            super(itemView);
            iv_download_flag = (ImageView)itemView.findViewById(R.id.id_item_store_iv_download_flag);
            iv_sticker_type = (ImageView)itemView.findViewById(R.id.id_item_store_iv_pic);
        }
    }
}
