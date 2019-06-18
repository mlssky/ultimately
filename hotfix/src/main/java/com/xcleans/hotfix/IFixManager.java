package com.xcleans.hotfix;

import java.io.File;

/**
 * Created by mengliwei on 2019-06-16.
 * 1.热修复
 * 2.冷启动修复
 *
 * @function:
 * @since 1.0.0
 */
public interface IFixManager {

    /**
     * @param file
     * @return
     * @throws PatchException
     */
    boolean hotfix(File file) throws PatchException;
}
