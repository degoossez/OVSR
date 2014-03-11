package com.denayer.ovsr;


import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.*;


public class RsScript extends Object {

	public Bitmap inBitmap = null;
	public Bitmap outBitmap = null;
	public Context mContext;
	public float saturationValue = 0;
	public ImageButton outputButton;
	
	
	public RsScript(Context ctxt, ImageButton outImageButton) {
		
	   mContext = ctxt;
	   outputButton = outImageButton;
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
	
	public void RenderScriptBlur()
	{
		if(inBitmap == null)
			return;
		
		Log.i("koen","inside RenderScriptBlur");
		
		float []filter = new float[]{1,1,1,1,1,1,1,1,1};
        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        
        ScriptC_blur script = new ScriptC_blur(rs);
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    script.set_filterC(filter);
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    script.invoke_filter();	
	    
	    output.copyTo(outBitmap);
		
	
	}
	
	public void RenderScriptSaturatie()
	{		
		if(inBitmap == null)
			return;
		
		final EditText input = new EditText(mContext);
		final Resources res = mContext.getResources();
		
		
		input.setText("50");
		
		//final String value;
		
		//only allow numeric values
		input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);        
        builder.setMessage("saturation value")
        	   .setView(input)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   String value = input.getText().toString();      
                	   saturationValue = Float.parseFloat(value);  
                	   outBitmap = saturate(inBitmap, saturationValue);
                	   outputButton.setImageBitmap(outBitmap);
                	   
                	   
                   }
               });
              
               
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();      
	}
	
	public Bitmap saturate(Bitmap bmIn, float saturation)
	{
		final RenderScript rs = RenderScript.create(mContext);
		
	    Bitmap bmOut = Bitmap.createBitmap(bmIn.getWidth(), bmIn.getHeight(),
	            bmIn.getConfig());
	    
	    Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, bmIn,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());
	    
	    ScriptC_saturation scriptSat = new ScriptC_saturation(rs);		
	    
	    scriptSat.set_in(allocIn);
	    scriptSat.set_out(allocOut);
	    scriptSat.set_script(scriptSat);
	    scriptSat.set_saturation(saturation);
	    //scriptSat.invoke_filter();	   
	    scriptSat.forEach_root(allocIn, allocOut);
	    allocOut.copyTo(bmOut);
	    
	   
	    return bmOut;
	}
	
}







