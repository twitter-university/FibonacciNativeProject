#include "com_twitter_android_fibonaccinative_FibLib.h"
#include "libfibonacci.h"

JNIEXPORT jlong JNICALL Java_com_twitter_android_fibonaccinative_FibLib_fibNR
  (JNIEnv *env, jclass clazz, jlong n) {
    return LB_fib_recursive(n);
}


JNIEXPORT jlong JNICALL Java_com_twitter_android_fibonaccinative_FibLib_fibNI
  (JNIEnv *env, jclass clazz, jlong n) {
    return LB_fib_iterative(n);
}
