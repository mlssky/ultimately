//
// Created by NetEase on 16/6/7.
//

#ifndef CANDYWEBCACHE_JNIHELPER_H
#define CANDYWEBCACHE_JNIHELPER_H

#include <android/log.h>

//log define
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

#define NATIVE_METHOD(className, functionName, signature) \
    { #functionName, signature, reinterpret_cast<void*>(className ## _ ## functionName) }


/**
 * 调用默认的空的构造方法
 * @param env
 * @param obj_class
 * @return
 */
jobject getInstance(JNIEnv *env, jclass obj_class) {
    jmethodID construction_id = env->GetMethodID(obj_class, "<init>", "()V");
    jobject obj = env->NewObject(obj_class, construction_id);
    return obj;
}


#endif //CANDYWEBCACHE_JNIHELPER_H
