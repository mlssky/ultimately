//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcleans.hotfix;

import android.app.Application;

import com.xcleans.hotfix.manager.SophixManagerImpl;

public class SophixApplication extends Application {
    public SophixApplication() {
    }

    public void onCreate() {
        super.onCreate();
        ((SophixManagerImpl) SophixManager.getInstance()).a();
    }
}
