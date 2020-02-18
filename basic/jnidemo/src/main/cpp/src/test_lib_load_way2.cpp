/**
  * Created by mengliwei on 2019/1/18.
  */
#include <jni.h>
#include <string>
#include <unistd.h>
#include <cstdlib>
#include "util/log.h"
#include "JNIHelpers.h"

//cache class info
static struct {
  jclass cls;
  jmethodID jmethodID1;
} sMainActivityClassInfo;

//native method class path
#define JNI_PACKAGE "tv/yixia/testj"

/**
 *
 * @param env
 * @param clazz
 * @return
 */
jstring stringFromJNI(JNIEnv *env, jobject clazz);

/**
 *
 * @param env
 * @param clazz
 * @param str
 * @param context
 */
void callFromJava(JNIEnv *env, jobject clazz, jstring str, jobject context);

static JNINativeMethod gMethods[2] = {
    {"stringFromJNI", "()Ljava/lang/String;", (void *) stringFromJNI},
    {"callFromJava", "(Ljava/lang/String;Landroid/content/Context;)V", (void *) callFromJava}
};

int registerTestLibWayNativeMethod(JNIEnv *env) {
  jclass localClsRef;
  if ((localClsRef = env->FindClass(JNI_PACKAGE "/MainActivity")) != NULL) {
    sMainActivityClassInfo.cls = reinterpret_cast<jclass>(env->NewGlobalRef(localClsRef));
    if (!env->RegisterNatives(sMainActivityClassInfo.cls, gMethods, ARRAY_SIZE(gMethods))) {
      ALOGE("regixster method succ!!");
      return JNI_OK;
    }
  }
  ALOGE("regixster method err!!");
  return JNI_ERR;
}

jstring stringFromJNI(JNIEnv *env, jobject clazz) {
  std::string hello = "Hello from C++";
  char *test;// = (char *) malloc(10);
  *test = 's';
  *(test + 1) = 'b';
  *(test + 1000000000) = 'b';
//  test = "d";
  return env->NewStringUTF(hello.c_str());
}

void callFromJava(JNIEnv *env, jobject clazz, jstring str, jobject context) {

  const char *cStr = env->GetStringUTFChars(str, NULL);

  //获取类
  jclass toastCls = env->FindClass("android/widget/Toast");
//  Toast makeText(Context context, CharSequence text, @Duration int duration)
  //获取方法ID
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


