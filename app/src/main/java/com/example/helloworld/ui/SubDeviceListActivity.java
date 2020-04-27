package com.example.helloworld.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.ui.adddevice.AddSubDeviceActivity;
import com.example.helloworld.ui.cotrol.DbACDevControlActivity;
import com.example.helloworld.ui.cotrol.CustomDevControlActivity;
import com.example.helloworld.ui.cotrol.DbTvStbDevControlActivity;
import com.example.helloworld.utils.DeviceUtil;
import com.example.helloworld.utils.DialogUtils;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.listener.OnGetSubDeviceListener;
import com.geeklink.smartpisdk.listener.OnSetSubDevicveListener;
import com.geeklink.smartpisdk.utils.SharePrefUtil;
import com.gl.ActionFullType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SubDeviceListActivity extends AppCompatActivity implements View.OnClickListener, OnGetSubDeviceListener, OnSetSubDevicveListener {
    private Button addBtn;
    private String md5 = "";
    private int subId;

    private Context context;

    private List<SubDevInfo> mSubDeviceList = new ArrayList<>();
    private CommonAdapter<SubDevInfo> adapter;
    private RecyclerView recyclerView;

    private static final String TAG = "SubDeviceListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sub_device_list);

        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        subId = intent.getIntExtra("subId",0);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<SubDevInfo>(context,R.layout.item_sub_device,mSubDeviceList) {
            @Override
            public void convert(ViewHolder holder, SubDevInfo subDevInfo, int position) {
                holder.setText(R.id.nameTv, DeviceUtil.getDeviceType(context,subDevInfo.mMainType,subDevInfo.mSubType));
                if(subDevInfo.mMainType == DeviceMainType.DATABASE){
                    holder.setText(R.id.stateTv," subId : " +subDevInfo.mSubId + "  fileId : " + subDevInfo.mFileId);
                }else{
                    holder.setText(R.id.stateTv," subId : " +subDevInfo.mSubId  + "    " + subDevInfo.mKeyIdList.size() + context.getString(R.string.text_keys_num));
                }
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, final int position) {
                super.onItemClick(view, position);
                List<String> actions = new ArrayList<>();
                actions.add(context.getString(R.string.text_dev_detail));
                actions.add(context.getString(R.string.text_delete));
                DialogUtils.showItemDialog(SubDeviceListActivity.this,actions,new OnItemClickListenerImp(){
                    @Override
                    public void onItemClick(View view, int pos) {
                        super.onItemClick(view, pos);
                        if(pos == 0) startDeviceDetail(mSubDeviceList.get(position));
                        else{
                           ApiManager.getInstance().setSubDeviceWithMd5(md5,mSubDeviceList.get(position), ActionFullType.DELETE);
                        }
                    }
                });


            }
        }));

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        ApiManager.getInstance().setGetSubDeviceListener(this);
        ApiManager.getInstance().setOnSetSubDevicveListener(this);
    }

    private void startDeviceDetail(SubDevInfo subDevInfo) {
        Intent intent = new Intent();
        if(subDevInfo.mMainType == DeviceMainType.DATABASE) {
            if (DatabaseType.values()[subDevInfo.mSubType] == DatabaseType.AC) {
                intent.setClass(context, DbACDevControlActivity.class);
            }else{
                intent.setClass(context, DbTvStbDevControlActivity.class);
            }
        }else{
            intent.setClass(context, CustomDevControlActivity.class);
            intent.putExtra("mKeyIdListStr",new Gson().toJson(subDevInfo.mKeyIdList));
        }
        intent.putExtra("md5",subDevInfo.mMd5);
        intent.putExtra("subId",subDevInfo.mSubId);
        intent.putExtra("mainType",subDevInfo.mMainType.ordinal());
        intent.putExtra("subType",subDevInfo.mSubType);
        intent.putExtra("fileId" ,subDevInfo.mFileId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiManager.getInstance().getSubDeviceListWithMd5(md5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addBtn:
                Intent intent = new Intent(context, AddSubDeviceActivity.class);
                intent.putExtra("md5",md5);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onGetSubDevice(StateType state, String md5, ArrayList<SubDevInfo> subInfoList) {
        if(state == StateType.OK){
            mSubDeviceList.clear();
            mSubDeviceList.addAll(subInfoList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSetSubDevice(StateType state, String md5, ActionFullType action, SubDevInfo subInfo) {
        ApiManager.getInstance().getSubDeviceListWithMd5(md5);
    }
}
