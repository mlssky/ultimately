package com.xcleans.hotfix.core;

/**
 *
 */
public class FixNative {

    /**
     * @param obj
     */
    public static native void clearPreVerified(Object obj);

    /**
     * @param str
     * @param obj
     * @return
     */
    public static native boolean clearResolvedCache(String str, Object obj);

    /**
     * @return
     */
    public static native boolean initHotNative();

    /**
     * @param str
     * @param strArr
     * @return
     */
    public static native boolean markKeptMethods(String str, String[] strArr);

    /**
     * 移除类在dex文件中
     *
     * @param str
     * @param str2
     * @param strArr
     * @return
     */
    public static native boolean removeClassesInBase(String str, String str2, String[] strArr);

    /**
     * 方法替换
     *
     * @param obj
     * @param obj2
     */
    public static native void replaceMethod(Object obj, Object obj2);

    static {
        System.loadLibrary("sophix");
    }
}