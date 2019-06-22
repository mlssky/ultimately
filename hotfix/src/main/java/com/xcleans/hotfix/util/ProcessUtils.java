//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.util.Iterator;

public final class ProcessUtils {
    private static String sMainProcessName;
    private static String sCurrentProcessName;

    /**
     * @param app
     * @return
     */
    public static boolean isMainProcess(Application app) {
        try {
            sCurrentProcessName = getCurrentProcessName(app);
            if (TextUtils.isEmpty(sMainProcessName)) {
                sMainProcessName = app.getPackageManager().getPackageInfo(app.getBaseContext().getPackageName(), 0).applicationInfo.processName;
            }
        } catch (Throwable var5) {
        }
        return !TextUtils.isEmpty(sMainProcessName) && sMainProcessName.equals(sCurrentProcessName);
    }

    public static String getCurrentProcessName(final Context appContext) {
        //get current process name
        if (TextUtils.isEmpty(sCurrentProcessName)) {
            int myPid = Process.myPid();
            ActivityManager aMgr = (ActivityManager) appContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (aMgr != null) {
                Iterator iterator = aMgr.getRunningAppProcesses().iterator();
                while (iterator.hasNext()) {
                    RunningAppProcessInfo processInfo = (RunningAppProcessInfo) iterator.next();
                    if (processInfo.pid == myPid) {
                        sCurrentProcessName = processInfo.processName;
                    }
                }
            }
        }
        return sCurrentProcessName;
    }
}
