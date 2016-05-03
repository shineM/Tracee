package com.zxy.tracee.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by zxy on 16/4/30.
 * 申请权限帮助类
 */
public class PermissonUtil {
    private Service service;
    private Activity activity;
    private PermissionOperation permissionOperation;

    public PermissonUtil(Activity activity) {
        this.activity = activity;
    }

    public PermissonUtil(Service service) {
        this.service = service;
    }

    public interface PermissionOperation {
        void doIfGranted(int requestCode);
    }

    public void setPermissionOperation(PermissionOperation permissionOperation) {
        this.permissionOperation = permissionOperation;
    }

    public void checkPermission(String permissionType, int requestCode) {
        int storagePermission = ContextCompat.checkSelfPermission(activity, permissionType);
        String[] reqPermissonList = {permissionType};
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, reqPermissonList, requestCode);
        } else {
            permissionOperation.doIfGranted(requestCode);
        }
    }

    public void onRequestResultAction(int requestCode, String[] permissions, int[] grantResults, int[] requestCodeList) {
        for (int i = 0; i < requestCodeList.length; i++) {
            if (requestCodeList[i] == requestCode) {
                for (int j = 0; j < permissions.length; i++) {
                    if (grantResults[j] == PackageManager.PERMISSION_GRANTED) {
                        permissionOperation.doIfGranted(requestCode);
                        return;
                    } else if (grantResults[j] == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
            }
        }
    }
}
