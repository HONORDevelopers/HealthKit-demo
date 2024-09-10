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
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.BloodSugarField;

import java.util.List;

/**
 * 血糖数据格式转换
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class BloodAdapter extends RecyclerView.Adapter<BloodAdapter.ViewHolder> {
    private static final String[] BLOOD_TIME_RANGE = {"全时段", "夜间", "空腹", "随机", "早餐后", "午餐前", "午餐后", "晚餐前", "晚餐后", "睡前"};

    private List<SampleData> dataList;

    public BloodAdapter(List<SampleData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public BloodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BloodAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_step_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BloodAdapter.ViewHolder holder, int position) {
        SampleData sampleData = dataList.get(position);
        if (sampleData == null) {
            return;
        }

        int timeRange = sampleData.getInteger(BloodSugarField.FIELD_TIME_RANGE_NAME);
        float measureValue = sampleData.getFloat(BloodSugarField.FIELD_MEASURE_VALUE_NAME);
        String time = BLOOD_TIME_RANGE[timeRange];
        holder.tvTime.setText(time);
        holder.tvStep.setText(measureValue + " mmol/L");
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
