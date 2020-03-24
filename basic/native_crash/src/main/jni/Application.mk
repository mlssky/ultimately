#stlport_static
APP_STL :=c++_static
#all
APP_ABI := armeabi-v7a  arm64-v8a
APP_CXXFLAGS := -std=c++11 -D__STDC_LIMIT_MACROS
APP_PLATFORM := android-12
#此变量值为release或debug，默认为release发行模式，生成高度优化的二进制文件
#APP_OPTIM := release

