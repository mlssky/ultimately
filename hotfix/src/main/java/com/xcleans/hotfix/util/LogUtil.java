//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.util;

import android.text.TextUtils;
import android.util.Log;

public class LogUtil {

    private static boolean  a = true;
    private static LogLevel b;

    public static void a(boolean var0) {
        a = var0;
    }

    public static void a(LogLevel var0) {
        if (var0 != null) {
            b = var0;
            if (a) {
                c("LogLevel", "set", "value", var0);
            }
        }

    }

    public static int a(String var0, String var1, Object... var2) {
        return a && b(LogLevel.VERBOSE) ? Log.v(a(var0), a(var1, var2)) : 0;
    }

    public static int b(String var0, String var1, Object... var2) {
        return a && b(LogLevel.DEBUG) ? Log.d(a(var0), a(var1, var2)) : 0;
    }

    public static int c(String var0, String var1, Object... var2) {
        return a && b(LogLevel.INFO) ? Log.i(a(var0), a(var1, var2)) : 0;
    }

    public static int d(String var0, String var1, Object... var2) {
        return a(var0, var1, (Throwable) null, var2);
    }

    public static int a(String var0, String var1, Throwable var2, Object... var3) {
        return a && b(LogLevel.WARN) ? Log.w(a(var0), a(var1, var3), var2) : 0;
    }

    public static int e(String var0, String var1, Object... var2) {
        return b(var0, var1, (Throwable) null, var2);
    }

    public static int b(String var0, String var1, Throwable var2, Object... var3) {
        return a && b(LogLevel.ERROR) ? Log.e(a(var0), a(var1, var3), var2) : 0;
    }

    private static boolean b(LogLevel var0) {
        return var0.ordinal() >= b.ordinal();
    }

    private static String a(String var0) {
        return !TextUtils.isEmpty(var0) ? "Sophix." + var0 : var0;
    }

    private static String a(String var0, Object... var1) {
        if (var0 == null && var1 == null) {
            return "";
        } else {
            StringBuilder var2 = new StringBuilder();
            if (var0 != null) {
                var2.append(" ").append(var0);
            }

            if (var1 != null) {
                int var3;
                for (var3 = 0; var3 + 1 < var1.length; ++var3) {
                    var2.append(" ");
                    Object var10001 = var1[var3];
                    ++var3;
                    var2.append(a(var10001, var1[var3]));
                }

                if (var3 == var1.length - 1) {
                    var2.append(" ");
                    var2.append(var1[var3]);
                }
            }

            return var2.toString();
        }
    }

    private static String a(Object var0, Object var1) {
        return (var0 == null ? "" : var0) + ": " + (var1 == null ? "" : var1);
    }

    static {
        b = LogLevel.INFO;
    }

    public static enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR;
    }
}
