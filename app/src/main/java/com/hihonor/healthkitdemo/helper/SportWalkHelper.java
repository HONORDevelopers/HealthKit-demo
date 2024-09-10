/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.helper;

import android.content.Context;
import android.util.Log;

import com.hihonor.healthkitdemo.data.SportWalkData;
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
import com.hihonor.mcs.fitness.health.datastruct.SpeedField;
import com.hihonor.mcs.fitness.health.datastruct.StepRateField;
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
 * 步行帮助类 获取步行数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportWalkHelper {
    private static final String TAG = SportWalkHelper.class.getSimpleName();

    private Context mContext;

    private long mStartTime;

    private long mEndTime;

    private Map<Long, SportWalkData> walkDataMap = new HashMap<>();

    private ISportResultHelper mResult;

    public SportWalkHelper(Context context) {
        mContext = context;
    }

    public void setSportResult(ISportResultHelper sportResult) {
        this.mResult = sportResult;
    }

    public void setRequestTime(long startTime, long endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public Map<Long, SportWalkData> getWalkDataMap() {
        return walkDataMap;
    }

    /**
     * 获取步行数据
     */
    public void getWalkData() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                requestWalkData();
            }
        });
    }

    private void requestWalkData() {
        QueryRequest queryRequest = new QueryRequest(DataType.RECORD_WALKING, mStartTime, mEndTime);
        HealthKit.getDataStoreClient(mContext)
            .querySampleRecord(Utils.getHonorSignInAccount(), queryRequest)
            .addOnSuccessListener(sampleDataQueryResponse -> {
                List<SampleRecord> dataList = sampleDataQueryResponse.getDataList();
                if (dataList != null && !dataList.isEmpty()) {
                    for (SampleRecord record : dataList) {
                        if (record == null) {
                            continue;
                        }
                        SummaryData summary = record.getSummary();
                        if (summary == null) {
                            continue;
                        }
                        SportWalkData walkData = new SportWalkData();
                        walkData.setStartTime(record.getStartTime());
                        walkData.setEndTime(record.getEndTime());
                        if (summary.getInteger(WalkingField.FIELD_DISTANCE_NAME) != null) {
                            walkData.setDistance(summary.getInteger(WalkingField.FIELD_DISTANCE_NAME));
                        }

                        if (summary.getInteger(WalkingField.FIELD_SPORT_TIME_NAME) != null) {
                            walkData.setSportTime(summary.getInteger(WalkingField.FIELD_SPORT_TIME_NAME));
                        }

                        if (summary.getFloat(WalkingField.FIELD_CALORIE_NAME) != null) {
                            walkData.setCalorie(summary.getFloat(WalkingField.FIELD_CALORIE_NAME));
                        }

                        if (summary.getFloat(WalkingField.FIELD_SPEED_NAME) != null) {
                            walkData.setSpeed(summary.getFloat(WalkingField.FIELD_SPEED_NAME));
                        }

                        if (summary.getInteger(WalkingField.FIELD_STEP_CADENCE_NAME) != null) {
                            walkData.setStepCadence(summary.getInteger(WalkingField.FIELD_STEP_CADENCE_NAME));
                        }

                        if (summary.getInteger(WalkingField.FIELD_STEPS_NAME) != null) {
                            walkData.setSteps(summary.getInteger(WalkingField.FIELD_STEPS_NAME));
                        }

                        if (summary.getInteger(WalkingField.FIELD_AVG_HEART_RATE_NAME) != null) {
                            walkData.setAvgHeartRate(summary.getInteger(WalkingField.FIELD_AVG_HEART_RATE_NAME));
                        }

                        if (summary.getFloat(WalkingField.FIELD_TOTAL_RISE_HEIGHT_NAME) != null) {
                            walkData.setTotalRiseHeight(summary.getFloat(WalkingField.FIELD_TOTAL_RISE_HEIGHT_NAME));
                        }
                        if (summary.getFloat(WalkingField.FIELD_TOTAL_DROP_HEIGHT_NAME) != null) {
                            walkData.setTotalDropHeight(summary.getFloat(WalkingField.FIELD_TOTAL_DROP_HEIGHT_NAME));
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
                                    case DataType.SAMPLE_STEP_RATE:
                                        walkData.setStepCadenceDetails(helperUtils.parseStepCadenceInfo(list));
                                        break;
                                    case DataType.SAMPLE_SPEED:
                                        walkData.setSpeedDetails(helperUtils.parseSpeedInfo(list));
                                        break;
                                    case DataType.SAMPLE_LOCATION:
                                        walkData.setLocationDetails(helperUtils.parseLocationInfo(list));
                                        break;
                                    case DataType.SAMPLE_HEART_RATE:
                                        walkData.setHeartRateDetails(helperUtils.parseHeartRateInfo(list));
                                        break;
                                    default:
                                        break;
                                }
                            }
                            walkDataMap.put(walkData.getStartTime(), walkData);
                        }
                    }
                }
                boolean isTotal = (sampleDataQueryResponse.getTotal()) == (sampleDataQueryResponse.getIndex() + 1);
                if (mResult != null && isTotal) {
                    mResult.onSuccess(DataType.RECORD_WALKING);
                }
            })
            .addOnFailureListener(e -> {
                LogUtil.e(TAG, " onFail e: " + e);
                if (mResult != null) {
                    mResult.onFail(DataType.RECORD_WALKING, e);
                }
            });
    }

    public void insertData() {
        // 设置运动记录信息，开始时间StartTime和结束时间EndTime，为毫米级时间戳。
        long endTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        long startTime = endTime - TimeUnit.MINUTES.toMillis(6);
        SummaryData summaryData = new SummaryData();
        summaryData.putInteger(WalkingField.FIELD_DISTANCE_NAME, 1100);
        summaryData.putInteger(WalkingField.FIELD_SPORT_TIME_NAME, 300);
        summaryData.putFloat(WalkingField.FIELD_CALORIE_NAME, 30.0f);
        summaryData.putFloat(WalkingField.FIELD_SPEED_NAME, 6.3f);
        summaryData.putInteger(WalkingField.FIELD_STEP_CADENCE_NAME, 6);
        summaryData.putInteger(WalkingField.FIELD_STEPS_NAME, 8);
        summaryData.putInteger(WalkingField.FIELD_AVG_HEART_RATE_NAME, 88);
        summaryData.putFloat(WalkingField.FIELD_TOTAL_DROP_HEIGHT_NAME, 9.1f);
        summaryData.putFloat(WalkingField.FIELD_TOTAL_RISE_HEIGHT_NAME, 18.9f);

        SampleRecord sampleRecord = new SampleRecord(DataType.RECORD_WALKING, startTime, endTime);
        sampleRecord.setSummary(summaryData);

        List<SampleData> stepRateDataList = new ArrayList<>();
        List<SampleData> speedDataList = new ArrayList<>();
        List<SampleData> locationDataList = new ArrayList<>();
        List<SampleData> heartRateDataList = new ArrayList<>();
        List<SampleData> paceInfoDataList = new ArrayList<>();

        long associationStartTime = endTime - TimeUnit.MINUTES.toMillis(5);
        long associationEndTime = associationStartTime + TimeUnit.SECONDS.toMillis(5);
        // 设置步频关联信息。
        SampleData stepRateSampleData = new SampleData();
        stepRateSampleData.setDataType(DataType.SAMPLE_STEP_RATE);
        stepRateSampleData.setStartTime(associationStartTime);
        stepRateSampleData.setEndTime(associationEndTime);
        stepRateSampleData.putInteger(StepRateField.FIELD_STEP_RATE_NAME, 12);
        stepRateDataList.add(stepRateSampleData);

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

        sampleRecord.putDetail(DataType.SAMPLE_STEP_RATE, stepRateDataList);
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
