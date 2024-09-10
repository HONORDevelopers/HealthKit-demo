/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.io.Serializable;

/**
 * 睡眠详情数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepSegmentData implements Serializable {
    private long startTime;
    private long endTime;
    private int duration;
    private int status;
    private int dataType;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDetailStartTimeFormat() {
        return TimeUtils.timestamp2Date(startTime, null);
    }

    public String getDetailEndTimeFormat() {
        return TimeUtils.timestamp2Date(endTime, null);
    }

}
