package com.example.helloworld.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {


    public static String formatWeek(byte currentDayOfWeek, Context context) {
        if (currentDayOfWeek == 127) {
            return "每天";
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
            return "工作日";
        } else if (!monday && !tuesday && !wednesday && !thursday && !friday && satuarday && sunday) {
            return "周末";
        } else if (!monday && !tuesday && !wednesday && !thursday && !friday && !satuarday && !sunday) {
            return "一次";
        } else {
            StringBuffer sb = new StringBuffer();
            if (monday) {
                sb.append("周一").append(" ");
            }
            if (tuesday) {
                sb.append("周二").append(" ");
            }
            if (wednesday) {
                sb.append(("周三")).append(" ");
            }
            if (thursday) {
                sb.append(("周四")).append(" ");
            }
            if (friday) {
                sb.append(("周五")).append(" ");
            }
            if (satuarday) {
                sb.append(("周六")).append(" ");
            }
            if (sunday) {
                sb.append(("周日"));
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


    /**
     * 秒数转时间
     * */
    public static String formatDateFromSeconds(String seconds){
        if(seconds==null) {
            return " ";
        } else{
            Date date=new Date();
            try{
                date.setTime(Long.parseLong(seconds)*1000);
            }catch(NumberFormatException nfe){

            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }
    }
}
