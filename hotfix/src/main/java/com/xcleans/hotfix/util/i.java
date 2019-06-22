//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class i {
    public static int a(Context var0, String var1, int var2) {
        if (var0 == null) {
            return var2;
        } else {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            return var3.getInt(var1, var2);
        }
    }

    public static void b(Context var0, String var1, int var2) {
        if (var0 != null) {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            Editor var4 = var3.edit();
            var4.putInt(var1, var2);
            boolean var5 = var4.commit();
            if (!var5) {
//                d.d("SharedPrefUtils", "putInt fail", new Object[]{"key", var1});
            }

        }
    }

    public static boolean a(Context var0, String var1, boolean var2) {
        if (var0 == null) {
            return var2;
        } else {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            return var3.getBoolean(var1, var2);
        }
    }

    public static void b(Context var0, String var1, boolean var2) {
        if (var0 != null) {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            Editor var4 = var3.edit();
            var4.putBoolean(var1, var2);
            boolean var5 = var4.commit();
            if (!var5) {
//                d.d("SharedPrefUtils", "putBoolean fail", new Object[]{"key", var1});
            }

        }
    }

    public static String a(Context var0, String var1, String var2) {
        if (var0 == null) {
            return var2;
        } else {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            return var3.getString(var1, var2);
        }
    }

    public static void b(Context var0, String var1, String var2) {
        if (var0 != null) {
            SharedPreferences var3 = var0.getSharedPreferences("sp_sophix", 0);
            Editor var4 = var3.edit();
            var4.putString(var1, var2);
            boolean var5 = var4.commit();
            if (!var5) {
//                d.d("SharedPrefUtils", "putString fail", new Object[]{"key", var1});
            }

        }
    }

    public static void a(Context var0, String var1) {
        if (var0 != null) {
            SharedPreferences var2 = var0.getSharedPreferences("sp_sophix", 0);
            Editor var3 = var2.edit();
            var3.remove(var1);
            boolean var4 = var3.commit();
            if (!var4) {
//                d.d("SharedPrefUtils", "remove fail", new Object[]{"key", var1});
            }

        }
    }

    public static void a(Context var0) {
        if (var0 != null) {
            SharedPreferences var1 = var0.getSharedPreferences("sp_sophix", 0);
            Editor var2 = var1.edit();
            var2.clear();
            boolean var3 = var2.commit();
            if (!var3) {
//                d.d("SharedPrefUtils", "clear fail", new Object[0]);
            }

        }
    }
}
