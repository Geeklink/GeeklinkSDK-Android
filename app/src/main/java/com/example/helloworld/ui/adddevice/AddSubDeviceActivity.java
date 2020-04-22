package com.example.helloworld.ui.adddevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.bean.AddDeviceInfo;
import com.example.helloworld.enumdata.AddDevType;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.listener.OnSetSubDevicveListener;
import com.gl.ActionFullType;
import com.gl.CarrierType;
import com.gl.CustomType;
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
        adapter = new CommonAdapter<AddDeviceInfo>(context,R.layout.item_add_sub_dev, addDeviceInfos) {
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
                    int subType;
                    switch (addDeviceInfos.get(position).mAddDevType) {
//                        case Curtain:
//                            subType = CustomType.CURTAIN.ordinal();
//                            break;
//                        case Fan:
//                            subType = CustomType.FAN.ordinal();
//                            break;
//                        case SoundBox:
//                            subType = CustomType.SOUNDBOX.ordinal();
//                            break;
//                        case Light:
//                            subType = CustomType.RC_LIGHT.ordinal();
//                            break;
//                        case AC_FAN:
//                            subType = CustomType.AC_FAN.ordinal();
//                            break;
//                        case PROJECTOR:
//                            subType = CustomType.PROJECTOR.ordinal();
//                            break;
//                        case AIR_PURIFIER:
//                            subType = CustomType.AIR_PURIFIER.ordinal();
//                            break;
//                        case ONE_KEY:
//                            subType = CustomType.ONE_KEY.ordinal();
//                            break;
                        default:
                            subType = CustomType.CUSTOM.ordinal();
                            break;
                    }
                    SubDevInfo subDevInfo = new SubDevInfo(0, DeviceMainType.CUSTOM,subType,0,0, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");
                    ApiManager.getInstance().setSubDeviceWithMd5(md5, subDevInfo, ActionFullType.INSERT);
                }else{
                    Intent intent  = new Intent(context, AddDbControlDevActivity.class);
                    intent.putExtra("md5",md5);
                    intent.putExtra("addDevType",addDeviceInfos.get(position).mAddDevType.ordinal());
                    startActivity(intent);
                    finish();
                }
            }
        }));


        ApiManager.getInstance().setOnSetSubDevicveListener(this);
        getYKBAddDevType();
    }


    //小派分机添加的设备列表
    public void getYKBAddDevType() {
        addDeviceInfos.clear();
        addDeviceInfos.add(new AddDeviceInfo("空调",AddDevType.AirCondition));//空调
        addDeviceInfos.add(new AddDeviceInfo("电视",AddDevType.TV));//电视
        addDeviceInfos.add(new AddDeviceInfo("机顶盒",AddDevType.STB));//机顶盒
        addDeviceInfos.add(new AddDeviceInfo("安卓盒子",AddDevType.IPTV));//安卓盒子
//        addDeviceInfos.add(new AddDeviceInfo("窗帘",AddDevType.Curtain));//窗帘
//        addDeviceInfos.add(new AddDeviceInfo("风扇",AddDevType.Fan));//风扇
//        addDeviceInfos.add(new AddDeviceInfo("音响",AddDevType.SoundBox));//音响
//        addDeviceInfos.add(new AddDeviceInfo("遥控灯",AddDevType.Light));//遥控灯
//        addDeviceInfos.add(new AddDeviceInfo("空调扇",AddDevType.AC_FAN));//空调扇
//        addDeviceInfos.add(new AddDeviceInfo("投影仪",AddDevType.PROJECTOR));//投影仪
//        addDeviceInfos.add(new AddDeviceInfo("空气净化器",AddDevType.AIR_PURIFIER));//空气净化器
//        addDeviceInfos.add(new AddDeviceInfo("一键遥控",AddDevType.ONE_KEY));//一键遥控
        addDeviceInfos.add(new AddDeviceInfo("自定义",AddDevType.Custom));//自定义
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSetSubDevice(StateType state, String md5, ActionFullType action, SubDevInfo subInfo) {
        if(state == StateType.OK){
            finish();
        }
    }
}
