#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

rs_allocation out;
rs_allocation in;
rs_script script;


void init(){

	//runs first time script is created
}

void root(const uchar4* v_in, uchar4* v_out, const void* usrData, uint32_t x,
      uint32_t y)
{
	//runs for each pixel in image
	
    float4 current = rsUnpackColor8888(*v_in);	

    current.r = current.r;
    current.g = current.g;
    current.b = current.b;    

    *v_out = rsPackColorTo8888(current.r, current.g, current.b, current.a);
    
  
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