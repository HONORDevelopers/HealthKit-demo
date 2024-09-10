/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.data.DailySportData;

import java.util.ArrayList;
import java.util.List;

/**
 * 日常活动详情列表Adapter
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SevenDayDataAdapter extends RecyclerView.Adapter<SevenDayDataAdapter.ViewHolder> {
    private List<DailySportData> mRecordList = new ArrayList<>();

    public void setRecordList(List<DailySportData> recordList) {
        this.mRecordList.clear();
        if (recordList != null) {
            this.mRecordList.addAll(recordList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mRecordList.size()) {
            return;
        }

        DailySportData dailySportData = mRecordList.get(position);
        if (dailySportData == null) {
            return;
        }
        if (position == 0) {
            Context context = holder.itemView.getContext();

            holder.date.setText(context.getResources().getString(R.string.seven_day_item_date));
            holder.date.setTypeface(Typeface.DEFAULT_BOLD);
            holder.step.setText(context.getResources().getString(R.string.seven_day_item_step));
            holder.step.setTypeface(Typeface.DEFAULT_BOLD);
            holder.calorie.setText(context.getResources().getString(R.string.seven_day_item_calorie));
            holder.calorie.setTypeface(Typeface.DEFAULT_BOLD);
            holder.distance
                .setText(context.getResources().getString(R.string.seven_day_item_distance));
            holder.distance.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.date.setText(dailySportData.getData());
            holder.step.setText(dailySportData.getStep());
            holder.calorie.setText(dailySportData.getCalorie());
            holder.distance.setText(dailySportData.getDistance());
        }
    }

    @Override
    public int getItemCount() {
        return mRecordList == null ? 0 : mRecordList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        TextView step;

        TextView calorie;

        TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_daily_date);
            step = itemView.findViewById(R.id.item_daily_step);
            calorie = itemView.findViewById(R.id.item_daily_calorie);
            distance = itemView.findViewById(R.id.item_daily_distance);
        }
    }
}
