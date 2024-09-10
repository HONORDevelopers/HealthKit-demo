/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.SportRecordListAdapter;
import com.hihonor.healthkitdemo.data.SportIndoorRunningData;
import com.hihonor.healthkitdemo.data.SportOutdoorRunningData;
import com.hihonor.healthkitdemo.data.SportRecordData;
import com.hihonor.healthkitdemo.data.SportRidingData;
import com.hihonor.healthkitdemo.data.SportWalkData;
import com.hihonor.healthkitdemo.helper.ISportResultHelper;
import com.hihonor.healthkitdemo.helper.SportIndoorHelper;
import com.hihonor.healthkitdemo.helper.SportJumpRopeHelper;
import com.hihonor.healthkitdemo.helper.SportOutdoorHelper;
import com.hihonor.healthkitdemo.helper.SportRindingHelper;
import com.hihonor.healthkitdemo.helper.SportWalkHelper;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.constants.DataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 历史运动记录界面
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class HistorySportDataActivity extends BaseActivity
    implements SportRecordListAdapter.ISportRecordListItem, ISportResultHelper {
    private static final int REQUEST_TOTAL_TIMES = 4;

    private static final String RECORD_DATE_FORMAT = "yyyy-MM-dd";

    private SportIndoorHelper indoorHelper;

    private SportOutdoorHelper outdoorHelper;

    private SportRindingHelper rindingHelper;

    private SportWalkHelper walkHelper;

    private Map<Long, SportIndoorRunningData> indoorDataMap = new HashMap<>();

    private Map<Long, SportRidingData> rindingDataMap = new HashMap<>();

    private Map<Long, SportWalkData> walkDataMap = new HashMap<>();

    private Map<Long, SportOutdoorRunningData> outdoorDataMap = new HashMap<>();

    private List<SportRecordData> mRecordList = new ArrayList<>(16);

    private RecyclerView mListView;

    private TextView mHint;

    private SportRecordListAdapter mAdapter;

    private int count = 0;

    private int failCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        initView();
        initData();
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
        mAdapter = new SportRecordListAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        hideList();
    }

    private void initData() {
        count = 0;
        failCount = 0;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - TimeUnit.DAYS.toMillis(7);

        indoorHelper = new SportIndoorHelper(this);
        indoorHelper.setSportResult(this);
        indoorHelper.setRequestTime(startTime, endTime);
//        indoorHelper.insertData();
        indoorHelper.getIndoorData();

        outdoorHelper = new SportOutdoorHelper(this);
        outdoorHelper.setSportResult(this);
        outdoorHelper.setRequestTime(startTime, endTime);
//        outdoorHelper.insertData();
        outdoorHelper.getOutdoorData();

        rindingHelper = new SportRindingHelper(this);
        rindingHelper.setSportResult(this);
        rindingHelper.setRequestTime(startTime, endTime);
//        rindingHelper.insertData();
        rindingHelper.getRindingData();

        walkHelper = new SportWalkHelper(this);
        walkHelper.setSportResult(this);
        walkHelper.setRequestTime(startTime, endTime);
//        walkHelper.insertData();
        walkHelper.getWalkData();

        SportJumpRopeHelper jumpRopeHelper = new SportJumpRopeHelper(this);
        jumpRopeHelper.setRequestTime(startTime, endTime);
//        jumpRopeHelper.insertData();
        jumpRopeHelper.getJumpRopeData();
    }

    private void showList() {
        mListView.setVisibility(View.VISIBLE);
        mHint.setVisibility(View.GONE);
    }

    private void hideList() {
        mListView.setVisibility(View.GONE);
        mHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(int type) {
        count++;
        checkRequestFinish();
    }

    @Override
    public void onFail(int type, Exception e) {
        count++;
        failCount++;
        if (failCount == REQUEST_TOTAL_TIMES) {
            checkRequestFail(e);
        } else {
            checkRequestFinish();
        }
    }

    private void checkRequestFail(Exception e) {
        failCount = 0;
        count = 0;
        hideList();
        String errorInfo =
            getString(R.string.none_error) + (Utils.parseErrorInfo(e).isEmpty() ? "" : ": " + Utils.parseErrorInfo(e));
        mHint.setText(errorInfo);
    }

    private void checkRequestFinish() {
        if (count == REQUEST_TOTAL_TIMES) {
            failCount = 0;
            count = 0;
            indoorDataMap = indoorHelper.getIndoorDataMap();
            outdoorDataMap = outdoorHelper.getOutdoorDataMap();
            rindingDataMap = rindingHelper.getRindingDataMap();
            walkDataMap = walkHelper.getWalkDataMap();
            parseData();
        }
    }

    private void parseData() {
        parseIndoorData();
        parseOutdoorData();
        parseRindingData();
        parseWalkData();
        if (mRecordList != null && !mRecordList.isEmpty()) {
            mRecordList.sort((t1, t2) -> Long.compare(t2.getStartTime(), t1.getStartTime()));
            if (mAdapter != null) {
                showList();
                mAdapter.setDataList(mRecordList);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            hideList();
            mHint.setText(getString(R.string.none_data));
        }
    }

    private void parseIndoorData() {
        if (indoorDataMap != null || !indoorDataMap.isEmpty()) {
            for (Map.Entry<Long, SportIndoorRunningData> entry : indoorDataMap.entrySet()) {
                long key = entry.getKey();
                SportIndoorRunningData entryValue = entry.getValue();
                if (entryValue == null) {
                    continue;
                }
                SportRecordData sportRecordData = new SportRecordData();
                sportRecordData.setStartTime(key);
                sportRecordData.setStampStartTime(TimeUtils.timestamp2Date(key, RECORD_DATE_FORMAT));
                sportRecordData.setSportType(entryValue.getSportType());
                sportRecordData.setSportTypeName(entryValue.getSportTypeName());
                sportRecordData.setDistance(((float) (entryValue.getDistance())) / 1000);
                sportRecordData.setDuration(entryValue.getSportTime());
                sportRecordData.setCalorie(entryValue.getCalorie());
                mRecordList.add(sportRecordData);
            }
        }
    }

    private void parseOutdoorData() {
        if (outdoorDataMap != null || !outdoorDataMap.isEmpty()) {
            for (Map.Entry<Long, SportOutdoorRunningData> entry : outdoorDataMap.entrySet()) {
                long key = entry.getKey();
                SportOutdoorRunningData entryValue = entry.getValue();
                if (entryValue == null) {
                    continue;
                }

                SportRecordData sportRecordData = new SportRecordData();
                sportRecordData.setStartTime(key);
                sportRecordData.setStampStartTime(TimeUtils.timestamp2Date(key, RECORD_DATE_FORMAT));
                sportRecordData.setSportType(entryValue.getSportType());
                sportRecordData.setSportTypeName(entryValue.getSportTypeName());
                sportRecordData.setDistance(((float) (entryValue.getDistance())) / 1000);
                sportRecordData.setDuration(entryValue.getSportTime());
                sportRecordData.setCalorie(entryValue.getCalorie());
                mRecordList.add(sportRecordData);
            }
        }
    }

    private void parseRindingData() {
        if (rindingDataMap != null || !rindingDataMap.isEmpty()) {
            for (Map.Entry<Long, SportRidingData> entry : rindingDataMap.entrySet()) {
                long key = entry.getKey();
                SportRidingData entryValue = entry.getValue();
                if (entryValue == null) {
                    continue;
                }
                SportRecordData sportRecordData = new SportRecordData();
                sportRecordData.setStartTime(key);
                sportRecordData.setStampStartTime(TimeUtils.timestamp2Date(key, RECORD_DATE_FORMAT));
                sportRecordData.setSportType(entryValue.getSportType());
                sportRecordData.setSportTypeName(entryValue.getSportTypeName());
                sportRecordData.setDistance(((float) (entryValue.getDistance())) / 1000);
                sportRecordData.setDuration(entryValue.getSportTime());
                sportRecordData.setCalorie(entryValue.getCalorie());
                mRecordList.add(sportRecordData);
            }
        }
    }

    private void parseWalkData() {
        if (walkDataMap != null || !walkDataMap.isEmpty()) {
            for (Map.Entry<Long, SportWalkData> entry : walkDataMap.entrySet()) {
                long key = entry.getKey();
                SportWalkData entryValue = entry.getValue();
                if (entryValue == null) {
                    continue;
                }
                SportRecordData sportRecordData = new SportRecordData();
                sportRecordData.setStartTime(key);
                sportRecordData.setStampStartTime(TimeUtils.timestamp2Date(key, RECORD_DATE_FORMAT));
                sportRecordData.setSportType(entryValue.getSportType());
                sportRecordData.setSportTypeName(entryValue.getSportTypeName());
                sportRecordData.setDistance(((float) (entryValue.getDistance())) / 1000);
                sportRecordData.setDuration(entryValue.getSportTime());
                sportRecordData.setCalorie(entryValue.getCalorie());
                mRecordList.add(sportRecordData);
            }
        }
    }

    @Override
    public void onClick(int position) {
        if (mRecordList == null) {
            return;
        }
        if (position < 0 || position > mRecordList.size()) {
            return;
        }

        SportRecordData recordData = mRecordList.get(position);
        int type = recordData.getSportType();

        switch (type) {
            case DataType.RECORD_RUNNING_INDOOR:
                for (Map.Entry<Long, SportIndoorRunningData> entry : indoorDataMap.entrySet()) {
                    long key = entry.getKey();
                    if (key == recordData.getStartTime()) {
                        SportIndoorRunningData entryValue = entry.getValue();
                        gotoItemActivity(entryValue, SportRecordDetailActivity.SPORT_DETAIL_INDOOR_INFO);
                        return;
                    }
                }
                break;
            case DataType.RECORD_RUNNING_OUTDOOR:
                for (Map.Entry<Long, SportOutdoorRunningData> entry : outdoorDataMap.entrySet()) {
                    long key = entry.getKey();
                    if (key == recordData.getStartTime()) {
                        SportOutdoorRunningData entryValue = entry.getValue();
                        gotoItemActivity(entryValue, SportRecordDetailActivity.SPORT_DETAIL_OUTDOOR_INFO);
                        return;
                    }
                }
                break;
            case DataType.RECORD_RIDING:
                for (Map.Entry<Long, SportRidingData> entry : rindingDataMap.entrySet()) {
                    long key = entry.getKey();
                    if (key == recordData.getStartTime()) {
                        SportRidingData entryValue = entry.getValue();
                        gotoItemActivity(entryValue, SportRecordDetailActivity.SPORT_DETAIL_RIDING_INFO);
                        return;
                    }
                }
                break;
            case DataType.RECORD_WALKING:
                for (Map.Entry<Long, SportWalkData> entry : walkDataMap.entrySet()) {
                    long key = entry.getKey();
                    if (key == recordData.getStartTime()) {
                        SportWalkData entryValue = entry.getValue();
                        gotoItemActivity(entryValue, SportRecordDetailActivity.SPORT_DETAIL_WALK_INFO);
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private <T extends Serializable> void gotoItemActivity(T entryValue, int type) {
        Intent intent = new Intent(HistorySportDataActivity.this, SportRecordDetailActivity.class);
        intent.putExtra(SportRecordDetailActivity.SPORT_DETAIL_TYPE, type);
        intent.putExtra(SportRecordDetailActivity.SPORT_DETAIL_CONTENT, entryValue);
        startActivity(intent);
    }
}
