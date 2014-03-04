kernel void inverseKernel
(
    global const int* inputPixels,
    global int* outputPixels,
    const uint rowPitch
)
{
	// 2-dimensionale working q, 0 width, 1 height =>     size_t globalSize[2] = { bitmapInfo.width, bitmapInfo.height };
    int x = get_global_id(0);
    int y = get_global_id(1);
	int inPixel = inputPixels[x + y*rowPitch];
	int outPixel;
	
	//ARGB to RGB
	int R = (inPixel >> 16) & 0xff;
	int G = (inPixel >> 8) & 0xff;
	int B = inPixel & 0xff;
	R=255-R;
	G=255-G;
	B=255-B;

	//RGB to ARGB
    outputPixels[x + y*rowPitch] = 0xff000000 | (R << 16) | (G << 8) | B;
	
	//outPixel = inPixel;
	//outputPixels[x + y*rowPitch] = outPixel;
	

}