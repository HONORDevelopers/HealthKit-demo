/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.mcs.fitness.health.constants.DataType;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.CaloriesField;
import com.hihonor.mcs.fitness.health.datastruct.DistanceField;
import com.hihonor.mcs.fitness.health.datastruct.StrengthField;

import java.util.List;

/**
 * 步数、卡路里、距离、运动类型四种数据模型
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SampleCaloriesDistanceStrengthAdapter extends RecyclerView.Adapter<SampleCaloriesDistanceStrengthAdapter.ViewHolder>{
    private static final int MOTION_TYPE_1 = 1;

    private static final int MOTION_TYPE_2 = 2;

    private static final int MOTION_TYPE_3 = 3;

    private static final int MOTION_TYPE_4 = 4;

    private static final int MOTION_TYPE_9 = 9;

    private List<SampleData> mListData;

    public SampleCaloriesDistanceStrengthAdapter(List<SampleData> mListData) {
        this.mListData = mListData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SampleCaloriesDistanceStrengthAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_step_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SampleData sampleData = mListData.get(position);
        if (sampleData == null) {
            return;
        }
        int dataType = sampleData.getDataType();
        long startTime = sampleData.getStartTime();
        long endTime = sampleData.getEndTime();
        String time = TimeUtils.timestamp2Date(startTime,"HH:mm") + "-" + TimeUtils.timestamp2Date(endTime, "HH:mm");
        switch (dataType) {
            case DataType.SAMPLE_CALORIES:
                int calories = sampleData.getInteger(CaloriesField.FIELD_CALORIES_NAME);
                holder.tvStep.setText(calories + "千卡");
                holder.tvTime.setText(time);
                break;
            case DataType.SAMPLE_DISTANCE:
                int distance = sampleData.getInteger(DistanceField.FIELD_DISTANCE_NAME);
                holder.tvStep.setText(distance + "米");
                holder.tvTime.setText(time);
                break;
            case DataType.SAMPLE_STRENGTH:
                int motionType = sampleData.getInteger(StrengthField.FIELD_MOTION_TYPE_NAME);
                String name;
                if (motionType == MOTION_TYPE_1) {
                    name = "步行";
                } else if (motionType == MOTION_TYPE_2) {
                    name = "跑步";
                } else if (motionType == MOTION_TYPE_3) {
                    name = "爬楼";
                } else if (motionType == MOTION_TYPE_4) {
                    name = "骑行";
                } else if (motionType == MOTION_TYPE_9) {
                    name = "游泳";
                } else {
                    name = "";
                }
                holder.tvStep.setText(name);
                holder.tvTime.setText(time);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStep;

        private TextView tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStep = itemView.findViewById(R.id.tv_step);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
