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

#ifndef RASTERMILL_JNIHELPERS_H
#define RASTERMILL_JNIHELPERS_H

#include <jni.h>
#include <cstring>

//兼容C语言自定义包含宏定义
#ifdef __cplusplus
#define    BEGIN_C_DECLS    extern "C" {
#define    END_C_DECLS    }
#else
#define    BEGIN_C_DECLS
#define    END_C_DECLS
#endif

/**
 * Explicitly marks result of an expression as unused.
 * We instruct the compiler to be very strict when it comes to code
 * like unused arguments that could indicate a bug. Because of that, handling
 * intentionally unused arguments requires tricks like casting to void. This
 * macro provides a readable name for this operation.
 */
#ifndef UNUSED
#define UNUSED(expr) ((void) (expr));
#endif

//计算数组的长度
#ifndef ARRAY_SIZE
#define ARRAY_SIZE(arr) ((int)sizeof(arr) / sizeof((arr)[0]))
#endif

//#define Conn(x,y)    x##y   //连接x,y
//#define ToChar(x)    #@x    //加单引号
//#define ToString(x)  #x     //加双引号
#ifndef NATIVE_METHOD
#define NATIVE_METHOD(javaMethodName, javaMethodSign, nativeFunPointer) \
    {javaMethodName, javaMethodSign, reinterpret_cast<void*>(nativeFunPointer) }
#endif

#ifndef ILLEGAL_STATE_EXEPTION
#define ILLEGAL_STATE_EXEPTION "java/lang/IllegalStateException"
#endif

/**
 *
 * Throw an exception with the specified class and an optional message.
 * The "className" argument will be passed directly to FindClass, which
 * takes strings with slashes (e.g. "java/lang/Object").
 * If an exception is currently pending, we log a warning message and
 * clear it.
 * Returns 0 on success, nonzero if something failed (e.g. the exception
 * class couldn't be found, so *an* exception will still be pending).
 * Currently aborts the VM if it can't throw the exception.
 * @param env
 * @param className
 * @param msg
 **/
void jniThrowException(JNIEnv *env, const char *className, const char *msg);

/**
 * Constructs a new java.lang.String object
 * from an array of characters in modified UTF-8 encoding.
 * 代替NewStringUTF
 * @param env
 * @param str
 * @return
 */
jstring cStrTojstring(JNIEnv *env, jobject thiz, const char *str);

/**
 * 替代env->GetStringUTFChars(jstr, JNI_FALSE);方法
 * 防止出现乱码
 * @param env
 * @param jstr
 * @return
 */
const char *jstringToChar(JNIEnv *env, jstring jstr);

/**
 *
 * @param env
 * @param classPathName  类名
 * @param nativeMethods
 * @param nMethods
 * @return
 */
jint jniRegisterNativeMethods(JNIEnv *env,
                             const char *classPathName,
                             JNINativeMethod *nativeMethods,
                             jint nMethods);

#endif //RASTERMILL_JNIHELPERS_H
