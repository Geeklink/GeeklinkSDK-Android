package com.example.helloworld.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld.R;
import com.example.helloworld.ui.timer.TimerListActivity;
import com.example.helloworld.utils.DeviceUtil;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.listener.OnDeviceStateChangedListener;
import com.geeklink.smartpisdk.listener.OnDeviceTimeZoneListener;
import com.geeklink.smartpisdk.listener.OnDeviceUpgradeListener;
import com.geeklink.smartpisdk.listener.OnSetDeviceListener;
import com.geeklink.smartpisdk.utils.DevTimeZoneCalculateRunnable;
import com.gl.ActionFullType;
import com.gl.DevConnectState;
import com.gl.DiscoverDeviceInfo;
import com.gl.GeeklinkDevType;
import com.gl.GlDevStateInfo;
import com.gl.StateType;
import com.gl.TimezoneAction;

public class MainDeviceDetailActivity extends AppCompatActivity implements View.OnClickListener,
        OnSetDeviceListener, OnDeviceUpgradeListener, OnDeviceStateChangedListener, OnDeviceTimeZoneListener {

    private TextView nameTv;
    private TextView stateTv;
    private TextView firewareCurVerTv;
    private TextView firewareLatestVerTv;
    private TextView macTv;
    private TextView ipTv;
    private TextView timezoneTv;
    private RelativeLayout timerListBtn;
    private RelativeLayout timezoneBtn;
    private RelativeLayout firewareCurVerLayout;
    private RelativeLayout firewareLatestVerLayout;
    private RelativeLayout macLayout;
    private RelativeLayout ipLayout;
    private RelativeLayout sunDevBtn;
    private RelativeLayout firewareUpgradeBtn;
    private RelativeLayout localIrCodeBtn;
    private  Button delBtn;

    private String md5 = "";
    private int subId;

    private Context context;
    private GlDevStateInfo glDevStateInfo;
    private DevTimeZoneCalculateRunnable calculateRunnable;

    private static final String TAG = "MainDeviceDetailActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_pi_device_detail);

        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId",0);

        nameTv = findViewById(R.id.nameTv);
        stateTv = findViewById(R.id.stateTv);
        firewareCurVerTv = findViewById(R.id.firewareCurVerTv);
        firewareLatestVerTv = findViewById(R.id.firewareLatestVerTv);
        timezoneTv = findViewById(R.id.timezoneTv);
        timezoneBtn = findViewById(R.id.timezoneBtn);
        timerListBtn = findViewById(R.id.timerListBtn);
        localIrCodeBtn = findViewById(R.id.localIrCodeBtn);
        macTv = findViewById(R.id.macTv);
        ipTv = findViewById(R.id.ipTv);
        firewareCurVerLayout = findViewById(R.id.firewareCurVerLayout);
        firewareLatestVerLayout = findViewById(R.id.firewareLatestVerLayout);
        macLayout = findViewById(R.id.macLayout);
        ipLayout = findViewById(R.id.ipLayout);
        firewareUpgradeBtn = findViewById(R.id.firewareUpgradeBtn);
        sunDevBtn = findViewById(R.id.sunDevBtn);
        delBtn = findViewById(R.id.delBtn);

        firewareUpgradeBtn.setOnClickListener(this);
        localIrCodeBtn.setOnClickListener(this);
        sunDevBtn.setOnClickListener(this);
        timezoneBtn.setOnClickListener(this);
        timerListBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);

        //设置回调
        setListener();

        //获取时区
        ApiManager.getInstance().toDeviceTimeZone(md5, TimezoneAction.TIMEZONE_ACTION_GET,1);

        setupView();
    }

    /**
     * 设置接口回调监听
     */
    private void setListener() {
        //设备状态改变回调（设备状态改变会自动回调）
        ApiManager.getInstance().setOnDeviceStateChangedListener(this);
        //设备时区设置回调
        ApiManager.getInstance().setOnDeviceTimeZoneListener(this);
        //设备设置回调（增删改）
        ApiManager.getInstance().setSetDeviceListener(this);
        //设备固件更新回调
        ApiManager.getInstance().setDeviceUpgradeListener(this);
    }

    private void setupView(){
        nameTv.setText(md5);
        //获取设备状态
        glDevStateInfo = ApiManager.getInstance().getDeviceStateInfo(md5.toLowerCase());
        if (glDevStateInfo.mState == DevConnectState.LOCAL){
            stateTv.setText(context.getString(R.string.text_local_online));
            firewareCurVerLayout.setVisibility(View.VISIBLE);
            firewareLatestVerLayout.setVisibility(View.VISIBLE);
            macLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            timezoneBtn.setVisibility(View.VISIBLE);
            sunDevBtn.setVisibility(View.VISIBLE);
            timerListBtn.setVisibility(View.VISIBLE);
            firewareUpgradeBtn.setVisibility(View.VISIBLE);
            localIrCodeBtn.setVisibility(View.VISIBLE);
        }else if(glDevStateInfo.mState == DevConnectState.REMOTE){
            stateTv.setText(context.getString(R.string.text_remote_online));
            firewareCurVerLayout.setVisibility(View.VISIBLE);
            firewareLatestVerLayout.setVisibility(View.VISIBLE);
            macLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            timezoneBtn.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.GONE);
            sunDevBtn.setVisibility(View.VISIBLE);
            timerListBtn.setVisibility(View.VISIBLE);
            firewareUpgradeBtn.setVisibility(View.VISIBLE);
            localIrCodeBtn.setVisibility(View.VISIBLE);
        }else{
            stateTv.setText(context.getString(R.string.text_offline));
            firewareCurVerLayout.setVisibility(View.GONE);
            firewareLatestVerLayout.setVisibility(View.GONE);
            macLayout.setVisibility(View.GONE);
            ipLayout.setVisibility(View.GONE);
            ipLayout.setVisibility(View.GONE);
            timezoneBtn.setVisibility(View.GONE);
            sunDevBtn.setVisibility(View.GONE);
            timerListBtn.setVisibility(View.GONE);
            firewareUpgradeBtn.setVisibility(View.GONE);
            localIrCodeBtn.setVisibility(View.GONE);
        }

        firewareCurVerTv.setText(glDevStateInfo.mCurVer);
        firewareLatestVerTv.setText(glDevStateInfo.mLatestVer);
        macTv.setText(glDevStateInfo.mMac);
        ipTv.setText(glDevStateInfo.mIp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delBtn:
                //删除设备
                ApiManager.getInstance().deleteMainDevice(md5);
                break;
            case R.id.timerListBtn:
                Intent intent3 = new Intent(context, TimerListActivity.class);
                intent3.putExtra("md5",md5.toLowerCase());
                startActivity(intent3);
                break;
            case R.id.timezoneBtn:
                Intent intent2 = new Intent(context, TimezoneActivity.class);
                intent2.putExtra("md5",md5.toLowerCase());
                startActivityForResult(intent2, 1101);
                break;
            case R.id.firewareUpgradeBtn:
                if(DeviceUtil.hasNewerVersion(glDevStateInfo.mCurVer,glDevStateInfo.mLatestVer)){
                    //设备固件更新
                    ApiManager.getInstance().upgradeDeviceWithMd5(md5);
                }else{
                    Toast.makeText(context, context.getString(R.string.text_fireware_update_un_availeable), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sunDevBtn:
                Intent intent = new Intent(context, SubDeviceListActivity.class);
                intent.putExtra("md5",md5);
                intent.putExtra("subId",subId);
                startActivity(intent);
                break;

            case R.id.localIrCodeBtn:
                Intent intent4 = new Intent(context, LocalIrCodeActivity.class);
                intent4.putExtra("md5",md5.toLowerCase());
                startActivity(intent4);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1101 && resultCode == RESULT_OK){

        }
    }

    @Override
    public void onSetDevice(StateType state, ActionFullType action, DiscoverDeviceInfo discoverDeviceInfo) {
        if(state == StateType.OK){
            Toast.makeText(context, context.getString(R.string.text_operate_successed), Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(context, context.getString(R.string.text_operate_failed), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDeviceUpgrade(StateType state, String md5, String action) {
        if(state == StateType.OK
         && action.equals("update")){
            new AlertDialog.Builder(context).setTitle(context.getString(R.string.text_promt))
                    .setMessage(context.getString(R.string.text_fireware_update_note))
                    .setPositiveButton(context.getString(R.string.text_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }


    @Override
    public void OnDeviceStateChanged(String md5) {
        setupView();
    }

    @Override
    public void onDeviceTimeZone(StateType state, String md5, int timezone) {
        if(state == StateType.OK){
            if (calculateRunnable == null) {
                calculateRunnable = new DevTimeZoneCalculateRunnable(new Handler(), timezoneTv);
            }
            calculateRunnable.start((short) timezone);
        }
    }
}
