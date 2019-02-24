//#include <jni.h>
//#include <string>
//#include "util/log.h"
//#include <iostream>
//
///***
// * 使用默认的方式加载
// */
//#ifdef __cplusplus
//extern "C" {
//#endif
//
//JNIEXPORT jstring JNICALL Java_tv_yixia_testj_MainActivity_stringFromJNI(JNIEnv *env,
//                                                                         jobject /* this */) {
//  std::string hello = "Hello from C++";
//  return env->NewStringUTF(hello.c_str());
//}
//
//JNIEXPORT void JNICALL Java_tv_yixia_testj_MainActivity_callFromJava(JNIEnv *env,
//                                                                     jobject /* this */,
//                                                                     jstring jStr) {
//
//  jsize len = env->GetStringLength(jStr);
//  ALOGV("input str len=%d", len);
//  const char *cStr = env->GetStringUTFChars(jStr, NULL);
//
//  char buf[60] = {'0'};
//  snprintf(buf, 60, "====%s===", cStr);
//  ALOGV("ssd%s", buf);
//
//  char tmp;
//  do {
//    tmp = *(cStr++);
//    ALOGV(">>>>%c", tmp);
//  } while (tmp != '\0');
//
//  env->ReleaseStringUTFChars(jStr, cStr);
//}
//
//#ifdef __cplusplus
//}
//#endif
//
//
