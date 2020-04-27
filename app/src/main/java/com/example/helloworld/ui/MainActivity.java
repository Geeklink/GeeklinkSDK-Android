package com.example.helloworld.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.impl.ConfigDevResult;
import com.geeklink.smartpisdk.listener.OnDiscoverDeviceListener;
import com.geeklink.smartpisdk.listener.OnSetDeviceListener;
import com.geeklink.smartpisdk.utils.EspWifiAdminSimple;
import com.geeklink.smartpisdk.utils.SharePrefUtil;
import com.gl.ActionFullType;
import com.gl.DeviceInfo;
import com.gl.DiscoverDeviceInfo;
import com.gl.StateType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ConfigDevResult,OnDiscoverDeviceListener, OnSetDeviceListener {
    private EditText ssidEdt;
    private EditText pswEdt;
    private CommonToolbar toolbar;

    private Context context;
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final String TAG = "MainActivityDebug";
    private boolean isHasSetDev = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.title);

        ssidEdt = findViewById(R.id.ssidEdt);
        pswEdt = findViewById(R.id.pswEdt);


        toolbar.getBackIconView().setVisibility(View.INVISIBLE);
        toolbar.getBackIconView().setEnabled(false);
        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                startActivity(new Intent(context,MainDeviceListActivity.class));
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getWifi();
    }



    public void getWifi() {
        String wifiName = "";
        WifiManager wifi_service = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        String name = wifiInfo.getSSID();
        if (name.substring(0, 1).equals("\"") && name.substring(name.length() - 1).equals("\"")) {
            wifiName = name.substring(1, name.length() - 1);
        } else {
            wifiName = name;
        }
        ssidEdt.setText(wifiName);
        ssidEdt.setSelection(wifiName.length());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWifi();
                }
                break;
        }
    }


    @SuppressLint("SetTextI18n")
    public void buttonWasPressed(View view) {
        isHasSetDev = false;
        String ssid = ssidEdt.getText().toString().trim();
        String pwd = pswEdt.getText().toString().trim();

        String apBssid = new EspWifiAdminSimple(MainActivity.this).getWifiConnectedBssid();
        //初始化sdk
        ApiManager.getInstance().configWifi(MainActivity.this,apBssid, ssid, pwd , this);
        //设备配网成功后发现设备回调监听
        ApiManager.getInstance().setDiscoverDeviceListener(this);
        //设备配网成功后，自动绑定设备回调监听
        ApiManager.getInstance().setSetDeviceListener(this);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.configuring_message));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ApiManager.getInstance().stopConfigWifi();
            }
        });
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiManager.getInstance().stopConfigWifi();
            }
        });
        mProgressDialog.show();
    }


    /**
     * 发现设备回调,注：配网成功后自动绑定设备，无需手动绑定
     * @param discoverDeviceInfo
     */
    @Override
    public void onDiscoverDevice(DiscoverDeviceInfo discoverDeviceInfo) {

    }

    /**
     * 自动绑定设备回调
     * @param state
     * @param action
     * @param discoverDeviceInfo
     */
    @Override
    public void onSetDevice(StateType state, ActionFullType action, DiscoverDeviceInfo discoverDeviceInfo) {
        if(isHasSetDev){
            return;
        }
        isHasSetDev = true;
        //保存设备信息到本地，第三方可将数据保存到服务器，从服务去中获取
        //以下是模拟数据 ↓
        //1.获取之前保存的数据
        String deviceInfoListStr = SharePrefUtil.getString(context,"deviceInfoList","");
        List<DeviceInfo> deviceInfoList = new Gson().fromJson(deviceInfoListStr,new TypeToken<List<DeviceInfo>>() { }.getType());
        if(deviceInfoList == null || deviceInfoList.isEmpty()){
            deviceInfoList = new ArrayList<>();
        }
        //2.保存新添加的设备
        DeviceInfo deviceInfo = new DeviceInfo(discoverDeviceInfo.mDeviceToken,discoverDeviceInfo.mMainType,discoverDeviceInfo.mMD5.toLowerCase(),discoverDeviceInfo.mType,0);
        deviceInfoList.add(deviceInfo);
        SharePrefUtil.saveString(context,"deviceInfoList",new Gson().toJson(deviceInfoList));
    }
    /**
     * 设备配网回调
     * @param state
     */
    @Override
    public void configResult(StateType state) {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        switch (state){
            case OK:
                new AlertDialog.Builder(context).setTitle(context.getString(R.string.text_promt))
                        .setMessage(context.getString(R.string.text_config_successed))
                        .setPositiveButton(context.getString(R.string.text_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            default:
                new AlertDialog.Builder(context).setTitle(context.getString(R.string.text_promt))
                        .setMessage(context.getString(R.string.text_config_failed))
                        .setPositiveButton(context.getString(R.string.text_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
        }
    }
}
