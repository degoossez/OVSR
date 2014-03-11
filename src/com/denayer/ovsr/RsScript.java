package com.denayer.ovsr;



import android.util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;


public class RsScript extends Object {

	public Bitmap inBitmap = null;
	public Bitmap outBitmap = null;
	public Context mContext;
	
	public RsScript(Context ctxt) {
		
	   mContext = ctxt;
	}
	
	
	public void setInputBitmap(Bitmap in)
	{
		inBitmap = in;
		outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(),
	            inBitmap.getConfig());
	}
	
	public Bitmap getOutputBitmap()
	{
		return outBitmap;
	}
	
	public void RenderScriptEdge()
	{
		if(inBitmap == null)
			return;
		
		Log.i("koen","inside RenderScriptEdge");
		
		float []filter = new float[]{0,-1,0,-1,4,-1,0,-1,0};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_edgedetection script = new ScriptC_edgedetection(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    
	    output.copyTo(outBitmap);
		
	}
	
	public void RenderScriptInverse()
	{
		if(inBitmap == null)
			return;
		
		final RenderScript rs = RenderScript.create(mContext);
		Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, inBitmap,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());	    
	    ScriptC_inverse script = new ScriptC_inverse(rs);
	    
	    script.set_in(allocIn);
	    script.set_out(allocOut);
	    script.set_script(script);
	    
	    //script.invoke_filter();	   
	    script.forEach_root(allocIn, allocOut);
	    allocOut.copyTo(outBitmap);
	    
		
	}	
	
	public void RenderScriptSharpen()
	{
		if(inBitmap == null)
			return;
		
		Log.i("koen","inside RenderScriptSharpen");
		
		float []filter = new float[]{0,-1,0,-1,5,-1,0,-1,0};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_sharpen script = new ScriptC_sharpen(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    
	    output.copyTo(outBitmap);
		
	}
	
	
}







