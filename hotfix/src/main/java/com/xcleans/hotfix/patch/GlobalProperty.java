package com.xcleans.hotfix.patch;

import android.annotation.SuppressLint;
import android.app.Application;

import com.taobao.sophix.e.i;
import com.xcleans.hotfix.listener.PatchLoadStatusListener;

import java.io.File;

/**
 * com.taobao.sophix.b.b
 */
public class GlobalProperty {
    public static volatile int                     a = 0;
    @SuppressLint({"StaticFieldLeak"})
    public static          Application             globalApp;//b
    public static          File                    c;
    public static          File                    d;
    public static          File                    e;
    //true:SophixApplication
    public static          boolean                 f;
    //g
    public static          PatchLoadStatusListener patchLoadStatusListener;

    public static void a(int var0) {
        a = var0;
        i.b(globalApp, "hpatch_version", a);
        com.taobao.sophix.e.d.c("GlobalProperty", "addPatch", new Object[]{"update patch version", a});
    }
}
