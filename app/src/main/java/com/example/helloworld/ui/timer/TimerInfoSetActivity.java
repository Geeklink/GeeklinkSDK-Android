package com.example.helloworld.ui.timer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.utils.TimeUtils;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.data.GlobalData;
import com.geeklink.smartpisdk.listener.OnGetSmartPiTimerDetailListener;
import com.geeklink.smartpisdk.listener.OnSetSmartPiTimerListener;
import com.gl.AcStateInfo;
import com.gl.DatabaseType;
import com.gl.SingleTimerActionType;
import com.gl.SmartPiTimerAction;
import com.gl.SmartPiTimerFull;
import com.gl.StateType;

import java.util.ArrayList;
import java.util.List;


public class TimerInfoSetActivity extends AppCompatActivity implements View.OnClickListener,CommonToolbar.RightListener,
        OnGetSmartPiTimerDetailListener , OnSetSmartPiTimerListener {

    private Context context;
    private CommonToolbar toolbar;
    private EditText timerName;
    private ImageView remarkDot;
    private CardView actionTimeLayout;
    private TextView actionTimeTv;
    private CardView repeatLayout;
    private TextView repeatTv;
    private RecyclerView actionList;
    private ImageView addActionBtn;
    private Button delBtn;
    private TimeActionAdapter actionAdapter;
    private boolean isAdd;
    private int timerId;
    private int dayOfWeek = 0;
    private SmartPiTimerFull timerFull;
    private List<SmartPiTimerAction> actionInfos = new ArrayList<>();
    private int editPos = 0;
    private int mTime = 9 * 60 ;//默认9：00
    private String md5 = "";

    private static final String TAG = "AddSmartTimingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_smart_timing_layout);
        isAdd = getIntent().getBooleanExtra("isAdd", true);
        editPos = getIntent().getIntExtra("editPos", 0);
        md5 = getIntent().getStringExtra("md5");
        context = this;
        initView();

    }

    public void initView() {
        addActionBtn = findViewById(R.id.addActionBtn);
        actionTimeLayout = findViewById(R.id.action_time_layout);
        actionTimeTv = findViewById(R.id.text_action_time);
        repeatLayout = findViewById(R.id.repeat_layout);
        repeatTv = findViewById(R.id.text_repeat);
        actionList = findViewById(R.id.actionListView);
        timerName = findViewById(R.id.editTimerName);
        remarkDot = findViewById(R.id.remarkDot);
        delBtn = findViewById(R.id.del_btn);
        toolbar = findViewById(R.id.title_bar);
        toolbar.setRightClick(this);
        delBtn.setOnClickListener(this);
        addActionBtn.setOnClickListener(this);

        delBtn.setVisibility(isAdd ? View.GONE : View.VISIBLE);

        setTextChangedListener();
        actionTimeLayout.setOnClickListener(this);
        repeatLayout.setOnClickListener(this);

        actionList.setLayoutManager(new LinearLayoutManager(context));
        actionAdapter = new TimeActionAdapter(context, actionInfos);
        actionList.setLayoutManager(new LinearLayoutManager(context));
        actionList.setNestedScrollingEnabled(false);
        actionList.setHasFixedSize(true);
        actionList.setFocusable(false);
        actionList.setAdapter(actionAdapter);

        actionList.addOnItemTouchListener(new RecyclerItemClickListener(context,actionList,new OnItemClickListenerImp(){
            @Override
            public void onItemClick(View view, final int position) {
                super.onItemClick(view, position);
                new AlertDialog.Builder(context).setTitle("提示")
                        .setMessage("确定要删除此任务吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    timerFull.mActionList.remove(position);
                                    getAction();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }));


        if (!isAdd) {
            timerId = getIntent().getIntExtra("timerId", 0);
            ApiManager.getInstance().getTimerInfoDetailWithMd5(md5,timerId);
        }else{
            timerFull = new SmartPiTimerFull(0,timerName.getText().toString(),mTime,0,true,new ArrayList<SmartPiTimerAction>());
        }
        timerName.setSelection(timerName.getText().toString().length());
        actionTimeTv.setText(TimeUtils.formatTime(mTime));
        repeatTv.setText(TimeUtils.formatWeek((byte) dayOfWeek, context));

        //设置回调
        ApiManager.getInstance().setOnGetSmartPiTimerDetailListener(this);
        ApiManager.getInstance().setOnSetSmartPiTimerListener(this);
    }

    private void setTextChangedListener() {
        timerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                remarkDot.setSelected(editable.toString().length()!= 0);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_time_layout:

                break;
            case R.id.addActionBtn:
                if(actionInfos.size() == 5){
                    Toast.makeText(context, "最多添加5个", Toast.LENGTH_SHORT).show();
                    return;
                }
                GlobalData.smartPiTimerFull = timerFull;
                Intent intent =  new Intent(context, TimerActionSetActivity.class);
                intent.putExtra("md5", md5);
                startActivityForResult(intent, 10);
                break;
            case R.id.del_btn:
                new AlertDialog.Builder(context).setTitle("提示")
                        .setMessage("确定删除此定时吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ApiManager.getInstance().setActionTimerInfoWithMd5(md5,SingleTimerActionType.DELETE,timerFull);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
        }
    }

    private void getAction() {
        actionInfos.clear();
        actionInfos.addAll(timerFull.mActionList);
        actionAdapter.notifyDataSetChanged();
    }


    @Override
    public void rightClick() {
        String name = timerName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "请输入名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.getBytes().length > 24){
            Toast.makeText(context, "字数过长，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        SingleTimerActionType actionType;
        if (isAdd) {
            actionType = SingleTimerActionType.INSERT;
        } else {
            if (timerFull == null) {
                return;
            }
            actionType = SingleTimerActionType.UPDATE;
        }
        timerFull.mName = name;
        timerFull.mTime = mTime;
        timerFull.mWeek = dayOfWeek;
        //设置定时
        ApiManager.getInstance().setActionTimerInfoWithMd5(md5,actionType,timerFull);
    }

    @Override
    public void onGetSmartPiTimerDetail(StateType state, String md5, SmartPiTimerFull smartPiTimerFull) {
        if(state == StateType.OK) {
            timerFull = smartPiTimerFull;
            mTime = timerFull.mTime;
            timerName.setText(timerFull.mName);
            timerName.setSelection(timerName.getText().length());
            actionTimeTv.setText(TimeUtils.formatTime(mTime));
            getAction();
        } else if(state == StateType.FULL_ERROR){
            Toast.makeText(context, "数据已满！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetSmartPiTimer(StateType state, String md5, SingleTimerActionType action) {
        if(state == StateType.OK) {
            Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }else{
            Toast.makeText(context, "数据保存失败！", Toast.LENGTH_SHORT).show();
        }
    }


    class TimeActionAdapter extends CommonAdapter<SmartPiTimerAction> {

        public TimeActionAdapter(Context context, List<SmartPiTimerAction> datas) {
            super(context, R.layout.item_action_layout, datas);
        }

        @Override
        public void convert(ViewHolder holder, SmartPiTimerAction smartPiTimerAction, int position) {
            holder.setText(R.id.devNameTv,  "subId : " + smartPiTimerAction.mSubId);
            if(smartPiTimerAction.mValue.length() == 10){
                ((TextView) holder.getView(R.id.actionTv)).setText(getAcStateDes(ApiManager.getInstance().getACStateInfoWithStateValue(smartPiTimerAction.mValue)));
            }else {
                ((TextView) holder.getView(R.id.actionTv)).setText("");
            }

        }
    }


    private String getAcStateDes(AcStateInfo acStateInfo) {
        if(acStateInfo == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (acStateInfo.mPower) {
            sb.append("电源：开");
        } else {
            sb.append("电源：关");
        }
        sb.append(" ")
                .append("温度：")
                .append(acStateInfo.mTemp)
                .append("℃  ");
        if (acStateInfo.mDir == 1) {
            sb.append("风向：风向1");
        } else if (acStateInfo.mDir == 2) {
            sb.append("风向：风向2");
        } else if (acStateInfo.mDir == 3) {
            sb.append("风向：风向3");
        } else if (acStateInfo.mDir == 4) {
            sb.append("风向：风向4");
        } else {
            sb.append("风向：扫风");
        }

        sb.append(" ");
        if (acStateInfo.mSpeed == 1) {
            sb.append("风速：低");
        } else if (acStateInfo.mDir == 2) {
            sb.append("风速：中");
        } else if (acStateInfo.mDir == 3) {
            sb.append("风速：高");
        } else {
            sb.append("风速：自动");
        }

        sb.append(" ");
        if (acStateInfo.mMode == 1) {
            sb.append("模式：制冷");
        } else if (acStateInfo.mDir == 2) {
            sb.append("模式：除湿");
        } else if (acStateInfo.mDir == 3) {
            sb.append("模式：送风");
        } else if (acStateInfo.mDir == 4) {
            sb.append("模式：制热");
        } else {
            sb.append("模式：自动");
        }
        return sb.toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 10 && resultCode == RESULT_OK){
            timerFull = GlobalData.smartPiTimerFull;
            getAction();
        }
        if(requestCode == 101 && resultCode == RESULT_OK){
            dayOfWeek = data.getIntExtra("Week",0);
            repeatTv.setText(TimeUtils.formatWeek((byte) dayOfWeek, context));
        }
    }
}
