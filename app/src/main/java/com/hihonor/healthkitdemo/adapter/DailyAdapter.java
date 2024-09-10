/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hihonor.healthkitdemo.R;

import java.util.List;

/**
 * 日常活动数据适配器
 *
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    private List<String> mList;

    private OnItemClickListener mOnclickListener;

    public DailyAdapter(List<String> data){
        mList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position > mList.size()) {
            return;
        }
        String name = mList.get(position);
        if (TextUtils.isEmpty(name)) {
            return;
        }
        holder.tvName.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnclickListener == null) {
                    return;
                }
                mOnclickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        mOnclickListener = clickListener;
    }

    /**
     * 点击事件
     */
    public interface OnItemClickListener{
        /**
         * 点击
         *
         * @param positon 下标
         */
        void onItemClick(int positon);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.title);
        }
    }
}
