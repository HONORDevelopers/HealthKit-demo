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
import com.hihonor.healthkitdemo.data.LocationDetailData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置详情页面列表适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class LocationDetailListAdapter extends RecyclerView.Adapter<LocationDetailListAdapter.ViewHolder> {
    private List<LocationDetailData> mDataList = new ArrayList<>();

    public LocationDetailListAdapter() {
    }

    public LocationDetailListAdapter(List<LocationDetailData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public LocationDetailListAdapter setDataList(List<LocationDetailData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
        return this;
    }

    @NonNull
    @Override
    public LocationDetailListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationDetailListAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item_location_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationDetailListAdapter.ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        LocationDetailData segmentData = mDataList.get(position);
        if (segmentData == null) {
            return;
        }

        holder.mStartTime.setText(TimeUtils.timestamp2DateSecond(segmentData.getStartTime()));
        holder.mLatitude.setText(String.valueOf(segmentData.getLatitude()));
        holder.mLongitude.setText(String.valueOf(segmentData.getLongitude()));
        holder.mSpeed.setText(String.valueOf(segmentData.getSpeed()));
        holder.mPrecision.setText(String.valueOf(segmentData.getPrecision()));
        holder.mGpsTra.setText(String.valueOf(segmentData.getGpsTra()));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;

        TextView mStartTime;

        TextView mLatitude;

        TextView mLongitude;

        TextView mSpeed;

        TextView mPrecision;

        TextView mGpsTra;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mStartTime = itemView.findViewById(R.id.item_detail_start_time);
            mLatitude = itemView.findViewById(R.id.location_detail_latitude);
            mLongitude = itemView.findViewById(R.id.location_detail_longitude);
            mSpeed = itemView.findViewById(R.id.location_detail_speed);
            mPrecision = itemView.findViewById(R.id.location_detail_precision);
            mGpsTra = itemView.findViewById(R.id.location_detail_gpstra);
        }
    }
}
