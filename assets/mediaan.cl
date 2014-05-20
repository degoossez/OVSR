void bubble_sort(float list[], float n)
{
  int c, d;
  float t;
  
  for (c = 1 ; c <= n - 1; c++) {
    d = c;
 	
    while ( d > 0 && list[d] < list[d-1]) {
      t          = list[d];
      list[d]   = list[d-1];
      list[d-1] = t;
 
      d--;
    }
  }  
}

__kernel void mediaanKernel(__read_only  image2d_t  srcImage,
                          __write_only image2d_t  dstImage)
{    
    const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE |
                               CLK_ADDRESS_REPEAT         |
                               CLK_FILTER_NEAREST;

     int x = get_global_id(0);
     int y = get_global_id(1);
     int2 coords = (int2) (x,y);       

	int counter = 0;
	float pixelListR[25];
	float pixelListG[25];
	float pixelListB[25];


	int i = 0;
	int j = 0;
	
	float4 bufferPixel;
	
	for(i=-2;i<=2;i++)
	{
		for(j=-2;j<=2;j++)
		{
		coords = (int2)((x+i),(y+j));
	    bufferPixel = read_imagef(srcImage,sampler,coords);
	    
		pixelListR[counter] = bufferPixel.x;
		pixelListG[counter] = bufferPixel.y;
		pixelListB[counter] = bufferPixel.z;	

	    counter++;
		}
	}
	
	bubble_sort(pixelListR, 25);
	bubble_sort(pixelListG, 25);
	bubble_sort(pixelListB, 25);
		
	float4 result = { 0, 0, 0, 255 };
	result.x = pixelListR[12] ;	
	result.y = pixelListG[12] ;
	result.z = pixelListB[12] ;
	coords = (int2) (x,y);
	write_imagef(dstImage,coords,result);	
}

