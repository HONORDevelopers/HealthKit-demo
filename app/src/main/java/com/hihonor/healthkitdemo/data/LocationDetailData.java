/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

/**
 * 位置信息
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class LocationDetailData extends BaseDetailData {
    private double latitude;

    private double longitude;

    private float speed;

    private float precision;

    private int gpsTra;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public int getGpsTra() {
        return gpsTra;
    }

    public void setGpsTra(int gpsTra) {
        this.gpsTra = gpsTra;
    }
}
