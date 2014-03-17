#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

#include "rs_types.rsh"

rs_allocation out;
rs_allocation in;
rs_script script;

float filterC[9];
int width,height;

void init(){

}

void root(const uchar4* v_in, uchar4* v_out, const void* usrData, uint32_t x,
      uint32_t y)
{
	
	if( x == 0 || x == width - 1 || y == 0 || y == height - 1)
	{
		*v_out = *v_in;
		return;
	}
		
	
	float4 pixel = { 0, 0, 0, 0 };
	int counter = 0;
	
	float4 current = rsUnpackColor8888(*v_in);
	float sum = 0;			
	
	for(float i=-1;i<=1;i++)
	{		
	
		for(float j=-1;j<=1;j++)
		{
			
		
			pixel = rsUnpackColor8888(*(uchar*) rsGetElementAt(in, x+j, y+i));
			pixel.r *= 255;			
			sum = sum + ( pixel.r * filterC[counter]);
			
			counter++;		
		
		}		
			
	}
	
	

	if(sum > 255)
		sum = 255;
	if(sum < 0)
		sum = 0;
		
	sum /= 255;     

    *v_out = rsPackColorTo8888(sum, sum, sum, current.a);
    
  
}

void filter()
{
    rsDebug("RS_VERSION = ", RS_VERSION);
    #if !defined(RS_VERSION) || (RS_VERSION < 14)
        rsForEach(script, in, out, 0);
    #else
        rsForEach(script, in, out);
    #endif
    
}
   