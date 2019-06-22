//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.patch.dex.cold;

import android.app.Application;
import android.os.Build;
import android.os.Build.VERSION;

import com.taobao.sophix.a.b;
import com.taobao.sophix.core.dex.ILibPathLoader;
import com.taobao.sophix.core.dex.SophixNative;
import com.xcleans.hotfix.manager.PatchException;
import com.xcleans.hotfix.patch.GlobalProperty;
import com.xcleans.hotfix.util.ReflectUtil;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//from com.taobao.sophix.mFindResourceMth.a.a.a

/**
 *
 */
class PatchPathClassLoader extends PathClassLoader implements ILibPathLoader {
    //true:替换系统Classloader
    private final boolean mIsNeedReplaceSysClzLoader;

    private final Method mFindResourceMth;
    private final Method mFindLibraryMth;
    private final Method mFindClassMth;
    private final Method mFindLoadedClassMth;
    private final Method mGetPackageMth;

    //系统默认的ClassLoader
    private ClassLoader   mParentClzLoader;
    //当前系统使用的ClassLoader
    private ClassLoader   mCurrentSysClzLoader;
    //need patch  file list
    private List<DexFile> mPatchDexFiles = new ArrayList();

    /**
     * insert new ClassLoader before Pre-ClassLoader
     *
     * @param parentSys
     * @throws com.taobao.sophix.a.b
     */
    PatchPathClassLoader(ClassLoader parentSys) throws PatchException {
        super("", parentSys.getParent());
        try {
            synchronized (parentSys) {
                Field parent = ReflectUtil.getDeclaredField(ClassLoader.class, "parent");
                parent.set(parentSys, this);
            }

            //当前的ClassLoader的父ClassLoader 已经替换
            this.mParentClzLoader = parentSys;

            this.mFindLibraryMth = ReflectUtil.getDeclaredMethod(ClassLoader.class, "findLibrary", new Class[]{String.class});
            this.mFindClassMth = ReflectUtil.getDeclaredMethod(ClassLoader.class, "findClass", new Class[]{String.class});
            this.mFindResourceMth = ReflectUtil.getDeclaredMethod(ClassLoader.class, "findResource", new Class[]{String.class});
            this.mFindLoadedClassMth = ReflectUtil.getDeclaredMethod(ClassLoader.class, "findLoadedClass", new Class[]{String.class});
            this.mGetPackageMth = ReflectUtil.getDeclaredMethod(ClassLoader.class, "getPackage", new Class[]{String.class});

            //samsung????  samsung
            String var2 = "zaszun#".replace("s", "m").
                    replace("z", "s").
                    replace("#", "g");//samsung

            boolean var3 = Build.BRAND.equals(var2) && Build.VERSION.SDK_INT == 21;
            boolean target24Above = VERSION.SDK_INT >= 24;

            //setUsingEnhance
            this.mIsNeedReplaceSysClzLoader = !ColdDexManager.a/**现在默认为false*//*setUsingEnhance*/ &&
                    (target24Above ||
                            var3/**特殊处理三星手机*/ ||
                            GlobalProperty.f/**使用代理主工程的Application*/);

            this.mCurrentSysClzLoader = (ClassLoader) (this.mIsNeedReplaceSysClzLoader ? this : this.mParentClzLoader);

            //什么情况下会使用这个逻辑呢
            if (this.mIsNeedReplaceSysClzLoader) {

                //替换掉当前的ClassLoader
                Object loadApkObj = ReflectUtil.getDeclaredField(com.taobao.sophix.e.g.a, "mPackageInfo").
                        get(com.taobao.sophix.b.b.b.getBaseContext());
                ReflectUtil.getDeclaredField(loadApkObj.getClass(), "mClassLoader").set(loadApkObj, this);
                Thread.currentThread().setContextClassLoader(this);

                if (target24Above) {
                    //VERSION.SDK_INT >= 24 && !GlobalProperty.f
                    //目前的逻辑不会走到这个逻辑的
                    if (ColdDexManager.c()) {
                        this.clearResolvedCache(this.getDexCache(com.taobao.sophix.b.b.b));
                        this.a();
                    }
                } else if (var3) {//三星且api=21
                    this.resolvedTypes(this.getDexCache(com.taobao.sophix.b.b.b));
                    this.a();
                }
            }

        } catch (NoSuchMethodException var7) {
            throw new PatchException(84, var7);
        } catch (NoSuchFieldException var8) {
            throw new PatchException(85, var8);
        } catch (IllegalAccessException var9) {
            throw new PatchException(86, var9);
        }
    }

    /**
     * @param patchFile Jar or APK file "classes.dex"
     * @throws b
     */
    void addDexFile(File patchFile) throws PatchException {
        com.taobao.sophix.e.d.a("ColdClassLoader", "addDexFile", new Object[]{patchFile.getName()});
        DexFile dexFile;
        try {
            dexFile = DexFile.loadDex(patchFile.getPath(), ColdDexManager.withPostfix(patchFile.getPath()), 0);
        } catch (IOException var4) {
            throw new PatchException(81, var4);
        }

        this.mPatchDexFiles.add(dexFile);
    }

