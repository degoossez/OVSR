LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_ARM_MODE  := arm

LOCAL_MODULE    := OVSR

LOCAL_SRC_FILES := OVSR.cpp

LOCAL_CFLAGS 	+= -DANDROID_CL 
LOCAL_CFLAGS    += -O3 -ffast-math

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../include

LOCAL_LDLIBS := $(LOCAL_PATH)/../external/libPVROCL.so
LOCAL_LDLIBS    := -llog
#LOCAL_LDLIBS := $(LOCAL_PATH)/../external/libOpenCL.so
LOCAL_SRC_FILES := OVSR.cpp

include $(BUILD_SHARED_LIBRARY)
