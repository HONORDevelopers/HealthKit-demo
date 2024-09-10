/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.helper;

/**
 * 运动记录请求结果接口帮助类
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public interface ISportResultHelper {
    /**
     * 成功
     *
     * @param type 类型
     */
    void onSuccess(int type);

    /**
     * 失败
     *
     * @param type 类型
     * @param e 异常信息
     */
    void onFail(int type, Exception e);
}
