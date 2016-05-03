package com.zxy.tracee.util;

import android.content.Context;
import android.view.WindowManager;

import net.tsz.afinal.core.Arrays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by zxy on 16/3/31.
 */
public class CommonUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String getMnDString(Date date) {
        String monthAndDay = new SimpleDateFormat("MMdd").format(date);
        return changeNumToMonth(monthAndDay)+" "+monthAndDay.substring(2,4);
    }

    //转换数字为月份缩写
    public static String changeNumToMonth(String date) {
        Map<String, String> months = new HashMap<>();
        months.put("01", "Jan");
        months.put("02", "Feb");
        months.put("03", "Mar");
        months.put("04", "Apr");
        months.put("05", "May");
        months.put("06", "Jun");
        months.put("07", "Jul");
        months.put("08", "Aug");
        months.put("09", "Sep");
        months.put("10", "Oct");
        months.put("11", "Nov");
        months.put("12", "Dec");

        if (date != null) {
            String monthNum = date.substring(0, 2);
            return months.get(monthNum);
        } else {
            return "";
        }
    }
    public static List<String> parsePicString(String imagePath) {
        if (imagePath==null||imagePath.equals("")){
            return null;
        }
        String[] picPaths = imagePath.split("\\|");
        List<String> list= new ArrayList<>();
        list.addAll(Arrays.asList(picPaths));
        return  list;

    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static String parsePicList(List<String> list) {
        StringBuffer picPathString = new StringBuffer();
         String[] picPaths = list.toArray(new String[list.size()]);
        System.out.println(picPaths.length);

        for (int i = 0; i < picPaths.length; i++) {
            picPathString.append(picPaths[i] + "|");
            System.out.println(picPaths[i] + "----");
        }
        System.out.println(picPathString.toString());
        return picPaths.toString();
    }

    public static String getFormatedDateString(Date date) {
        String dateString =  new SimpleDateFormat("yyyy/MM/dd").format(date);

        return dateString;
    }
}
