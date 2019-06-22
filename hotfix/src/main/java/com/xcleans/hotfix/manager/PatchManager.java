package com.xcleans.hotfix.manager;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.taobao.sophix.e.f;
import com.taobao.sophix.e.h;
import com.taobao.sophix.e.i;
import com.taobao.sophix.e.j;
import com.xcleans.hotfix.PatchStatus;
import com.xcleans.hotfix.SophixApplication;
import com.xcleans.hotfix.SophixEntry;
import com.xcleans.hotfix.listener.PatchLoadStatusListener;
import com.xcleans.hotfix.patch.DexManager;
import com.xcleans.hotfix.patch.GlobalProperty;
import com.xcleans.hotfix.patch.dex.cold.ColdDexManager;
import com.xcleans.hotfix.util.FileUtils;
import com.xcleans.hotfix.util.ProcessUtils;
import com.xcleans.hotfix.util.ReflectUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * from  com.taobao.sophix.a.c
 */
public class PatchManager {
    private          int           a;
    private          File          b;
    private          File          c;
    private          File          d;
    private          File          e;
    private          String        f;
    private          String        g;
    private          Application   i;
    private          boolean       j;
    private volatile d             k;
    private          AtomicBoolean l = new AtomicBoolean(false);
    private          boolean       debugMode;

    private boolean m = false;
    private boolean n = false;
    private List    o = new ArrayList();


    /**
     * @param appVersion
     * @param var2
     */
    public synchronized void a(String appVersion, PatchLoadStatusListener var2) {
        if (TextUtils.isEmpty(appVersion)) {
            throw new RuntimeException("app version is null");
        } else if (GlobalProperty.globalApp == null) {
            throw new RuntimeException("app is null");
        } else {
            if (!this.l.get()) {
                com.taobao.sophix.e.d.c("PatchManager", "init", new Object[]{"appVersion", appVersion});

                if (GlobalProperty.globalApp instanceof SophixApplication) {
                    GlobalProperty.f = true;
                } else if (VERSION.SDK_INT >= 28) {
                    //强制一定要使用使用SophixApplication
                    com.taobao.sophix.e.d.d("PatchManager", "should use stable initialize when android version >= 9.0.", new Object[0]);
                    return;
                }

                this.f = appVersion;
                this.j = ProcessUtils.isMainProcess(GlobalProperty.globalApp);

                if (Looper.myLooper() != Looper.getMainLooper()) {
                    com.taobao.sophix.e.d.d("PatchManager", "init recommend to best call in main thread", new Object[0]);
                }

                if (this.h() && this.i()) {
                    this.a(var2);
                } else {
                    com.taobao.sophix.e.d.d("PatchManager", "patch is not the newest, skip loading in sub-process", new Object[0]);
                }

                if (GlobalProperty.f) {
                    this.o();
                    this.n();
                }

                this.l.set(true);
            }

        }
    }

    /**
     * @param patchDirPath
     */
    public void initPatchDirs(String patchDirPath) {

        File var2 = new File(patchDirPath);

        this.a = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "SP_SOPHIX_DIR_STATE", 0);

        boolean mainProcess = ProcessUtils.isMainProcess(GlobalProperty.globalApp);

