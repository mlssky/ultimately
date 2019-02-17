//
// Created by mengliwei on 2019/2/16.
//

#include "uninstall.h"
#include <jni.h>
#include <string>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/file.h>
#include <sys/inotify.h>
#include <sys/stat.h>
#include <android/log.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/system_properties.h>
#include <util/log.h>

//app 目录
static const char APP_DIR[] = "/data/data/com.wx.appuninstall";
static const char APP_FILES_DIR[] = "/data/data/com.wx.appuninstall/files";
static const char APP_OBSERVED_FILE[] = "/data/data/com.wx.appuninstall/files/observedFile";
static const char APP_LOCK_FILE[] = "/data/data/com.wx.appuninstall/files/lockFile";
static const char *HOST_ADDR = "www.baidu.com";
static const char *SERVER_ADDR = "http://www.baidu.com";
static const int OK = 0;
static const int ERROR = -1;
int watchDescriptor;
int fileDescriptor;
pid_t observer = -1;

/**
 *
 * @param env
 * @param thiz
 * @param upload_obj  上传对象信息
 * @return
 */
jint native_uninstall_watch(JNIEnv *env, jobject thiz, jobject upload_obj) {

    UNUSED(thiz)

    if (upload_obj == NULL) {
        exit(1);
    }

    // 获得UploadInfo类引用
    jclass upload_cls = env->GetObjectClass(upload_obj);
    if (upload_cls == NULL) {
        exit(1);
    }

    // 判断监听进程是否活着
    if (isProcessAlive(APP_OBSERVED_FILE) == OK) {
        ALOGE("watch process already exists");
        return observer;
    }

    // 若被监听文件存在，删除
    FILE *p_observedFile = fopen(APP_OBSERVED_FILE, "r");
    if (p_observedFile != NULL) {
        ALOGD("delete observed file");
        remove(APP_OBSERVED_FILE);
        fclose(p_observedFile);
    }

    // 删除锁文件
    FILE *p_LockedFile = fopen(APP_LOCK_FILE, "r");
    if (p_LockedFile != NULL) {
        ALOGD("delete lock file");
        remove(APP_LOCK_FILE);
        fclose(p_LockedFile);
    }

    // 创建进程
    pid_t pid = fork();
    // 根据返回值不同做不同操作
    if (pid < 0) {  // 创建进程失败
        ALOGE("fork process error!");
    } else if (pid == 0) {  // 创建第一个子进程成功，代码运行在子进程中
        ALOGD("fork first process succ pid = %d", getpid());
        setsid();  // 将进程和它当前的对话过程和进程组分离开，并且把它设置成一个新的对话过程的领头进程。
        umask(0);  // 为文件赋予更多的权限，因为继承来的文件可能某些权限被屏蔽
        int pid = fork();
        if (pid == 0) { // 第二个子进程
            // 保存监听进程id
            ALOGD("fork second process succ pid = %d", getpid());
            // 分配缓存，以便读取event，缓存大小等于一个struct inotify_event的大小，这样一次处理一个event
            void *p_buf = malloc(sizeof(struct inotify_event));
            if (p_buf == NULL) {
                ALOGD("malloc failed !!!");
                exit(1);
            }
            // 通过linux中的inotify机制来监听应用的卸载。inotify是linux内核用于通知用户空间文件系统变化的机制，文件的添加或卸载等事件都能够及时捕获到。
            if (startObserver(p_buf) != 0) {
                return 0;
            }
            writePidFile(APP_OBSERVED_FILE);

            // 开始监听
            while (1) {
                ALOGD("start watch");
                // 调用read函数开始监听，read会阻塞进程
                ssize_t readBytes = read(fileDescriptor, p_buf, sizeof(struct inotify_event));

                // 走到这里说明收到目录被删除的事件
                if (IN_DELETE_SELF == ((struct inotify_event *) p_buf)->mask) {
                    ALOGD("IN_DELETE_SELF");
                    // 若文件被删除，可能是已卸载，还需进一步判断app文件夹是否存在
                    FILE *p_appDir = fopen(APP_DIR, "r");
                    if (p_appDir != NULL) {
                        // 应用主目录还在（可能还没有来得及清除），sleep一段时间后再判断
                        sleep(5);
                        p_appDir = fopen(APP_DIR, "r");
                    }
                    // 确认已卸载
                    if (p_appDir == NULL) {
                        ALOGD("inotify rm watch");
                        inotify_rm_watch(fileDescriptor, watchDescriptor);
                        break;
                    } else {  // 未卸载，可能用户执行了"清除数据"
                        ALOGD("not uninstall");
                        fclose(p_appDir);
                        // 应用没有卸载，重新监听
                        if (startObserver(p_buf) != 0) {
                            return 0;
                        }
                    }

                } else {
                    ALOGD("NOT IN_DELETE_SELF");
                }
            }
            ALOGD("end watch");
            remove(APP_OBSERVED_FILE);
            remove(APP_LOCK_FILE);
            free(p_buf);

            jfieldID nameFieldID = env->GetFieldID(upload_cls, "versionName",
                                                   "Ljava/lang/String;"); // 获得属性ID
            jfieldID codeFieldID = env->GetFieldID(upload_cls, "versionCode", "I"); // 获得属性ID
            jfieldID browserFieldID = env->GetFieldID(upload_cls, "isBrowser", "Z");   // 获得属性ID
            jstring versionName = (jstring) env->GetObjectField(upload_obj, nameFieldID);// 获得属性值
            jint versionCode = env->GetIntField(upload_obj, codeFieldID);  // 获得属性值
            jboolean isBrowser = env->GetBooleanField(upload_obj, browserFieldID);    // 获得属性值
            char *vName = JstringToCStr(env, versionName);

            // 上传统计数据
            if (uploadStatData(vName, versionCode) == OK) {
                ALOGD("upload data succ");
            }

            // 是否打开浏览器
            if (isBrowser) {    // TODO 打开浏览器命令在有些手机上可能失效
                // 执行命令am start --user userSerial -a android.intent.action.VIEW -d $(url)
                execlp("am", "am", "start", "--user", "0", "-a", "android.intent.action.VIEW", "-d",
                       SERVER_ADDR,
                       (char *) NULL);
            }

        } else {
            exit(0);
        }
    } else {
        // 父进程直接退出，使子进程被init进程领养，以避免子进程僵死，同时返回子进程pid
        ALOGD("parent process exit");
    }
    return pid;
}

