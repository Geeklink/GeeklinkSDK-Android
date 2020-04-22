package com.example.helloworld.ui.adddevice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helloworld.R;
import com.example.helloworld.view.CommonToolbar;
import com.example.helloworld.view.SelectorImageView;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.data.DBRCKeyType;
import com.geeklink.smartpisdk.listener.OnGetDBRCBrandFlieIdsListener;
import com.geeklink.smartpisdk.listener.OnSetSubDevicveListener;
import com.gl.AcStateInfo;
import com.gl.ActionFullType;
import com.gl.CarrierType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;
import com.gl.StateType;
import com.gl.SubDevInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestCodeActivity extends AppCompatActivity implements View.OnClickListener , OnGetDBRCBrandFlieIdsListener, OnSetSubDevicveListener {
    private CommonToolbar toolbar;
    private Context context;

    public int index;//当前序号
    private int maxIndex;//最大
    private String currentBrandName;//当前品牌名
    private String brandId;
    private String irLibName = "";
    private DatabaseType databaseType;
    private String md5 = "";

    //新码库
    private List<String> fileIdList = new ArrayList<>();
    private int fileId;

    private LinearLayout layout = null;
    private TextView tvTitle = null;
    private Button btnPre = null;
    private Button btnNext = null;

    //控件
    private LinearLayout layout1;
    private LinearLayout layout2;
    private TextView tvTitle1, tvTitle2;
    private Button btnPre1,btnPre2;
    private Button btnNext1, btnNext2;
    private SelectorImageView btnSwitch;
    private SelectorImageView btnMute;
    private SelectorImageView btnUp;
    private SelectorImageView btnDown;
    private SelectorImageView btnLeft;
    private SelectorImageView btnRight;
    private SelectorImageView btnOK;
    private SelectorImageView btnAV_TV;
    private Button btnAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_test_code);

        Intent intent  = getIntent();
        md5 = intent.getStringExtra("md5");
        databaseType = DatabaseType.values()[intent.getIntExtra("databaseType",0)];
        currentBrandName = intent.getStringExtra("brandName");
        brandId = intent.getStringExtra("brandId");
        initView();

        ApiManager.getInstance().getDBRCBrandFlieIdWithMd5(md5, databaseType,brandId);
        ApiManager.getInstance().setOnGetDBRCBrandFlieIdsListener(this);
    }


    private void initView() {
        toolbar = findViewById(R.id.title);
        layout1 = findViewById(R.id.layout_tv_stb);
        layout2 = findViewById(R.id.layout_ac);

        tvTitle1 = findViewById(R.id.test_title);
        tvTitle2 = findViewById(R.id.test_title1);

        btnPre1 = findViewById(R.id.btn_previous);
        btnPre2 = findViewById(R.id.btn_previous1);

        btnNext1 = findViewById(R.id.btn_next);
        btnNext2 = findViewById(R.id.btn_next1);

        btnSwitch = findViewById(R.id.btn_sw);
        btnMute = findViewById(R.id.btn_mute);
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnOK = findViewById(R.id.btn_ok);
        btnAV_TV = findViewById(R.id.btn_avtv);
        btnAgain = findViewById(R.id.button_test);

        if (databaseType == DatabaseType.IPTV) {
            ((ImageView)findViewById(R.id.btn_mute)).setImageResource(R.drawable.key_home);
        }

        toolbar.setMainTitle(currentBrandName);
        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                startBindDeviceAty(databaseType.ordinal(),fileId,currentBrandName);
            }
        });

        ApiManager.getInstance().setOnSetSubDevicveListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_test:
                getKeyAndSendCtrl(fileIdList.get(index), 0);
                break;

            case R.id.btn_avtv:
                if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_AVTV.getKeyId());
                }
                break;

            case R.id.btn_sw:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_WAIT.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_POWER.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_POWER.getKeyId());
                }
                break;

            case R.id.btn_mute:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_MUTE.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_MUTE.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_HOME.getKeyId());
                }
                break;

            case R.id.btn_up:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_UP.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_UP.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_UP.getKeyId());
                }
                break;

            case R.id.btn_down:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_DOWN.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_DOWN.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_DOWN.getKeyId());
                }
                break;

            case R.id.btn_left:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_LEFT.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_LEFT.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_LEFT.getKeyId());
                }
                break;

            case R.id.btn_right:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_RIGHT.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_RIGHT.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_RIGHT.getKeyId());
                }
                break;

            case R.id.btn_ok:
                if (databaseType == DatabaseType.STB) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.STB_OK.getKeyId());
                } else if (databaseType == DatabaseType.TV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.TV_DONE.getKeyId());
                } else if (databaseType == DatabaseType.IPTV) {
                    getKeyAndSendCtrl(fileIdList.get(index), DBRCKeyType.IPTV_OK.getKeyId());
                }
                break;

            case R.id.btn_previous:
            case R.id.btn_previous1: {
                if (index > 0) {
                    index--;
                }

                fileId = Integer.parseInt(fileIdList.get(index));
//                getKeyAndSendCtrl(fileIdList.get(index), 0);
                String text = currentBrandName + String.format(Locale.ENGLISH, "%d/%d", index + 1, maxIndex + 1);
                tvTitle.setText(text);
            }
            break;

            case R.id.btn_next:
            case R.id.btn_next1: {
                if (index < maxIndex) {
                    index++;
                }

                fileId = Integer.parseInt(fileIdList.get(index));
//                getKeyAndSendCtrl(fileIdList.get(index), 0);
                String text = currentBrandName + String.format(Locale.ENGLISH, "%d/%d", index + 1, maxIndex + 1);
                tvTitle.setText(text);
            }
            break;
        }
    }

    /**
     * 获取云码库的key_id, 然后再发码测试
     * @param fileId 遥控器id
     * @param keyId 中间层遥控类型
     */
    private void getKeyAndSendCtrl(final String fileId, final int keyId) {

        AcStateInfo acStateInfo = null;
        if(databaseType == DatabaseType.AC){
            acStateInfo = new AcStateInfo(true,1,26,0,1);//默认一个空调状态，开-制冷-温度26-风速0-风向1
        }
        ApiManager.getInstance().testDataBaseDeviceWithMd5(md5,databaseType,fileId,acStateInfo,keyId);
    }


    private void setupView() {
        if (fileIdList.size() > 0) {
            index = 0;
            maxIndex = fileIdList.size() - 1;
            fileId = Integer.parseInt(fileIdList.get(index));

            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            switch (databaseType) {
                case AC:
                    layout = layout2;
                    tvTitle = tvTitle1;
                    btnPre = btnPre1;
                    btnNext = btnNext1;
                    layout.setVisibility(View.VISIBLE);
                    irLibName = context.getString(R.string.text_ac);
                    break;

                case TV:
                    layout = layout1;
                    tvTitle = tvTitle2;
                    btnPre = btnPre2;
                    btnNext = btnNext2;
                    layout.setVisibility(View.VISIBLE);
                    btnAV_TV.setVisibility(View.VISIBLE);
                    irLibName = context.getString(R.string.text_tv);
                    break;

                case STB:
                    layout = layout1;
                    tvTitle = tvTitle2;
                    btnPre = btnPre2;
                    btnNext = btnNext2;
                    layout.setVisibility(View.VISIBLE);
                    btnAV_TV.setVisibility(View.GONE);
                    irLibName = context.getString(R.string.text_stb);
                    break;

                case IPTV:
                    layout = layout1;
                    tvTitle = tvTitle2;
                    btnPre = btnPre2;
                    btnNext = btnNext2;
                    layout.setVisibility(View.VISIBLE);
                    btnAV_TV.setVisibility(View.GONE);
                    irLibName = context.getString(R.string.text_iptv);
                    break;
            }

            String title = currentBrandName + String.format(Locale.ENGLISH,"%d/%d", index + 1, maxIndex + 1);
            tvTitle.setText(title);

            btnAgain.setOnClickListener(this);
            btnAV_TV.setOnClickListener(this);
            btnSwitch.setOnClickListener(this);
            btnMute.setOnClickListener(this);
            btnUp.setOnClickListener(this);
            btnDown.setOnClickListener(this);
            btnLeft.setOnClickListener(this);
            btnRight.setOnClickListener(this);
            btnOK.setOnClickListener(this);
            btnPre.setOnClickListener(this);
            btnNext.setOnClickListener(this);
        }
    }


    //前往添加界面
    private void startBindDeviceAty(int subType, int fileId, String name) {
        SubDevInfo subDevInfo = new SubDevInfo(0, DeviceMainType.DATABASE,subType,0,fileId, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");
        ApiManager.getInstance().setSubDeviceWithMd5(md5, subDevInfo, ActionFullType.INSERT);
    }


    @Override
    public void onGetDBRCBrandFlieIds(StateType state, String md5, ArrayList<String> flieIdList) {
        this.fileIdList = flieIdList;
        setupView();
    }

    @Override
    public void onSetSubDevice(StateType state, String md5, ActionFullType action, SubDevInfo subInfo) {
        if(state == StateType.OK){
            finish();
        }
    }
}
