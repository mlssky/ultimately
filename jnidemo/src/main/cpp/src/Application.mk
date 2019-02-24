# 此变量用于存储应用项目根目录的绝对路径，如果Application.mk文件放在$NDK/apps/<myapp>下必须设置此变量，
#如果放在$PROJECT/jni/目录下此变量可选
#APP_PROJECT_PATH := /src

#默认情况下，NDK编译系统会在$(APP_PROJECT_PATH)/jni目录下寻找名为Android.mk文件
APP_BUILD_SCRIPT := Android.mk


APP_MK_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
NDK_MODULE_PATH := $(APP_MK_DIR)

#从Android4.1（API Level 16）开始，Android的动态链接器支持位置独立的可执行文件（PIE）;
#从Android 5.0（API Level 21）开始可执行文件需要PIE.
#默认情况下，从android-16起ndk-build会自动将此值设置为true，也可也同手动设置true或false；
#APP_PIE

#设置Android平台的名称
APP_PLATFORM ：=android-14

#NDK构建系统默认使用armeabi ABI生成机器代码，可以自己进行设置
APP_ABI := armeabi-v7a x86

#此变量值为release或debug，默认为release发行模式，生成高度优化的二进制文件
APP_OPTIM := release

#默认情况下，NDK构建系统为Android系统提供的最小C++运行时库
APP_STL := gnustl_static

