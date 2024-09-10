/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

/**
 * 步频详情数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StepCadenceDetailData extends BaseDetailData {
    private int cadence;

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }
}
