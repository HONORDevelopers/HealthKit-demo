/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.data;

/**
 * 运动记录概要页面显示数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SummaryItemData {
    private String name;

    private String value;

    private String unit;

    public SummaryItemData(String name, int value, String unit) {
        this.name = name;
        this.value = String.valueOf(value);
        this.unit = unit;
    }

    public SummaryItemData(String name, float value, String unit) {
        this.name = name;
        this.value = String.valueOf(value);
        this.unit = unit;
    }

    public SummaryItemData(String name, double value, String unit) {
        this.name = name;
        this.value = String.valueOf(value);
        this.unit = unit;
    }

    public SummaryItemData(String name, String value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
