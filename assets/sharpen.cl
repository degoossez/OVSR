kernel void sharpenKernel
(
    global const uchar4* inputPixels,
    global uchar4* outputPixels,
    const uint rowPitch,
    const uint width,
    const uint height
)
{
//uchar4 conversie naar float4 zie link http://developer.sonymobile.com/knowledge-base/tutorials/android_tutorial/boost-the-performance-of-your-android-app-with-opencl/
     int x = get_global_id(0);
     int y = get_global_id(1);
     int counter=0;
     const int edgeKernel[9] = {0,-1,0,-1,5,-1,0,-1,0};
     //	const int edgeKernel[9] = {0,1,0,1,-4,1,0,1,0};
     
     float4 convPixel;
	if(get_global_id(0) < 1 || get_global_id(0) > width - 2 || get_global_id(1) < 1 || get_global_id(1) > height - 2)
	{
		return;
	}
	int centerIndex;
	for(int xOffset=-1;xOffset<=1;xOffset++)
	{
		for(int yOffset=-1;yOffset<=1;yOffset++)
		{
			centerIndex = (y+yOffset) * width + x + xOffset;
		    float4 centerPixel = convert_float4(inputPixels[centerIndex]);
		    convPixel.x += centerPixel.x * edgeKernel[counter];
		    convPixel.y += centerPixel.y * edgeKernel[counter];
		    convPixel.z += centerPixel.z * edgeKernel[counter];
		    counter++;    
	    }
	}
	if(convPixel.x>255) convPixel.x=255;
	if(convPixel.y>255) convPixel.y=255;
	if(convPixel.z>255) convPixel.z=255;
	if(convPixel.x<0) convPixel.x=0;
	if(convPixel.y<0) convPixel.y=0;
	if(convPixel.z<0) convPixel.z=0;
    outputPixels[centerIndex] = convert_uchar4_sat_rte(convPixel);
}

