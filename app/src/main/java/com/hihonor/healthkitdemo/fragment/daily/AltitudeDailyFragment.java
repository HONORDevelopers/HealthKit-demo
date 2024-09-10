/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.fragment.daily;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.StepDailyAdapter;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.healthkitdemo.view.CardRoundView;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.AltitudeField;
import com.hihonor.mcs.fitness.health.datastruct.AltitudeStatisticField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 爬高统计和每分钟爬高
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class AltitudeDailyFragment extends BaseFragment {
    private static final String TAG = "AltitudeDailyFragment";

    private CardRoundView mAllStepCard;

    private TextView mTvStep;

    private RecyclerView mRecyclerView;

    private StepDailyAdapter mAdapter;

    private TextView mTvTitle;

    private TextView mTvAltitudeTitle;

    private TextView tvUnit;

    private TextView mTvError;

    public AltitudeDailyFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_daily;
    }

    @Override
    protected void initView(View view) {
        mTvStep = view.findViewById(R.id.tv_current_day_step);
        mRecyclerView = view.findViewById(R.id.every_minute_date_list);
        tvUnit = view.findViewById(R.id.tv_unit);
        mTvTitle = view.findViewById(R.id.tv_realtime_title);
        mTvAltitudeTitle = view.findViewById(R.id.tv_step_title);
        mAllStepCard = view.findViewById(R.id.card_step_daily);
        mTvError = view.findViewById(R.id.tv_error_hint);
        tvUnit.setText(R.string.unit_distance_m);
        mTvTitle.setText("今日总高度");
        mTvAltitudeTitle.setText("每分钟高度");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        insertEveryMinuteData();
        getStepDailyData();
        getEveryMinuteData();
    }

    private void getStepDailyData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
                    QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_ALTITUDE_STATISTIC, startTime, endTime);
                    HealthKit.getDataStoreClient(getActivity().getApplicationContext()).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                            .addOnSuccessListener(sampleDataQueryResponse -> {
                                List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                                if (dataList != null && !dataList.isEmpty()) {
                                    SampleData sampleData = dataList.get(0);
                                    int altitude = 0;
                                    if (sampleData != null && sampleData.getInteger(AltitudeStatisticField.FIELD_ALTITUDE_NAME) != null) {
                                        altitude = sampleData.getInteger(AltitudeStatisticField.FIELD_ALTITUDE_NAME);
                                    }
                                    emitter.onNext(altitude);
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
                .subscribe(altitude -> {
                    Log.i(TAG, "getStepDailyData: altitude = " + altitude);
                    mTvStep.setText(altitude.toString());
                }, throwable -> {
                    LogUtil.i(TAG, "onFail, height error=" + throwable);
                });
    }

    private void getEveryMinuteData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_ALTITUDE, startTime, endTime);
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
                    mAdapter = new StepDailyAdapter((List<SampleData>) dataList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }, throwable -> {
                    Log.e(TAG, "getEveryMinuteData: error = " + throwable);
                    mRecyclerView.setVisibility(View.GONE);
                    mTvError.setVisibility(View.VISIBLE);
                });
    }

    private void insertEveryMinuteData() {
        // 构造对象，设置开始时间，结束时间，插入数据。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_ALTITUDE);
        long curTimeMills = System.currentTimeMillis();
        // 当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1)) ;
        // 开始时间和结束时间相差1分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(1);
        sampleData.setStartTime(startTime);
        sampleData.setEndTime(endTime);
        sampleData.putFloat(AltitudeField.FIELD_HEIGHT_NAME, 520);
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
