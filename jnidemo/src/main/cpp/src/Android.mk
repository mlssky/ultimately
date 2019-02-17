LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := native-lib

#1定义查找所有cpp文件的宏
define all-cpp-files-under
$(patsubst ./%,%, $(shell find $(LOCAL_PATH) -name "platform" -prune -o -name "*.cpp" -and -not -name ".*"))
endef

define all-subdir-cpp-files
$(call all-cpp-files-under,.)
endef

#匹配结果展开为已经存在的、使用空格分开的、匹配此模式的所有文件列表
#基本用法$(wildcard PATTERN...)
#相对路径会导致wildcard匹配不到源文件
CPP_FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)

LOCAL_SRC_FILES := $(CPP_FILE_LIST:$(LOCAL_PATH)/%=%)

#LOCAL_SRC_FILES := \
#	JNIHelpers.cpp \
#	test_lib_load_way2.cpp \


CXX11_FLAGS := -std=c++11
#隐藏非必要函数导出
LOCAL_CFLAGS += -fvisibility=hidden
LOCAL_LDLIBS :=-llog

include $(BUILD_SHARED_LIBRARY)









