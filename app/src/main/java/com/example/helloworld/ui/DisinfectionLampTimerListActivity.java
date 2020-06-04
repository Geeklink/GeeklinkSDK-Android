package com.example.helloworld.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.SectionRecyclerItemClickListener;
import com.example.helloworld.utils.TimeUtils;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.DisinfectionLampApiManager;
import com.geeklink.smartpisdk.data.GlobalData;
import com.geeklink.smartpisdk.listener.OnDisinfectionLampTimerGetListener;
import com.geeklink.smartpisdk.listener.OnDisinfectionLampTimerSetListener;
import com.gl.ActionFullType;
import com.gl.DisinfectionTimer;
import com.gl.StateType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DisinfectionLampTimerListActivity extends AppCompatActivity implements OnDisinfectionLampTimerGetListener, OnDisinfectionLampTimerSetListener {
    private CommonToolbar toolbar;
    private RecyclerView timerList;
    private SwipeRefreshLayout refreshView;
    private TimerAapter adapter;
    private Handler handler = new Handler();

    private Context context;
    private List<DisinfectionTimer> mDatas = new ArrayList<>();

    private String md5 = "";
    private static final int REQ_TIMER_SET = 1101;
    private static final String TAG = "DisinfectionLampTimerList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disinfection_lamp_timer_list);

        context = this;

        md5 = getIntent().getStringExtra("md5");

        initView();
        setListener();
        //获取定时列表
        DisinfectionLampApiManager.getInstance().getDisinfectionLampTimerInfoList(md5);
    }

    private void setListener() {
        //获取消毒灯定时列表回调
        DisinfectionLampApiManager.getInstance().setOnDisinfectionLampTimerGetListener(this);
        //设置定时回调
        DisinfectionLampApiManager.getInstance().setOnDisinfectionLampTimerSetListener(this);
    }

    public void initView() {
        toolbar = findViewById(R.id.title_bar);
        refreshView = findViewById(R.id.refreshable_view);
        timerList = findViewById(R.id.timer_list);

        refreshView.setColorSchemeResources(android.R.color.darker_gray);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DisinfectionLampApiManager.getInstance().getDisinfectionLampTimerInfoList(md5);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                },3000);
            }
        });
        adapter = new TimerAapter(context, mDatas);
        timerList.setLayoutManager(new LinearLayoutManager(context));
        timerList.setAdapter(adapter);

        timerList.addOnItemTouchListener(new SectionRecyclerItemClickListener(new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = timerList.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int position = timerList.getChildAdapterPosition(childView);
                    Switch timeSwitch = childView.findViewById(R.id.timer_switch);
                    int touchX = (int) e.getX();
                    if (touchX < timeSwitch.getX()) {
                        GlobalData.editPlugTimerInfo = mDatas.get(position);
                            startAddTimerAty(false, mDatas.get(position).mTimerId, position);
                    } else {
                            DisinfectionTimer timerSimple = mDatas.get(position);
                            timerSimple.mOnOff = !timerSimple.mOnOff;
                        DisinfectionLampApiManager.getInstance().setDisinfectionLampTimerInfo(md5, ActionFullType.UPDATE,timerSimple);
                    }
                }
                return true;
            }
        })));
        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                startAddTimerAty(true, 0,0);
            }
        });
    }


    private void startAddTimerAty(boolean isAdd, int timerId , int editPos) {
        Intent intent = new Intent(context, DisinfectionEditTimerFourActivity.class);
        intent.putExtra("md5", md5);
        intent.putExtra("isAdd", isAdd);
        intent.putExtra("timerId", timerId);
        intent.putExtra("editPos",editPos);
        startActivityForResult(intent,REQ_TIMER_SET);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_TIMER_SET && resultCode == RESULT_OK){
            //获取定时列表
            DisinfectionLampApiManager.getInstance().getDisinfectionLampTimerInfoList(md5);
        }
    }

    @Override
    public void onDisinfectionLampTimerGetResp(StateType state, String md5, ArrayList<DisinfectionTimer> disinfectionTimerList) {
        if(state == StateType.OK){
            mDatas.clear();
            mDatas.addAll(disinfectionTimerList);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, context.getString(R.string.text_get_data_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisinfectionLampTimerSetResp(StateType state, String md5) {
        //获取定时列表
        DisinfectionLampApiManager.getInstance().getDisinfectionLampTimerInfoList(md5);
    }

    class TimerAapter extends CommonAdapter<DisinfectionTimer> {

        public TimerAapter(Context context, List<DisinfectionTimer> datas) {
            super(context, R.layout.item_disinfection_lamp_timing_layout, datas);
        }

        @Override
        public void convert(ViewHolder holder, DisinfectionTimer item, int position) {
            int mMin, mHour;
            String daysOfWeek = TimeUtils.formatWeek((byte) item.mWeek, context);
            if (!daysOfWeek.equals("")) {
                holder.setText(R.id.text_repeat, daysOfWeek);
            } else {
                holder.setText(R.id.text_repeat, getString(R.string.text_once_time));
            }
            String timerState = context.getString(R.string.text_disinfection);
            // 格式化时间
            mHour = (int) Math.floor(item.mStartTime / 60);
            mMin = item.mStartTime - mHour * 60;
            StringBuilder sb1 = new StringBuilder();
            sb1.append(String.format(Locale.ENGLISH,"%02d", mHour)).append(":")
                    .append(String.format(Locale.ENGLISH,"%02d", mMin));
            int lastedTime = item.mDisinfectionTime / 60;

            // 名称
            holder.setText(R.id.text_timer_name, item.mName);
            // 时间
            holder.setText(R.id.text_timer, sb1.toString());

            if (lastedTime == 0) {
                holder.setText(R.id.text_keep_state_time, "");
            } else {

                StringBuffer sb = new StringBuffer();
                sb.append(timerState).append(lastedTime/60 + lastedTime % 60)
                        .append(getResources().getString(R.string.text_minute_unit));
                TextView keepTime = holder.getView(R.id.text_keep_state_time);
                keepTime.setText(sb.toString());
            }
            if(item.mOnOff){
                ((Switch)holder.getView(R.id.timer_switch)).setChecked(true);
//                holder.getView(R.id.timer_switch).setBackgroundResource(R.drawable.sence_kaiguan_off);
            }else{
                ((Switch)holder.getView(R.id.timer_switch)).setChecked(false);
//                holder.getView(R.id.timer_switch).setBackgroundResource(R.drawable.sence_kaiguan_on);
            }
        }
    }
}
