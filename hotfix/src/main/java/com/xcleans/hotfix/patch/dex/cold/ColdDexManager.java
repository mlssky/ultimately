package com.xcleans.hotfix.patch.dex.cold;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.taobao.sophix.a.b;
import com.taobao.sophix.c.c;
import com.taobao.sophix.core.dex.SophixNative;
import com.taobao.sophix.e.f;
import com.taobao.sophix.e.g;
import com.taobao.sophix.e.h;
import com.taobao.sophix.e.i;
import com.taobao.sophix.e.k;
import com.xcleans.hotfix.PatchStatus;
import com.xcleans.hotfix.listener.DefaultPatchLoadStatusListener;
import com.xcleans.hotfix.listener.PatchLoadStatusListener;
import com.xcleans.hotfix.manager.PatchException;
import com.xcleans.hotfix.patch.GlobalProperty;
import com.xcleans.hotfix.patch.dex.IFixManager;
import com.xcleans.hotfix.util.FileUtils;
import com.xcleans.hotfix.util.IoUtils;
import com.xcleans.hotfix.util.MD5Utils;
import com.xcleans.hotfix.util.ProcessUtils;

import dalvik.system.DexFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import java.util.zip.ZipOutputStream;

/**
 * com.taobao.sophix.b.a.a.b
 * form
 * 重启fix
 */
public class ColdDexManager implements IFixManager /*com.taobao.sophix.b.a.a*/ {
    private        c                       b;
    private static PatchLoadStatusListener c;
    private static String                  d;
    static         boolean                 a = false;

    /**
     * 默认False
     */
    public static void a() {
        com.taobao.sophix.e.d.c("ColdDexManager", "setUsingEnhance", new Object[]{"force to treat as enhanced app"});
        a = true;
    }

    public ColdDexManager(PatchLoadStatusListener var1, c var2) {
        c = var1;
        this.b = var2;
    }

    @Override
    public boolean hotfix/*a*/(final File patchFile) throws PatchException {
        //验证文件
        if (this.b(patchFile)) {

            //FIXME 暂且忽略这些逻辑
            this.b.a();
            this.b.c = "181";
            com.taobao.sophix.c.b.a(this.b);

            this.injectPatch(patchFile);

            return true;
        } else {

            //预加载
            k.a(new Runnable() {
                public void run() {
                    try {
                        long var1x = System.currentTimeMillis();
                        ColdDexManager.this.doOptPatch(patchFile);

                        long var3 = 0L;
                        File[] var5 = patchFile.getParentFile().listFiles();
                        int var6 = var5.length;
                        for (int var7 = 0; var7 < var6; ++var7) {
                            File var8 = var5[var7];
                            if (var8.getName().endsWith(".dex") || var8.getName().equals("sophix-merged.zip")) {
                                var3 += var8.length();
                            }
                        }

                        ColdDexManager.this.b.a();
                        ColdDexManager.this.b.g = var3;
                        ColdDexManager.this.b.e = System.currentTimeMillis() - var1x;
                        ColdDexManager.this.b.c = "181";
                        com.taobao.sophix.c.b.a(ColdDexManager.this.b);

                        ColdDexManager.c.onLoad(0, 100, "preload success", ColdDexManager.this.b.d);
                        ColdDexManager.c.onLoad(0, 12, "relaunch app", ColdDexManager.this.b.d);
                    } catch (PatchException var9) {
                        ColdDexManager.this.b.a();
                        ColdDexManager.this.b.c = "182";
                        ColdDexManager.this.b.i = var9.getErrCode();
                        com.taobao.sophix.c.b.a(ColdDexManager.this.b);
                        com.taobao.sophix.e.d.b("ColdDexManager", "patch", var9, new Object[]{"code", var9.getErrCode(), "msg", var9.getMessage()});
                        ColdDexManager.c.onLoad(0, var9.getErrCode(), var9.getMessage(), ColdDexManager.this.b.d);
                    } catch (Throwable var10) {
                        ColdDexManager.this.b.a();
                        ColdDexManager.this.b.c = "182";
                        ColdDexManager.this.b.i = 101;
                        com.taobao.sophix.c.b.a(ColdDexManager.this.b);
                        com.taobao.sophix.e.d.b("ColdDexManager", "patch", var10, new Object[0]);
                        ColdDexManager.c.onLoad(0, 101, var10.getMessage(), ColdDexManager.this.b.d);
                    }

                }
            });
            return false;
        }
    }

