package com.zxy.tracee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zxy.tracee.R;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.util.BitmapTransform;
import com.zxy.tracee.util.CommonUtils;
import com.zxy.tracee.util.ImageLoadUtil;
import com.zxy.tracee.widget.TimelineView;

import java.util.List;

/**
 * Created by zxy on 16/3/19.
 */
public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter {

    public void setmDiaryList(List<Diary> mDiaryList) {
        this.mDiaryList = mDiaryList;
    }

    private List<Diary> mDiaryList;

    TimelineItemClick timelineItemClick;

    private Context mContext;

    public enum ITEM_TYPE {
        ITEM_TYPE_FIRST,
        ITEM_TYPE_LAST,
        ITEM_TYPE_NORMAL_ONE,
        ITEM_TYPE_NORMAL_TWO,
        ITEM_TYPE_NORMAL_THREE,
        ITEM_TYPE_NORMAL_FOUR,
        ITEM_TYPE_NORMAL_FIVE
    }

    public TimelineRecyclerViewAdapter(List<Diary> mDiaryList) {
        this.mDiaryList = mDiaryList;
    }
    public TimelineRecyclerViewAdapter(Context context,List<Diary> mDiaryList) {
        this.mDiaryList = mDiaryList;
        this.mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);

        return new TimelineViewHolder(view, viewType);
    }

    public void setTimelineItemClick(TimelineItemClick listener) {
        this.timelineItemClick = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TimelineViewHolder timelineViewHolder = (TimelineViewHolder) holder;

        Diary diary = mDiaryList.get(position);
        timelineViewHolder.dateText.setText(CommonUtils.getMnDString(diary.getDate()));

        timelineViewHolder.content.setText(diary.getContent());
        timelineViewHolder.title.setText(diary.getTitle());
        String imagePath = diary.getPicPath();

        if (null != imagePath) {
            List<String> picPaths = CommonUtils.parsePicString(imagePath);
            Picasso.with(mContext).load("file:"+picPaths.get(0)).resize(500, 500).skipMemoryCache().centerCrop().config(Bitmap.Config.RGB_565).
            into(timelineViewHolder.imageView);

        }
//        timelineViewHolder.imageView.setImageBitmap(ImageLoadUtil.cutBitmap("/storage/emulated/0/DCIM/Camera/20151017_154318_HDR.jpg", 400, 400, 200));
        View timeLineItemView = timelineViewHolder.itemView;

        timeLineItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timelineItemClick.onTimelineClick(view, position);
            }
        });


    }



    public interface TimelineItemClick {

        void onTimelineClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return mDiaryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDiaryList.size() == 1) {
            return 999;
        }
        int lastPostion = mDiaryList.size() - 1;
        if (position == 0) {
            return ITEM_TYPE.ITEM_TYPE_FIRST.ordinal();
        } else if (position == lastPostion) {
            return ITEM_TYPE.ITEM_TYPE_LAST.ordinal();
        } else {
            switch (position % 5) {
                case 0:
                    return ITEM_TYPE.ITEM_TYPE_NORMAL_ONE.ordinal();
                case 1:
                    return ITEM_TYPE.ITEM_TYPE_NORMAL_TWO.ordinal();
                case 2:
                    return ITEM_TYPE.ITEM_TYPE_NORMAL_THREE.ordinal();
                case 3:
                    return ITEM_TYPE.ITEM_TYPE_NORMAL_FOUR.ordinal();
                default:
                    return ITEM_TYPE.ITEM_TYPE_NORMAL_FIVE.ordinal();
            }
        }
    }

    private static class TimelineViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView content;
        private ImageView imageView;
        private TextView dateText;
        public TimelineViewHolder(View itemView, int type) {
            super(itemView);
            TimelineView timelineView = (TimelineView) itemView.findViewById(R.id.timeline_item_left);
            if (type == 999) {
                timelineView.setIsVisible(false);
                timelineView.setIsFirstItem(true);
                timelineView.setmPosition(6);
            }
            if (type == ITEM_TYPE.ITEM_TYPE_FIRST.ordinal()) {
                timelineView.setIsFirstItem(true);
                timelineView.setmPosition(6);
            } else if (type == ITEM_TYPE.ITEM_TYPE_LAST.ordinal()) {
                timelineView.setIsLastItem(true);
                timelineView.setmPosition(6);
            } else if (type == ITEM_TYPE.ITEM_TYPE_NORMAL_ONE.ordinal()) {
                timelineView.setmPosition(2);
            } else if (type == ITEM_TYPE.ITEM_TYPE_NORMAL_TWO.ordinal()) {
                timelineView.setmPosition(3);
            } else if (type == ITEM_TYPE.ITEM_TYPE_NORMAL_THREE.ordinal()) {
                timelineView.setmPosition(4);
            } else if (type == ITEM_TYPE.ITEM_TYPE_NORMAL_FOUR.ordinal()) {
                timelineView.setmPosition(5);
            } else {
                timelineView.setmPosition(6);

            }
            this.dateText = (TextView) itemView.findViewById(R.id.date_text);
            this.imageView = (ImageView) itemView.findViewById(R.id.timeline_item_image);
            this.title = (TextView) itemView.findViewById(R.id.timeline_item_title);
            this.content = (TextView) itemView.findViewById(R.id.timeline_item_content);

        }
    }

}
