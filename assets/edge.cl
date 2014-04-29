__kernel void edgeKernel(__read_only  image2d_t  srcImage,
                          __write_only image2d_t  dstImage)
{    
    const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE |
                               CLK_ADDRESS_REPEAT         |
                               CLK_FILTER_NEAREST;
    int x = get_global_id(0);
    int y = get_global_id(1);
	int2 coords = (int2) (x,y);
	
	int i = 0;
	int j = 0;
	float4 bufferPixel;
	float4 currentPixel;
	float sum = 0;
	int counter = 0;
	const float edgeKernel[9] = {0.0f,1.0f,0.0f,1.0f,-4.0f,1.0f,0.0f,1.0f,0.0f};
	currentPixel = read_imagef(srcImage,sampler,coords);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	    sum = sum + (bufferPixel.y * edgeKernel[counter]);
	    counter++;
		}
	}
	if(sum>255) sum=255;
	if(sum<0) sum=0;
	
	currentPixel.x=sum;
	currentPixel.y=sum;
	currentPixel.z=sum;
	
	write_imagef(dstImage,coords,currentPixel);	                          

}
/*kernel void edgeKernel
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
	float sum = 0;
	int counter = 0;
	const float edgeKernel[9] = {0.0f,1.0f,0.0f,1.0f,-4.0f,1.0f,0.0f,1.0f,0.0f};
	currentPixel = convert_float4(inputPixels[centerIndex]);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		centerIndex = (y+i) * width + (x+j);
	    bufferPixel = convert_float4(inputPixels[centerIndex]);
	    sum = sum + (bufferPixel.y * edgeKernel[counter]);
	    counter++;
		}
	}
	if(sum>1) sum=1;
	if(sum<0) sum=0;
	
	currentPixel.x=sum;
	currentPixel.y=sum;
	currentPixel.z=sum;
	
	outputPixels[centerIndex] = convert_uchar4_sat_rte(currentPixel);					

} */
