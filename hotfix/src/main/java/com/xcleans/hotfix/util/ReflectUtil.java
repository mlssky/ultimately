//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Class a = forName("android.app.ContextImpl");
    public static Class b = forName("android.app.ActivityThread");

    /**
     * @param className
     * @return
     */
    public static Class forName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception var2) {
            return null;
        }
    }

    /**
     * @param cls
     * @param name
     * @param parameterTypes
     * @return
     */
    public static Method getDeclaredMethod(Class cls, String name, Class... parameterTypes) throws NoSuchMethodException {
        Method method = cls.getDeclaredMethod(name, parameterTypes);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    public static Constructor getDeclaredConstructor(Class cls, Class... parameterTypes) throws NoSuchMethodException {
        Constructor constructor = cls.getDeclaredConstructor(parameterTypes);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    /**
     * 遍历所有类
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws Exception
     */
    public static Field getDeclaredField(Object obj, String fieldName) throws Exception {
        Class cls = obj.getClass();
        while (cls != null) {
            try {
                Field field = cls.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException var5) {
                cls = cls.getSuperclass();
            }
        }
        throw new Exception("Field " + fieldName + " not found in " + obj.getClass());
    }

    public static Field getDeclaredField(Class clz, String var1) throws NoSuchFieldException {
        Field field = clz.getDeclaredField(var1);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field;
    }
}
