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
import com.hihonor.healthkitdemo.data.StepCadenceDetailData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 步频详情页面列表适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class StepRateDetailListAdapter extends RecyclerView.Adapter<StepRateDetailListAdapter.ViewHolder> {

    private List<StepCadenceDetailData> mDataList = new ArrayList<>();

    public StepRateDetailListAdapter() {
    }

    public StepRateDetailListAdapter(List<StepCadenceDetailData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public StepRateDetailListAdapter setDataList(List<StepCadenceDetailData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
        return this;
    }

    @NonNull
    @Override
    public StepRateDetailListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepRateDetailListAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item_3_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StepRateDetailListAdapter.ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        StepCadenceDetailData segmentData = mDataList.get(position);
        if (segmentData == null) {
            return;
        }

        holder.mStartTime.setText(TimeUtils.timestamp2DateSecond(segmentData.getStartTime()));
        holder.mEndTime.setText(TimeUtils.timestamp2DateSecond(segmentData.getEndTime()));
        holder.mValue.setText(String.valueOf(segmentData.getCadence()));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;

        TextView mStartTime;

        TextView mEndTime;

        TextView mValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mStartTime = itemView.findViewById(R.id.item_detail_start_time);
            mEndTime = itemView.findViewById(R.id.item_detail_end_time);
            mValue = itemView.findViewById(R.id.item_detail_value);
        }
    }
}
