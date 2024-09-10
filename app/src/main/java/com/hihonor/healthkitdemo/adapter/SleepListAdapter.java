/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.data.SleepData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠数据列表Adapter
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepListAdapter extends RecyclerView.Adapter<SleepListAdapter.ViewHolder> {
    private final List<SleepData> mDataList = new ArrayList<>();

    private ISleepListItem mSleepListItem;

    public SleepListAdapter(List<SleepData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public void setDataList(List<SleepData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.sleep_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SleepData sleepData = mDataList.get(position);

        // TODO 没有睡眠共有时间
        holder.mSleepDate.setText(sleepData.getStartTimeDataStamp());
        int nightTime = sleepData.getDeepSleep() + sleepData.getLightSleep() + sleepData.getRemSleep();
        int napTime = sleepData.getSporadicNaps();
        Context context = holder.itemView.getContext();
        holder.mSleepTime.setText(TimeUtils.formatDurationFromMinutes(context, nightTime + napTime));
        holder.mNightSleepTime.setText(TimeUtils.formatDurationFromMinutes(context, nightTime));
        holder.mNapSleepTime.setText(TimeUtils.formatDurationFromMinutes(context, napTime));
        holder.mRootView.setOnClickListener(view -> mSleepListItem.onClick(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;

        TextView mSleepDate;

        TextView mSleepTime;

        TextView mNightSleepTime;

        TextView mNapSleepTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mSleepDate = itemView.findViewById(R.id.item_date);
            mSleepTime = itemView.findViewById(R.id.item_sleep_time);
            mNightSleepTime = itemView.findViewById(R.id.item_night_sleep_time);
            mNapSleepTime = itemView.findViewById(R.id.item_nap_sleep_time);
        }
    }

    public void setOnItemClickListener(ISleepListItem sleepClickListener) {
        this.mSleepListItem = sleepClickListener;
    }

    public interface ISleepListItem {
        /**
         * 点击
         *
         * @param position 下标
         */
        void onClick(int position);
    }
}
