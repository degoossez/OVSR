package com.denayer.ovsr;


import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
	public TextView mElapsedTime;
	public MainActivity MmainThread;
	
	
	public RsScript(MainActivity mActiv, ImageButton outImageButton, TextView view) {
		
	   mContext = mActiv;	//needed by renderscript
	   MmainThread = mActiv;	//needed for updating UI components from a subthread
	   outputButton = outImageButton;
	   mElapsedTime = view;
	   
	   
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
	    
	    long startTime = System.nanoTime(); 
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
		
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
	    
	    long startTime = System.nanoTime(); 
	    script.invoke_filter();	   
	    //script.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
	    
		
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
	    
	    long startTime = System.nanoTime(); 
	    script.invoke_filter();	
	    rs.finish();
	    
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
		
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
	    
	    long startTime = System.nanoTime(); 	    
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
		
	
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
		MyRsMessageHandler myHandler = new MyRsMessageHandler(mElapsedTime,MmainThread);
		rs.setMessageHandler(myHandler);
		
	    Bitmap bmOut = Bitmap.createBitmap(bmIn.getWidth(), bmIn.getHeight(),
	            bmIn.getConfig());
	    
	    Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, bmIn,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());
	    
	    Type t = new Type.Builder(rs, Element.I32(rs)).setX(1).create();
	    Allocation allocT = Allocation.createTyped(rs, t);
	    
	    ScriptC_saturation scriptSat = new ScriptC_saturation(rs);		
	    
	    scriptSat.set_in(allocIn);
	    scriptSat.set_out(allocOut);
	    scriptSat.set_script(scriptSat);
	    scriptSat.set_saturation(saturation);
	    scriptSat.set_timeAlloc(allocT);
	    
	    long startTime = System.nanoTime(); 
	    
	    scriptSat.invoke_filter();	   
	    //scriptSat.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(bmOut);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
	    
	    int [] time = new int[1];
	    allocT.copyTo(time);
	    
	    //mElapsedTime.setText(String.valueOf(time[0]) + " ns");
	   
	    return bmOut;
	}
	
	public void RenderScriptMediaan()
	{
		
		if(inBitmap == null)
			return;
		
		Log.i("koen","inside RenderScriptMediaan");        
		
        final RenderScript rs = RenderScript.create(mContext);
        final Allocation input = Allocation.createFromBitmap(rs, inBitmap,Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());        
        
        ScriptC_mediaan script = new ScriptC_mediaan(rs);      
	    
	    script.set_in(input);
	    script.set_out(output);
	    script.set_script(script);
	    
	    script.set_width(inBitmap.getWidth());
	    script.set_height(inBitmap.getHeight());
	    
	    long startTime = System.nanoTime(); 
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    
	    long estimatedTime = System.nanoTime() - startTime;
        Log.i("koen","via java meting: " + String.valueOf(estimatedTime));
        mElapsedTime.setText(String.valueOf(estimatedTime) + "ps");
		
	}
	
}









