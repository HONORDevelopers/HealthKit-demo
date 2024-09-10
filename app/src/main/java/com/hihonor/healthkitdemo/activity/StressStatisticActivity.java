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
import com.hihonor.healthkitdemo.adapter.StressAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.StressField;
import com.hihonor.mcs.fitness.health.datastruct.StressStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 压力统计界面
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StressStatisticActivity extends BaseActivity {
    private static final String TAG = "StressStatisticActivity_";

    private static final int[][] STRESS = {{1, 29}, {30, 59}, {60, 79}, {80, 99}};

    private static final String[] STRESS_GRADE_NAME = {"放松", "正常", "中等", "偏高"};

    private TextView mTvStressStatisticInterval;

    private TextView mTvStressStatisticText;

    private TextView mTvMaxStress;

    private TextView mTvMaxStressText;

    private TextView mTvMinStress;

    private TextView mTvMinStressText;

    private RecyclerView mRecyclerView;

    private StressAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_statistic);
        initView();
    }

    private void initView() {
        mTvStressStatisticInterval = findViewById(R.id.tv_stress_statistic_interval);
        mTvStressStatisticText = findViewById(R.id.tv_stress_statistic_text);
        mTvMaxStress = findViewById(R.id.tv_max_stress);
        mTvMaxStressText = findViewById(R.id.tv_max_stress_text);
        mTvMinStress = findViewById(R.id.tv_min_stress);
        mTvMinStressText = findViewById(R.id.tv_min_stress_text);
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getStressStatistic();
        getEveryStress();
    }

    private void getStressStatistic() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STRESS_STATISTIC, startTime, endTime);
            HealthKit.getDataStoreClient(StressStatisticActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getStressStatistic: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getStressStatistic: getTotal = " + sampleDataQueryResponse.getTotal());
                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                    Log.i(TAG, "getStressStatistic: dataList = " + dataList.size());
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
            Log.i(TAG, "getStressStatistic: sampleData = " + sampleData);
            if (sampleData != null && sampleData instanceof SampleData) {
                setTextViewText((SampleData) sampleData);
            }
        }, throwable -> {
            LogUtil.e(TAG, "onFail, error=" + throwable);
        });
    }

    private void getEveryStress() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STRESS, startTime, endTime);
            HealthKit.getDataStoreClient(StressStatisticActivity.this)
                .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                .addOnSuccessListener(sampleDataQueryResponse -> {
                    Log.i(TAG, "getEveryStress: getIndex = " + sampleDataQueryResponse.getIndex());
                    Log.i(TAG, "getEveryStress: getTotal = " + sampleDataQueryResponse.getTotal());
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
                mAdapter = new StressAdapter((List<SampleData>) dataList);

                mRecyclerView.setAdapter(mAdapter);
            }
        }, throwable -> {
            LogUtil.i(TAG, "onFail, height error=" + throwable);
        });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_STRESS);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1));
        // 开始时间和结束时间相差1分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(1);
        sampleData.setStartTime(startTime);
        sampleData.setEndTime(endTime);
        sampleData.putInteger(StressField.FIELD_SCORE_NAME, 28);
        sampleData.putInteger(StressField.FIELD_GRADE_NAME, 1);
        sampleData.putInteger(StressField.FIELD_MEASURE_TYPE_NAME, 0);
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

    private void setTextViewText(SampleData sampleData) {
        if (sampleData == null) {
            LogUtil.e(TAG, "onFail, sampleData is null ");
            return;
        }
        int maxScore = sampleData.getInteger(StressStatisticField.FIELD_MAX_SCORE_NAME);
        int minScore = sampleData.getInteger(StressStatisticField.FIELD_MIN_SCORE_NAME);
        int avgScore = sampleData.getInteger(StressStatisticField.FIELD_AVG_SCORE_NAME);
        mTvStressStatisticInterval.setText(avgScore + "");
        mTvMaxStress.setText(maxScore + "");
        mTvMinStress.setText(minScore + "");
        for (int i = 0; i < STRESS.length; i++) {
            if (avgScore > STRESS[i][0] && avgScore < STRESS[i][1]) {
                mTvStressStatisticText.setText(STRESS_GRADE_NAME[i]);
                if (i == 2) {
                    mTvStressStatisticText.setTextColor(Color.YELLOW);
                } else if (i == 3) {
                    mTvStressStatisticText.setTextColor(Color.RED);
                } else {
                    mTvStressStatisticText.setTextColor(Color.BLACK);
                }
            }

            if (minScore > STRESS[i][0] && minScore < STRESS[i][1]) {
                mTvMinStressText.setText(STRESS_GRADE_NAME[i]);
                if (i == 2) {
                    mTvMinStressText.setTextColor(Color.YELLOW);
                } else if (i == 3) {
                    mTvMinStressText.setTextColor(Color.RED);
                } else {
                    mTvMinStressText.setTextColor(Color.BLACK);
                }
            }

            if (maxScore > STRESS[i][0] && maxScore < STRESS[i][1]) {
                mTvMaxStressText.setText(STRESS_GRADE_NAME[i]);
                if (i == 2) {
                    mTvMaxStressText.setTextColor(Color.YELLOW);
                } else if (i == 3) {
                    mTvMaxStressText.setTextColor(Color.RED);
                } else {
                    mTvMaxStressText.setTextColor(Color.BLACK);
                }
            }
        }
    }
}
