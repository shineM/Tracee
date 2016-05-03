package com.zxy.tracee.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zxy.tracee.ui.SettingActivity;

/**
 * Created by zxy on 16/4/3.
 */
public class SettingUtil {
    public static final String PREF_NIGHT_MODE = "pref_night_mode";

    public static final String PREF_SYCN_DATA_AUTO = "pref_sycn_data_auto";

    public static void registerOnSharedPrefenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unRegisterOnSharedPrefenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static boolean shouldNightModeOn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_NIGHT_MODE, false);
    }
}