    /**
     * 优先从Patch dex 文件查找
     *
     * @param name
     * @return
     * @throws
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        boolean isPatchSdkPkgClz = this.isPatchSdkPkgClz(name);
        Class apkClz;
        Class patchClz;

        if (this.mIsNeedReplaceSysClzLoader) {
            try {
                apkClz = (Class) this.mFindLoadedClassMth.invoke(this.mParentClzLoader, name);
                if (apkClz != null) {
                    if (isPatchSdkPkgClz) {
                        return apkClz;
                    }
                    Iterator var4 = ColdDexManager.b().iterator();
                    while (var4.hasNext()) {
                        patchClz = (Class) var4.next();
                        if (patchClz.equals(apkClz)) {
                            return apkClz;
                        }
                    }
                }
            } catch (Exception var8) {
                com.taobao.sophix.e.d.b("ColdClassLoader", "findClass fail to find loaded class: " + name, new Object[]{var8});
            }
        }

        if (isPatchSdkPkgClz) {
            //如果是PatchSDK 直接从老的Classloader 加载
            try {
                return (Class) this.mFindClassMth.invoke(this.mParentClzLoader, name);
            } catch (Exception var7) {
                com.taobao.sophix.e.d.a("ColdClassLoader", "findClass fail to find kept class: " + name, var7, new Object[0]);
            }
        }

        //优先从PatchDex中查找
        Iterator iterator = this.mPatchDexFiles.iterator();
        do {
            //从合并的DEX无法找到时，使用默认的DEX查找类
            if (!iterator.hasNext()) {
                try {
                    apkClz = (Class) this.mFindClassMth.invoke(this.mParentClzLoader, name);
                    if (apkClz != null) {
                        return apkClz;
                    }
                } catch (Exception var6) {
                    com.taobao.sophix.e.d.a("ColdClassLoader", "findClass fail to find: " + name, var6, new Object[0]);
                }

                throw new ClassNotFoundException();
            }

            DexFile patchDexFile = (DexFile) iterator.next();
            patchClz = patchDexFile.loadClass(name, this.mCurrentSysClzLoader);
        } while (patchClz == null);

        return patchClz;
    }

    @Override
    public String findLibrary(String var1) {
        try {
            return (String) this.mFindLibraryMth.invoke(this.mParentClzLoader, var1);
        } catch (Exception var3) {
            com.taobao.sophix.e.d.b("ColdClassLoader", "findLibrary", var3, new Object[0]);
            return null;
        }
    }

    @Override
    public URL findResource(String var1) {
        try {
            ZipFile var2 = new ZipFile(com.taobao.sophix.b.b.c.getPath());
            ZipEntry var3 = var2.getEntry(var1);
            if (var3 != null) {
                String var6 = "jar:" + com.taobao.sophix.b.b.c.toURI().toURL() + "!/" + var1;
                return new URL(var6);
            }

            URL var4 = (URL) this.mFindResourceMth.invoke(this.mParentClzLoader, var1);
            if (var4 != null) {
                return var4;
            }
        } catch (Exception var5) {
            com.taobao.sophix.e.d.a("ColdClassLoader", "findResource", var5, new Object[0]);
        }

        com.taobao.sophix.e.d.d("ColdClassLoader", "findResource don't find " + var1, new Object[0]);
        return null;
    }

    @Override
    public Package getPackage(String var1) {
        try {
            return (Package) this.mGetPackageMth.invoke(this.mParentClzLoader, var1);
        } catch (Exception var3) {
            com.taobao.sophix.e.d.b("ColdClassLoader", "getPackage", var3, new Object[0]);
            return null;
        }
    }

    @Override
    public String getLdLibraryPath() {
        if (this.mParentClzLoader instanceof BaseDexClassLoader) {
            try {
                return (String) ReflectUtil.getDeclaredMethod(BaseDexClassLoader.class, "getLdLibraryPath", new Class[0]).invoke(this.mParentClzLoader);
            } catch (Exception var2) {
                com.taobao.sophix.e.d.b("ColdClassLoader", "getLdLibraryPath", var2, new Object[0]);
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // private method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param app
     * @return
     * @throws
     */
    private Object getDexCache(Application app) throws IllegalAccessException, NoSuchFieldException {
        return ReflectUtil.getDeclaredField(Class.class.getClass(), "dexCache").get(app.getClass());
    }

    /**
     *
     * @throws
     * @throws
     */
    private void a() throws IllegalAccessException, NoSuchFieldException {
        //defining class loader, or null for the "bootstrap" system loader.
        Field classLoaderField = ReflectUtil.getDeclaredField(Class.class.getClass(), "classLoader");
        Iterator var2 = ColdDexManager.b().iterator();
        while (var2.hasNext()) {
            Class var3 = (Class) var2.next();
            classLoaderField.set(var3, this);
        }
    }

    /**
     * @param appDexCacheObj
     * @throws b
     */
    private void resolvedTypes(Object appDexCacheObj) throws PatchException {
        try {
            Object[] var2 = ((Object[]) ReflectUtil.getDeclaredField(appDexCacheObj.getClass(), "resolvedTypes").get(appDexCacheObj));
            List var3 = ColdDexManager.b();
            for (int var4 = 0; var4 < var2.length; ++var4) {
                Class var5 = (Class) var2[var4];
                if (this.a(var5, var3)) {
                    var2[var4] = null;
                }
            }
        } catch (NoSuchFieldException var6) {
            throw new PatchException(85, var6);
        } catch (IllegalAccessException var7) {
            throw new PatchException(86, var7);
        }
    }

    /**
     * @param dexCacheObj
     * @throws b
     */
    private void clearResolvedCache(Object dexCacheObj) throws PatchException {
        File var2 = new File(com.taobao.sophix.b.b.d, "sophix.mkbm");
        if (!SophixNative.clearResolvedCache(var2.getPath(), dexCacheObj)) {
            throw new PatchException(78, "fail to clear cache! abort cold fix");
        }
    }

    private boolean a(Class var1, List<Class> var2) {
        return var1 != null && var1.getClassLoader() != null && var1.getClassLoader().equals(this.getClass().getClassLoader()) && !var2.contains(var1) && !this.isPatchSdkPkgClz(var1.getName());
    }

    private boolean isPatchSdkPkgClz(String var1) {
        return var1.startsWith("com.taobao.sophix.") || var1.startsWith("com.ali.fixHelper");
    }

}
