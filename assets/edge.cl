kernel void edgeKernel
(
    global const uint* inputPixels,
    global uint* outputPixels,
    const uint rowPitch,
    const uint width,
    const uint height
)
{
	const int edgeKernel[9] = {0,1,0,1,-4,1,0,1,0};
	// 2-dimensionale working q, 0 width, 1 height =>     size_t globalSize[2] = { bitmapInfo.width, bitmapInfo.height };

	int counter=0;
	int sumR=0;
	int sumG=0;
	int sumB=0;
	if(get_global_id(0) < 1 || get_global_id(0) > width - 2 || get_global_id(1) < 1 || get_global_id(1) > height - 2)
	{
		return;
	}
	
//	-1 -1	 0-1	 1 -1
//	-1 0	 0 0	 1 0
//	-1 1	 0 1     1 1

	
	int curPos; 
	int x=0;
	int y=0;
	for(x=-1;x<=1;x++)
	{
		/*for(y=-1;y<=1;y++)
		{
			curPos = inputPixels[(get_global_id(0)+x) + (get_global_id(1)+y)*rowPitch];
			sumR += ((curPos >> 16) & 0xff)*edgeKernel[0+counter];
			sumG += ((curPos >> 8) & 0xff)*edgeKernel[0+counter];
			sumB += (curPos & 0xff )*edgeKernel[0+counter];		
			counter++;
		}*/
		curPos = inputPixels[(get_global_id(0)+x) + (get_global_id(1)-1)*rowPitch];
		sumR += ((curPos >> 16) & 0xff)*edgeKernel[0+counter];
		sumG += ((curPos >> 8) & 0xff)*edgeKernel[0+counter];
		sumB += (curPos & 0xff )*edgeKernel[0+counter];
		curPos = inputPixels[(get_global_id(0)+x) + (get_global_id(1))*rowPitch];
		sumR += ((curPos >> 16) & 0xff)*edgeKernel[1+counter];
		sumG += ((curPos >> 8) & 0xff)*edgeKernel[1+counter];
		sumB += (curPos & 0xff )*edgeKernel[1+counter];
		curPos = inputPixels[(get_global_id(0)+x) + (get_global_id(1)+1)*rowPitch];
		sumR += ((curPos >> 16) & 0xff)*edgeKernel[2+counter];
		sumG += ((curPos >> 8) & 0xff)*edgeKernel[2+counter];
		sumB += (curPos & 0xff )*edgeKernel[2+counter];
		counter++;
	}
	
	if(sumR>255) sumR=255;
	if(sumG>255) sumG=255;
	if(sumB>255) sumB=255;
	if(sumR<0) sumR=0;
	if(sumG<0) sumG=0;
	if(sumB<0) sumB=0;	
	sumB = sumG = sumR;
	//RGB to ARGB
	outputPixels[get_global_id(0) + get_global_id(1)*rowPitch] = 0xff000000 | (sumR << 16) | (sumG << 8) | sumB;
}