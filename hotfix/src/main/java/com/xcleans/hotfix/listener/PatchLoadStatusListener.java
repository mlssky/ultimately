package com.xcleans.hotfix.listener;

public interface PatchLoadStatusListener {
    /**
     *
     * @param mode
     * @param code
     * @param info
     * @param handlePatchVersion  0:无 -1:本地补丁 其它:后台补丁
     */
    void onLoad(final int mode, final int code, final String info, final int handlePatchVersion);
}
