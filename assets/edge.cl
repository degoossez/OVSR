kernel void edgeKernel(__global uchar4 *srcBuffer,
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
    const float edgeKernel[9] = {0.0f,1.0f,0.0f,1.0f,-4.0f,1.0f,0.0f,1.0f,0.0f};
		
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[0];
	convPixel.y += currentPixel.y *edgeKernel[0];
	convPixel.z += currentPixel.z*edgeKernel[0];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[1];
	convPixel.y += currentPixel.y *edgeKernel[1];
	convPixel.z += currentPixel.z*edgeKernel[1];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)-1) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[2];
	convPixel.y += currentPixel.y *edgeKernel[2];
	convPixel.z += currentPixel.z*edgeKernel[2];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[3];
	convPixel.y += currentPixel.y *edgeKernel[3];
	convPixel.z += currentPixel.z*edgeKernel[3];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[4];
	convPixel.y += currentPixel.y *edgeKernel[4];
	convPixel.z += currentPixel.z*edgeKernel[4];
	convPixel.w = currentPixel.w;
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[5];
	convPixel.y += currentPixel.y *edgeKernel[5];
	convPixel.z += currentPixel.z*edgeKernel[5];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1)-1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[6];
	convPixel.y += currentPixel.y *edgeKernel[6];
	convPixel.z += currentPixel.z*edgeKernel[6];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1))*width]);
	convPixel.x += currentPixel.x*edgeKernel[7];
	convPixel.y += currentPixel.y *edgeKernel[7];
	convPixel.z += currentPixel.z*edgeKernel[7];
	currentPixel = convert_float4(srcBuffer[(get_global_id(0)+1) + (get_global_id(1)+1)*width]);
	convPixel.x += currentPixel.x*edgeKernel[8];
	convPixel.y += currentPixel.y *edgeKernel[8];
	convPixel.z += currentPixel.z*edgeKernel[8];

	/*float avg = (convPixel.x + convPixel.x + convPixel.x )/3;
	//convPixel.x = convPixel.y = convPixel.z = avg;
	if(avg>75)
	{
		convPixel.x = convPixel.y = convPixel.z = 255;
	}  
	else
	{
		convPixel.x = convPixel.y = convPixel.z = avg;
	}*/
	convPixel.x = convPixel.y = convPixel.z;
	dstBuffer[centerIndex] = convert_uchar4_sat_rte(convPixel);
}