        //????
        if (mainProcess && this.a(2)) {
            this.a = 3 & ~this.a;
            com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "SP_SOPHIX_DIR_STATE", this.a);
        }

        if (this.a(1)) {
            this.b = FileUtils.ensureDirCreated(var2, "patch_");
            this.c = FileUtils.ensureDirCreated(var2, "libs_");
            this.d = FileUtils.ensureDirCreated(var2, "patch");
            this.e = FileUtils.ensureDirCreated(var2, "libs");
        } else {
            this.b = FileUtils.ensureDirCreated(var2, "patch");
            this.c = FileUtils.ensureDirCreated(var2, "libs");
            this.d = FileUtils.ensureDirCreated(var2, "patch_");
            this.e = FileUtils.ensureDirCreated(var2, "libs_");
        }

        com.taobao.sophix.e.d.b("PatchManager", "initPatchDir", new Object[]{"patchDir", this.b.getName(), "nativeLibDir", this.c.getName()});
        GlobalProperty.d = this.b;
        GlobalProperty.e = this.c;

        if (mainProcess) {
            FileUtils.clearDir(this.d);
            FileUtils.clearDir(this.e);
        }

    }

    private boolean a(int var1) {
        return (this.a & var1) == var1;
    }

    /**
     *
     */
    private void g() {
        this.a |= 2;
        com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "SP_SOPHIX_DIR_STATE", this.a);
    }

    private boolean h() {
        String var2 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_version", "NO_FOUND_SOPHIX_APP_VERSION");
        boolean var1;
        if (!var2.equals(this.f)) {
            if (this.j) {
                com.taobao.sophix.e.d.d("PatchManager", "checkAppUpdatedForContinue", new Object[]{"app is updated. clean old patch."});
                this.cleanPatches(false);
                var1 = true;
            } else {
                com.taobao.sophix.e.d.b("PatchManager", "checkAppUpdatedForContinue", new Object[]{"app is updated"});
                var1 = false;
            }
        } else {
            var1 = true;
        }

        return var1;
    }

    private boolean i() {
        String var2 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "sophix_system_fingerprint", "INVALID_SYSTEM_FINGERPRINT");
        boolean var1;
        if (!var2.equals("INVALID_SYSTEM_FINGERPRINT") && !var2.equals(Build.FINGERPRINT)) {
            if (this.j) {
                com.taobao.sophix.e.d.d("PatchManager", "checkSystemOTAHappenedForContinue", new Object[]{"OTA happened, so clear odex files"});
                File[] var3 = new File[]{this.a()};
                com.taobao.sophix.e.b.a(this.b, var3);
                var1 = true;
            } else {
                com.taobao.sophix.e.d.b("PatchManager", "checkSystemOTAHappenedForContinue", new Object[]{"OTA happened"});
                var1 = false;
            }
        } else {
            var1 = true;
        }

        return var1;
    }

    public File a() {
        File[] var1 = this.b.listFiles(new FileFilter() {
            public boolean accept(File var1) {
                return var1.getName().endsWith(".jar");
            }
        });
        return var1 != null && var1.length > 0 ? var1[0] : null;
    }

    private void a(PatchLoadStatusListener var1) {
        if (this.j) {
            int var2 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_crash_num", 0);
            if (var2 >= 5) {
                com.taobao.sophix.e.d.d("PatchManager", "handleLocalPatch", new Object[]{"continuous crash happened too mush, so cleaning patch by force"});
                this.cleanPatches(false);
            }

            if (com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "hpatch_clear", false)) {
                com.taobao.sophix.e.d.d("PatchManager", "handleLocalPatch", new Object[]{"force cleaning patch as server require clear"});
                this.cleanPatches(true);
            }
        }

        File var4 = this.a();
        if (var4 != null) {
            com.taobao.sophix.c.c var3 = new com.taobao.sophix.c.c(3);
            var3.d = GlobalProperty.a;
            GlobalProperty.c = var4;
            this.a(var4, var1, var3);
        } else {
            com.taobao.sophix.e.d.d("PatchManager", "handleLocalPatch", new Object[]{"no any patch exists"});
        }

        if (this.j) {
            Handler var5 = new Handler();
            var5.postDelayed(new Runnable() {
                public void run() {
                    PatchManager.this.k();
                }
            }, 3000L);
        }

    }

    private void j() {
        if (!this.j) {
            com.taobao.sophix.e.d.a("PatchManager", "prepareCrashHandler", new Object[]{"skip"});
        } else {
            int var1 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_crash_num", 0);
            ++var1;
            com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "happ_crash_num", var1);
            com.taobao.sophix.a.a.a(true);
        }
    }

    private void k() {
        if (com.taobao.sophix.a.a.a() && !com.taobao.sophix.a.a.b()) {
            com.taobao.sophix.a.a.a(false);
            com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_crash_num");
            com.taobao.sophix.e.d.b("PatchManager", "clearCrashHandler", new Object[]{"clear crash flag"});
        }

    }

    /**
     * @param patchFileJar 本地的sophix-patch.jar文件
     * @param var2
     * @param var3
     */
    public synchronized void a(String patchFileJar, PatchLoadStatusListener var2, com.taobao.sophix.c.c var3) {
        com.taobao.sophix.e.d.a("PatchManager", "addPatch", new Object[]{"src patch", patchFileJar});
        if (!this.l.get()) {
            com.taobao.sophix.e.d.e("PatchManager", "addPatch", new Object[]{"did not init before!!"});
        } else {
            ZipFile var4 = null;
            File var5 = null;
            try {
                File patchFile = new File(patchFileJar);
                if (TextUtils.isEmpty(patchFileJar) || !patchFile.exists() || !patchFile.isFile() || !patchFile.getName().endsWith(".jar")) {
                    throw new com.taobao.sophix.a.b(20, "patch is illegal");
                }

                //设备过滤
                if (!com.taobao.sophix.e.j.a()) {
                    throw new com.taobao.sophix.a.b(4, "device is not support");
                }

                //有jar patch
                boolean var7 = this.a() != null;

                File var8 = var7 ? this.d : this.b;
                FileUtils.clearDir(var8);

                var5 = new File(var8, patchFile.getName());
                try {
                    FileUtils.copy(patchFile, var5);
                } catch (IOException var21) {
                    throw new PatchException(PatchStatus.CODE_LOAD_COPY_FILE, var21);
                }

                com.taobao.sophix.e.d.a("PatchManager", "addPatch", new Object[]{"dest patch", var5.getName()});
                GlobalProperty.a(var3.d);

                boolean var11;
                if (!TextUtils.isEmpty(this.g)) {
                    com.taobao.sophix.e.d.b("PatchManager", "addPatch", new Object[]{"aes key is set, do decrypt..."});
                    long var9 = System.currentTimeMillis();
                    var11 = com.taobao.sophix.e.a.a(var5.getAbsolutePath(), this.g);
                    if (!var11 || !var5.exists()) {
                        throw new com.taobao.sophix.a.b(72, "aes decrypt fail");
                    }

                    com.taobao.sophix.e.d.a("PatchManager", "addPatch", new Object[]{"finish local aes decrypt patch(ms)", System.currentTimeMillis() - var9});
                }

                Attributes var25 = this.a(var5);
                String var10 = var25.getValue("Modified-So");
                var4 = new ZipFile(var5);
                var11 = false;
                if (!TextUtils.isEmpty(var10)) {
                    var11 = true;
                    long var12 = System.currentTimeMillis();
                    com.taobao.sophix.e.d.a("PatchManager", "addPatch", new Object[]{"start unzip lib file"});
                    File var14 = var7 ? this.e : this.c;
                    com.taobao.sophix.b.c var15 = new com.taobao.sophix.b.c();
                    var15.a(var10, var4, var14);
                    com.taobao.sophix.e.d.a("PatchManager", "addPatch", new Object[]{"finish unzip lib file(ms)", System.currentTimeMillis() - var12});
                }

                if (var7) {
                    this.g();
                }

                boolean var26 = this.a(var25) && !this.n && com.taobao.sophix.b.a.b.b.a();
                if (!this.m && var7 && var26) {
                    com.taobao.sophix.e.d.d("PatchManager", "addPatch is hotfix patch, but app exist old patch, please relaunch app", new Object[0]);
                    var2.onLoad(0, 12, "relaunch app", var3.d);
                    return;
                }

                boolean var13 = var4.getEntry("classes.dex") != null;
                boolean var27 = Boolean.valueOf(var25.getValue("Has-Res")) || var4.getEntry("resources.arsc") != null || var4.getEntry("assets") != null;
                if (!var13 && (var11 || var27 && var7)) {
                    com.taobao.sophix.e.d.d("PatchManager", "addPatch only need to patch so lib, please relaunch app", new Object[0]);
                    var2.onLoad(0, 12, "relaunch app", var3.d);
                    return;
                }

                this.a(var5, var2, var3);
            } catch (com.taobao.sophix.a.b var22) {
                if (var5 != null) {
                    var5.delete();
                }

                var3.a();
                var3.c = "201";
                var3.i = var22.a();
                com.taobao.sophix.c.b.a(var3);
                com.taobao.sophix.e.d.b("PatchManager", "addPatch fail", var22, new Object[]{"code", var22.a(), "msg", var22.getMessage()});
                var2.onLoad(0, var22.a(), var22.getMessage(), var3.d);
            } catch (Throwable var23) {
                if (var5 != null) {
                    var5.delete();
                }

                var3.a();
                var3.c = "201";
                var3.i = 71;
                com.taobao.sophix.c.b.a(var3);
                com.taobao.sophix.e.d.b("PatchManager", "addPatch fail", var23, new Object[0]);
                var2.onLoad(0, 71, var23.getMessage(), var3.d);
            } finally {
                com.taobao.sophix.e.b.a(var4);
            }

        }
    }

    private synchronized void a(File var1, PatchLoadStatusListener var2, com.taobao.sophix.c.c var3) {
        com.taobao.sophix.e.d.b("PatchManager", "loadPatch", new Object[]{"patchFile", var1.getName()});
        ZipFile var4 = null;
        long var5 = System.currentTimeMillis();

        try {
            if (!this.debugMode) {
                if (!com.taobao.sophix.e.h.a(var1)) {
                    throw new com.taobao.sophix.a.b(75, "patch signInfo not match to apk");
                }
            } else {
                com.taobao.sophix.e.d.d("PatchManager", "loadPatch", new Object[]{"skip verifyPatchLegal in debug mode"});
            }

            try {
                var4 = new ZipFile(var1);
            } catch (IOException var24) {
                throw new com.taobao.sophix.a.b(77, var24);
            }

            Attributes var7 = this.a(var1);
            String var8 = var7.getValue("Modified-So");
            boolean var9 = Boolean.valueOf(var7.getValue("Has-Res")) || var4.getEntry("resources.arsc") != null || var4.getEntry("assets") != null;
            int var10 = 0;
            if (var4.getEntry("classes.dex") != null) {
                var10 |= 1;
            }

            if (var9) {
                var10 |= 4;
            }

            if (!TextUtils.isEmpty(var8)) {
                var10 |= 2;
            }

            var3.f = var10;
            boolean var11 = this.a(var7) && !this.n && com.taobao.sophix.b.a.b.b.a();
            var3.h = var11 ? 1 : 0;
            this.j();
            boolean var12 = false;
            boolean var13 = false;
            boolean var14 = false;
            boolean var15 = false;
            long var16;
            if (var4.getEntry("classes.dex") != null) {
                var16 = System.currentTimeMillis();
                com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"start patch dex file"});
                DexManager var18 = new DexManager();
                var12 = var18.a(var1, var11, var2, var3);
                if (var12) {
                    com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"finish patch dex file(ms)", System.currentTimeMillis() - var16});
                    this.l();
                } else {
                    com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"wait preload dex file complete"});
                    if (com.taobao.sophix.e.f.a(GlobalProperty.globalApp)) {
                        this.k();
                    }
                }
            } else {
                var15 = true;
                com.taobao.sophix.e.d.d("PatchManager", "loadPatch", new Object[]{"no dex file found"});
            }

            if ((var12 || var15) && var9) {
                var16 = System.currentTimeMillis();
                com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"start patch res file"});
                com.taobao.sophix.b.d var28 = new com.taobao.sophix.b.d();
                var14 = var28.a(var1);
                com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"finish patch res file(ms)", System.currentTimeMillis() - var16});
            }

            if (var12) {
                if (!TextUtils.isEmpty(var8)) {
                    var16 = System.currentTimeMillis();
                    com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"start patch lib file"});
                    com.taobao.sophix.b.c var29 = new com.taobao.sophix.b.c();
                    var13 = var29.a(var8, this.c);
                    com.taobao.sophix.e.d.c("PatchManager", "loadPatch", new Object[]{"finish patch lib file(ms)", System.currentTimeMillis() - var16});
                }

                if (VERSION.SDK_INT >= 18) {
                    com.taobao.sophix.b.c.a();
                }
            }

            if (!var12 && !var14 && !var13) {
                if (var15) {
                    com.taobao.sophix.e.d.c("PatchManager", "loadPatch fail", new Object[]{"invalid patch"});
                    var2.onLoad(0, 20, "did not load anything", var3.d);
                }
            } else {
                if (this.k == null) {
                    this.k = new d(var12, var14, var13, var11);
                }

                var3.a();
                var3.e = System.currentTimeMillis() - var5;
                var3.c = "200";
                com.taobao.sophix.c.b.a(var3);
                com.taobao.sophix.e.d.c("PatchManager", "loadPatch success", new Object[]{"hasDexPatched", var12, "hasResPatched", var14, "hasSOPatched", var13});
                var2.onLoad(0, 1, "load success", var3.d);
            }
        } catch (com.taobao.sophix.a.b var25) {
            var3.a();
            var3.c = "201";
            var3.i = var25.a();
            com.taobao.sophix.c.b.a(var3);
            com.taobao.sophix.e.d.b("PatchManager", "loadPatch fail", var25, new Object[]{"code", var25.a(), "msg", var25.getMessage()});
            var2.onLoad(0, var25.a(), var25.getMessage(), var3.d);
        } catch (Throwable var26) {
            var3.a();
            var3.c = "201";
            var3.i = 71;
            com.taobao.sophix.c.b.a(var3);
            com.taobao.sophix.e.d.b("PatchManager", "loadPatch fail", var26, new Object[0]);
            var2.onLoad(0, 71, var26.getMessage(), var3.d);
        } finally {
            com.taobao.sophix.e.b.a(var4);
        }

    }

    private Attributes a(File var1) throws com.taobao.sophix.a.b {
        InputStream var2 = null;
        JarFile var3 = null;

        Attributes var8;
        try {
            var3 = new JarFile(var1);
            JarEntry var4 = var3.getJarEntry("META-INF/SOPHIX.MF");
            var2 = var3.getInputStream(var4);
            Manifest var5 = new Manifest(var2);
            Attributes var6 = var5.getMainAttributes();
            String var7 = var6.getValue("Sophix-Version");
            if (TextUtils.isEmpty(var7) || !"2.0".equals(var7)) {
                throw new com.taobao.sophix.a.b(76, "require version:2.0 but patch version:" + var7);
            }

            var8 = var6;
        } catch (IOException | com.taobao.sophix.a.b var12) {
            throw new com.taobao.sophix.a.b(73, var12);
        } finally {
            com.taobao.sophix.e.b.a(var2);
            com.taobao.sophix.e.b.a(var3);
        }

        return var8;
    }

    private boolean a(Attributes var1) throws JSONException {
        boolean var2 = Boolean.parseBoolean(var1.getValue("Support-Hot"));
        if (var2) {
            String var3 = var1.getValue("System-Reference");
            if (!TextUtils.isEmpty(var3)) {
                com.taobao.sophix.e.d.a("PatchManager", "checkSupportHotfix", new Object[]{"systemReferences", var3});
                JSONArray var4 = new JSONArray(var3);
                ClassLoader var5 = this.getClass().getClassLoader();

                for (int var6 = 0; var6 < var4.length(); ++var6) {
                    String var7 = (String) var4.get(var6);

                    try {
                        Class var8 = var5.loadClass(var7);
                        if ((var8.getModifiers() & 1) == 0) {
                            com.taobao.sophix.e.d.c("PatchManager", "checkSupportHotfix", new Object[]{"systemClzName", var7, "not public"});
                            return false;
                        }
                    } catch (ClassNotFoundException var9) {
                        com.taobao.sophix.e.d.a("PatchManager", "checkSupportHotfix", var9, new Object[0]);
                    }
                }
            }
        }

        return var2;
    }

    private void l() {
        if (com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_ishotfix", false)) {
            com.taobao.sophix.e.d.b("PatchManager", "clearPreVerified", new Object[]{"skip clear at hot"});
        } else {
            this.a(this.getClass());
            Iterator var1 = ColdDexManager.b().iterator();

            while (var1.hasNext()) {
                Class var2 = (Class) var1.next();
                this.a(var2);
            }

        }
    }

    private void m() {
        try {
            Class var1 = ReflectUtil.a;
            ReflectUtil.getDeclaredField(var1, "mOuterContext").set(GlobalProperty.globalApp.getBaseContext(), this.i);
            Object var2 = ReflectUtil.getDeclaredField(var1, "mPackageInfo").get(GlobalProperty.globalApp.getBaseContext());
            ReflectUtil.getDeclaredField(var2.getClass(), "mApplication").set(var2, this.i);
            Object var3 = ReflectUtil.getDeclaredField(var2.getClass(), "mActivityThread").get(var2);
            ReflectUtil.getDeclaredField(var3.getClass(), "mInitialApplication").set(var3, this.i);
            List var4 = (List) ReflectUtil.getDeclaredField(var3.getClass(), "mAllApplications").get(var3);

            for (int var5 = 0; var5 < var4.size(); ++var5) {
                var4.set(var5, this.i);
            }

            com.taobao.sophix.e.d.b("PatchManager", "replaceRealApplication finish", new Object[0]);
        } catch (Exception var6) {
            this.a("replaceRealApplication fail", (Throwable) var6);
        }

    }

    /**
     *
     */
    private void n() {
        try {
            String var2 = "$RealApplicationStub";
            String var3 = "_modified_name";
            String var4 = GlobalProperty.globalApp.getClass().getName();

            Class var1;
            try {
                var1 = GlobalProperty.globalApp.getClassLoader().loadClass(var4 + "$RealApplicationStub");
            } catch (Exception var7) {
                var1 = GlobalProperty.globalApp.getClassLoader().loadClass(var4.replace("_modified_name", "") + "$RealApplicationStub");
            }

            Class var5 = ((SophixEntry) var1.getAnnotation(SophixEntry.class)).value();
            Application var6 = (Application) var5.newInstance();
            ReflectUtil.getDeclaredMethod(Application.class, "attach", new Class[]{Context.class}).invoke(var6, GlobalProperty.globalApp.getBaseContext());
            this.i = var6;
            com.taobao.sophix.e.d.b("PatchManager", "callRealAppAttach", new Object[]{"finish call real App.attach, app", this.i.getClass().getName()});
        } catch (Exception var8) {
            this.a("callRealAppAttach", (Throwable) var8);
        }

    }

    private void o() {
        try {
            Class var1 = ReflectUtil.b;
            Object var2 = ReflectUtil.getDeclaredField(var1, "mBoundApplication").
                    get(ReflectUtil.getDeclaredMethod(var1, "currentActivityThread", new Class[0]).
                            invoke((Object) null));
            List var3 = (List) ReflectUtil.getDeclaredField(var2.getClass(), "providers").get(var2);
            if (var3 != null) {
                this.o.addAll(var3);
                var3.clear();
            }
        } catch (Exception var4) {
            this.a("removeProvidersTemporarily", (Throwable) var4);
        }

    }

    private void p() {
        try {
            Class var1 = ReflectUtil.b;
            Object var2 = ReflectUtil.getDeclaredMethod(var1, "currentActivityThread", new Class[0]).invoke((Object) null);
            Object var3 = ReflectUtil.getDeclaredField(var1, "mBoundApplication").
                    get(ReflectUtil.getDeclaredMethod(var1, "currentActivityThread", new Class[0]).invoke((Object) null));
            List var4 = (List) ReflectUtil.getDeclaredField(var3.getClass(), "providers").get(var3);
            if (var4 != null) {
                var4.addAll(this.o);
                this.o.clear();
                ReflectUtil.getDeclaredMethod(var1, "installContentProviders", new Class[]{Context.class, List.class}).invoke(var2, this.i, var4);
            }
        } catch (Exception var5) {
            this.a("installProviders", (Throwable) var5);
        }

    }

    public void b() {
        if (!GlobalProperty.f) {
            com.taobao.sophix.e.d.d("PatchManager", "do not call real App.onCreate", new Object[0]);
        } else {
            if (this.i != null) {
                this.m();
                this.p();
                this.i.onCreate();
            } else {
                com.taobao.sophix.e.d.d("PatchManager", "real application is null, fail to call onCreate", new Object[0]);
            }

        }
    }

    public void b(String var1) {
        this.g = var1;
    }

    public void setEnableDebug(boolean var1) {
        this.debugMode = var1;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void a(String var1, int var2) {
        com.taobao.sophix.e.j.a(var1, var2);
    }

    /**
     * setClearPreVerified
     *
     * @param var1
     */
    public void a(Class var1) {
        ColdDexManager.a(var1);
    }

    public Object d() {
        return this.k;
    }

    public synchronized void cleanPatches(boolean var1) {
        com.taobao.sophix.e.d.d("PatchManager", "cleanPatches", new Object[]{"positive", var1});

        if (this.a() != null) {
            com.taobao.sophix.c.c var2 = new com.taobao.sophix.c.c(4);
            var2.d = GlobalProperty.a;
            var2.c = var1 ? "300" : "301";
            com.taobao.sophix.c.b.a(var2);
        }

        FileUtils.clearDir(this.b);
        FileUtils.clearDir(this.c);
//        com.taobao.sophix.e.b.a(this.b);
//        com.taobao.sophix.e.b.a(this.c);

        boolean var4 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "happ_ishotfix", false);
        String var3 = com.taobao.sophix.e.i.a(GlobalProperty.globalApp, "SP_SOPHIX_DEVICE_ID", (String) null);
        com.taobao.sophix.e.i.a(GlobalProperty.globalApp);

        GlobalProperty.a(GlobalProperty.a);
        com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "happ_version", this.f);
        com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "SP_SOPHIX_DEVICE_ID", var3);
        com.taobao.sophix.e.i.b(GlobalProperty.globalApp, "happ_ishotfix", var4);
        com.taobao.sophix.e.d.d("PatchManager", "cleanPatches", new Object[]{"finish clean"});
    }

    private void a(String var1, Throwable var2) {
        throw new RuntimeException("abandon initialization: " + var1, var2);
    }

    public void e() {
        com.taobao.sophix.e.d.d("PatchManager", "killProcessSafely", new Object[0]);
        Process.killProcess(Process.myPid());
    }

    public void a(com.taobao.sophix.c.a var1) {
        com.taobao.sophix.c.b.a(var1);
    }

    public void setPreLoadedClass(Class var1) {
        ColdDexManager.setPreLoadedClass(var1);
    }

    /**
     * setUsingEnhance
     */
    public void f() {
        ColdDexManager.a();
    }

    public void c(boolean var1) {
        this.m = var1;
    }

    public void d(boolean var1) {
        this.n = var1;
    }
}
