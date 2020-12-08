package com.xxx.myapplication.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.xxx.myapplication.ui.activity.chat.ChatActivity;
import com.xxx.myapplication.ui.activity.chat.MainActivity;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

    public static Context getContext() {
        return context;
    }

}
