/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.fragment.daily;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 基础fragment
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 上下文
     */
    protected Context context;

    @Override
    public void onAttach(@NonNull Context contxt) {
        super.onAttach(contxt);
        this.context = contxt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        initData();
        return view;
    }

    /**
     * 获取布局
     *
     * @return 布局资源id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     *
     * @param view view
     */
    protected abstract void initView(View view);

    /**
     * 初始化数据
     */
    public void initData() {
    }
}
