LOCAL_PATH		:= $(call my-dir)
LOCAL_PATH_EXT	:= $(call my-dir)/../external/
include $(CLEAR_VARS)

#MY_ODROID := true
#MY_NEXUS := false

MY_ODROID := false
MY_NEXUS := true

LOCAL_MODULE    := OVSR

LOCAL_CFLAGS 	+= -DANDROID_CL 
LOCAL_CFLAGS    += -O3 -ffast-math 

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../include

LOCAL_SRC_FILES := OVSR.cpp

LOCAL_LDLIBS 	:= -llog -ljnigraphics

#Odroid device
ifeq ($(MY_ODROID),true)
	LOCAL_LDLIBS 	+= $(LOCAL_PATH)/../external/libPVROCL.so
endif
#Nexus 10 device 
ifeq ($(MY_NEXUS),true)
	LOCAL_LDLIBS 	+= $(LOCAL_PATH)/../external/libGLES_mali.so
endif

LOCAL_ARM_MODE  := arm

include $(BUILD_SHARED_LIBRARY)
