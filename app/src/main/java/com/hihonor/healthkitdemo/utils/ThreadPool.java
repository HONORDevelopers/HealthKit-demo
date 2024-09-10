/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

import android.os.Looper;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 *
 * @author hw0070631
 * @since 2024-08-01
 */
public class ThreadPool {
    private static final ThreadFactory S_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable arg0) {
            return new Thread(arg0, "HeartStudy thread:" + integer.getAndIncrement());
        }
    };

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), S_THREAD_FACTORY);

    /**
     * 在子线程执行runnable
     *
     * @param runnable 任务
     */
    public static void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (!isMainThread()) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }
    }

    /**
     * 是否是主线程
     *
     * @return 是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
