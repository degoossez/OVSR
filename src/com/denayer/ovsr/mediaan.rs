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
	float pixelListR[9];
	float pixelListG[9];
	float pixelListB[9];
	int counter = 0;
	
	
	float4 current = rsUnpackColor8888(*v_in);
			
	
	for(float i=-1;i<=1;i++)
	{		
	
		for(float j=-1;j<=1;j++)
		{
			
		
			pixel = rsUnpackColor8888(*(uchar*) rsGetElementAt(in, x+j, y+i));
			
			
			pixelListR[counter] = pixel.r * 255;	//collect surrounding pixels
			pixelListG[counter] = pixel.g * 255;
			pixelListB[counter] = pixel.b * 255;
			
			
			counter++;	
		
		}		
			
	}
	
	bubble_sort(pixelListR, 9);
	bubble_sort(pixelListG, 9);
	bubble_sort(pixelListB, 9);
	
	

	   

    *v_out = rsPackColorTo8888(pixelListR[4] / 255, pixelListG[4] / 255, pixelListB[4] / 255, current.a);
    
  
}

void bubble_sort(float list[], float n)
{
  long c, d, t;
 
  for (c = 0 ; c < ( n - 1 ); c++)
  {
    for (d = 0 ; d < n - c - 1; d++)
    {
      if (list[d] > list[d+1])
      {
        /* Swapping */
 
        t         = list[d];
        list[d]   = list[d+1];
        list[d+1] = t;
      }
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
   