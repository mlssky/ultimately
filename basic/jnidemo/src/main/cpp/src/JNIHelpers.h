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
 *
 * We instruct the compiler to be very strict when it comes to code
 * like unused arguments that could indicate a bug. Because of that, handling
 * intentionally unused arguments requires tricks like casting to void. This
 * macro provides a readable name for this operation.
 */
#define UNUSED(expr) ((void) (expr));

//计算数组的长度
#define ARRAY_SIZE(arr) (sizeof(arr) / sizeof((arr)[0]))

#define ILLEGAL_STATE_EXEPTION "java/lang/IllegalStateException"

/**
 *
 * @param env         JVM  env
 * @param className   异常类名
 * @param msg         异常消息
 */
void jniThrowException(JNIEnv *env, const char *className, const char *msg);

#endif //RASTERMILL_JNIHELPERS_H
