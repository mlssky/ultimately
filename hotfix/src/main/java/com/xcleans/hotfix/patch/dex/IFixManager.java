package com.xcleans.hotfix.patch.dex;

import com.xcleans.hotfix.manager.PatchException;

import java.io.File;

/**
 * Created by mengliwei on 2019-06-16.
 * 1.热修复
 * 2.冷启动修复
 * 3.资源修复
 * 4.so 修复
 *
 * @function:
 * @since 1.0.0
 */
public interface IFixManager {

    String DEX_BASENAME          = "classes.dex";
    String Patch_Merged_FILENAME = "sophix-merged.zip";

    /**
     * @param file
     * @return
     * @throws PatchException
     */
    boolean hotfix(File file) throws PatchException;
}
