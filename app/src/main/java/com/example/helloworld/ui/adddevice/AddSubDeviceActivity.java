package com.example.helloworld.ui.adddevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.bean.AddDeviceInfo;
import com.example.helloworld.enumdata.AddDevType;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.geeklink.smartpisdk.api.SmartPiApiManager;
import com.geeklink.smartpisdk.listener.OnSetSubDevicveListener;
import com.gl.ActionFullType;
import com.gl.CarrierType;
import com.gl.DatabaseDevType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;

import java.util.ArrayList;
import java.util.List;

public class AddSubDeviceActivity extends AppCompatActivity implements OnSetSubDevicveListener {

    private Context context;
    private RecyclerView recyclerView;

    private CommonAdapter<AddDeviceInfo> adapter;

    private List<AddDeviceInfo> addDeviceInfos = new ArrayList<>();
    private String md5 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add_sub_device);

        md5 = getIntent().getStringExtra("md5").toLowerCase();

        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 20, false));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<AddDeviceInfo>(context, R.layout.item_add_sub_dev, addDeviceInfos) {
            @Override
            public void convert(ViewHolder holder, AddDeviceInfo addDeviceInfo, int position) {
                holder.setText(R.id.nameTv,addDeviceInfo.mDevName);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                if(addDeviceInfos.get(position).mAddDevType.ordinal() > AddDevType.IPTV.ordinal()){//添加自学习遥控
                    SubDevInfo subDevInfo = new SubDevInfo(0, DeviceMainType.CUSTOM_DEV, DatabaseDevType.TV,0,0,0, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");
                    SmartPiApiManager.getInstance().setSubDeviceWithMd5(md5, subDevInfo, ActionFullType.INSERT);
                }else{
                    Intent intent  = new Intent(context, AddDbControlDevActivity.class);
                    intent.putExtra("md5",md5);
                    intent.putExtra("addDevType",addDeviceInfos.get(position).mAddDevType.ordinal());
                    startActivity(intent);
                    finish();
                }
            }
        }));

        //设置子设备回调
        SmartPiApiManager.getInstance().setOnSetSubDevicveListener(this);
        getYKBAddDevType();
    }


    //小派分机添加的设备列表
    public void getYKBAddDevType() {
        addDeviceInfos.clear();
        addDeviceInfos.add(new AddDeviceInfo(context.getString(R.string.text_ac), AddDevType.AirCondition));//空调
        addDeviceInfos.add(new AddDeviceInfo(context.getString(R.string.text_tv), AddDevType.TV));//电视
        addDeviceInfos.add(new AddDeviceInfo(context.getString(R.string.text_stb), AddDevType.STB));//机顶盒
        addDeviceInfos.add(new AddDeviceInfo(context.getString(R.string.text_iptv), AddDevType.IPTV));//安卓盒子
        addDeviceInfos.add(new AddDeviceInfo(context.getString(R.string.text_custom), AddDevType.Custom));//自定义
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSetSubDevice(StateType state, String md5, ActionFullType action, SubDevInfo subInfo) {
        if(state == StateType.OK){
            finish();
        }
    }
}