    /**
     * @param var1
     * @return
     */
    private boolean b(File var1) throws PatchException {
        String var2 = var1.getParent();
        if (a) {
            //检测odex文件是否存在
            return this.d(/**var1.getParentFile()/oat/basename.odex or var1 **/this.c(new File(var2, withPostfix(var1.getName()))));
        } else {

            File var3;
            if (VERSION.SDK_INT > 20) {
                var3 = new File(var2, withPostfix(IFixManager.Patch_Merged_FILENAME));
                return this.d(this.c(var3));
            } else {
                var3 = new File(var2, withPostfix(IFixManager.DEX_BASENAME));
                boolean var4 = this.d(var3);
                int var5 = i.a(com.taobao.sophix.b.b.b, "SP_BASE_DEX_COUNT", 0);

                for (var4 = var5 != 0 && var4; var5 != 0 && var4; --var5) {
                    File var6 = new File(var1.getParent(), withPostfix("classes" + String.valueOf(var5) + ".dex"));
                    boolean var7 = this.d(var6);
                    var4 = var7 && var4;
                }

                return var4;
            }
        }
    }

    /**
     * @param var1
     * @return var1.getParentFile()/oat/basename.odex
     */
    private File c(File var1) {
        if (VERSION.SDK_INT >= 26) {
            String var2 = var1.getName();
            var2 = var2.substring(0, var2.indexOf(".")) + ".odex";
            File var3 = new File(var1.getParentFile(), "oat" + File.separator + g() + File.separator + var2);
            return var3;
        } else {
            return var1;
        }
    }

    /**
     * 检测文件是否合法，不合法就删除处理
     *
     * @param var1
     * @return
     * @throws com.taobao.sophix.a.b
     */
    private boolean d(File var1) throws PatchException {
        com.taobao.sophix.e.d.b("ColdDexManager", "isOptFileValid", new Object[]{"file", var1.getName()});
        boolean var2 = false;
        if (var1.exists()) {
            //计算文件MD5和已保存的md5对比
            if (h.b(var1)) {
                //文件验证合法
                com.taobao.sophix.e.d.a("ColdDexManager", "isOptFileValid", new Object[]{"odex is legal"});
                var2 = true;
            } else if (f.a(com.taobao.sophix.b.b.b)) {//判断是否在主进程
                com.taobao.sophix.e.d.d("ColdDexManager", "isOptFileValid", new Object[]{"odex is illegal"});
                //校验失败删除文件
                if (!var1.delete()) {
                    com.taobao.sophix.e.d.e("ColdDexManager", "isOptFileValid", new Object[]{"fail to delete illegal odex file."});
                    throw new PatchException(80, "fail to delete odex file.");
                }
            }
        } else {
            com.taobao.sophix.e.d.b("ColdDexManager", "isOptFileValid", new Object[]{"odex not exist"});
        }

        return var2;
    }

    private synchronized void doOptPatch(File patcFile) throws PatchException {
        com.taobao.sophix.e.d.c("ColdDexManager", "doOptPatch start", new Object[0]);
        if (!ProcessUtils.isMainProcess(GlobalProperty.globalApp)) {
            com.taobao.sophix.e.d.d("ColdDexManager", "doOptPatch", new Object[]{"skip in main process"});
        } else {
            if (a) {
                this.preloadDex(patcFile);
            } else {
                //解压PAK dex及patch dex
                this.extractAllBaseDexFiles(patcFile);

                if (VERSION.SDK_INT > 20) {
                    //合并已经解压的dex到一个zip中加载
                    this.optMergedAllDexFilesAbove20(patcFile);
                } else {
                    this.optMergedAllDexFiles(patcFile);
                }
            }
            i.b(com.taobao.sophix.b.b.b, "sophix_system_fingerprint", Build.FINGERPRINT);
        }
    }


