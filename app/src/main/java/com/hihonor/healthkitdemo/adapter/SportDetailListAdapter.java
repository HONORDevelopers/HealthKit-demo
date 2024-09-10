/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;
import com.hihonor.healthkitdemo.data.SportDetailData;
import com.hihonor.healthkitdemo.data.SportDistanceDetailData;
import com.hihonor.healthkitdemo.utils.TimeUtils;
import com.hihonor.mcs.fitness.health.constants.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动记录详情列表Adapter
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SportDetailListAdapter extends RecyclerView.Adapter<SportDetailListAdapter.ViewHolder> {
    private List<SportDetailData> mDataList = new ArrayList<>();

    public SportDetailListAdapter(List<SportDetailData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public void setDataList(List<SportDetailData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.sport_detail_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SportDetailData segmentData = mDataList.get(position);
        if (segmentData == null) {
            return;
        }
        holder.mDate.setText(TimeUtils.timestamp2Date(segmentData.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        switch (segmentData.getDataType()) {
            // 关联数据Type
            case DataType.SAMPLE_STEP_RATE: {
                visibleView(holder.mLyt1, holder.mLyt2, holder.mLyt3, holder.mLyt4);
                List<Integer> list = segmentData.getValue();
                if (list != null && !list.isEmpty()) {
                    holder.mJumpSpeed.setText("" + list.get(0).intValue());
                }
            }
                break;
            case DataType.SAMPLE_SPEED: {
                visibleView(holder.mLyt2, holder.mLyt1, holder.mLyt3, holder.mLyt4);
                List<Integer> list = segmentData.getValue();
                if (list != null && !list.isEmpty()) {
                    holder.mSpeed.setText("" + list.get(0).intValue());
                }
            }
                break;
            case DataType.SAMPLE_LOCATION: {
                visibleView(holder.mLyt3, holder.mLyt1, holder.mLyt2, holder.mLyt4);
                List<SportDistanceDetailData> list = segmentData.getValue();
                if (list != null && !list.isEmpty()) {
                    SportDistanceDetailData distanceDetail = list.get(0);
                    if (distanceDetail == null) {
                        return;
                    }
                    holder.mLatitude.setText("" + distanceDetail.getLatitude());
                    holder.mLongitude.setText("" + distanceDetail.getLongitude());
                    holder.mPrecision.setText("" + distanceDetail.getPrecision());
                    holder.mGpsTra.setText("" + distanceDetail.getGpsTra());
                }
                break;
            }

            default:
                break;
        }

    }

    private void visibleView(LinearLayout showView, View ... views) {
        showView.setVisibility(View.VISIBLE);
        for (View v : views) {
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLyt1;
        LinearLayout mLyt2;
        LinearLayout mLyt3;
        LinearLayout mLyt4;
        TextView mDate;

        TextView mJumpSpeed;

        TextView mSpeed;

        TextView mLatitude;
        TextView mLongitude;
        TextView mPrecision;
        TextView mGpsTra;

        TextView mDynamicHeartRate;
        TextView mRestingHeartRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLyt1 = itemView.findViewById(R.id.sport_detail_1);
            mLyt2 = itemView.findViewById(R.id.sport_detail_2);
            mLyt3 = itemView.findViewById(R.id.sport_detail_3);
            mLyt4 = itemView.findViewById(R.id.sport_detail_4);

            mDate = itemView.findViewById(R.id.item_date);

            mJumpSpeed = itemView.findViewById(R.id.item_detail_jump_speed);

            mSpeed = itemView.findViewById(R.id.item_detail_speed);

            mLatitude = itemView.findViewById(R.id.item_detail_latitude);
            mLongitude = itemView.findViewById(R.id.item_detail_longitude);
            mPrecision = itemView.findViewById(R.id.item_detail_precision);
            mGpsTra = itemView.findViewById(R.id.item_detail_gpsTra);

            mDynamicHeartRate = itemView.findViewById(R.id.sport_item_dynamic_heart_rate);
            mRestingHeartRate = itemView.findViewById(R.id.sport_item_resting_heart_rate);
        }
    }
}
