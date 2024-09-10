/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

import android.content.Context;
import android.util.Log;

import com.hihonor.healthkitdemo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class TimeUtils {
    private static final int DEFAULT_ZERO = 0;

    private static final int DEFAULT_HOUR = 23;

    private static final int DEFAULT_MINUTES_OR_SECONDS = 59;

    private static final int DEFAULT_MAX_MILLISECOND = 999;
    /**
     * 日期转换为时间戳
     *
     * @param dateString 日期
     * @param format 日期格式
     * @return 时间戳
     */
    public static long date2TimeStamp(String dateString, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss SSS";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateString).getTime();
        } catch (ParseException e) {
            Log.e("TimeUtils", "parse date failed");
        }
        return 0;
    }

    /**
     * 时间戳转换为日期
     *
     * @param time 时间戳
     * @param format 日期格式
     * @return 日期
     */
    public static String timestamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss SSS";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    public static String timestamp2DateMinute(long time) {
        return timestamp2Date(time, "yyyy-MM-dd HH:mm");
    }

    public static String timestamp2DateSecond(long time) {
        return timestamp2Date(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将秒转换成HH:mm:ss格式
     *
     * @param time 秒数
     * @return HH:mm:ss格式字符串
     */
    public static String covert2HourTime(long time) {
        long second = time  % 60;
        long minute= time / 60 % 60;
        long hour =  time / 60 / 60;

        return (((hour > 9) ? hour : ("0" + hour)) + ":"
                + ((minute > 9) ? minute : ("0" + minute)) + ":"
                + ((second > 9) ? second : ("0" + second)));
    }

    public static String formatDurationFromMinutes(Context context, int duration) {
        if (duration == 0) {
            return "--";
        }

        int hours = duration / 60;
        int minutes = duration % 60;

        return ((hours != 0) ? (hours + context.getResources().getString(R.string.time_format_hour)) : "")
            + ((minutes != 0) ? (minutes + context.getResources().getString(R.string.time_format_minute)) : "");
    }

    /**
     * 将秒转换成HH:mm:ss格式
     *
     * @param time 秒数
     * @return HH:mm:ss格式字符串
     */
    public static String covert2Hour(long time) {

        long minute= time / 60 % 60;
        long hour =  time / 60 / 60;

        return (((hour > 9) ? hour : ("0" + hour)) + ":"
                + ((minute > 9) ? minute : ("0" + minute)));
    }

    public static String formatDurationFromSecond(Context context, int duration) {
        if (duration == 0) {
            return "--";
        }

        int seconds = duration % 60;
        int minutes = duration / 60;
        int hours = duration / 60 / 60;

        return (hours > 9 ? hours : ("0" + hours)) + context.getResources().getString(R.string.time_format_hour)
            + (minutes > 9 ? minutes : ("0" + minutes)) + context.getResources().getString(R.string.time_format_minute)
            + (seconds > 9 ? seconds : ("0" + seconds)) + context.getResources().getString(R.string.time_format_second);
    }

    /**
     * 获取指定时间戳所在日期的最小时间戳
     *
     * @param timeMillis 时间戳
     * @return 所在日期的最小时间戳
     */
    public static long getMinTimeMillis(long timeMillis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_ZERO);
        calendar.set(Calendar.MINUTE, DEFAULT_ZERO);
        calendar.set(Calendar.SECOND, DEFAULT_ZERO);
        calendar.set(Calendar.MILLISECOND, DEFAULT_ZERO);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定时间戳所在日期的最大时间戳
     *
     * @param timeMillis 时间戳
     * @return 所在日期的最大时间戳
     */
    public static long getMaxTimeMillis(long timeMillis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
        calendar.set(Calendar.MINUTE, DEFAULT_MINUTES_OR_SECONDS);
        calendar.set(Calendar.SECOND, DEFAULT_MINUTES_OR_SECONDS);
        calendar.set(Calendar.MILLISECOND, DEFAULT_MAX_MILLISECOND);
        return calendar.getTimeInMillis();
    }
}
