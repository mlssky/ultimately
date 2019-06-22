//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.core;

public class SophixNative {
    public static native boolean initHotNative();

    public static native void replaceMethod(Object var0, Object var1);

    public static native boolean removeClassesInBase(String var0, String var1, String[] var2);

    /**
     * 清除PreVerified
     * @param var0
     */
    public static native void clearPreVerified(Object var0);

    /**
     * clearResolvedCache
     * 不使用代理Application，使用代理替换Application时不需要这个方法的
     * @param var0
     * @param var1
     * @return
     */
    public static native boolean clearResolvedCache(String var0, Object var1);

    public static native boolean markKeptMethods(String var0, String[] var1);

    static {
        System.loadLibrary("sophix");
    }
}
