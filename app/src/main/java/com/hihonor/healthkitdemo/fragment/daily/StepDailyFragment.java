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
import com.hihonor.mcs.fitness.health.datastore.InsertResponse;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.StepField;
import com.hihonor.mcs.fitness.health.datastruct.StepStatisticField;
import com.hihonor.mcs.fitness.health.exception.HealthKitException;
import com.hihonor.mcs.fitness.health.task.OnFailureListener;
import com.hihonor.mcs.fitness.health.task.OnSuccessListener;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 步数统计和每分钟步数
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StepDailyFragment extends BaseFragment {
    private static final String TAG = "StepDailyFragment";

    private CardRoundView mAllStepCard;

    private TextView mTvStep;

    private RecyclerView mRecyclerView;

    private StepDailyAdapter mAdapter;

    private TextView mTvError;

    public StepDailyFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_daily;
    }

    @Override
    protected void initView(View view) {
        mTvStep = view.findViewById(R.id.tv_current_day_step);
        mRecyclerView = view.findViewById(R.id.every_minute_date_list);
        mAllStepCard = view.findViewById(R.id.card_step_daily);
        mTvError = view.findViewById(R.id.tv_error_hint);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        getStepDailyData();
        getEveryMinuteData();
    }

    private void insertStepData() {
        // 构造步数对象，设置步数的开始时间，结束时间，插入步数大小。
        SampleData sampleData = new SampleData();
        sampleData.setDataType(DataType.SAMPLE_STEPS);
        long curTimeMills = System.currentTimeMillis();
        // 取当前时间的分钟毫秒值
        long endTime = curTimeMills - (curTimeMills % TimeUnit.MINUTES.toMillis(1)) ;
        // 开始时间和结束时间相差1分钟
        long startTime = endTime - TimeUnit.MINUTES.toMillis(1);
        sampleData.setStartTime(startTime);
        sampleData.setEndTime(endTime);
        sampleData.putInteger(StepField.FIELD_STEP_NAME, 100);
        // 构建步数数据列表sampleDataList
        List<SampleData> sampleDataList = new ArrayList<>();
        sampleDataList.add(sampleData);


        // 调用insertSampleData接口， 将步数数据插入到Health Kit中
        HealthKit.getDataStoreClient(getActivity().getApplicationContext()).insertSampleData(Utils.getHonorSignInAccount(), sampleDataList)
                .addOnSuccessListener(new OnSuccessListener<InsertResponse>() {
                    @Override
                    public void onSuccess(InsertResponse insertResponse) {
                        // 插入数据成功回调方法，insertResponse包含插入成功数据的id。
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // 插入数据失败相应的处理逻辑。
                        int errorCode = ((HealthKitException) e).getErrorCode();
                        String errorMsg = e.getMessage();
                    }
                });
    }

    private void getStepDailyData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        Observable.create(emitter -> {
                    QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STEPS_STATISTIC, startTime, endTime);
                    HealthKit.getDataStoreClient(getActivity().getApplicationContext()).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                            .addOnSuccessListener(sampleDataQueryResponse -> {
                                List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                                if (dataList != null && !dataList.isEmpty()) {
                                    SampleData sampleData = dataList.get(0);
                                    int step = 0;
                                    if (sampleData != null && sampleData.getInteger(StepStatisticField.FIELD_STEP_NAME) != null) {
                                        step = sampleData.getInteger(StepStatisticField.FIELD_STEP_NAME);
                                    }
                                    emitter.onNext(step);
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
                .subscribe(step -> {
                    Log.i(TAG, "getStepDailyData: step = " + step);
                    mTvStep.setText(step.toString());
                }, throwable -> {
                    LogUtil.i(TAG, "onFail, step error=" + throwable);
                });
    }

    private void getEveryMinuteData() {
        long currentTime = System.currentTimeMillis();
        long startTime = TimeUtils.getMinTimeMillis(currentTime);
        long endTime = TimeUtils.getMaxTimeMillis(currentTime);
        QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STEPS, startTime, endTime);
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
                    mTvError.setText(throwable.toString());
                    mTvError.setVisibility(View.VISIBLE);
                });

    }
}
