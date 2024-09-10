/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.BloodOxygenField;

import java.util.List;

/**
 * 血氧数据格式转换
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class BloodOxygenAdapter extends RecyclerView.Adapter<BloodOxygenAdapter.ViewHolder> {
    private static final int BLOOD_OXYGEN = 90;

    private List<SampleData> dataList;

    public BloodOxygenAdapter(List<SampleData> dataList) {
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
        long startTime = sampleData.getStartTime();
        long endTime = sampleData.getEndTime();
        int bloodOxygen = sampleData.getInteger(BloodOxygenField.FIELD_BLOOD_OXYGEN_NAME);
        String time = TimeUtils.timestamp2DateMinute(startTime);
        holder.tvStep.setText(bloodOxygen + "%");
        holder.tvTime.setText(time);
        if (bloodOxygen < BLOOD_OXYGEN) {
            holder.tvStep.setTextColor(Color.RED);
        } else {
            holder.tvStep.setTextColor(Color.GREEN);
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
