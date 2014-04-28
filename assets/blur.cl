kernel void blurKernel
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

	int i = 0;
	int j = 0;
	float4 bufferPixel;
	float4 currentPixel;
	float sumr,sumg,sumb;
	sumr = sumg = sumb = 0;
	int counter = 0;
    const float blurKernel[9] = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
	currentPixel = convert_float4(inputPixels[centerIndex]);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		centerIndex = (y+i) * width + (x+j);
	    bufferPixel = convert_float4(inputPixels[centerIndex]);
	   	sumr = sumr + (bufferPixel.x * blurKernel[counter]);
	    sumg = sumg + (bufferPixel.y * blurKernel[counter]);
	    sumb = sumb + (bufferPixel.z * blurKernel[counter]);
	    counter++;
		}
	}
	sumr /= 9;
	sumg /= 9;
	sumb /= 9;
			
	currentPixel.x = sumr;
	currentPixel.y = sumg;
	currentPixel.z = sumb;
		
	outputPixels[centerIndex] = convert_uchar4_sat_rte(currentPixel);					

}