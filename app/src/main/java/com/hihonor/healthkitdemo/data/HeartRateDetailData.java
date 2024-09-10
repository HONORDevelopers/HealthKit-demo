/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

/**
 * 心率信息
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class HeartRateDetailData extends BaseDetailData {
    private int dynamicHeartRate;

    private int restingHeartRate;

    public int getDynamicHeartRate() {
        return dynamicHeartRate;
    }

    public void setDynamicHeartRate(int dynamicHeartRate) {
        this.dynamicHeartRate = dynamicHeartRate;
    }

    public int getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(int restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }
}
