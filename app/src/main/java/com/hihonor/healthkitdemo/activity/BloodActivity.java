/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.BloodAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.BloodSugarField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseActivity
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class BloodActivity extends BaseActivity {
    private static final String TAG = "BloodActivity_";

    private RecyclerView mRecyclerView;

    private BloodAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        insertEveryMinuteData();
        getEveryBlood();
    }

    private void getEveryBlood() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
            QueryRequest queryRequestStep
                    = new QueryRequest(DataType.SAMPLE_BLOOD_SUGAR, startTime, endTime);
            HealthKit.getDataStoreClient(BloodActivity.this)
                    .querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
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
            Log.i(TAG, "getEveryBlood: dataList = " + dataList);
            if (dataList != null) {
                mAdapter = new BloodAdapter((List<SampleData>) dataList);
                mRecyclerView.setAdapter(mAdapter);
            } }, throwable -> {
                LogUtil.i(TAG, "onFail, height error=" + throwable);
            });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_BLOOD_SUGAR);
        long curTimeMills = System.currentTimeMillis();
        sampleData.setStartTime(curTimeMills);
        sampleData.setEndTime(curTimeMills);
        sampleData.putFloat(BloodSugarField.FIELD_MEASURE_VALUE_NAME, 5.8F);
        sampleData.putInteger(BloodSugarField.FIELD_TIME_RANGE_NAME, 2);
        // 构建步数数据列表sampleDataList
        List<SampleData> sampleDataList = new ArrayList<>();
        sampleDataList.add(sampleData);
        Observable.create(emitter
            -> HealthKit.getDataStoreClient(this)
                 .insertSampleData(Utils.getHonorSignInAccount(), sampleDataList)
                 .addOnSuccessListener(insertResponse -> {
                     emitter.onNext(insertResponse);
                     emitter.onComplete();
                 }).addOnFailureListener(e -> {
                     emitter.onError(e);
                 }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(insertResponse -> {
                    Log.i(TAG, "写入成功");

                }, throwable -> {
                        Log.e(TAG, "写入失败！！！: error = " + throwable);
                    });
    }
}
