package com.example.helloworld.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.ui.timer.TimerListActivity;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.data.GlobalData;
import com.geeklink.smartpisdk.listener.OnDeviceStateChangedListener;
import com.geeklink.smartpisdk.listener.OnDeviceTimeZoneListener;
import com.geeklink.smartpisdk.listener.OnDeviceUpgradeListener;
import com.geeklink.smartpisdk.listener.OnSetDeviceListener;
import com.geeklink.smartpisdk.utils.DevTimeZoneCalculateRunnable;
import com.gl.ActionFullType;
import com.gl.DevConnectState;
import com.gl.DiscoverDeviceInfo;
import com.gl.GlDevStateInfo;
import com.gl.StateType;
import com.gl.TimezoneAction;
import com.google.gson.Gson;

public class MainDeviceDetailActivity extends AppCompatActivity implements View.OnClickListener,
        OnSetDeviceListener , OnDeviceUpgradeListener , OnDeviceStateChangedListener , OnDeviceTimeZoneListener {

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
        firewareUpgradeBtn.setOnClickListener(this);

        localIrCodeBtn.setOnClickListener(this);

        sunDevBtn = findViewById(R.id.sunDevBtn);
        sunDevBtn.setOnClickListener(this);

        timezoneBtn.setOnClickListener(this);
        timerListBtn.setOnClickListener(this);

        delBtn = findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        nameTv.setText(md5);

        ApiManager.getInstance().setOnDeviceStateChangedListener(this);
        ApiManager.getInstance().setOnDeviceTimeZoneListener(this);


        //获取时区
        ApiManager.getInstance().toDeviceTimeZone(md5, TimezoneAction.TIMEZONE_ACTION_GET,1);

        setupView();
    }

    private synchronized void setupView(){
        //获取设备状态
        glDevStateInfo = ApiManager.getInstance().getDeviceStateInfo(md5.toLowerCase(),subId);
//        GlobalData.CLOUD_IR_2019_CTRL_MAC = glDevStateInfo.mMac;

        if (glDevStateInfo.mState == DevConnectState.LOCAL){
            stateTv.setText("本地在线");
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
            stateTv.setText("远程在线");
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
            stateTv.setText("离线");
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
                ApiManager.getInstance().setSetDeviceListener(this);
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
                if(hasNewerVersion(glDevStateInfo.mCurVer,glDevStateInfo.mLatestVer)){
                    ApiManager.getInstance().upgradeDeviceWithMd5(md5);
                    ApiManager.getInstance().setDeviceUpgradeListener(this);
                }else{
                    Toast.makeText(context, "已是最新固件，无需更新。", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "删除成功。", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDeviceUpgrade(StateType state, String md5, String action) {
        if(state == StateType.OK
         && action.equals("update")){
            new AlertDialog.Builder(context).setTitle("提示")
                    .setMessage("设备升级大概需要1分钟，请勿断电")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    private  boolean hasNewerVersion(String version1, String version2) {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version1)) {
            return false;
        }
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        for (int i = 0; i < Math.max(v1.length, v2.length); i++) {
            int num1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
            int num2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;
            if (num1 < num2) {
                return true;
            }
        }
        return false;
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
