/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.helper;

import android.content.Context;
import android.util.Log;

import com.hihonor.healthkitdemo.data.SportRidingData;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.ThreadPool;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.data.SampleRecord;
import com.hihonor.mcs.fitness.health.data.SummaryData;
import com.hihonor.mcs.fitness.health.datastore.InsertResponse;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.HeartRateField;
import com.hihonor.mcs.fitness.health.datastruct.LocationField;
import com.hihonor.mcs.fitness.health.datastruct.PaceInfoField;
import com.hihonor.mcs.fitness.health.datastruct.RidingField;
import com.hihonor.mcs.fitness.health.datastruct.SpeedField;
import com.hihonor.mcs.fitness.health.datastruct.WalkingField;
import com.hihonor.mcs.fitness.health.exception.HealthKitException;
import com.hihonor.mcs.fitness.health.task.OnFailureListener;
import com.hihonor.mcs.fitness.health.task.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 骑行帮助类 获取骑行数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportRindingHelper {
    private static final String TAG = SportRindingHelper.class.getSimpleName();

    private Context mContext;

    private long mStartTime;

    private long mEndTime;

    private Map<Long, SportRidingData> rindingDataMap = new HashMap<>();

    private ISportResultHelper mResult;

    public SportRindingHelper(Context context) {
        mContext = context;
    }

    public void setSportResult(ISportResultHelper sportResult) {
        this.mResult = sportResult;
    }

    public void setRequestTime(long startTime, long endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public Map<Long, SportRidingData> getRindingDataMap() {
        return rindingDataMap;
    }

    /**
     * 获取骑行数据
     */
    public void getRindingData() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                requestRindingData();
            }
        });
    }

    private void requestRindingData() {
        QueryRequest queryRequest = new QueryRequest(DataType.RECORD_RIDING, mStartTime, mEndTime);
        HealthKit.getDataStoreClient(mContext)
            .querySampleRecord(Utils.getHonorSignInAccount(), queryRequest)
            .addOnSuccessListener(sampleDataQueryResponse -> {
                List<SampleRecord> dataList = sampleDataQueryResponse.getDataList();
                if (dataList == null || dataList.isEmpty()) {
                    if (mResult != null) {
                        mResult.onSuccess(DataType.RECORD_RIDING);
                    }
                    return;
                }
                for (SampleRecord record : dataList) {
                    if (record == null) {
                        continue;
                    }
                    SummaryData summary = record.getSummary();
                    if (summary == null) {
                        continue;
                    }
                    SportRidingData ridingData = new SportRidingData();
                    ridingData.setStartTime(record.getStartTime());
                    ridingData.setEndTime(record.getEndTime());
                    if (summary.getInteger(RidingField.FIELD_DISTANCE_NAME) != null) {
                        ridingData.setDistance(summary.getInteger(WalkingField.FIELD_DISTANCE_NAME));
                    }
                    if (summary.getInteger(RidingField.FIELD_SPORT_TIME_NAME) != null) {
                        ridingData.setSportTime(summary.getInteger(WalkingField.FIELD_SPORT_TIME_NAME));
                    }
                    if (summary.getFloat(RidingField.FIELD_CALORIE_NAME) != null) {
                        ridingData.setCalorie(summary.getFloat(WalkingField.FIELD_CALORIE_NAME));
                    }
                    if (summary.getFloat(RidingField.FIELD_SPEED_NAME) != null) {
                        ridingData.setSpeed(summary.getFloat(WalkingField.FIELD_SPEED_NAME));
                    }
                    if (summary.getInteger(RidingField.FIELD_AVG_HEART_RATE_NAME) != null) {
                        ridingData.setAvgHeartRate(summary.getInteger(WalkingField.FIELD_AVG_HEART_RATE_NAME));
                    }
                    Map<Integer, List<SampleData>> detailMap = record.getDetailMap();
                    if (detailMap != null) {
                        SportHelperUtils helperUtils = new SportHelperUtils();
                        for (Map.Entry<Integer, List<SampleData>> entry : detailMap.entrySet()) {
                            int key = entry.getKey();
                            List<SampleData> list = entry.getValue();
                            if (list == null || list.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case DataType.SAMPLE_SPEED:
                                    ridingData.setSpeedDetails(helperUtils.parseSpeedInfo(list));
                                    break;
                                case DataType.SAMPLE_LOCATION:
                                    ridingData.setLocationDetails(helperUtils.parseLocationInfo(list));
                                    break;
                                case DataType.SAMPLE_HEART_RATE:
                                    ridingData.setHeartRateDetails(helperUtils.parseHeartRateInfo(list));
                                    break;
                                default:
                                    break;
                            }
                        }
                        rindingDataMap.put(ridingData.getStartTime(), ridingData);
                    }
                }
                boolean isTotal = (sampleDataQueryResponse.getTotal()) == (sampleDataQueryResponse.getIndex() + 1);
                if (mResult != null && isTotal) {
                    mResult.onSuccess(DataType.RECORD_RIDING);
                }
            })
            .addOnFailureListener(e -> {
                LogUtil.e(TAG, " onFail e: " + e);
                if (mResult != null) {
                    mResult.onFail(DataType.RECORD_RIDING, e);
                }
            });
    }

    public void insertData() {
        // 设置运动记录信息，开始时间StartTime和结束时间EndTime，为毫米级时间戳。
        long endTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        long startTime = endTime - TimeUnit.MINUTES.toMillis(6);
        SummaryData summaryData = new SummaryData();
        summaryData.putInteger(RidingField.FIELD_DISTANCE_NAME, 4100);
        summaryData.putInteger(RidingField.FIELD_SPORT_TIME_NAME, 600);
        summaryData.putFloat(RidingField.FIELD_CALORIE_NAME, 30.0f);
        summaryData.putFloat(RidingField.FIELD_SPEED_NAME, 6.3f);
        summaryData.putInteger(RidingField.FIELD_AVG_HEART_RATE_NAME, 88);

        SampleRecord sampleRecord = new SampleRecord(DataType.RECORD_RIDING, startTime, endTime);
        sampleRecord.setSummary(summaryData);

        List<SampleData> speedDataList = new ArrayList<>();
        List<SampleData> locationDataList = new ArrayList<>();
        List<SampleData> heartRateDataList = new ArrayList<>();
        List<SampleData> paceInfoDataList = new ArrayList<>();

        long associationStartTime = endTime - TimeUnit.MINUTES.toMillis(5);
        long associationEndTime = associationStartTime + TimeUnit.SECONDS.toMillis(5);

        // 设置速度关联信息。
        SampleData speedSampleData = new SampleData();
        speedSampleData.setDataType(DataType.SAMPLE_SPEED);
        speedSampleData.setStartTime(associationStartTime);
        speedSampleData.setEndTime(associationEndTime);
        speedSampleData.putFloat(SpeedField.FIELD_SPEED_NAME, 6.0f);
        speedDataList.add(speedSampleData);

        // 设置位置关联信息。
        long locationStartTime = ((endTime - TimeUnit.MINUTES.toMillis(5)));
        long locationEndTime = associationStartTime + TimeUnit.SECONDS.toMillis(1);
        SampleData locationSampleData = new SampleData();
        locationSampleData.setDataType(DataType.SAMPLE_LOCATION);
        locationSampleData.setStartTime(locationStartTime);
        locationSampleData.setEndTime(locationEndTime);
        locationSampleData.putDouble(LocationField.FIELD_LATITUDE_NAME, 85.3);
        locationSampleData.putDouble(LocationField.FIELD_LONGITUDE_NAME, 46.4);
        locationSampleData.putFloat(LocationField.FIELD_PRECISION_NAME, 2);
        locationSampleData.putFloat(LocationField.FIELD_SPEED_NAME, 1);
        locationSampleData.putInteger(LocationField.FIELD_GPS_TRA_NAME, 0);
        locationDataList.add(locationSampleData);

        // 每公里配速
        SampleData paceInfo = new SampleData();
        paceInfo.setDataType(DataType.SAMPLE_PACE_INFO);
        paceInfo.putInteger(PaceInfoField.FIELD_INDEX_NAME, 1);
        paceInfo.putFloat(PaceInfoField.FIELD_DISTANCE_NAME, 1000f);
        paceInfo.putInteger(PaceInfoField.FIELD_TAKEUP_TIME_NAME, 300);
        paceInfo.setStartTime(associationStartTime);
        paceInfo.setEndTime(associationEndTime);
        paceInfoDataList.add(paceInfo);

        // 设置心率关联信息。
        SampleData heartRateSampleData = new SampleData();
        heartRateSampleData.setDataType(DataType.SAMPLE_HEART_RATE);
        heartRateSampleData.setStartTime(associationStartTime);
        heartRateSampleData.setEndTime(associationEndTime);
        heartRateSampleData.putInteger(HeartRateField.FIELD_DYNAMIC_HEART_RATE_NAME, 136);
        heartRateSampleData.putInteger(HeartRateField.FIELD_RESTING_HEART_RATE_NAME, 78);
        heartRateDataList.add(heartRateSampleData);

        sampleRecord.putDetail(DataType.SAMPLE_SPEED, speedDataList);
        sampleRecord.putDetail(DataType.SAMPLE_LOCATION, locationDataList);
        sampleRecord.putDetail(DataType.SAMPLE_HEART_RATE, heartRateDataList);
        sampleRecord.putDetail(DataType.SAMPLE_PACE_INFO, paceInfoDataList);

        // 构建运动记录数据列表sampleRecordList。
        List<SampleRecord> sampleRecordList = new ArrayList<>();
        sampleRecordList.add(sampleRecord);

        // 调用insertSampleData接口， 将步数数据插入到Health Kit中
        HealthKit.getDataStoreClient(mContext).insertSampleRecord(Utils.getHonorSignInAccount(), sampleRecordList)
                .addOnSuccessListener(new OnSuccessListener<InsertResponse>() {
                    @Override
                    public void onSuccess(InsertResponse insertResponse) {
                        // 插入数据成功回调方法，insertResponse包含插入成功数据的id。
                        Log.i(TAG, "onSuccess: 写入成功");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // 插入数据失败相应的处理逻辑。
                        int errorCode = ((HealthKitException) e).getErrorCode();
                        String errorMsg = e.getMessage();
                        Log.i(TAG, "onSuccess: 写入失败 ： " + errorCode + " --- errorMsg : " + errorMsg);
                    }
                });
    }
}