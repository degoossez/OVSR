package com.denayer.ovsr;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ClipData.Item;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.os.Build;

public class SettingsActivity extends Activity {
	static SharedPreferences settings;
	static CheckBox checkBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		settings = getSharedPreferences("Preferences", 0);
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
	        if (settings.getBoolean("AutoName", false)) {
	            checkBox.setChecked(true);
	            Log.i("debug","true");
	        }
	        else
	        {
	        	checkBox.setChecked(false);
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
			return rootView;
		}
	}	
}
