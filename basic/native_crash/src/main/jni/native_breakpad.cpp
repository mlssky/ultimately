#include "third_party/breakpad/src/client/linux/handler/exception_handler.h"
#include "third_party/breakpad/src/client/linux/handler/minidump_descriptor.h"

#include "util/JNIHelpers.h"
#include "util/log.h"

extern jint NativeBreakpad_nativeInit(JNIEnv *env,
                                      jobject obj,
                                      jstring crash_dump_path);

extern jint NativeBreakpad_testCrash(JNIEnv *env, jobject obj);

#define JNI_JAVA_CLZ "com/xcleans/apm/NativeUncaughtExceptionHandler"
JNINativeMethod gMethods[2] = {
    NATIVE_METHOD("testcrash", "()V", NativeBreakpad_testCrash),
    NATIVE_METHOD("init", "(Ljava/lang/String;)V", NativeBreakpad_nativeInit),
};

/**
 * @param env
 * @return
 */
jint registerNativeMethod(JNIEnv *env) {
  return jniRegisterNativeMethods(env, JNI_JAVA_CLZ, gMethods, ARRAY_SIZE(gMethods));
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env;
  jint result = vm->GetEnv(reinterpret_cast<void **> (&env), JNI_VERSION_1_4);
  if (result) {
    ALOGD("JNI_OnLoad ====> could not get JNI env");
    return JNI_ERR;
  }
  result = registerNativeMethod(env);
  if (result) {
    ALOGE("registerUninstallNativeMethod err!!");
    return JNI_ERR;
  }
  ALOGE("JNI_OnLoad succ");
  return JNI_VERSION_1_4;
}

bool DumpCallback(const google_breakpad::MinidumpDescriptor &descriptor,
                  void *context,
                  bool succeeded) {
  ALOGD("DumpCallback ===> succeeded %d", succeeded);
  return succeeded;
}

jint NativeBreakpad_nativeInit(JNIEnv *env,
                               jobject obj,
                               jstring crash_dump_path) {
  // (char *) env->GetStringUTFChars(crash_dump_path, NULL);
  const char *path = jstringToChar(env, crash_dump_path);
  google_breakpad::MinidumpDescriptor descriptor(path);
  static google_breakpad::ExceptionHandler eh(descriptor, NULL, DumpCallback, NULL, true, -1);
  ALOGD("nativeInit ===> breakpad initialized succeeded, dump file will be saved at %s", path);
  return JNI_OK;
}

jint NativeBreakpad_testCrash(JNIEnv *env, jobject obj) {
  ALOGD("native crash capture begin");
  char *ptr = NULL;
  *ptr = 1;
  ALOGD("native crash capture end");
  return 0;
}
