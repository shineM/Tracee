package com.zxy.tracee.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;


/**
 * Created by zxy on 16/4/26.
 */
public class AnimationUtil {
    private static int color = Color.BLACK;
    private static int startRadius = 0;
    private static int endRadius = 10;

    @TargetApi(21)
    public static void setAnimation(Activity activity, View view) {

        ReturnToFab returnToFab = new ReturnToFab(2, endRadius, color);
        activity.getWindow().setSharedElementReturnTransition(returnToFab);
    }
}
