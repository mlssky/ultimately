//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.ArrayMap;

import com.taobao.sophix.e.g;
import com.xcleans.hotfix.util.ReflectUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class RessourcePatchManager {
    private Class a;

    public RessourcePatchManager() throws PatchException {
        try {
            this.a = Class.forName("android.content.res.AssetManager");
        } catch (ClassNotFoundException var2) {
            throw new PatchException(82, var2);
        }
    }

    /**
     * @param patchRes
     * @return
     * @throws PatchException
     */
    public boolean injectPatchResource(File patchRes) throws PatchException {

        String var2 = patchRes.getPath();

        if (VERSION.SDK_INT >= 24) {
            try {
                this.a(var2);
            } catch (Exception var11) {
                throw new PatchException(121, var11);
            }
        }

        AssetManager assets = GlobalCfg.b.getAssets();
        synchronized (assets) {
            Method var5 = this.a("addAssetPath", String.class);
            try {
                if (VERSION.SDK_INT <= 20) {
                    this.a(assets, var5, var2);

                    var5.invoke(assets, var2);

                    this.a("ensureStringBlocks").invoke(assets);
                } else {
                    var5.invoke(assets, var2);
                }
            } catch (Exception var9) {
                throw new PatchException(123, var9);
            }

            try {
                this.a();
            } catch (Exception var8) {
                throw new PatchException(122, var8);
            }

            return true;
        }
    }

    /**
     * @param patchResPath
     * @throws Exception
     */
    @TargetApi(24)
    private void a(String patchResPath) throws Exception {


        Object loadApk = ReflectUtil.getDeclaredField(g.a, "mPackageInfo").get(GlobalCfg.b.getBaseContext());

        Field splitResDirs = ReflectUtil.getDeclaredField(loadApk.getClass(), "mSplitResDirs");

        String[] oldSplitResDirs = (String[]) splitResDirs.get(loadApk);

        splitResDirs.set(loadApk, this.appendPatchResource(oldSplitResDirs, patchResPath));
    }

    /**
     * @param mSplitResDirs
     * @param patchResPath
     * @return
     */
    private String[] appendPatchResource(String[] mSplitResDirs, String patchResPath) {

        String[] resArr = new String[1 + (mSplitResDirs != null ? mSplitResDirs.length : 0)];

        if (mSplitResDirs != null) {
            System.arraycopy(mSplitResDirs, 0, resArr, 0, mSplitResDirs.length);
        }
        resArr[resArr.length - 1] = patchResPath;

        return resArr;
    }

    private void a() {
        Collection var1;
        Class var2;
        Object var3;
        if (VERSION.SDK_INT >= 19) {
            var2 = Class.forName("android.app.ResourcesManager");
            var3 = g.a(var2, "getInstance", new Class[0]).invoke((Object) null);

            try {
                ArrayMap var4 = (ArrayMap) g.a(var2, "mActiveResources").get(var3);
                var1 = var4.values();
            } catch (NoSuchFieldException var5) {
                var1 = (Collection) g.a(var2, "mResourceReferences").get(var3);
            }
        } else {
            var2 = g.b;
            var3 = g.a(var2, "currentActivityThread", new Class[0]).invoke((Object) null);
            if (var3 == null) {
                Object var7 = g.a(Application.class, "mLoadedApk").get(com.taobao.sophix.b.b.b);
                var3 = g.a(var7.getClass(), "mActivityThread").get(var7);
            }

            HashMap var9 = (HashMap) g.a(var2, "mActiveResources").get(var3);
            var1 = var9.values();
        }

        Iterator var6 = var1.iterator();

        while (var6.hasNext()) {
            WeakReference var8 = (WeakReference) var6.next();
            Resources var10 = (Resources) var8.get();
            if (var10 != null) {
                var10.updateConfiguration(var10.getConfiguration(), var10.getDisplayMetrics());
                com.taobao.sophix.e.d.a("ResourceManager", "update successfully", new Object[0]);
            }
        }

    }


    /**
     * @param assetManager
     * @param addAssetPathMethod addAssetPathMehots
     * @param patchResPath       PatchResPath
     * @throws Exception
     */
    private void a(AssetManager assetManager, Method addAssetPathMethod, String patchResPath) throws Exception {

        Method destroyMethod = ReflectUtil.getDeclaredMethod(this.a, "destroy");
        Method getStringBlockCountMethod = ReflectUtil.getDeclaredMethod(this.a, "getStringBlockCount");
        Method getCookieNameMethod = ReflectUtil.getDeclaredMethod(this.a, "getCookieName", Integer.TYPE);
        Field stringBlocksField = ReflectUtil.getDeclaredField(this.a, "mStringBlocks");

        ArrayList var8 = new ArrayList();
        int var9 = (Integer) getStringBlockCountMethod.invoke(assetManager);

        String var11;
        for (int var10 = 0; var10 < var9; ++var10) {
            var11 = (String) getCookieNameMethod.invoke(assetManager, var10 + 1);
            if (!var11.equals(patchResPath)) {
                var8.add(var11);
            }
        }

        //销毁重新创建
        destroyMethod.invoke(assetManager);
        stringBlocksField.set(assetManager, (Object) null);

        try {
            ReflectUtil.getDeclaredMethod(this.a, "init").invoke(assetManager);
        } catch (Exception var12) {
            ReflectUtil.getDeclaredMethod(this.a, "init", Boolean.TYPE).
                    invoke(assetManager, false);
        }

        Iterator var13 = var8.iterator();

        while (var13.hasNext()) {
            var11 = (String) var13.next();
            com.taobao.sophix.e.d.a("ResourceManager", "add asset " + var11, new Object[0]);
            addAssetPathMethod.invoke(assetManager, var11);
        }

    }

    private Method a(String var1, Class... var2) throws PatchException {
        try {
            return ReflectUtil.getDeclaredMethod(this.a, var1, var2);
        } catch (NoSuchMethodException var4) {
            throw new PatchException(84, var4);
        }
    }

    private Field b(String var1) throws PatchException {
        try {
            return ReflectUtil.getDeclaredField(this.a, var1);
        } catch (Exception var3) {
            throw new PatchException(85, var3);
        }
    }
}
