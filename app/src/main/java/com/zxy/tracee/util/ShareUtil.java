package com.zxy.tracee.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by zxy on 16/4/30.
 */
public class ShareUtil {

    public static void share(Context context, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, "分享"));
    }
}
