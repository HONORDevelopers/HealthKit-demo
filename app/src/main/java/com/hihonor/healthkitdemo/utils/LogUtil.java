/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.utils;

import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Logging tools class
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class LogUtil {
    private static final String TAG = LogUtil.class.getSimpleName();

    /**
     * If it is in debug mode (true), log is forced to be turned on
     */
    private static boolean DEBUG = true;

    /**
     * The output stream object of the log file
     */
    private static BufferedWriter bw = null;

    /**
     * The maximum number of logs allowed to be cached (for example, the SD card is not ready)
     */
    private static final int MAX_CACHE_SIZE = 128;

    /**
     * The maximum size allowed for the log file, currently 3M
     */
    private static final int LOG_FILE_MAX_SIZE = 3 * 1024 * 1024;

    /**
     * The maximum days,2 days
     */
    private static final long TWO_DAYS_LIMIT = 2 * 24 * 60 * 60 * 1000;

    /**
     * Log file storage directory
     */
    private static final String LOG_FILE_DIR = "logs";

    /**
     * The log queue to be recorded to the file, need to support synchronization
     */
    private static Queue<String> queLogs = new ConcurrentLinkedQueue<String>();

    /**
     * Log level and its corresponding character label
     */
    private static SparseArray<String> degreeLabel = new SparseArray<String>();

    static String logFile;

    /**
     * Do you need to log to a file
     */
    private static boolean IS_NEED_FILELOG = true;

    private static OutputStreamWriter outputStreamWriter;

    private static FileOutputStream fileOutputStream;

    static {
        if (degreeLabel == null) {
            degreeLabel = new SparseArray<String>();
        }
        degreeLabel.put(Log.VERBOSE, "V");
        degreeLabel.put(Log.DEBUG, "D");
        degreeLabel.put(Log.INFO, "I");
        degreeLabel.put(Log.WARN, "W");
        degreeLabel.put(Log.ERROR, "E");
    }

    public static void setIsDebug(boolean isDebug) {
        LogUtil.DEBUG = isDebug;
    }

    /**
     * Record VERBOSE level log, if DEBUG=false and the log level is higher than VERBOSE, no log is recorded.
     *
     * @param tag log TAG
     * @param msg Log information, support for dynamic parameter that can be one or more (to avoid log information
     *            string "+" operation execution)
     */
    public static void v(String tag, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.VERBOSE));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "[" + tag + "]" + msgStr);
            }
            if (IS_NEED_FILELOG) {
                writeLogToFile(Log.VERBOSE, tag, msgStr, null);
            }
        }
    }

    /**
     * Record DEBUG level log. If DEBUG=false and the log level is higher than DEBUG, no log is recorded.
     *
     * @param tag log TAG
     * @param msg Log information, support for dynamic parameter
     */
    public static void d(String tag, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.DEBUG));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "[" + tag + "]" + msgStr);
            }
            if (IS_NEED_FILELOG) {
                writeLogToFile(Log.DEBUG, tag, msgStr, null);
            }
        }
    }

    public static void i(boolean isNeedToLog, String tag, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.INFO));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "[" + tag + "]" + msgStr);
            }
            if (isNeedToLog) {
                writeLogToFile(Log.INFO, tag, msgStr, null);
            }
        }
    }

    /**
     * Record INFO level log. If DEBUG=false and the log level is higher than INFO, no log is recorded.
     *
     * @param tag log TAG
     * @param msg Log information, support for dynamic parameter
     */
    public static void i(String tag, String... msg) {
        i(true, tag, msg);
    }

    /**
     * Record WARN level log. If DEBUG=false and the log level is higher than WARN, no log is recorded.
     *
     * @param tag log TAG
     * @param msg Log information, support for dynamic parameter
     */
    public static void w(String tag, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.WARN));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "[" + tag + "]" + msgStr);
            }
            if (IS_NEED_FILELOG) {
                // 打印文件到手机里面
                writeLogToFile(Log.WARN, tag, msgStr, null);
            }
        }
    }

    /**
     * Record ERROR level log. If DEBUG=false and the log level is higher than ERROR, no log is recorded.
     *
     * @param tag log TAG
     * @param msg Log information, support for dynamic parameter
     */
    public static void e(String tag, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.ERROR));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "[" + tag + "]" + msgStr);
            }
            if (IS_NEED_FILELOG) {
                writeLogToFile(Log.ERROR, tag, msgStr, null);
            }
        }
    }

    /**
     * Record ERROR level log. If DEBUG=false and the log level is higher than ERROR, no log is recorded.
     *
     * @param tag log TAG
     * @param tr  throwable object
     * @param msg Log information, support for dynamic parameter
     */
    public static void e(String tag, Throwable tr, String... msg) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.VERBOSE));
        // write log to console
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.e(TAG, "[" + tag + "]" + msgStr + "[Throwable]" + (tr == null ? " null" : tr.getMessage()));
            }
            // wirte log to file
            if (IS_NEED_FILELOG) {
                writeLogToFile(Log.ERROR, tag, msgStr, tr);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        boolean isDebug = (DEBUG || Log.isLoggable(TAG, Log.VERBOSE));
        if (isDebug || IS_NEED_FILELOG) {
            String msgStr = combineLogMsg(msg);
            if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.e(TAG, "[" + tag + "]" + msgStr + "[Throwable]" + tr.getMessage());
            }
            if (IS_NEED_FILELOG) {
                writeLogToFile(Log.ERROR, tag, msgStr, tr);
            }
        }
    }

    /**
     * concatenate the dynamic parameter string into a string.
     *
     * @param msg log message, dynamic parameter
     * @return concatenated string
     */
    private static String combineLogMsg(String... msg) {
        if (null == msg) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : msg) {
            sb.append(s);
        }
        return sb.toString();
    }

    private static void writeLogToFile(int degree, String tag, String msg, Throwable tr) {

    }
}
