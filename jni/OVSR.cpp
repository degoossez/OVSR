#include <android/bitmap.h>
#include <android/log.h>
#define app_name "hello_OpenCL_Example1"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, app_name, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, app_name, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, app_name, __VA_ARGS__))
#include <jni.h>
#include <stdio.h>
#include <cstring>
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdio>
#include <cstdlib>

#include <time.h>
#include <math.h>

#define CL_USE_DEPRECATED_OPENCL_1_1_APIS
#define __CL_ENABLE_EXCEPTIONS
//#include <CL/cl.h>
#include <CL/cl.hpp>

inline std::string loadProgram(std::string input)
{
	std::ifstream stream(input.c_str());
	LOGI("input string: %s", input.c_str());
	if (!stream.is_open()) {
		LOGE("Cannot open input file\n");
		exit(1);
	}
	return std::string( std::istreambuf_iterator<char>(stream),
						(std::istreambuf_iterator<char>()));
}
void openCLNR (unsigned char* bufIn, unsigned char* bufOut, int* info)
{

	LOGI("\n\nStart openCLNR (i.e., OpenCL on the GPU)");

	int width = info[0];
	int height = info[1];
	unsigned int imageSize = width * height * 4 * sizeof(cl_uchar);

	cl_int err = CL_SUCCESS;
	try {
		std::vector<cl::Platform> platforms;
		cl::Platform::get(&platforms);
		if (platforms.size() == 0) {
			std::cout << "Platform size 0\n";
			return;
		}
		cl_context_properties properties[] ={ CL_CONTEXT_PLATFORM, (cl_context_properties)(platforms[0])(), 0};
		cl::Context context(CL_DEVICE_TYPE_GPU, properties);

		std::vector<cl::Device> devices = context.getInfo<CL_CONTEXT_DEVICES>();
		cl::CommandQueue queue(context, devices[0], 0, &err);

		std::string kernelSource = loadProgram("data/data/com.denayer.ovsr/app_execdir/EigenOpenCLKernel.cl");
		//std::string kernelSource = loadProgram("/data/data/com.denayer.ovsr/app_execdir/bilateralKernel.cl");

		cl::Program::Sources source(1, std::make_pair(kernelSource.c_str(),kernelSource.length()+1));

		cl::Program program(context, source);
		const char *options = "-cl-fast-relaxed-math";
		program.build(devices, options);

		cl::Kernel kernel(program, "EigenOpenCLKernel", &err);
		//cl::Kernel kernel(program, "bilateralFilterKernel", &err);

		cl::Buffer bufferIn = cl::Buffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, imageSize, (void *) &bufIn[0], &err);
		cl::Buffer bufferOut = cl::Buffer(context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, imageSize, (void *) &bufOut[0], &err);

		kernel.setArg(0,bufferIn);
		kernel.setArg(1,bufferOut);
		kernel.setArg(2,width);
		kernel.setArg(3,height);

		cl::Event event;

		clock_t startTimer1, stopTimer1;
		startTimer1=clock();

		//one time
		queue.enqueueNDRangeKernel(	kernel,
				cl::NullRange,
				cl::NDRange(width,height),
				cl::NullRange,
				NULL,
				&event);

//		//swap in and out buffer pointers and run a 2nd time
//		kernel.setArg(0,bufferOut);
//		kernel.setArg(1,bufferIn);
//		queue.enqueueNDRangeKernel(	kernel,
//				cl::NullRange,
//				cl::NDRange(width,height),
//				cl::NullRange,
//				NULL,
//				&event);
//
//		//swap in and out buffer pointers and run a 3rd time
//		kernel.setArg(0,bufferIn);
//		kernel.setArg(1,bufferOut);
//		queue.enqueueNDRangeKernel(	kernel,
//				cl::NullRange,
//				cl::NDRange(width,height),
//				cl::NullRange,
//				NULL,
//				&event);

		queue.finish();

		stopTimer1 = clock();
		double elapse = 1000.0* (double)(stopTimer1 - startTimer1)/(double)CLOCKS_PER_SEC;
		info[2] = (int)elapse;
		LOGI("OpenCL code on the GPU took %g ms\n\n", 1000.0* (double)(stopTimer1 - startTimer1)/(double)CLOCKS_PER_SEC) ;

		queue.enqueueReadBuffer(bufferOut, CL_TRUE, 0, imageSize, bufOut);
		queue.finish();
	}
	catch (cl::Error err) {
		LOGE("ERROR: %s\n", err.what());
	}
	return;
}

//extern "C" jint
extern "C" JNIEXPORT jint JNICALL
Java_com_denayer_ovsr_MainActivity_runOpenCL(JNIEnv* env, jclass clazz, jobject bitmapIn, jobject bitmapOut, jintArray info)
{

    void*	bi;
    void*   bo;

	jint* i = env->GetIntArrayElements(info, NULL);

    AndroidBitmap_lockPixels(env, bitmapIn, &bi);
    AndroidBitmap_lockPixels(env, bitmapOut, &bo);

	openCLNR((unsigned char *)bi, (unsigned char *)bo, (int *)i);

    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);
    env->ReleaseIntArrayElements(info, i, 0);

    return 0;
}
