/*kernel void saturatieKernel(__global uchar4 *srcBuffer,
                                    __global uchar4 *dstBuffer, 
                                    const uint rowPitch,
                                    const int width, 
                                    const int height,
                                    float saturatie)
{
	//const float saturatie = 50;
    int xCoor = get_global_id(0);
    int yCoor = get_global_id(1);
    int centerIndex = yCoor * width + xCoor;

    float4  currentPixel = (float4)0.0f;
    int counter=0;
    const float edgeKernel[9] = {0.0f,1.0f,0.0f,1.0f,-4.0f,1.0f,0.0f,1.0f,0.0f};
		
	currentPixel = convert_float4(srcBuffer[centerIndex]);

    float Pr = 0.299f;
    float Pg = 0.587f;
    float Pb = 0.114f;
    
	float comp =  (currentPixel.x)*(currentPixel.x)*Pr+(currentPixel.y)*(currentPixel.y)*Pg+(currentPixel.z)*(currentPixel.z)*Pb;
    float  P=sqrt(comp);

	saturatie = saturatie / 100;

	currentPixel.x=P+((currentPixel.x)-P)*saturatie;
	currentPixel.y=P+((currentPixel.y)-P)*saturatie;
	currentPixel.z=P+((currentPixel.z)-P)*saturatie;
    
	dstBuffer[centerIndex] = convert_uchar4_sat_rte(currentPixel);
}*/
__kernel void saturatieKernel(__read_only  image2d_t  srcImage,
                              __write_only image2d_t  dstImage,
                              const float saturatie)
{ 
    const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE |
                               CLK_ADDRESS_REPEAT        |
                               CLK_FILTER_NEAREST;
     int x = get_global_id(0);
     int y = get_global_id(1);
     int2 coords = (int2) (x,y);

    float4  currentPixel = (float4)0.0f;

	currentPixel = read_imagef(srcImage,sampler,coords);

    float Pr = 0.299f;
    float Pg = 0.587f;
    float Pb = 0.114f;
    
	float comp =  (currentPixel.x)*(currentPixel.x)*Pr+(currentPixel.y)*(currentPixel.y)*Pg+(currentPixel.z)*(currentPixel.z)*Pb;
    float  P=sqrt(comp);

	//saturatie = saturatie / 100;

	currentPixel.x=P+((currentPixel.x)-P)*saturatie;
	currentPixel.y=P+((currentPixel.y)-P)*saturatie;
	currentPixel.z=P+((currentPixel.z)-P)*saturatie;
    
	write_imagef(dstImage,coords,currentPixel);
}