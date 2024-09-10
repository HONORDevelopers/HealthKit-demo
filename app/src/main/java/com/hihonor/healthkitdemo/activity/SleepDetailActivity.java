/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.SleepDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.SummaryListAdapter;
import com.hihonor.healthkitdemo.data.SleepData;
import com.hihonor.healthkitdemo.data.SleepSegmentData;
import com.hihonor.healthkitdemo.data.SummaryItemData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠详情界面
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepDetailActivity extends BaseActivity {
    public static final String SLEEP_DETAIL_INFO = SleepDetailActivity.class.getSimpleName();

    private TextView mNightTime;

    private TextView mNightIn;

    private TextView mNightOut;

    private RecyclerView mSummaryRecyclerView;

    private RecyclerView mNightRecyclerView;

    private RecyclerView mNapRecyclerView;

    private List<SummaryItemData> mSummaryItemList = new ArrayList<>(16);

    private List<SleepSegmentData> mNightSegmentList = new ArrayList<>(16);

    private List<SleepSegmentData> mNapSegmentList = new ArrayList<>(16);

    private SummaryListAdapter mSummaryListAdapter = new SummaryListAdapter();

    private SleepDetailListAdapter mNightDetailAdapter = new SleepDetailListAdapter();

    private SleepDetailListAdapter mNapDetailAdapter = new SleepDetailListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_detail);
        Intent intent = getIntent();
        SleepData data = (SleepData) intent.getSerializableExtra(SLEEP_DETAIL_INFO);
        if (data == null) {
            return;
        }

        initView();
        loadData(data);
    }

    private void initView() {
        mNightTime = findViewById(R.id.sleep_summay_night);
        mNightIn = findViewById(R.id.sleep_summay_in);
        mNightOut = findViewById(R.id.sleep_summay_out);

        mSummaryRecyclerView = findViewById(R.id.sleep_summay_list);
        mNightRecyclerView = findViewById(R.id.night_sleep_list);
        mNapRecyclerView = findViewById(R.id.nap_sleep_list);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mSummaryRecyclerView.setLayoutManager(layoutManager1);
        mSummaryRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mSummaryRecyclerView.setAdapter(mSummaryListAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mNightRecyclerView.setLayoutManager(layoutManager2);
        mNightRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mNightRecyclerView.setAdapter(mNightDetailAdapter);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        layoutManager3.setOrientation(LinearLayoutManager.VERTICAL);
        mNapRecyclerView.setLayoutManager(layoutManager3);
        mNapRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mNapRecyclerView.setAdapter(mNapDetailAdapter);
    }

    private void loadData(SleepData data) {
        mNightTime.setText(
            TimeUtils.formatDurationFromMinutes(this, data.getDeepSleep() + data.getLightSleep() + data.getRemSleep()));
        mNightIn.setText(data.getSleepTimestamp());
        mNightOut.setText(data.getWakeupTimestamp());

        mSummaryItemList.clear();

        mSummaryItemList.add(
            new SummaryItemData(getString(R.string.deep_sleep), data.getDeepSleep(), getString(R.string.unit_minutes)));
        mSummaryItemList.add(new SummaryItemData(getString(R.string.light_sleep), data.getLightSleep(),
            getString(R.string.unit_minutes)));
        mSummaryItemList.add(
            new SummaryItemData(getString(R.string.rem_sleep), data.getRemSleep(), getString(R.string.unit_minutes)));
        mSummaryItemList.add(
            new SummaryItemData(getString(R.string.wide_awake), data.getWideAwake(), getString(R.string.unit_minutes)));

        mSummaryItemList.add(
            new SummaryItemData(getString(R.string.awake_times), data.getAwakeTimes(), getString(R.string.unit_times)));

        mSummaryItemList.add(new SummaryItemData(getString(R.string.sleep_quality), data.getSleepQuality(),
            getString(R.string.unit_score)));
        mSummaryItemList.add(new SummaryItemData(getString(R.string.respiratory_quality), data.getRespiratoryQuality(),
            getString(R.string.unit_score)));
        mSummaryItemList.add(new SummaryItemData(getString(R.string.deep_sleep_continue), data.getDeepSleepContinue(),
            getString(R.string.unit_score)));

        mSummaryListAdapter.setDataList(mSummaryItemList);
        mSummaryListAdapter.notifyDataSetChanged();

        mNightSegmentList = data.getNightSegmentList();
        mNightDetailAdapter.setData(mNightSegmentList);
        mNightDetailAdapter.notifyDataSetChanged();

        mNapSegmentList = data.getNapSegmentList();
        mNapDetailAdapter.setData(mNapSegmentList);
        mNapDetailAdapter.notifyDataSetChanged();
    }
}