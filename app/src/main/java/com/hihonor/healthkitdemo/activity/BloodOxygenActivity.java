/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.BloodOxygenAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.BloodOxygenField;
import com.hihonor.mcs.fitness.health.datastruct.BloodOxygenStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 血氧统计
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class BloodOxygenActivity extends BaseActivity {
    private static final String TAG = "BloodOxygenActivity_";

    private static final int BLOOD_OXYGEN = 90;

    private TextView mTvBloodOxygenInterval;

    private TextView mTvMaxBloodOxygen;

    private TextView mTvMinBloodOxygen;

    private RecyclerView mRecyclerView;

    private BloodOxygenAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_oxygen);
        initView();
    }

    private void initView() {
        mTvBloodOxygenInterval = findViewById(R.id.tv_blood_oxygen_interval);
        mTvMaxBloodOxygen = findViewById(R.id.tv_max_blood_oxygen);
        mTvMinBloodOxygen = findViewById(R.id.tv_min_blood_oxygen);
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getBloodOxygen();
        getEveryBloodOxygen();
    }

    private void getBloodOxygen() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep =
                new QueryRequest(DataType.SAMPLE_BLOOD_OXYGEN_STATISTIC, startTime, endTime);
            HealthKit.getDataStoreClient(BloodOxygenActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getBloodOxygen: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getBloodOxygen: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                    if (dataList != null && !dataList.isEmpty()) {
                        SampleData sampleData = dataList.get(0);
                        emitter.onNext(sampleData);
                        emitter.onComplete();
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.i(TAG, "onFail, error=" + e);
                    emitter.onError(e);
                });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(sampleData -> {
            Log.i(TAG, "getBloodOxygen: sampleData = " + sampleData);
            if (sampleData != null && sampleData instanceof SampleData) {
                int avgValue = ((SampleData) sampleData).getInteger(BloodOxygenStatisticField.FIELD_AVG_VALUE_NAME);
                int maxValue = ((SampleData) sampleData).getInteger(BloodOxygenStatisticField.FIELD_MAX_VALUE_NAME);
                int minValue = ((SampleData) sampleData).getInteger(BloodOxygenStatisticField.FIELD_MIN_VALUE_NAME);
                mTvBloodOxygenInterval.setText(avgValue + "");
                mTvMaxBloodOxygen.setText(maxValue + "");
                mTvMinBloodOxygen.setText(minValue + "");
                if (avgValue < BLOOD_OXYGEN) {
                    mTvBloodOxygenInterval.setTextColor(Color.RED);
                } else {
                    mTvBloodOxygenInterval.setTextColor(Color.GREEN);
                }
                if (maxValue < BLOOD_OXYGEN) {
                    mTvMaxBloodOxygen.setTextColor(Color.RED);
                } else {
                    mTvMaxBloodOxygen.setTextColor(Color.GREEN);
                }
                if (minValue < BLOOD_OXYGEN) {
                    mTvMinBloodOxygen.setTextColor(Color.RED);
                } else {
                    mTvMinBloodOxygen.setTextColor(Color.GREEN);
                }
            }
        }, throwable -> {
                LogUtil.e(TAG, "onFail, error=" + throwable);
            });
    }

    private void getEveryBloodOxygen() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_BLOOD_OXYGEN, startTime, endTime);
            HealthKit.getDataStoreClient(BloodOxygenActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getEveryBloodOxygen: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getEveryBloodOxygen: getTotal = " + sampleDataQueryResponse.getTotal());
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
            Log.i(TAG, "getEveryBloodOxygen: dataList = " + dataList);
            if (dataList != null) {
                mAdapter = new BloodOxygenAdapter((List<SampleData>) dataList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, throwable -> {
                LogUtil.i(TAG, "onFail, height error=" + throwable);
            });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_BLOOD_OXYGEN);
        long curTimeMills = System.currentTimeMillis();
        sampleData.setStartTime(curTimeMills);
        sampleData.setEndTime(curTimeMills);
        sampleData.putInteger(BloodOxygenField.FIELD_BLOOD_OXYGEN_NAME, 96);

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
}
