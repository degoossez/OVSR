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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class LogFile extends Object {
	private Context mContext; //<-- declare a Context reference
	private String FileName;
	
	public LogFile(Context context){
		mContext = context;
		
	}		
	public void writeToFile(String data, String FileName, boolean overwrite) {				
	    try {
	    	OutputStreamWriter MyOutputStreamWriter;
	    	
	    	if(!overwrite)
	    	{
		        MyOutputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(FileName, Context.MODE_PRIVATE | Context.MODE_APPEND));
		        MyOutputStreamWriter.append(data);
	    	}
	    	else
	    	{
	    		Log.i("Debug","Data" + data);
		        MyOutputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(FileName, Context.MODE_PRIVATE));
		        MyOutputStreamWriter.write(data);	    		
	    	}
	        MyOutputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	public void writeToPublicFile(String data, String FileName) {	
		FileOutputStream fop = null;
		File file;
		data = data + "\n";
		try {
 
			file = new File("/sdcard/DCIM/" + FileName);
			fop = new FileOutputStream(file,true);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = data.getBytes();
			
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public String readFromFile(String path, String FileName) {
		String ret = "";
		if(path==""){
			try {
				InputStream inputStream = mContext.openFileInput(FileName);

				if ( inputStream != null ) {
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String receiveString = "";
					StringBuilder stringBuilder = new StringBuilder();

					while ( (receiveString = bufferedReader.readLine()) != null ) {
						stringBuilder.append(receiveString).append("\n");
					}

					inputStream.close();
					ret = stringBuilder.toString();
				}
			}
			catch (FileNotFoundException e) {
				Log.e("login activity", "File not found: " + e.toString());
			} catch (IOException e) {
				Log.e("login activity", "Can not read file: " + e.toString());
			}
		}
		else
		{
			File file = new File(path);
			String receiveString = "";
			StringBuilder stringBuilder = new StringBuilder();
			try {
				BufferedReader buf = new BufferedReader(new FileReader(file));
				while ( (receiveString = buf.readLine()) != null ) {
					stringBuilder.append(receiveString).append("\n");
				}
				buf.close();
				ret = stringBuilder.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	void deleteExternalStoragePrivateFile() {
	    // Get path for the file on external storage.  If external
	    // storage is not currently mounted this will fail.
	    File file = new File(mContext.getFilesDir().getAbsolutePath() + File.separator + FileName);
	    if (file != null) {
	        file.delete();
	        Log.i("Debug","File deleted!");
	    }
	}
	boolean hasExternalStoragePrivateFile() {
	    // Get path for the file on external storage.  If external
	    // storage is not currently mounted this will fail.
	    File file = new File(FileName);
	    if (file != null) {
	        return file.exists();
	    }
	    return false;
	}
	
}
