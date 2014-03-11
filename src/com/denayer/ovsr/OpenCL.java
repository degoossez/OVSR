package com.denayer.ovsr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class OpenCL extends Object {
	private Context mContext; //<-- declare a Context reference
	Bitmap bmpOrig, bmpOpenCL;
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
	
	public OpenCL(Context context) {
    	mContext = context; //<-- fill it with the Context you are passed
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
    private native void nativeBasicOpenCL (
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
                bmpOrig,
                bmpOpenCL
            );
    	shutdownOpenCL();
    	Log.i("DEBUG","AFTER runOpencl sharpen");
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
