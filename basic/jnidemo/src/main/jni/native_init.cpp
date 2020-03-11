#include <jni.h>
#include "util/log.h"
#include "uninstalInit.h"

/**
 * JNIEXPORT 保证函数是可见的
 * JVM 加载启动时调用
 * @param vm
 * @param reserved
 * @return
 */
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

  //@since 2.3.3 开始使用java1.6
  JNIEnv *env;
  jint result = vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
  if (result) {
    ALOGE("GetEnv err!!");
    return JNI_ERR;
  }
  result = registerUninstallNativeMethod(env);
  if (result) {
    ALOGE("registerUninstallNativeMethod err!!");
    return JNI_ERR;
  }
  ALOGE("JNI_OnLoad succ");
  
  return JNI_VERSION_1_6;

}

/**
 * #define JNIEXPORT  __attribute__ ((visibility ("default")))
 * “default”：用它定义的符号将被导出，动态库中的函数默认是可见的
 * 隐藏的符号将不会出现在动态符号表中，但是还被留在符号表中用于静态链接。
 * @param vm
 * @param reserved
 */
JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {

}