package com.example.helloworld.utils;

import com.example.helloworld.R;
import com.gl.CustomType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;

public class DeviceUtil {
    public static String getDeviceType(DeviceMainType mainType,int subType){
        String type = "";
        switch (mainType){
            case DATABASE:
                switch (DatabaseType.values()[subType]){
                    case AC:
                        type = "空调（码库）";
                        break;
                    case TV:
                        type = "电视（码库）";
                        break;
                    case STB:
                        type = "机顶盒（码库）";
                        break;
                    case IPTV:
                        type = "安卓盒子（码库）";
                        break;
                }
                break;
            case CUSTOM:
                switch (CustomType.values()[subType]){
                    case TV:
                        type = "电视（自学习）";
                        break;
                    case STB:
                        type = "机顶盒（自学习）";
                        break;
                    case IPTV:
                        type = "安卓盒子（自学习）";
                        break;
                    case FAN:
                        type = "风扇";
                        break;
                    case CURTAIN:
                        type = "窗帘";
                        break;
                    case SOUNDBOX:
                        type = "音响";
                        break;
                    case RC_LIGHT:
                        type = "调光灯";
                        break;
                    case AC_FAN:
                        type = "空调扇";
                        break;
                    case PROJECTOR:
                        type = "投影仪";
                        break;
                    case AIR_PURIFIER:
                        type = "空气净化器";
                        break;
                    case ONE_KEY:
                        type = "一键遥控";
                        break;
                    case CUSTOM:
                        type = "自定义";
                        break;
                }
                break;
        }
        return type;
    }
}
