package com.xcleans.hotfix;

import android.app.Application;
import com.taobao.sophix.a.e;
import com.taobao.sophix.listener.PatchLoadStatusListener;

public abstract class SophixManager {
    public static final String VERSION = "3.2.6";

    public static SophixManager getInstance() {
        return e.a;
    }

    public abstract void initialize();

    public abstract SophixManager setContext(Application var1);

    public abstract SophixManager setAppVersion(String var1);

    public abstract SophixManager setPatchLoadStatusStub(PatchLoadStatusListener var1);

    public abstract SophixManager setEnableFullLog();

    public abstract SophixManager setPreLoadedClass(Class var1);

    public abstract SophixManager setSecretMetaData(String var1, String var2, String var3);

    public abstract SophixManager setAesKey(String var1);

    public abstract SophixManager setEnableDebug(boolean var1);

    public abstract SophixManager setUnsupportedModel(String var1, int var2);

    public abstract SophixManager setProcessSpecialClass(Class var1);

    public abstract SophixManager setUsingEnhance();

    public abstract SophixManager setHost(String var1, boolean var2);

    public abstract void queryAndLoadNewPatch();

    public abstract Object getPatchStateInfo();

    public abstract void cleanPatches();

    public abstract void killProcessSafely();
}
