//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix.manager;

import com.taobao.sophix.b.b;
import com.taobao.sophix.e.d;
import com.taobao.sophix.e.i;
import java.lang.Thread.UncaughtExceptionHandler;

public class a implements UncaughtExceptionHandler {
    private volatile boolean a = false;
    private volatile boolean b = false;
    private volatile int c;
    private UncaughtExceptionHandler d;
    private static a e = new a();

    public static void a(boolean var0) {
        e.b = var0;
        if (var0) {
            Thread.setDefaultUncaughtExceptionHandler(e);
            com.taobao.sophix.e.d.b("CrashHandler", "set", new Object[]{"crash number", String.valueOf(e.c)});
        } else {
            Thread.setDefaultUncaughtExceptionHandler(e.d);
        }

    }

    public static boolean a() {
        return e.b;
    }

    public static boolean b() {
        return e.a;
    }

    private a() {
        this.c = i.a(com.taobao.sophix.b.b.b, "happ_crash_num", 0);
        this.d = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread var1, Throwable var2) {
        if (!(var2 instanceof OutOfMemoryError)) {
            this.a = true;
            ++this.c;
            i.b(com.taobao.sophix.b.b.b, "happ_crash_num", this.c);
            com.taobao.sophix.e.d.d("CrashHandler", "crash is found", new Object[]{"crashNum", this.c});
        }

        a(false);
        this.d.uncaughtException(var1, var2);
    }
}
