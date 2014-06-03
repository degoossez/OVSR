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
	int counter;
    const float sharpenKernel[9] = {0.0f,-1.0f,0.0f,-1.0f,5.0f,-1.0f,0.0f,-1.0f,0.0f};
	counter = 0;	
	curPix = (float4)(0.0f, 0.0f, 0.0f, 0.0f);

	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	    curPix += bufferPixel * sharpenKernel[counter];    
	    counter++;
		}
	}
			
	curPix.w = 1.0f;
			
	write_imagef(dstImage,curCoords,curPix);

}