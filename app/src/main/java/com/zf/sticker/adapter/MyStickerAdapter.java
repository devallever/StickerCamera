package com.zf.sticker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zf.sticker.R;
import com.zf.sticker.bean.MyStickerItem;
import com.zf.sticker.event.DeleteStickerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 *
 * @author Allever
 * @date 18/2/9
 */

public class MyStickerAdapter extends RecyclerView.Adapter<MyStickerAdapter.MyViewHolder> {
    private Context mContext;
    private List<MyStickerItem> mMyStickerItemList;
    public MyStickerAdapter(Context context, List<MyStickerItem> myStickerItemList){
        mContext = context;
        mMyStickerItemList = myStickerItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_my_sticker,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyStickerItem myStickerItem = mMyStickerItemList.get(position);
        Glide.with(mContext).load(myStickerItem.getPath()).into(holder.iv_type);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send Event
                EventBus.getDefault().post(new DeleteStickerEvent(myStickerItem.getName(),position));
            }
        });
        holder.tv_name.setText(myStickerItem.getName());
    }

    @Override
    public int getItemCount() {
        return mMyStickerItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_type;
        TextView tv_name;
        ImageView iv_delete;

        public MyViewHolder(View itemView){
            super(itemView);
            iv_type = (ImageView)itemView.findViewById(R.id.id_item_my_sticker_iv_type);
            iv_delete = (ImageView)itemView.findViewById(R.id.id_item_my_sticker_iv_delete);
            tv_name = (TextView)itemView.findViewById(R.id.id_item_my_sticker_tv_name);
        }
    }
}
