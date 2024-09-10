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
import com.hihonor.healthkitdemo.data.SummaryItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康汇总页面列表适配器
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class SummaryListAdapter extends RecyclerView.Adapter<SummaryListAdapter.ViewHolder> {
    private List<SummaryItemData> mDataList = new ArrayList<>();

    public SummaryListAdapter() {
    }

    public SummaryListAdapter(List<SummaryItemData> dataList) {
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
    }

    public SummaryListAdapter setDataList(List<SummaryItemData> dataList) {
        mDataList.clear();
        if (dataList != null) {
            mDataList.addAll(dataList);
        }
        return this;
    }

    @NonNull
    @Override
    public SummaryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryListAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.summay_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryListAdapter.ViewHolder holder, int position) {
        if (position < 0 || position > mDataList.size()) {
            return;
        }

        SummaryItemData itemData = mDataList.get(position);

        holder.name.setText(itemData.getName());
        holder.value.setText(itemData.getValue());
        holder.unit.setText(itemData.getUnit());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        TextView value;

        TextView unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_summary_name);
            value = itemView.findViewById(R.id.item_summary_value);
            unit = itemView.findViewById(R.id.item_summary_unit);
        }
    }
}
