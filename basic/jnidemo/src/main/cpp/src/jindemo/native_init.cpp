//
// Created by mengliwei on 2019/2/16.
//

#include <jni.h>
#include "test_lib_load_way2.h"

/**
 * JVM 加载启动时调用
 * @param vm
 * @param reserved
 * @return
 */
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    //@since 2.3.3 开始使用java1.6
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6)) {
        return JNI_ERR;
    }

    if (registerTestLibWayNativeMethod(env)) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;

}

/**
 * @param vm
 * @param reserved
 */
JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {

}