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
