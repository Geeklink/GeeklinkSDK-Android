package com.example.helloworld.ui.cotrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld.R;
import com.geeklink.smartpisdk.api.SmartPiApiManager;
import com.geeklink.smartpisdk.data.DBRCKeyType;
import com.geeklink.smartpisdk.listener.OnControlDeviceListener;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceStateListener;
import com.gl.AcStateInfo;
import com.gl.CarrierType;
import com.gl.DatabaseDevType;
import com.gl.DbAirKeyType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.gl.SubStateInfo;

import java.util.ArrayList;

public class DbACDevControlActivity extends AppCompatActivity implements OnGetSubDeviceStateListener, View.OnClickListener, OnControlDeviceListener {

    private Context context;
    private String md5 = "";
    private int subId;
    private int fileId;
    private LinearLayout powerLayout;
    private LinearLayout modeLayout;
    private LinearLayout dirLayout;
    private LinearLayout speedLayout;
    private LinearLayout temPlusLayout;
    private LinearLayout temMiusLayout;
    private TextView stateTv;

    private AcStateInfo acStateInfo;
    private SubDevInfo subDevInfo;

    private static final String TAG = "AcDeviceActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sub_device_detail);
        stateTv = findViewById(R.id.stateTv);
        powerLayout = findViewById(R.id.powerLayout);
        powerLayout.setOnClickListener(this);
        modeLayout = findViewById(R.id.modeLayout);
        modeLayout.setOnClickListener(this);
        dirLayout = findViewById(R.id.dirLayout);
        dirLayout.setOnClickListener(this);
        speedLayout = findViewById(R.id.speedLayout);
        speedLayout.setOnClickListener(this);
        temPlusLayout = findViewById(R.id.temPlusLayout);
        temPlusLayout.setOnClickListener(this);
        temMiusLayout = findViewById(R.id.temMinusLayout);
        temMiusLayout.setOnClickListener(this);

        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId", 0);
        fileId = intent.getIntExtra("fileId", 0);
        subDevInfo = new SubDevInfo(subId, DeviceMainType.DATABASE_DEV, DatabaseDevType.AC,0,0,fileId, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");
        acStateInfo = new AcStateInfo(true,1,26,0,1);//默认一个空调状态，开-制冷-温度26-风速0-风向1

        setListener();

        //获取空调设备状态
        SmartPiApiManager.getInstance().getSubDeviceStateInfo(md5.toLowerCase(), subId);

        setupView(acStateInfo);
    }

    private void setListener() {
        //控制回复
        SmartPiApiManager.getInstance().setOnControlDeviceListener(this);
        //获取设备状态回复
        SmartPiApiManager.getInstance().setOnGetSubDeviceStateListener(this);
    }

    @Override
    public void onSubDeviceState(StateType state, SubStateInfo subStateInfo) {
        if (state == StateType.OK) {
            if (subStateInfo.mStateValue != null
            && !subStateInfo.mStateValue.equals("")) {
                subDevInfo.mState = subStateInfo.mStateValue;
                acStateInfo = SmartPiApiManager.getInstance().getACStateInfoWithStateValue(subStateInfo.mStateValue);
                setupView(acStateInfo);
            }
        }
    }

    private void setupView(AcStateInfo acStateInfo) {
        if(acStateInfo == null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.text_power))
                .append(":");
        if (acStateInfo.mPower) {
            sb.append(context.getString(R.string.text_on));
        } else {
            sb.append(context.getString(R.string.text_off));
        }
        sb.append(" ").append(context.getString(R.string.text_temp))
                .append(":")
                .append(acStateInfo.mTemp)
                .append("℃  ");
        sb.append(" ").append(context.getString(R.string.text_wind_dir))
                .append(":");
        if (acStateInfo.mDir == 1) {
            sb.append("1");
        } else if (acStateInfo.mDir == 2) {
            sb.append("2");
        } else if (acStateInfo.mDir == 3) {
            sb.append("3");
        } else if (acStateInfo.mDir == 4) {
            sb.append("4");
        } else {
            sb.append(context.getString(R.string.text_wind_dir_radom));
        }

