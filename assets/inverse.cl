__kernel void inverseKernel(__read_only  image2d_t  srcImage,
                          __write_only image2d_t  dstImage)
{ 
    const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |
                               CLK_ADDRESS_CLAMP_TO_EDGE  |
                               CLK_FILTER_NEAREST;
     int x = get_global_id(0);
     int y = get_global_id(1);
     int2 coords = (int2) (x,y);

    float4 centerPixel = read_imagef(srcImage,sampler,coords);
    centerPixel.x = 1.0f-centerPixel.x;
    centerPixel.y = 1.0f-centerPixel.y;
    centerPixel.z = 1.0f-centerPixel.z;
    write_imagef(dstImage,coords,centerPixel);	
}