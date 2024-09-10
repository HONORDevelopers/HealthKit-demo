/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleRecord;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.MenstrualCycleField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 生理周期
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class MenstrualCycleActivity extends BaseActivity {
    private static final String TAG = "MenstrualCycleActivity_";

    private static final int SUB_STATUS_ONE = 1;

    private static final int SUB_STATUS_TWO = 2;

    private static final int VOLUME_ONE = 1;

    private static final int VOLUME_TWO = 2;

    private static final int VOLUME_THREE = 3;

    private static final int LEVEL_ONE = 1;

    private static final int LEVEL_TWO = 2;

    private static final int LEVEL_THREE = 3;

    private TextView mTvMenstrualCycleStatus;

    private LinearLayout mLayoutMenstrualVolume;

    private TextView mTvMenstrualCycleVolume;

    private LinearLayout mLayoutMenstrualLevel;

    private TextView mTvMenstrualCycleLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menstrual_cycle);
        initView();
    }

    private void initView() {
        mTvMenstrualCycleStatus = findViewById(R.id.tv_menstrual_cycle_status);
        mLayoutMenstrualVolume = findViewById(R.id.layout_menstrual_volume);
        mTvMenstrualCycleVolume = findViewById(R.id.tv_menstrual_cycle_volume);
        mLayoutMenstrualLevel = findViewById(R.id.layout_menstrual_level);
        mTvMenstrualCycleLevel = findViewById(R.id.tv_menstrual_cycle_level);
        insertData();
        getMenstrualCycle();
    }
    private void getMenstrualCycle() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
                    QueryRequest queryRequestStep = new QueryRequest(DataType.RECORD_MENSTRUAL_CYCLE_INFO, startTime, endTime);
                    HealthKit.getDataStoreClient(MenstrualCycleActivity.this).querySampleRecord(Utils.getHonorSignInAccount(), queryRequestStep)
                            .addOnSuccessListener(sampleDataQueryResponse -> {
                                Log.i(TAG, "getMenstrualCycle: getIndex = " + sampleDataQueryResponse.getIndex());
                                Log.i(TAG, "getMenstrualCycle: getTotal = " + sampleDataQueryResponse.getTotal());
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sampleRecord -> {
                    Log.i(TAG, "getMenstrualCycle: sampleRecord = " + sampleRecord);
                    if (sampleRecord != null && sampleRecord instanceof SampleRecord) {
                        setDateToView((SampleRecord) sampleRecord);
                    }
                }, throwable -> {
                    LogUtil.i(TAG, "onFail, height error=" + throwable);
                });
    }

    private void setDateToView(SampleRecord sampleRecord) {
        if (sampleRecord == null) {
            return;
        }
        int status = sampleRecord.getIntegerSummary(MenstrualCycleField.FIELD_STATUS_NAME);
        int subStatus = sampleRecord.getIntegerSummary(MenstrualCycleField.FIELD_SUB_STATUS_NAME);
        String menstrualStatus = "";
        switch (status) {
            case 0:
                menstrualStatus = "非经期";
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                mLayoutMenstrualLevel.setVisibility(View.GONE);
                mLayoutMenstrualVolume.setVisibility(View.GONE);
                break;
            case 1:
                menstrualStatus = "易孕期";
                if (subStatus == SUB_STATUS_ONE) {
                    menstrualStatus += "第一天";
                } else if (subStatus == SUB_STATUS_TWO) {
                    menstrualStatus += "最后一天";
                }
                mLayoutMenstrualLevel.setVisibility(View.GONE);
                mLayoutMenstrualVolume.setVisibility(View.GONE);
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                break;
            case 2:
                menstrualStatus = "排卵期";
                if (subStatus == SUB_STATUS_ONE) {
                    menstrualStatus += "第一天";
                } else if (subStatus == SUB_STATUS_TWO) {
                    menstrualStatus += "最后一天";
                }
                mLayoutMenstrualLevel.setVisibility(View.GONE);
                mLayoutMenstrualVolume.setVisibility(View.GONE);
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                break;
            case 3:
                menstrualStatus = "预测经期";
                if (subStatus == SUB_STATUS_ONE) {
                    menstrualStatus += "第一天";
                } else if (subStatus == SUB_STATUS_TWO) {
                    menstrualStatus += "最后一天";
                }
                mLayoutMenstrualLevel.setVisibility(View.GONE);
                mLayoutMenstrualVolume.setVisibility(View.GONE);
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                break;
            case 4:
                menstrualStatus = "实际经期";
                if (subStatus == SUB_STATUS_ONE) {
                    menstrualStatus += "第一天";
                } else if (subStatus == SUB_STATUS_TWO) {
                    menstrualStatus += "最后一天";
                }
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                int volume = sampleRecord.getIntegerSummary(MenstrualCycleField.FIELD_VOLUME_NAME);
                mLayoutMenstrualVolume.setVisibility(View.VISIBLE);
                if (volume == VOLUME_ONE) {
                    mTvMenstrualCycleVolume.setText("少量");
                } else if (volume == VOLUME_TWO) {
                    mTvMenstrualCycleVolume.setText("正常");
                } else if (volume == VOLUME_THREE){
                    mTvMenstrualCycleVolume.setText("多量");
                } else {
                    mLayoutMenstrualVolume.setVisibility(View.GONE);
                }
                int level = sampleRecord.getIntegerSummary(MenstrualCycleField.FIELD_LEVEL_NAME);
                mLayoutMenstrualLevel.setVisibility(View.VISIBLE);
                if (level == LEVEL_ONE) {
                    mTvMenstrualCycleLevel.setText("轻微");
                } else if (level == LEVEL_TWO) {
                    mTvMenstrualCycleLevel.setText("中等");
                } else if (level == LEVEL_THREE){
                    mTvMenstrualCycleLevel.setText("重度");
                } else {
                    mLayoutMenstrualLevel.setVisibility(View.GONE);
                }
                mTvMenstrualCycleStatus.setText(menstrualStatus);
                break;
            default:
                break;
        }
    }

    private void insertData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleRecord sampleRecord = new SampleRecord();
        sampleRecord.setDataType(DataType.RECORD_MENSTRUAL_CYCLE_INFO);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        sampleRecord.setStartTime(curTimeMills);
        sampleRecord.setEndTime(curTimeMills);
        sampleRecord.putIntegerSummary(MenstrualCycleField.FIELD_STATUS_NAME, 4);
        sampleRecord.putIntegerSummary(MenstrualCycleField.FIELD_SUB_STATUS_NAME, 1);
        sampleRecord.putIntegerSummary(MenstrualCycleField.FIELD_VOLUME_NAME, 2);
        sampleRecord.putIntegerSummary(MenstrualCycleField.FIELD_LEVEL_NAME, 1);

        // 构建步数数据列表sampleDataList
        List<SampleRecord> sampleRecordList = new ArrayList<>();
        sampleRecordList.add(sampleRecord);
        Observable.create(emitter ->
                        HealthKit.getDataStoreClient(this).insertSampleRecord(Utils.getHonorSignInAccount(), sampleRecordList)
                                .addOnSuccessListener(insertResponse -> {
                                    emitter.onNext(insertResponse);
                                    emitter.onComplete();
                                }).addOnFailureListener(e -> {
                                    emitter.onError(e);
                                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(insertResponse -> {
                    Log.i(TAG, "插入成功");

                }, throwable -> {
                    Log.e(TAG, "插入失败！！！: error = " + throwable);

                });
    }
}
