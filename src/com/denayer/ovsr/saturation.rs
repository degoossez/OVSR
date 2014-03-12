#pragma version(1)
#pragma rs java_package_name(com.denayer.ovsr)

#include "rs_time.rsh"

rs_allocation out;
rs_allocation in;
rs_script script;
float saturation;

void init(){

}

void root(const uchar4* v_in, uchar4* v_out, const void* usrData, uint32_t x,
      uint32_t y)
{
    float4 current = rsUnpackColor8888(*v_in);

    float t = current.r * 0.11f + current.g * 0.59f + current.b * 0.3f;
    //rsDebug("renderscript t = ", t);
    //rsDebug("renderscript red before = ", current.r);
    current.r = fmin(current.r + (t - current.r) * saturation / 100.0f, 1.f);
    current.g = fmin(current.g + (t - current.g) * saturation / 100.0f, 1.f);
    current.b = fmin(current.b + (t - current.b) * saturation / 100.0f, 1.f);
    //rsDebug("renderscript red after = ", current.r);

    *v_out = rsPackColorTo8888(current.r, current.g, current.b, current.a);
}

void filter()
{
	int64_t t;
	
	t = rsUptimeNanos();
	
    rsDebug("RS_VERSION = ", RS_VERSION);
    #if !defined(RS_VERSION) || (RS_VERSION < 14)
        rsForEach(script, in, out, 0);
    #else
        rsForEach(script, in, out);
    #endif
    
    int64_t runtime = rsUptimeNanos() - t;
    rsDebug("Saturation elapsed time = ", runtime);
   
}