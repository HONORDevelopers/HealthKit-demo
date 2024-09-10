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
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.BloodPressureField;

import java.util.List;

/**
 * 血压数据格式转换
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class BloodPressureAdapter extends RecyclerView.Adapter<BloodPressureAdapter.ViewHolder> {
    private static final int PRESSURE_TYPE_TWO = 2;

    private static final int PRESSURE_TYPE_FIVE = 5;

    private static final int PRESSURE_TYPE_FOUR = 4;

    private List<SampleData> dataList;

    public BloodPressureAdapter(List<SampleData> dataList) {
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

        int highPressure = sampleData.getInteger(BloodPressureField.FIELD_HIGH_PRESSURE_NAME);
        int lowPressure = sampleData.getInteger(BloodPressureField.FIELD_LOW_PRESSURE_NAME);
        int pressureType = sampleData.getInteger(BloodPressureField.FIELD_PRESSURE_TYPE_NAME);
        int pulse = sampleData.getInteger(BloodPressureField.FIELD_PULSE_NAME);
        if (pulse > 0) {
            holder.tvTime.setText(pulse + "/分钟");
        } else {
            holder.tvTime.setText("--/分钟");
        }
        holder.tvStep.setText(highPressure + " / " + lowPressure);
        if (pressureType == PRESSURE_TYPE_TWO || pressureType == PRESSURE_TYPE_FIVE) {
            holder.tvStep.setTextColor(Color.YELLOW);
        } else if (pressureType == PRESSURE_TYPE_FOUR) {
            holder.tvStep.setTextColor(Color.RED);
        } else{
            holder.tvStep.setTextColor(Color.BLACK);
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
