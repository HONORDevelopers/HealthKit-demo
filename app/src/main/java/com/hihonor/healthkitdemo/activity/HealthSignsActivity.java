/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.hihonor.healthkitdemo.R;

/**
 * 页面控制
 *
 * @author x00017514
 * @since 2024-08-01
 */
public class HealthSignsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_signs);
    }

    public void starHeartHealthActivity(View view) {
        startActivity(HeartHealthActivity.class);
    }

    public void starStressStatistic(View view) {
        startActivity(StressStatisticActivity.class);
    }

    public void starBloodActivity(View view) {
        startActivity(BloodActivity.class);
    }

    public void starBloodPressureActivity(View view) {
        startActivity(BloodPressureActivity.class);
    }

    public void startBloodOxygenActivity(View view) {
        startActivity(BloodOxygenActivity.class);
    }

    public void startBodyTemperatureActivity(View view) {
        startActivity(BodyTemperatureActivity.class);
    }

    public void startMenstrualCycleActivity(View view) {
        startActivity(MenstrualCycleActivity.class);
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(HealthSignsActivity.this, cls);
        startActivity(intent);
    }
}
