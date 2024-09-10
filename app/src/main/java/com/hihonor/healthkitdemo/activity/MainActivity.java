/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hihonor.healthkitdemo.R;

/**
 * MainActivity
 *
 * @author x00017514
 * @since 2024-07-30
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startDailyActivity(View view) {
        startActivity(DailyActivity.class);
    }

    public void starHealthSignsActivity(View view) {
        startActivity(HealthSignsActivity.class);
    }

    public void startDailyDataActivity(View view) {
        startActivity(DailyActivitiesDataActivity.class);
    }

    public void startRealTimeHeartRateActivity(View view) {
        startActivity(RealTimeHeartRateActivity.class);
    }

    public void startSubscribeActivity(View view) {
        startActivity(SubscribeActivity.class);
    }

    public void startSleepActivity(View view) {
        startActivity(SleepActivity.class);
    }

    public void startSportRecordActivity(View view) {
        startActivity(HistorySportDataActivity.class);
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}