/**
 * 监听
 */
int startObserver(void *p_buf) {

    // 若监听文件所在文件夹不存在，创建文件夹
    FILE *p_filesDir = fopen(APP_FILES_DIR, "r");
    if (p_filesDir == NULL) {
        int filesDirRet = mkdir(APP_FILES_DIR, S_IRWXU | S_IRWXG | S_IXOTH);
        if (filesDirRet == -1) {
            ALOGE("create app files dir failed");
            exit(1);
        }
    }

    // 若被监听文件不存在，创建监听文件
    FILE *p_observedFile = fopen(APP_OBSERVED_FILE, "r");
    if (p_observedFile == NULL) {
        p_observedFile = fopen(APP_OBSERVED_FILE, "w");
        ALOGD("create app observed file");
    }
    fclose(p_observedFile);

    // 创建锁文件，通过检测加锁状态来保证只有一个卸载监听进程
    int lockFileDescriptor = open(APP_LOCK_FILE, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(APP_LOCK_FILE, O_CREAT);
        ALOGD("create app lock file");
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
    if (lockRet == -1) {
        ALOGE("watch by other process");
        return ERROR;
    }

    // 初始化inotify进程
    fileDescriptor = inotify_init();
    if (fileDescriptor < 0) {
        ALOGE("inotify init failed");
        free(p_buf);
        exit(1);
    }

    // 添加inotify监听器，监听APP_OBSERVED_FILE文件
    watchDescriptor = inotify_add_watch(fileDescriptor, APP_OBSERVED_FILE, IN_ALL_EVENTS);
    if (watchDescriptor < 0) {
        ALOGE("inotify watch failed");
        free(p_buf);
        exit(1);
    }
    return OK;
}

/**
 * 上传统计数据
 */
int uploadStatData(char *versionName, jint versionCode) {
    ALOGD("upload stat data");

    struct sockaddr_in serv_addr;
    struct hostent *host;
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    if ((host = gethostbyname(HOST_ADDR)) == NULL) {
        ALOGE("host name is null.");
        return ERROR;
    }

    memset(&serv_addr, 0, sizeof(serv_addr));  // 每个字节都用0填充
    serv_addr.sin_family = AF_INET;  // 使用IPv4地址
//    serv_addr.sin_addr.s_addr = inet_addr("192.168.1.1");  // 具体的IP地址
    serv_addr.sin_addr = *((struct in_addr *) host->h_addr);
    serv_addr.sin_port = htons(80);  //端口

    if (connect(sock, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        ALOGE("connect error");
        return ERROR;
    }

    ALOGD("connect succ");
    int sdkVersion = get_sdk_version();
    char request[200];
    sprintf(request, "GET /web/index.html?versionName=%s&versionCode=%d&sdkVersion=%d", versionName,
            versionCode, sdkVersion);
    if (write(sock, request, strlen(request)) < 0) {
        ALOGE("request failed");
        return ERROR;
    }
    ALOGD("request success");
    // 关闭套接字
    close(sock);
    return OK;
}

/**
 * 判断进程是否存在
 * @param pid
 * @return
 */
int isProcessAlive(const char *pid) {

    FILE *pidFile;
    char observerPID[32];
    if ((pidFile = fopen(pid, "rb")) == NULL) {
        ALOGE("can't open pid file");
        return ERROR;
    }
    fscanf(pidFile, "%d", &observer);
    fclose(pidFile);

    if (observer > 1) {
        sprintf(observerPID, "%d/n", observer);
        ALOGD("read saved pid");


        if (kill(observer, 0/**判断进程是否存在*/) == 0) {
            ALOGD("process is alive");
            return OK;
        }
        ALOGD("process is not alive");
    } else {
        ALOGD("not read saved pid");
        return ERROR;
    }
}

/**
 * 记录pid
 */
void writePidFile(const char *pid) {
    char str[32];
    int pidFile = open(pid, O_WRONLY | O_TRUNC);
    if (pidFile < 0) {
        ALOGE("pid is %d", pidFile);
        exit(1);
    }

    if (flock(pidFile, LOCK_EX | LOCK_NB) < 0) {
        ALOGD("cann't lock pid file: %s", pid);
        fprintf(stderr, "can't lock pid file: %s", pid);
        exit(1);
    }

    sprintf(str, "%d/n", getpid());
    ssize_t len = strlen(str);
    ssize_t ret = write(pidFile, str, len);

    if (ret != len) {
        ALOGE("can't write pid file: %s", pid);
        fprintf(stderr, "can't write pid file: %s", pid);
        exit(1);
    }
    close(pidFile);
    ALOGD("write pid file success");
}

/**
 * 获取SDK版本号
 */
int get_sdk_version() {
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);
    return atoi(value);
}

/**
 * Jstring转char*
 */
char *JstringToCStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String"); //String
    jstring strencode = env->NewStringUTF("GB2312"); // 得到一个java字符串 "GB2312"
    jmethodID mid = env->GetMethodID(clsstring, "getBytes",
                                     "(Ljava/lang/String;)[B"); //[ String.getBytes("gb2312");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid,
                                                         strencode); // String .getByte("GB2312");
    jsize alen = env->GetArrayLength(barr); // byte数组的长度
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1); //"\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

