//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class g {
    public static Class a = a("android.app.ContextImpl");
    public static Class b = a("android.app.ActivityThread");

    private static Class a(String var0) {
        try {
            return Class.forName(var0);
        } catch (Exception var2) {
//            d.HotFixManager("ReflectUtil", "fail to reflect", var2, new Object[0]);
            return null;
        }
    }

    public static Method a(Class var0, String var1, Class... var2) throws NoSuchMethodException {
        Method var3 = var0.getDeclaredMethod(var1, var2);
        if (!var3.isAccessible()) {
            var3.setAccessible(true);
        }

        return var3;
    }

    public static Constructor a(Class var0, Class... var1) throws NoSuchMethodException {
        Constructor var2 = var0.getDeclaredConstructor(var1);
        if (!var2.isAccessible()) {
            var2.setAccessible(true);
        }

        return var2;
    }

    public static Field a(Class var0, String var1) throws NoSuchFieldException {
        Field var2 = var0.getDeclaredField(var1);
        if (!var2.isAccessible()) {
            var2.setAccessible(true);
        }

        return var2;
    }
}
