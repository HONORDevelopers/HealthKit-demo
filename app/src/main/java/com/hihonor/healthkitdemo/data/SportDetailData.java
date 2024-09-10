/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

import java.io.Serializable;
import java.util.List;

/**
 * 运动详情数据
 *
 * @author lW0037320
 * @param <T> 具体运动数据类型
 * @since 2024-08-01
 */
public class SportDetailData<T> implements Serializable {
    private int dataType;

    private long startTime;

    private long endTime;

    private List<T> value;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<T> getValue() {
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }
}
