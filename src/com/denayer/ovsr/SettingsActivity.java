package com.denayer.ovsr;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.denayer.ovsr.MainActivity.ConnectTask;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ClipData.Item;
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
import android.os.Build;
import android.content.Context;


public class SettingsActivity extends Activity {
	static SharedPreferences settings;
	static CheckBox checkBox, checkBox2;
	static public Button signIn;
	static public Button signUp;
	public static Context con;
	public static SettingsActivity act;
	
	
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.Home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_settings,container, false);
			//checkBox = (CheckBox) inflater.inflate(R.id.AutoName, container, false);
			checkBox = (CheckBox) rootView.findViewById(R.id.AutoName);	
			checkBox2 = (CheckBox) rootView.findViewById(R.id.rememberUser);
			signUp = (Button) rootView.findViewById(R.id.buttonSignUP2);
			signIn = (Button) rootView.findViewById(R.id.buttonSignIN2);
			
	        if (settings.getBoolean("AutoName", false)) {
	            checkBox.setChecked(true);
	            Log.i("debug","true");
	        }
	        else
	        {
	        	checkBox.setChecked(false);
	            Log.i("debug","false");
	        }
	        
	        if (settings.getBoolean("rememberUser", false)) {
	            checkBox2.setChecked(true);
	            Log.i("debug","true");
	        }
	        else
	        {
	        	checkBox2.setChecked(false);
	            Log.i("debug","false");
	        }
	        
	        
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = settings.edit();
				            if (arg1){
				            	editor.putBoolean("AutoName", true);
				                checkBox.setChecked(true);
					            Log.i("debug","set True");
				            }  else {
				            	editor.putBoolean("AutoName", false);	   
				                checkBox.setChecked(false);
					            Log.i("debug","set False");
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
					            Log.i("debug","set True");
				            }  else {
				            	editor.putBoolean("rememberUser", false);	   
				                checkBox2.setChecked(false);
					            Log.i("debug","set False");
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
				    
					Button btncreate=(Button)dialog.findViewById(R.id.buttonCreateAccount);		
					
					btncreate.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							// get The User name and Password
//							String username=editTextUserName.getText().toString();
//							String passwd=editTextPassword.getText().toString();						
//							
//							Intent returnIntent = new Intent();
//							returnIntent.putExtra("login",username + " " + passwd);
//							act.setResult(RESULT_OK,returnIntent);     
//							
//							dialog.dismiss();
//							act.finish();
							
							
							
							
						}
					});
					
					dialog.show();

				    

				}
			});
			
			return rootView;
		}
	}	
}
