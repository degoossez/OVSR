#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

rs_allocation out;
rs_allocation in;
rs_script script;


void init(){

}

void root(const uchar4* v_in, uchar4* v_out, const void* usrData, uint32_t x,
      uint32_t y)
{
    float4 current = rsUnpackColor8888(*v_in);

	//rsDebug("Rood voor = ", current.r);

    current.r = 1 - current.r;
    current.g = 1 - current.g;
    current.b = 1 - current.b;
    
    //rsDebug("Rood na = ", current.r);
    

    *v_out = rsPackColorTo8888(current.r, current.g, current.b, current.a);
    
  
}

void filter()
{
    /*rsDebug("RS_VERSION = ", RS_VERSION);
    #if !defined(RS_VERSION) || (RS_VERSION < 14)
        rsForEach(script, in, out, 0);
    #else
        rsForEach(script, in, out);
    #endif*/
   
}