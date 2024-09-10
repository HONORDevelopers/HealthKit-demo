/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.SleepListAdapter;
import com.hihonor.healthkitdemo.data.SleepData;
import com.hihonor.healthkitdemo.data.SleepSegmentData;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.ThreadPool;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.HonorSignInAccount;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.data.SampleRecord;
import com.hihonor.mcs.fitness.health.data.SummaryData;
import com.hihonor.mcs.fitness.health.datastore.QueryRequest;
import com.hihonor.mcs.fitness.health.datastruct.NapSleepField;
import com.hihonor.mcs.fitness.health.datastruct.NightSleepField;
import com.hihonor.mcs.fitness.health.datastruct.SleepField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 历史睡眠记录
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepActivity extends BaseActivity implements SleepListAdapter.ISleepListItem {
    private static final String TAG = SleepActivity.class.getSimpleName();

    private static final int RESULT_SUCCESS = 1;

    private static final int RESULT_FAIL = 2;

    private RecyclerView mListView;

    private TextView mHint;

    private List<SleepData> mSleepList = new ArrayList<>(16);

    private SleepListAdapter mSleepAdapter;

    private String mFailMessage;

    private List<SampleRecord> mRecordList;

    private Map<Integer, List<SampleRecord>> mDataMap = new HashMap<>();

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case RESULT_SUCCESS:
                    parseData(mDataMap);
                    break;
                case RESULT_FAIL:
                    if (mHint != null) {
                        hideList();
                        mHint.setText(mFailMessage);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        initView();
    }

    private void initView() {
        View rootView = getLayoutInflater().inflate(R.layout.activity_sleep, null);
        if (rootView == null) {
            LogUtil.e(TAG, "root view is null");
            return;
        }

        mListView = rootView.findViewById(R.id.sleep_list);
        mHint = rootView.findViewById(R.id.sleep_hint);
        if (mListView == null) {
            LogUtil.e(TAG, "sleep list is null");
            return;
        }
        setContentView(rootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        mListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSleepAdapter = new SleepListAdapter(mSleepList);
        mSleepAdapter.setOnItemClickListener(this::onClick);
        mListView.setAdapter(mSleepAdapter);
        // insertEveryMinuteData();
        querySleepData();
    }

    private void querySleepData() {
        mSleepList.clear();
        mRecordList = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - TimeUnit.DAYS.toMillis(30);
        QueryRequest queryRequest = new QueryRequest(DataType.RECORD_SLEEP, startTime, endTime);
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                HonorSignInAccount account = Utils.getHonorSignInAccount();
                HealthKit.getDataStoreClient(SleepActivity.this)
                    .querySampleRecord(account, queryRequest)
                    .addOnSuccessListener(sampleDataQueryResponse -> {
                        LogUtil.i(TAG, "get sleep data success");
                        mRecordList = sampleDataQueryResponse.getDataList();
                        mDataMap.put(sampleDataQueryResponse.getIndex(), mRecordList);
                        boolean isTotal =
                            (sampleDataQueryResponse.getTotal()) == (sampleDataQueryResponse.getIndex() + 1);
                        if (isTotal) {
                            mHandler.sendEmptyMessage(RESULT_SUCCESS);
                        }
                    })
                    .addOnFailureListener(e -> {
                        LogUtil.e(TAG, "get sleep data fail e: " + e);
                        mFailMessage = getString(R.string.none_error)
                            + (Utils.parseErrorInfo(e).isEmpty() ? "" : ": " + Utils.parseErrorInfo(e));
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(RESULT_FAIL);
                        }
                    });
            }
        });
    }

    private void showList() {
        mListView.setVisibility(View.VISIBLE);
        mHint.setVisibility(View.GONE);
    }

    private void hideList() {
        mListView.setVisibility(View.GONE);
        mHint.setVisibility(View.VISIBLE);
    }

    private void parseData(Map<Integer, List<SampleRecord>> mDataMap) {
        if (isFinishing()) {
            return;
        }
        if (mDataMap == null || mDataMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, List<SampleRecord>> entry : mDataMap.entrySet()) {
            List<SampleRecord> recordList = entry.getValue();
            if (recordList == null) {
                continue;
            }
            if (recordList == null || recordList.isEmpty()) {
                mHint.setText(R.string.none_data);
                hideList();
                return;
            }
            showList();
            for (SampleRecord record : recordList) {
                if (record == null) {
                    continue;
                }
                SummaryData summaryData = record.getSummary();

                SleepData sleepData = new SleepData();
                sleepData.setStartTime(record.getStartTime());
                sleepData.setEndTime(record.getEndTime());
                if (summaryData.getLong(SleepField.FIELD_SLEEP_TIMESTAMP_NAME) != null) {
                    sleepData.setSleepTimestamp(
                        TimeUtils.timestamp2DateMinute(summaryData.getLong(SleepField.FIELD_SLEEP_TIMESTAMP_NAME)));
                }
                if (summaryData.getLong(SleepField.FIELD_WAKEUP_TIMESTAMP_NAME) != null) {
                    sleepData.setWakeupTimestamp(
                        TimeUtils.timestamp2DateMinute(summaryData.getLong(SleepField.FIELD_WAKEUP_TIMESTAMP_NAME)));
                }
                if (summaryData.getInteger(SleepField.FIELD_DEEP_SLEEP_NAME) != null) {
                    sleepData.setDeepSleep(summaryData.getInteger(SleepField.FIELD_DEEP_SLEEP_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_LIGHT_SLEEP_NAME) != null) {
                    sleepData.setLightSleep(summaryData.getInteger(SleepField.FIELD_LIGHT_SLEEP_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_REM_SLEEP_NAME) != null) {
                    sleepData.setRemSleep(summaryData.getInteger(SleepField.FIELD_REM_SLEEP_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_WIDE_AWAKE_NAME) != null) {
                    sleepData.setWideAwake(summaryData.getInteger(SleepField.FIELD_WIDE_AWAKE_NAME));
                }

                if (summaryData.getInteger(SleepField.FIELD_AWAKE_TIMES_NAME) != null) {
                    sleepData.setAwakeTimes(summaryData.getInteger(SleepField.FIELD_AWAKE_TIMES_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_SLEEP_TYPE_NAME) != null) {
                    sleepData.setSleepType(summaryData.getInteger(SleepField.FIELD_SLEEP_TYPE_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_SPORADIC_NAPS_NAME) != null) {
                    sleepData.setSporadicNaps(summaryData.getInteger(SleepField.FIELD_SPORADIC_NAPS_NAME));
                }

                if (summaryData.getInteger(SleepField.FIELD_SLEEP_QUALITY_NAME) != null) {
                    sleepData.setSleepQuality(summaryData.getInteger(SleepField.FIELD_SLEEP_QUALITY_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_RESPIRATORY_QUALITY_NAME) != null) {
                    sleepData.setRespiratoryQuality(summaryData.getInteger(SleepField.FIELD_RESPIRATORY_QUALITY_NAME));
                }
                if (summaryData.getInteger(SleepField.FIELD_DEEP_SLEEP_CONTINUE_NAME) != null) {
                    sleepData.setDeepSleepContinue(summaryData.getInteger(SleepField.FIELD_DEEP_SLEEP_CONTINUE_NAME));
                }

                Map<Integer, List<SampleData>> detailMap = record.getDetailMap();
                if (detailMap == null) {
                    return;
                }
                List<SleepSegmentData> nightSegmentList = new ArrayList<>(16);
                List<SleepSegmentData> napSegmentList = new ArrayList<>(16);
                for (Map.Entry<Integer, List<SampleData>> detailEntry : detailMap.entrySet()) {
                    List<SampleData> detailSamples = detailEntry.getValue();
                    for (SampleData detail : detailSamples) {
                        if (detail == null) {
                            continue;
                        }
                        SleepSegmentData sleepSegmentData = new SleepSegmentData();
                        sleepSegmentData.setStartTime(detail.getStartTime());
                        sleepSegmentData.setEndTime(detail.getEndTime());
                        if (detail.getInteger(NapSleepField.FIELD_DURATION_NAME) != null) {
                            sleepSegmentData.setDuration(detail.getInteger(NapSleepField.FIELD_DURATION_NAME));
                        }
                        if (detail.getInteger(NapSleepField.FIELD_STATUS_NAME) != null) {
                            sleepSegmentData.setStatus(detail.getInteger(NapSleepField.FIELD_STATUS_NAME));
                        }
                        if (detail.getDataType() == DataType.SAMPLE_NIGHT_SLEEP) {
                            nightSegmentList.add(sleepSegmentData);
                        } else if (detail.getDataType() == DataType.SAMPLE_NAP_SLEEP) {
                            napSegmentList.add(sleepSegmentData);
                        }
                    }
                }
                if (!nightSegmentList.isEmpty()) {
                    nightSegmentList.sort((t1, t2) -> Long.compare(t2.getStartTime(), t1.getStartTime()));
                    sleepData.setNightSegmentList(nightSegmentList);
                }
                if (!napSegmentList.isEmpty()) {
                    napSegmentList.sort((t1, t2) -> Long.compare(t2.getStartTime(), t1.getStartTime()));
                    sleepData.setNapSegmentList(napSegmentList);
                }
                mSleepList.add(sleepData);
            }
        }
        mSleepList.sort((t1, t2) -> Long.compare(t2.getStartTime(), t1.getStartTime()));
        if (mSleepAdapter != null) {
            mSleepAdapter.setDataList(mSleepList);
            mSleepAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(int position) {
        if (position < 0 || position > mSleepList.size()) {
            return;
        }
        SleepData data = mSleepList.get(position);
        Intent intent = new Intent(this, SleepDetailActivity.class);
        intent.putExtra(SleepDetailActivity.SLEEP_DETAIL_INFO, data);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void insertEveryMinuteData() {
        long simpleTime = 24 * 60 * 60 * 1000;
        long cur = System.currentTimeMillis();
        long zeroTime = cur - cur % simpleTime - 8 * 60 * 60 * 1000 - TimeUnit.DAYS.toMillis(1);
        long startTime = zeroTime + TimeUnit.HOURS.toMillis(1);
        long endTime = zeroTime + TimeUnit.HOURS.toMillis(6);

        // 设置睡眠记录汇总信息SummaryData。
        SummaryData summaryData = new SummaryData();
        summaryData.putInteger(SleepField.FIELD_DEEP_SLEEP_NAME, 120);
        summaryData.putInteger(SleepField.FIELD_LIGHT_SLEEP_NAME, 60);
        summaryData.putInteger(SleepField.FIELD_REM_SLEEP_NAME, 30);
        summaryData.putInteger(SleepField.FIELD_WIDE_AWAKE_NAME, 20);
        summaryData.putInteger(SleepField.FIELD_AWAKE_TIMES_NAME, 2);
        summaryData.putInteger(SleepField.FIELD_SLEEP_TYPE_NAME, 1);
        summaryData.putInteger(SleepField.FIELD_SPORADIC_NAPS_NAME, 60);
        summaryData.putInteger(SleepField.FIELD_SLEEP_QUALITY_NAME, 76);
        summaryData.putInteger(SleepField.FIELD_RESPIRATORY_QUALITY_NAME, 96);
        summaryData.putInteger(SleepField.FIELD_DEEP_SLEEP_CONTINUE_NAME, 66);
        summaryData.putLong(SleepField.FIELD_SLEEP_TIMESTAMP_NAME, startTime);
        summaryData.putLong(SleepField.FIELD_WAKEUP_TIMESTAMP_NAME, endTime);

        // 设置睡眠记录信息，开始时间StartTime和结束时间EndTime,开始时间和结束时间为同一天，为毫米级时间戳。
        SampleRecord sampleRecord = new SampleRecord();
        sampleRecord.setDataType(DataType.RECORD_SLEEP);
        sampleRecord.setStartTime(startTime);
        sampleRecord.setEndTime(endTime);
        sampleRecord.setSummary(summaryData);

        // 设置睡眠记录关联信息，关联信息的开始时间和结束时间包含在运动记录信息的开始时间StartTime和结束时间EndTime内。
        List<SampleData> detailNight = new ArrayList<>();
        List<SampleData> detailNap = new ArrayList<>();

        // 设置夜间睡眠关联信息。
        SampleData nightSleepSampleData = new SampleData();
        nightSleepSampleData.setDataType(DataType.SAMPLE_NIGHT_SLEEP);
        long nightStartTime = startTime + TimeUnit.HOURS.toMillis(1);
        long nightEndTime = startTime + TimeUnit.HOURS.toMillis(3);
        nightSleepSampleData.setStartTime(nightStartTime);
        nightSleepSampleData.setEndTime(nightEndTime);
        nightSleepSampleData.putInteger(NightSleepField.FIELD_STATUS_NAME, 7);
        nightSleepSampleData.putInteger(NightSleepField.FIELD_DURATION_NAME,
            (int) ((nightEndTime - nightStartTime) / 1000));
        detailNight.add(nightSleepSampleData);

        // 设置零星小睡关联信息
        SampleData napSleepSampleData = new SampleData();
        napSleepSampleData.setDataType(DataType.SAMPLE_NAP_SLEEP);
        long napStartTime = startTime + TimeUnit.HOURS.toMillis(3);
        long napEndTime = startTime + TimeUnit.HOURS.toMillis(4);
        napSleepSampleData.setStartTime(napStartTime);
        napSleepSampleData.setEndTime(napEndTime);
        napSleepSampleData.putInteger(NapSleepField.FIELD_STATUS_NAME, 6);
        napSleepSampleData.putInteger(NapSleepField.FIELD_DURATION_NAME, (int) ((napEndTime - napStartTime) / 1000));
        detailNap.add(napSleepSampleData);

        sampleRecord.putDetail(DataType.SAMPLE_NIGHT_SLEEP, detailNight);
        sampleRecord.putDetail(DataType.SAMPLE_NAP_SLEEP, detailNap);

        // 构建睡眠记录数据列表sampleRecordList。
        List<SampleRecord> sampleRecordList = new ArrayList<>();
        sampleRecordList.add(sampleRecord);

        Observable.create(emitter -> HealthKit.getDataStoreClient(this)
            .insertSampleRecord(Utils.getHonorSignInAccount(), sampleRecordList)
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