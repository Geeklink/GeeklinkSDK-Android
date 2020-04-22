package com.example.helloworld.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.utils.DialogUtils;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.bean.DBRCKeyInfo;
import com.geeklink.smartpisdk.listener.OnControlDeviceListener;
import com.geeklink.smartpisdk.listener.OnDeviceEntryCodeListener;
import com.geeklink.smartpisdk.listener.OnSetDeviceKeyListener;
import com.geeklink.smartpisdk.utils.SharePrefUtil;
import com.gl.ActionFullType;
import com.gl.CarrierType;
import com.gl.CustomType;
import com.gl.DeviceMainType;
import com.gl.KeyInfo;
import com.gl.KeyStudyType;
import com.gl.StateType;
import com.gl.SubDevInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LocalIrCodeActivity extends AppCompatActivity implements OnDeviceEntryCodeListener, OnControlDeviceListener , OnSetDeviceKeyListener {

    private CommonToolbar toolbar;
    private Context context;
    private RecyclerView recyclerView;

    private CommonAdapter<DBRCKeyInfo> adapter;

    private List<DBRCKeyInfo> IrCodeList = new ArrayList<>();
    private String md5 = "";
    private String IrCodeInfoStr;
    private int subId = 0;
    private String irCode = "";
    private SubDevInfo subDevInfo;

    private AlertDialog alertDialog;
    private Handler handler = new Handler();
    private Runnable cancelDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    };

    private static final String TAG = "LocalIrCodeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_key);
        context = this;
        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5").toLowerCase();

        subDevInfo = new SubDevInfo(subId, DeviceMainType.CUSTOM, CustomType.CUSTOM.ordinal(),0,0, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");

        toolbar = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommonAdapter<DBRCKeyInfo>(context,R.layout.item_add_sub_dev2, IrCodeList) {
            @Override
            public void convert(ViewHolder holder, DBRCKeyInfo myKeyInfo, int position) {
                holder.setText(R.id.nameTv,myKeyInfo.getKeyName());
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new OnItemClickListenerImp() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                ApiManager.getInstance().controlSubDeviceKeyWithMd5(md5,subDevInfo,null, IrCodeList.get(position).getKeyId());
            }
        }));

        ApiManager.getInstance().setOnControlDeviceListener(this);
        ApiManager.getInstance().setOnDeviceEntryCodeListener(this);
        ApiManager.getInstance().setOnSetDeviceKeyListener(this);

        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                startStudyKeyCode();
            }
        });

        toolbar.setMainTitle("红外码读取和发送");
        initData();

    }


    private void startStudyKeyCode(){
        if(alertDialog == null){
            alertDialog = new AlertDialog.Builder(context).setMessage("请在20秒内将红外遥控器对准主机并且按下遥控器按键")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ApiManager.getInstance().getCodeFromDeviceWithMd5(md5, KeyStudyType.KEY_STUDY_CANCEL);
                        }
                    }).create();
        }
        alertDialog.show();
        ApiManager.getInstance().getCodeFromDeviceWithMd5(md5, KeyStudyType.KEY_STUDY_IR);
        handler.postDelayed(cancelDialogRunnable, 20 * 1000);
    }

    private void initData() {
        IrCodeInfoStr = SharePrefUtil.getString(context,"IrCodeInfoStr","");
        if(IrCodeInfoStr.getBytes().length > 2){
            IrCodeList.clear();
            List<DBRCKeyInfo> ketList = new Gson().fromJson(IrCodeInfoStr,new TypeToken<List<DBRCKeyInfo>>() { }.getType());
            if(ketList != null){
                IrCodeList.addAll(ketList);
            }
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCotrolDevice(StateType state, String md5, int deviceSubId) {
        if(state == StateType.OK){
            Toast.makeText(context, "控制成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeviceEntryCode(StateType state, String md5, KeyStudyType type, String code) {
        if(state == StateType.OK){
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
                irCode = code;
                KeyInfo keyInfo = new KeyInfo(0,type.ordinal(),code);
                ApiManager.getInstance().setSubDeviceKeyWithMd5(md5,subId, ActionFullType.INSERT,keyInfo);
            }

        }
    }

    @Override
    public void onSetDeviceKey(StateType state, String md5, ActionFullType action, int subId, int keyId) {
        if(state == StateType.OK){
            if(action == ActionFullType.INSERT) {
                DBRCKeyInfo keyInfo = new DBRCKeyInfo(keyId,irCode);
                IrCodeList.add(keyInfo);
                adapter.notifyDataSetChanged();
                SharePrefUtil.saveString(context,"IrCodeInfoStr",new Gson().toJson(IrCodeList));
            }
            Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "操作失败！", Toast.LENGTH_SHORT).show();
        }

    }


}
