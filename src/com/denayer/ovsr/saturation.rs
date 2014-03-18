#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

#include "rs_time.rsh"
#include "rs_core.rsh"
#include "rs_allocation.rsh"

rs_allocation out;
rs_allocation in;
rs_allocation timeAlloc;
rs_script script;
float saturation;

void init(){

}

void root(const uchar4* v_in, uchar4* v_out, const void* usrData, uint32_t x,
      uint32_t y)
{
    float4 current = rsUnpackColor8888(*v_in);   
    
    
    //rsDebug("Saturation before = ", saturation);
    
    float Pr = 0.299f;
    float Pg = 0.587f;
    float Pb = 0.114f;
    
	float comp =  (current.r)*(current.r)*Pr+
  (current.g)*(current.g)*Pg+
  (current.b)*(current.b)*Pb;
    
    float  P=sqrt(comp) ;

  current.r=P+((current.r)-P)*saturation;
  current.g=P+((current.g)-P)*saturation;
  current.b=P+((current.b)-P)*saturation;
    

    *v_out = rsPackColorTo8888(current.r, current.g, current.b, current.a);
}

void filter()
{
	rs_time_t t;
	rs_time_t runtime = 0;
	
	t = rsUptimeNanos();
	
    rsDebug("RS_VERSION = ", RS_VERSION);
    #if !defined(RS_VERSION) || (RS_VERSION < 14)
        rsForEach(script, in, out, 0);
    #else
        rsForEach(script, in, out);
    #endif
    
    runtime = rsUptimeNanos() - t;
    rsDebug("Saturation elapsed time = ", runtime);
    //rsSendToClient(1,  &runtime, sizeof(runtime));
    
     
}


