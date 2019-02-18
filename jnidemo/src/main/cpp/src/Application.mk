APP_BUILD_SCRIPT := Android.mk
APP_PLATFORM ：=android-14
APP_ABI := armeabi-v7a
#x86
APP_MK_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
NDK_MODULE_PATH := $(APP_MK_DIR)

#APP_OPTIM := release
APP_STL := gnustl_static

