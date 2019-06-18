package com.xcleans.hotfix;

import com.xcleans.hotfix.util.FileUtils;
import com.xcleans.hotfix.util.ReflectUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import dalvik.system.DexFile;

/**
 *
 */
class PatchClassLoader extends ClassLoader {

    private Method mFindResourceMethod;
    private Method mFindLibraryMethod;
    private ClassLoader mParentClassLoader;
    private DexFile mDexFile;

    /**
     * @param parent
     * @throws PatchException
     */
    public PatchClassLoader(ClassLoader parent) {
        super(parent);
        this.mParentClassLoader = parent;
        Class cls = ClassLoader.class;
        try {
            this.mFindLibraryMethod = ReflectUtil.getDeclaredMethod(cls, "findLibrary", String.class);
            this.mFindResourceMethod = ReflectUtil.getDeclaredMethod(cls, "findResource", String.class);
        } catch (Throwable e) {
            throw new PatchException(84, e);
        }
    }

    public void init(DexFile dexFile) {
        this.mDexFile = dexFile;
    }

    @Override
    public Class<?> loadClass(String str) throws ClassNotFoundException {

        // First, check if the class has already been loaded
        Class<?> findLoadedClass = findLoadedClass(str);
        if (findLoadedClass != null && findLoadedClass.getClassLoader() == this) {
            return findLoadedClass;
        }
        findLoadedClass = this.mDexFile.loadClass(str, this);
        if (findLoadedClass == null) {
            return getParent().loadClass(str);
        }

        return findLoadedClass;
    }

    @Override
    protected String findLibrary(String libname) {
        try {
            File file = new File(GlobalCfg.e, "lib" + libname + ".so");
            if (!file.exists()) {
                FileUtils.copy(new File((String) this.mFindLibraryMethod.invoke(this.mParentClassLoader, new Object[]{libname})),
                        file);
            }
            return file.getAbsolutePath();
        } catch (Throwable th) {
            return null;
        }
    }

    @Override
    public URL findResource(String str) {
        try {
            return (URL) this.mFindResourceMethod.invoke(this.mParentClassLoader, new Object[]{str});
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Package getPackage(String str) {
        Package packageR = null;
        if (!(str == null || str.isEmpty())) {
            synchronized (this) {
                packageR = super.getPackage(str);
                if (packageR == null) {
                    packageR = definePackage(str, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", null);
                }
            }
        }
        return packageR;
    }
}