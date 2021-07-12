package com.example.practical.core;

import android.app.Application;


public class CoreApp extends Application {
    private static CoreApp mInstance;

    public static synchronized CoreApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
