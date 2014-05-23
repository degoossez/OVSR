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
	
	/*! \brief constructor 	
    * 
    * passes arguments to super class
    * @param assets
    * @param metrics
    * @param config
    *  
    */
	public MyResources(AssetManager assets, DisplayMetrics metrics,
			Configuration config) {
		super(assets, metrics, config);
		
		
	}
	/*! \brief sets context object 
	*
    * context is used as a link to MainActivity
    * @param x the context
    *  
    */
	public void setMyContext(Context x)
	{
		mContext = x;
	}
	
	/*! \brief reads the RenderScript Bytecode from own location
	*
	* This function reimplements the openRawResource function from the super class.
	* Because we want to change bytecode at runtime, the location of the byte code needs to be read and writeable.
	* Instead of reading from the resource folder inside the apk, this function will now read the bytecode from the apps private directory.
	* 
    * @param id id of the resource. This needs to be passed in order for this function to match the signature of the super
    * classe's function. This value will be ignored because we no longer use the resource system due to it's read-only nature. 
    *  
    */
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
