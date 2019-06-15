package com.xcleans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;

/**
 * Created by mengliwei on 2019-06-05.
 */
public class PerUtil {


    /**
     *
     * @param context
     * @param permissions
     * @return
     */
    private static boolean hasOpsPermission(@NonNull Context context, @NonNull String... permissions) {
        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_ALLOWED) return true;
        }
        return false;
    }
}
