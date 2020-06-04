package com.example.helloworld.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.DisinfectionLampApiManager;
import com.geeklink.smartpisdk.listener.OnDisinfectionLampRecordListener;
import com.gl.DisinfectionRecord;
import com.gl.StateType;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DisinfectionLampRecordsActivity extends AppCompatActivity implements OnDisinfectionLampRecordListener {
    private Context context;
    private CommonToolbar toolbar;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private List<DisinfectionRecord> DisinfectionRecordList = new ArrayList<>();
    private CommonAdapter<DisinfectionRecord> adapter;
    private byte loadIndex = 0;
    private String md5 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_disinfection_lamp_records);
        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        initView();
        setListener();
        getHistories();
    }

    private void setListener() {
        //设置消毒记录回调
        DisinfectionLampApiManager.getInstance().setOnDisinfectionLampRecordListener(this);
    }


    public void initView() {
        toolbar = findViewById(R.id.title_bar);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadIndex = 0;
                getHistories();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getHistories();
                refreshlayout.finishLoadMore(3000/*,false*/);//传入false表示加载失败
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);

        adapter = new CommonAdapter<DisinfectionRecord>(context, R.layout.item_single_device_record,DisinfectionRecordList) {
            @Override
            public void convert(ViewHolder holder, DisinfectionRecord disinfectionRecord, int position) {
                Date date=new Date();
                date.setTime(disinfectionRecord.mTime * 1000l);
                holder.setText(R.id.timeTv,new SimpleDateFormat("HH:mm").format(date));
                holder.setText(R.id.dateTv,new SimpleDateFormat("yyyy-MM-dd").format(date));
                switch (disinfectionRecord.mRecordType){
                    case PAUSE:
                        holder.setText(R.id.actionTv,context.getString(R.string.text_disinfection_paused));
                        holder.setText(R.id.accountTv, R.string.text_detected_someone);
                        break;
                    case END:
                        holder.setText(R.id.actionTv,String.format(Locale.ENGLISH,context.getString(R.string.text_disinfection_ended),disinfectionRecord.mDuration));
                        holder.setText(R.id.accountTv, "");
                        break;
                    case OPERATION:
                        holder.setText(R.id.actionTv,context.getString(R.string.text_disinfection_opened));
                        holder.setText(R.id.accountTv, getOperationTypeStr(disinfectionRecord));
                        break;
                    case CANCEL:
                        holder.setText(R.id.actionTv,context.getString(R.string.text_disinfection_canceled));
                        holder.setText(R.id.accountTv, getOperationTypeStr(disinfectionRecord));
                        break;
                    case RESTORE:
                        holder.setText(R.id.actionTv,context.getString(R.string.text_disinfection_resumed));
                        holder.setText(R.id.accountTv, R.string.text_device_auto);
                        break;
                    case CHILD_LOCK:
                        holder.setText(R.id.actionTv,context.getString(R.string.text_device_child_lock));
                        holder.setText(R.id.accountTv, R.string.text_device_auto);
                        break;
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }


    private String getOperationTypeStr(DisinfectionRecord disinfectionRecord){
        String oporationStr = "";
        switch (disinfectionRecord.mOperationType){
            case TIMER:
                oporationStr = context.getString(R.string.text_timing_disinfection);
                break;
            case APP:
                oporationStr = disinfectionRecord.mAccount;
                break;
            case HARDWARE:
                oporationStr = context.getString(R.string.text_device_auto);
                break;
        }

        return oporationStr;
    }

    private void getHistories() {
        //获取消毒记录
        DisinfectionLampApiManager.getInstance().getDisinfectionLampRecords(md5,loadIndex);
    }

    private void sort(List<DisinfectionRecord> records){
        for (int i = 0; i < records.size() - 1 ; i++) {
            int max = i; // 遍历的区间最大的值
            for (int j = i + 1; j < records.size(); j++) {
                if (records.get(j).mTime > records.get(max).mTime) {
                    // 找到当前遍历区间最大的值的索引
                    max = j;
                }
            }
            if (max != i) {
                // 发生了调换
                DisinfectionRecord temp = records.get(max);
                records.set(max,records.get(i));
                records.set(i,temp);
            }
        }
    }

    @Override
    public void onDisinfectionLampRecordResp(StateType state, String md5, int loadIndex, ArrayList<DisinfectionRecord> disinfectionRecordList) {
        if(loadIndex == 0){
            refreshLayout.finishRefresh();
            DisinfectionRecordList.clear();
            sort(disinfectionRecordList);
            DisinfectionRecordList.addAll(disinfectionRecordList);
        }else if (this.loadIndex == loadIndex){
            refreshLayout.finishLoadMore();
            sort(disinfectionRecordList);
            DisinfectionRecordList.addAll(disinfectionRecordList);
            loadIndex ++;
        }
        adapter.notifyDataSetChanged();
        if(DisinfectionRecordList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
    }
}
