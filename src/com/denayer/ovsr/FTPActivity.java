package com.denayer.ovsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.denayer.ovsr.SettingsActivity.PlaceholderFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class FTPActivity extends Activity {
	
	ListView myListView;
	Button downloadButton;
	MyArrayAdapter myArrayAdapter;
	private MyFTPClient ftpclient = null;
	MainActivity mActivity;
	private Thread mThread;
	SharedPreferences settings;
	String IP;
	Intent intent;
	ProgressDialog mDialog;
	
	private ArrayList<String> rsFileList = new ArrayList<String>();	 
	 
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.ftpmenu, menu);			
			return true;
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ftp);
		downloadButton = (Button)findViewById(R.id.getresult);
		downloadButton.setText("Busy...");
		downloadButton.setClickable(false);
		intent = getIntent();
		settings = getSharedPreferences("Preferences", 0);
		myListView = (ListView)findViewById(R.id.list);
		final MainActivity mActivity= new MainActivity();
		ftpclient = new MyFTPClient();
		if(settings.getBoolean("UseDefault", true)) {
			IP= getResources().getString(R.string.defaultIP);
		} else {
			IP = settings.getString("ServerIP", getResources().getString(R.string.defaultIP));
		}
		
		mDialog = new ProgressDialog(FTPActivity.this);
		mDialog.setMessage("Loading filelist.");	
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		
		
		mThread = new Thread() {
			
			@Override
	        public void run(){
				
					if(ftpclient.ftpConnect(IP, intent.getStringExtra("user"), mActivity.createHash(intent.getStringExtra("pas")), 21))
					{
						//mActivity.createToast("connection to ftp succesful", false);
						Log.i("ftpactivity", "connection to ftp succesful");
						String dir = ftpclient.ftpGetCurrentWorkingDirectory();
						rsFileList = ftpclient.ftpPrintFilesList(dir);
	//					myArrayAdapter = new MyArrayAdapter(
	//			        		FTPActivity.this,
	//			        		R.layout.row,
	//			        		android.R.id.text1,
	//			        		rsFileList
	//			        		);
						FTPActivity.this.runOnUiThread(new Runnable() {
						     @Override
						     public void run() {
						    	 myArrayAdapter = new MyArrayAdapter(
						         		FTPActivity.this,
						         		R.layout.row,
						         		android.R.id.text1,
						         		rsFileList
						         		);
						    	 myListView.setAdapter(myArrayAdapter);
						    	 downloadButton.setText("Download");
						 		 downloadButton.setClickable(true);
						 		 mDialog.dismiss();
	
						    }
						});
					}
					else
					{
						FTPActivity.this.runOnUiThread(new Runnable() {
						     @Override
						     public void run() {
						    	 downloadButton.setText("Connection error");
						 		 mDialog.dismiss();
	
						    }
						});
						
					}
			}	
			
		};
		mThread.start(); 
		
//		myArrayAdapter = new MyArrayAdapter(
//        		this,
//        		R.layout.row,
//        		android.R.id.text1,
//        		rsFileList
//        		);

		//myListView.setAdapter(myArrayAdapter);
        myListView.setOnItemClickListener(myOnItemClickListener);        
        
        downloadButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String result = "";
				
				/*
				//getCheckedItemPositions
				List<Integer> resultList = myArrayAdapter.getCheckedItemPositions();
				for(int i = 0; i < resultList.size(); i++){
					result += String.valueOf(resultList.get(i)) + " ";
				}
				*/
				
				List<String> resultList = myArrayAdapter.getCheckedItems();
				for(int i = 0; i < resultList.size(); i++){
					result += String.valueOf(resultList.get(i)) + "\n";
				}
				
				if(resultList.size() > 1)
				{
					//mActivity.createToast("Choose only 1 file", false);
					Toast.makeText(getApplicationContext(), "Choose only 1 file", 
							   Toast.LENGTH_SHORT).show();
				}
				else if(resultList.size() == 0)
				{
					//mActivity.createToast("No file selected", false);
					Toast.makeText(getApplicationContext(), "No file selected", 
							   Toast.LENGTH_SHORT).show();
				}
				else
				{
					myArrayAdapter.getCheckedItemPositions().toString();
					final String fileName = resultList.get(0);					
					mThread = null;
					mThread = new Thread() {
						
						@Override
				        public void run(){
							
								if(ftpclient.ftpDownload(fileName, getFilesDir().getPath() + "/" + fileName))
								{
									Log.i("ftpactivity", "download rs file");	
									Intent returnIntent = new Intent();
									returnIntent.putExtra("filename",fileName);
									FTPActivity.this.setResult(RESULT_OK,returnIntent);     									
									FTPActivity.this.finish();
								}
								else
								{
									FTPActivity.this.runOnUiThread(new Runnable() {
									     @Override
									     public void run() {
									    	 downloadButton.setText("Download failed");
				
									    }
									});
								}
									
									
						}
					};
					mThread.start(); 
				}			
				
				
			}});
        
	}
	
	OnItemClickListener myOnItemClickListener
    = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			myArrayAdapter.toggleChecked(position);
			
		}};		

	
	 private class MyArrayAdapter extends ArrayAdapter<String>{
	    	
	    	private HashMap<Integer, Boolean> myChecked = new HashMap<Integer, Boolean>();

			public MyArrayAdapter(Context context, int resource,
					int textViewResourceId, List<String> objects) {
				super(context, resource, textViewResourceId, objects);
				
				for(int i = 0; i < objects.size(); i++){
					myChecked.put(i, false);
				}
			}
	    	
			public void toggleChecked(int position){
				if(myChecked.get(position)){
					myChecked.put(position, false);
				}else{
					myChecked.put(position, true);
				}
				
				notifyDataSetChanged();
			}
			
			public List<Integer> getCheckedItemPositions(){
				List<Integer> checkedItemPositions = new ArrayList<Integer>();
				
				for(int i = 0; i < myChecked.size(); i++){
					if (myChecked.get(i)){
						(checkedItemPositions).add(i);
					}
				}
				
				return checkedItemPositions;
			}
			
			public List<String> getCheckedItems(){
				List<String> checkedItems = new ArrayList<String>();
				
				for(int i = 0; i < myChecked.size(); i++){
					if (myChecked.get(i)){
						(checkedItems).add(rsFileList.get(i));
					}
				}
				
				return checkedItems;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row = convertView;
				
				if(row==null){
					LayoutInflater inflater=getLayoutInflater();
					row=inflater.inflate(R.layout.row, parent, false); 	
				}
				
				CheckedTextView checkedTextView = (CheckedTextView)row.findViewById(R.id.text1);
				checkedTextView.setText(rsFileList.get(position));
				
				Boolean checked = myChecked.get(position);
				if (checked != null) {
					checkedTextView.setChecked(checked);
	            }
				
				return row;
			}
			
	    }

}