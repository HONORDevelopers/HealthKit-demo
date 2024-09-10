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
import com.hihonor.mcs.fitness.health.datastruct.JumpRopeField;
import com.hihonor.mcs.fitness.health.exception.HealthKitException;
import com.hihonor.mcs.fitness.health.task.OnFailureListener;
import com.hihonor.mcs.fitness.health.task.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 跳绳帮助类 获取步行数据
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportJumpRopeHelper {
    private static final String TAG = SportJumpRopeHelper.class.getSimpleName();

    private Context mContext;

    private long mStartTime;

    private long mEndTime;

    private Map<Long, SportWalkData> walkDataMap = new HashMap<>();

    private ISportResultHelper mResult;

    public SportJumpRopeHelper(Context context) {
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
    public void getJumpRopeData() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                requestWalkData();
            }
        });
    }

    private void requestWalkData() {
        QueryRequest queryRequest = new QueryRequest(DataType.RECORD_JUMP_ROPE, mStartTime, mEndTime);
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
//                        int number = summary.getInteger(JumpRopeField.FIELD_NUMBER_NAME);
                        int sportTime = summary.getInteger(JumpRopeField.FIELD_SPORT_TIME_NAME);
                        float calorie = summary.getFloat(JumpRopeField.FIELD_CALORIE_NAME);
                        int speed = summary.getInteger(JumpRopeField.FIELD_SPEED_NAME);
                        int tripped = summary.getInteger(JumpRopeField.FIELD_TRIPPED_NAME);
                        int longestStreak = summary.getInteger(JumpRopeField.FIELD_LONGEST_STREAK_NAME);
                        int avgHeartRate = summary.getInteger(JumpRopeField.FIELD_AVG_HEART_RATE_NAME);
//                        LogUtil.i(TAG, " OnSuccess jump number:" + number);
                    }
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
        summaryData.putInteger(JumpRopeField.FIELD_SPORT_TIME_NAME, 600);
        summaryData.putFloat(JumpRopeField.FIELD_CALORIE_NAME, 30.0f);
        summaryData.putInteger(JumpRopeField.FIELD_SPEED_NAME, 120);
        summaryData.putInteger(JumpRopeField.FIELD_AVG_HEART_RATE_NAME, 88);
        summaryData.putInteger(JumpRopeField.FIELD_TRIPPED_NAME, 3);
//        summaryData.putInteger(JumpRopeField.FIELD_NUMBER_NAME, 1205);
        summaryData.putInteger(JumpRopeField.FIELD_LONGEST_STREAK_NAME, 432);

        SampleRecord sampleRecord = new SampleRecord(DataType.RECORD_JUMP_ROPE, startTime, endTime);
        sampleRecord.setSummary(summaryData);

        List<SampleData> jumpRopeSpeedList = new ArrayList<>();
        List<SampleData> heartRateDataList = new ArrayList<>();

        // 跳绳速度
        long associationStartTime = endTime - TimeUnit.MINUTES.toMillis(5);
        long associationEndTime = associationStartTime + TimeUnit.SECONDS.toMillis(5);
        SampleData jumpRopeSpeedSampleData = new SampleData();
        jumpRopeSpeedSampleData.setDataType(DataType.SAMPLE_JUMP_ROPE_SPEED);
        jumpRopeSpeedSampleData.setStartTime(associationStartTime);
        jumpRopeSpeedSampleData.setEndTime(associationEndTime);
        jumpRopeSpeedSampleData.putInteger(JumpRopeField.FIELD_SPEED_NAME, 120);
        jumpRopeSpeedList.add(jumpRopeSpeedSampleData);

        // 设置心率关联信息。
        SampleData heartRateSampleData = new SampleData();
        heartRateSampleData.setDataType(DataType.SAMPLE_HEART_RATE);
        heartRateSampleData.setStartTime(associationStartTime);
        heartRateSampleData.setEndTime(associationEndTime);
        heartRateSampleData.putInteger(HeartRateField.FIELD_DYNAMIC_HEART_RATE_NAME, 136);
        heartRateSampleData.putInteger(HeartRateField.FIELD_RESTING_HEART_RATE_NAME, 78);
        heartRateDataList.add(heartRateSampleData);

        sampleRecord.putDetail(DataType.SAMPLE_HEART_RATE, heartRateDataList);
        sampleRecord.putDetail(DataType.SAMPLE_JUMP_ROPE_SPEED, jumpRopeSpeedList);

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
