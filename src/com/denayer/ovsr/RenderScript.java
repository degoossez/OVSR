package com.denayer.ovsr;

import android.util.Log;
import android.graphics.Bitmap;
import android.renderscript.*;

public class RenderScript extends Object {

	public Bitmap inBitmap, outBitmap;
	
	public void RenderScriptEdge ()
	{
		Log.i("RenderScript","RenderScriptEdge");
	}
	
	public void setInputBitmap(Bitmap in)
	{
		inBitmap = in;
	}
	
	public Bitmap getOutputBitmap()
	{
		return outBitmap;
	}
}
