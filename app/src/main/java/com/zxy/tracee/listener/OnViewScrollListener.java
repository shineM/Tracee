package com.zxy.tracee.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by zxy on 16/3/23.
 */
public abstract class OnViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int ON_HIDE_HEIGHT = 50;

    private boolean isToolbarVisible = true;

    private int mScrolledDistance = 0;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisibleItemPosition == 0) {
            if (!isToolbarVisible) {
                showToolbar();
                isToolbarVisible = true;
            }
        } else if (dy<0) {
                showToolbar();
        }else {
            hideToolbar();
        }
    }

    protected abstract void hideToolbar();

    protected abstract void showToolbar();
}
