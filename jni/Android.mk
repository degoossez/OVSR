LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := OVSR
LOCAL_SRC_FILES := OVSR.cpp

include $(BUILD_SHARED_LIBRARY)
