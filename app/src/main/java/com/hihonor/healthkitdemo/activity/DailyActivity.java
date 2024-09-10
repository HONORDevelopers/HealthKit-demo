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
import com.hihonor.healthkitdemo.adapter.DailyAdapter;
import com.hihonor.mcs.fitness.health.constants.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * 日常活动数据
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class DailyActivity extends BaseActivity {
    private static final String TAG = "DailyActivity_";
    private static final int[] DATA_TYPE = {DataType.SAMPLE_STEPS_STATISTIC, DataType.SAMPLE_STRENGTH_STATISTIC,
        DataType.SAMPLE_DISTANCE_STATISTIC, DataType.SAMPLE_CALORIES_STATISTIC, DataType.SAMPLE_ALTITUDE_STATISTIC};

    private List<String> mListData;

    private RecyclerView mRecyclerView;

    private DailyAdapter mDailyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.daily_activity_list);
        initData();
    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add("步数");
        mListData.add("中高强度");
        mListData.add("距离");
        mListData.add("热量");
        mListData.add("爬楼");
        mDailyAdapter = new DailyAdapter(mListData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mDailyAdapter);
        mDailyAdapter.setOnItemClickListener(position -> {
            if (position >= DATA_TYPE.length) {
                Log.e(TAG, "initData: position is error");
                return;
            }
            DailyDetailsActivity.startToDailyDetailsActivity(this, DATA_TYPE[position]);
        });
    }
}
