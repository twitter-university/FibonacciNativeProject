LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := libfibonacci.c
LOCAL_MODULE    := libfibonacci
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := com_twitter_android_fibonaccinative_FibLib.c
LOCAL_MODULE    := com_twitter_android_fibonaccinative_FibLib
LOCAL_STATIC_LIBRARIES := libfibonacci
include $(BUILD_SHARED_LIBRARY)
