/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.mcs.fitness.health.data.SampleData;
import com.hihonor.mcs.fitness.health.datastruct.StressField;

import java.util.List;

/**
 * 压力适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StressAdapter extends RecyclerView.Adapter<StressAdapter.ViewHolder> {
    private static final String[]STRESS_GRADE_NAME = {"放松", "正常", "中等", "偏高"};

    private List<SampleData> dataList;

    public StressAdapter(List<SampleData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public StressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StressAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_step_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StressAdapter.ViewHolder holder, int position) {
        SampleData sampleData = dataList.get(position);
        if (sampleData == null) {
            return;
        }

            long startTime = sampleData.getStartTime();
            long endTime = sampleData.getEndTime();
            int score = sampleData.getInteger(StressField.FIELD_SCORE_NAME);
            int grade = sampleData.getInteger(StressField.FIELD_GRADE_NAME);
            String time = TimeUtils.timestamp2Date(startTime, "HH:mm") + "-" + TimeUtils.timestamp2Date(endTime, "HH:mm");
            holder.tvTime.setText(time);
            setText(holder, score, grade);

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

    private void setText(StressAdapter.ViewHolder holder, int score, int grade) {
        String text = score + "-" + STRESS_GRADE_NAME[grade - 1];
        SpannableString spannableText = new SpannableString(text);
        switch (grade) {
            case 3:
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.YELLOW);
                int start = String.valueOf(score).length() + 1;
                spannableText.setSpan(colorSpan, start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 4:
                ForegroundColorSpan colorSpanRed = new ForegroundColorSpan(Color.RED);
                int startRed = String.valueOf(score).length() + 1;
                spannableText.setSpan(colorSpanRed, startRed, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }

        holder.tvStep.setText(spannableText);
    }
}

