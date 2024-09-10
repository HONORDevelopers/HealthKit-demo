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
import com.hihonor.healthkitdemo.adapter.BodyTemperatureAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.BodyTemperatureField;
import com.hihonor.mcs.fitness.health.datastruct.BodyTemperatureStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 体温统计
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class BodyTemperatureActivity extends BaseActivity {
    private static final String TAG = "BodyTemperatureActivity_";

    private static final float MAX_TEMPERATURE = 37.2f;

    private TextView mTvBodyTemperatureInterval;

    private TextView mTvMaxBodyTemperature;

    private TextView mTvMinBodyTemperature;

    private RecyclerView mRecyclerView;

    private BodyTemperatureAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_temperture);
        initView();
    }

    private void initView() {
        mTvBodyTemperatureInterval = findViewById(R.id.tv_body_temperature_interval);
        mTvMaxBodyTemperature = findViewById(R.id.tv_max_body_temperature);
        mTvMinBodyTemperature = findViewById(R.id.tv_min_body_temperature);
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getBodyTemperature();
        getEveryBodyTemperature();
    }

    private void getBodyTemperature() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep =
                new QueryRequest(DataType.SAMPLE_BODY_TEMPERATURE_STATISTIC, startTime, endTime);
            HealthKit.getDataStoreClient(BodyTemperatureActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getBodyTemperature: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getBodyTemperature: getTotal = " + sampleDataQueryResponse.getTotal());
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
            Log.i(TAG, "getBodyTemperature: sampleData = " + sampleData);
            if (sampleData != null && sampleData instanceof SampleData) {
                float avgValue = ((SampleData) sampleData).getFloat(BodyTemperatureStatisticField.FIELD_AVG_VALUE_NAME);
                float maxValue = ((SampleData) sampleData).getFloat(BodyTemperatureStatisticField.FIELD_MAX_VALUE_NAME);
                float minValue = ((SampleData) sampleData).getFloat(BodyTemperatureStatisticField.FIELD_MIN_VALUE_NAME);
                mTvBodyTemperatureInterval.setText(avgValue + "");
                mTvMaxBodyTemperature.setText(maxValue + "");
                mTvMinBodyTemperature.setText(minValue + "");
                if (avgValue > MAX_TEMPERATURE) {
                    mTvBodyTemperatureInterval.setTextColor(Color.RED);
                } else {
                    mTvBodyTemperatureInterval.setTextColor(Color.GREEN);
                }
                if (maxValue > MAX_TEMPERATURE) {
                    mTvMaxBodyTemperature.setTextColor(Color.RED);
                } else {
                    mTvMaxBodyTemperature.setTextColor(Color.GREEN);
                }
                if (minValue > MAX_TEMPERATURE) {
                    mTvMinBodyTemperature.setTextColor(Color.RED);
                } else {
                    mTvMinBodyTemperature.setTextColor(Color.GREEN);
                }
            }
        }, throwable -> {
            LogUtil.e(TAG, "onFail, error=" + throwable);
        });
    }

    private void getEveryBodyTemperature() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_BODY_TEMPERATURE, startTime, endTime);
            HealthKit.getDataStoreClient(BodyTemperatureActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getEveryBodyTemperature: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getEveryBodyTemperature: getTotal = " + sampleDataQueryResponse.getTotal());
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
            Log.i(TAG, "getEveryBodyTemperature: dataList = " + dataList);
            if (dataList != null) {
                mAdapter = new BodyTemperatureAdapter((List<SampleData>) dataList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_BODY_TEMPERATURE);
        long curTimeMills = System.currentTimeMillis();
        sampleData.setStartTime(curTimeMills);
        sampleData.setEndTime(curTimeMills);
        sampleData.putFloat(BodyTemperatureField.FIELD_TEMPERATURE_NAME, 36.5F);
        sampleData.putInteger(BodyTemperatureField.FIELD_TEMPERATURE_TYPE_NAME, 1);
        sampleData.putInteger(BodyTemperatureField.FIELD_MEASURE_TYPE_NAME, 2);
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
