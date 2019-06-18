package com.xcleans.hotfix;

import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.taobao.sophix.b.b;
import com.taobao.sophix.e.d;
import com.taobao.sophix.e.g;
import com.xcleans.hotfix.util.ReflectUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by mengliwei on 2019-06-16.
 *
 * @function:
 * @since 1.0.0
 */
public class NativeLibManager {

    private static String[] primaryCpuAbis;

    public NativeLibManager() {
        if (primaryCpuAbis == null) {
            try {
                PackageManager var1 = b.b.getPackageManager();
                if (var1 != null) {
                    ApplicationInfo var2 = var1.getApplicationInfo(b.b.getBaseContext().getPackageName(), 0);
                    if (var2 != null) {
                        primaryCpuAbis = Build.VERSION.SDK_INT >= 21 ? new String[]{(String) g.a(ApplicationInfo.class, "primaryCpuAbi").get(var2)} : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
                    }
                }
                d.b("NativeLibManager", "init", new Object[]{"primaryCpuAbis", Arrays.toString(primaryCpuAbis)});
            } catch (Throwable var3) {
                d.b("NativeLibManager", "init", var3, new Object[0]);
            }

        }
    }

    /**
     * @param libPathJsonCfg
     * @param pathSoListZipFile
     * @param libDir
     */
    public void a(String libPathJsonCfg, ZipFile pathSoListZipFile, File libDir) throws PatchException {
        d.b("NativeLibManager", "unZipLibFile", new Object[]{"libPath", libPathJsonCfg, "libDir", libDir});
        try {
            Map<String, String> var4 = parseLibSoJsonCfg(libPathJsonCfg);

            Enumeration var5 = pathSoListZipFile.entries();

            String fname;
            boolean var10;
            do {
                ZipEntry zipEntry;
                do {
                    if (!var5.hasMoreElements()) {
                        return;
                    }
                    zipEntry = (ZipEntry) var5.nextElement();
                    fname = zipEntry.getName();//
                } while (!checkIsNeedExtra(fname, libDir, var4));

                String baseFname = fname.substring(fname.lastIndexOf(File.separator) + 1);
                d.a("NativeLibManager", "unZipLibFile", new Object[]{"entryName", fname, "soName", baseFname});
                int var9 = 0;
                var10 = false;

                while (var9 < 3 && !var10) {
                    ++var9;
                    BufferedOutputStream var11 = null;
                    BufferedInputStream var12 = null;

                    try {
                        File var13 = new File(libDir, baseFname);
                        var11 = new BufferedOutputStream(new FileOutputStream(var13));
                        var12 = new BufferedInputStream(pathSoListZipFile.getInputStream(zipEntry));
                        byte[] var14 = new byte[1024];

                        for (int var15 = var12.read(var14); var15 != -1; var15 = var12.read(var14)) {
                            var11.write(var14, 0, var15);
                        }

                        var10 = true;
                    } catch (IOException var21) {
                        d.a("NativeLibManager", "unZipLibFile", var21, new Object[]{"entryName", fname, "libName", baseFname, "numAttempts", var9});
                    } finally {
                        com.taobao.sophix.e.b.a(var11);
                        com.taobao.sophix.e.b.a(var12);
                    }
                }
            } while (var10);

            throw new com.taobao.sophix.a.b(135, "unZipLibFile entryName:" + fname);
        } catch (PatchException var23) {
            throw var23;
        } catch (Throwable var24) {
            throw new PatchException(131, var24);
        }
    }

    /**
     * @param cfg
     * @return <soName,路径>
     * @throws PatchException
     */
    private static Map<String, String> parseLibSoJsonCfg(String cfg) throws PatchException {
        if (primaryCpuAbis != null && primaryCpuAbis.length != 0) {
            try {
                HashMap var1 = new HashMap();
                JSONObject cfgObj = new JSONObject(cfg);
                Iterator keys = cfgObj.keys();

                while (keys.hasNext()) {

                    //so库名称
                    String key = (String) keys.next();

                    JSONArray values = cfgObj.optJSONArray(key);

                    for (int i = 0, n = primaryCpuAbis.length; i < n; ++i) {
                        String cpuAbi = primaryCpuAbis[i];

                        for (int j = 0; j < values.length(); ++j) {
                            String var11 = values.optString(j);
                            if (cpuAbi.equals(var11)) {
                                //类型名
                                var1.put(key, var11);
                                continue;
                            }
                        }
                    }
                }
                d.a("NativeLibManager", "getLibPatchMap", new Object[]{"libPatchMap", var1});
                return var1;
            } catch (JSONException var12) {
                throw new PatchException(133, var12);
            }
        } else {
            d.d("NativeLibManager", "getLibPatchMap", new Object[]{"primaryCpuAbis is null"});
            throw new PatchException(132, "primaryCpuAbis is null");
        }
    }

