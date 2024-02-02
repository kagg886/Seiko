#include <jni.h>
#include <string>
#include "android/log.h"

static const char *TAG = "test";

#define logI(x) __android_log_print(ANDROID_LOG_INFO,"Native",x)


extern "C"
JNIEXPORT void JNICALL
Java_com_kagg886_seiko_util_AntiDetect_init(JNIEnv *env, jclass clazz) {

    logI("inline hook prepare success!");
}