/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.SevenDayDataAdapter;
import com.hihonor.healthkitdemo.data.DailySportData;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.ThreadPool;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.CaloriesStatisticField;
import com.hihonor.mcs.fitness.health.datastruct.DistanceStatisticField;
import com.hihonor.mcs.fitness.health.datastruct.RealTimeStepsField;
import com.hihonor.mcs.fitness.health.datastruct.StepStatisticField;
import com.hihonor.mcs.fitness.health.realtimedata.RealTimeDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 日常活动数据界面
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class DailyActivitiesDataActivity extends BaseActivity {
    private static final String TAG = "DailyActivitiesDataActivity_";

    private static final int UPDATE_STEP = 1;

    private static final int RESULT_DAILY_STEP_SUCCESS = 2;

    private static final int RESULT_DAILY_CALORIE_SUCCESS = 3;

    private static final int RESULT_DAILY_DISTANCE_SUCCESS = 4;

    private static final int SEVEN_DAY_STEP_FINISH = 5;

    private static final int SEVEN_DAY_CALORIE_FINISH = 6;

    private static final int SEVEN_DAY_DISTANCE_FINISH = 7;

    private final int SEVEN_DAY_REQUEST_POINT = 3;

    private TextView mTvCurrentDayStep;

    private TextView mTvOneDaySteps;

    private TextView mTvOneDayCalorie;

    private TextView mTvOneDayDistance;

    private RecyclerView mSevenDayView;

    private Button mBtnRefresh;

    private Map<Long, String> mStepMap = new HashMap<>();

    private Map<Long, String> mCalorieMap = new HashMap<>();

    private Map<Long, String> mDistanceMap = new HashMap<>();

    private SevenDayDataAdapter mAdapter;

    private int mSevenDayRequestPoint = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case UPDATE_STEP:
                    if (mTvCurrentDayStep != null) {
                        mTvCurrentDayStep.setText("" + msg.arg1);
                    }
                    break;

                case SEVEN_DAY_STEP_FINISH:
                case SEVEN_DAY_CALORIE_FINISH:
                case SEVEN_DAY_DISTANCE_FINISH:
                    LogUtil.i(TAG, "SEVEN_DAY_FINISH:" + (mSevenDayRequestPoint + 1));
                    sevenDayRequestFinish();
                    break;
                case RESULT_DAILY_STEP_SUCCESS:
                    if (mTvOneDaySteps != null) {
                        mTvOneDaySteps.setText("" + msg.obj);
                        mTvCurrentDayStep.setText("" + msg.obj);
                    }
                    break;
                case RESULT_DAILY_CALORIE_SUCCESS:
                    if (mTvOneDayCalorie != null) {
                        mTvOneDayCalorie.setText("" + msg.obj);
                    }
                    break;
                case RESULT_DAILY_DISTANCE_SUCCESS:
                    if (mTvOneDayDistance != null) {
                        mTvOneDayDistance.setText("" + msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void sevenDayRequestFinish() {
        if (isFinishing()) {
            return;
        }

        mSevenDayRequestPoint++;
        if (mSevenDayRequestPoint != SEVEN_DAY_REQUEST_POINT) {
            return;
        }

        List<DailySportData> list = new ArrayList<>();
        for (Map.Entry entry : mStepMap.entrySet()) {
            if (entry == null) {
                continue;
            }
            long date = (long) entry.getKey();
            DailySportData sportData = new DailySportData();
            sportData.setStartTime(date);
            sportData.setData(TimeUtils.timestamp2Date(date, "yyyy-MM-dd"));
            sportData.setStep((String) entry.getValue());
            if (mCalorieMap.containsKey(date)) {
                sportData.setCalorie(mCalorieMap.get(date));
                mCalorieMap.remove(date);
            } else {
                sportData.setCalorie(0 + "");
            }
            if (mDistanceMap.containsKey(date)) {
                sportData.setDistance(mDistanceMap.get(date));
                mDistanceMap.remove(date);
            } else {
                sportData.setDistance(0 + "");
            }
            list.add(sportData);
        }

        for (Map.Entry entry : mCalorieMap.entrySet()) {
            if (entry == null) {
                continue;
            }
            long date = (long) entry.getKey();
            DailySportData sportData = new DailySportData();
            sportData.setStartTime(date);
            sportData.setData(TimeUtils.timestamp2Date(date, "yyyy-MM-dd"));
            sportData.setStep(0 + "");
            sportData.setCalorie((String) entry.getValue());
            if (mDistanceMap.containsKey(date)) {
                sportData.setDistance(mDistanceMap.get(date));
                mDistanceMap.remove(date);
            } else {
                sportData.setDistance(0 + "");
            }
            list.add(sportData);
        }

        for (Map.Entry entry : mDistanceMap.entrySet()) {
            if (entry == null) {
                continue;
            }
            long date = (long) entry.getKey();
            DailySportData sportData = new DailySportData();
            sportData.setStartTime(date);
            sportData.setData(TimeUtils.timestamp2Date(date, "yyyy-MM-dd"));
            sportData.setStep(0 + "");
            sportData.setCalorie(0 + "");
            sportData.setDistance((String) entry.getValue());
            list.add(sportData);
        }
        list.sort((t1, t2) -> Long.compare(t2.getStartTime(), t1.getStartTime()));

        if (mAdapter != null) {
            mAdapter.setRecordList(list);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_activities_data);
        initView();
        registerStep();
        getDailyData();
        getSevenDayData();
    }

    private void initView() {
        mTvCurrentDayStep = findViewById(R.id.tv_current_day_step);
        mTvOneDaySteps = findViewById(R.id.tv_one_day_step);
        mTvOneDayCalorie = findViewById(R.id.tv_one_day_calorie);
        mTvOneDayDistance = findViewById(R.id.tv_one_day_distance);
        mSevenDayView = findViewById(R.id.daily_record_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSevenDayView.setLayoutManager(layoutManager);
        mSevenDayView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new SevenDayDataAdapter();
        mSevenDayView.setAdapter(mAdapter);
        mBtnRefresh = findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDailyData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unRegisterStep();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private RealTimeDataListener mRealTimeStepListener = new RealTimeDataListener() {
        @Override
        public void onResult(SampleData sampleData) {
            if (isFinishing()) {
                return;
            }
            if (mTvCurrentDayStep == null || mHandler == null) {
                return;
            }
            if (sampleData.getDataType() != DataType.SAMPLE_STEPS_REALTIME) {
                return;
            }
            if (sampleData.getInteger(RealTimeStepsField.FIELD_STEP_NAME) != null) {
                Message message = Message.obtain();
                message.what = UPDATE_STEP;
                message.arg1 = sampleData.getInteger(RealTimeStepsField.FIELD_STEP_NAME);
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void onFail(int i, String s) {
            LogUtil.i(TAG, "onFail, errorCode=" + i + ", errorMsg=" + s);
        }
    };

    private void registerStep() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int registerType = DataType.SAMPLE_STEPS_REALTIME;
                HealthKit.getRealTimeDataClient(DailyActivitiesDataActivity.this)
                        .registerRealTimeDataListener(Utils.getHonorSignInAccount(), registerType, mRealTimeStepListener)
                        .addOnSuccessListener(unused -> {
                            LogUtil.i(TAG, "register realTimeStepListener onSuccess");
                        })
                        .addOnFailureListener(e -> {
                            LogUtil.i(TAG, "register realTimeStepListener onFail, error=" + e);
                        });
            }
        });
    }

    private void unRegisterStep() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int registerType = DataType.SAMPLE_STEPS_REALTIME;
                HealthKit.getRealTimeDataClient(DailyActivitiesDataActivity.this)
                    .unregisterRealTimeDataListener(Utils.getHonorSignInAccount(), registerType)
                    .addOnSuccessListener(unused -> {
                        LogUtil.i(TAG, "unRegister onSuccess");
                    })
                    .addOnFailureListener(e -> {
                        LogUtil.i(TAG, "unRegister onFail, error=" + e);
                    });
            }
        });
    }

    private void getDailyData() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long endTime = startTime;
                QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STEPS_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            SampleData sampleData = dataList.get(0);
                            int step = 0;
                            if (sampleData != null && sampleData.getInteger(StepStatisticField.FIELD_STEP_NAME) != null) {
                                step = sampleData.getInteger(StepStatisticField.FIELD_STEP_NAME);
                            }
                            Message message = Message.obtain();
                            message.what = RESULT_DAILY_STEP_SUCCESS;
                            message.obj = step;
                            mHandler.sendMessage(message);
                        }
                    })
                    .addOnFailureListener(e -> {
                        LogUtil.i(TAG, "onFail, step error=" + e);
                    });

                QueryRequest queryRequestCalories = new QueryRequest(DataType.SAMPLE_CALORIES_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestCalories)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            SampleData sampleData = dataList.get(0);
                            long total = getTotalCalorie(sampleData);
                            LogUtil.i(TAG, "onFail, calorie total =" + total);
                            Message message = Message.obtain();
                            message.what = RESULT_DAILY_CALORIE_SUCCESS;
                            message.obj = total;
                            mHandler.sendMessage(message);
                        }
                    })
                    .addOnFailureListener(e -> {
                        LogUtil.i(TAG, "onFail, calorie error=" + e);
                    });

                QueryRequest queryRequestDistance = new QueryRequest(DataType.SAMPLE_DISTANCE_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestDistance)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            SampleData sampleData = dataList.get(0);
                            long total = getTotalDistance(sampleData);
                            Message message = Message.obtain();
                            message.what = RESULT_DAILY_DISTANCE_SUCCESS;
                            message.obj = total;
                            mHandler.sendMessage(message);
                        }
                    })
                    .addOnFailureListener(e -> {
                        LogUtil.i(TAG, "onFail, distance error=" + e);
                    });
            }
        });
    }

    private void getSevenDayData() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                long endTime = System.currentTimeMillis();
                long startTime = endTime -  TimeUnit.DAYS.toMillis(27);
                QueryRequest queryRequestStep = new QueryRequest(DataType.SAMPLE_STEPS_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestStep)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList == null || dataList.isEmpty()) {
                            return;
                        }
                        for (SampleData data : dataList) {
                            if (data == null) {
                                continue;
                            }
                            int step = 0;
                            if (data.getInteger(StepStatisticField.FIELD_STEP_NAME) != null) {
                                step = data.getInteger(StepStatisticField.FIELD_STEP_NAME);
                            }
                            long time = data.getStartTime();
                            mStepMap.put(time, "" +step);
                        }
                        mHandler.sendEmptyMessage(SEVEN_DAY_STEP_FINISH);
                    })
                    .addOnFailureListener(e -> {
                        mHandler.sendEmptyMessage(SEVEN_DAY_STEP_FINISH);
                    });

                QueryRequest queryRequestCalories = new QueryRequest(DataType.SAMPLE_CALORIES_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestCalories)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList == null || dataList.isEmpty()) {
                            return;
                        }
                        for (SampleData data : dataList) {
                            if (data == null) {
                                continue;
                            }
                            long time = data.getStartTime();
                            long total = getTotalCalorie(data);
                            mCalorieMap.put(time, "" +total);
                        }
                        mHandler.sendEmptyMessage(SEVEN_DAY_CALORIE_FINISH);
                    })
                    .addOnFailureListener(e -> {
                        mHandler.sendEmptyMessage(SEVEN_DAY_CALORIE_FINISH);
                    });

                QueryRequest queryRequestDistance = new QueryRequest(DataType.SAMPLE_DISTANCE_STATISTIC, startTime, endTime);
                HealthKit.getDataStoreClient(DailyActivitiesDataActivity.this).querySampleData(Utils.getHonorSignInAccount(), queryRequestDistance)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        List<SampleData> dataList = sampleDataQueryResponse.getDataList();
                        if (dataList == null || dataList.isEmpty()) {
                            return;
                        }

                        for (SampleData data : dataList) {
                            if (data == null) {
                                continue;
                            }
                            long time = data.getStartTime();
                            long total = getTotalDistance(data);
                            mDistanceMap.put(time, "" +total);
                        }
                        mHandler.sendEmptyMessage(SEVEN_DAY_DISTANCE_FINISH);
                    })
                    .addOnFailureListener(e -> {
                        mHandler.sendEmptyMessage(SEVEN_DAY_DISTANCE_FINISH);
                    });
            }
        });
    }

    private long getTotalDistance(SampleData sampleData) {
        if (sampleData == null) {
            return 0;
        }

        int climbDistance = 0;
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_CLIMB_NAME) != null) {
            climbDistance = sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_CLIMB_NAME);
        }

        int runDistance = 0;
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_RUN_NAME) != null) {
            runDistance = sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_RUN_NAME);
        }

        int walkDistance = 0;
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_WALK_NAME) != null) {
            walkDistance = sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_WALK_NAME);
        }

        int otherDistance = 0;
        if (sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_OTHER_NAME) != null) {
            otherDistance = sampleData.getInteger(DistanceStatisticField.FIELD_DISTANCE_OTHER_NAME);
        }

        return climbDistance + runDistance + walkDistance + otherDistance;
    }

    private long getTotalCalorie(SampleData sampleData) {
        if (sampleData == null) {
            return 0;
        }

        int excise = 0;
        if (sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_EXCISE_NAME) != null) {
            excise = sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_EXCISE_NAME);
        }

        int walk = 0;
        if (sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_WALK_NAME) != null) {
            walk = sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_WALK_NAME);
        }

        int ride = 0;
        if (sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_RIDE_NAME) != null) {
            ride = sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_RIDE_NAME);
        }

        int run = 0;
        if (sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_RUN_NAME) != null) {
            run = sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_RUN_NAME);
        }

        int other = 0;
        if (sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_OTHER_NAME) != null) {
            other = sampleData.getInteger(CaloriesStatisticField.FIELD_CALORIES_OTHER_NAME);
        }

        return (excise + walk + ride + run + other);
    }
}
