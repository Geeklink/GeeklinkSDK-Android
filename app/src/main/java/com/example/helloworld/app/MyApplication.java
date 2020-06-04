package com.example.helloworld.app;

import android.app.Application;

import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.api.DisinfectionLampApiManager;
import com.geeklink.smartpisdk.api.SmartPiApiManager;

public class MyApplication extends Application {
    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化公用接口，传入在开发平台注册的appId和secretKey
        ApiManager.getInstance().initManagerWithAppId(this,"875da62ac55ff12a","d4f7b957875da62ac55ff12aeba6e44f");
        //初始化小Pi SDK接口，集成小Pi功能需要
        SmartPiApiManager.getInstance().initManager(this);
        //初始化消毒灯SDK接口，集成消毒灯功能需要
        DisinfectionLampApiManager.getInstance().initManager(this);
    }
}
