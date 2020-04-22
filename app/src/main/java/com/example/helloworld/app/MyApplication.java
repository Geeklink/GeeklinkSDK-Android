package com.example.helloworld.app;

import android.app.Application;

import com.geeklink.smartpisdk.api.ApiManager;

public class MyApplication extends Application {
    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApiManager.getInstance().init(this,"875da62ac55ff12a","d4f7b957875da62ac55ff12aeba6e44f");
    }
}
