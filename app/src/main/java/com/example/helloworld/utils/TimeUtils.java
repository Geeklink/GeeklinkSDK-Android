package com.example.helloworld.utils;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;

import com.example.helloworld.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {


    public static String formatWeek(byte currentDayOfWeek, Context context) {
        if (currentDayOfWeek == 127) {
            return context.getString(R.string.text_every_day);
        }
        boolean monday = false;
        boolean tuesday = false;
        boolean wednesday = false;
        boolean thursday = false;
        boolean friday = false;
        boolean satuarday = false;
        boolean sunday = false;

        if ((byte) (currentDayOfWeek & 0x01) == (byte) 0x01) {
            monday = true;
        }
        if ((byte) (currentDayOfWeek & (byte) (1 << 1)) == (byte) (1 << 1)) {
            tuesday = true;
        }

        if ((byte) (currentDayOfWeek & (byte) (1 << 2)) == (byte) (1 << 2)) {
            wednesday = true;
        }

        if ((byte) (currentDayOfWeek & (byte) (1 << 3)) == (byte) (1 << 3)) {
            thursday = true;
        }

        if ((byte) (currentDayOfWeek & (byte) (1 << 4)) == (byte) (1 << 4)) {
            friday = true;
        }

        if ((byte) (currentDayOfWeek & (byte) (1 << 5)) == (byte) (1 << 5)) {
            satuarday = true;
        }

        if ((byte) (currentDayOfWeek & (byte) (1 << 6)) == (byte) (1 << 6)) {
            sunday = true;
        }
        if (monday && tuesday && wednesday && thursday && friday && !satuarday && !sunday) {
            return context.getString(R.string.text_work_day);
        } else if (!monday && !tuesday && !wednesday && !thursday && !friday && satuarday && sunday) {
            return context.getString(R.string.text_weekend);
        } else if (!monday && !tuesday && !wednesday && !thursday && !friday && !satuarday && !sunday) {
            return context.getString(R.string.text_once_time);
        } else {
            StringBuffer sb = new StringBuffer();
            if (monday) {
                sb.append(context.getString(R.string.text_mon)).append(" ");
            }
            if (tuesday) {
                sb.append(context.getString(R.string.text_tues)).append(" ");
            }
            if (wednesday) {
                sb.append(context.getString(R.string.text_weds)).append(" ");
            }
            if (thursday) {
                sb.append(context.getString(R.string.text_thurs)).append(" ");
            }
            if (friday) {
                sb.append(context.getString(R.string.text_fri)).append(" ");
            }
            if (satuarday) {
                sb.append(context.getString(R.string.text_satur)).append(" ");
            }
            if (sunday) {
                sb.append(context.getString(R.string.text_sun));
            }

            return sb.toString();
        }
    }



    /**
     * 获取时间字符串
     * @param minutes 分钟数  单位：分钟
     * @return
     */
    public static String formatTime(int minutes) {
        StringBuffer sb = new StringBuffer();
        if (minutes / 60 < 10) {
            if (minutes % 60 < 10) {
                sb.append("0")
                        .append(minutes / 60)
                        .append(":")
                        .append("0")
                        .append(minutes % 60);
            } else {
                sb.append("0")
                        .append(minutes / 60)
                        .append(":")
                        .append(minutes % 60);
            }
        } else {
            if (minutes % 60 < 10) {
                sb.append(minutes / 60)
                        .append(":")
                        .append("0")
                        .append(minutes % 60);
            } else {
                sb.append(minutes / 60)
                        .append(":")
                        .append(minutes % 60);
            }
        }
        return sb.toString();
    }

}
