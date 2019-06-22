//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.manager;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.taobao.sophix.e.i;
import com.taobao.sophix.e.k;
import com.xcleans.base.ReLinker;
import com.xcleans.hotfix.SophixManager;
import com.xcleans.hotfix.listener.DefaultPatchLoadStatusListener;
import com.xcleans.hotfix.listener.PatchLoadStatusListener;
import com.xcleans.hotfix.net.NetworkManager;
import com.xcleans.hotfix.patch.GlobalProperty;
import com.xcleans.hotfix.util.FileUtils;
import com.xcleans.hotfix.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//com.taobao.sophix.a.e
public class SophixManagerImpl extends SophixManager {
    public static SophixManagerImpl       a       = new SophixManagerImpl();
    private       String                  mAppVersionName;
    private       PatchLoadStatusListener mPatchLoadStatusListener;
    private       AtomicBoolean           mIsInit = new AtomicBoolean(false);

    //网络管理
    private NetworkManager e = new NetworkManager();

    private PatchManager f = new PatchManager();

    private SophixManagerImpl() {
        this.f.a(this.e);
        this.mPatchLoadStatusListener = new DefaultPatchLoadStatusListener();
    }

    @Override
    public void initialize() {
        try {
            this.initializeInner();
        } catch (Throwable var2) {
            com.taobao.sophix.e.d.b("SophixManager", "init", var2, new Object[0]);
            if (com.taobao.sophix.a.a.a()) {
                throw var2;
            }
            this.a(false);
        }

    }

    private void initializeInner() {
        if (GlobalProperty.globalApp == null) {
            throw new RuntimeException("app is null");
        } else {
            if (this.mIsInit.compareAndSet(false, true)) {
                long t1 = System.currentTimeMillis();
                String patchRootDir = FileUtils.ensureDirCreated(GlobalProperty.globalApp.getFilesDir(), "sophix").getAbsolutePath();
                this.f.initPatchDirs(patchRootDir);

                GlobalProperty.a = i.a(GlobalProperty.globalApp, "hpatch_version", 0);
                com.taobao.sophix.e.d.c("SophixManager", "Sophix starting...", new Object[]{"sdk", "3.2.8", "main process", com.taobao.sophix.e.f.a(GlobalProperty.globalApp), "fingerprint", Build.FINGERPRINT, "app version", this.mAppVersionName, "patch version", GlobalProperty.a});
                com.taobao.sophix.d.a.a.a(0, true);
                this.e.a(patchRootDir, this.mAppVersionName);
                this.f.c(false);
                this.f.d(false);
                this.f.a(this.mAppVersionName, this.mPatchLoadStatusListener);
                com.taobao.sophix.e.d.b("SophixManager", "initialize", new Object[]{"time consumed(ms)", System.currentTimeMillis() - t1});
            }
        }
    }

    @Override
    public SophixManager setContext(Application var1) {
        if (this.mIsInit.get()) {
            com.taobao.sophix.e.d.b("SophixManager", "setContext", new Object[]{"can not set app again"});
        } else {
            GlobalProperty.globalApp = var1;
        }
        return this;
    }

    public SophixManager setAppVersion(String var1) {
        this.mAppVersionName = var1;
        return this;
    }

    @Override
    public SophixManager setPatchLoadStatusStub(PatchLoadStatusListener var1) {
        if (var1 != null) {
            this.mPatchLoadStatusListener = var1;
            GlobalProperty.patchLoadStatusListener = var1;
        }
        return this;
    }

    public SophixManager setEnableFullLog() {
        com.taobao.sophix.e.d.a(true);
        com.taobao.sophix.e.d.a(com.taobao.sophix.e.d.a.a);
        return this;
    }

    /**
     * 设置预先加载的类
     *
     * @param var1
     * @return
     */
    public SophixManager setPreLoadedClass(Class var1) {
        this.f.setPreLoadedClass(var1);
        return this;
    }

    public SophixManager setHost(String var1, boolean var2) {
        com.taobao.sophix.e.d.c("SophixManager", "setHost", new Object[0]);
        com.taobao.sophix.d.a.a.a(var1, var2);
        return this;
    }

    public SophixManager setTags(List<String> var1) {
        if (var1 != null && var1.size() > 0) {
            this.e.a(new ArrayList(var1));
        }

        return this;
    }


    /**
     *
     */
    @Override
    public void queryAndLoadNewPatch() {
        com.taobao.sophix.c.c var1 = new com.taobao.sophix.c.c(1);
        int var2 = GlobalProperty.a == -1 ? 0 : GlobalProperty.a;
        var1.d = var2;
        this.a((String) null, var1, this.mPatchLoadStatusListener);
    }


    public SophixManager setSecretMetaData(String idSecret, String appSecret, String rsaSecret) {
        this.e.a(idSecret, appSecret, rsaSecret);
        return this;
    }

    public void a(final String var1, final com.taobao.sophix.c.c var2, final PatchLoadStatusListener var3) {
        if (GlobalProperty.globalApp == null) {
            throw new RuntimeException("app is null");
        } else if (!com.taobao.sophix.e.f.a(GlobalProperty.globalApp)) {
            com.taobao.sophix.e.d.d("SophixManager", "queryLoadPatch", new Object[]{"not in main progress, skip"});
        } else {
            k.a(new Runnable() {
                public void run() {
                    String var1x = SophixManagerImpl.this.e.a(var1, var2, var3);
                    if (!TextUtils.isEmpty(var1x)) {
                        SophixManagerImpl.this.f.a(var1x, var3, var2);
                    }

                }
            });
        }
    }

    public SophixManager setUsingEnhance() {
        this.f.f();
        return this;
    }

    public void a(String var1, PatchLoadStatusListener var2) {
        if (!this.f.isDebugMode()) {
            com.taobao.sophix.e.d.d("SophixManager", "addPatch", new Object[]{"forbid loading local patch for secure reason in release mode"});
        } else {
            com.taobao.sophix.c.c var3 = new com.taobao.sophix.c.c(1);
            var3.d = -1;
            this.f.a(var1, var2, var3);
        }
    }

    public void a() {
        this.f.b();
    }

    public SophixManager setAesKey(String var1) {
        this.f.b(var1);
        return this;
    }

    public SophixManager setEnableDebug(boolean debug) {
        if (this.mIsInit.get()) {
            com.taobao.sophix.e.d.b("SophixManager", "setEnableDebug", new Object[]{"can not set debug state again"});
        } else {
            this.f.setEnableDebug(debug);

            //log debug set
            if (debug) {
                com.taobao.sophix.e.d.a(com.taobao.sophix.e.d.a.b);
            } else {
                com.taobao.sophix.e.d.a(com.taobao.sophix.e.d.a.c);
            }
        }

        return this;
    }

    public SophixManager setUnsupportedModel(String var1, int var2) {
        this.f.a(var1, var2);
        return this;
    }

    public SophixManager setProcessSpecialClass(Class var1) {
        this.f.a(var1);
        return this;
    }

    public Object getPatchStateInfo() {
        return this.f.d();
    }

    public void cleanPatches() {
        this.a(true);
    }

    public void a(boolean var1) {
        this.f.cleanPatches(var1);
    }

    public void killProcessSafely() {
        this.f.e();
    }
}
