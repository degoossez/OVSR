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
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.content.Context;


public class SettingsActivity extends Activity {
	static SharedPreferences settings;
	static CheckBox checkBox, checkBox2, checkBox3;
	static EditText ServerIP,ServerPort;
	static public Button signIn;
	static public Button signUp;
	public static Context con;
	public static SettingsActivity act;
	
	/*! \brief Constructor
	 *
    * sets the layout of the settings activity 
    * @param savedInstanceState
    * 
    */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		settings = getSharedPreferences("Preferences", 0);
		
		con = this;
		act = this;
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences.Editor editor = settings.edit();
		if(!settings.getBoolean("UseDefault", false))
		{
        	editor.putString("ServerIP", ServerIP.getText().toString());
        	editor.putInt("ServerPort", Integer.valueOf(ServerPort.getText().toString()));
		}
		editor.commit();
	}
	/*! \brief creates the options menu
	 *
    * 
    * @param menu    
    */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
   /*! \brief react on user menu selection
   *
   * 
   * @param item item from menu that is selected  
   */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.Home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	  /*! \brief initialization when creating the view
	   * 
	   * initializes the settings window. Read the previous state from the shared preferences and update
	   * the settings accordingly.
	   * Listeners for the widgets are defined here. Login information is send back to the main activity for 
	   * further processing.
	   * 
	   */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_settings,container, false);
			//checkBox = (CheckBox) inflater.inflate(R.id.AutoName, container, false);
			checkBox = (CheckBox) rootView.findViewById(R.id.AutoName);	
			checkBox2 = (CheckBox) rootView.findViewById(R.id.rememberUser);
			checkBox3 = (CheckBox) rootView.findViewById(R.id.UseDefaultServer);
			signUp = (Button) rootView.findViewById(R.id.buttonSignUP2);
			signIn = (Button) rootView.findViewById(R.id.buttonSignIN2);
			ServerIP = (EditText) rootView.findViewById(R.id.OVSRServerName);
			ServerPort = (EditText) rootView.findViewById(R.id.OVSRServerPort);

	        if (settings.getBoolean("AutoName", false)) {
	            checkBox.setChecked(true);
	        }
	        else
	        {
	        	checkBox.setChecked(false);
	        }
	        
	        if (settings.getBoolean("rememberUser", false)) {
	            checkBox2.setChecked(true);
	        }
	        else
	        {
	        	checkBox2.setChecked(false);
	        }	
	        
	        if (settings.getBoolean("UseDefault", false)) {
	            checkBox3.setChecked(true);
            	ServerIP.setFocusable(false);
            	ServerPort.setFocusable(false);
	        }
	        else
	        {
	        	checkBox3.setChecked(false);
            	ServerIP.setFocusableInTouchMode(true);
            	ServerPort.setFocusableInTouchMode(true);
	        }
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = settings.edit();
				            if (arg1){
				            	editor.putBoolean("AutoName", true);
				                checkBox.setChecked(true);
				            }  else {
				            	editor.putBoolean("AutoName", false);	   
				                checkBox.setChecked(false);
				            }
				    editor.commit();					
				}
			});
			checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = settings.edit();
				            if (arg1){
				            	editor.putBoolean("rememberUser", true);
				                checkBox2.setChecked(true);
				            }  else {
				            	editor.putBoolean("rememberUser", false);	   
				                checkBox2.setChecked(false);
				            }
				    editor.commit();					
				}
			});
			checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					SharedPreferences.Editor editor = settings.edit();
		            if (arg1){
		            	editor.putBoolean("UseDefault", true);
		            	ServerIP.setFocusable(false);
		            	ServerPort.setFocusable(false);
		            	editor.putString("ServerIP", "192.168.0.198");
		            	editor.putInt("ServerPort", 64000);
		            }  else {
		            	editor.putBoolean("UseDefault", false);
		            	ServerIP.setFocusableInTouchMode(true);
		            	ServerPort.setFocusableInTouchMode(true);
		            }		
				    editor.commit();					
				}
			});		
			signIn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					final Dialog dialog = new Dialog(con);
					dialog.setContentView(R.layout.login);
				    dialog.setTitle("Login");

				    // get the Refferences of views
				    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
				    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
				    
//				    editTextUserName.setText(username);
//				    editTextPassword.setText(passwd);
				    
					Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);						
					
					// Set On ClickListener
					btnSignIn.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							// get The User name and Password
							String username=editTextUserName.getText().toString();
							String passwd=editTextPassword.getText().toString();						
							
							Intent returnIntent = new Intent();
							returnIntent.putExtra("login",username + " " + passwd);
							act.setResult(RESULT_OK,returnIntent);     
							
							dialog.dismiss();
							act.finish();
							
							
							
							
						}
					});
					
					dialog.show();
				

				}
			});
			
			signUp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					final Dialog dialog = new Dialog(con);
					dialog.setContentView(R.layout.signup);
				    dialog.setTitle("Create account");				    
				    
				    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserName);
				    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.singUpEditTextPassword);
				    final  EditText editTextConfirmPassword=(EditText)dialog.findViewById(R.id.singUpEditTextConfirmPassword);
				    
					Button btncreate=(Button)dialog.findViewById(R.id.buttonCreateAccount);		
					
					btncreate.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							// get The User name and Password	
							
							String username=editTextUserName.getText().toString();
							String passwd=editTextPassword.getText().toString();
							String passwdConfirm=editTextConfirmPassword.getText().toString();

							Log.i("create",username + "/" + passwd + "/" + passwdConfirm  +"/");
							
							if(passwd.equals(passwdConfirm))
							{							
								Intent returnIntent = new Intent();
								returnIntent.putExtra("login",username + " " + passwd + " " + passwdConfirm);
								act.setResult(RESULT_OK,returnIntent); 
								dialog.dismiss();
								act.finish();
							}
							else
							{
								Log.i("account error","confirm passwd error");
							}
							
						}
					});
					
					dialog.show();

				    

				}
			});
					
			return rootView;
		}
	}	
}
