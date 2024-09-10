/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.fragment.daily.AltitudeDailyFragment;
import com.hihonor.healthkitdemo.fragment.daily.BaseFragment;
import com.hihonor.healthkitdemo.fragment.daily.CaloriesDailyFragment;
import com.hihonor.healthkitdemo.fragment.daily.DistanceDailyFragment;
import com.hihonor.healthkitdemo.fragment.daily.StepDailyFragment;
import com.hihonor.healthkitdemo.fragment.daily.StrengthDailyFragment;
import com.hihonor.mcs.fitness.health.constants.DataType;

/**
 * 日常活动统计
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class DailyDetailsActivity extends FragmentActivity {
    private static final String TAG = "DailyDetailsActivity_";

    private static final String DATA_TYPE = "data_type";

    private FrameLayout mContent;

    private TextView mTvError;

    public static void startToDailyDetailsActivity(Context context, int dataType) {
        Intent intent = new Intent(context, DailyDetailsActivity.class);
        intent.putExtra(DATA_TYPE, dataType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_details);
        initView();
    }

    private void initView() {
        mContent = findViewById(R.id.fragment_content);
        mTvError = findViewById(R.id.tv_error_hint);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        int dataType = intent.getIntExtra(DATA_TYPE, DataType.SAMPLE_STEPS_STATISTIC);
        addFragments(dataType);
    }

    private void addFragments(int dataType) {
        BaseFragment baseFragment = null;
        switch (dataType) {
            case DataType.SAMPLE_STEPS_STATISTIC:
                baseFragment = new StepDailyFragment();
                break;
            case DataType.SAMPLE_STRENGTH_STATISTIC:
                baseFragment = new StrengthDailyFragment();
                break;
            case DataType.SAMPLE_DISTANCE_STATISTIC:
                baseFragment = new DistanceDailyFragment();
                break;
            case DataType.SAMPLE_CALORIES_STATISTIC:
                baseFragment = new CaloriesDailyFragment();
                break;
            case DataType.SAMPLE_ALTITUDE_STATISTIC:
                baseFragment = new AltitudeDailyFragment();
                break;
            default:
                Log.e(TAG, "addFragments: dataType is error");
                break;
        }
        if (baseFragment == null) {
            mTvError.setVisibility(View.VISIBLE);
            mTvError.setText("数据类型异常！！！");
            mContent.setVisibility(View.GONE);
            Log.e(TAG, "addFragments: baseFragment is null");
            return;
        }
        mTvError.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_content,baseFragment);
        fragmentTransaction.commit();
    }
}
