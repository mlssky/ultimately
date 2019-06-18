package com.xcleans.hotfix;

import android.os.Build;
import android.os.Build.VERSION;

import com.taobao.sophix.core.dex.SophixNative;
import com.taobao.sophix.core.dex.hot.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

/* compiled from: Sophix */
public class HotFixManager implements IFixManager {
    private static boolean a;

    private PatchClassLoader mPatchClsLoader = new PatchClassLoader(GlobalCfg.b.getClassLoader());

    static {
        a = true;
        if (VERSION.SDK_INT >= 21 && (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22)) {
            for (String equals : Build.SUPPORTED_64_BIT_ABIS) {
                if (equals.equals("arm64-v8a")) {
                    a = false;
                }
            }
        }
        a = SophixNative.initHotNative();
    }

    /**
     * isSupport#
     *
     * @return
     */
    public static boolean a() {
//        d.c("HotDexManager", "isSupport", Boolean.valueOf(a));
        return a;
    }

    /**
     * path file
     *
     * @param file
     * @return
     */
    @Override
    public boolean hotfix(File file) throws PatchException {
        DexFile loadDex = null;
        try {
            File file2 = new File(file.getParentFile(), "hotfix-patch.odex");
            loadDex = DexFile.loadDex(file.getPath(), file2.getAbsolutePath(), 0);
            this.mPatchClsLoader.init(loadDex);
        } catch (IOException e) {
            throw new PatchException(81, e);
        }

        Enumeration entries = loadDex.entries();
        while (entries.hasMoreElements()) {
            String str = (String) entries.nextElement();

            //新加类，忽略
            Class oldCls;
            try {
                oldCls = Class.forName(str, true, GlobalCfg.b.getClassLoader());
            } catch (Throwable var22) {
                continue;
            }

            Class patchCls;
            try {
                patchCls = Class.forName(str, true, this.mPatchClsLoader);
            } catch (ClassNotFoundException var20) {
                throw new PatchException(82, var20);
            }

            Constructor[] patchClsConstructors = patchCls.getDeclaredConstructors();

            for (int i = 0, n = patchClsConstructors.length; i < n; ++i) {
                Constructor patchClsConstructor = patchClsConstructors[i];
                if (patchClsConstructor.getAnnotation(MethodReplace.class) != null) {
                    Constructor oldConstructor;
                    try {
                        oldConstructor = oldCls.getDeclaredConstructor(this.a(patchClsConstructor.getParameterTypes()));
                    } catch (NoSuchMethodException e) {
                        throw new PatchException(83, e);
                    }
                    SophixNative.replaceMethod(oldConstructor, patchClsConstructor);
                }
            }

            Method[] patchClsMethods = patchCls.getDeclaredMethods();

            for (int i = 0, n = patchClsMethods.length; i < n; ++i) {
                Method patchClsMethod = patchClsMethods[i];
                if (patchClsMethod.getAnnotation(MethodReplace.class) != null) {
                    Method oldMethod;
                    try {
                        oldMethod = oldCls.getDeclaredMethod(patchClsMethod.getName(), this.a(patchClsMethod.getParameterTypes()));
                    } catch (NoSuchMethodException var18) {
                        throw new PatchException(84, var18);
                    }
                    SophixNative.replaceMethod(oldMethod, patchClsMethod);
                }
            }

        }
        return true;
    }

    /**
     * @param clsArr
     * @return
     */
    private Class<?>[] a(Class<?>[] clsArr) {
        Class<?>[] clsArr2 = new Class[clsArr.length];
        for (int i = 0; i != clsArr.length; i++) {
            Class cls = clsArr[i];
            try {
                cls = Class.forName(cls.getName());
            } catch (ClassNotFoundException e) {
            }
            clsArr2[i] = cls;
        }
        return clsArr2;
    }
}