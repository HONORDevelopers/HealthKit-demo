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
import com.hihonor.mcs.fitness.health.datastruct.AltitudeField;
import com.hihonor.mcs.fitness.health.datastruct.StepField;

import java.util.List;

/**
 * 步数适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StepDailyAdapter extends RecyclerView.Adapter<StepDailyAdapter.ViewHolder> {
    private List<SampleData> dataList;

    public StepDailyAdapter(List<SampleData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_step_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SampleData sampleData = dataList.get(position);
        if (sampleData == null) {
            return;
        }
        int dataType = sampleData.getDataType();
        long startTime = sampleData.getStartTime();
        long endTime = sampleData.getEndTime();
        String time = TimeUtils.timestamp2Date(startTime,"HH:mm") + "-" + TimeUtils.timestamp2Date(endTime, "HH:mm");
        switch (dataType) {
            case DataType.SAMPLE_STEPS:
                int step = sampleData.getInteger(StepField.FIELD_STEP_NAME);
                holder.tvStep.setText(step + "步");
                holder.tvTime.setText(time);
                break;
            case DataType.SAMPLE_ALTITUDE:
                float height = sampleData.getFloat(AltitudeField.FIELD_HEIGHT_NAME);
                holder.tvStep.setText(height + "米");
                holder.tvTime.setText(time);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
