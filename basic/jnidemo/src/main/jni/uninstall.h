//
// Created by mengliwei on 2019/2/16.
//

#ifndef ULTIMATELY_UNINSTALL_H
#define ULTIMATELY_UNINSTALL_H

#include <jni.h>


/**
 *
 * @param env
 * @param thiz
 * @param upload_obj  上传对象信息
 * @return
 */
jint native_uninstall_watch(JNIEnv *env, jobject thiz, jobject upload_obj);


/**
 * 获取SDK版本号
 */
int get_sdk_version();

/**
 * 字符串转化char*
 * @param env
 * @param jstr
 * @return
 */
char *JstringToCStr(JNIEnv *env, jstring jstr);

/**
 * 上传统计数据
 */
int uploadStatData(char *versionName, jint versionCode);

/**
 * 监听
 */
int startObserver(void *p_buf);

/**
 * 判断是否进程活着
 */
int isProcessAlive(const char *pid);

/**
 * 记录pid
 */
void writePidFile(const char *pid);


#endif //ULTIMATELY_UNINSTALL_H
