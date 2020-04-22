package com.example.helloworld.ui.cotrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.bean.DBRCKeyInfo;
import com.geeklink.smartpisdk.data.DBRCKeyType;
import com.geeklink.smartpisdk.listener.OnControlDeviceListener;
import com.geeklink.smartpisdk.listener.OnGetDBKeyListListener;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceStateListener;
import com.gl.CarrierType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.gl.SubStateInfo;

import java.util.ArrayList;
import java.util.List;

public class DbTvStbDevControlActivity extends AppCompatActivity implements OnGetSubDeviceStateListener, OnControlDeviceListener , OnGetDBKeyListListener {

    private Context context;
    private RecyclerView recyclerView;

    private CommonAdapter<DBRCKeyInfo> adapter;

    private List<DBRCKeyInfo> keyInfoList = new ArrayList<>();
    private String md5 = "";
    private int subId ;
    private int fileId;
    private int subType;
    private int mainType;
    private SubDevInfo subDevInfo;

    private SubStateInfo subStateInfo;
    private DatabaseType databaseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_stb_dev);
        context = this;
        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId",0);
        subType = intent.getIntExtra("subType",0);
        mainType = intent.getIntExtra("mainType",0);
        fileId = intent.getIntExtra("fileId",0);

        databaseType = DatabaseType.values()[subType];

        subDevInfo = new SubDevInfo(subId, DeviceMainType.DATABASE,subType,0,fileId, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<DBRCKeyInfo>(context,R.layout.item_add_sub_dev, keyInfoList) {
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
                ApiManager.getInstance().controlSubDeviceKeyWithMd5(md5,subDevInfo,null, keyInfoList.get(position).getKeyId());
            }
        }));

        ApiManager.getInstance().setOnControlDeviceListener(this);
        ApiManager.getInstance().setOnGetSubDeviceStateListener(this);
        ApiManager.getInstance().getSubDeviceStateInfo(md5,subId);

        initData();
    }

    private void initData() {
        ApiManager.getInstance().setOnGetDBKeyListListener(this);
        ApiManager.getInstance().getDBKeyListWithMd5(md5,databaseType,fileId);
        keyInfoList.clear();
        for (int i = 0 ; i < DBRCKeyType.values().length; i ++){
            DBRCKeyType keyType = DBRCKeyType.values()[i];
            DBRCKeyInfo keyInfo = new DBRCKeyInfo(keyType.getKeyId(),keyType.getKeyName());
            keyInfoList.add(keyInfo);
        }
        adapter.notifyDataSetChanged();
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
            Toast.makeText(context, "控制成功！", Toast.LENGTH_SHORT).show();
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
