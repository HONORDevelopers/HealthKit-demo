/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

/**
 * 运动记录页面显示数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportRecordData {
    private int sportType;

    private String sportTypeName;

    private long startTime;

    private String stampStartTime;

    private float distance;
    private int duration;
    private float calorie;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    public String getSportTypeName() {
        return sportTypeName;
    }

    public void setSportTypeName(String sportTypeName) {
        this.sportTypeName = sportTypeName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStampStartTime() {
        return stampStartTime;
    }

    public void setStampStartTime(String stampStartTime) {
        this.stampStartTime = stampStartTime;
    }
}
