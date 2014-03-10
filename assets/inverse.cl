kernel void inverseKernel
(
    global const uchar4* inputPixels,
    global uchar4* outputPixels,
    const uint rowPitch,
    const uint width,
    const uint height
)
{
//uchar4 conversie naar float4 zie link 
// http://developer.sonymobile.com/knowledge-base/tutorials/android_tutorial/boost-the-performance-of-your-android-app-with-opencl/
     int x = get_global_id(0);
     int y = get_global_id(1);
     int centerIndex = y * width + x;
     float4 sum4 = (float4)0.0f;
	if(get_global_id(0) < 1 || get_global_id(0) > width - 2 || get_global_id(1) < 1 || get_global_id(1) > height - 2)
	{
		return;
	}
    float4 centerPixel = convert_float4(inputPixels[centerIndex]);
    centerPixel.x = 255 - centerPixel.x;
    centerPixel.y = 255 - centerPixel.y;
    centerPixel.z = 255 - centerPixel.z;
    outputPixels[centerIndex] = convert_uchar4_sat_rte(centerPixel);
}