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
import com.hihonor.healthkitdemo.adapter.BloodPressureAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.BloodPressureField;
import com.hihonor.mcs.fitness.health.datastruct.BloodPressureStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 血压统计
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class BloodPressureActivity extends BaseActivity {
    private static final String TAG = "BloodPressureActivity_";

    private TextView mTvBloodPressureInterval;

    private TextView mTvMaxBloodPressure;

    private TextView mTvMinBloodPressure;

    private TextView mTvAveragePulse;

    private TextView mTvMaxPulse;

    private TextView mTvMinPulse;

    private RecyclerView mRecyclerView;

    private BloodPressureAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        initView();
    }

    private void initView() {
        mTvBloodPressureInterval = findViewById(R.id.tv_blood_pressure_interval);
        mTvMaxBloodPressure = findViewById(R.id.tv_max_blood_pressure);
        mTvMinBloodPressure = findViewById(R.id.tv_min_blood_pressure);
        mTvAveragePulse = findViewById(R.id.tv_average_pulse);
        mTvMaxPulse = findViewById(R.id.tv_max_pulse);
        mTvMinPulse = findViewById(R.id.tv_min_pulse);
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getBloodPressure();
        getEveryBloodPressure();
    }

    private void getBloodPressure() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep =
                new QueryRequest(DataType.SAMPLE_BLOOD_PRESSURE_STATISTIC, startTime, endTime);
            HealthKit.getDataStoreClient(BloodPressureActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getBloodPressure: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getBloodPressure: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                    Log.i(TAG, "getBloodPressure: dataList = " + dataList.size());
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
            Log.i(TAG, "getBloodPressure: sampleData = " + sampleData);
            if (sampleData != null && sampleData instanceof SampleData) {
                int averageHighPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_AVERAGE_HIGH_PRESSURE_NAME);
                int averageLowPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_AVERAGE_LOW_PRESSURE_NAME);
                int maxHighPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MAX_HIGH_PRESSURE_NAME);
                int maxLowPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MAX_LOW_PRESSURE_NAME);
                int minHighPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MIN_HIGH_PRESSURE_NAME);
                int minLowPressure =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MIN_LOW_PRESSURE_NAME);
                int averagePulse =
                    ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_AVERAGE_PULSE_NAME);
                int maxPulse = ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MAX_PULSE_NAME);
                int minPulse = ((SampleData) sampleData).getInteger(BloodPressureStatisticField.FIELD_MIN_PULSE_NAME);
                mTvBloodPressureInterval.setText(averageHighPressure + "/" + averageLowPressure);
                mTvMaxBloodPressure.setText(minHighPressure + "-" + maxHighPressure);
                mTvMinBloodPressure.setText(minLowPressure + "-" + maxLowPressure);
                if (averagePulse > 0) {
                    mTvAveragePulse.setText("平均脉搏：" + averagePulse + "/分钟");
                } else {
                    mTvAveragePulse.setText("平均脉搏：--/分钟");
                }
                if (maxPulse > 0) {
                    mTvMaxPulse.setText("最大脉搏：" + maxPulse + "/分钟");
                } else {
                    mTvMaxPulse.setText("最大脉搏：--/分钟");
                }
                if (minPulse > 0) {
                    mTvMinPulse.setText("最小脉搏：" + minPulse + "/分钟");
                } else {
                    mTvMinPulse.setText("最小脉搏：--/分钟");
                }
            }
        }, throwable -> {
            LogUtil.e(TAG, "onFail, error=" + throwable);
        });
    }

    private void getEveryBloodPressure() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_BLOOD_PRESSURE, startTime, endTime);
            HealthKit.getDataStoreClient(BloodPressureActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getEveryBloodPressure: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getEveryBloodPressure: getTotal = " + sampleDataQueryResponse.getTotal());
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
            Log.i(TAG, "getEveryStress: dataList = " + dataList);
            if (dataList != null) {
                mAdapter = new BloodPressureAdapter((List<SampleData>) dataList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_BLOOD_PRESSURE);
        long curTimeMills = System.currentTimeMillis();
        sampleData.setStartTime(curTimeMills);
        sampleData.setEndTime(curTimeMills);
        sampleData.putInteger(BloodPressureField.FIELD_HIGH_PRESSURE_NAME, 120);
        sampleData.putInteger(BloodPressureField.FIELD_LOW_PRESSURE_NAME, 79);
        sampleData.putInteger(BloodPressureField.FIELD_PRESSURE_TYPE_NAME,
            BloodPressureField.BloodPressureType.NORMOTENSION);
        sampleData.putInteger(BloodPressureField.FIELD_PULSE_NAME, 76);
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
