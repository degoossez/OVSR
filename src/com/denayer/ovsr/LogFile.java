package com.denayer.ovsr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		FileName = "LogFile.txt";
	}		
	public void writeToFile(String data) {
	    try {
	        OutputStreamWriter MyOutputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(FileName, Context.MODE_PRIVATE | Context.MODE_APPEND));
	        MyOutputStreamWriter.append(data);
	        MyOutputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	public String readFromFile(String path) {
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
