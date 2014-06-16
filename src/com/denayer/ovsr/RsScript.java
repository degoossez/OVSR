/*
 * Copyright (C) <2014> <Dries Goossens / driesgoossens93@gmail.com , Koen Daelman / koendaelman@gmail.com >
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
*/
package com.denayer.ovsr;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
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
	public float saturationValue = -1;
	public ImageView outputButton;
	public TextView mElapsedTime;
	public MainActivity MmainThread;
	static LogFile LogFileObject; 
	 /*! \brief Constructor
	 *
     * 
     * @param mActiv handle to the MainActivity necessary for the creation of RenderScript objects. 
     * @param imageView The image view from the MainActivity where the resulting image is displayed
     * @param view here execution times are displayed
     */
	public RsScript(MainActivity mActiv, ImageView imageView, TextView view) {
		
	   mContext = mActiv;	//needed by renderscript
	   MmainThread = mActiv;	//needed for updating UI components from a subthread
	   outputButton = imageView;
	   mElapsedTime = view;
	   LogFileObject = new LogFile(mContext); 	   
	}
	
	/*! \brief funtion to set the input bitmap 
	*
    * This funcion sets the input bitmap that is used in this class for the image processing.
    * The output bitmap used for storing the result after filter execution, is also created
    * with the same dimensions as the input bitmap.
    * @param in The bitmap data
    *  
    */
	public void setInputBitmap(Bitmap in)
	{
		inBitmap = in;
		outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), Bitmap.Config.ARGB_8888);
	}
	/*! \brief returns the output bitmap	
    * 
    * @return outBitmap the output bitmap
    */
	public Bitmap getOutputBitmap()
	{
		return outBitmap;
	}
	
	/*! \brief executes an Edge Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */
	public void RenderScriptEdge()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
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
	    rs.finish();
	    output.copyTo(outBitmap);
	    	
        input.destroy();
        output.destroy();
        rs.destroy();
        script.destroy();
        
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Edge",estimatedTime);
	}
	
	/*! \brief executes an inverse Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */
	public void RenderScriptInverse()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
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
	    
	    script.invoke_filter();	   
	    //script.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(outBitmap);
	        
        allocOut.destroy();
        allocIn.destroy();
        rs.destroy();
        script.destroy();
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Inverse",estimatedTime);
	}	
	
	/*! \brief executes a sharpen Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */
	public void RenderScriptSharpen()
	{
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
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
	    rs.finish();
	    
	    output.copyTo(outBitmap);
	    
        output.destroy();
        input.destroy();
        rs.destroy();
        script.destroy();
        
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Sharpen",estimatedTime);
	}
	
	/*! \brief executes a blur Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */	
	public void RenderScriptBlur()
	{
		if(inBitmap == null)
			return;
		long startTime = System.nanoTime(); 
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
	    rs.finish();
	    output.copyTo(outBitmap);
	    	
        output.destroy();
        input.destroy();
        rs.destroy();
        script.destroy();
	
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Blur",estimatedTime);
	}
	
	/*! \brief creates the seekbar for choosing a saturation value
    *  
    */
	public void RenderScriptSaturatie()
	{		
		if(inBitmap == null)
			return;		      
        if(saturationValue==-1){
        final TextView progressView = new TextView(mContext);
		final Resources res = mContext.getResources();
		final SeekBar MySeekBar = new SeekBar(mContext);
		MySeekBar.setMax(200);

		MySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
			   @Override 
			   public void onProgressChanged(SeekBar seekBar, int progress, 
			     boolean fromUser) { 
			    //  Auto-generated method stub 
				   progressView.setText(String.valueOf(progress)); 
			   } 
			   @Override 
			   public void onStartTrackingTouch(SeekBar seekBar) { 
			    //  Auto-generated method stub 
			   } 
			   @Override 
			   public void onStopTrackingTouch(SeekBar seekBar) { 
			    //  Auto-generated method stub 
			   } 
			       }); 
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);        
        builder.setMessage("saturation value")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {      
                	   saturationValue = MySeekBar.getProgress();  
                	   outBitmap = saturate(inBitmap, saturationValue);
                	   outputButton.setImageBitmap(outBitmap);
                	   saturationValue=-1;
                   }
               });
        progressView.setGravity(1 | 0x10);
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
	     LinearLayout ll=new LinearLayout(mContext);
	        ll.setOrientation(LinearLayout.VERTICAL);
	        ll.addView(MySeekBar);
	        ll.addView(progressView);
	        dialog.setView(ll);
        dialog.show(); 
        } else {
     	   outBitmap = saturate(inBitmap, saturationValue);
        }
	}
	
	/*! \brief executes a saturation Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */
	public Bitmap saturate(Bitmap bmIn, float saturation)
	{
		
	    long startTime = System.nanoTime(); 

		final RenderScript rs = RenderScript.create(mContext);	
		
		
	    Bitmap bmOut = Bitmap.createBitmap(bmIn.getWidth(), bmIn.getHeight(),
	            bmIn.getConfig());
	    
	    Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, bmIn,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());	    
	    
	    ScriptC_saturation scriptSat = new ScriptC_saturation(rs);	
	    
	    Log.i("koen", "saturation value = " + String.valueOf(saturation));
	    
	    scriptSat.set_in(allocIn);
	    scriptSat.set_out(allocOut);
	    scriptSat.set_script(scriptSat);
	    scriptSat.set_saturation(saturation/100);	    
	    
	    scriptSat.invoke_filter();	   
	    //scriptSat.forEach_root(allocIn, allocOut);
	    rs.finish();
	    allocOut.copyTo(bmOut);
	       
        allocIn.destroy();
        allocOut.destroy();
        rs.destroy();
        scriptSat.destroy();
       
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Saturation",estimatedTime);
        
	    return bmOut;
	}
	
	/*! \brief executes a mediaan Filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    *  
    */
	public void RenderScriptMediaan()
	{
		
		if(inBitmap == null)
			return;
				
		long startTime = System.nanoTime(); 
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
	    
	    
	    script.invoke_filter();	
	    rs.finish();
	    output.copyTo(outBitmap);
	    		
        input.destroy();
        output.destroy();
        rs.destroy();
        script.destroy();
        
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Median",estimatedTime);
	}
	
	/*! \brief executes a user defined filter on the input image
	*
    * A Renderscript context object is created to handle the lifetime of all other RenderScript objects.
    * The necessary memory is allocated for the computations, and to write back the result.
    * Global script variables are set and the script starts execution.
    * When complete, all objects are destroyed to free the memory.
    * 
    * Because the filter bytecode can change during app execution, the location of the bytecode is not from inside the 
    * APK but from a controlled location in the apps private memory. To inform the Renderscript API about this new
    * location, our own Resource object is passed to the script at script creation.
    *  
    */
	public void RenderScriptTemplate()
	{
		
		if(inBitmap == null)
			return;
	    long startTime = System.nanoTime(); 
		
		final RenderScript rs = RenderScript.create(mContext);
		Allocation allocIn;
	    allocIn = Allocation.createFromBitmap(rs, inBitmap,
	            Allocation.MipmapControl.MIPMAP_NONE,
	            Allocation.USAGE_SCRIPT);
	    Allocation allocOut = Allocation.createTyped(rs, allocIn.getType());
	    
	    MyResources myRes = new MyResources(mContext.getResources().getAssets(), mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());
	    myRes.setMyContext(mContext);
	    Resources hackedResources = myRes;	    
	    
	    ScriptC_template script = new ScriptC_template(rs,hackedResources,rs.getApplicationContext().getResources().getIdentifier(
                "template", "raw",rs.getApplicationContext().getPackageName()));	    
	    
	    
	    script.set_in(allocIn);
	    script.set_out(allocOut);
	    script.set_script(script);
	    
	    script.invoke_filter();	   	  
	    rs.finish();
	    allocOut.copyTo(outBitmap);
	    
        allocIn.destroy();
        allocOut.destroy();
        rs.destroy();
        script.destroy();
        
	    long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
        setTimeToLog(estimatedTime);
        
        setHistory("Runtime compiled",estimatedTime);
	}
	
	/*! \brief returns the RenderScript Template
	*
    * returns the RenderScript template used as a starting point for user defined scripts.
    * 
    *  @return template the template in String format
    */
	public String getTemplate()
	{
		String template = null;
		try {
			InputStream in = mContext.getAssets().open("templateRenderscript.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";
			StringBuilder stringBuilder = new StringBuilder();

			while ( (receiveString = bufferedReader.readLine()) != null ) {
				stringBuilder.append(receiveString).append("\n");
			}
			in.close();
			template = stringBuilder.toString();
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
			e.printStackTrace();
		}
		
		return template;
	}
	/*! \brief Returns the source code of the specified filter
	*
	* @param filtername The name of the filter
    * @return code The code of the specified filter
    */
	public String getFilterCode(String filterName)
	{
		String code = "";
		String file = "rs" + filterName + ".txt";
		
		try {
			InputStream in = mContext.getAssets().open(file);
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";
			StringBuilder stringBuilder = new StringBuilder();

			while ( (receiveString = bufferedReader.readLine()) != null ) {
				stringBuilder.append(receiveString).append("\n");
			}
			in.close();
			code = stringBuilder.toString();
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
			e.printStackTrace();
		}
		
		return code;
	}
	/*! \brief The setTimeToLog function sets a value to the GUI in the log window.
	 *
	 *@param time is the time that has to be placed in the log field
	 */
	public void setTimeToLog(long time)
	{
		mElapsedTime.setText(String.valueOf(time) + " ms" + "\n" + "Resolution: " + inBitmap.getWidth() + " x " + inBitmap.getHeight());

	}	
	/*! \brief The setTimeToLog function adds a line with information to the history file
	 *
	 *@param filterName the name of the executed filter
	 *@param time the time needed to execute the filter
	 */
	public void setHistory(String filterName,long time)
	{
        String Method="RenderScript";
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
		Date now = new Date();
		String fileName = "RenderScript/" + filterName + formatter.format(now);
		LogFileObject.writeToFile("\n" + Method + " : " + fileName + " : " + String.valueOf(time) + " ms", "LogFile.txt",false);
	}
}








