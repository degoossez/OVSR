package com.denayer.ovsr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

public class OpenCL extends Object {
	private Context mContext; //<-- declare a Context reference
	Bitmap bmpOrig, bmpOpenCL;
	float saturatie=0;
	public ImageButton outputButton;
	final int info[] = new int[3]; // Width, Height, Execution time (ms)

    static {
        try { 
        	System.load("/system/vendor/lib/libPVROCL.so");
        	 Log.i("Debug", "Libs Loaded");
      	
        	//System.loadLibrary("CLDeviceTest");  
        }
        catch (UnsatisfiedLinkError e) {
		    System.load("/system/vendor/lib/egl/libGLES_mali.so");
		      Log.i("Debug", "Mali loaded");
          boolean sfoundLibrary = false;
        }
      }
	static {
	  try {
		  System.loadLibrary("OVSR");  
		  Log.i("Debug","My Lib Loaded!");
	  }
	  catch (UnsatisfiedLinkError e) {
	      Log.e("Debug", "Error log", e);
	  }
	}
	
	public OpenCL(Context context, ImageButton outImageButton) {
    	mContext = context; //<-- fill it with the Context you passed
    	outputButton = outImageButton;
    }
    public void setBitmap(Bitmap bmpOrigJava)
    {
    	bmpOrig = bmpOrigJava;
        info[0] = bmpOrig.getWidth();
        info[1] = bmpOrig.getHeight();
        bmpOpenCL = Bitmap.createBitmap(info[0], info[1], Bitmap.Config.ARGB_8888);
//        for(int i=0;i<3;i++)
//        {
//            for(int j=0;j<3;j++)
//            {
//        	Log.i("Red:",String.valueOf(Color.red(bmpOrig.getPixel(i, j))));
//        	Log.i("Green:",String.valueOf(Color.green(bmpOrig.getPixel(i, j))));
//        	Log.i("Blue:",String.valueOf(Color.blue(bmpOrig.getPixel(i, j))));
//            }
//        } 
    }
    public Bitmap getBitmap()
    {
//        for(int i=0;i<3;i++)
//        {
//            for(int j=0;j<3;j++)
//            {
//        	Log.i("Red:",String.valueOf(Color.red(bmpOpenCL.getPixel(i, j))));
//        	Log.i("Green:",String.valueOf(Color.green(bmpOpenCL.getPixel(i, j))));
//        	Log.i("Blue:",String.valueOf(Color.blue(bmpOpenCL.getPixel(i, j))));
//            }
//        }  	
    	return bmpOpenCL;
    }
    private native void initOpenCL (String kernelName);
    private native void nativeBasicOpenCL (
            Bitmap inputBitmap,
            Bitmap outputBitmap
        );
    private native void nativeSaturatieOpenCL(
            Bitmap inputBitmap,
            Bitmap outputBitmap,
            float saturatie
        );
    private native void nativeImage2DOpenCL(
            Bitmap inputBitmap,
            Bitmap outputBitmap
        );
    private native void shutdownOpenCL ();
	
	public void OpenCLEdge ()
	{
		Log.i("OpenCL","OpenCLEdge");
    	copyFile("edge.cl");
    	String kernelName="edge";
    	Log.i("DEBUG","BEFORE runOpencl");
    	initOpenCL(kernelName);
    	nativeBasicOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	Log.i("DEBUG","AFTER runOpencl");
	}
	public void OpenCLInverse ()
	{
		Log.i("OpenCL","OpenCLInverse");
    	copyFile("inverse.cl");
    	String kernelName="inverse";
    	Log.i("DEBUG","BEFORE runOpencl");
    	initOpenCL(kernelName);
    	nativeBasicOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	Log.i("DEBUG","AFTER runOpencl");
	}
	public void OpenCLSharpen ()
	{
		Log.i("OpenCL","OpenCLSharpen");
    	copyFile("sharpen.cl");
    	String kernelName="sharpen";
    	Log.i("DEBUG","BEFORE runOpencl sharpen");
    	initOpenCL(kernelName);
    	nativeBasicOpenCL(
 //   	nativeImage2DOpenCL( //TODO nativeImage2DOpenCL testen
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	Log.i("DEBUG","AFTER runOpencl sharpen");
	}
	public void OpenCLMediaan ()
	{
		Log.i("OpenCL","OpenCLMediaan");
    	copyFile("mediaan.cl");
    	String kernelName="mediaan";
    	Log.i("DEBUG","BEFORE runOpencl Mediaan");
    	initOpenCL(kernelName);
    	nativeBasicOpenCL(
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	Log.i("DEBUG","AFTER runOpencl Mediaan");
	}	
	public void OpenCLSaturatie ()
	{	
		Log.i("DEBUG","OPENCLSATURATIE");

		final EditText input = new EditText(mContext);
		final Resources res = mContext.getResources();

		input.setText("50");

		//only allow numeric values
		input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);        
        builder.setMessage("saturation value")
        	   .setView(input)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   String value = input.getText().toString();      
                	   saturatie = Float.parseFloat(value);  
                	   saturate();
                	   outputButton.setImageBitmap(bmpOpenCL);
                   }
               });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show(); 
	}	
	private void saturate()
	{
		Log.i("OpenCL","OpenCLSaturatie");
    	copyFile("saturatie.cl");
    	String kernelName="saturatie";
    	Log.i("DEBUG","BEFORE runOpencl Saturatie");
    	initOpenCL(kernelName);
    	nativeSaturatieOpenCL(
                bmpOrig,
                bmpOpenCL,
                saturatie
            );
    	shutdownOpenCL();
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
}
