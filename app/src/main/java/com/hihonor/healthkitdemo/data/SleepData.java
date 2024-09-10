/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepData implements Serializable {
    private long startTime;
    private long endTime;
    private int deepSleep;
    private int lightSleep;
    private int remSleep;
    private int wideAwake;
    private int awakeTimes;
    private int sleepType;
    private int sporadicNaps;
    private int sleepQuality;
    private int respiratoryQuality;
    private int deepSleepContinue;
    private String sleepTimestamp;
    private String wakeupTimestamp;

    private List<SleepSegmentData> nightSegmentList = new ArrayList<>(16);

    private List<SleepSegmentData> napSegmentList = new ArrayList<>(16);


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

    public int getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public int getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(int lightSleep) {
        this.lightSleep = lightSleep;
    }

    public int getRemSleep() {
        return remSleep;
    }

    public void setRemSleep(int remSleep) {
        this.remSleep = remSleep;
    }

    public int getWideAwake() {
        return wideAwake;
    }

    public void setWideAwake(int wideAwake) {
        this.wideAwake = wideAwake;
    }

    public int getAwakeTimes() {
        return awakeTimes;
    }

    public void setAwakeTimes(int awakeTimes) {
        this.awakeTimes = awakeTimes;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }

    public int getSporadicNaps() {
        return sporadicNaps;
    }

    public void setSporadicNaps(int sporadicNaps) {
        this.sporadicNaps = sporadicNaps;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public int getRespiratoryQuality() {
        return respiratoryQuality;
    }

    public void setRespiratoryQuality(int respiratoryQuality) {
        this.respiratoryQuality = respiratoryQuality;
    }

    public int getDeepSleepContinue() {
        return deepSleepContinue;
    }

    public void setDeepSleepContinue(int deepSleepContinue) {
        this.deepSleepContinue = deepSleepContinue;
    }

    public String getSleepTimestamp() {
        return sleepTimestamp;
    }

    public void setSleepTimestamp(String sleepTimestamp) {
        this.sleepTimestamp = sleepTimestamp;
    }

    public String getWakeupTimestamp() {
        return wakeupTimestamp;
    }

    public void setWakeupTimestamp(String wakeupTimestamp) {
        this.wakeupTimestamp = wakeupTimestamp;
    }

    public List<SleepSegmentData> getNightSegmentList() {
        return nightSegmentList;
    }

    public void setNightSegmentList(List<SleepSegmentData> nightSegmentList) {
        this.nightSegmentList = nightSegmentList;
    }

    public List<SleepSegmentData> getNapSegmentList() {
        return napSegmentList;
    }

    public void setNapSegmentList(List<SleepSegmentData> napSegmentList) {
        this.napSegmentList = napSegmentList;
    }

    public String getStartTimeDataStamp() {
        return TimeUtils.timestamp2Date(startTime, "yyyy-MM-dd");
    }

    public String getEndTimeDataStamp() {
        return TimeUtils.timestamp2Date(endTime, "yyyy-MM-dd");
    }
}
