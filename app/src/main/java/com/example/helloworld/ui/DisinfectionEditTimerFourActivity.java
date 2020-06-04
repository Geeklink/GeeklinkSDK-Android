package com.example.helloworld.ui;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.helloworld.R;
import com.example.helloworld.utils.TimeUtils;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.DisinfectionLampApiManager;
import com.geeklink.smartpisdk.data.GlobalData;
import com.geeklink.smartpisdk.listener.OnDisinfectionLampTimerSetListener;
import com.gl.ActionFullType;
import com.gl.DisinfectionTimer;
import com.gl.StateType;

import java.util.Locale;


public class DisinfectionEditTimerFourActivity extends AppCompatActivity implements View.OnClickListener, OnDisinfectionLampTimerSetListener {
    private CommonToolbar toolbar;
    private EditText timerNameEdt;

    private TextView startTimeTv, delayTimeTv,repeatTv;

    private CardView timeLayout, delayedLayout, repeatLayout;

    private int dayOfWeek;
    private boolean isAdd = false;


    private Context context;
    private int timerId;
    private String initName;
    private int initDelaytime = 30;//默认30分钟,单位：分
    private int initTimer = 0;//默认00：00
    private int currentHour = 0;
    private int currentMinute = 0;
    private String md5 = "";

    private static final String TAG = "DisinfectionEditTimer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disinfection_timer_info_set);

        Intent intent = getIntent();
        isAdd = intent.getBooleanExtra("isAdd", false);
        md5 = getIntent().getStringExtra("md5");

        context = this;

        initView();

        setListener();
    }

    private void setListener() {
        //设置消毒灯定时回调
        DisinfectionLampApiManager.getInstance().setOnDisinfectionLampTimerSetListener(this);
    }

    public void initView() {
        toolbar = findViewById(R.id.title_bar);

        timerNameEdt = findViewById(R.id.editTimerName);

        timeLayout = findViewById(R.id.action_time_layout);
        delayedLayout = findViewById(R.id.action_duration_layout);
        repeatLayout = findViewById(R.id.repeat_layout);


        startTimeTv = findViewById(R.id.tv_time_detail);
        delayTimeTv = findViewById(R.id.tv_delay_time_detail);
        repeatTv = findViewById(R.id.tv_repeatdays);


            toolbar.setRightTextVisible(true);
            timeLayout.setOnClickListener(this);
            delayedLayout.setOnClickListener(this);
            repeatLayout.setOnClickListener(this);
            findViewById(R.id.del_btn).setOnClickListener(this);


        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                saveTimingAction();
            }
        });

        if (!isAdd) {
            toolbar.setMainTitle(R.string.text_change_timing);
            initData();
            findViewById(R.id.del_btn).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.del_btn).setVisibility(View.GONE);
        }

        currentHour = initTimer / 60;
        currentMinute = initTimer % 60;
        String strHour = String.format(Locale.ENGLISH,"%02d", currentHour);
        String strMinute = String.format(Locale.ENGLISH,"%02d", currentMinute);
        startTimeTv.setText(strHour + " : " + strMinute);
        delayTimeTv.setText( initDelaytime + getResources().getString(R.string.text_minute_unit));
        if(dayOfWeek == 0){
            repeatTv.setText(R.string.text_once_time);
        }else {
            repeatTv.setText(TimeUtils.formatWeek((byte) dayOfWeek, context));
        }
    }

    private void saveTimingAction() {
        initName = timerNameEdt.getText().toString();
        if (initName.length() == 0) {
            Toast.makeText(this, context.getString(R.string.dialog_input_name_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (initName.getBytes().length > 24) {
            Toast.makeText(this, context.getString(R.string.text_number_limit), Toast.LENGTH_SHORT).show();
            return;
        }

        String lastedTime = String.valueOf(initDelaytime * 60);
        int timerid = timerId;
        int timerDelayedTime;
        if (lastedTime == null || lastedTime.equals("")) {
            timerDelayedTime = 0;
        } else if (lastedTime.equals("0")) {
            timerDelayedTime = 0;
        } else {
            timerDelayedTime = Integer.parseInt(lastedTime);
        }


        DisinfectionTimer timerSimple = new DisinfectionTimer(timerid,initName,true,dayOfWeek,currentHour * 60 + currentMinute, timerDelayedTime);
        ActionFullType actionFullType;
        if (!isAdd) {
            actionFullType = ActionFullType.UPDATE;
        } else {
            actionFullType = ActionFullType.INSERT;
        }
        //设置消毒灯定时
        DisinfectionLampApiManager.getInstance().setDisinfectionLampTimerInfo(md5, actionFullType,timerSimple);
    }

    public void initData() {
        if(!isAdd) {
            timerId = GlobalData.editPlugTimerInfo.mTimerId;
            initName = GlobalData.editPlugTimerInfo.mName;
            initDelaytime = GlobalData.editPlugTimerInfo.mDisinfectionTime/60 + GlobalData.editPlugTimerInfo.mDisinfectionTime % 60;
            dayOfWeek = GlobalData.editPlugTimerInfo.mWeek;
            initTimer = GlobalData.editPlugTimerInfo.mStartTime;
        }
        timerNameEdt.setText(initName);
        timerNameEdt.setSelection(initName.length());
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.del_btn:
                DisinfectionLampApiManager.getInstance().setDisinfectionLampTimerInfo(md5, ActionFullType.DELETE, GlobalData.editPlugTimerInfo);
                break;
            case R.id.action_time_layout:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = hourOfDay;
                        int min = minute;
                        currentHour = hour;
                        currentMinute = min;
                        String strHour = String.format(Locale.ENGLISH,"%02d", hour);
                        String strMinute = String.format(Locale.ENGLISH,"%02d", min);
                        startTimeTv.setText(strHour + " : " + strMinute);

                    }
                }, currentHour, currentMinute, true).show();
                break;
            case R.id.action_duration_layout:

                break;
            case R.id.repeat_layout:

                break;

        }
    }


    @Override
    public void onDisinfectionLampTimerSetResp(StateType state, String md5) {
        if(state == StateType.OK){
            finish();
        }
    }
}
