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
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.HeartRateField;

import java.util.List;

/**
 * 心率数据格式转换
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class HeartHealthAdapter extends RecyclerView.Adapter<HeartHealthAdapter.ViewHolder> {
    private List<SampleData> dataList;

    public HeartHealthAdapter(List<SampleData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public HeartHealthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeartHealthAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_step_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HeartHealthAdapter.ViewHolder holder, int position) {
        SampleData sampleData = dataList.get(position);
        if (sampleData == null) {
            return;
        }
        if (position == 0) {
            holder.tvStep.setText("静息心率-动态心率");
        } else {
            int dataType = sampleData.getDataType();
            long startTime = sampleData.getStartTime();
            long endTime = sampleData.getEndTime();
            int dynamicHeartRate = sampleData.getInteger(HeartRateField.FIELD_DYNAMIC_HEART_RATE_NAME);
            int restingHeartRate = sampleData.getInteger(HeartRateField.FIELD_RESTING_HEART_RATE_NAME);
            String time = TimeUtils.timestamp2Date(startTime, "HH:mm") + "-" + TimeUtils.timestamp2Date(endTime, "HH:mm");
            holder.tvStep.setText(restingHeartRate + "-" + dynamicHeartRate + "次/分钟");
            holder.tvTime.setText(time);
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
