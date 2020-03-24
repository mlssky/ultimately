package com.xcleans.apm;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.Log;

import com.getkeepsafe.relinker.ReLinker;

/**
 *
 */
@Keep
public class NativeUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private volatile boolean mIsInitSucc = false;

    /**
     * @param context
     */
    public void init(final @NonNull Context context) {
        if (!mIsInitSucc) {
            ReLinker.recursively().loadLibrary(context, "breakpad", new ReLinker.LoadListener() {
                @Override
                public void success() {
                    mIsInitSucc = true;
                    Log.d("Native", "load succ");
                    init(context.getCacheDir().getAbsolutePath());
                    testcrash();
                }

                @Override
                public void failure(Throwable t) {
                    Log.e("Native", "load err:" + t.toString());
                }
            });
        }

    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

    }

    public native void init(String crashSavePath);

    public native void testcrash();
}

