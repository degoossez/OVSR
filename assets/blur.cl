kernel void blurKernel(__global uchar4 *srcBuffer,
                                    __global uchar4 *dstBuffer, 
                                    const uint rowPitch,
                                    const int width, 
                                    const int height)
{
    int xCoor = get_global_id(0);
    int yCoor = get_global_id(1);
    int centerIndex = yCoor * width + xCoor;
    float4  currentPixel = (float4)0.0f;
    float4  convPixel = (float4)0.0f;
    int counter=0;
    const float edgeKernel[9] = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
		
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[0];
	convPixel.y += currentPixel.y*edgeKernel[0];
	convPixel.z += currentPixel.z*edgeKernel[0];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[1];
	convPixel.y += currentPixel.y*edgeKernel[1];
	convPixel.z += currentPixel.z*edgeKernel[1];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[2];
	convPixel.y += currentPixel.y*edgeKernel[2];
	convPixel.z += currentPixel.z*edgeKernel[2];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[3];
	convPixel.y += currentPixel.y*edgeKernel[3];
	convPixel.z += currentPixel.z*edgeKernel[3];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[4];
	convPixel.y += currentPixel.y*edgeKernel[4];
	convPixel.z += currentPixel.z*edgeKernel[4];
	convPixel.w = currentPixel.w;
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[5];
	convPixel.y += currentPixel.y*edgeKernel[5];
	convPixel.z += currentPixel.z*edgeKernel[5];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[6];
	convPixel.y += currentPixel.y*edgeKernel[6];
	convPixel.z += currentPixel.z*edgeKernel[6];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[7];
	convPixel.y += currentPixel.y*edgeKernel[7];
	convPixel.z += currentPixel.z*edgeKernel[7];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[8];
	convPixel.y += currentPixel.y*edgeKernel[8];
	convPixel.z += currentPixel.z*edgeKernel[8];

	convPixel.x /= 9;
	convPixel.y /= 9;
	convPixel.z /= 9;
	
	dstBuffer[centerIndex] = convert_uchar4_sat_rte(convPixel);
}