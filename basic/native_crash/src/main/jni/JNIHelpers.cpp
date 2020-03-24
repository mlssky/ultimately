/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <cstdlib>
#include "util/JNIHelpers.h"
#include "util/log.h"

/**
 * register java native method
 *
 * @param env
 * @param classPathName   mapping to java class
 * @param nativeMethods   native method define 数组
 * @param nMethods
 * @return   JNI_ERR or JNI_OK
 */
jint jniRegisterNativeMethods(JNIEnv *env,
                              const char *classPathName,
                              JNINativeMethod *nativeMethods,
                              jint nMethods) {
  jclass clazz = env->FindClass(classPathName);
  if (clazz == NULL) {
    ALOGD("Native registration unable to find class '%s'", classPathName);
    return JNI_ERR;
  }
  if (env->RegisterNatives(clazz, nativeMethods, nMethods) < 0) {
    ALOGD("RegisterNatives failed for '%s'", classPathName);
    return JNI_ERR;
  }
  return JNI_OK;
}

void jniThrowException(JNIEnv *env, const char *className, const char *msg) {
  jclass clazz = env->FindClass(className);
  if (!clazz) {
    ALOGE("Unable to find exception class %s", className);
    /* ClassNotFoundException now pending */
    return;
  }

  if (env->ThrowNew(clazz, msg) != JNI_OK) {
    ALOGE("Failed throwing '%s' '%s'", className, msg);
    /* an exception, most likely OOM, will now be pending */
  }
  env->DeleteLocalRef(clazz);
}

/**
 *将Utf-8 str字符串转换成unicode 编码，在使用NewString 获取Java字符串
 * 防止出现乱码
 * //必现奔溃,直接使用
 * env->NewStringUTF("Test String from Natative 你好吧😋");
 *
 * 将native的UFT-8编码转换成java的Unicode编码(UTF-16 两个字节)
 * @param env
 * @param pat
 * @return
 */
jstring cStrTojstring(JNIEnv *env, jobject thiz, const char *pat) {
//  try {
  jclass strClass = env->FindClass("java/lang/String");
  if (strClass == NULL) {
    ALOGD("Native registration unable to find class");
    return NULL;
  }
  jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
  if (ctorID != NULL) {
    jbyteArray bytes = env->NewByteArray(strlen(pat));
    if (bytes != NULL) {
      //A family of functions that copies back a region of a primitive array from a buffer.
      (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
      jstring encoding = env->NewStringUTF("UTF-8");
      return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
    }
  }
  return NULL;
}

/**
 * GetStringUTFChars 会出现乱码如果输入的jstring 字符串中包含有emoji表情
 * @param env
 * @param jstr
 * @return
 */
const char *jstringToChar(JNIEnv *env, jstring jstr) {
  char *rtn = NULL;
  jclass clsstring = env->FindClass("java/lang/String");
  jstring strencode = env->NewStringUTF("UTF-8");
  jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
  jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
  jsize alen = env->GetArrayLength(barr);
  jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
  if (alen > 0) {
    rtn = (char *) malloc(alen + 1);
    memcpy(rtn, ba, alen);
    rtn[alen] = 0;
  }
  env->ReleaseByteArrayElements(barr, ba, 0);
  return rtn;
}

