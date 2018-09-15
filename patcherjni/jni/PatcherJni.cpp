//
// Created by NetEase on 16/6/6.
//

#include <stdlib.h>
#include "jni.h"

#include "bspatch.h"

#include "JNIHelper.h"

#define TAG "CandyWebCache"

static jboolean CourgettePatcher_nativeApplyPatch(JNIEnv *env, jclass, jstring javaOldFilePath,
                                                  jstring javaPatchFilePath,
                                                  jstring javaOutFilePath) {
    return JNI_TRUE;
}

static JNINativeMethod gCourgettePatcherMethods[] = {
        NATIVE_METHOD(CourgettePatcher, nativeApplyPatch,
                      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"),
};


/**
 *
 * @param env               默认虚拟机传递过来
 * @param jcls              默认虚拟机传递过来
 * ========================mapping to  java native define params
 * @param javaOldFilePath   输入文件路径
 * @param javaPatchFilePath patch file path
 * @param javaOutFilePath
 * @return
 */
static jboolean BsdiffPatcher_nativeApplyPatch(JNIEnv *env, jclass jcls, jstring javaOldFilePath,
                                               jstring javaPatchFilePath, jstring javaOutFilePath) {


    //local reference
//    jclass stringCls = env->FindClass("java/lang/String");
//    env->DeleteLocalRef(stringCls);
    //[jbyte
//    jobject defaultCls = getInstance(env, stringCls);
//    byte bytes[]



    const char *oldFilePath = env->GetStringUTFChars(javaOldFilePath, NULL);
    const char *patchFilePath = env->GetStringUTFChars(javaPatchFilePath, NULL);
    const char *outFilePath = env->GetStringUTFChars(javaOutFilePath, NULL);

//    env->GetObjectArrayElement()；

    jobjectArray a;


    char **argv = (char **) malloc(sizeof(char *) * 5);
    argv[0] = const_cast<char *>("bspatch");
    argv[1] = const_cast<char *>(oldFilePath);
    argv[2] = const_cast<char *>(outFilePath);
    argv[3] = const_cast<char *>(patchFilePath);
    argv[4] = NULL;

    int ret = bspatch_main(4, argv);
    free(argv);

    env->ReleaseStringUTFChars(javaOutFilePath, outFilePath);
    env->ReleaseStringUTFChars(javaPatchFilePath, patchFilePath);
    env->ReleaseStringUTFChars(javaOldFilePath, oldFilePath);

    if (ret == 0) {
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

static JNINativeMethod gBsdiffPatcherMethods[] = {
        NATIVE_METHOD(BsdiffPatcher, nativeApplyPatch,
                      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"),
};

/**
 * register java native method
 *
 * @param env
 * @param classPathName   mapping to java class
 * @param nativeMethods   native method define
 * @param nMethods
 * @return
 */
static int jniRegisterNativeMethods(JNIEnv *env,
                                    const char *classPathName,
                                    JNINativeMethod *nativeMethods,
                                    jint nMethods) {
    jclass clazz = env->FindClass(classPathName);
    if (clazz == NULL) {
        LOGW("Native registration unable to find class '%s'", classPathName);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, nativeMethods, nMethods) < 0) {
        LOGW("RegisterNatives failed for '%s'", classPathName);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

void register_com_netease_hearttouch_candywebcache_cachemanager_CourgettePatcher(JNIEnv *env) {
    jniRegisterNativeMethods(env,
                             "com/netease/hearttouch/candywebcache/cachemanager/CourgettePatcher",
                             gCourgettePatcherMethods, NELEM(gCourgettePatcherMethods));
}

/**
 *
 * @param env The JNIEnv provides most of the JNI functions.
 * Your native functions all receive a JNIEnv as the first argument
 */
void register_com_netease_hearttouch_candywebcache_cachemanager_BsdiffPatcher(JNIEnv *env) {
    jniRegisterNativeMethods(env, "com/netease/hearttouch/candywebcache/cachemanager/BsdiffPatcher",
                             gBsdiffPatcherMethods, NELEM(gBsdiffPatcherMethods));
}

/**
 * DalvikVM calls this on startup, so we can statically register all our native methods.
 * 虚拟机在启动时调用这个方法，可以静态注册我们的Native method
 *
 * @param vm
 * @return
 */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("JavaVM::GetEnv() failed");
        abort();
    }
//  register_com_netease_hearttouch_candywebcache_cachemanager_CourgettePatcher(env);
    register_com_netease_hearttouch_candywebcache_cachemanager_BsdiffPatcher(env);

    return JNI_VERSION_1_6;
}

/**
 * DalvikJVM calls this on unload
 * @param vm
 * @param reserved
 */
void JNI_OnUnload(JavaVM *vm, void *reserved) {

}