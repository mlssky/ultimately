package com.xcleans.hotfix;//package com.xcleans.hotfix;

import android.app.Application;

import com.xcleans.hotfix.listener.PatchLoadStatusListener;
import com.xcleans.hotfix.manager.SophixManagerImpl;

public abstract class SophixManager {
    public static final String VERSION = "3.2.6";

    public static SophixManager getInstance() {
        return SophixManagerImpl.a;
    }

    /**
     * 该方法主要做些必要的初始化工作以及如果本地有补丁的话会加载补丁, 但不会自动请求补丁。因此需要自行调用queryAndLoadNewPatch方法拉取补丁。
     * 这个方法调用需要尽可能的早, 必须在Application的attachBaseContext方法的最前面调用（在super.attachBaseContext之后，
     * 如果有Multidex，也需要在Multidex.install之后）,
     * initialize()方法调用之前你需要先调用如下几个方法进行一些必要的参数设置
     */
    public abstract void initialize();

    public abstract SophixManager setContext(Application application);

    /**
     * 设置应用版本号
     *
     * @param appVersion
     * @return
     */
    public abstract SophixManager setAppVersion(String appVersion);

    public abstract SophixManager setPatchLoadStatusStub(PatchLoadStatusListener patchLoadStatusStub);

    /**
     * 输出全部级别log
     *
     * @return
     */
    public abstract SophixManager setEnableFullLog();

    public abstract SophixManager setPreLoadedClass(Class clz);

    /**
     * @param idSecret
     * @param appSecret
     * @param rsaSecret
     * @return
     */
    public abstract SophixManager setSecretMetaData(String idSecret, String appSecret, String rsaSecret);

    /**
     * 用户自定义aes秘钥, 会对补丁包采用对称加密
     *
     * @param var1
     * @return
     */
    public abstract SophixManager setAesKey(String var1);

    /**
     * isEnabled默认为false, 是否调试模式, 调试模式下会输出日志以及不进行补丁签名校验.
     * 线下调试此参数可以设置为true, 查看日志过滤TAG:Sophix
     *
     * @param var1
     * @return
     */
    public abstract SophixManager setEnableDebug(boolean var1);

    /**
     * 设置不支持的版本信息
     *
     * @param modelName     为该机型上Build modelName为该机型上Build.MODEL的值
     * @param sdkVersionInt Build.VERSION.SDK_INT 若设为0，则对应该机型所有安卓版本
     * @return
     */
    public abstract SophixManager setUnsupportedModel(String modelName, int sdkVersionInt);

    public abstract SophixManager setProcessSpecialClass(Class var1);

    public abstract SophixManager setUsingEnhance();

    public abstract SophixManager setHost(String var1, boolean var2);

    /**
     * 查询服务器是否有新的可用补丁
     */
    public abstract void queryAndLoadNewPatch();

    public abstract Object getPatchStateInfo();

    /**
     * 清空本地补丁，并且不再拉取被清空的版本的补丁
     */
    public abstract void cleanPatches();

    /**
     * 可以在PatchLoadStatusListener监听到CODE_LOAD_RELAUNCH后在合适的时机，调用此方法杀死进程
     */
    public abstract void killProcessSafely();
}
