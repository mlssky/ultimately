//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.patch.dex.hot;

import com.xcleans.hotfix.manager.PatchException;
import com.xcleans.hotfix.util.FileUtils;
import com.xcleans.hotfix.util.ReflectUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import dalvik.system.DexFile;

/**
 *
 */
class HotClassLoader extends ClassLoader {
    private Method      mFindResourceMth;
    private Method      findLibraryMth;
    private ClassLoader mParent;
    private DexFile     mPatchDexFile;

    /**
     * @param parent
     * @throws PatchException
     */
    public HotClassLoader(ClassLoader parent) throws PatchException {
        super(parent);
        this.mParent = parent;
        Class clz = ClassLoader.class;
        try {
            this.findLibraryMth = ReflectUtil.getDeclaredMethod(clz, "findLibrary", new Class[]{String.class});
            this.mFindResourceMth = ReflectUtil.getDeclaredMethod(clz, "findResource", new Class[]{String.class});
        } catch (NoSuchMethodException var4) {
            throw new PatchException(84, var4);
        }
    }

    public void init(DexFile var1) {
        this.mPatchDexFile = var1;
    }

    @Override
    public Class<?> loadClass(String var1) throws ClassNotFoundException {

        //优先root clz
        Class clz = super.findLoadedClass(var1);
        if (clz != null && clz.getClassLoader() == this) {
            return clz;
        } else {
            Class patchClz = this.mPatchDexFile.loadClass(var1, this);
            if (patchClz != null) {
                com.taobao.sophix.e.d.b("HotClassLoader", "loadClass", new Object[]{"clazzPatch", var1});
                return patchClz;
            } else {
                return this.getParent().loadClass(var1);
            }
        }
    }

    @Override
    protected String findLibrary(String libname) {
        try {
            String libraryName = System.mapLibraryName(libname);
            File patchLibraryFile = new File(com.taobao.sophix.b.b.e, libraryName);
            //重定向
            if (!patchLibraryFile.exists()) {
                File var4 = new File((String) this.findLibraryMth.invoke(this.getParent(), libname));
                FileUtils.copy(var4, patchLibraryFile);
            }
            return patchLibraryFile.getAbsolutePath();
        } catch (Throwable var5) {
            com.taobao.sophix.e.d.b("HotClassLoader", "findLibrary", var5, new Object[0]);
            return null;
        }
    }

    @Override
    public URL findResource(String var1) {
        try {
            return (URL) this.mFindResourceMth.invoke(this.mParent, var1);
        } catch (Exception var3) {
            com.taobao.sophix.e.d.b("HotClassLoader", "findResource", var3, new Object[0]);
            return null;
        }
    }

    @Override
    public Package getPackage(String var1) {
        if (var1 != null && !var1.isEmpty()) {
            synchronized (this) {
                Package var3 = super.getPackage(var1);
                if (var3 == null) {
                    var3 = this.definePackage(var1, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", (URL) null);
                }
                return var3;
            }
        } else {
            return null;
        }
    }
}
