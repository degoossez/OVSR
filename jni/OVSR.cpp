#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdio>
#include <cstdlib>
#include <jni.h>

#include <time.h>
#include <math.h>

#include <stdio.h>

#include <android/log.h>
#include <android/bitmap.h>

#include <CL/cl.hpp>
#include <CL/cl.h>

extern "C" jint
Java_com_denayer_ovsr_MainActivity_runOpenCL(JNIEnv* env, jclass clazz, jobject bitmapIn, jobject bitmapOut, jintArray info)
{

	return 5;
}

}

