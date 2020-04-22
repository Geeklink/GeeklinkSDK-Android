package com.example.helloworld.ui.cotrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.data.DBRCKeyType;
import com.geeklink.smartpisdk.listener.OnControlDeviceListener;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceStateListener;
import com.gl.AcStateInfo;
import com.gl.CarrierType;
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
    private DeviceMainType mainType;
    private int subType = 0;
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
        mainType = DeviceMainType.values()[intent.getIntExtra("mainType", 0)];
        subType = intent.getIntExtra("subType", 0);
        fileId = intent.getIntExtra("fileId", 0);

        subDevInfo = new SubDevInfo(subId, DeviceMainType.DATABASE, subType, 0, fileId, CarrierType.CARRIER_38, new ArrayList<Integer>(), md5, "");
        acStateInfo = new AcStateInfo(true,1,26,0,1);//默认一个空调状态，开-制冷-温度26-风速0-风向1

        ApiManager.getInstance().setOnControlDeviceListener(this);
        ApiManager.getInstance().setOnGetSubDeviceStateListener(this);
        ApiManager.getInstance().getSubDeviceStateInfo(md5.toLowerCase(), subId);

        setupView(acStateInfo);
    }

    @Override
    public void onSubDeviceState(StateType state, SubStateInfo subStateInfo) {
        if (state == StateType.OK) {
            if (subStateInfo.mStateValue != null
            && !subStateInfo.mStateValue.equals("")) {
                subDevInfo.mState = subStateInfo.mStateValue;
                acStateInfo = ApiManager.getInstance().getACStateInfoWithStateValue(subStateInfo.mStateValue);
                setupView(acStateInfo);
            }
        }
    }

    private void setupView(AcStateInfo acStateInfo) {
        if(acStateInfo == null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (acStateInfo.mPower) {
            sb.append("电源：开");
        } else {
            sb.append("电源：关");
        }
        sb.append(" ")
                .append("温度：")
                .append(acStateInfo.mTemp)
                .append("℃  ");
        if (acStateInfo.mDir == 1) {
            sb.append("风向：风向1");
        } else if (acStateInfo.mDir == 2) {
            sb.append("风向：风向2");
        } else if (acStateInfo.mDir == 3) {
            sb.append("风向：风向3");
        } else if (acStateInfo.mDir == 4) {
            sb.append("风向：风向4");
        } else {
            sb.append("风向：扫风");
        }

        sb.append(" ");
        if (acStateInfo.mSpeed == 1) {
            sb.append("风速：低");
        } else if (acStateInfo.mDir == 2) {
            sb.append("风速：中");
        } else if (acStateInfo.mDir == 3) {
            sb.append("风速：高");
        } else {
            sb.append("风速：自动");
        }

        sb.append(" ");
        if (acStateInfo.mMode == 1) {
            sb.append("模式：制冷");
        } else if (acStateInfo.mDir == 2) {
            sb.append("模式：除湿");
        } else if (acStateInfo.mDir == 3) {
            sb.append("模式：送风");
        } else if (acStateInfo.mDir == 4) {
            sb.append("模式：制热");
        } else {
            sb.append("模式：自动");
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
        ApiManager.getInstance().controlSubDeviceKeyWithMd5(md5, subDevInfo, acStateInfo, keyId);

    }

    @Override
    public void onCotrolDevice(StateType state, String md5, int deviceSubId) {
        if (state == StateType.OK) {
            Toast.makeText(context, "控制成功！", Toast.LENGTH_SHORT).show();
        }
    }
}
