package com.denayer.ovsr;

import android.content.Context;
import android.renderscript.RenderScript.RSMessageHandler;
import android.util.Log;
import android.widget.TextView;

public class MyRsMessageHandler extends RSMessageHandler {
	
	TextView mElapsedTime;
	MainActivity mMainThread;
	
	
	public MyRsMessageHandler(TextView view, MainActivity mainAct) {
		
		mElapsedTime = view;
		mMainThread = mainAct;
	}

	@Override
    public void run()
    {
        switch (mID)
        {
            case 1:
            {
            	/*
            	 * Reason for this code
            	 * http://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
            	 * 
            	 */
            	
            	mMainThread.runOnUiThread(new Runnable() {
            	     @Override
            	     public void run() {

            	     Log.i("in message handler",String.valueOf(mData[0]));
            	     mElapsedTime.setText(String.valueOf(mData[0]) + " ns");

            	    }
            	});
            	

            }
            break;
            default: super.run();
                break;
        }
    }
	
	
}
