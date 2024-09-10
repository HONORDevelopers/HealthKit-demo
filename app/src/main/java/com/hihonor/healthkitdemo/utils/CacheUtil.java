/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存数据工具类
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class CacheUtil {
    /**
     * 用于存储上次授权并登录时间
     */
    public static final String KEY_LAST_AUTH_TIME = "last_auth_time";

    public static final int MODE_PRIVATE = 0;

    /**
     * 授权并登录失效间隔时间
     */
    private static final long AUTH_INTERVAL = 60 * 60 * 1000L;

    private static SharedPreferences mSp;

    private static SharedPreferences getPreferences(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        return mSp;
    }

    /**
     * 存储loog型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = getPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 读取long型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defvalue 默认值
     * @return 值
     */
    public static long getLong(Context context, String key, long defvalue) {
        SharedPreferences sp = getPreferences(context);
        return sp.getLong(key, defvalue);
    }

    /**
     * 授权是否失效
     *
     * @param context 上下文
     * @return true/false
     */
    public static boolean isAuthInvalid(Context context) {
        long lastAuthTime = getLong(context, KEY_LAST_AUTH_TIME, 0);
        if (lastAuthTime == 0) {
            return true;
        }
        return System.currentTimeMillis() - lastAuthTime > AUTH_INTERVAL;
    }
}
