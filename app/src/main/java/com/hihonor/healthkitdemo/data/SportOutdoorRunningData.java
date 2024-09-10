/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

import com.hihonor.mcs.fitness.health.constants.DataType;

import java.io.Serializable;
import java.util.List;

/**
 * 户外跑步数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportOutdoorRunningData implements Serializable {
    private int sportType = DataType.RECORD_RUNNING_OUTDOOR;

    private String sportTypeName = "户外跑步";

    private long startTime;

    private long endTime;

    private int distance;

    private int sportTime;

    private float calorie;

    private float speed;

    private int stepCadence;

    private int steps;

    private int avgHeartRate;

    private float totalRiseHeight;

    private float totalDropHeight;

    private List<HeartRateDetailData> heartRateDetails;

    private List<SpeedDetailData> speedDetails;

    private List<StepCadenceDetailData> stepCadenceDetails;

    private List<LocationDetailData> locationDetails;

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

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getSportTime() {
        return sportTime;
    }

    public void setSportTime(int sportTime) {
        this.sportTime = sportTime;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getStepCadence() {
        return stepCadence;
    }

    public void setStepCadence(int stepCadence) {
        this.stepCadence = stepCadence;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public float getTotalRiseHeight() {
        return totalRiseHeight;
    }

    public void setTotalRiseHeight(float totalRiseHeight) {
        this.totalRiseHeight = totalRiseHeight;
    }

    public float getTotalDropHeight() {
        return totalDropHeight;
    }

    public void setTotalDropHeight(float totalDropHeight) {
        this.totalDropHeight = totalDropHeight;
    }

    public List<HeartRateDetailData> getHeartRateDetails() {
        return heartRateDetails;
    }

    public void setHeartRateDetails(List<HeartRateDetailData> heartRateDetails) {
        this.heartRateDetails = heartRateDetails;
    }

    public List<SpeedDetailData> getSpeedDetails() {
        return speedDetails;
    }

    public void setSpeedDetails(List<SpeedDetailData> speedDetails) {
        this.speedDetails = speedDetails;
    }

    public List<StepCadenceDetailData> getStepCadenceDetails() {
        return stepCadenceDetails;
    }

    public void setStepCadenceDetails(List<StepCadenceDetailData> stepCadenceDetails) {
        this.stepCadenceDetails = stepCadenceDetails;
    }

    public List<LocationDetailData> getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(List<LocationDetailData> locationDetails) {
        this.locationDetails = locationDetails;
    }
}
