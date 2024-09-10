/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.adapter.HeartRateDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.LocationDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.SpeedDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.SportDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.StepRateDetailListAdapter;
import com.hihonor.healthkitdemo.adapter.SummaryListAdapter;
import com.hihonor.healthkitdemo.data.SportIndoorRunningData;
import com.hihonor.healthkitdemo.data.SportOutdoorRunningData;
import com.hihonor.healthkitdemo.data.SportRidingData;
import com.hihonor.healthkitdemo.data.SportWalkData;
import com.hihonor.healthkitdemo.data.SummaryItemData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动记录详情界面
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportRecordDetailActivity extends BaseActivity {
    public static final String SPORT_DETAIL_TYPE = "sportDetailType";

    public static final String SPORT_DETAIL_CONTENT = "sportDetailContent";

    public static final int SPORT_DETAIL_WALK_INFO = 1;

    public static final int SPORT_DETAIL_RIDING_INFO = 2;

    public static final int SPORT_DETAIL_INDOOR_INFO = 4;

    public static final int SPORT_DETAIL_OUTDOOR_INFO = 5;

    private RecyclerView mSummaryRecyclerView;

    private RecyclerView mHeartRateRecyclerView;

    private RecyclerView mSpeedRecyclerView;

    private RecyclerView mStepRateRecyclerView;

    private RecyclerView mLocationRecyclerView;

    private SummaryListAdapter mSummaryListAdapter = new SummaryListAdapter();

    private HeartRateDetailListAdapter mHeartRateDetailListAdapter = new HeartRateDetailListAdapter();

    private SpeedDetailListAdapter mSpeedDetailListAdapter = new SpeedDetailListAdapter();

    private StepRateDetailListAdapter mStepRateDetailListAdapter = new StepRateDetailListAdapter();

    private LocationDetailListAdapter mLoationDetailListAdapter = new LocationDetailListAdapter();

    private TextView mSportDistance;

    private TextView mSportType;

    private TextView mStartTime;

    private TextView mEndTime;

    private SportWalkData mWalkData;

    private SportRidingData mRidingData;

    private SportIndoorRunningData mIndoorData;

    private SportOutdoorRunningData mOutdoorData;

    private SportDetailListAdapter mDetailListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record_detail);
        initView();
        Intent intent = getIntent();
        int type = intent.getIntExtra(SPORT_DETAIL_TYPE, 0);
        switch (type) {
            case SPORT_DETAIL_WALK_INFO:
                mWalkData = (SportWalkData) intent.getSerializableExtra(SPORT_DETAIL_CONTENT);
                if (mWalkData != null) {
                    initData(mWalkData);
                }
                break;
            case SPORT_DETAIL_RIDING_INFO:
                mRidingData = (SportRidingData) intent.getSerializableExtra(SPORT_DETAIL_CONTENT);
                if (mRidingData != null) {
                    initData(mRidingData);
                }
                break;
            case SPORT_DETAIL_INDOOR_INFO:
                mIndoorData = (SportIndoorRunningData) intent.getSerializableExtra(SPORT_DETAIL_CONTENT);
                if (mIndoorData != null) {
                    initData(mIndoorData);
                }
                break;
            case SPORT_DETAIL_OUTDOOR_INFO:
                mOutdoorData = (SportOutdoorRunningData) intent.getSerializableExtra(SPORT_DETAIL_CONTENT);
                if (mOutdoorData != null) {
                    initData(mOutdoorData);
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        mSportDistance = findViewById(R.id.sport_distance);
        mSportType = findViewById(R.id.sport_type);
        mStartTime = findViewById(R.id.sport_start);
        mEndTime = findViewById(R.id.sport_end);

        mSummaryRecyclerView = findViewById(R.id.sport_summay_list);
        initRecyclerView(mSummaryRecyclerView, mSummaryListAdapter);

        mHeartRateRecyclerView = findViewById(R.id.sport_detail_heartrate_list);
        initRecyclerView(mHeartRateRecyclerView, mHeartRateDetailListAdapter);

        mSpeedRecyclerView = findViewById(R.id.sport_detail_speed_list);
        initRecyclerView(mSpeedRecyclerView, mSpeedDetailListAdapter);

        mStepRateRecyclerView = findViewById(R.id.sport_detail_steprate_list);
        initRecyclerView(mStepRateRecyclerView, mStepRateDetailListAdapter);

        mLocationRecyclerView = findViewById(R.id.sport_detail_location_list);
        initRecyclerView(mLocationRecyclerView, mLoationDetailListAdapter);
    }

    private void initData(SportWalkData data) {
        mSportType.setText(data.getSportTypeName());
        mSportDistance.setText(String.valueOf(((float) (data.getDistance())) / 1000));
        mStartTime.setText(TimeUtils.timestamp2DateSecond(data.getStartTime()));
        mEndTime.setText(TimeUtils.timestamp2DateSecond(data.getEndTime()));

        List<SummaryItemData> summaryItemList = new ArrayList<>();
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_time),
            TimeUtils.formatDurationFromSecond(this, data.getSportTime()), ""));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_calorie), data.getCalorie(),
            getString(R.string.unit_calorie)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_speed), data.getSpeed(),
            getString(R.string.unit_speed_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_step_cadence), data.getStepCadence(),
            getString(R.string.unit_step_cadence)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_steps), data.getSteps(),
            getString(R.string.unit_step)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_avg_heart_rate),
            data.getAvgHeartRate(), getString(R.string.unit_heart_rate)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_total_rise_height),
            data.getTotalRiseHeight(), getString(R.string.unit_distance_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_total_drop_height),
            data.getTotalDropHeight(), getString(R.string.unit_distance_m)));

        mSummaryListAdapter.setDataList(summaryItemList).notifyDataSetChanged();
        mHeartRateDetailListAdapter.setDataList(data.getHeartRateDetails()).notifyDataSetChanged();
        mSpeedDetailListAdapter.setDataList(data.getSpeedDetails()).notifyDataSetChanged();
        mStepRateDetailListAdapter.setDataList(data.getStepCadenceDetails()).notifyDataSetChanged();
        mLoationDetailListAdapter.setDataList(data.getLocationDetails()).notifyDataSetChanged();
    }

    private void initData(SportRidingData data) {
        mSportType.setText(data.getSportTypeName());
        mSportDistance.setText(String.valueOf(((float) (data.getDistance())) / 1000));
        mStartTime.setText(TimeUtils.timestamp2DateSecond(data.getStartTime()));
        mEndTime.setText(TimeUtils.timestamp2DateSecond(data.getEndTime()));

        List<SummaryItemData> summaryItemList = new ArrayList<>();

        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_time),
            TimeUtils.formatDurationFromSecond(this, data.getSportTime()), ""));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_calorie), data.getCalorie(),
            getString(R.string.unit_calorie)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_speed), data.getSpeed(),
            getString(R.string.unit_speed_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_avg_heart_rate),
            data.getAvgHeartRate(), getString(R.string.unit_heart_rate)));

        mSummaryListAdapter.setDataList(summaryItemList).notifyDataSetChanged();
        mHeartRateDetailListAdapter.setDataList(data.getHeartRateDetails()).notifyDataSetChanged();
        mSpeedDetailListAdapter.setDataList(data.getSpeedDetails()).notifyDataSetChanged();
        mLoationDetailListAdapter.setDataList(data.getLocationDetails()).notifyDataSetChanged();
    }

    private void initData(SportIndoorRunningData data) {
        mSportType.setText(data.getSportTypeName());
        mSportDistance.setText(String.valueOf(((float) (data.getDistance())) / 1000));
        mStartTime.setText(TimeUtils.timestamp2DateSecond(data.getStartTime()));
        mEndTime.setText(TimeUtils.timestamp2DateSecond(data.getEndTime()));

        List<SummaryItemData> summaryItemList = new ArrayList<>();
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_time),
            TimeUtils.formatDurationFromSecond(this, data.getSportTime()), ""));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_calorie), data.getCalorie(),
            getString(R.string.unit_calorie)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_speed), data.getSpeed(),
            getString(R.string.unit_speed_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_step_cadence), data.getStepCadence(),
            getString(R.string.unit_step_cadence)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_steps), data.getSteps(),
            getString(R.string.unit_step)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_avg_heart_rate),
            data.getAvgHeartRate(), getString(R.string.unit_heart_rate)));

        mSummaryListAdapter.setDataList(summaryItemList).notifyDataSetChanged();
        mHeartRateDetailListAdapter.setDataList(data.getHeartRateDetails()).notifyDataSetChanged();
        mSpeedDetailListAdapter.setDataList(data.getSpeedDetails()).notifyDataSetChanged();
        mStepRateDetailListAdapter.setDataList(data.getStepCadenceDetails()).notifyDataSetChanged();
    }

    private void initData(SportOutdoorRunningData data) {
        mSportType.setText(data.getSportTypeName());
        mSportDistance.setText(String.valueOf(((float) (data.getDistance())) / 1000));
        mStartTime.setText(TimeUtils.timestamp2DateSecond(data.getStartTime()));
        mEndTime.setText(TimeUtils.timestamp2DateSecond(data.getEndTime()));

        List<SummaryItemData> summaryItemList = new ArrayList<>();

        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_time),
            TimeUtils.formatDurationFromSecond(this, data.getSportTime()), ""));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_calorie), data.getCalorie(),
            getString(R.string.unit_calorie)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_speed), data.getSpeed(),
            getString(R.string.unit_speed_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_step_cadence), data.getStepCadence(),
            getString(R.string.unit_step_cadence)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_steps), data.getSteps(),
            getString(R.string.unit_step)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_sport_avg_heart_rate),
            data.getAvgHeartRate(), getString(R.string.unit_heart_rate)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_total_rise_height),
            data.getTotalRiseHeight(), getString(R.string.unit_distance_m)));
        summaryItemList.add(new SummaryItemData(getString(R.string.sport_item_total_drop_height),
            data.getTotalDropHeight(), getString(R.string.unit_distance_m)));

        mSummaryListAdapter.setDataList(summaryItemList).notifyDataSetChanged();
        mHeartRateDetailListAdapter.setDataList(data.getHeartRateDetails()).notifyDataSetChanged();
        mSpeedDetailListAdapter.setDataList(data.getSpeedDetails()).notifyDataSetChanged();
        mStepRateDetailListAdapter.setDataList(data.getStepCadenceDetails()).notifyDataSetChanged();
        mLoationDetailListAdapter.setDataList(data.getLocationDetails()).notifyDataSetChanged();
    }
}