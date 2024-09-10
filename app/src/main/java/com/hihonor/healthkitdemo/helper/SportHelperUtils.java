/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.helper;

import com.hihonor.healthkitdemo.data.HeartRateDetailData;
import com.hihonor.healthkitdemo.data.LocationDetailData;
import com.hihonor.healthkitdemo.data.SpeedDetailData;
import com.hihonor.healthkitdemo.data.StepCadenceDetailData;
import com.hihonor.mcs.fitness.health.data.AtomicPointValue;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.HeartRateField;
import com.hihonor.mcs.fitness.health.datastruct.LocationField;
import com.hihonor.mcs.fitness.health.datastruct.SpeedField;
import com.hihonor.mcs.fitness.health.datastruct.StepRateField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 解析运动详情数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportHelperUtils {
    public List<HeartRateDetailData> parseHeartRateInfo(List<SampleData> list) {
        List<HeartRateDetailData> detailInfoList = new ArrayList<>();

        for (SampleData sampleData : list) {
            HeartRateDetailData heartRateDetail = new HeartRateDetailData();
            heartRateDetail.setDataType(sampleData.getDataType());
            heartRateDetail.setStartTime(sampleData.getStartTime());
            heartRateDetail.setEndTime(sampleData.getEndTime());

            Map<String, AtomicPointValue> stepDetailMap = sampleData.getDataMap();
            for (Map.Entry<String, AtomicPointValue> detailEntry : stepDetailMap.entrySet()) {
                String detailKey = detailEntry.getKey();

                if (detailKey.equals(HeartRateField.FIELD_DYNAMIC_HEART_RATE_NAME)) {
                    heartRateDetail.setDynamicHeartRate(detailEntry.getValue().getInteger());
                } else if (detailKey.equals(HeartRateField.FIELD_RESTING_HEART_RATE_NAME)) {
                    heartRateDetail.setRestingHeartRate(detailEntry.getValue().getInteger());
                }
            }
            detailInfoList.add(heartRateDetail);
        }

        return detailInfoList;
    }

    public List<SpeedDetailData> parseSpeedInfo(List<SampleData> list) {
        List<SpeedDetailData> detailList = new ArrayList<>();

        for (SampleData sampleData : list) {
            SpeedDetailData detailData = new SpeedDetailData();
            detailData.setDataType(sampleData.getDataType());
            detailData.setStartTime(sampleData.getStartTime());
            detailData.setEndTime(sampleData.getEndTime());
            Map<String, AtomicPointValue> stepDetailMap = sampleData.getDataMap();
            for (Map.Entry<String, AtomicPointValue> detailEntry : stepDetailMap.entrySet()) {
                String detailKey = detailEntry.getKey();
                if (detailKey.equals(SpeedField.FIELD_SPEED_NAME)) {
                    detailData.setSpeed(detailEntry.getValue().getFloat());
                }
            }
            detailList.add(detailData);
        }

        return detailList;
    }

    public List<StepCadenceDetailData> parseStepCadenceInfo(List<SampleData> list) {
        List<StepCadenceDetailData> detailList = new ArrayList<>();

        for (SampleData sampleData : list) {
            StepCadenceDetailData detailData = new StepCadenceDetailData();
            detailData.setDataType(sampleData.getDataType());
            detailData.setStartTime(sampleData.getStartTime());
            detailData.setEndTime(sampleData.getEndTime());
            Map<String, AtomicPointValue> stepDetailMap = sampleData.getDataMap();
            for (Map.Entry<String, AtomicPointValue> detailEntry : stepDetailMap.entrySet()) {
                String detailKey = detailEntry.getKey();
                if (detailKey.equals(StepRateField.FIELD_STEP_RATE_NAME)) {
                    detailData.setCadence(detailEntry.getValue().getInteger());
                }
            }
            detailList.add(detailData);
        }

        return detailList;
    }

    public List<LocationDetailData> parseLocationInfo(List<SampleData> list) {
        List<LocationDetailData> detailList = new ArrayList<>();

        for (SampleData sampleData : list) {
            LocationDetailData detailData = new LocationDetailData();
            detailData.setDataType(sampleData.getDataType());
            detailData.setStartTime(sampleData.getStartTime());
            detailData.setEndTime(sampleData.getEndTime());
            Map<String, AtomicPointValue> stepDetailMap = sampleData.getDataMap();
            for (Map.Entry<String, AtomicPointValue> detailEntry : stepDetailMap.entrySet()) {
                String detailKey = detailEntry.getKey();
                if (detailKey.equals(LocationField.FIELD_LATITUDE_NAME)) {
                    detailData.setLatitude(detailEntry.getValue().getDouble());
                }
                if (detailKey.equals(LocationField.FIELD_LONGITUDE_NAME)) {
                    detailData.setLongitude(detailEntry.getValue().getDouble());
                }
                if (detailKey.equals(LocationField.FIELD_PRECISION_NAME)) {
                    detailData.setPrecision(detailEntry.getValue().getFloat());
                }
                if (detailKey.equals(LocationField.FIELD_GPS_TRA_NAME)) {
                    detailData.setGpsTra(detailEntry.getValue().getInteger());
                }
            }
            detailList.add(detailData);
        }

        return detailList;
    }
}
