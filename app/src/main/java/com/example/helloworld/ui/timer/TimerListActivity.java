package com.example.helloworld.ui.timer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
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
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.listener.OnGetSmartPiTimerListListener;
import com.geeklink.smartpisdk.listener.OnSetSmartPiTimerListener;
import com.gl.Api;
import com.gl.SingleTimerActionType;
import com.gl.SmartPiTimerSimple;
import com.gl.StateType;

import java.util.ArrayList;
import java.util.List;

public class TimerListActivity extends AppCompatActivity implements OnGetSmartPiTimerListListener {
    private CommonToolbar toolbar;
    private RecyclerView timerList;
    private SwipeRefreshLayout refreshView;
    private TimerAapter adapter;
    private Handler handler = new Handler();

    private Context context;
    private List<SmartPiTimerSimple> mDatas = new ArrayList<>();

    private String md5 = "";
    private static final int REQ_TIMER_SET = 1101;
    private static final String TAG = "SmartPiTimingListActivi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_pi_timing_list);

        context = this;

        md5 = getIntent().getStringExtra("md5");

        initView();
        ApiManager.getInstance().setOnGetSmartPiTimerListListener(this);
        ApiManager.getInstance().getActionTimerListWithMd5(md5);
    }

    public void initView() {
        toolbar = findViewById(R.id.title_bar);
        refreshView = findViewById(R.id.refreshable_view);
        timerList = findViewById(R.id.timer_list);

        refreshView.setColorSchemeResources(android.R.color.darker_gray);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ApiManager.getInstance().getActionTimerListWithMd5(md5);
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
                            startAddTimerAty(false, mDatas.get(position).mTimerId, position);
                    } else {
                            SmartPiTimerSimple timerSimple = mDatas.get(position);
                            timerSimple.mOnOff = !timerSimple.mOnOff;
                            ApiManager.getInstance().toDeviceSmartPiTimerSetSimple(md5,SingleTimerActionType.UPDATE,timerSimple);
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
        Intent intent = new Intent(context, TimerInfoSetActivity.class);
        intent.putExtra("md5", md5);
        intent.putExtra("isAdd", isAdd);
        intent.putExtra("timerId", timerId);
        intent.putExtra("editPos",editPos);
        startActivityForResult(intent,REQ_TIMER_SET);
    }


    @Override
    public void onGetSmartPiTimerList(StateType state, String md5, ArrayList<SmartPiTimerSimple> timerList) {
        if(state == StateType.OK){
            mDatas.clear();
            mDatas.addAll(timerList);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, context.getString(R.string.text_get_data_failed), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_TIMER_SET && resultCode == RESULT_OK){
            ApiManager.getInstance().getActionTimerListWithMd5(md5);
        }
    }

    class TimerAapter extends CommonAdapter<SmartPiTimerSimple> {

        public TimerAapter(Context context, List<SmartPiTimerSimple> datas) {
            super(context, R.layout.item_smart_timing_layout, datas);
        }

        @Override
        public void convert(ViewHolder holder, SmartPiTimerSimple smartPiTimerSimple, int position) {
            holder.setText(R.id.timer_name, smartPiTimerSimple.mName);
            holder.setText(R.id.timer_repeat, TimeUtils.formatWeek((byte) smartPiTimerSimple.mWeek, context));
            holder.setText(R.id.timer_time, TimeUtils.formatTime(smartPiTimerSimple.mTime));
            ((Switch)holder.getView(R.id.timer_switch)).setChecked(smartPiTimerSimple.mOnOff);

        }
    }
}