    /**
     * @param fileName
     * @param libDir
     * @param var2     <so文件名，so类型>
     * @return
     */
    private static boolean checkIsNeedExtra(String fileName, File libDir, Map<String, String> var2) {
        if (!TextUtils.isEmpty(fileName)) {
            Iterator var3 = var2.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry var4 = (Map.Entry) var3.next();
                //类型/so名称
                String var5 = (String) var4.getValue() + File.separator + System.mapLibraryName((String) var4.getKey());
                File var6 = new File(libDir, var5);
                if (var6.exists()) {
                    return false;
                }
                if (fileName.endsWith(var5)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 注入Patch so文件
     *
     * @param cfgJson
     * @param libDir
     * @return
     * @throws PatchException
     */
    public boolean injectSoPatchFile(String cfgJson, File libDir) throws PatchException {
        d.a("NativeLibManager", "patch", new Object[]{"libDir", libDir.getAbsolutePath()});
        boolean canInjectPatchSo = true;

        File[] listFiles = libDir.listFiles();

        if (listFiles != null && listFiles.length != 0) {
            //<soname,type>
            Map<String, String> soCfgs = parseLibSoJsonCfg(cfgJson);
            if (listFiles.length == soCfgs.keySet().size()) {
                Iterator soNamesIterator = soCfgs.keySet().iterator();
                while (soNamesIterator.hasNext()) {
                    String var7 = (String) soNamesIterator.next();
                    boolean var8 = true;

                    File[] var9 = listFiles;
                    int var10 = listFiles.length;
                    for (int var11 = 0; var11 < var10; ++var11) {
                        File var12 = var9[var11];
                        if (var12.getName().equals(System.mapLibraryName(var7))) {
                            var8 = false;
                            break;
                        }
                    }
                    if (var8) {
                        canInjectPatchSo = false;
                        break;
                    }
                }
            } else {
                canInjectPatchSo = false;
            }
        } else {
            canInjectPatchSo = false;
        }

        if (!canInjectPatchSo) {
            throw new PatchException(134, "lost some libs");
        } else {
            try {
                ClassLoader classLoader = this.getClass().getClassLoader();
                d.a("NativeLibManager", "patch", new Object[]{"inject before classLoader", classLoader});
                //  BaseDexClassLoader.pathList(DexPathList);
                Field pathListField = ReflectUtil.getDeclaredField((Object) classLoader, (String) "pathList");
                Object pathListObj = pathListField.get(classLoader);

                if (!TextUtils.isEmpty(libDir.getAbsolutePath()) && libDir.exists()) {
                    Object nativeLibraryElementObj;
                    if (Build.VERSION.SDK_INT >= 26) {
                        nativeLibraryElementObj = this.newNativeLibraryElement(libDir);
                        this.injectPatchNativeLibraryPathElements(pathListObj, "nativeLibraryPathElements", nativeLibraryElementObj);
                        this.injectNativeLibraryDirectories(pathListObj, "nativeLibraryDirectories", libDir);
                    } else if (Build.VERSION.SDK_INT >= 23) {
                        nativeLibraryElementObj = this.newElement(libDir);
                        this.injectPatchNativeLibraryPathElements(pathListObj, "nativeLibraryPathElements", nativeLibraryElementObj);
                        this.injectNativeLibraryDirectories(pathListObj, "nativeLibraryDirectories", libDir);
                    } else {
                        this.injectPatchNativeLibraryPathElements((Object) pathListObj, (String) "nativeLibraryDirectories", (Object) libDir);
                    }
                }

                d.a("NativeLibManager", "patch", new Object[]{"inject after classLoader", classLoader});
                return true;
            } catch (Throwable var13) {
                throw new PatchException(136, var13);
            }
        }
    }

    @TargetApi(18)
    public static void a() throws PatchException {
        try {
            ClassLoader var0 = NativeLibManager.class.getClassLoader();
            ClassLoader var1 = GlobalCfg.b.getClassLoader();
            if (var0 instanceof BaseDexClassLoader && var1.getClass().getName().startsWith("com.taobao.sophix")) {
                //已经ji
                String var2 = (String) ReflectUtil.getDeclaredMethod(BaseDexClassLoader.class, "getLdLibraryPath", new Class[0]).
                        invoke(var0);
                if (Build.VERSION.SDK_INT >= 27) {
//                    private static native String createClassloaderNamespace(
//                    ClassLoader classLoader,
//                    int targetSdkVersion,
//                    String librarySearchPath,
//                    String libraryPermittedPath,
//                    boolean isNamespaceShared,
//                    boolean isForVendor)
                    ReflectUtil.getDeclaredMethod(Class.forName("com.android.internal.os.ClassLoaderFactory"),
                            "createClassloaderNamespace",
                            new Class[]{ClassLoader.class, Integer.TYPE, String.class, String.class, Boolean.TYPE, Boolean.TYPE}).
                            invoke((Object) null, var1/**classLoader*/, 14, var2, null, false, false);
                } else if (Build.VERSION.SDK_INT >= 24) {
                    ReflectUtil.getDeclaredMethod(Class.forName("com.android.internal.os.PathClassLoaderFactory"), "createClassloaderNamespace", new Class[]{ClassLoader.class, Integer.TYPE, String.class, String.class, Boolean.TYPE}).
                            invoke((Object) null, var1, 14, var2, null, false);
                } else {
                    d.a("NativeLibManager", "classloader has processed it", new Object[0]);
                }
            } else {
                d.a("NativeLibManager", "no need to process ld path", new Object[0]);
            }

        } catch (Throwable var3) {
            throw new PatchException(137, var3);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 注入Patch so 的lib 目录到
     *
     * @param pathListObj
     * @param nativeLibraryDirectoriesFieldName
     * @param libDir
     * @throws Exception
     */
    private void injectNativeLibraryDirectories(Object pathListObj, String nativeLibraryDirectoriesFieldName, Object libDir) throws Exception {
        Field nativeLibraryDirectoriesField = ReflectUtil.getDeclaredField(pathListObj, nativeLibraryDirectoriesFieldName);
        List nativeLibraryDirectories = (List) nativeLibraryDirectoriesField.get(pathListObj);
        nativeLibraryDirectories.add(0, libDir);
    }

    /**
     * @param pathListObj
     * @param nativeLibraryPathElementsFieldName
     * @param nativeLibraryElementObj
     * @throws Exception
     */
    private void injectPatchNativeLibraryPathElements(Object pathListObj, String nativeLibraryPathElementsFieldName, Object nativeLibraryElementObj) throws Exception {
        Field oldNativeLibraryPathElementsField = ReflectUtil.getDeclaredField(pathListObj, nativeLibraryPathElementsFieldName);
        Object[] oldNativeLibraryPathElements = (Object[]) ((Object[]) oldNativeLibraryPathElementsField.get(pathListObj));

        Object[] newNativeLibraryPathElements = (Object[]) ((Object[]) Array.newInstance(oldNativeLibraryPathElements.getClass().getComponentType(), oldNativeLibraryPathElements.length + 1));
        newNativeLibraryPathElements[0] = nativeLibraryElementObj;
        System.arraycopy(oldNativeLibraryPathElements, 0, newNativeLibraryPathElements, 1, oldNativeLibraryPathElements.length);

        oldNativeLibraryPathElementsField.set(pathListObj, newNativeLibraryPathElements);
    }

    /**
     * @param var1
     * @return
     * @throws Exception
     */
    private Object newElement(File var1) throws Exception {
        Constructor var2 = ReflectUtil.getDeclaredConstructor(Class.forName("dalvik.system.DexPathList$Element"), new Class[]{File.class, Boolean.TYPE, File.class, DexFile.class});
        if (var2 != null) {
            return var2.newInstance(var1, true, null, null);
        } else {
            throw new Exception("make nativeElement fail no such constructor");
        }
    }

    /**
     * @param var1
     * @return
     */
    private Object newNativeLibraryElement(File var1) throws Exception {
        //NativeLibraryElement
        Constructor var2 = ReflectUtil.getDeclaredConstructor(Class.forName("dalvik.system.DexPathList$NativeLibraryElement"), new Class[]{File.class});
        if (var2 != null) {
            var2.setAccessible(true);
            return var2.newInstance(var1);
        } else {
            throw new Exception("make nativeElement fail no such constructor");
        }
    }

}
