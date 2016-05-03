package com.zxy.tracee.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zxy on 16/3/25.
 */
public class FileUtil {
    private Context context;

    static {
        createSDCardDir("/Tracee/photos/");
    }

    public FileUtil(Context context) {
        this.context = context;
    }

    public static boolean createSDCardDir(String name) {
        if (getSDPath() == null) {
            return false;
        } else {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                File sdcardDir = Environment.getExternalStorageDirectory();

                String newPath = sdcardDir.getPath() + name;

                File imgFile = new File(newPath);
                if (!imgFile.exists()) {

                    if (imgFile.mkdirs()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private static String getSDPath() {
        File SDdir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            SDdir = Environment.getExternalStorageDirectory();
        }
        if (SDdir != null) {
            return SDdir.toString();
        } else {
            return null;
        }
    }

    private static String getImageDirPath() {
        String path = getSDPath();
        if (path != null) {
            return path + "/Tracee/photos/";
        }
        return null;
    }

    public static File createFileByDate() {

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";
        File file = new File(getImageDirPath(), fileName);
        return file;
    }

}
