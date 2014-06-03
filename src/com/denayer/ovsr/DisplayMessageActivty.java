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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivty extends Activity {
	private LogFile LogFileObject;
	private TextView HistoryField;
	/*! \brief The onCreate function will be called when this Acvity is called.
	*
	* It creates a LogFile object and sets the text from LogFile.txt (from the private directory) 
	* in the historyfield to be displayed on screen.
	* @param savedInstanceState are is a value that remembers what the last instance on screen was.
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		LogFileObject = new LogFile(this);
		HistoryField = (TextView)findViewById(R.id.LogField);
		HistoryField.setText(LogFileObject.readFromFile("","LogFile.txt"));
	}
	/*! \brief The onCreate function will be called when the optionsmenu is created.
	*
	* It inflates the values into the menu.
	* @param menu is the menu that has to be displayed
	* @return It will always return true.
	*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message_activty, menu);
		return true;
	}
       /*! \brief The onOptionsItemSelected will be called when an item from the menu is clicked.
	*
	* Depending on the item that is clicked, a specific action will be excecuted.
	* @param item is the id of the item that is selected.
	* @return It will always return true or itself (recursive)
	*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.Home:
	            finish();
	            return true;
	        case R.id.Delete:
	        	DeleteFile();
	        	return true;
	        case R.id.Settings:
	    		Intent intent = new Intent(this,SettingsActivity.class);
	    		startActivity(intent);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
       /*! \brief The DeleteFile will delete the history file.
	*
	* When the DeleteFile function is called, it will show a pop up to make sure you want to delete the history file.
	*/
	public void DeleteFile()
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	LogFileObject.deleteExternalStoragePrivateFile();
		        	HistoryField.setText(LogFileObject.readFromFile("","LogFile.txt"));
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
