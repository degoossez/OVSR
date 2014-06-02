__kernel void blurKernel(__read_only  image2d_t  srcImage,
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
	float4 bufferPixel;
	float4 currentPixel;
	float sumr,sumg,sumb;
	sumr = sumg = sumb = 0.0f;
	int counter = 0;
    const float blurKernel[9] = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
	currentPixel = read_imagef(srcImage,sampler,coords);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	    sumr = mad(bufferPixel.x,blurKernel[counter],sumr);
	    sumg = mad(bufferPixel.y,blurKernel[counter],sumg);
	    sumb = mad(bufferPixel.z,blurKernel[counter],sumb);
	    
	    counter++;
		}
	}
	sumr /= 9.0f;
	sumg /= 9.0f;
	sumb /= 9.0f;
			
	currentPixel.x = sumr;
	currentPixel.y = sumg;
	currentPixel.z = sumb;
		
	write_imagef(dstImage,curCoords,currentPixel);	
}

