/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

import static java.util.regex.Pattern.compile;

import com.hihonor.mcs.fitness.health.data.HonorSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class Utils {
    public static HonorSignInAccount getHonorSignInAccount() {
        // 此处需要实现荣耀账号接入，获取用户 Id 和 accessToken
        return new HonorSignInAccount("appId", "userId", "accessToken");
    }

    /**
     * 提取请求错误码信息
     *
     * @param exp 错误信息
     * @return 错误码
     */
    public static String parseErrorInfo(Exception exp) {
        if (exp == null) {
            return "";
        }
        if (exp.getMessage() == null || exp.getMessage().isEmpty()) {
            return "";
        }
        try {
            JSONObject jsonObject = new JSONObject(exp.getMessage());
            return jsonObject.getString("desc");
        } catch (JSONException jsonException) {
            return "";
        }
    }

    /**
     * 判断字符串中是否只包含数字
     *
     * @param str 字符串
     * @return true:字符串中只包含数字
     */
    public static boolean isValidNumber(String str) {
        // 使用正则表达式 ^[0-9]*$ 匹配空字符串或仅包含数字的字符串
        Pattern pattern = compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);

        // 如果字符串匹配正则表达式，则返回 true；否则返回 false
        return matcher.matches();
    }
}
