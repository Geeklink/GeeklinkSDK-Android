package com.example.helloworld.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.enumdata.TopGetTimeZoneTypeEnum;
import com.example.helloworld.impl.OnItemClickListenerImp;
import com.example.helloworld.impl.RecyclerItemClickListener;
import com.example.helloworld.view.CommonToolbar;
import com.geeklink.smartpisdk.api.ApiManager;
import com.gl.TimezoneAction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class TimezoneActivity extends AppCompatActivity {
    private Context context;
    private RecyclerView mListView;
    private TimeZoneAdapter timeZoneAdapter;
    private SimpleDateFormat sdf;
    private String timezoneName[];
    private CommonToolbar topbar;
    private boolean isTimezoneAty = false;
    private List<String> mtime = new ArrayList<String>();
    private String md5="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_time);

        context = this;
        md5 = getIntent().getStringExtra("md5");
        initView();
    }

    public void initView() {
        isTimezoneAty = true;
        topbar = findViewById(R.id.topbar);
        mListView = findViewById(R.id.listView);

        timezoneName = getResources().getStringArray(R.array.timezone);
        for (int i = 0; i < TopGetTimeZoneTypeEnum.values().length; i++) {
            String j = Time(TopGetTimeZoneTypeEnum.values()[i].ordinal());
            Date d = new Date();
            sdf = new SimpleDateFormat("HH:mm:ss");
            String GMT = "GMT";
            String GMTADD = GMT + j;
            sdf.setTimeZone(TimeZone.getTimeZone(GMTADD));
            mtime.add(sdf.format(d));
        }
        timeZoneAdapter = new TimeZoneAdapter(context);
        mListView.setLayoutManager(new LinearLayoutManager(context));
        mListView.setAdapter(timeZoneAdapter);
        mListView.addOnItemTouchListener(new RecyclerItemClickListener(context, mListView, new OnItemClickListenerImp() {

            @Override
            public void onItemClick(View view, final int position) {
                new AlertDialog.Builder(context).setTitle("设置时区")
                        .setMessage("是否确认更改时区：" + timezoneName[position])
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeTimeZone(position);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }

        }));

        topbar.setLeftClick(new CommonToolbar.LeftListener() {
            @Override
            public void leftClick() {
                Intent intent = new Intent();
                intent.putExtra("timeZone", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void changeTimeZone(int position){
        String time = Time(TopGetTimeZoneTypeEnum.values()[position].ordinal());
        String symbol = time.substring(0, 1);
        String hourSt;
        String minuteStr;
        if (time.length() == 5) {
            hourSt = time.substring(1, 2);
            minuteStr = time.substring(3, 5);
        } else {
            hourSt = time.substring(1, 3);
            minuteStr = time.substring(4, 6);
        }
        int hour = Integer.valueOf(hourSt);
        int minute = Integer.valueOf(minuteStr).byteValue();
        short timezone = 0;
        if (symbol.equals("+")) {
            timezone = (short) (hour * 60 + minute);
        } else {
            timezone = (short) -(hour * 60 + minute);
        }
        ApiManager.getInstance().toDeviceTimeZone(md5, TimezoneAction.TIMEZONE_ACTION_SET,timezone);
        Intent intent = new Intent();
        intent.putExtra("timeZone", "GMT" + Time(TopGetTimeZoneTypeEnum.values()[position].ordinal()));
        setResult(RESULT_OK, intent);
        finish();
    }


    class TimeZoneAdapter extends CommonAdapter<String> {


        public TimeZoneAdapter(Context context) {
            super(context, R.layout.choose_time_item, mtime);
        }

        @Override
        public void convert(ViewHolder helper, String item, int position) {
            helper.setText(R.id.Time, item);
            helper.setText(R.id.name, timezoneName[position]);
            helper.setText(R.id.gmt, "GMT" + Time(TopGetTimeZoneTypeEnum.values()[position].ordinal()));
        }
    }

    String Time(int i) {

        switch (TopGetTimeZoneTypeEnum.values()[i]) {
            case KLT:
                return "+14:00";
            case NZDT:
                return "+13:00";

            case IDLE:
                return "+12:00";

            case NZST:
                return "+12:00";

            case NZT:
                return "+12:00";

            case AESST:
                return "+11:00";

            case ACSST:
                return "+10:30";

            case CADT:
                return "+10:30";

            case SADT:
                return "+10:30";

            case EAST:
                return "+10:00";

            case GST:
                return "+10:00";

            case LIGT:
                return "+10:00";

            case CAST:
                return "+9:30";

            case SAST:
                return "+9:30";

            case AWSST:
                return "+9:00";

            case JST:
                return "+9:00";

            case KST:
                return "+9:00";

            case MT:
                return "+8:30";

            case AWST:
                return "+8:00";

            case CCT:
                return "+8:00";

            case WST:
                return "+8:00";

            case JT:
                return "+7:30";

            case ALMST:
                return "+7:00";

            case CXT:
                return "+7:00";

            case MMT:
                return "+6:30";

            case ALMT:
                return "+6:00";
            case IST2:
                return "+5:30";
            case IOT:
                return "+5:00";

            case MVT:
                return "+5:00";

            case TFT:
                return "+5:00";

            case AFT:
                return "+4:30";

            case MUT:
                return "+4:00";

            case RET:
                return "+4:00";

            case SCT:
                return "+4:00";

            case IT:
                return "+3:30";

            case BT:
                return "+3:00";

            case EETDST:
                return "+3:00";

            case CETDST:
                return "+2:00";

            case EET:
                return "+2:00";

            case FWT:
                return "+2:00";

            case IST:
                return "+2:00";

            case MEST:
                return "+2:00";

            case METDST:
                return "+2:00";

            case SST:
                return "+2:00";

            case BST:
                return "+1:00";

            case CET:
                return "+1:00";

            case DNT:
                return "+1:00";

            case FST:
                return "+1:00";

            case MET:
                return "+1:00";

            case MEWT:
                return "+1:00";

            case MEZ:
                return "+1:00";

            case NOR:
                return "+1:00";

            case SWT:
                return "+1:00";

            case WETDST:
                return "+1:00";
            case UST:
                return "+0:00";
            case GMT:
                return "+0:00";

            case WET:
                return "+0:00";

            case WAT:
                return "-1:00";

            case FNST:
                return "-1:00";

            case FNT:
                return "-2:00";

            case BRST:
                return "-2:00";
            case NDT:
                return "-2:00";

            case ADT:
                return "-3:00";

            case BRT:
                return "-3:00";

            case NFT:
                return "-3:00";

            case AST:
                return "-4:00";

            case ACST:
                return "-4:00";

            case EDT:
                return "-4:00";

            case ACT:
                return "-5:00";

            case CDT:
                return "-5:00";

            case EST:
                return "-5:00";

            case CST:
                return "-6:00";

            case MDT:
                return "-6:00";

            case MST:
                return "-7:00";

            case PDT:
                return "-7:00";

            case PST:
                return "-8:00";

            case YDT:
                return "-8:00";

            case HDT:
                return "-9:00";

            case YST:
                return "-9:00";

            case AHST:
                return "-10:00";

            case CAT:
                return "-10:00";

            case NT:
                return "-11:00";

            case IDLW:
                return "-12:00";
            default:

        }
        return "0:00";
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("timeZone", "");
        setResult(RESULT_OK, intent);
        finish();
    }
}
