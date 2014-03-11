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
	float sumr,sumg,sumb;
	sumr = sumg = sumb = 0;			
	
	for(float i=-1;i<=1;i++)
	{		
	
		for(float j=-1;j<=1;j++)
		{
			
		
			pixel = rsUnpackColor8888(*(uchar4*) rsGetElementAt(in, x+j, y+i));
			pixel.r *= 255.0f;		
			pixel.g *= 255.0f;
			pixel.b *= 255.0f;				
			
			sumr = sumr + (pixel.r * filterC[counter]);
			sumg = sumg + (pixel.g * filterC[counter]);			
			sumb = sumb + (pixel.b * filterC[counter]);
			
			
			counter++;		
		
		}		
			
	}
	
	//normalize values
	sumr /= 9;
	sumg /= 9;
	sumb /= 9;

	if(sumr > 255)
		sumr = 255;
	if(sumr < 0)
		sumr = 0;
	
	if(sumg > 255)
		sumg = 255;
	if(sumg < 0)
		sumg = 0;
	
	if(sumb > 255)
		sumb = 255;
	if(sumb < 0)
		sumb = 0;
	
		
	sumr /= 255.0f;
	sumg /= 255.0f;
	sumb /= 255.0f; 
	
	/*rsDebug("r = ",sumr);
	rsDebug("g = ",sumg);
	rsDebug("b = ", sumb);*/

    *v_out = rsPackColorTo8888(sumr, sumg, sumb);
    
  
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