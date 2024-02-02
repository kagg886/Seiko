#include <jni.h>
#include <string>
#include "android/log.h"
#include "shadowhook.h"
#include <dirent.h>

void logI(std::string x) {
    __android_log_print(ANDROID_LOG_INFO, "Native", "%s", x.c_str());
}


auto origin_open_dir = opendir;


DIR *fakeOpen(const char *const path) {
    std::string str = "fake open:";
    str = str + path;
    logI(str);

    return origin_open_dir(path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_kagg886_seiko_util_AntiDetect_init(JNIEnv *env, jclass clazz) {
    shadowhook_init(SHADOWHOOK_MODE_SHARED, false);
    shadowhook_hook_func_addr(reinterpret_cast<void *>(origin_open_dir),
                              reinterpret_cast<void *>(fakeOpen),
                              reinterpret_cast<void **>(&origin_open_dir));
    logI("inline hook prepare success!");
}