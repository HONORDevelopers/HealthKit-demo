/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.fragment.daily;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.SampleCaloriesDistanceStrengthAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.DistanceField;
import com.hihonor.mcs.fitness.health.datastruct.DistanceStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 距离统计和每分钟距离
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class DistanceDailyFragment extends BaseFragment{
    private static final String TAG = "DistanceDailyFragment";

    private TextView mTvError;

    private TextView mTvOneResultUnit;

    private TextView mTvTwoResultUnit;

    private TextView mTvFourTitle;

    private TextView mTvFourResultUnit;

    private TextView mTvFiveResultUnit;

    private LinearLayout mLayoutThree;

    private TextView mTvTitle;

    private TextView mTvEveryMinuteTitle;

    private RecyclerView mRecyclerView;

    private SampleCaloriesDistanceStrengthAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_calories_daily;
    }

    @Override
    protected void initView(View view) {
        mLayoutThree = view.findViewById(R.id.layout_three);
        mTvTitle = view.findViewById(R.id.tv_realtime_title);
        mTvEveryMinuteTitle = view.findViewById(R.id.tv_step_title);
        mTvTitle.setText("今日运动总距离");
        mTvEveryMinuteTitle.setText("每分钟运动距离");
        mLayoutThree.setVisibility(View.GONE);
        mTvError = view.findViewById(R.id.tv_error_hint);
        mTvOneResultUnit = view.findViewById(R.id.tv_one_result_unit);
        mTvTwoResultUnit = view.findViewById(R.id.tv_two_result_unit);
        mTvFourTitle = view.findViewById(R.id.tv_four_title);
        mTvFourTitle.setText(R.string.sport_climb);
        mTvFourResultUnit = view.findViewById(R.id.tv_four_result_unit);
        mTvFiveResultUnit = view.findViewById(R.id.tv_five_result_unit);
        mRecyclerView = view.findViewById(R.id.every_minute_date_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        getDistanceDailyData();
//        insertEveryMinuteData();
        getEveryMinuteData();
    }

    private void getDistanceDailyData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
                    QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_DISTANCE_STATISTIC, startTime, endTime);
                    HealthKit.getDataStoreClient(getActivity().getApplicationContext()).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                            .addOnSuccessListener(sampleDataQueryResponse -> {
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sampleData -> {
                    Log.i(TAG, "getDistanceDailyData: sampleData = " + sampleData);
                    setTextViewDate((SampleData) sampleData);
                }, throwable -> {
                    LogUtil.i(TAG, "getDistanceDailyData-onFail, height error=" + throwable);
                });
    }

    private void setTextViewDate(SampleData sampleData) {
        if (sampleData == null) {
            return;
        }
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_RUN_NAME) != null) {
            mTvOneResultUnit.setText(sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_RUN_NAME).toString() + " 米");
        } else {
            mTvOneResultUnit.setText(0  + " 米");
        }
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_WALK_NAME) != null) {
            mTvTwoResultUnit.setText(sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_WALK_NAME).toString()  + " 米");
        } else {
            mTvTwoResultUnit.setText(0  + " 米");
        }
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_CLIMB_NAME) != null) {
            mTvFourResultUnit.setText(sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_CLIMB_NAME).toString() + " 米");
        } else {
            mTvFourResultUnit.setText(0  + " 米");
        }
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_OTHER_NAME) != null) {
            mTvFiveResultUnit.setText(sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_OTHER_NAME).toString() + " 米");
        } else{
            mTvFiveResultUnit.setText(0  + " 米");
        }
    }

    private void getEveryMinuteData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_DISTANCE, startTime, endTime);
        Observable.create(emitter ->
                        HealthKit.getDataStoreClient(getActivity().getApplicationContext()).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                                .addOnSuccessListener(sampleDataQueryResponse -> {
                                    List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                                    emitter.onNext(dataList);
                                    emitter.onComplete();
                                }).addOnFailureListener(e -> {
                                    emitter.onError(e);
                                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataList -> {
                    Log.i(TAG, "getEveryMinuteData: dataList = " + dataList);
                    mAdapter = new SampleCaloriesDistanceStrengthAdapter((List<SampleData>) dataList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }, throwable -> {
                    Log.e(TAG, "getEveryMinuteData: error = " + throwable);
                    mRecyclerView.setVisibility(View.GONE);
                    mTvError.setText(throwable.toString());
                    mTvError.setVisibility(View.VISIBLE);
                });

    }

    private void insertEveryMinuteData() {
        // 构造活动距离对象，设置活动距离的开始时间，结束时间，插入活动距离大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_DISTANCE);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1)) ;
        // 开始时间和结束时间相差1分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(1);
        sampleData.setStartTime(startTime);
        sampleData.setEndTime(endTime);
        sampleData.putInteger(DistanceField.FIELD_DISTANCE_NAME, 1000);
        // 构建步数数据列表sampleDataList
        List<SampleData> sampleDataList = new ArrayList<>();
        sampleDataList.add(sampleData);
        Observable.create(emitter ->
                HealthKit.getDataStoreClient(getActivity().getApplicationContext()).insertSampleData(Utils.getHonorSignInAccount(), sampleDataList)
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
