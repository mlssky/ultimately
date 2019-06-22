package com.ta.utdid2.device;

import android.app.Application;
import com.alibaba.sdk.android.utils.AMSDevReporter;
import com.alibaba.sdk.android.utils.AMSDevReporter.AMSSdkExtInfoKeyEnum;
import com.alibaba.sdk.android.utils.AMSDevReporter.AMSSdkTypeEnum;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.e.d;
import java.util.HashMap;

/* compiled from: Sophix */
public class SophixInvoker {
    private static final String TAG = "SophixInvoker";

    public static void invokeAlicloudReport(Application application) {
        HashMap hashMap = new HashMap();
        hashMap.put(AMSSdkExtInfoKeyEnum.AMS_EXTINFO_KEY_VERSION.toString(), SophixManager.VERSION);
        AMSDevReporter.asyncReport(application, AMSSdkTypeEnum.AMS_HOTFIX, hashMap);
        d.a(TAG, "device is active.", new Object[0]);
    }
}
