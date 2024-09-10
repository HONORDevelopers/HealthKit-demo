/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

/**
 * 历史睡眠记录
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class AuthInfo {
    private static String appId;

    private static String userId;

    private static String accessToken;

    /**
     * 获取三方应用appId
     *
     * @return appId
     */
    public static String getAppId() {
        return appId;
    }

    /**
     * 设置三方应用appId
     *
     * @param appId appId
     */
    public static void setAppId(String appId) {
        AuthInfo.appId = appId;
    }

    /**
     * 获取三方应用userId
     *
     * @return userId
     */
    public static String getUserId() {
        return userId;
    }

    /**
     * 设置三方应用userId
     *
     * @param userId userId
     */
    public static void setUserId(String userId) {
        AuthInfo.userId = userId;
    }

    /**
     * 获取三方应用token
     *
     * @return token
     */
    public static String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置三方应用token
     *
     * @param accessToken token
     */
    public static void setAccessToken(String accessToken) {
        AuthInfo.accessToken = accessToken;
    }
}
