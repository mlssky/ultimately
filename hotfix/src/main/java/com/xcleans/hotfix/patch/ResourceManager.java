package com.xcleans.hotfix.patch;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.ArrayMap;

import com.taobao.sophix.e.g;
import com.xcleans.hotfix.GlobalCfg;
import com.xcleans.hotfix.manager.PatchException;
import com.xcleans.hotfix.patch.dex.IFixManager;
import com.xcleans.hotfix.util.ReflectUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 资源修复
 * TODO 工具的解决
 * 需要分析比较resource.arsc 文件生成新的资源包
 * 相关======
 * android4.4(19) 引入ResourcesManager
 * android4.4(20):addAssetPath不会触发资源的加载,必须要重新创建AssetManager才行,
 * 需要解决如何不重新创建AssetManager,把Patch资源插入到主APK资源中
 * android6.0：
 * android7.0 ResourcesManager引入资源的缓存，资源缓存自动重新创建时会只包含原APK的资源
 * 修复资源时要解决如何把Patch资源添加到系统，及时缓存重建也能生效的问题
 *
 *
 */
public class ResourceManager implements IFixManager {

    private Class mAssetManagerClz;

    public ResourceManager() throws PatchException {
        try {
            this.mAssetManagerClz = Class.forName("android.content.res.AssetManager");
        } catch (ClassNotFoundException var2) {
            throw new PatchException(82, var2);
        }
    }

    /**
     * @param patchResFile
     * @return
     * @throws PatchException
     */
    @Override
    public boolean hotfix(File patchResFile) throws PatchException {
        return injectPatchResource(patchResFile);
    }

