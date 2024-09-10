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
import com.hihonor.healthkitdemo.data.SportRecordData;
import com.hihonor.healthkitdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动记录列表Adapter
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportRecordListAdapter extends RecyclerView.Adapter<SportRecordListAdapter.ViewHolder> {
    private final List<SportRecordData> mDataList = new ArrayList<>();

    private ISportRecordListItem mSportRecordListItem;

    public SportRecordListAdapter() {
        mDataList.clear();
    }

    public void setDataList(List<SportRecordData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.sport_record_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SportRecordData recordData = mDataList.get(position);
        holder.mItemDate.setText(recordData.getStampStartTime());
        holder.mItemType.setText(recordData.getSportTypeName());
        holder.mItemDistance.setText(String.valueOf(recordData.getDistance()));
        holder.mItemDuration
            .setText(TimeUtils.formatDurationFromSecond(holder.itemView.getContext(), recordData.getDuration()));
        holder.mItemCalorie.setText(String.valueOf(recordData.getCalorie()));
        holder.mRootView.setOnClickListener(view -> mSportRecordListItem.onClick(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;

        TextView mItemDate;

        TextView mItemType;

        TextView mItemDistance;

        TextView mItemDuration;

        TextView mItemCalorie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootView = itemView;
            mItemDate = itemView.findViewById(R.id.item_sport_record_date);
            mItemType = itemView.findViewById(R.id.item_sport_record_type);
            mItemDistance = itemView.findViewById(R.id.item_sport_distance);
            mItemDuration = itemView.findViewById(R.id.item_sport_duration);
            mItemCalorie = itemView.findViewById(R.id.item_sport_calorie);
        }
    }

    public void setOnItemClickListener(ISportRecordListItem recordListItem) {
        this.mSportRecordListItem = recordListItem;
    }

    public interface ISportRecordListItem {
        /**
         * 点击
         *
         * @param position 下标
         */
        void onClick(int position);
    }
}
