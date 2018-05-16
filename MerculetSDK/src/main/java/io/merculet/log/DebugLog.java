package io.merculet.log;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.merculet.config.Constant;
import io.merculet.util.Preconditions;
import io.merculet.util.SPHelper;

/***
 * Created by aaron on 14/11/13.
 * <p/>
 * This is free and unencumbered software released into the public domain.
 * <p/>
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * <p/>
 * For more information, please refer to <http://unlicense.org/>
 */
/***
 This is free and unencumbered software released into the public domain.

 Anyone is free to copy, modify, publish, use, compile, sell, or
 distribute this software, either in source code form or as a compiled
 binary, for any purpose, commercial or non-commercial, and by any
 means.

 For more information, please refer to <http://unlicense.org/>
 */

/**
 * @author aaron
 *         <p/>
 *         Create a simple and more understandable Android logs.
 */

public class DebugLog {

    static final String SDK_INTEGRATION_DEBUG = "MWSDKIntegrationDebug";
    static final String SDK_INTEGRATION_OK = "MWSDKIntegrationTest";
    static final String SDK_DEBUG = "MWSDKDebug";
    static boolean DebugLogOpen = true;


    private DebugLog() {
    }

    public static void setDebugLogOpen(boolean open) {
        DebugLogOpen = open;
    }

    private static boolean isDebuggable() {
        return DebugLogOpen && SPHelper.create().getDebugMode();
    }

    private static String getMethodNames() {
        StackTraceElement[] sElements = Thread.currentThread().getStackTrace();

        int stackOffset = LoggerPrinter.getStackOffset(sElements);

        stackOffset++;
        StringBuilder builder = new StringBuilder();
        builder.append("  ").append(LoggerPrinter.BR);
        builder.append(LoggerPrinter.TOP_BORDER).append("\r\n")
                // 添加当前线程名
                .append("║ " + "MW SDK Version: " + Constant.VERSION).append("\r\n")
                .append(LoggerPrinter.MIDDLE_BORDER).append("\r\n")
                .append("║ " + "Thread: " + Thread.currentThread().getName()).append("\r\n")
                .append(LoggerPrinter.MIDDLE_BORDER).append("\r\n")
                // 添加类名、方法名、行数
                .append("║ ")
                .append(sElements[stackOffset].getClassName())
                .append(".")
                .append(sElements[stackOffset].getMethodName())
                .append(" ")
                .append(" (")
                .append(sElements[stackOffset].getFileName())
                .append(":")
                .append(sElements[stackOffset].getLineNumber())
                .append(")")
                .append("\r\n")
                .append(LoggerPrinter.MIDDLE_BORDER).append("\r\n")
                // 添加打印的日志信息
                .append("║ ").append("%s").append("\r\n")
                .append(LoggerPrinter.BOTTOM_BORDER).append("\r\n");
        return builder.toString();
    }

    public static void e(String message) {
        if (!isDebuggable())
            return;

        // Throwable instance must be created before any methods
        String s = getMethodNames();
        Log.e(SDK_DEBUG, String.format(s,message));
    }

    public static void i(String message) {
        if (!isDebuggable())
            return;

        String s = getMethodNames();
        Log.i(SDK_DEBUG, String.format(s,message));
    }

    public static void d(String message) {
        if (!isDebuggable())
            return;

        String s = getMethodNames();
        Log.d(SDK_DEBUG, String.format(s,message));
    }

    public static void v(String message) {
        if (!isDebuggable())
            return;

        String s = getMethodNames();
        Log.v(SDK_DEBUG, String.format(s,message));
    }

    public static void debug(String message) {
        if (!isDebuggable())
            return;

        // Throwable instance must be created before any methods
//        getMethodNames();
//        Log.i(SDK_INTEGRATION_DEBUG, className + createLog(message));

        String s = getMethodNames();
        Log.i(SDK_DEBUG, String.format(s,message));
    }

    public static void test(String message) {
        if (!isDebuggable())
            return;

        String s = getMethodNames();
        Log.w(SDK_INTEGRATION_OK, String.format(s,message));
    }
}

