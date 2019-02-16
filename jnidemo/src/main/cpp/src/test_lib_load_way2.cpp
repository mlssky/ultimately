/**
  * Created by mengliwei on 2019/1/18.
  */
#include <jni.h>
#include <string>
#include "util/log.h"
#include "JNIHelpers.h"

jstring stringFromJNI(JNIEnv *env, jobject clazz) {
  std::string hello = "Hello from C++";
  return env->NewStringUTF(hello.c_str());
}

void callFromJava(JNIEnv *env, jobject clazz, jstring str, jobject context) {

  const char *cStr = env->GetStringUTFChars(str, NULL);

  jclass toastCls = env->FindClass("android/widget/Toast");
//  Toast makeText(Context context, CharSequence text, @Duration int duration)
  jmethodID makeTextMethod = env->GetStaticMethodID(toastCls,
                                                    "makeText",
                                                    "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
  jobject toastObj = env->CallStaticObjectMethod(toastCls, makeTextMethod, context, str, 1);

  //Toast#void show()
  jmethodID toastShowMethod = env->GetMethodID(toastCls, "show", "()V");
  env->CallVoidMethod(toastObj, toastShowMethod);

  //主动是否本地java引用
  env->DeleteLocalRef(toastCls);
  env->DeleteLocalRef(toastObj);

  env->ReleaseStringUTFChars(str, cStr);

}
//cache class info
static struct {
  jclass cls;
  jmethodID jmethodID1;
} sMainActivityClassInfo;

//native method class path
#define JNI_PACKAGE "tv/yixia/testj"

static JNINativeMethod gMethods[2] = {
    {"stringFromJNI", "()Ljava/lang/String;", (void *) stringFromJNI},
    {"callFromJava", "(Ljava/lang/String;Landroid/content/Context;)V", (void *) callFromJava}
};

int registerNativeMethod(JNIEnv *env) {
  jclass localClsRef;
  if ((localClsRef = env->FindClass(JNI_PACKAGE "/MainActivity")) != NULL) {
    sMainActivityClassInfo.cls = reinterpret_cast<jclass>(env->NewGlobalRef(localClsRef));
    if (!env->RegisterNatives(sMainActivityClassInfo.cls, gMethods, METHOD_COUNT(gMethods))) {
      ALOGE("regixster method succ!!");
      return JNI_OK;
    }
  }
  ALOGE("regixster method err!!");
  return JNI_ERR;
}

/*JVM 调用*/
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

  JNIEnv *env;
  if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6)) {//@since 2.3.3 开始使用java1.6
    return JNI_ERR;
  }

  if (registerNativeMethod(env)) {
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





