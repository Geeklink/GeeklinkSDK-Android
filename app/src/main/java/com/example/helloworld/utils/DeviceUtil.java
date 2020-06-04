package com.example.helloworld.utils;

import android.text.TextUtils;

import com.gl.DatabaseDevType;
import com.gl.DeviceMainType;

public class DeviceUtil {
    public static String getDeviceType(DeviceMainType mainType, DatabaseDevType dataBaseType){
        String type = "";
        switch (mainType){
            case DATABASE_DEV:
                switch (dataBaseType){
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
                    default:
                        type = "安卓盒子（码库）";
                        break;
                }
                break;
            case CUSTOM_DEV:
                type = "自定义";
                break;
        }
        return type;
    }

    public static boolean hasNewerVersion(String version1, String version2) {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version1)) {
            return false;
        }
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        for (int i = 0; i < Math.max(v1.length, v2.length); i++) {
            int num1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
            int num2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;
            if (num1 < num2) {
                return true;
            }
        }
        return false;
    }
}
