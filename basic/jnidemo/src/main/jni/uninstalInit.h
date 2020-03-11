/**
  * Created by mengliwei on 2020-02-19.
  */

#ifndef ULTIMATELY_UNINSTALINIT_H
#define ULTIMATELY_UNINSTALINIT_H

#include <jni.h>
#include "uninstall.h"

//native method class path
#define JNI_PACKAGE "com/xcleans/uninstall"
#define JNI_JAVA_CLZ "com/xcleans/uninstall/Uninstall"

/**
 * 注册关联Native方法
 * @param env
 * @return
 */
int registerUninstallNativeMethod(JNIEnv *env);

#endif //ULTIMATELY_UNINSTALINIT_H
