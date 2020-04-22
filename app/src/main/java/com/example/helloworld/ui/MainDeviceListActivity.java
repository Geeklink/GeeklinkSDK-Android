package com.example.helloworld.ui;

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
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.utils.DialogUtils;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.utils.SharePrefUtil;
import com.gl.DeviceInfo;
import com.gl.DeviceMainType;
import com.gl.GlDevType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainDeviceListActivity extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerView;

    private List<DeviceInfo> mainDevices = new ArrayList<>();

    private CommonAdapter<DeviceInfo> adapter;

    private static final String TAG = "MainDeviceListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_device_list);
        context = this;

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<DeviceInfo>(context,R.layout.item_device,mainDevices) {
            @Override
            public void convert(ViewHolder holder, DeviceInfo deviceInfo, int position) {
                holder.setText(R.id.nameTv,deviceInfo.mMd5);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, final int position) {
                super.onItemClick(view, position);
                List<String> actions = new ArrayList<>();
                actions.add("查看设备");
                actions.add("删除");
                DialogUtils.showItemDialog(MainDeviceListActivity.this,actions,new OnItemClickListenerImp(){
                    @Override
                    public void onItemClick(View view, int pos) {
                        super.onItemClick(view, pos);
                        if(pos == 0){
                            Intent intent = new Intent(context, MainDeviceDetailActivity.class);
                            intent.putExtra("md5",mainDevices.get(position).mMd5);
                            intent.putExtra("subId",mainDevices.get(position).mSubId);
                            startActivity(intent);
                        }else{
                            ApiManager.getInstance().deleteMainDevice(mainDevices.get(position).mMd5);
                            mainDevices.remove(position);
                            adapter.notifyDataSetChanged();
                            SharePrefUtil.saveString(context,"deviceInfoList",new Gson().toJson(mainDevices));
                        }
                    }
                });


            }
        }));

        getAndLinkMainDevice();
    }

    /**
     * 连接所有设备，让设备在线（注意MD5要小写）
     */
    private void getAndLinkMainDevice() {
        String deviceInfoListStr = SharePrefUtil.getString(context,"deviceInfoList","");
        List<DeviceInfo> deviceInfoList = new Gson().fromJson(deviceInfoListStr,new TypeToken<List<DeviceInfo>>() { }.getType());
        if(deviceInfoList != null && deviceInfoList.size() > 0){
            mainDevices.clear();
            mainDevices.addAll(deviceInfoList);
        }

//        String md5 = "B97C1B579A595EEB4B5FD36E28B771A0";
//        String token = "314A9455";
//        DeviceInfo deviceInfo = new DeviceInfo(token, DeviceMainType.GEEKLINK,md5.toLowerCase(), GlDevType.SMART_PI.ordinal(),0);
//        mainDevices.clear();
//        mainDevices.add(deviceInfo);

        adapter.notifyDataSetChanged();
        ApiManager.getInstance().linkAllMainDevice((ArrayList<DeviceInfo>) mainDevices);
    }

}
