package com.zxy.tracee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zxy.tracee.R;
import com.zxy.tracee.ui.AddDiaryActivity;
import com.zxy.tracee.util.OnImageClick;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zxy on 16/3/27.
 */
public class SelectPhotoViewAdapter extends RecyclerView.Adapter {
    private List<String> mList;

    private OnCancelButtonClick onCancelButtonClick;
    private OnImageClick onImageClick;
    private Context mContext;
    private int width;

    public SelectPhotoViewAdapter(Context context, int screenWidth, List<String> list) {
        this.mList = list;
        this.mContext = context;
        this.width = screenWidth;
    }

    public void setOnImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    public interface OnCancelButtonClick {
        void onCancelClick(View view, int position);
    }

    public void setOnCancelButtonClick(OnCancelButtonClick click) {
        this.onCancelButtonClick = click;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_photo_item, parent, false);
        PhotoViewHolder viewHolder = new PhotoViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final PhotoViewHolder viewHolder = (PhotoViewHolder) holder;
        Picasso.with(mContext).load("file:" + mList.get(position)).resize(width / 4, width / 4).centerCrop().into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick.onClick(mList.get(position), viewHolder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageButton imageButton;

        public PhotoViewHolder(View view, int type) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.photo_item);
            this.imageButton = (ImageButton) view.findViewById(R.id.select_cancel_btn);
            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            delete(getAdapterPosition());
        }
    }

    public void setmList(List<String> list) {
        mList.clear();
        mList.addAll(list);
    }

    public List<String> getmList() {
        return mList;
    }

    public void add(String newPictrue) {
        mList.add(newPictrue);
        notifyDataSetChanged();
    }

    private void delete(int adapterPosition) {
        mList.remove(adapterPosition);
        if (mList.size() == 0) {
            EventBus.getDefault().post(AddDiaryActivity.GALLERY_EMPTY_EVENT);
        }
        notifyItemRemoved(adapterPosition);

    }
}
