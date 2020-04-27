package com.example.helloworld.utils;

import android.content.Context;

import com.example.helloworld.R;
import com.gl.CustomType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;

public class DeviceUtil {
    public static String getDeviceType(Context context,DeviceMainType mainType, int subType){
        String type = "";
        switch (mainType){
            case DATABASE:
                switch (DatabaseType.values()[subType]){
                    case AC:
                        type = context.getString(R.string.text_ac);
                        break;
                    case TV:
                        type = context.getString(R.string.text_tv);
                        break;
                    case STB:
                        type = context.getString(R.string.text_stb);
                        break;
                    case IPTV:
                        type = context.getString(R.string.text_iptv);
                        break;
                }
                break;
            case CUSTOM:
                switch (CustomType.values()[subType]){
                    case TV:
                        type = context.getString(R.string.text_tv);
                        break;
                    case STB:
                        type = context.getString(R.string.text_stb);
                        break;
                    case IPTV:
                        type = context.getString(R.string.text_iptv);
                        break;
                    case CUSTOM:
                        type = context.getString(R.string.text_custom);
                        break;
                }
                break;
        }
        return type;
    }
}
