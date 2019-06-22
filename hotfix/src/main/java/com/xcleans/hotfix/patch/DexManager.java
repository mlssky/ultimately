package com.xcleans.hotfix.patch;

import com.taobao.sophix.c.c;
import com.taobao.sophix.e.d;
import com.taobao.sophix.e.i;
import com.xcleans.hotfix.listener.PatchLoadStatusListener;
import com.xcleans.hotfix.manager.PatchException;
import com.xcleans.hotfix.patch.dex.IFixManager;
import com.xcleans.hotfix.patch.dex.cold.ColdDexManager;
import com.xcleans.hotfix.patch.dex.hot.HotFixManager;

import java.io.File;

public class DexManager {

    /**
     * @param patchFile
     * @param hotPatch
     * @param statusListener
     * @param var4
     * @return
     * @throws PatchException
     */
    public boolean a(File patchFile, boolean hotPatch, PatchLoadStatusListener statusListener, c var4) throws PatchException {
        d.b("DexManager", "patch", new Object[]{"supportHotfix", hotPatch});
        IFixManager fixManager;
        if (hotPatch) {
            fixManager = new HotFixManager();
        } else {
            fixManager = new ColdDexManager(statusListener, var4);
        }
        //cfg save
        i.b(com.taobao.sophix.b.b.b, "happ_ishotfix", hotPatch);
        return fixManager.hotfix(patchFile);
    }
}