    /**
     * @param patchRes
     * @return
     * @throws PatchException
     */
    public boolean injectPatchResource(File patchRes) throws PatchException {

        String patchResPath = patchRes.getPath();

        //android 7.0 ResourcesManager引入资源的缓存,修复资源时要解决如何把Patch资源添加到系统，即使缓存重建也能生效的问题
        //如果这一步失败的话，就直接结束
        //缓存的资源是一个WeakRefrence 类型存在被系统回收重新创建的过程,在重新创建的过程中一定要把Patch的资源加载
        // mSplitResDirs 数组中，这样重新创建的AssetManager也会包含Patch Resource,否则必然会出现概率性的奔溃
        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                this.injectPatchToApkResourceDir(patchResPath);
            } catch (Exception var11) {
                throw new PatchException(121, var11);
            }
        }

        AssetManager assets = GlobalCfg.b.getAssets();

        synchronized (assets) {
            try {
                Method addAssetPathMethod = ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "addAssetPath", String.class);
                //KK 版本及以下addAssetPath不会触发资源的加载,必须要重新创建AssetManager才行
                if (VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {

                    this.reCreateAssetManager(assets, addAssetPathMethod, patchResPath);

                    addAssetPathMethod.invoke(assets, patchResPath);

                    //已经加载的过得所有资源包StringPool
                    ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "ensureStringBlocks").invoke(assets);
                } else {
                    addAssetPathMethod.invoke(assets, patchResPath);
                }
            } catch (Exception var9) {
                throw new PatchException(123, var9);
            }

            try {
                this.updateResConfiguration();
            } catch (Exception var8) {
                throw new PatchException(122, var8);
            }

            return true;
        }
    }

    /**
     * 在缓存资源重新创建时会把这个目录的资源添加到
     *
     * @param patchResPath
     * @throws
     */
    @TargetApi(24)
    private void injectPatchToApkResourceDir(String patchResPath) throws Exception {

        Object loadApk = ReflectUtil.getDeclaredField(g.a, "mPackageInfo").get(GlobalCfg.b.getBaseContext());

        Field splitResDirs = ReflectUtil.getDeclaredField(loadApk.getClass(), "mSplitResDirs");

        String[] oldSplitResDirs = (String[]) splitResDirs.get(loadApk);

        splitResDirs.set(loadApk, this.appendArrays(oldSplitResDirs, patchResPath));
    }

    /**
     * 连接到数组后面
     *
     * @param srcStrArr
     * @param appendStr
     * @return
     */
    private String[] appendArrays(String[] srcStrArr, String appendStr) {
        String[] resArr = new String[1 + (srcStrArr != null ? srcStrArr.length : 0)];
        if (srcStrArr != null) {
            System.arraycopy(srcStrArr, 0, resArr, 0, srcStrArr.length);
        }
        resArr[resArr.length - 1] = appendStr;
        return resArr;
    }

    /**
     * 给析构重新创建的资源重新赋值Configuration相关信息
     *
     * @throws
     */
    private void updateResConfiguration() throws Exception {
        Collection activeResourcesCl;
        Class resMgrClz;
        Object object;

        //资源移到了ResourcesManager统一管理,
        //原先老的方案
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            resMgrClz = Class.forName("android.app.ResourcesManager");
            object = ReflectUtil.getDeclaredMethod(resMgrClz, "getInstance", new Class[0]).invoke((Object) null);
            try {
                ArrayMap var4 = (ArrayMap) ReflectUtil.getDeclaredField(resMgrClz, "mActiveResources").get(object);
                activeResourcesCl = var4.values();
            } catch (Exception var5) {
                activeResourcesCl = (Collection) ReflectUtil.getDeclaredField(resMgrClz, "mResourceReferences").get(object);
            }
        } else {
            object = ReflectUtil.getDeclaredMethod(g.b, "currentActivityThread", new Class[0]).invoke((Object) null);
            if (object == null) {
                //Application#
                // public LoadedApk mLoadedApk;
                Object loadedApk = ReflectUtil.getDeclaredField(Application.class, "mLoadedApk").get(com.taobao.sophix.b.b.b);
                object = ReflectUtil.getDeclaredField(loadedApk.getClass(), "mActivityThread").get(loadedApk);
            }
            HashMap var9 = (HashMap) ReflectUtil.getDeclaredField(g.b, "mActiveResources").get(object);
            activeResourcesCl = var9.values();
        }

        Iterator iterator = activeResourcesCl.iterator();
        while (iterator.hasNext()) {
            WeakReference reference = (WeakReference) iterator.next();
            Resources resources = (Resources) reference.get();
            if (resources != null) {
                //TODO update successfully
                resources.updateConfiguration(resources.getConfiguration(), resources.getDisplayMetrics());
            }
        }

    }


    /**
     * 重新析构AssetManager
     * 避免重新创建
     *
     * @param assetManager
     * @param addAssetPathMethod addAssetPathMehots
     * @param patchResPath       PatchResPath
     * @throws
     */
    private void reCreateAssetManager(AssetManager assetManager, Method addAssetPathMethod, String patchResPath) throws Exception {

        Method destroyMethod = ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "destroy");
        Method getStringBlockCountMethod = ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "getStringBlockCount");
        Method getCookieNameMethod = ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "getCookieName", Integer.TYPE);
        Field stringBlocksField = ReflectUtil.getDeclaredField(this.mAssetManagerClz, "mStringBlocks");

        //已经加载过得资源
        ArrayList<String> loadedPaths = new ArrayList();
        int var9 = (Integer) getStringBlockCountMethod.invoke(assetManager);
        String var11;
        for (int i = 0; i < var9; ++i) {
            var11 = (String) getCookieNameMethod.invoke(assetManager, i + 1);
            if (!var11.equals(patchResPath)) {
                loadedPaths.add(var11);
            }
        }

        //需要销毁Native层已经的创建的Resource对象
        //销毁
        destroyMethod.invoke(assetManager);
        //置空StringBlocks
        stringBlocksField.set(assetManager, (Object) null);

        //重新初始AssertManager
        try {
            ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "init").invoke(assetManager);
        } catch (Exception var12) {
            ReflectUtil.getDeclaredMethod(this.mAssetManagerClz, "init", Boolean.TYPE).
                    invoke(assetManager, false);
        }

        for (String path : loadedPaths) {
            //add asset
//            com.taobao.sophix.ERROR.d.a("ResourceManager", "add asset " + path, new Object[0]);
            addAssetPathMethod.invoke(assetManager, path);
        }
    }
}
