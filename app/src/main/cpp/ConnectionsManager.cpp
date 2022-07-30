#include <jni.h>
#include <pthread.h>

timespec timeSpec;
timespec timeSpecMonotonic;
int32_t timeDifference = 0;

int64_t getCurrentTimeMillis() {
    clock_gettime(CLOCK_REALTIME, &timeSpec);
    return (int64_t) timeSpec.tv_sec * 1000 + (int64_t) timeSpec.tv_nsec / 1000000;
}

int64_t getCurrentTimeMonotonicMillis() {
    clock_gettime(CLOCK_BOOTTIME, &timeSpecMonotonic);
    return (int64_t) timeSpecMonotonic.tv_sec * 1000 +
           (int64_t) timeSpecMonotonic.tv_nsec / 1000000;
}

int32_t getCurrentTime() {
    return (int32_t) (getCurrentTimeMillis() / 1000) + timeDifference;
}

int32_t getTimeDifference() {
    return timeDifference;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_xyz_illuminate_server_ConnectionsManager_getCurrentTimeMillis(JNIEnv *env, jclass clazz) {
    return getCurrentTimeMillis() +
           ((jlong) getTimeDifference()) * 1000;
}
extern "C"
JNIEXPORT jint JNICALL
Java_xyz_illuminate_server_ConnectionsManager_getCurrentTime(JNIEnv *env, jclass clazz) {
    return getCurrentTime();
}
extern "C"
JNIEXPORT jint JNICALL
Java_xyz_illuminate_server_ConnectionsManager_getTimeDifference(JNIEnv *env, jclass clazz) {
    return getTimeDifference();
}

extern "C"
JNIEXPORT jint JNICALL
Java_xyz_illuminate_server_ConnectionsManager_getCurrentTimeMonotonicMillis(JNIEnv *env,
                                                                            jclass clazz) {
    return getCurrentTimeMonotonicMillis();
}
