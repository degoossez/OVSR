__kernel void sharpenKernel(__read_only  image2d_t  srcImage,
                          __write_only image2d_t  dstImage)
{ 
    const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |
                               CLK_ADDRESS_CLAMP_TO_EDGE  |
                               CLK_FILTER_NEAREST;   
    int x = get_global_id(0);
    int y = get_global_id(1);
	int2 coords = (int2) (x,y);
	int2 curCoords = (int2) (x,y);
	int i = 0;
	int j = 0;          
	float4 bufferPixel,curPix;
	float sumr,sumg,sumb;
	sumr = 0.0f;
	sumg = 0.0f;
	sumb = 0.0f;
	int counter = 0;		
        const float sharpenKernel[9] = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.0f,-1.0f,0.0f};
		curPix = read_imagef(srcImage,sampler,coords);

	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	    
	    sumr = mad(bufferPixel.x,sharpenKernel[counter],sumr);
	    sumg = mad(bufferPixel.y,sharpenKernel[counter],sumg);
	    sumb = mad(bufferPixel.z,sharpenKernel[counter],sumb);
	    
	    counter++;
		}
	}
	if(sumr>1) sumr=1.0f;
	if(sumr<0) sumr=0.0f;
	if(sumg>1) sumg=1.0f;
	if(sumg<0) sumg=0.0f;
	if(sumb>1) sumb=1.0f;
	if(sumb<0) sumb=0.0f;	
		
	curPix.x = sumr;
	curPix.y = sumg;
	curPix.z = sumb;
			
	write_imagef(dstImage,curCoords,curPix);

}
/* kernel void sharpenKernel
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
    const float sharpenKernel[9] = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.0f,-1.0f,0.0f};
	currentPixel = convert_float4(inputPixels[centerIndex]);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		centerIndex = (y+i) * width + (x+j);
	    bufferPixel = convert_float4(inputPixels[centerIndex]);
	   	sumr = sumr + (bufferPixel.x * sharpenKernel[counter]);
	    sumg = sumg + (bufferPixel.y * sharpenKernel[counter]);
	    sumb = sumb + (bufferPixel.z * sharpenKernel[counter]);
	    counter++;
		}
	}
	if(sumr>255) sumr=255;
	if(sumr<0) sumr=0;
	if(sumg>255) sumg=255;
	if(sumg<0) sumg=0;
	if(sumb>255) sumb=255;
	if(sumb<0) sumb=0;	
		
	currentPixel.x = sumr;
	currentPixel.y = sumg;
	currentPixel.z = sumb;
		
	outputPixels[centerIndex] = convert_uchar4_sat_rte(currentPixel);					
}*/