        sb.append(" ").append(context.getString(R.string.text_wind_speed)).append(":");
        if (acStateInfo.mSpeed == 1) {
            sb.append(context.getString(R.string.text_low));
        } else if (acStateInfo.mDir == 2) {
            sb.append(context.getString(R.string.text_mid));
        } else if (acStateInfo.mDir == 3) {
            sb.append(context.getString(R.string.text_high));
        } else {
            sb.append(context.getString(R.string.text_auto));
        }

        sb.append(" ").append(context.getString(R.string.text_mode)).append(":");
        if (acStateInfo.mMode == 1) {
            sb.append(context.getString(R.string.text_cold));
        } else if (acStateInfo.mDir == 2) {
            sb.append(context.getString(R.string.text_dry));
        } else if (acStateInfo.mDir == 3) {
            sb.append(context.getString(R.string.text_cool_wind));
        } else if (acStateInfo.mDir == 4) {
            sb.append(context.getString(R.string.text_heat));
        } else {
            sb.append(context.getString(R.string.text_auto));
        }
        stateTv.setText(sb.toString());
    }

    @Override
    public void onClick(View v) {
        int keyId = DbAirKeyType.AIR_SWITCH.ordinal();
        switch (v.getId()){
            case R.id.powerLayout:
                acStateInfo.mPower = !acStateInfo.mPower;
//                keyId = DbAirKeyType.AIR_SWITCH.ordinal();
                keyId = DBRCKeyType.AC_POWER.getKeyId();
                break;
            case R.id.modeLayout:
                if(acStateInfo.mMode == 4){
                    acStateInfo.mMode = 0;
                }else {
                    acStateInfo.mMode ++;
                }
//                keyId = DbAirKeyType.AIR_MODE.ordinal();
                keyId = DBRCKeyType.AC_MODE.getKeyId();
                break;
            case R.id.dirLayout:
                if(acStateInfo.mDir == 4){
                    acStateInfo.mDir= 0;
                }else {
                    acStateInfo.mDir ++;
                }
//                keyId = DbAirKeyType.AIR_WIN_DIR.ordinal();
                keyId = DBRCKeyType.AC_DIR.getKeyId();
                break;
            case R.id.speedLayout:
                if(acStateInfo.mSpeed == 3){
                    acStateInfo.mSpeed= 0;
                }else {
                    acStateInfo.mSpeed ++;
                }
//                keyId = DbAirKeyType.AIR_WIN_SPEED.ordinal();
                keyId = DBRCKeyType.AC_SPEED.getKeyId();
                break;
            case R.id.temPlusLayout:
                if(acStateInfo.mTemp >= 30){
                    return;
                }
                acStateInfo.mTemp ++;
//                keyId = DbAirKeyType.AIR_TEMP_PLUS.ordinal();
                keyId = DBRCKeyType.AC_TEM.getKeyId();
                break;
            case R.id.temMinusLayout:
                if(acStateInfo.mTemp <= 16){
                    return;
                }
                acStateInfo.mTemp --;
//                keyId = DbAirKeyType.AIR_TEMP_MINUS.ordinal();
                keyId = DBRCKeyType.AC_TEM.getKeyId();
                break;
        }

        setupView(acStateInfo);
        //控制码库子设备
        SmartPiApiManager.getInstance().controlSubDeviceKeyWithMd5(md5, subDevInfo, acStateInfo, keyId);

    }

    @Override
    public void onCotrolDevice(StateType state, String md5, int deviceSubId) {
        if (state == StateType.OK) {
            Toast.makeText(context, context.getString(R.string.text_operate_successed), Toast.LENGTH_SHORT).show();
        }
    }
}
