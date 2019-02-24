#获取源码所在的位置
LOCAL_PATH := $(call my-dir)

#清空内置变量 clear-vars.mk
include $(CLEAR_VARS)

#设置编译模块的名字：最终生成的目标文件名
LOCAL_MODULE := native-lib

#可选。用来override LOCAL_MODULE. 即允许用户重新定义最终生成的目标文件名。
#LOCAL_MODULE_FILENAME

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

#指出C++ 扩展名 eg: .cxx .cpp .cc 从NDK R7后，可以写多个
#LOCAL_CPP_EXTENSION

#用来指定C++ features eg:rtti exceptions
#LOCAL_CPP_FEATURES

#一个可选的设置，在编译C/C++ source 时添加如Flags，用来附加编译选项
LOCAL_CFLAGS += -fvisibility=hidden

#LOCAL_CPPFLAGS的别名
#LOCAL_CXXFLAGS

#==C++ Source 编译时添加的C Flags。这些Flags将出现在LOCAL_CFLAGS flags 的后面
#LOCAL_CPPFLAGS

#==缺省模式下，ARM目标代码被编译为thumb模式。每个指令16位。如果指定此变量为：arm。 则指令为32位。
#==LOCAL_ARM_MODE := arm   其实也可以指定某一个或者某几个文件的ARM指令模式
#LOCAL_ARM_MODE

#==设置为true时，会讲浮点编译成neon指令。这会极大地加快浮点运算(前提是硬件支持)
#==只有targeting 为 'armeabi-v7a'时才可以
#LOCAL_ARM_NEON

#可选变量，表示头文件的搜索路径,默认的头文件的搜索路径是LOCAL_PATH目录
#LOCAL_C_INCLUDES

CXX11_FLAGS := -std=c++11

#设置添加系统库
LOCAL_LDLIBS :=-llog

#设置链接到本模块的静态库
#LOCAL_STATIC_LIBRARIES:=

#设置链接到本模块的动态库
#LOCAL_SHARED_LIBRARIES:=


#==目标CPU架构名。如果为　“arm” 则声称ARM兼容的指令。 与CPU架构版本无关
#ARGET_ARCH
#==目标平台的名字
#TARGET_PLATFORM
#==Name of the target CPU+ABI
#TARGET_ARCH_ABI
#TARGET_ABI


#==build-shared-library.mk 构建共享库
#==BUILD_STATIC_LIBRARY 编译静态库
#==BUILD_EXECUTABLE 编译Native可执行库；BUILD_SHARED_LIBRARY 编译动态库
#==PREBUILT_SHARED_LIBRARY/PREBUILT_STATIC_LIBRARY:用来指定一个预先编译好多动态库
include $(BUILD_SHARED_LIBRARY)

#==NDK提供的功能宏



#Tips
##############################
#=====NDK Build System 保留以下变量名===
# 以LOCAL_ 为开头的
# 以PRIVATE_ ,NDK_ 或者APP_ 开头的名字。
# 小写字母名字：如my-dir
#如果想要定义自己在Android.mk中使用的变量名，建议添加 ＭＹ＿　前缀。
#====NDK提供的功能宏
#通过$(call function)方式调用GNU Make提供的功能的宏
#my-dir:返回的是最近一次include的Makefile的路径
#all-subdir-makefiles:返回一个列表，包含'my-dir'中所有子目录中的Android.mk
#this-makefile:当前Makefile的路径
#parent-makefile:返回include tree中父Makefile 路径
#import-module:允许寻找并inport其它modules到本Android.mk中来。它会从NDK_MODULE_PATH寻找指定的模块名。
#              $(call import-module,)
#
#
#
##########################










