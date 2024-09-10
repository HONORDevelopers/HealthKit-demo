/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.LogUtil;
import com.hihonor.healthkitdemo.utils.ThreadPool;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.AtomicPointValue;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.realtimedata.RealTimeDataListener;

import java.util.Map;

/**
 * 实时心率订阅界面 —— 实时心率、实时步数、实时心率和步数；
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class RealTimeHeartRateActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RealTimeHeartRateActivity_";

    private static final int RESULT_REGISTER_SUCCESS = 1;

    private static final int RESULT_REGISTER_FAIL = 2;

    private static final int RESULT_UNREGISTER_SUCCESS = 3;

    private static final int RESULT_UNREGISTER_FAIL = 4;

    private static final int UPDATE_HEART = 5;

    private static final String HEART_RATE = "heartRate";

    private static final String STEPS = "steps";

    /**
     * 实时心率控制按钮
     */
    private Button mBtnController;

    /**
     * 动态步数开启按钮
     */
    private Button mBtnStep;

    /**
     * 动态心率和步数开启按钮
     */
    private Button mBtnHeartRateStep;

    private View mLlHeartRateData;

    private View mStepDateView;

    private View mHeartRateStepDateView;

    private LinearLayout mLayoutTwoHeartRate;

    private LinearLayout mLayoutTwoStep;

    private LinearLayout mLayoutThreeHeartRate;

    private LinearLayout mLayoutThreeStep;

    private TextView mRealTimeTvTitle;

    private TextView mHeartRateAndStepTitle;

    private TextView mStepNumber;

    private TextView mThreeHeartRate;

    private TextView mThreeStep;

    private TextView mTvDynamicHeartRate;

    private String mHeartRate;

    private String mSteps;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case RESULT_REGISTER_SUCCESS:
                    LogUtil.i(TAG, "registerHeart onSuccess");
                    int dateType = (int) msg.obj;
                    startMonitor(dateType);
                    break;
                case RESULT_REGISTER_FAIL:
                    LogUtil.i(TAG, "registerHeart onFail");
                    break;
                case RESULT_UNREGISTER_SUCCESS:
                    LogUtil.i(TAG, "unRegisterHeart onSuccess");
                    int unDateType = (int) msg.obj;
                    stopMonitor(unDateType);
                    break;
                case RESULT_UNREGISTER_FAIL:
                    LogUtil.i(TAG, "unRegisterHeart onFail");
                    break;
                case UPDATE_HEART:
                    if (mTvDynamicHeartRate != null && !TextUtils.isEmpty(mHeartRate)) {
                        mTvDynamicHeartRate.setText(mHeartRate);
                    }
                    if (mStepNumber != null && !TextUtils.isEmpty(mSteps)) {
                        mStepNumber.setText(mSteps);
                    }
                    if (mThreeHeartRate != null  && !TextUtils.isEmpty(mHeartRate)) {
                        mThreeHeartRate.setText(mHeartRate);
                    }
                    if (mThreeStep != null && !TextUtils.isEmpty(mSteps)) {
                        mThreeStep.setText(mSteps);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        mBtnController = findViewById(R.id.btn_controller);
        mBtnController.setOnClickListener(this);
        mLlHeartRateData = findViewById(R.id.ll_heart_rate_data);
        mTvDynamicHeartRate = findViewById(R.id.tv_dynamic_heart_rate);
        initRealTimeStepView();
        initRealTimeHeartRateAndStepView();
    }

    private void initRealTimeStepView() {
        mBtnStep = findViewById(R.id.btn_step);
        mBtnStep.setOnClickListener(this);
        mStepDateView = findViewById(R.id.include_step);
        mLayoutTwoHeartRate = mStepDateView.findViewById(R.id.layout_heart_rate);
        mLayoutTwoStep = mStepDateView.findViewById(R.id.layout_step);
        mLayoutTwoHeartRate.setVisibility(View.GONE);
        mLayoutTwoStep.setVisibility(View.VISIBLE);
        mRealTimeTvTitle = mStepDateView.findViewById(R.id.heart_rate_and_step_title);
        mRealTimeTvTitle.setText(R.string.real_time_step_title);
        mStepNumber = mStepDateView.findViewById(R.id.tv_step);
    }

    private void initRealTimeHeartRateAndStepView() {
        mBtnHeartRateStep = findViewById(R.id.btn_heart_rate_step);
        mBtnHeartRateStep.setOnClickListener(this);
        mHeartRateStepDateView = findViewById(R.id.include_rate_step);
        mLayoutThreeHeartRate = mHeartRateStepDateView.findViewById(R.id.layout_heart_rate);
        mLayoutThreeStep = mHeartRateStepDateView.findViewById(R.id.layout_step);
        mLayoutThreeHeartRate.setVisibility(View.VISIBLE);
        mLayoutThreeStep.setVisibility(View.VISIBLE);
        mHeartRateAndStepTitle = mHeartRateStepDateView.findViewById(R.id.heart_rate_and_step_title);
        mHeartRateAndStepTitle.setText(R.string.real_time_heart_rate_and_step_title);
        mThreeHeartRate = mHeartRateStepDateView.findViewById(R.id.tv_dynamic_heart_rate);
        mThreeStep = mHeartRateStepDateView.findViewById(R.id.tv_step);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_controller) {
            if (mBtnController.getText().equals(getString(R.string.start_heart_monitor))) {
                mBtnController.setText(getString(R.string.stop_heart_monitor));
                mLlHeartRateData.setVisibility(View.VISIBLE);
                registerHeart(DataType.SAMPLE_HEART_RATE_REALTIME);
            } else {
                mBtnController.setText(getString(R.string.start_heart_monitor));
                mLlHeartRateData.setVisibility(View.INVISIBLE);
                unRegisterHeart(DataType.SAMPLE_HEART_RATE_REALTIME);
            }
        } else if (view.getId() == R.id.btn_step) {
            if (mBtnStep.getText().equals(getString(R.string.start_step_monitor))) {
                mBtnStep.setText(getString(R.string.stop_step_monitor));
                mStepDateView.setVisibility(View.VISIBLE);
                registerHeart(DataType.SAMPLE_STEPS_REALTIME);
            } else {
                mBtnStep.setText(getString(R.string.start_step_monitor));
                mStepDateView.setVisibility(View.INVISIBLE);
                unRegisterHeart(DataType.SAMPLE_STEPS_REALTIME);
            }
        } else if (view.getId() == R.id.btn_heart_rate_step) {
            if (mBtnHeartRateStep.getText().equals(getString(R.string.start_heart_rate_step_monitor))) {
                mBtnHeartRateStep.setText(getString(R.string.stop_heart_rate_step_monitor));
                mHeartRateStepDateView.setVisibility(View.VISIBLE);
                registerHeart(DataType.SAMPLE_HEART_RATE_AND_STEP);
            } else {
                mBtnHeartRateStep.setText(getString(R.string.start_heart_rate_step_monitor));
                mHeartRateStepDateView.setVisibility(View.INVISIBLE);
                unRegisterHeart(DataType.SAMPLE_HEART_RATE_AND_STEP);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mLlHeartRateData.getVisibility() == View.VISIBLE) {
            unRegisterHeart(DataType.SAMPLE_HEART_RATE_REALTIME);
        }
        if (mStepDateView.getVisibility() == View.VISIBLE) {
            unRegisterHeart(DataType.SAMPLE_STEPS_REALTIME);
        }
        if (mHeartRateStepDateView.getVisibility() == View.VISIBLE) {
            unRegisterHeart(DataType.SAMPLE_HEART_RATE_AND_STEP);
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void startMonitor(int dateType) {
        switch (dateType) {
            case DataType.SAMPLE_HEART_RATE_REALTIME:
                if (mBtnController.getText().equals(getString(R.string.start_heart_monitor))) {
                    mBtnController.setText(getString(R.string.stop_heart_monitor));
                    mLlHeartRateData.setVisibility(View.VISIBLE);
                }
                break;
            case DataType.SAMPLE_HEART_RATE_AND_STEP:
                if (mBtnHeartRateStep.getText().equals(getString(R.string.start_heart_rate_step_monitor))) {
                    mBtnHeartRateStep.setText(getString(R.string.stop_heart_rate_step_monitor));
                    mHeartRateStepDateView.setVisibility(View.VISIBLE);
                }
                break;
            case DataType.SAMPLE_STEPS_REALTIME:
                if (mBtnStep.getText().equals(getString(R.string.start_step_monitor))) {
                    mBtnStep.setText(getString(R.string.stop_step_monitor));
                    mStepDateView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void stopMonitor(int dateType) {
        switch (dateType) {
            case DataType.SAMPLE_HEART_RATE_REALTIME:
                if (mBtnController.getText().equals(getString(R.string.stop_heart_monitor))) {
                    mBtnController.setText(getString(R.string.start_heart_monitor));
                    mLlHeartRateData.setVisibility(View.INVISIBLE);
                }
                break;
            case DataType.SAMPLE_HEART_RATE_AND_STEP:
                if (mBtnHeartRateStep.getText().equals(getString(R.string.stop_heart_rate_step_monitor))) {
                    mBtnHeartRateStep.setText(getString(R.string.start_heart_rate_step_monitor));
                    mHeartRateStepDateView.setVisibility(View.INVISIBLE);
                }
                break;
            case DataType.SAMPLE_STEPS_REALTIME:
                if (mBtnStep.getText().equals(getString(R.string.stop_step_monitor))) {
                    mBtnStep.setText(getString(R.string.start_step_monitor));
                    mStepDateView.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private RealTimeDataListener mRealTimeListener = new RealTimeDataListener() {
        @Override
        public void onResult(SampleData sampleData) {
            if (isFinishing()) {
                return;
            }
            if (mTvDynamicHeartRate == null || mHandler == null) {
                return;
            }

            Map<String, AtomicPointValue> map = sampleData.getDataMap();
            if (map != null && map.containsKey(HEART_RATE)) {
                Integer heartRate = map.get(HEART_RATE).getInteger();
                if (heartRate != null) {
                    mHeartRate = heartRate + "";
                    mHandler.sendEmptyMessage(UPDATE_HEART);
                }
            }
            if (map != null && map.containsKey(STEPS)) {
                Integer steps = map.get(STEPS).getInteger();
                if (steps != null) {
                    mSteps = steps + "";
                    mHandler.sendEmptyMessage(UPDATE_HEART);
                }
            }
        }

        @Override
        public void onFail(int i, String s) {
            LogUtil.i(TAG, "onFail, errorCode=" + i + ", errorMsg=" + s);
        }
    };

    private void registerHeart(int dateType) {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int registerType = dateType;
                HealthKit.getRealTimeDataClient(mContext)
                        .registerRealTimeDataListener(Utils.getHonorSignInAccount(), registerType, mRealTimeListener)
                        .addOnSuccessListener(unused -> {
                            Message message = new Message();
                            message.what = RESULT_REGISTER_SUCCESS;
                            message.obj = registerType;
                            mHandler.sendMessage(message);
                        })
                        .addOnFailureListener(e -> {
                            Message message = new Message();
                            message.what = RESULT_REGISTER_FAIL;
                            message.obj = registerType;
                            mHandler.sendMessage(message);
                });
            }
        });
    }

    private void unRegisterHeart(int dateType) {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int registerType = dateType;
                HealthKit.getRealTimeDataClient(mContext)
                        .unregisterRealTimeDataListener(Utils.getHonorSignInAccount(), registerType)
                        .addOnSuccessListener(unused -> {
                            Message message = new Message();
                            message.what = RESULT_UNREGISTER_SUCCESS;
                            message.obj = registerType;
                            mHandler.sendMessage(message);
                        })
                        .addOnFailureListener(e -> {
                            Message message = new Message();
                            message.what = RESULT_UNREGISTER_FAIL;
                            message.obj = registerType;
                            mHandler.sendMessage(message);
                        });
            }
        });
    }
}
