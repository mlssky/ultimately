package com.xcleans.hotfix;

import android.annotation.SuppressLint;
import android.app.Application;

import com.taobao.sophix.e.i;
import com.taobao.sophix.listener.PatchLoadStatusListener;

import java.io.File;

public class GlobalCfg {
    public static volatile int a = 0;
    @SuppressLint({"StaticFieldLeak"})
    public static Application b;
    public static File c;
    public static File d;
    //Path so 目录
    public static File e;
    public static boolean f;
    public static PatchLoadStatusListener g;

    public static void a(int var0) {
        a = var0;
        i.b(b, "hpatch_version", a);
        com.taobao.sophix.e.d.c("GlobalProperty", "addPatch", new Object[]{"update patch version", a});
    }
}
