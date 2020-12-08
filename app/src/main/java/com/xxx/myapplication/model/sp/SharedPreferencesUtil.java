package com.xxx.myapplication.model.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.xxx.myapplication.base.App;

public class SharedPreferencesUtil {

    private static final Object SYC = new Object();

    private static SharedPreferencesUtil sharedPreferencesUtils;

    private SharedPreferences sharedPreferences;

    private SharedPreferencesUtil() {
        sharedPreferences = App.getContext().getSharedPreferences("test", Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtils == null) {
            synchronized (SYC) {
                if (sharedPreferencesUtils == null) {
                    sharedPreferencesUtils = new SharedPreferencesUtil();
                }
            }
        }
        return sharedPreferencesUtils;
    }


    public void saveString(String tag, String content) {
        if (tag != null && content != null) {
            sharedPreferences.edit().putString(tag, content).apply();
        }
    }

    public void saveInt(String tag, Integer content) {
        if (tag != null && content != null) {
            sharedPreferences.edit().putInt(tag, content).apply();
        }
    }


    public String getString(String tag) {
        String data = "";
        if (tag != null) {
            data = sharedPreferences.getString(tag, "");
        }
        return data;
    }

    public int getInt(String tag) {
        int data = 0;
        if (tag != null) {
            data = sharedPreferences.getInt(tag, 0);
        }
        return data;
    }

    /**
     * 清除
     */
    public void cleanAll() {
        sharedPreferences.edit().clear().apply();
    }
}