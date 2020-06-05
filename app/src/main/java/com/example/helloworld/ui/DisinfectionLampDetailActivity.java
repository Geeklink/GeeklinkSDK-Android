package com.example.helloworld.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld.R;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.utils.DeviceUtil;
import com.example.helloworld.utils.DialogUtils;
import com.example.helloworld.utils.TimeUtils;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.api.DisinfectionLampApiManager;
import com.geeklink.smartpisdk.listener.OnCtrlDisinfectionLampListener;
import com.geeklink.smartpisdk.listener.OnDeviceStateChangedListener;
import com.geeklink.smartpisdk.listener.OnDeviceTimeZoneListener;
import com.geeklink.smartpisdk.listener.OnDeviceUpgradeListener;
import com.geeklink.smartpisdk.listener.OnDisinfectionChildlockListener;
import com.geeklink.smartpisdk.listener.OnGLSingleDeviceStateGetListener;
import com.geeklink.smartpisdk.listener.OnSetDeviceListener;
import com.geeklink.smartpisdk.utils.DevTimeZoneCalculateRunnable;
import com.gl.ActionFullType;
import com.gl.ActionType;
import com.gl.DevConnectState;
import com.gl.DiscoverDeviceInfo;
import com.gl.GeeklinkDevType;
import com.gl.GlDevStateInfo;
import com.gl.StateType;
import com.gl.TimezoneAction;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DisinfectionLampDetailActivity extends AppCompatActivity implements View.OnClickListener ,
        OnSetDeviceListener, OnDeviceUpgradeListener, OnDeviceStateChangedListener, OnDeviceTimeZoneListener,
        OnCtrlDisinfectionLampListener, OnDisinfectionChildlockListener, OnGLSingleDeviceStateGetListener {
    private TextView nameTv;
    private Button ctrlBtn;
    private TextView disinfectionLampStatusTv;
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
    private RelativeLayout recordBtn;
    private RelativeLayout firewareUpgradeBtn;
    private RelativeLayout childLockBtn;
    private Switch childLockSwitch;
    private Button delBtn;

    private String md5 = "";
    private int subId;

    private Context context;
    private GlDevStateInfo glDevStateInfo;
    private DevTimeZoneCalculateRunnable calculateRunnable;

    private static final String TAG = "DisinfectionLampDetail";
    private boolean isDisinfecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_disinfection_lamp_detail);

        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId",0);

        nameTv = findViewById(R.id.nameTv);
        ctrlBtn = findViewById(R.id.ctrlBtn);
        disinfectionLampStatusTv = findViewById(R.id.disinfectionLampStatusTv);
        stateTv = findViewById(R.id.stateTv);
        firewareCurVerTv = findViewById(R.id.firewareCurVerTv);
        firewareLatestVerTv = findViewById(R.id.firewareLatestVerTv);
        timezoneTv = findViewById(R.id.timezoneTv);
        timezoneBtn = findViewById(R.id.timezoneBtn);
        timerListBtn = findViewById(R.id.timerListBtn);
        recordBtn = findViewById(R.id.recordBtn);
        childLockBtn = findViewById(R.id.childLockBtn);
        childLockSwitch = findViewById(R.id.childLockSwitch);

        macTv = findViewById(R.id.macTv);
        ipTv = findViewById(R.id.ipTv);
        firewareCurVerLayout = findViewById(R.id.firewareCurVerLayout);
        firewareLatestVerLayout = findViewById(R.id.firewareLatestVerLayout);
        macLayout = findViewById(R.id.macLayout);
        ipLayout = findViewById(R.id.ipLayout);

        ctrlBtn.setOnClickListener(this);

        firewareUpgradeBtn = findViewById(R.id.firewareUpgradeBtn);
        firewareUpgradeBtn.setOnClickListener(this);

        childLockBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);

        timezoneBtn.setOnClickListener(this);
        timerListBtn.setOnClickListener(this);

        delBtn = findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        nameTv.setText(md5);

        setListener();

        //获取时区
        ApiManager.getInstance().toDeviceTimeZone(md5, TimezoneAction.TIMEZONE_ACTION_GET,1);
        //获取消毒灯状态
        DisinfectionLampApiManager.getInstance().getDisinfectionLampState(md5);
        //获取儿童锁状态
        DisinfectionLampApiManager.getInstance().disinfectionLampChildLock(md5, ActionType.CHECK,false);
    }

    private void setListener() {
        //设置设备时区回调
        ApiManager.getInstance().setOnDeviceTimeZoneListener(this);
        //设置设备回调（增删改）
        ApiManager.getInstance().setSetDeviceListener(this);
        //设备状态改变回调（设备自动回复）
        ApiManager.getInstance().setOnDeviceStateChangedListener(this);
        //获取消毒灯设备状态回调
        DisinfectionLampApiManager.getInstance().setGLSingleDeviceStateGetListener(this);
        //消毒灯控制回调
        DisinfectionLampApiManager.getInstance().setCtrlDisinfectionLampListener(this);
        //儿童锁状态回调
        DisinfectionLampApiManager.getInstance().setDisinfectionChildlockListener(this);
    }

    private void setupView() {
        glDevStateInfo = ApiManager.getInstance().getDeviceStateInfo(md5);
        if(glDevStateInfo == null){
            return;
        }
        Log.e(TAG, "setupView:glDevStateInfo = " + new Gson().toJson(glDevStateInfo));
        if (glDevStateInfo.mState == DevConnectState.LOCAL){
            stateTv.setText(R.string.text_local_online);
            firewareCurVerLayout.setVisibility(View.VISIBLE);
            firewareLatestVerLayout.setVisibility(View.VISIBLE);
            macLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            timezoneBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.VISIBLE);
            timerListBtn.setVisibility(View.VISIBLE);
            firewareUpgradeBtn.setVisibility(View.VISIBLE);
            childLockBtn.setVisibility(View.VISIBLE);
        }else if(glDevStateInfo.mState == DevConnectState.REMOTE){
            stateTv.setText(R.string.text_remote_online);
            firewareCurVerLayout.setVisibility(View.VISIBLE);
            firewareLatestVerLayout.setVisibility(View.VISIBLE);
            macLayout.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.VISIBLE);
            timezoneBtn.setVisibility(View.VISIBLE);
            ipLayout.setVisibility(View.GONE);
            recordBtn.setVisibility(View.VISIBLE);
            timerListBtn.setVisibility(View.VISIBLE);
            firewareUpgradeBtn.setVisibility(View.VISIBLE);
            childLockBtn.setVisibility(View.VISIBLE);
        }else{
            stateTv.setText(R.string.text_offline);
            firewareCurVerLayout.setVisibility(View.GONE);
            firewareLatestVerLayout.setVisibility(View.GONE);
            macLayout.setVisibility(View.GONE);
            ipLayout.setVisibility(View.GONE);
            ipLayout.setVisibility(View.GONE);
            timezoneBtn.setVisibility(View.GONE);
            recordBtn.setVisibility(View.GONE);
            timerListBtn.setVisibility(View.GONE);
            firewareUpgradeBtn.setVisibility(View.GONE);
            childLockBtn.setVisibility(View.GONE);
        }

        switch (glDevStateInfo.mUvState){
            case ON:
                ctrlBtn.setText(R.string.text_stop_disinfection);
                isDisinfecting = true;
                disinfectionLampStatusTv.setText(R.string.text_disinfecting);
                break;
            case OFF:
                ctrlBtn.setText(R.string.text_start_disinfection);
                isDisinfecting = false;
                setUVDisinfectionOffViews(glDevStateInfo);
                break;
            default:
                ctrlBtn.setText(R.string.text_pause_disinfection);
                isDisinfecting = false;
                setUVDisinfectionOffViews(glDevStateInfo);
                break;
        }

        firewareCurVerTv.setText(glDevStateInfo.mCurVer);
        firewareLatestVerTv.setText(glDevStateInfo.mLatestVer);
        macTv.setText(glDevStateInfo.mMac);
        ipTv.setText(glDevStateInfo.mIp);
    }


    private void setUVDisinfectionOffViews(GlDevStateInfo stateInfo) {
        Log.e(TAG, "setUVDisinfectionOffViews: stateInfo.mUvLastTime = " + stateInfo.mUvLastTime);
        if (stateInfo.mUvLastTime == 0) {
            disinfectionLampStatusTv.setText(String.format(context.getString(R.string.text_latest_disinfection_time), context.getString(R.string.text_undisinfect)));
         } else if (stateInfo.mUvLastTime == 1) {
            disinfectionLampStatusTv.setText(String.format(context.getString(R.string.text_latest_disinfection_time), context.getString(R.string.text_undisinfect)));
        }
        if(stateInfo.mUvLastTime  <= 1){
//            disinfectionLampStatusTv.setVisibility(View.GONE);
          } else {
            disinfectionLampStatusTv.setVisibility(View.VISIBLE);
            String lastestDiSinfectionTime = TimeUtils.formatDateFromSeconds(String.valueOf(stateInfo.mUvLastTime));
            disinfectionLampStatusTv.setText(String.format(context.getString(R.string.text_latest_disinfection_time), lastestDiSinfectionTime));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delBtn:
                ApiManager.getInstance().deleteMainDevice(md5);
                break;
            case R.id.timerListBtn:
                Intent intent3 = new Intent(context, DisinfectionLampTimerListActivity.class);
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
                    ApiManager.getInstance().upgradeDeviceWithMd5(md5);
                    ApiManager.getInstance().setDeviceUpgradeListener(this);
                }else{
                    Toast.makeText(context, R.string.text_fireware_update_un_availeable, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ctrlBtn:
                if(isDisinfecting){
                    //停止消毒
                    DisinfectionLampApiManager.getInstance().controlDisinfectionLamp(md5,0);//0：停止消毒
                }else {
                    showCtrlDialog();
                }
                break;
            case R.id.recordBtn:
                Intent intent = new Intent(context, DisinfectionLampRecordsActivity.class);
                intent.putExtra("md5",md5.toLowerCase());
                startActivity(intent);
                break;
            case R.id.childLockBtn:
                //设置儿童锁
                DisinfectionLampApiManager.getInstance().disinfectionLampChildLock(md5, ActionType.MODIFY,!childLockSwitch.isChecked());
                break;
        }
    }

    private void showCtrlDialog() {
        List<String> actions = new ArrayList<>();
        actions.add(context.getString(R.string.text_disinfection_for_15_mins));
        actions.add(context.getString(R.string.text_disinfection_for_30_mins));
        actions.add(context.getString(R.string.text_disinfection_for_60_mins));
        DialogUtils.showItemDialog(DisinfectionLampDetailActivity.this,actions,new OnItemClickListenerImp(){
            @Override
            public void onItemClick(View view, int pos) {
                super.onItemClick(view, pos);
                int duration; //消毒15分钟，单位：秒
               switch (pos){
                   case 0:
                       duration = 15 * 60;
                       break;
                   case 1:
                       duration = 30 * 60;
                       break;
                   case 2:
                   default:
                       duration = 60 * 60;
                       break;
               }
                //开始消毒
                DisinfectionLampApiManager.getInstance().controlDisinfectionLamp(md5,duration);
            }
        });
    }


    @Override
    public void onSetDevice(StateType state, ActionFullType action, DiscoverDeviceInfo discoverDeviceInfo) {
        if(state == StateType.OK){
            Toast.makeText(context, context.getString(R.string.text_delete_successed), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("delete",true);
            setResult(RESULT_OK,intent);
            finish();
        }else{
            Toast.makeText(context, context.getString(R.string.text_delete_failed), Toast.LENGTH_SHORT).show();
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
    public void onDeviceTimeZone(StateType state, String md5, int timezone) {
        if(state == StateType.OK){
            if (calculateRunnable == null) {
                calculateRunnable = new DevTimeZoneCalculateRunnable(new Handler(), timezoneTv);
            }
            calculateRunnable.start((short) timezone);
        }
    }

    @Override
    public void OnDeviceStateChanged(String md5) {
        setupView();
    }

    @Override
    public void onCtrlResp(StateType state, String md5, int disinfectionTime) {
        if(state == StateType.OK){
            Toast.makeText(this, context.getString(R.string.text_control_successed), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, context.getString(R.string.text_control_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisinfectionChildlock(StateType state, String md5, ActionType actionType, boolean childLock) {
        if(state == StateType.OK){
            childLockSwitch.setChecked(childLock);
        }
    }

    @Override
    public void onGLSingleDeviceStateGetResp(StateType state, String md5, GlDevStateInfo glDevStateInfo) {
        Log.e(TAG, "onGLSingleDeviceStateGetResp: ");
        if(state == StateType.OK) {
            this.glDevStateInfo = glDevStateInfo;
            setupView();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //移除监听回调
//        DisinfectionLampApiManager.getInstance().removeGLSingleDeviceStateGetListener(this);
//    }
}
