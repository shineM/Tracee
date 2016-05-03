package com.zxy.tracee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zxy.tracee.R;
import com.zxy.tracee.util.ImageLoadUtil;
import com.zxy.tracee.util.OnImageClick;

import java.util.List;

/**
 * Created by zxy on 16/4/18.
 */
public class DetailImagesGridAdapter extends RecyclerView.Adapter {

    private List<String> imageList;

    private OnImageClick onImageClick;
    private Context mContext;
    private int width;
    private int rowNum;

    public DetailImagesGridAdapter(Context context, int width, List<String> list) {
        this.mContext = context;
        this.imageList = list;
        this.width = width;
    }

    public void setOnImageClick(OnImageClick click) {
        this.onImageClick = click;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_image_item, parent, false);
        return new DetailImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DetailImageViewHolder mHolder = (DetailImageViewHolder) holder;
        rowNum = getRowCount(imageList.size());
        Picasso.with(mContext).load("file:" + imageList.get(position)).resize(width / rowNum, width / rowNum).centerCrop().into(mHolder.imageView);
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imagePath = imageList.get(position);
                onImageClick.onClick(imagePath, mHolder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public int getRowCount(int size) {
        if (size >= 4) {
            return 4;
        } else {
            return size;
        }
    }

    public static class DetailImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public DetailImageViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.detail_image_item);
        }
    }

}
