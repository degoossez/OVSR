kernel void saturatieKernel(__global uchar4 *srcBuffer,
                                    __global uchar4 *dstBuffer, 
                                    const uint rowPitch,
                                    const int width, 
                                    const int height,
                                    const float saturatie)
{
	//const float saturatie = 50;
    int xCoor = get_global_id(0);
    int yCoor = get_global_id(1);
    int centerIndex = yCoor * width + xCoor;

    float4  currentPixel = (float4)0.0f;
    int counter=0;
    const float edgeKernel[9] = {0.0f,1.0f,0.0f,1.0f,-4.0f,1.0f,0.0f,1.0f,0.0f};
		
	currentPixel = convert_float4(srcBuffer[centerIndex]);
	float t = (currentPixel.x*0.11f)+(currentPixel.y*0.59f)+(currentPixel.z*0.3f);
	currentPixel.x = fmin(currentPixel.x+(t-currentPixel.x) * (saturatie/100.0f) , 255.f);
	currentPixel.y = fmin(currentPixel.y+(t-currentPixel.y) * (saturatie/100.0f) , 255.f);
	currentPixel.z = fmin(currentPixel.z+(t-currentPixel.z) * (saturatie/100.0f) , 255.f);
	
	dstBuffer[centerIndex] = convert_uchar4_sat_rte(currentPixel);
}
