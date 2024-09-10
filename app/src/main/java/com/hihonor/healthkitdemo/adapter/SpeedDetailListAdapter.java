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
import com.hihonor.healthkitdemo.data.SpeedDetailData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 速度详情页面列表适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SpeedDetailListAdapter extends RecyclerView.Adapter<SpeedDetailListAdapter.ViewHolder> {

    private List<SpeedDetailData> mDataList = new ArrayList<>();

    public SpeedDetailListAdapter() {
    }

    public SpeedDetailListAdapter(List<SpeedDetailData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public SpeedDetailListAdapter setDataList(List<SpeedDetailData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
        return this;
    }

    @NonNull
    @Override
    public SpeedDetailListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpeedDetailListAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item_3_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpeedDetailListAdapter.ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SpeedDetailData segmentData = mDataList.get(position);
        if (segmentData == null) {
            return;
        }

        holder.mStartTime.setText(TimeUtils.timestamp2DateSecond(segmentData.getStartTime()));
        holder.mEndTime.setText(TimeUtils.timestamp2DateSecond(segmentData.getEndTime()));
        holder.mValue.setText(String.valueOf(segmentData.getSpeed()));
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
