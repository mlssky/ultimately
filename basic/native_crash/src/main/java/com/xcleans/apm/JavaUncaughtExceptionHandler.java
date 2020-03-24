package com.xcleans.apm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by mengliwei on 2020/3/24.
 */
public class JavaUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Nullable
    private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler = null;

    /**
     * @param context
     */
    public void init(@NonNull Context context) {
        mOldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (!handlerException(t, e) && mOldUncaughtExceptionHandler != null && mOldUncaughtExceptionHandler != this) {
            mOldUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }

    /**
     * @param t
     * @param e
     * @return true:异常可以忽略
     */
    private boolean handlerException(@NonNull Thread t, @NonNull Throwable e) {
        //TODO obtaion exception info
        StringWriter crashInfoWriter = new StringWriter();
        PrintWriter localPrintWriter = new PrintWriter(crashInfoWriter);
        e.printStackTrace(localPrintWriter);
        localPrintWriter.close();
        return false;
    }
}
