ROOT_PATH := $(call my-dir)
include $(ROOT_PATH)/third_party/breakpad/android/google_breakpad/Android.mk
LOCAL_PATH := $(ROOT_PATH)

include $(CLEAR_VARS)

LOCAL_MODULE    := breakpad

LOCAL_SRC_FILES := native_breakpad.cpp \
                   JNIHelpers.cpp

LOCAL_LDLIBS := -llog

#一个可选的设置，在编译C/C++ source 时添加如Flags，用来附加编译选项
#-fvisibility=hidden 隐藏非必要函数导出
#-DDEBUG :#define DEBUG
LOCAL_CFLAGS += -fvisibility=hidden -DDEBUG
 #-Werror  -Wreturn-type

LOCAL_STATIC_LIBRARIES += breakpad_client

include $(BUILD_SHARED_LIBRARY)
