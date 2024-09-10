/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.ThreadPool;
import com.hihonor.healthkitdemo.utils.Utils;
import com.hihonor.healthkitdemo.view.CardRoundView;
import com.hihonor.mcs.fitness.health.HealthKit;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.goals.GoalEvent;
import com.hihonor.mcs.fitness.health.goals.GoalListener;
import com.hihonor.mcs.fitness.health.goals.GoalRequest;

import java.text.DecimalFormat;

/**
 * 日常数据订阅（目标订阅和周期订阅）
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SubscribeActivity extends BaseActivity {
    private static final String TAG = "SubscribeActivity";

    private static final int VALUES_LENGTH = 4;

    private static final int[] SUBSCRIBE_TYPE = {GoalRequest.SUBSCRIBE_TYPE_TARGET, GoalRequest.SUBSCRIBE_TYPE_CYCLE};

    private static final int[] DATE_TYPE = {DataType.SAMPLE_STEPS, DataType.SAMPLE_DISTANCE,
            DataType.SAMPLE_CALORIES};

    private static final int[] CYCLE_STEP = {GoalRequest.STEP_SUBSCRIBE_CYCLE_500, GoalRequest.STEP_SUBSCRIBE_CYCLE_1000,
            GoalRequest.STEP_SUBSCRIBE_CYCLE_2000, GoalRequest.STEP_SUBSCRIBE_CYCLE_5000};

    private static final int[] CYCLE_DISTANCE = {GoalRequest.DISTANCE_SUBSCRIBE_CYCLE_500, GoalRequest.DISTANCE_SUBSCRIBE_CYCLE_1000,
            GoalRequest.DISTANCE_SUBSCRIBE_CYCLE_2000, GoalRequest.DISTANCE_SUBSCRIBE_CYCLE_5000};

    private static final int[] CYCLE_CALORIES = {GoalRequest.CALORIES_SUBSCRIBE_CYCLE_20, GoalRequest.CALORIES_SUBSCRIBE_CYCLE_50,
            GoalRequest.CALORIES_SUBSCRIBE_CYCLE_100, GoalRequest.CALORIES_SUBSCRIBE_CYCLE_200};

    private static final int RESULT_REGISTER_SUCCESS = 1;

    private static final int RESULT_REGISTER_FAIL = 2;

    private static final int RESULT_UNREGISTER_SUCCESS = 3;


    private static final int RESULT_UNREGISTER_FAIL = 4;

    private static final int UPDATE_RESULT = 5;

    private static final int UPDATE_CALORIES_RESULT = 6;


    private View mTargeOrCycleView;

    private RadioGroup mRadioGroupOneLine;

    private View mDateTypeView;

    private RadioGroup mRadioGroupTwoLine;

    private LinearLayout mEditLayout;

    private EditText mEditText;

    private LinearLayout mLayoutValue;

    private View mDateValueView;

    private RadioGroup mRadioGroupThreeLine;

    private Button mBtn;

    private CardRoundView mSubscribeResult;

    private TextView mSubscribeTitle;

    private TextView mTvAim;

    private TextView mTvResult;

    private TextView mTvUnit;

    private int mSubscribeType = SUBSCRIBE_TYPE[0];

    private int mDataType = DATE_TYPE[0];

    private int mDataValue = CYCLE_STEP[0];

    private int[] mValues = CYCLE_STEP;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESULT_REGISTER_SUCCESS:
                    startMonitor();
                    Toast.makeText(SubscribeActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_REGISTER_FAIL:
                    stopMonitor();
                    Toast.makeText(SubscribeActivity.this, "订阅失败", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_UNREGISTER_SUCCESS:
                    stopMonitor();
                    break;
                case UPDATE_RESULT:
                    Object object =  msg.obj;
                    if (mTvResult != null && !TextUtils.isEmpty(object.toString())) {
                        DecimalFormat df = new DecimalFormat("#");
                        String s = df.format(object);
                        mTvResult.setText(s);
                    }
                    break;
                case UPDATE_CALORIES_RESULT:
                    Object objectCalories =  msg.obj;
                    if (mTvResult != null && !TextUtils.isEmpty(objectCalories.toString())) {
                        Double mCalories = (Double) objectCalories / 1000;
                        DecimalFormat df = new DecimalFormat("#");
                        String s = df.format(mCalories);
                        mTvResult.setText(s);
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
        setContentView(R.layout.activity_subscribe);
        initView();
    }

    private void initView() {
        initOneLineView();
        initTwoLineView();
        initThreeView();
        mBtn = findViewById(R.id.btn_subscribe);
        mSubscribeResult = findViewById(R.id.ll_subscribe_result);
        mSubscribeTitle = findViewById(R.id.tv_subscribe_title);
        mTvAim = findViewById(R.id.tv_subscribe_type_and_aim);
        mTvResult = findViewById(R.id.tv_subscribe_result);
        mTvUnit = findViewById(R.id.tv_result_unit);
        mBtn.setOnClickListener(view -> {
            if (mBtn.getText().equals(getString(R.string.subscribe_start_date))) {
                if (mSubscribeType == GoalRequest.SUBSCRIBE_TYPE_TARGET) {
                    String value = mEditText.getText().toString();
                    if (TextUtils.isEmpty(value) || !Utils.isValidNumber(value)) {
                        Toast.makeText(SubscribeActivity.this, "请输入正确的值", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDataValue = Integer.parseInt(value.trim());
                    if (mDataType == DataType.SAMPLE_CALORIES) {
                        mDataValue = mDataValue * 1000;
                    }
                }
                registerGoal();
            } else {
                unsubscribeGoal();
            }
        });
        setStringToThreeLine();
        setVisibleToLayoutThreeLine();
    }

    private void initOneLineView() {
        mTargeOrCycleView = findViewById(R.id.include_target_or_cycle);
        mRadioGroupOneLine = mTargeOrCycleView.findViewById(R.id.radio_group);
        for (int i = 0; i < mRadioGroupOneLine.getChildCount(); i++) {
            if (mRadioGroupOneLine.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) mRadioGroupOneLine.getChildAt(i);
                if (i == 0) {
                    radioButton.setText(R.string.subscribe_type_target);
                } else if (i == 1) {
                    radioButton.setText(R.string.subscribe_type_cycle);
                } else {
                    radioButton.setVisibility(View.GONE);
                }
            }
        }
        Log.i(TAG, "initOneLineView: mSubscribeType dddd = " + mSubscribeType);
        mRadioGroupOneLine.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobutton_one) {
                mSubscribeType = SUBSCRIBE_TYPE[0];
            } else {
                mSubscribeType = SUBSCRIBE_TYPE[1];
            }
            setVisibleToLayoutThreeLine();
            Log.i(TAG, "onCheckedChanged: mSubscribeType = " + mSubscribeType);
        });
    }

    private void initTwoLineView() {
        mDateTypeView = findViewById(R.id.include_date_type);
        mRadioGroupTwoLine = mDateTypeView.findViewById(R.id.radio_group);
        for (int i = 0; i < mRadioGroupTwoLine.getChildCount(); i++) {
            if (mRadioGroupTwoLine.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) mRadioGroupTwoLine.getChildAt(i);
                if (i == 0) {
                    radioButton.setText(R.string.seven_day_item_step);
                } else if (i == 1) {
                    radioButton.setText(R.string.seven_day_item_distance);
                } else if (i == 2) {
                    radioButton.setText(R.string.seven_day_item_calorie);
                } else {
                    radioButton.setVisibility(View.GONE);
                }
            }
        }
        Log.i(TAG, "initOneLineView: mDateType dddd = " + mDataType);
        mRadioGroupTwoLine.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobutton_one) {
                mDataType = DATE_TYPE[0];
            } else if (checkedId == R.id.radiobutton_two) {
                mDataType = DATE_TYPE[1];
            } else {
                mDataType = DATE_TYPE[2];
            }
            setStringToThreeLine();
            Log.i(TAG, "onCheckedChanged: mDateType = " + mDataType);
        });
    }

    private void initThreeView() {
        mEditLayout = findViewById(R.id.layout_edit);
        mLayoutValue = findViewById(R.id.layout_value);
        mDateValueView = findViewById(R.id.include_date_value);
        mEditText = findViewById(R.id.edit_target_value);
        mRadioGroupThreeLine = mDateValueView.findViewById(R.id.radio_group);
        String[] stepStrings = {"500", "1000", "2000", "5000"};
        setStringToRadioButton(stepStrings);
        setCheckedValue();
    }

    private void setVisibleToLayoutThreeLine() {
        if (mEditLayout == null || mLayoutValue == null) {
            Log.i(TAG, "setVisibleToLayoutThreeLine: view is null");
            return;
        }
        switch (mSubscribeType) {
            case GoalRequest.SUBSCRIBE_TYPE_TARGET:
                mEditLayout.setVisibility(View.VISIBLE);
                mLayoutValue.setVisibility(View.GONE);
                mSubscribeTitle.setText("目标订阅");

                break;
            case GoalRequest.SUBSCRIBE_TYPE_CYCLE:
                mEditLayout.setVisibility(View.GONE);
                mLayoutValue.setVisibility(View.VISIBLE);
                mSubscribeTitle.setText("周期订阅");
                break;
            default:
                break;
        }
    }

    private void setStringToThreeLine() {
        if (mRadioGroupThreeLine == null) {
            Log.i(TAG, "mRadioGroupThreeLine: view is null");
            return;
        }
        switch (mDataType) {
            case DataType.SAMPLE_STEPS:
                mValues = CYCLE_STEP;
                mDataValue = mValues[0];
                String[] stepStrings = {"500", "1000", "2000", "5000"};
                setStringToRadioButton(stepStrings);
                break;
            case DataType.SAMPLE_DISTANCE:
                mValues = CYCLE_DISTANCE;
                mDataValue = mValues[0];
                String[] distanceStrings = {"500", "1000", "2000", "5000"};
                setStringToRadioButton(distanceStrings);
                break;
            case DataType.SAMPLE_CALORIES:
                mValues = CYCLE_CALORIES;
                mDataValue = mValues[0];
                String[] distanceCalories = {"20", "50", "100", "200"};
                setStringToRadioButton(distanceCalories);
                break;
            default:
                break;
        }

        if (mRadioGroupThreeLine.getChildAt(0) != null && mRadioGroupThreeLine.getChildAt(0) instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) mRadioGroupThreeLine.getChildAt(0);
            radioButton.setChecked(true);
        }
    }

    private void setStringToRadioButton(String[] string) {
        for (int i = 0; i < mRadioGroupThreeLine.getChildCount(); i++) {
            if (mRadioGroupThreeLine.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) mRadioGroupThreeLine.getChildAt(i);
                if (i == 0) {
                    radioButton.setText(string[0]);
                } else if (i == 1) {
                    radioButton.setText(string[1]);
                } else if (i == 2) {
                    radioButton.setText(string[2]);
                } else {
                    radioButton.setText(string[3]);
                }
            }
        }
    }

    private void setCheckedValue() {
        if (mValues == null || mValues.length < VALUES_LENGTH) {
            Log.e(TAG, "setCheckedValue: mValues is null or length < 4 ");
            return;
        }
        mRadioGroupThreeLine.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobutton_one) {
                mDataValue = mValues[0];
            } else if (checkedId == R.id.radiobutton_two) {
                mDataValue = mValues[1];
            } else if (checkedId == R.id.radiobutton_three) {
                mDataValue = mValues[2];
            } else {
                mDataValue = mValues[3];
            }
            Log.i(TAG, "onCheckedChanged: mDateValue = " + mDataValue);
        });
    }

    private void startMonitor() {
        if (mBtn.getText().equals(getString(R.string.subscribe_start_date))) {
            mBtn.setText(getString(R.string.subscribe_stop_date));
            mSubscribeResult.setVisibility(View.VISIBLE);
            mTvResult.setText("0");
            switch (mDataType) {
                case DataType.SAMPLE_STEPS:
                    mTvAim.setText("步数：" + mDataValue);
                    mTvUnit.setText(R.string.unit_step);
                    break;
                case DataType.SAMPLE_DISTANCE:
                    mTvAim.setText("距离：" + mDataValue);
                    mTvUnit.setText(R.string.unit_distance_m);
                    break;
                case DataType.SAMPLE_CALORIES:
                    int calories = mDataValue / 1000;
                    mTvAim.setText("热量：" + calories);
                    mTvUnit.setText(R.string.unit_calorie);
                    break;
                default:
                    break;
            }
        }
    }

    private void stopMonitor() {
        if (mBtn.getText().equals(getString(R.string.subscribe_stop_date))) {
            mBtn.setText(getString(R.string.subscribe_start_date));
            mSubscribeResult.setVisibility(View.GONE);
        }
    }

    private GoalListener mGoalListener = new GoalListener() {
        @Override
        public void onReceived(GoalEvent goalEvent) {
            if (goalEvent.getDataType() != DataType.SAMPLE_CALORIES) {
                Message message = new Message();
                message.obj = goalEvent.getRealValue();
                message.what = UPDATE_RESULT;
                mHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.obj = goalEvent.getRealValue();
                message.what = UPDATE_CALORIES_RESULT;
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void onFail(int i, String s) {
            Log.i(TAG, "onReceived: onFail errorCode : " + i + "-- errorMsg : " + s);
        }
    };

    private void registerGoal() {
        ThreadPool.execute(() -> {
            GoalRequest goalRequest = new GoalRequest(mDataType, mSubscribeType, mDataValue);
            HealthKit.getGoalsClient(SubscribeActivity.this).subscribeGoal(Utils.getHonorSignInAccount(), goalRequest, mGoalListener)
                .addOnSuccessListener(unused -> {
                    Message msg = new Message();
                    msg.what = RESULT_REGISTER_SUCCESS;
                    mHandler.sendMessage(msg);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "registerGoal: 订阅失败！！！" + e);
                    Message msg = new Message();
                    msg.what = RESULT_REGISTER_FAIL;
                    mHandler.sendMessage(msg);
                });
        });
    }

    private void unsubscribeGoal() {
        ThreadPool.execute(() -> {
            HealthKit.getGoalsClient(SubscribeActivity.this).unsubscribeGoal(Utils.getHonorSignInAccount(), mGoalListener)
                    .addOnSuccessListener(unused -> {
                        Message msg = new Message();
                        msg.what = RESULT_UNREGISTER_SUCCESS;
                        mHandler.sendMessage(msg);
                    })
                    .addOnFailureListener(e -> {
                        Message msg = new Message();
                        msg.what = RESULT_UNREGISTER_FAIL;
                        mHandler.sendMessage(msg);
                    });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribeGoal();
    }
}

