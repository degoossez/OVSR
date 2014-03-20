package com.denayer.ovsr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivty extends Activity {
	private LogFile LogFileObject;
	private TextView HistoryField;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		LogFileObject = new LogFile(this);
		HistoryField = (TextView)findViewById(R.id.LogField);
		HistoryField.setText(LogFileObject.readFromFile(""));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message_activty, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.Home:
	            Log.i("Debug","Pressed on history");
	            finish();
	            return true;
	        case R.id.Delete:
	        	DeleteFile();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void DeleteFile()
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	LogFileObject.deleteExternalStoragePrivateFile();
		        	HistoryField.setText(LogFileObject.readFromFile(""));
		            break;
		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();		
	}
}
