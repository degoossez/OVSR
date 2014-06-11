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

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class OpenCL extends Object {
	private Context mContext; //<-- declare a Context reference
	Bitmap bmpOrig, bmpOpenCL;
	static float saturatie=-1;
	public ImageView outputButton;
	final int info[] = new int[3]; // Width, Height, Execution time (ms)
	static boolean sfoundLibrary = true;
	String kernelName = "";
	private OnUpdateProcessBar mGUIUpdater = null;
	static int dev_type;
	/*! \brief The OpenCL constructor.
	 *
	 * The constructor takes two arguments. Loads the available OpenCL library 
	 * and the self generated libOVSR where all the native functions are defined.
	 * It takes two arguments. The context is needed to make the mainwindow accessable from this class.
	 * The imageView is the output imageview on the mainwindow.
	 * @param context is the context of MainActivity
	 * @param imageView is the ImageView to put the result in
	 */
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
	public OpenCL (Context context, ImageView imageView, OnUpdateProcessBar listener) {
		mContext = context; //<-- fill it with the Context you passed
		outputButton = imageView;
		mGUIUpdater = listener;
	}
	/*! \brief Returns a boolean to be able to check OpenCL support in the main code
	 *
	 * @return sFoundLibrary is true if OpenCL is supported
	 */  
	public boolean getOpenCLSupport(){
		return sfoundLibrary;
	}
	/*! \brief Setter function for the input bitmap.
	 *
	 * The setBitmap function creates a copy of the argument (bmpOrigJava) and creates a 2th bitmap.
	 * @param bmpOrigJava is the bitmap that has to be copied and processed.
	 */
	public void setBitmap(Bitmap bmpOrigJava)
	{
		bmpOrig = bmpOrigJava;
		info[0] = bmpOrig.getWidth();
		info[1] = bmpOrig.getHeight();
		bmpOpenCL = Bitmap.createBitmap(info[0], info[1], Bitmap.Config.ARGB_8888);
	}
	/*! \brief Getter function to get the resulting bitmap from one of the OpenCL functions.
	 *
	 * It has no arguments and returns a bitmap
	 * @return bmpOpenCL The resulting bitmap
	 */
	public Bitmap getBitmap()
	{
		return bmpOpenCL;
	}
	/*! \brief Connection between Java and Native code.
	 *
	 * The initOpenCL function needs a kernel name and initialises the OpenCL environment.
	 * @param kernelName is the kernel name of the kernel that has to be excecuted later
	 */
	private native void initOpenCL (String kernelName,int dev_type);
	/*! \brief Connection between Java and Native code.
	 *
	 * The initOpenCLFromInput function needs a kernel name and the OpenCL code.
	 * This function initialises the OpenCL environment for the code from the input field in mainwindow.
	 * @param OpenCLCode is a String that contains the OpenCL code
	 * @param kernelName is a String that contains the kernel name from the OpenCLCode
	 */
	private native void initOpenCLFromInput (String OpenCLCode, String kernelName,int dev_type);
	/*! \brief Connection between Java and Native code.
	 *
	 * The nativeBasicOpenCL function needs an input and output bitmap and executes the kernel initialized in initOpenCL.
	 * @param inputBitmap is the bitmap to be processed
	 * @param outputBitmap is the resulting bitmap
	 */
	private native void nativeBasicOpenCL (
			Bitmap inputBitmap,
			Bitmap outputBitmap
			);
	/*! \brief Connection between Java and Native code.
	 *
	 * The nativeImage2DOpenCL function needs an input and output bitmap and executes the kernel initialized in initOpenCL.
	 * The difference with nativeBasicOpenCL is that this function uses image2d_t in it's kernels.
	 * @param inputBitmap is the bitmap to be processed
	 * @param outputBitmap is the resulting bitmap
	 */
	private native void nativeImage2DOpenCL(
			Bitmap inputBitmap,
			Bitmap outputBitmap
			);
	/*! \brief Connection between Java and Native code.
	 *
	 * The nativeSaturatieImage2DOpenCL function needs an input and output bitmap and executes the kernel initialized in initOpenCL.
	 * This function als needs a saturation value (saturatie). This value will be between 0 (under saturation) and 200 (over saturation).
	 * The difference with nativeImage2DOpenCL is that this function is ONLY able to do the saturation filter.
	 * @param inputBitmap is the bitmap to be processed
	 * @param outputBitmap is the resulting bitmap
	 * @param saturatie is a float between 0 and 200
	 */
	private native void nativeSaturatieImage2DOpenCL(
			Bitmap inputBitmap,
			Bitmap outputBitmap,
			float saturatie
			);
	/*! \brief Connection between Java and Native code.
	 *
	 * The shutdownOpenCL function removes all OpenCL allocations.
	 */
	private native void shutdownOpenCL ();
	/*! \brief This function will be called when the Edge button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL edge filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	public void OpenCLEdge ()
	{
		if(bmpOrig == null)
			return;
		copyFile("edge.cl");
		String kernelName="edge";
		long startTime = System.nanoTime(); 
		initOpenCL(kernelName,dev_type);
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
	/*! \brief This function will be called when the Inverse button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL inverse filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	public void OpenCLInverse ()
	{
		if(bmpOrig == null)
			return;
		copyFile("inverse.cl");
		String kernelName="inverse";
		long startTime = System.nanoTime(); 
		initOpenCL(kernelName,dev_type);
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
	/*! \brief This function will be called when the Sharpen button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL sharpen filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	public void OpenCLSharpen ()
	{
		if(bmpOrig == null)
			return;
		copyFile("sharpen.cl");
		String kernelName="sharpen";
		long startTime = System.nanoTime(); 
		initOpenCL(kernelName,dev_type);
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
	/*! \brief This function will be called when the mediaan button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL mediaan filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	public void OpenCLMediaan ()
	{
		if(bmpOrig == null)
			return;
		copyFile("mediaan.cl");
		String kernelName="mediaan";
		long startTime = System.nanoTime(); 

		initOpenCL(kernelName,dev_type);
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
	/*! \brief This function will be called when the Blur button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL blur filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	public void OpenCLBlur ()
	{
		if(bmpOrig == null)
			return;
		copyFile("blur.cl");
		String kernelName="blur";
		long startTime = System.nanoTime(); 
		initOpenCL(kernelName,dev_type);
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
	/*! \brief This function will be called when the saturatie button is clicked.
	 *
	 * It will execute all steps to apply the OpenCL saturation filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 * This function will also show a pop up window with a slider bar to select the saturation value between 0 and 200.
	 */
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
	/*! \brief This function will be called when OpenCLsaturatie's pop up windows is closed.
	 *
	 * It will execute all steps to apply the OpenCL saturation filter onto the image, gets the execution time and has a check to make sure the bitmap is valid.
	 */
	private void saturate()
	{
		copyFile("saturatie.cl");
		String kernelName="saturatie";
		long startTime = System.nanoTime(); 
		initOpenCL(kernelName,dev_type);
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
	}
	/*! \brief This function will copy a file from the assets folder specified by the argument to the execdir of the application.
	 *
	 * The argument is the file name and must be located inside the assets folder. It copies the file to make sure the OpenCL code can acces it.
	 * @param f is the name of the file that has to be copied
	 */
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
	/*! \brief The setTimeFromJNI function allows the native code to set a value to the GUI in the log window.
	 *
	 *@param time is the time that has to be placed in the log field
	 */
	public void setTimeFromJNI(float time)
	{
		Log.i("setTimeFromJNI","Time set on " + String.valueOf(time));
		//time = (float) (Math.round(time*1000.0) / 1000.0);	
		time = time *1000;
		View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
		TextView v = (TextView) rootView.findViewById(R.id.timeview);
		v.setText(String.valueOf(time) + " ms");

	}
	/*! \brief The setConsoleOutput function allows the native code to set a value to the GUI in the console window.
	 *
	 *@param ErrorLog is the error log that has to be placed in the console view.
	 */
	public void setConsoleOutput(String ErrorLog)
	{
		View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
		TextView v = (TextView) rootView.findViewById(R.id.ConsoleView);
		v.setText(ErrorLog);
	}
	/*! \brief Gets the template from the assets file and returns it.
	 *
	 *@return template is a string that contains the standard template for OpenCL
	 */
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
	/*! \brief This function gives the kernelName a value.
	 *
	 *@param name contains the kernel name to be set
	 */
	public void setKernelName(String name) {
		kernelName = name;
	}
	/*! \brief This function calls all the need OpenCL function to compile OpenCL code from text input.
	 *
	 *@param code is the OpenCL code that needs to be compiled
	 */
	public void codeFromFile(final String code)
	{
		initOpenCLFromInput(code, kernelName,dev_type);
		nativeImage2DOpenCL(		
				bmpOrig,
				bmpOpenCL
				);
		shutdownOpenCL();		
	}
	public void OpenCLVideo(String[] arg)
	{
		int LengthInFrames = 0;
		int counter = 0;
		long startTime = System.nanoTime(); 
		try{
			mGUIUpdater.updateProcessBar("Load");

			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("/sdcard/DCIM/small.mp4"); 
			grabber.start();

			while(true)
			{
				if(grabber.grab()==null) break;
				LengthInFrames++;
			}
			mGUIUpdater.updateProcessBar("Start" + " " + String.valueOf(LengthInFrames));
			FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("/sdcard/DCIM/saved_images/smallTesting.mp4", grabber.getImageWidth(), grabber.getImageHeight());

			recorder.setFormat("mp4");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
			recorder.setVideoBitrate(33000);
			recorder.setFrameRate(grabber.getFrameRate());				

			IplImage image = IplImage.create(grabber.getImageWidth(), grabber.getImageHeight(), IPL_DEPTH_8U, 4);
			IplImage frame2 = IplImage.create(image.width(), image.height(), IPL_DEPTH_8U, 4);
			Bitmap MyBitmap = Bitmap.createBitmap(frame2.width(), frame2.height(), Bitmap.Config.ARGB_8888);   
			Bitmap MyBitmap2 = Bitmap.createBitmap(frame2.width(), frame2.height(), Bitmap.Config.ARGB_8888);   
			recorder.start();
			
			String kernelName=arg[0];
			if(!arg[1].equals("runtime"))
			{
				copyFile( arg[0] +".cl");
				initOpenCL(kernelName,dev_type);
			}
			else
			{
				Log.d("Kernel code",arg[2]);
				Log.d("Kernel Name",kernelName);
				initOpenCLFromInput(arg[2], kernelName,dev_type);
			}
	    	
			grabber.setFrameNumber(0);

			while(true)
			{					
				image = grabber.grab();
				if(image==null)
				{
					break;
				}
				opencv_imgproc.cvCvtColor(image, frame2, opencv_imgproc.CV_BGR2RGBA);
				MyBitmap.copyPixelsFromBuffer(frame2.getByteBuffer());
				if(kernelName.equals("saturatie"))
				{
					nativeSaturatieImage2DOpenCL(
							MyBitmap,
							MyBitmap2,
							saturatie
							);    	
				}
				else
				{
					nativeImage2DOpenCL(
							MyBitmap,
							MyBitmap2
							);    	
				}
				MyBitmap2.copyPixelsToBuffer(frame2.getByteBuffer());
				opencv_imgproc.cvCvtColor(frame2, image, opencv_imgproc.CV_RGBA2BGR);		            
				recorder.record(image);
				counter++;
				mGUIUpdater.updateProcessBar(String.valueOf(counter));
			}   			

			shutdownOpenCL();
			recorder.stop();
			grabber.stop();	
			mGUIUpdater.updateProcessBar("Done");
		}catch(Exception e){
			e.printStackTrace();
		}   	
		long estimatedTime = System.nanoTime() - startTime;
		estimatedTime = TimeUnit.NANOSECONDS.toMillis(estimatedTime);
		Log.d("Time:",Long.toString(estimatedTime));
	}
	public interface OnUpdateProcessBar {
		public void updateProcessBar(String message);
	}	
	/*! \brief Returns the source code of the specified filter
	 *
	 * @param filtername The name of the filter
	 * @return code The code of the specified filter
	 */
	public String getFilterCode(String filterName)
	{
		String code = "";
		String file = filterName + ".cl";

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
	public void setDeviceType(int device)
	{
		dev_type = device;
	}
}
