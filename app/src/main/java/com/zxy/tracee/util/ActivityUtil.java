package com.zxy.tracee.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.zxy.tracee.ui.AddDiaryActivity;
import com.zxy.tracee.ui.HDImageActivity;

/**
 * Created by zxy on 16/4/24.
 */
public class ActivityUtil {
    public static final String IMAGE_PATH = "image_path";

    public static void startHDImageActivity(Context context,Activity activity,String imagePath, View view) {
        Intent intent = new Intent(activity, HDImageActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, HDImageActivity.TRANSIT_PIC);
        intent.putExtra(IMAGE_PATH, imagePath);
        try {
            ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            activity.startActivity(intent);

        }

    }
//
}
