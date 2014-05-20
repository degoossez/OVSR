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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class OpenCL extends Object {
	private Context mContext; //<-- declare a Context reference
	Bitmap bmpOrig, bmpOpenCL;
	float saturatie=-1;
	public ImageView outputButton;
	final int info[] = new int[3]; // Width, Height, Execution time (ms)
	static boolean sfoundLibrary = true;
	String kernelName = "";

	public OpenCL(Context context, ImageView imageView) {
    	mContext = context; //<-- fill it with the Context you passed
    	outputButton = imageView;
    	try { 
    		//Odroid lib
    		System.load("/system/vendor/lib/libPVROCL.so");
    		Log.i("Debug", "libPVROCL Loaded"); 
    	}
    	catch (UnsatisfiedLinkError e) {
    		sfoundLibrary = false;
    	}
    	if(sfoundLibrary==false)
    	{
	    	try { 
	    		System.load("/system/vendor/lib/egl/libGLES_mali.so");
	    		Log.i("Debug", "libGLES_mali loaded");
	    		sfoundLibrary=true;
	    	}
	    	catch (UnsatisfiedLinkError e) {
	    		sfoundLibrary = false;
	    	}
    	}
    	if(sfoundLibrary==false)
    	{
	    	try { 
	    		System.load("/system/lib/libOpenCL.so");
	    		Log.i("Debug", "libOpenCL Qualcomm loaded");
	    		sfoundLibrary=true;
	    	}
	    	catch (UnsatisfiedLinkError e) {
	    		sfoundLibrary = false;
	    	}
    	}    	
    	try {
    		System.loadLibrary("OVSR");  
    		Log.i("Debug","My Lib Loaded!");
    	}
    	catch (UnsatisfiedLinkError e) {
    		Log.e("Debug", "Error log", e);
    	} 	
    }
	public boolean getOpenCLSupport(){
		return sfoundLibrary;
	}
    public void setBitmap(Bitmap bmpOrigJava)
    {
    	bmpOrig = bmpOrigJava;
        info[0] = bmpOrig.getWidth();
        info[1] = bmpOrig.getHeight();
        bmpOpenCL = Bitmap.createBitmap(info[0], info[1], Bitmap.Config.ARGB_8888);
    }
    public Bitmap getBitmap()
    {
    	return bmpOpenCL;
    }
    private native void initOpenCL (String kernelName);
    private native void initOpenCLFromInput (String OpenCLCode, String kernelName);
    private native void nativeBasicOpenCL (
            Bitmap inputBitmap,
            Bitmap outputBitmap
        );
    private native void nativeImage2DOpenCL(
            Bitmap inputBitmap,
            Bitmap outputBitmap
        );
    private native void nativeSaturatieImage2DOpenCL(
            Bitmap inputBitmap,
            Bitmap outputBitmap,
            float saturatie
        );
    private native void shutdownOpenCL ();
	public void RemoveOpenCL ()
	{
		shutdownOpenCL();
	}
	public void OpenCLEdge ()
	{
		if(bmpOrig == null)
			return;
    	copyFile("edge.cl");
    	String kernelName="edge";
	    long startTime = System.nanoTime(); 
	    initOpenCL(kernelName);
    	//nativeBasicOpenCL(
        nativeImage2DOpenCL(
                bmpOrig,
                bmpOpenCL
            );
        shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");
	}
	public void OpenCLInverse ()
	{
		if(bmpOrig == null)
			return;
    	copyFile("inverse.cl");
    	String kernelName="inverse";
	    long startTime = System.nanoTime(); 
    	initOpenCL(kernelName);
    	//nativeBasicOpenCL(
    	nativeImage2DOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");
	}
	public void OpenCLSharpen ()
	{
		if(bmpOrig == null)
			return;
		Log.i("OpenCL","OpenCLSharpen");
    	copyFile("sharpen.cl");
    	String kernelName="sharpen";
    	Log.i("DEBUG","BEFORE runOpencl sharpen");
	    long startTime = System.nanoTime(); 
    	initOpenCL(kernelName);
  //  	nativeBasicOpenCL(
    	nativeImage2DOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");
    	Log.i("DEBUG","AFTER runOpencl sharpen");
	}
	public void OpenCLMediaan ()
	{
		if(bmpOrig == null)
			return;
    	copyFile("mediaan.cl");
    	String kernelName="mediaan";
	    long startTime = System.nanoTime(); 

    	initOpenCL(kernelName);
    	//nativeBasicOpenCL(
    	nativeImage2DOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");
	}
	public void OpenCLBlur ()
	{
		if(bmpOrig == null)
			return;
    	copyFile("blur.cl");
    	String kernelName="blur";
	    long startTime = System.nanoTime(); 
    	initOpenCL(kernelName);
    	//nativeBasicOpenCL(
    	nativeImage2DOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");
	}	
	public void OpenCLSaturatie ()
	{	
		if(bmpOrig == null)
			return;
		Log.i("DEBUG","OPENCLSATURATIE");
		if(saturatie==-1) {
		final TextView progressView = new TextView(mContext);
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
                	   saturatie = MySeekBar.getProgress();
                	   saturate();
                	   outputButton.setImageBitmap(bmpOpenCL);
                	   saturatie = -1;
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
     	   saturate();		
		}
	}	
	private void saturate()
	{
		Log.i("OpenCL","OpenCLSaturatie");
    	copyFile("saturatie.cl");
    	String kernelName="saturatie";
    	Log.i("DEBUG","BEFORE runOpencl Saturatie");
	    long startTime = System.nanoTime(); 
    	initOpenCL(kernelName);
    	//nativeSaturatieOpenCL(
    	nativeSaturatieImage2DOpenCL(
                bmpOrig,
                bmpOpenCL,
                saturatie
            );
    	shutdownOpenCL();
    	long estimatedTime = System.nanoTime() - startTime;
	    estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
	    LogFile MyFile = new LogFile(mContext);
	    MyFile.writeToPublicFile(String.valueOf(estimatedTime), "TestResultsVideo.txt");   	
    	Log.i("DEBUG","AFTER runOpencl Saturatie");		
	}
	private void copyFile(final String f) {
		InputStream in;
		try {
			in = mContext.getAssets().open(f);
			final File of = new File(mContext.getDir("execdir",Context.MODE_PRIVATE), f);

			final OutputStream out = new FileOutputStream(of);

			final byte b[] = new byte[65535];
			int sz = 0;
			while ((sz = in.read(b)) > 0) {
				out.write(b, 0, sz);
			}
			in.close();
			out.close();
		} catch (IOException e) {       
			e.printStackTrace();
		}
	}
	public void setTimeFromJNI(float time)
	{
		Log.i("setTimeFromJNI","Time set on " + String.valueOf(time));
		//time = (float) (Math.round(time*1000.0) / 1000.0);	
		time = time *1000;
		View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
		TextView v = (TextView) rootView.findViewById(R.id.timeview);
		v.setText(String.valueOf(time) + " ms");
		
	}
	public void setConsoleOutput(String ErrorLog)
	{
		View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
		TextView v = (TextView) rootView.findViewById(R.id.ConsoleView);
		v.setText(ErrorLog);
	}
	public String getTemplate()
	{
		String template = null;
		try {
			InputStream in = mContext.getAssets().open("templateOpenCL.txt");
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
	public void setKernelName(String name) {
		kernelName = name;
	}
	public void codeFromFile(final String code)
	{
		Log.i("Debug","OpenCL: " + code);

			initOpenCLFromInput(code, kernelName);
	    	nativeImage2DOpenCL(		
	                bmpOrig,
	                bmpOpenCL
	            );
	    	shutdownOpenCL();		
	}
}