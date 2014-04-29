__kernel void blurKernel(__read_only  image2d_t  srcImage,
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
	float sumr,sumg,sumb;
	sumr = sumg = sumb = 0;
	int counter = 0;
    const float blurKernel[9] = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
	currentPixel = read_imagef(srcImage,sampler,coords);
	for(i=-1;i<=1;i++)
	{
		for(j=-1;j<=1;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	   	sumr = sumr + (bufferPixel.x * blurKernel[counter]);
	    sumg = sumg + (bufferPixel.y * blurKernel[counter]);
	    sumb = sumb + (bufferPixel.z * blurKernel[counter]);
	    counter++;
		}
	}
	sumr /= 9;
	sumg /= 9;
	sumb /= 9;
			
	currentPixel.x = sumr;
	currentPixel.y = sumg;
	currentPixel.z = sumb;
		
	write_imagef(dstImage,coords,currentPixel);	
}

