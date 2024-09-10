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
import com.hihonor.healthkitdemo.data.SleepSegmentData;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠数据详情列表Adapter
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SleepDetailListAdapter extends RecyclerView.Adapter<SleepDetailListAdapter.ViewHolder> {
    private List<SleepSegmentData> mDataList = new ArrayList<>();

    public void setData(List<SleepSegmentData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.detail_item_4_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SleepSegmentData segmentData = mDataList.get(position);
        if (segmentData == null) {
            return;
        }

        holder.mStartTime.setText(segmentData.getDetailStartTimeFormat());
        holder.mEndTime.setText(segmentData.getDetailEndTimeFormat());
        holder.mStatus.setText(segmentData.getStatus() + "");
        holder.mDuration.setText(segmentData.getDuration() + "");
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        TextView mStartTime;
        TextView mEndTime;
        TextView mStatus;
        TextView mDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mStartTime = itemView.findViewById(R.id.item_detail_start_time);
            mEndTime = itemView.findViewById(R.id.item_detail_end_time);
            mStatus = itemView.findViewById(R.id.item_detail_value1);
            mDuration = itemView.findViewById(R.id.item_detail_value2);
        }
    }
}
