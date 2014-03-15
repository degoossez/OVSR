/*__kernel void sharpenKernel(__read_only  image2d_t  srcImage,
                          __write_only image2d_t  dstImage)
{    
    const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE |
                               CLK_ADDRESS_REPEAT         |
                               CLK_FILTER_NEAREST;

        int2 coords = (int2)(get_global_id(0), get_global_id(1));       
		float4 curPix = read_imagef(srcImage,sampler,coords);
		write_imagef(dstImage,coords,curPix);
	

}*/

kernel void sharpenKernel(__global uchar4 *srcBuffer,
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
    const float edgeKernel[9] = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.0f,-1.0f,0.0f};
		
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

	dstBuffer[centerIndex] = convert_uchar4_sat_rte(convPixel);
}
/*
__kernel void sharpenKernel(const __global  int * pInput,
                        __global  int * pOutput,
                        const uint rowPitch,
                        const int width,
                        const int height)
{
	const int nInWidth = width;

	const int pFilter[9] = {0,1,0,1,-4,1,0,1,0};
	//const int pFilter[9] = {0,0,0,0,0,0,0,0,0};
	const int nFilterWidth = 3;
    const int nWidth = get_global_size(0);

    const int xOut = get_global_id(0);
    const int yOut = get_global_id(1);
	
	const int xInTopLeft = xOut;
    const int yInTopLeft = yOut;

    int sum = 0;
    int sumR,sumG,sumB;
    sumR=0;
    sumG=0;
    sumB=0;
    		int R,G,B;
    for (int r = 0; r < nFilterWidth; r++)
    {
        const int idxFtmp = r * nFilterWidth; //1st: 0 , 2e: 3 , 3e: 6

        const int yIn = yInTopLeft + r;
        const int idxIntmp = yIn * nInWidth + xInTopLeft; //index in temp

        for (int c = 0; c < nFilterWidth; c++)
        {
            const int idxF  = idxFtmp  + c; // r * nFilterWidth + c => zelfde als counter (plaats in de filter array bepalen)
            const int idxIn = idxIntmp + c; // plaats in de afbeelding bepalen
            //sum += pFilter[idxF]*pInput[idxIn];
            	 R = (pInput[idxIn] >> 16) & 0xff;
				 G = (pInput[idxIn] >> 8) & 0xff;
				 B = pInput[idxIn] & 0xff;
				 sumR += pFilter[idxF]*R;
				 sumG += pFilter[idxF]*G;
				 sumB += pFilter[idxF]*B;
        }
    }
    if(sumR>255) sumR=255;
    if(sumG>255) sumG=255;
    if(sumB>255) sumB=255;
    const int idxOut = yOut * nWidth + xOut; //huidige pixel locatie
    pOutput[idxOut]=0xff000000 | (sumR << 16) | (sumG << 8) | sumB;
    //pOutput[idxOut] = sum;
}
*/