package com.example.helloworld.ui.cotrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.geeklink.smartpisdk.api.SmartPiApiManager;
import com.geeklink.smartpisdk.bean.DBRCKeyInfo;
import com.geeklink.smartpisdk.listener.OnControlDeviceListener;
import com.geeklink.smartpisdk.listener.OnGetDBKeyListListener;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceStateListener;
import com.gl.CarrierType;
import com.gl.DatabaseDevType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.gl.SubStateInfo;

import java.util.ArrayList;
import java.util.List;

public class DbTvStbDevControlActivity extends AppCompatActivity implements OnGetSubDeviceStateListener, OnControlDeviceListener, OnGetDBKeyListListener {

    private Context context;
    private RecyclerView recyclerView;

    private CommonAdapter<DBRCKeyInfo> adapter;

    private List<DBRCKeyInfo> keyInfoList = new ArrayList<>();
    private String md5 = "";
    private int subId ;
    private int fileId;
    private SubDevInfo subDevInfo;

    private SubStateInfo subStateInfo;
    private DatabaseDevType databaseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_stb_dev);
        context = this;
        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId",0);
        fileId = intent.getIntExtra("fileId",0);
        databaseType = DatabaseDevType.values()[intent.getIntExtra("databaseType",0)];

        subDevInfo = new SubDevInfo(subId, DeviceMainType.DATABASE_DEV, databaseType,0,0,fileId, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<DBRCKeyInfo>(context, R.layout.item_add_sub_dev, keyInfoList) {
            @Override
            public void convert(ViewHolder holder, DBRCKeyInfo dbrcKeyInfo, int position) {
                holder.setText(R.id.nameTv,dbrcKeyInfo.getKeyName());
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                //发码控制设备
                SmartPiApiManager.getInstance().controlSubDeviceKeyWithMd5(md5,subDevInfo,null, keyInfoList.get(position).getKeyId());
            }
        }));

        setListener();

        //获取设备状态
        SmartPiApiManager.getInstance().getSubDeviceStateInfo(md5,subId);

        initData();
    }

    private void setListener() {
        //控制回调
        SmartPiApiManager.getInstance().setOnControlDeviceListener(this);
        //设备状态获取回调
        SmartPiApiManager.getInstance().setOnGetSubDeviceStateListener(this);
        //获取码库按键列表回调
        SmartPiApiManager.getInstance().setOnGetDBKeyListListener(this);
    }

    private void initData() {
        //获取码库设备按键列表
        SmartPiApiManager.getInstance().getDBKeyListWithMd5(md5,databaseType,fileId);

//        keyInfoList.clear();
//        for (int i = 0; i < DBRCKeyType.values().length; i ++){
//            DBRCKeyType keyType = DBRCKeyType.values()[i];
//            DBRCKeyInfo keyInfo = new DBRCKeyInfo(keyType.getKeyId(),keyType.getKeyName());
//            keyInfoList.add(keyInfo);
//        }
//        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSubDeviceState(StateType state, SubStateInfo subStateInfo) {
        if(state == StateType.OK){
            this.subStateInfo = subStateInfo;
        }
    }

    @Override
    public void onCotrolDevice(StateType state, String md5, int deviceSubId) {
        if(state == StateType.OK){
            Toast.makeText(context, context.getString(R.string.text_operate_successed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnDBKeyList(StateType stateType, List<DBRCKeyInfo> list) {
        if(stateType == StateType.OK){
            if(list != null){
                keyInfoList.clear();
                keyInfoList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
