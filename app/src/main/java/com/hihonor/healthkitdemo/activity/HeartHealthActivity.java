/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.HeartHealthAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.data.SampleRecord;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.HeartHealthField;
import com.hihonor.mcs.fitness.health.datastruct.HeartRateField;
import com.hihonor.mcs.fitness.health.datastruct.HeartRateStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 心率统计
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class HeartHealthActivity extends BaseActivity {
    private static final String TAG = "HeartHealthActivity_";

    private TextView mTvHeartRateInterval;

    private TextView mTvMaxHeartRate;

    private TextView mTvMinHeartRate;

    private TextView mTvMaxDynamicHeartRate;

    private TextView mTvMinDynamicHeartRate;

    private TextView mTvAverageHeartRate;

    private TextView mTvMaxRestingHeartRate;

    private TextView mTvMinRestingHeartRate;

    private TextView mTvAveRestingHeartRate;

    private RecyclerView mRecyclerView;

    private HeartHealthAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_health);
        initView();
    }

    private void initView() {
        initHeartHealthView();
        initSampleHeartRateStatistic();
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getEveryHeartHealthData();
    }

    private void initHeartHealthView() {
        mTvHeartRateInterval = findViewById(R.id.tv_heart_rate_interval);
        mTvMaxHeartRate = findViewById(R.id.tv_max_heart_rate);
        mTvMinHeartRate = findViewById(R.id.tv_min_heart_rate);
        insertHeartHealthData();
        getHeartHealth();
    }

    private void initSampleHeartRateStatistic() {
        mTvMaxDynamicHeartRate = findViewById(R.id.tv_max_dynamic_heart_rate);
        mTvMinDynamicHeartRate = findViewById(R.id.tv_min_dynamic_heart_rate);
        mTvAverageHeartRate = findViewById(R.id.tv_average_dynamic_heart_rate);
        mTvMaxRestingHeartRate = findViewById(R.id.tv_max_resting_heart_rate);
        mTvMinRestingHeartRate = findViewById(R.id.tv_min_resting_heart_rate);
        mTvAveRestingHeartRate = findViewById(R.id.tv_ave_resting_heart_rate);
        getSampleHeartRateStatistic();
    }

    private void getHeartHealth() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime) - TimeUnit.DAYS.toMillis(7);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.RECORD_HEART_HEALTH, startTime, endTime);
            HealthKit.getDataStoreClient(HeartHealthActivity.this)
                .querySampleRecord(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getHeartHealth: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getHeartHealth: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleRecord> dataList = sampleDataQueryResponse.getDataList();
                    if (dataList != null && !dataList.isEmpty()) {
                        SampleRecord sampleRecord = dataList.get(0);
                        emitter.onNext(sampleRecord);
                        emitter.onComplete();
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.i(TAG, "onFail, step error=" + e);
                    emitter.onError(e);
                });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(sampleRecord -> {
            Log.i(TAG, "getHeartHealth: sampleRecord = " + sampleRecord);
            if (sampleRecord != null && sampleRecord instanceof SampleRecord) {
                int maxHeartRate =
                    ((SampleRecord) sampleRecord).getIntegerSummary(HeartHealthField.FIELD_MAX_HEART_RATE_NAME);
                int minHeartRate =
                    ((SampleRecord) sampleRecord).getIntegerSummary(HeartHealthField.FIELD_MIN_HEART_RATE_NAME);
                int warningType =
                    ((SampleRecord) sampleRecord).getIntegerSummary(HeartHealthField.FIELD_WARNING_TYPE_NAME);
                int warningLimit =
                    ((SampleRecord) sampleRecord).getIntegerSummary(HeartHealthField.FIELD_WARNING_LIMIT_NAME);
                mTvHeartRateInterval.setText(minHeartRate + "-" + maxHeartRate);
                switch (warningType) {
                    case 0:
                        mTvMaxHeartRate.setText(warningLimit);
                        break;
                    case 1:
                        mTvMinHeartRate.setText(warningLimit);
                        break;
                    default:
                        break;
                }
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void getSampleHeartRateStatistic() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_HEART_RATE_STATISTIC, startTime, endTime);
            HealthKit.getDataStoreClient(HeartHealthActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getSampleHeartRateStatistic: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getSampleHeartRateStatistic: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                    if (dataList != null && !dataList.isEmpty()) {
                        SampleData sampleData = dataList.get(0);
                        emitter.onNext(sampleData);
                        emitter.onComplete();
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.i(TAG, "onFail, step error=" + e);
                    emitter.onError(e);
                });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(sampleData -> {
            Log.i(TAG, "getSampleHeartRateStatistic: sampleData = " + sampleData);
            if (sampleData != null && sampleData instanceof SampleData) {
                int maxDynamicHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_MAX_VALUE_NAME);
                int minDynamicHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_MIN_VALUE_NAME);
                int averageDynamicHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_AVERAGE_VALUE_NAME);
                int maxRestingHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_MAX_RESTING_VALUE);
                int minRestingHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_MIN_RESTING_VALUE);
                int restingAverageHeartRate =
                    ((SampleData) sampleData).getInteger(HeartRateStatisticField.FIELD_AVERAGE_RESTING_VALUE);
                mTvMaxDynamicHeartRate.setText(maxDynamicHeartRate + "");
                mTvMinDynamicHeartRate.setText(minDynamicHeartRate + "");
                mTvAverageHeartRate.setText(averageDynamicHeartRate + "");
                mTvMaxRestingHeartRate.setText(maxRestingHeartRate + "");
                mTvMinRestingHeartRate.setText(minRestingHeartRate + "");
                mTvAveRestingHeartRate.setText(restingAverageHeartRate + "");
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void getEveryHeartHealthData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_HEART_RATE, startTime, endTime);
            HealthKit.getDataStoreClient(HeartHealthActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getEveryHeartHealth: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getEveryHeartHealth: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                    if (dataList != null && !dataList.isEmpty()) {
                        emitter.onNext(dataList);
                        emitter.onComplete();
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.i(TAG, "onFail, step error=" + e);
                    emitter.onError(e);
                });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(dataList -> {
            Log.i(TAG, "getEveryHeartHealth: dataList = " + dataList);
            if (dataList != null) {
                List<SampleData> listAll = new ArrayList<>();
                listAll.addAll((List<SampleData>) dataList);
                listAll.add(0, new SampleData());
                mAdapter = new HeartHealthAdapter(listAll);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_HEART_RATE);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1));
        // 开始时间和结束时间相差1分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(1);
        sampleData.setStartTime(startTime);
        sampleData.setEndTime(endTime);
        sampleData.putInteger(HeartRateField.FIELD_DYNAMIC_HEART_RATE_NAME, 128);
        sampleData.putInteger(HeartRateField.FIELD_RESTING_HEART_RATE_NAME, 82);
        // 构建步数数据列表sampleDataList
        List<SampleData> sampleDataList = new ArrayList<>();
        sampleDataList.add(sampleData);
        Observable.create(emitter -> HealthKit.getDataStoreClient(this)
            .insertSampleData(Utils.getHonorSignInAccount(), sampleDataList)
            .addOnSuccessListener(insertResponse -> {
                emitter.onNext(insertResponse);
                emitter.onComplete();
            })
            .addOnFailureListener(e -> {
                emitter.onError(e);
            })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(insertResponse -> {
                Log.i(TAG, "写入成功");

            }, throwable -> {
                Log.e(TAG, "写入失败！！！: error = " + throwable);

            });
    }

    private void insertHeartHealthData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleRecord sampleRecord = new SampleRecord();
        sampleRecord.setDataType(DataType.RECORD_HEART_HEALTH);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1));
        // 开始时间和结束时间相差10分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(10);
        sampleRecord.setStartTime(startTime);
        sampleRecord.setEndTime(endTime);
        sampleRecord.putIntegerSummary(HeartHealthField.FIELD_MAX_HEART_RATE_NAME, 176);
        sampleRecord.putIntegerSummary(HeartHealthField.FIELD_MIN_HEART_RATE_NAME, 80);
        sampleRecord.putIntegerSummary(HeartHealthField.FIELD_WARNING_LIMIT_NAME, 172);
        sampleRecord.putIntegerSummary(HeartHealthField.FIELD_WARNING_TYPE_NAME, 0);

        // 构建步数数据列表sampleDataList
        List<SampleRecord> sampleRecordList = new ArrayList<>();
        sampleRecordList.add(sampleRecord);
        Observable.create(emitter -> HealthKit.getDataStoreClient(this)
            .insertSampleRecord(Utils.getHonorSignInAccount(), sampleRecordList)
            .addOnSuccessListener(insertResponse -> {
                emitter.onNext(insertResponse);
                emitter.onComplete();
            })
            .addOnFailureListener(e -> {
                emitter.onError(e);
            })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(insertResponse -> {
                Log.i(TAG, "插入成功");

            }, throwable -> {
                    Log.e(TAG, "插入失败！！！: error = " + throwable);
                });
    }
}
