package com.example.helloworld.ui.timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.utils.DeviceUtil;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.data.DBRCKeyType;
import com.geeklink.smartpisdk.data.GlobalData;
import com.geeklink.smartpisdk.listener.OnGetIRDataListener;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceListener;
import com.gl.AcStateInfo;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;
import com.gl.SmartPiTimerAction;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TimerActionSetActivity extends AppCompatActivity implements OnGetSubDeviceListener, OnGetIRDataListener {

    private String md5 = "";
    private int subId;

    private Context context;


    private List<SubDevInfo> mSubDeviceList = new ArrayList<>();
    private CommonAdapter<SubDevInfo> adapter;
    private RecyclerView recyclerView;

    private static final String TAG = "SubDeviceListActivity";
    private int keyId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_timer_action_set);

        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
//        ApiManager.getInstance().setOnGetIRDataListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<SubDevInfo>(context,R.layout.item_sub_device,mSubDeviceList) {
            @Override
            public void convert(ViewHolder holder, SubDevInfo subDevInfo, int position) {
                if(subDevInfo.mMainType == DeviceMainType.DATABASE){
                    holder.setText(R.id.stateTv," subId : " +subDevInfo.mSubId + "  fileId : " + subDevInfo.mFileId);
                }else{
                    holder.setText(R.id.stateTv," subId : " +subDevInfo.mSubId + "    " + subDevInfo.mKeyIdList.size() + context.getString(R.string.text_keys_num));
                }
                holder.setText(R.id.nameTv, DeviceUtil.getDeviceType(context,subDevInfo.mMainType,subDevInfo.mSubType));
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                subId = mSubDeviceList.get(position).mSubId;
                if(mSubDeviceList.get(position).mMainType == DeviceMainType.DATABASE) {
                        getDeviceIrCode(mSubDeviceList.get(position));
                }else{
                    if(mSubDeviceList.get(position).mKeyIdList.size() > 0){
                        getDeviceIrCode(mSubDeviceList.get(position));
                    }else{
                        Toast.makeText(context, context.getString(R.string.text_please_add_key_first), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }));

    }

    private void getDeviceIrCode(SubDevInfo subDevInfo) {
        AcStateInfo acStateInfo = null;
        SmartPiTimerAction timerAction = null;
        if(subDevInfo.mMainType == DeviceMainType.DATABASE) {
            switch (DatabaseType.values()[subDevInfo.mSubType]){
                case AC:
                    acStateInfo = ApiManager.getInstance().getACStateInfoWithStateValue(subDevInfo.mState);
                    timerAction = new SmartPiTimerAction(subId,ApiManager.getInstance().getStateValueWithACState(acStateInfo),0,"");
                break;
                case TV:
                    keyId = DBRCKeyType.TV_POWER.getKeyId();

                    timerAction = new SmartPiTimerAction(subId,String.valueOf(keyId),0,"");
                    break;
                case STB:
                    keyId = DBRCKeyType.STB_WAIT.getKeyId();
                    timerAction = new SmartPiTimerAction(subId,String.valueOf(keyId),0,"");
                    break;
                case IPTV:
                default:
                    keyId = DBRCKeyType.IPTV_POWER.getKeyId();
                    timerAction = new SmartPiTimerAction(subId,String.valueOf(keyId),0,"");
                    break;
            }
        }else{
            keyId = subDevInfo.mKeyIdList.get(0);
            timerAction = new SmartPiTimerAction(subId,String.valueOf(keyId),0,"");
        }

        GlobalData.smartPiTimerFull.mActionList.add(timerAction);
        setResult(RESULT_OK);
        finish();

//        ApiManager.getInstance().getIRData(subDevInfo, acStateInfo,keyId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSubDevice();
    }

    /**
     *  获取主机设备下的子设备
     */
    private void getSubDevice() {
        ApiManager.getInstance().getSubDeviceListWithMd5(md5.toLowerCase());
        ApiManager.getInstance().setGetSubDeviceListener(this);
    }


    @Override
    public void onGetSubDevice(StateType state, String md5, ArrayList<SubDevInfo> subInfoList) {
        if(state == StateType.OK){
            mSubDeviceList.clear();
//            for (SubDevInfo subDevInfo : subInfoList){
//                if(subDevInfo.mMainType == DeviceMainType.DATABASE){
//                    mSubDeviceList.add(subDevInfo);
//                }
//            }
            mSubDeviceList.addAll(subInfoList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onIRData(StateType state, String value, String irData) {
//        SmartPiTimerAction timerAction = new SmartPiTimerAction(subId,value,0,irData);
//        GlobalData.smartPiTimerFull.mActionList.add(timerAction);
//        setResult(RESULT_OK);
//        finish();
    }
}
