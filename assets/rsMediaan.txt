#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

#include "rs_types.rsh"

static void bubble_sort(float[], float);

rs_allocation out;
rs_allocation in;
rs_script script;

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
	float pixelListR[25];
	float pixelListG[25];
	float pixelListB[25];
		
	for(float i=-2;i<=2;i++)
	{		
	
		for(float j=-2;j<=2;j++)
		{
			
		
			pixel = rsUnpackColor8888(*(uchar4*) rsGetElementAt(in, x+j, y+i));
			pixel.r *= 255.0f;		
			pixel.g *= 255.0f;
			pixel.b *= 255.0f;	
			
			pixelListR[counter] = pixel.r;
			pixelListG[counter] = pixel.g;
			pixelListB[counter] = pixel.b;				
			
			counter++;		
		
		}		
			
	}	
	
	bubble_sort(pixelListR, 25);
	bubble_sort(pixelListG, 25);
	bubble_sort(pixelListB, 25);

    *v_out = rsPackColorTo8888(pixelListR[12]/255.0f,pixelListG[12]/255.0f,pixelListB[12]/255.0f);
    
  
}

void bubble_sort(float list[], float n)
{
  long c, d, t;
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

void filter()
{
    rsDebug("RS_VERSION = ", RS_VERSION);
    #if !defined(RS_VERSION) || (RS_VERSION < 14)
        rsForEach(script, in, out, 0);
    #else
        rsForEach(script, in, out);
    #endif
    
}