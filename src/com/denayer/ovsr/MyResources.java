package com.denayer.ovsr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.*;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

public class MyResources extends Resources{
	
	
	final Object mAccessLock = new Object();
	TypedValue mTmpValue = new TypedValue();
	Context mContext;
	
	public MyResources(AssetManager assets, DisplayMetrics metrics,
			Configuration config) {
		super(assets, metrics, config);
		
		
	}
	
	public void setMyContext(Context x)
	{
		mContext = x;
	}
	
	@Override
	public InputStream openRawResource(int id) throws NotFoundException {       
        
        Log.i("koen","HACKED RESOURCES!!!");        
        
        InputStream res = null;
        String baseDir;        
        String fileName = "template.bc";
        
        //TODO make this a user setting
        boolean privateDir = true;
        
        if(!privateDir)
        {        
	        //check if sdcard is mounted
	        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	        {
	        	baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();	           
	            File f = new File(baseDir + File.separator + fileName);
	            String path = new String();
	            path = baseDir + File.separator + fileName;
	            
	            try {
	    			res = new FileInputStream(f);//			
	    			
	    			Log.i("koen","found .bc found");
	    			Log.i("koen","path to file " + path);
	    			//Log.i("koen",fileContent.toString());
	    		} catch (FileNotFoundException e) {
	    			// TODO Auto-generated catch block
	    			Log.i("koen","can't find .bc found");
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        }
	        else
	        {
	        	return res;
	        }
        }
        else
        {
        	//read from apps private dir
        	try {
        		Log.i("koen","path to file " + mContext.getApplicationInfo().dataDir);
				res = mContext.openFileInput(fileName);
				Log.i("koen","read .bc file in private app dir");
				
			} catch (FileNotFoundException e) {
				Log.i("koen","cannot read .bc file from private app dir");
			}
        }        
                
        return res;
    }	
}
