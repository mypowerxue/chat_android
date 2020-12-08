package com.xxx.myapplication.model.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {

    //授权请求码
    private static final int REQUEST_PERMISSION_CODE = 0;

    @SuppressLint("InlinedApi")
    public static final String READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE; //写入权限
    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;   //读取权限
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;  //相机权限
    public static final String RECORD_PERMISSION  = Manifest.permission.RECORD_AUDIO;   //录音权限
    /**
     * 检查权限
     */
    public static boolean checkPermission(Activity activity, String... params) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        if (activity != null) {
            for (String requestPermission : params) {
                if (ActivityCompat.checkSelfPermission(activity, requestPermission) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(activity, params, REQUEST_PERMISSION_CODE);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 回调方法
     */
    public static boolean onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == -1) {
                return false;
            }
        }
        return true;
    }
}