    private boolean g(File var1) {
        String var2 = var1.getPath();
        String var3 = (new File(var1.getParent(), IFixManager.DEX_BASENAME)).getPath();
        return SophixNative.removeClassesInBase(var2, var3, a(b()));
    }

    /**
     * 手机启动的Application 继承关系相关的类
     *
     * @return
     */
    private static List<Class> f() {

        Class var0 = com.taobao.sophix.b.b.b.getClass();

        ArrayList var1 = new ArrayList();
        c(var1, var0);

        b(var1, var0);

        a((List) var1, (Class) var0);

        return var1;
    }

    public static List<Class> b() {
        return ColdDexManager.aInnder.a;
    }

    private static void a(List<Class> var0, Class var1) {
        Class[] var2 = var1.getInterfaces();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Class var5 = var2[var4];
            c(var0, var5);
            a(var0, var5);
        }

    }

    private static void b(List<Class> var0, Class var1) {
        Class var2 = var1.getSuperclass();
        ArrayList var3 = new ArrayList();
        var3.add(Application.class);

        while (!var3.contains(var2)) {
            c(var0, var2);
            var2 = var2.getSuperclass();
        }

    }

    private static void c(List<Class> var0, Class var1) {
        if (!var0.contains(var1)) {
            var0.add(var1);
        }
    }

    private static String[] a(List<Class> var0) {
        ArrayList var1 = new ArrayList();
        Iterator var2 = var0.iterator();

        while (var2.hasNext()) {
            Class var3 = (Class) var2.next();
            String var4 = "L" + var3.getName().replace(".", "/") + ";";
            com.taobao.sophix.e.d.a("ColdDexManager", "exclude " + var4, new Object[0]);
            var1.add(var4);
        }

        String[] var5 = new String[var1.size()];
        var1.toArray(var5);
        return var5;
    }

    /**
     * preload dex that have extracted dexs one by one
     *
     * @param patchFile
     * @throws PatchException
     */
    private void optMergedAllDexFiles(File patchFile) throws PatchException {
        com.taobao.sophix.e.d.b("ColdDexManager", "optProcessedBaseDexFiles start", new Object[0]);
        File dexFile = new File(patchFile.getParent(), IFixManager.DEX_BASENAME);
        this.preloadDex(dexFile);

        for (int var3 = i.a(com.taobao.sophix.b.b.b, "SP_BASE_DEX_COUNT", 0); var3 != 0; --var3) {
            File var4 = new File(patchFile.getParent(), "classes" + String.valueOf(var3) + ".dex");
            if (!this.g(var4)) {
                throw new PatchException(105, "processBaseDexFiles fail");
            }
            this.preloadDex(var4);
        }
    }

    /**
     * merge all dex(include the dex from apk and patch's dex)
     * 按照如下顺序：
     * classes.dex(Patch dex),classes1.dex(原先APK中的classes.dex:重命名到classes1.dex), ... classesN.dex 顺序压缩合并到一个zip中加载
     *
     * @param patchFile
     * @throws PatchException
     */
    private void optMergedAllDexFilesAbove20(File patchFile) throws PatchException {
        com.taobao.sophix.e.d.b("ColdDexManager", "optMergedAllDexFiles start", new Object[0]);
        ZipOutputStream zipOutputStream = null;
        try {
            File var3 = File.createTempFile(IFixManager.Patch_Merged_FILENAME, ".tmp", patchFile.getParentFile());

            zipOutputStream = new ZipOutputStream(new FileOutputStream(var3));

            ZipEntry var4 = new ZipEntry(IFixManager.DEX_BASENAME);
            zipOutputStream.putNextEntry(var4);
            File dexFile = new File(patchFile.getParent(), IFixManager.DEX_BASENAME);
            FileInputStream dexFileInputStream = new FileInputStream(dexFile);
            FileUtils.copy(dexFileInputStream, zipOutputStream);
            dexFileInputStream.close();
            dexFile.delete();

            int index = 1;
            File var8 = new File(patchFile.getParent(), "classes" + String.valueOf(index) + ".dex");

            //TODO
            if (VERSION.SDK_INT < 27 && c() && !SophixNative.markKeptMethods(var8.getPath(), a(b()))) {
                com.taobao.sophix.e.d.e("ColdDexManager", "fail to mark kept methods", new Object[0]);
                throw new PatchException(106, "fail to mark kept methods");
            }

            while (var8.exists()) {
                com.taobao.sophix.e.d.b("ColdDexManager", "append dex file " + var8.getName() + " to jar", new Object[0]);
                ZipEntry var9 = new ZipEntry("classes" + String.valueOf(index + 1) + ".dex");
                zipOutputStream.putNextEntry(var9);
                FileInputStream var10 = new FileInputStream(var8);
                FileUtils.copy(var10, zipOutputStream);
                var10.close();
                var8.delete();
                ++index;
                var8 = new File(patchFile.getParent(), "classes" + String.valueOf(index) + ".dex");
            }

            zipOutputStream.close();
            File var16 = new File(patchFile.getParentFile(), IFixManager.Patch_Merged_FILENAME);
            boolean var17 = var3.renameTo(var16);
            if (!var17) {
                throw new IOException("fail to rename");
            }

            this.preloadDex(var16);
        } catch (IOException var14) {
            throw new PatchException(107, var14);
        } finally {
            IoUtils.closeSilent(zipOutputStream);
        }
    }

    /**
     * @param patchFile
     * @throws PatchException
     */
    private void preloadDex(File patchFile) throws PatchException {

        com.taobao.sophix.e.d.c("ColdDexManager", "preloadDex start", new Object[]{patchFile.getName()});
        long var2 = System.currentTimeMillis();
        File outOptDexFile = new File(withPostfix(patchFile.getPath()));
        try {
            if (VERSION.SDK_INT != 23 && VERSION.SDK_INT >= 21 && VERSION.SDK_INT < 26 && patchFile.length() > 2000000L) {
                if (!this.dex2oat(patchFile.getAbsolutePath(), outOptDexFile.getPath())) {
                    com.taobao.sophix.e.d.d("ColdDexManager", "preloadDex switch to traditional odex", new Object[0]);
                    DexFile.loadDex(patchFile.getAbsolutePath(), outOptDexFile.getPath(), 0);
                }
            } else {
                DexFile.loadDex(patchFile.getAbsolutePath(), outOptDexFile.getPath(), 0);
            }
        } catch (IOException var11) {
            throw new PatchException(102, var11);
        }

        File var5 = this.c(outOptDexFile);
        long var6 = 1000L;

        String dexMd5 = MD5Utils.md5(var5);

        while (TextUtils.isEmpty(dexMd5) || !dexMd5.equals(MD5Utils.md5(var5))) {
            dexMd5 = MD5Utils.md5(var5);
            try {
                com.taobao.sophix.e.d.b("ColdDexManager", "preloadDex wait dexopt " + String.valueOf(var6) + "ms", new Object[0]);
                Thread.sleep(var6);
            } catch (InterruptedException var10) {
            }

            if (var6 < 5000L) {
                var6 += 1000L;
            } else if (TextUtils.isEmpty(dexMd5)) {
                com.taobao.sophix.e.d.d("ColdDexManager", "preloadDex signature is always empty, abandon.", new Object[0]);
                break;
            }
        }
        com.taobao.sophix.e.d.c("ColdDexManager", "preloadDex end", new Object[]{"time consumed(ms)", System.currentTimeMillis() - var2});
        h.a(var5, dexMd5);
    }

    /**
     * 基本命令说明：
     * <p>
     * 基本参数：
     * <p>
     * --compiler-filter=(verify-none| interpret-only| space |balanced |speed |everything |time)
     * <p>
     * TODO 添加ART 相关的文档说明
     *
     * @param dexFilePathInput
     * @param oatFilePathOut
     * @return
     */
    private boolean dex2oat(String dexFilePathInput, String oatFilePathOut) {

        ArrayList command = new ArrayList();
        command.add("dex2oat");

        if (VERSION.SDK_INT >= 24) {
            command.add("--runtime-arg");
            command.add("-classpath");
            command.add("--runtime-arg");
            command.add("&");
        }
        command.add("--dex-file=" + dexFilePathInput);
        command.add("--oat-file=" + oatFilePathOut);
        command.add("--instruction-set=" + g());
        command.add("--compiler-filter=interpret-only");

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            int resultCode = process.waitFor();
            if (resultCode != 0) {
                com.taobao.sophix.e.d.e("ColdDexManager", "dex2oat exit un-normally: " + resultCode, new Object[0]);
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException var7) {
            com.taobao.sophix.e.d.b("ColdDexManager", "dex2oat was interrupted: ", var7, new Object[0]);
            return false;
        } catch (IOException var8) {
            com.taobao.sophix.e.d.b("ColdDexManager", "dex2oat failed to start", var8, new Object[0]);
            return false;
        }
    }

    /**
     * @return
     */
    private static String g() {
        if (d == null) {
            try {
                String var0 = (String) g.a(ApplicationInfo.class, "primaryCpuAbi").get(com.taobao.sophix.b.b.b.getApplicationInfo());
                d = (String) g.a(Class.forName("dalvik.system.VMRuntime"), "getInstructionSet", new Class[]{String.class}).invoke((Object) null, var0);
            } catch (Exception var1) {
                com.taobao.sophix.e.d.b("ColdDexManager", "fail to get primary cpu abi", var1, new Object[0]);
            }
        }

        return d;
    }

    /**
     * 将APK下的dex和Patch中的dex解压
     *
     * @param patchFile
     * @throws PatchException
     */
    private void extractAllBaseDexFiles(File patchFile) throws PatchException {
        com.taobao.sophix.e.d.b("ColdDexManager", "extractAllBaseDexFiles start", new Object[0]);

        ZipFile patchZipFile = null;
        ZipFile apkZipFile = null;
        try {
            try {
                patchZipFile = new ZipFile(patchFile);
                String var4 = GlobalProperty.globalApp.getApplicationInfo().sourceDir;
                apkZipFile = new ZipFile(var4);
            } catch (IOException var15) {
                //zip 格式异常
                throw new PatchException(PatchStatus.CODE_PRELOAD_NOT_ZIP_FORMAT, var15);
            }

            File patchFileParentFile = patchFile.getParentFile();
            ZipEntry patchDexZipEntry = patchZipFile.getEntry(IFixManager.DEX_BASENAME);
            if (patchDexZipEntry != null) {
                String var6 = patchFileParentFile + File.separator + IFixManager.DEX_BASENAME;
                this.unzipToFile(patchZipFile, patchDexZipEntry, var6);
            }

            int var18 = 0;

            i.b(GlobalProperty.globalApp, "SP_BASE_DEX_COUNT", var18);

            Enumeration entries = apkZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String entryName = zipEntry.getName();
                if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
                    String outSaveFilename = entryName.equals(IFixManager.DEX_BASENAME) ? "classes1.dex" : entryName;
                    String outSavePath = patchFileParentFile + File.separator + outSaveFilename;
                    this.unzipToFile(apkZipFile, zipEntry, outSavePath);
                    ++var18;
                }
            }
            i.b(com.taobao.sophix.b.b.b, "SP_BASE_DEX_COUNT", var18);
        } finally {
            IoUtils.closeSilent(patchZipFile);
            IoUtils.closeSilent(apkZipFile);
        }

    }

    /**
     * @param zipFile
     * @param zipEntry
     * @param outFilePath
     * @throws PatchException
     */
    private void unzipToFile(ZipFile zipFile, ZipEntry zipEntry, String outFilePath) throws PatchException {
        try {
            FileUtils.unzipToFile(zipFile, zipEntry, outFilePath);
        } catch (IOException e) {
            throw new PatchException(104, e);
        }
    }

    /**
     * inject patch file to system classloader
     *
     * @param patchFile
     * @throws b
     */
    private void injectPatch(File patchFile) throws PatchException {
        com.taobao.sophix.e.d.c("ColdDexManager", "doApplyPatch", new Object[0]);

        PatchPathClassLoader patchClzLoader = new PatchPathClassLoader(this.getClass().getClassLoader());
        if (a) {
            patchClzLoader.addDexFile(patchFile);
        } else if (VERSION.SDK_INT >= 21) {
            //合并一个完整的dex，替换掉老的DEX，ART模式
            //利用系统加载会自动分析多个dex的情况
            File mergedPatchFile = new File(patchFile.getParentFile(), IFixManager.Patch_Merged_FILENAME);
            patchClzLoader.addDexFile(mergedPatchFile);
        } else {
            //直接添加dex,允许存在多个dex
            int index = 0;
            int total = i.a(com.taobao.sophix.b.b.b, "SP_BASE_DEX_COUNT", 0);
            for (File file = new File(patchFile.getParent(), IFixManager.DEX_BASENAME); index <= total; file = new File(patchFile.getParent(), "classes" + String.valueOf(index) + ".dex")) {
                if (!file.exists()) {
                    com.taobao.sophix.e.d.e("ColdDexManager", "doApplyPatch", new Object[]{"file not exist", file.getName()});
                    throw new PatchException(79, "file not exist:" + file.getName());
                }
                patchClzLoader.addDexFile(file);
                ++index;
            }
        }

    }

    /**
     * ART 或强制使用加强模式
     *
     * @return
     */
    static boolean c() {
        return VERSION.SDK_INT >= 24 && !GlobalProperty.f;
    }

    /**
     * setClearPreVerified
     * @param var0
     */
    public static void a(Class var0) {
        if (VERSION.SDK_INT > 20) {
            com.taobao.sophix.e.d.a("ColdDexManager", "setClearPreVerified", new Object[]{"ignore in art."});
        } else {
            com.taobao.sophix.e.d.b("ColdDexManager", "setClearPreVerified", new Object[]{"clz", var0.getName()});
            Constructor[] var2 = var0.getDeclaredConstructors();
            Object var1;
            if (var2.length == 0) {
                com.taobao.sophix.e.d.b("ColdDexManager", "setClearPreVerified", new Object[]{"don't have any constructor", var0.getName()});
                Method[] var3 = var0.getDeclaredMethods();
                if (var3.length == 0) {
                    com.taobao.sophix.e.d.d("ColdDexManager", "setClearPreVerified", new Object[]{"don't have any method", var0.getName()});
                    return;
                }

                var1 = var3[0];
            } else {
                var1 = var2[0];
            }

            SophixNative.clearPreVerified(var1);
        }
    }

    /**
     * @param var0
     * @return
     */
    static String withPostfix(String var0) {
        return var0 + ".odex";
    }

    /**
     * @param var0
     */
    public static void setPreLoadedClass(Class var0) {
        ColdDexManager.aInnder.b(ColdDexManager.aInnder.a, var0);
    }

    static {
        File var0 = com.taobao.sophix.b.b.b.getFilesDir();
        boolean var1 = (new File(var0.getParentFile(), ".jiagu")).exists() | (new File(var0.getParentFile(), "lib/libjiagu.so")).exists();
        boolean var2 = (new File(var0.getParentFile(), "lib/libSecShell.so")).exists();
        boolean var3 = (new File(var0.getParentFile(), "tx_shell")).exists();
        boolean var4 = (new File(var0, "libijmDataEncryption.so")).exists();
        boolean var5 = (new File(var0.getParentFile(), ".cache/d3d3Lm5hZ2Fpbi5jb20=")).exists();
        a |= var1 | var2 | var3 | var4 | var5;
        if (a) {
            com.taobao.sophix.e.d.b("ColdDexManager", "init", new Object[]{"36", var1, "bb", var2, "le", var3, "ae", var4, "nj", var5});
        } else {
            com.taobao.sophix.e.d.a("ColdDexManager", "init", new Object[]{"plain app"});
        }

    }

    private static class aInnder {

        private static final List<Class> a = b();

        private static List<Class> b() {
            List var0 = ColdDexManager.f();
            PatchLoadStatusListener var1 = GlobalProperty.patchLoadStatusListener;
            if (var1 != null && !(var1 instanceof DefaultPatchLoadStatusListener)) {
                b(var0, var1.getClass());
            }
            return var0;
        }

        /**
         * @param target
         * @param clz
         */
        private static void b(List<Class> target, Class clz) {
            if (clz != null && !target.contains(clz)) {
                target.add(clz);
            }
        }
    }
}
