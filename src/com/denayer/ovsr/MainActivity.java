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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;

import com.lamerman.FileDialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.VideoView;

import java.security.*;

public class MainActivity extends Activity {
	public static String DEFAULT_IP_ADDR;
	public static String IP_ADDR;
	SharedPreferences settings;
	private Uri mImageCaptureUri;
	private ImageView Input_Image;
	private ImageView Output_Image;
	private VideoView Input_Video;
	private VideoView Output_Video;
	private Bitmap bitmap   = null;
	private Bitmap outBitmap   = null;
	private File file;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private static final int REQUEST_LOAD = 3;
	private static final int REQUEST_SAVE = 4;
	private static final int REQUEST_SAVE_IMAGE = 5;
	private static final int SETTINGS = 6;
	private static final int PICK_VIDEO = 7;
	private static final int REQUEST_PATH = 8;
	private Button SubmitButton;
	private Button previousButton;
	private RadioButton RenderScriptButton;
	private RadioButton OpenCLButton;
	private EditText CodeField;
	public TextView TimeView;
	public TextView ConsoleView;
	public TextView NetworkView;
	public String fileName;
	public String CodeFieldCode;
	public String tabCodeString;
	public String tabConsoleString;
	public String tabLogString;
	OpenCL OpenCLObject;
	RsScript RenderScriptObject;
	LogFile LogFileObject;   
	private Button connectButton, disconnectButton;

	public String username = "";
	public String passwd = "";
	
	public String previousCode = "";	//used to backup the code field
	private TabHost myTabHost;

	public boolean isImage = true;
	public boolean isRenderScript = true;
	public boolean isRuntime = false;
	public String videoPath;
	public String savePath;
	private Method m;
	MediaController mediaControllerOut;
	MediaController mediaControllerIn;
	Uri videoOut;
	Uri videoIn;
	private TcpClient mTcpClient;	
	MyFTPClient ftpclient = null;
	ProgressDialog dialog = null;
	ProgressDialog videoProcessDialog = null;
	
	private EditVideoTask MyEditVideoATask = null;
	private static String[] OpenCLVideoArguments = {null,null};
	//item in de lijst toevoegen voor nieuwe filters toe te voegen.
	private String [] itemsFilterBox = new String [] {"Edge", "Inverse","Sharpen","Mediaan","Saturatie","Blur"};

	/*! \brief Major initialization for app behavior.
	* In this function the initialization of the most important app components is done: class 
	* variables are assigned, the layout with the widgets is set and various listeners are implemented.
    * 
    * Submit button listener: when this button is clicked, the listener behaves differently depending on which radiobutton is selected
    *  When the RenderScript radiobutton is checked, the written code inside the app will be send to the 
    * server for compilation. When not yet logged in, a login dialog will be shown. If the Radiobutten for OpenCL is checked
    * the function will check the device for OpenCL support. If supported, a kernel name is asked and the code is executed.
    * If a video is selected as input, the location to save the result can be specified with a file browser.<br>
    * 
    * Connect button listener: setup a connection to the server.<br>
    * 
    * Disconnect button listener: disconnect from server.<br>
    * @param savedInstanceState
    * 
    * 
    */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DEFAULT_IP_ADDR = getResources().getString(R.string.defaultIP);
		IP_ADDR=DEFAULT_IP_ADDR;
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("");

		ftpclient = new MyFTPClient();

		settings = getSharedPreferences("Preferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		if(!settings.getBoolean("AutoName", false))
		{
			editor.putBoolean("AutoName", false);
			editor.commit();
		}	
		if(!settings.getBoolean("rememberUser", false))
		{
			editor.putBoolean("rememberUser", false);
			editor.commit();
		}	
		if(!settings.getBoolean("UseDefault", false))
		{
			editor.putBoolean("UseDefault", false);
			editor.commit();
		}	
		if(!settings.getBoolean("rememberUser", false))
		{
			editor.putBoolean("rememberUser", false);
			editor.commit();
		}	
		if(!settings.getBoolean("showCode", false))
		{
			editor.putBoolean("showCode", false);
			editor.commit();
		}		
		if(settings.getString("userName","") == "")
		{
			editor.putString("username", "");
			editor.commit();
		}
		if(settings.getString("passwd","") == "")
		{
			editor.putString("passwd", "");
			editor.commit();
		}
		/*
		 * If the saved IP is equal to the default IP or the ServerIP is not yet created
		 * create a ServerIP sharedpref with default_ip_addr as value.
		 */
		if(settings.getString("ServerIP", DEFAULT_IP_ADDR)==DEFAULT_IP_ADDR)
		{
			editor.putString("ServerIP", DEFAULT_IP_ADDR);
			editor.commit();
		}
		else
		{
			IP_ADDR = settings.getString("ServerIP", DEFAULT_IP_ADDR);
		}
		/*
		 * If ServerPort is not 64000, it already exists. 
		 */
		if(settings.getInt("ServerPort", 64000)==64000)
		{
			editor.putInt("ServerPort", 64000);
			editor.commit();
		}
		Input_Image = (ImageView)findViewById(R.id.ImageView1);
		Output_Image = (ImageView)findViewById(R.id.ImageView2);
		Input_Video = (VideoView)findViewById(R.id.VideoView1);
		Output_Video = (VideoView)findViewById(R.id.VideoView2);
		Input_Video.setVisibility(View.INVISIBLE);
		Output_Video.setVisibility(View.INVISIBLE);
		SubmitButton=(Button) findViewById(R.id.submit_button);
		previousButton = (Button) findViewById(R.id.previous_button);
		ConsoleView=(TextView)findViewById(R.id.ConsoleView);
		TimeView=(TextView)findViewById(R.id.timeview);
		NetworkView=(TextView)findViewById(R.id.networkview);
		CodeField=(EditText)findViewById(R.id.editText1);
		RenderScriptButton = (RadioButton) findViewById(R.id.radioButton2);
		OpenCLButton = (RadioButton) findViewById(R.id.radioButton1);
		connectButton = (Button) findViewById(R.id.connect_button);
		disconnectButton = (Button) findViewById(R.id.disconnect_button);

		myTabHost= (TabHost) findViewById(R.id.tabhost);
		myTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if(tabId=="CodeTab")	
				{
					myTabHost.setCurrentTabByTag("Code");
				}
				else if(tabId=="ConsoleTab")
				{
					myTabHost.setCurrentTabByTag("Console");					
				}
				else if(tabId=="LogTab")
				{
					myTabHost.setCurrentTabByTag("Log");										
				}
			}
		});
		myTabHost.setup();
		TabSpec spec1 = myTabHost.newTabSpec("Code");
		spec1.setContent(R.id.CodeTab);
		spec1.setIndicator("Code");
		TabSpec spec2 = myTabHost.newTabSpec("Console");
		spec2.setContent(R.id.ConsoleTab);
		spec2.setIndicator("Console");        
		TabSpec spec3 = myTabHost.newTabSpec("Log");
		spec3.setContent(R.id.LogTab);
		spec3.setIndicator("Log");
		TabSpec spec4 = myTabHost.newTabSpec("Network");
		spec4.setContent(R.id.NetworkTab);
		spec4.setIndicator("Network"); 
		myTabHost.addTab(spec1);
		myTabHost.addTab(spec2);
		myTabHost.addTab(spec3);     
		myTabHost.addTab(spec4);

		OpenCLObject = new OpenCL(MainActivity.this,(ImageView)findViewById(R.id.ImageView2));
		RenderScriptObject = new RsScript(this,(ImageView)findViewById(R.id.ImageView2),TimeView);
		LogFileObject = new LogFile(this);   

		createBoxes();
		CodeField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				CodeFieldCode = CodeField.getText().toString();			
			}
			@Override
			public void afterTextChanged(Editable s) {		
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {			
			}
		});

		new Handler();	

		SubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isRuntime = true;
				if(CodeField.getText().toString()!=""){
					if(RenderScriptButton.isChecked() ) 
					{
						isRenderScript=true;
						if(TcpClient.isConnected)
						{
							username = "";
							passwd = "";
							
							boolean tmp = false;
							if(settings.getBoolean("rememberUser", false))
							{
								if(settings.getString("userName", "") == "")
									if(settings.getString("passwd", "") == "")
									{
										tmp = true;
										Log.i("main","remember user is true, but no user information stored");
									}
							}
							
							if(!settings.getBoolean("rememberUser", false) || tmp)
							{
								//if the user information is not saved or if it is but no login has yet happened
								//show login dialog
								final Dialog dialog = new Dialog(MainActivity.this);
								dialog.setContentView(R.layout.login);
								dialog.setTitle("Login");

								// get the Refferences of views
								final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
								final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);

								editTextUserName.setText(username);
								editTextPassword.setText(passwd);

								Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);

								// Set On ClickListener
								btnSignIn.setOnClickListener(new View.OnClickListener() {

									public void onClick(View v) {

										// get The User name and Password
										username=editTextUserName.getText().toString();
										passwd=editTextPassword.getText().toString();

										sendRenderscriptMessage(username, passwd);													

										dialog.dismiss();								

									}
								});

								dialog.show();
							}
							else
							{
								sendRenderscriptMessage(settings.getString("userName", ""), settings.getString("passwd", ""));
							}				
						}
						else
							createToast("Not connected", false);
					}
					else 
					{
						if(OpenCLObject.getOpenCLSupport())
						{		
							AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

							alert.setTitle("Enter the kernel name:");

							// Set an EditText view to get user input 
							final EditText input = new EditText(MainActivity.this);
							alert.setView(input);
							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								OpenCLObject.setKernelName(input.getText().toString());
								isRenderScript=false;								
								if(isImage){
									OpenCLObject.codeFromFile(CodeField.getText().toString());	
									if(OpenCLObject.getBitmap()!=null && isImage)
									{
										outBitmap = OpenCLObject.getBitmap();
										Output_Image.setImageBitmap(outBitmap);
									}
									else
									{
										createToast("Select image!",false);					
									}
								}
								else
								{
									Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
									intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
									intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
									intentLoad.putExtra("isRs", false);
									startActivityForResult(intentLoad, REQUEST_PATH);
								}	
								}
							});		
							alert.show();
							
			
						}
						else createToast("No OpenCL support!",false);
					}
				}

			}
		});

		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				createToast("connecting to " + TcpClient.SERVER_IP, false);

			}
		});		

		disconnectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TcpClient.isConnected)
				{
					mTcpClient.stopClient();
				}
				createToast("disconnected", false);


			}
		});		
		
		previousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(previousCode.equals(""))
				{
					createToast("No previous code available", false);
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

				    builder.setTitle("Confirm");
				    builder.setMessage("Are you sure you want to revert to the previous code? " +
				    		"This will overwrite all current code and can not be " +
				    		"undone.");

				    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				        public void onClick(DialogInterface dialog, int which) {
				            CodeField.setText(previousCode);
				            previousCode = "";
				            dialog.dismiss();
				        }

				    });

				    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            // Do nothing
				            dialog.dismiss();
				        }
				    });

				    AlertDialog alert = builder.create();
				    alert.show();
					
				}
			}
		});

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x/2 - 15;
		int height = size.y/2 - 15;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		Input_Image.setImageBitmap(bitmap);
		OpenCLObject.setBitmap(bitmap);
		RenderScriptObject.setInputBitmap(bitmap);

	}

	/*! \brief Receives data from other activities via intents
	 *
   * This function receives data from other activities via intents. From the resultCode variable the origin of the
   * Invocation can be derived. The Data variable contains the data send by the intent. Depending on the resultCode
   * the function will act differently.
   * 
   * @param requestCode
   * @param resultCode integer to indicate the activity who's responsible for calling this function
   * @param data the data from the stopped activity
   */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		String path     = "";
		if(requestCode == REQUEST_LOAD)
		{
			String PathLoadFile = data.getStringExtra(FileDialog.RESULT_PATH);
			String FileContent = null;
			Log.i("Debug",PathLoadFile);
			FileContent = LogFileObject.readFromFile(PathLoadFile,"");
			CodeField.setText(FileContent);
		}
		else if(requestCode == REQUEST_PATH)
		{
			savePath = data.getStringExtra(FileDialog.RESULT_PATH);
			Log.e("requestCode","REQUEST_PATH");
			Log.i("debug",savePath + " : " + videoPath);
			if(savePath.equals(videoPath))
			{
				createToast("Invalid Video!", false);
			}
			else
				MyEditVideoATask = (EditVideoTask) new EditVideoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else if (requestCode == REQUEST_SAVE) {
			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);            	
			LogFileObject.writeToFile("		Code file saved to: " + filePath, "LogFile.txt",false);
			try{   
				if(!CodeFieldCode.equals("")){
					File CodeFile =new File(filePath);
					//if file doesnt exists, then create it
					if(!CodeFile.exists()){
						CodeFile.createNewFile();
					}
					FileWriter fileWritter = new FileWriter(CodeFile,true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					bufferWritter.write(CodeFieldCode);
					bufferWritter.close();
				} 
			}catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		else if (requestCode == PICK_FROM_FILE) {
			path = data.getStringExtra(FileDialog.RESULT_PATH);
			File f = new File(path);
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x/2 - 15;
			int height = size.y/2 - 15;
			bitmap = decodeAndResizeFile(f,height,width);
			setBitmaps();
		} else if (requestCode == PICK_FROM_CAMERA) {
			bitmap.recycle();
			bitmap  = BitmapFactory.decodeFile(mImageCaptureUri.getPath());
			try {                
				FileOutputStream out = new FileOutputStream(file);
				int BHeight = bitmap.getHeight()/2;
				int BWidth = bitmap.getWidth()/2;                	
				bitmap = Bitmap.createScaledBitmap(bitmap, BWidth, BHeight, false);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
				MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
				file.delete(); //remove the temp file
			} catch (Exception e) {
				e.printStackTrace();
			}
			setBitmaps();
		} else if (requestCode == PICK_VIDEO){
			videoPath = data.getStringExtra(FileDialog.RESULT_PATH);
			mediaControllerIn = new MediaController(this);
			mediaControllerIn.setAnchorView(Input_Video);
			videoIn = Uri.parse(new File(videoPath).toString());
			Input_Video.setMediaController(mediaControllerIn);
			Input_Video.setVideoURI(videoIn);
		} else if(requestCode == REQUEST_SAVE_IMAGE) {
			String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
			LogFileObject.writeToFile(" File saved to: " + filePath,"LogFile.txt",false);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(filePath);
				outBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				createToast("Image saved!",false);	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try{
					out.close();
				} catch(Throwable ignore) {}
			} 			
		}
		else if(requestCode == SETTINGS)
		{
			if(TcpClient.isConnected)
			{
				//data from settingsactivity
				String str = data.getStringExtra("login");
				String[] split= str.split("\\s+");

				//login request
				if(split.length == 2)
				{
					username = split[0];
					passwd = split[1];
					String hash = createHash(passwd);						

					Log.i("tcp send","LOGIN " + username + " " + hash + " ENDLOGIN");
					mTcpClient.sendMessage("LOGIN " + username + " " + hash + " ENDLOGIN");

				}
				//create account request
				else if(split.length == 3)
				{	
					String newUser = split[0];
					String newPas = createHash(split[1]);

					Log.i("tcp account", "ACCOUNT " + newUser + " " + newPas + " ENDACCOUNT");
					mTcpClient.sendMessage("ACCOUNT " + newUser + " " + newPas + " ENDACCOUNT");


				}
				else
					Log.i("main","error splitting string");
			}
			else
				createToast("Not connected", false);


		}
		System.gc();
	}
	/*! \brief Sets the input and output bitmap
	 *
  *  This function will set the input bitmap to the image view. The bitmap is scaled depending on the device's
  *  screen resolution. A copy is also provided to the RenderScript and OpenCL objects. The output image view is set with a
  *  bitmap of the same dimensions as the input bitmap.
  */
	public void setBitmaps()
	{
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x/2 - 15;
		int height = size.y/2 - 15;
		Log.i("Debug","Width: " + width + " " + "Height: " + height);

		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		Input_Image.setImageBitmap(bitmap);
		Output_Image.setImageBitmap(Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888));

		OpenCLObject.setBitmap(bitmap);
		RenderScriptObject.setInputBitmap(bitmap);
	}
	/*! \brief Converts URI to a real file path
	*
	* @param contentUri the URI to be converted
	* @return returns the file path resulting from the URI
	*/
	public String getRealPathFromURI(Uri contentUri) {
		String [] proj      = {MediaStore.Images.Media.DATA};
		Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);

		if (cursor == null) return null;

		int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	/*! \brief Creates choose boxes to chose a filter
	*
	* This function creates all the filter chose boxes specified in the itemsFilterBox string list.
	* It also dynamicly adds onclick functions to each box and calls the correct filter for each box.
	*
	*/
	public void createBoxes()
	{
		//choose box voor opencl of renderscript te selecteren
		ArrayAdapter<String> adapterFilterBox  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,itemsFilterBox);        
		AlertDialog.Builder builderFilterBox     = new AlertDialog.Builder(this);
		builderFilterBox.setTitle("Select Filter");
		builderFilterBox.setAdapter( adapterFilterBox, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialogEdgeBox, int item ) {
				TimeView.setText("0");
				SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
				Date now = new Date();
				isRuntime=false;
				if(RenderScriptButton.isChecked())
				{		
					isRenderScript = true;
					fileName = "RenderScript/" + itemsFilterBox[item] + formatter.format(now);
					String FunctionName = "RenderScript" + itemsFilterBox[item];
					try {
						m = RsScript.class.getMethod(FunctionName);
						try {
							if(isImage){
								m.invoke(RenderScriptObject, null);
								if(RenderScriptObject.getOutputBitmap()!=null && isImage)
								{
									if(settings.getBoolean("showCode", false))
									{
										if(!CodeField.getText().toString().equals(""))
										{
											//if code field is not empty, backup the current code
											previousCode = CodeField.getText().toString();
										}
										CodeField.setText(RenderScriptObject.getFilterCode(itemsFilterBox[item]));
									}								
									
									outBitmap = RenderScriptObject.getOutputBitmap();
									Output_Image.setImageBitmap(RenderScriptObject.getOutputBitmap());
								}
								else
								{
									createToast("Select image!",false);	
								}
							}
							else
							{ 
								if(settings.getBoolean("showCode", false))
								{
									if(!CodeField.getText().toString().equals(""))
									{
										//if code field is not empty, backup the current code
										previousCode = CodeField.getText().toString();
									}
									CodeField.setText(RenderScriptObject.getFilterCode(itemsFilterBox[item]));
								}		
								
								if(itemsFilterBox[item]=="Saturatie")
								{
							        final TextView progressView = new TextView(MainActivity.this);
									final Resources res = MainActivity.this.getResources();
									final SeekBar MySeekBar = new SeekBar(MainActivity.this);
									MySeekBar.setMax(200);

									MySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
										   @Override 
										   public void onProgressChanged(SeekBar seekBar, int progress, 
										     boolean fromUser) { 
											   progressView.setText(String.valueOf(progress)); 
										   } 
										   @Override 
										   public void onStartTrackingTouch(SeekBar seekBar) { 
										   } 
										   @Override 
										   public void onStopTrackingTouch(SeekBar seekBar) { 
										   } 
										       }); 
									AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);        
							        builder.setMessage("saturation value")
							               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							                   public void onClick(DialogInterface dialog, int id) {      
							                	   RenderScriptObject.saturationValue = MySeekBar.getProgress();
													Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
													intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
													intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
													startActivityForResult(intentLoad, REQUEST_PATH);
							                   }
							               });
							        progressView.setGravity(1 | 0x10);
							        // Create the AlertDialog object and return it
							        AlertDialog dialog = builder.create();
								     LinearLayout ll=new LinearLayout(MainActivity.this);
								        ll.setOrientation(LinearLayout.VERTICAL);
								        ll.addView(MySeekBar);
								        ll.addView(progressView);
								        dialog.setView(ll);
							        dialog.show(); 									
								}
								else
								{
									Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
									intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
									intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
									startActivityForResult(intentLoad, REQUEST_PATH);
								}
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
				else
				{
					isRenderScript = false;
					if(OpenCLObject.getOpenCLSupport())
					{		
						fileName = "OpenCL/" + itemsFilterBox[item] + formatter.format(now);
						String FunctionName = "OpenCL" + itemsFilterBox[item];
						try {
							//MainActivity obj = new MainActivity();
							m = OpenCL.class.getMethod(FunctionName);
							try {
								if(isImage){
									m.invoke(OpenCLObject, null);
									if(OpenCLObject.getBitmap()!=null && isImage)
									{
										if(settings.getBoolean("showCode", false))
										{
											if(!CodeField.getText().toString().equals(""))
											{
												//if code field is not empty, backup the current code
												previousCode = CodeField.getText().toString();
											}
											CodeField.setText(OpenCLObject.getFilterCode(itemsFilterBox[item]));
										}	
										
										outBitmap = OpenCLObject.getBitmap();
										Output_Image.setImageBitmap(outBitmap);
									}
									else
									{
										createToast("Select image!",false);					
									}
								}
								else
								{
									if(settings.getBoolean("showCode", false))
									{
										if(!CodeField.getText().toString().equals(""))
										{
											//if code field is not empty, backup the current code
											previousCode = CodeField.getText().toString();
										}
										CodeField.setText(OpenCLObject.getFilterCode(itemsFilterBox[item]));
									}	
									
									m = OpenCL.class.getDeclaredMethod("OpenCLVideo",new Class[]{String[].class});
									if(itemsFilterBox[item]=="Saturatie")
									{
										OpenCLVideoArguments[0]= itemsFilterBox[item];
								        final TextView progressView = new TextView(MainActivity.this);
										final Resources res = MainActivity.this.getResources();
										final SeekBar MySeekBar = new SeekBar(MainActivity.this);
										MySeekBar.setMax(200);

										MySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
											   @Override 
											   public void onProgressChanged(SeekBar seekBar, int progress, 
											     boolean fromUser) { 
												   progressView.setText(String.valueOf(progress)); 
											   } 
											   @Override 
											   public void onStartTrackingTouch(SeekBar seekBar) { 
											   } 
											   @Override 
											   public void onStopTrackingTouch(SeekBar seekBar) { 
											   } 
											       }); 
										AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);        
								        builder.setMessage("saturation value")
								               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
								                   public void onClick(DialogInterface dialog, int id) {      
								                	   OpenCLObject.saturatie = MySeekBar.getProgress();
														Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
														intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
														intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
														startActivityForResult(intentLoad, REQUEST_PATH);
								                   }
								               });
								        progressView.setGravity(1 | 0x10);
								        // Create the AlertDialog object and return it
								        AlertDialog dialog = builder.create();
									     LinearLayout ll=new LinearLayout(MainActivity.this);
									        ll.setOrientation(LinearLayout.VERTICAL);
									        ll.addView(MySeekBar);
									        ll.addView(progressView);
									        dialog.setView(ll);
								        dialog.show(); 									
									}
									else
									{
										OpenCLVideoArguments[0]= itemsFilterBox[item];
										Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
										intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
										intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
										startActivityForResult(intentLoad, REQUEST_PATH);
									}
								}
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}	
					}
					else
					{
						createToast("No OpenCL support!",false);					
					}
				}
				if(TimeView.getText()!="0")
				{
					String Method="OpenCL";
					if(RenderScriptButton.isChecked()) Method="RenderScript";
					LogFileObject.writeToFile("\n" + Method + " : " + fileName + " : " + TimeView.getText(), "LogFile.txt",false);
				}
			}
		} );
		final AlertDialog dialogFilterBox = builderFilterBox.create();

		(findViewById(R.id.FilterButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogFilterBox.show();
			}
		});
		//einde choose box

		//radio buttons

		RenderScriptButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderScriptButton.setChecked(true);
				OpenCLButton.setChecked(false);
			}
		});
		OpenCLButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenCLButton.setChecked(true);
				RenderScriptButton.setChecked(false);
			}
		});
		ConsoleView.addTextChangedListener(new  TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if(arg0.toString().contains("error"))
				{
					myTabHost.setCurrentTabByTag("Console");
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {			
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {			
			}
		});
		//einde radio buttons
	}
		/*! \brief Displays a small message on the screen
		 *
		 *  @param Message This is the message.
		 *  @param isLong controls the duration the message is shown
		 */
	public void createToast(String Message,boolean isLong)
	{
		Context context = getApplicationContext();
		CharSequence text = Message;
		int duration = Toast.LENGTH_SHORT;
		if(isLong) duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.History:
			startHistoryActivity();
			return true;
		case R.id.Template:
			if(!CodeField.getText().toString().equals(""))
				previousCode = CodeField.getText().toString();
			if(RenderScriptButton.isChecked()) CodeField.setText(RenderScriptObject.getTemplate());
			else CodeField.setText(OpenCLObject.getTemplate());
			return true;
		case R.id.SaveF:
			Intent intentSave = new Intent(getBaseContext(), FileDialog.class);
			intentSave.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
			intentSave.putExtra(FileDialog.FORMAT_FILTER, new String[] { "txt","cl","rs" });
			startActivityForResult(intentSave, REQUEST_SAVE);
			return true;
		case R.id.LoadF:
			Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
			intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
			intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] { "txt","cl","rs" });
			startActivityForResult(intentLoad, REQUEST_LOAD);
			return true;   
		case R.id.Settings:
			Intent intent = new Intent(this,SettingsActivity.class);
			startActivityForResult(intent, SETTINGS);
			return true;
		case R.id.Camera:
			Input_Image.setVisibility(View.VISIBLE);
			Input_Video.setVisibility(View.INVISIBLE);
			Output_Image.setVisibility(View.VISIBLE);
			Output_Video.setVisibility(View.INVISIBLE);
			isImage=true;
			Intent intentCamera    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//save file for camera
			String SavePath = Environment.getExternalStorageDirectory().toString();
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
			Date now = new Date();
			String fileCameraName = formatter.format(now) + ".jpg";
			file = new File(SavePath, "OVSR"+fileCameraName);
			mImageCaptureUri = Uri.fromFile(file);
			try {
				intentCamera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
				intentCamera.putExtra("return-data", true);
				startActivityForResult(intentCamera, PICK_FROM_CAMERA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		case R.id.Video:
			isImage=false;
			Input_Image.setVisibility(View.INVISIBLE);
			Input_Video.setVisibility(View.VISIBLE);
			Output_Image.setVisibility(View.INVISIBLE);
			Output_Video.setVisibility(View.VISIBLE);
			Intent intentVideo = new Intent(getBaseContext(), FileDialog.class);
			intentVideo.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
			intentVideo.putExtra(FileDialog.FORMAT_FILTER, new String[] { "mp4", "avi","3gp","gif","mkv"});
			createToast("Select a video", false);
			startActivityForResult(intentVideo, PICK_VIDEO);
			return true;
		case R.id.Picture:
			isImage=true;
			Input_Image.setVisibility(View.VISIBLE);
			Input_Video.setVisibility(View.INVISIBLE);
			Output_Image.setVisibility(View.VISIBLE);
			Output_Video.setVisibility(View.INVISIBLE);
			Intent intentPicture = new Intent(getBaseContext(), FileDialog.class);
			intentPicture.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
			intentPicture.putExtra(FileDialog.FORMAT_FILTER, new String[] {"png" , "jpeg" , "jpg" , "bmp"});
			startActivityForResult(intentPicture, PICK_FROM_FILE);
			return true;
		case R.id.Save:
			SharedPreferences settings = getSharedPreferences("Preferences", 0);
			if(isImage){
				if(settings.getBoolean("AutoName", false))
				{
					String filePath = null;
					//save the output bitmap to a file
					File picDir = new File(Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + "/OpenCL/");
					picDir.mkdirs(); //creates directory when needed
					picDir = new File(Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + "/RenderScript/");
					picDir.mkdirs(); //creates directory when needed
					//fileName created after filter
					filePath = Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + File.separator + fileName + ".jpg";            	
					LogFileObject.writeToFile(" File saved to: " + filePath,"LogFile.txt",false);
					FileOutputStream out = null;
					try {
						out = new FileOutputStream(filePath);
						outBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
						createToast("Image saved!",false);	
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try{
							out.close();
						} catch(Throwable ignore) {}
					} 
				}
				else
				{
					//chose file
					Intent intentSaveImage = new Intent(getBaseContext(), FileDialog.class);
					intentSaveImage.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
					intentSaveImage.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" , "jpeg" , "jpg" , "bmp"});
					startActivityForResult(intentSaveImage, REQUEST_SAVE_IMAGE);
				}
			}
			else createToast("Video is already saved", false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/*! \brief Starts the history activity.
	*
	*/
	public void startHistoryActivity()
	{
		Intent intent = new Intent(this,DisplayMessageActivty.class);
		startActivity(intent);
	}
	/*! \brief Decodes and resizes a file to the largest possible bitmap.
	*
	* This function resizes the bitmap from a file to a specified size and uses as less memory as possible.
	* @param f is the directory to the image file to be converted
	* @param Req_Height is the required hight
	* @param Req_Width is the required width
	* @return The bitmap from the file
	*/
	public static Bitmap decodeAndResizeFile(File f,int Req_Height, int Req_Width) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < Req_Width || height_tmp / 2 < Req_Height)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	public class ConnectTask extends AsyncTask<String, String, TcpClient> {
		/*! \brief Processing of incoming TCP messages
		 *
	   * A new TCPClient object is created, and the interface onMessageReceived is implemented. Here incoming TCP messages
	   * are received and can be processed. The communication with the server is always initiated by the client, which means
	   * when we receive a message we know a request has been send by the user. 
	   * If the response is "Successful" the RenderScript code is successfully compiled by the server and we
	   * ask the server if the bytecode is present on the FTP server. If the server sends "UPLOADED" communication 
	   * is made with the FTP server and the bytecode is downloaded to the application.<br>
	   * Besides the runtime compilation, we also receive feedback from the server concerning login requests and account creation.
	   * @param message the message received from the server
	   * 
	   */
		@Override
		protected TcpClient doInBackground(String... message) {
			//we create a TCPClient object
			mTcpClient = new TcpClient(MainActivity.this, new TcpClient.OnMessageReceived() {
				@Override
				public void messageReceived(String message) {				
					Log.i("message","messageReceived: " + message);

					if(message.contains("Succesful"))
					{
						/*
						 * the next code is needed in the following situation
						 * the remember user option is selected but the user has not yet logged in via the login button
						 * or during a previous session.
						 * If we receive the Succesful message from the server,we can deduce that the send username en password
						 * were correct and therefore can be stored to the shared preferences file
						 */
						if(settings.getBoolean("rememberUser", false))
						{
							if(username != "" && passwd != "")
							{
								SharedPreferences.Editor editor = settings.edit();
								editor.putString("userName",username);
								editor.putString("passwd", passwd);
								editor.commit();
							}
						}
						
						ConsoleView.setText("Build Succesful");
						mTcpClient.sendMessage("give bc");
						Log.i("message","give bc");
					}    
					else if(message.contains("UPLOADED"))
					{
						//LogFileObject.writeToFile(byteCode, "template.bc", true); 
						//Connect to ftp server and fetch te file.
						new Thread(new Runnable() {
							public void run(){
								boolean status = false;
								Log.i("MainAct","FtpThread");
								// Replace your UID & PW here
								Log.i("ftp","ftp connect with " + username + " " + passwd);
								if(settings.getBoolean("UseDefault", true)) {
									IP_ADDR=DEFAULT_IP_ADDR;
								} else {
									IP_ADDR = settings.getString("ServerIP", DEFAULT_IP_ADDR);
								}
								if(!settings.getBoolean("rememberUser", false))
									status = ftpclient.ftpConnect(IP_ADDR, username, createHash(passwd), 21);
								else
									status = ftpclient.ftpConnect(IP_ADDR, settings.getString("userName", ""), createHash(settings.getString("passwd", "")), 21);
								if (status == true) {
									Log.d("FTP", "Connection Success");
									status = ftpclient.ftpDownload("/template.bc", getFilesDir().getPath() + "/template.bc");
									publishProgress("stop");
									if(status){
										publishProgress("updateBitmap");
									}
									else
									{
										//Cannot create toasts in ftpthread
										//http://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare

										//createToast("Downloading failed", true);
									}
								} else {
									publishProgress("stop");
									Log.d("FTP", "Connection failed");
									//createToast("Connection with TCP server failed!", false);
								}
							}
						}).start();
					}
					else if(message.contains("login ok"))
					{
						publishProgress("login_ok");						
					}
					else if(message.contains("login error"))
					{
						publishProgress("login_nok");						
					}
					else if(message.contains("acount error"))
					{
						publishProgress("account_error");
					}
					else if(message.contains("acount created"))
					{
						publishProgress("acount_created");
					}
					else
					{
						if(settings.getBoolean("rememberUser", false))
						{
							if(username != "" && passwd != "")
							{
								SharedPreferences.Editor editor = settings.edit();
								editor.putString("userName",username);
								editor.putString("passwd", passwd);
								editor.commit();
							}
						}
						publishProgress(message);
						Log.i("Error","Error message: " + message);
					}
				}
			});
			mTcpClient.run();

			return null;
		}
	/*! \brief Update parts of the app
		 * When the function publisProgress is called, this funtion gets executed. 
		 * By checking the value variable, we know from where the publisProgress function was called, and can 
		 * update the state of the app accordingly. This can be as simple as showing a Toast letting the user know
		 * his login was successful.
	   * @param values used to determine which part to update
	   * 
	   */	
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			Log.i("onProgressUpdate",values[0]);
			if(values[0] == "updateBitmap"){
				try {
					if(isImage){
						if(OpenCLObject.getBitmap()!=null && isImage)
						{
							RenderScriptObject.RenderScriptTemplate();
							Output_Image.setImageBitmap(RenderScriptObject.getOutputBitmap()); 
						}
						else
						{
							createToast("Select image!",false);					
						}
					}
					else
					{
						Intent intentLoad = new Intent(getBaseContext(), FileDialog.class);
						intentLoad.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM);
						intentLoad.putExtra(FileDialog.FORMAT_FILTER, new String[] {"mp4", "avi","3gp","gif","mkv"});
						intentLoad.putExtra("isRs", false);
						startActivityForResult(intentLoad, REQUEST_PATH);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

			}
			else if(values[0]=="stop")
			{
				dialog.dismiss();
			}
			else if(values[0] == "login_ok")
			{
				if(settings.getBoolean("rememberUser", false))
			    {
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("userName", username);
					editor.putString("passwd", passwd);
					editor.commit();
			    }	
				createToast("Login succesful", false);				
			}
			else if(values[0] == "login_nok")
			{
				createToast("Wrong username or password", false);
				username = "";
				passwd = "";
				
				if(dialog.isShowing())
					dialog.dismiss();
			}
			else if(values[0] == "account_error")				
			{
				createToast("Accountname already in use", false);
			}
			else if(values[0] == "acount_created")
			{
				createToast("Acount created", false);
			}
			else
			{
				dialog.dismiss();
				ConsoleView.append(values[0]);
				myTabHost.setCurrentTabByTag("Console");
			}
		}
	}
	/* \brief Converts bytes to their hex value
	*
	* @param a is the array of bytes to be converted
	* @return Returns the String form of the bytes
	*/
	String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for(byte b: a)
			sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	/* \brief Converts byte to its hex value
	*
	* @param bytes are the bytes to be converted
	* @return Returns the String form of the byte
	*/
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/*! \brief Creates a MD5 hash of the password
	 *
   * Converts the password to a MD5 hash, used for security reasons. Because the Hash consists of hex values 
   * who need to be send over TCP, it's necessary to convert the byte array to a String. The resulting string
   * will be two times as long because each byte in hex is represented as two characters e.g. 01, 0A etc.
   * @param passwd The password used to create the hash value
   * @return strHash The resulting hash as a hex presented string
   */
	public String createHash(String passwd)
	{
		//create hash
		byte[] bytesOfMessage = null;
		try {
			bytesOfMessage = passwd.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] hash= md.digest(bytesOfMessage);

		Log.i("send",byteArrayToHex(hash));

		String strHash = bytesToHex(hash);

		return strHash;

	}
	/*! \brief Message for the server used for the runtime compilation of RenderScript
	 *
  * This function will be called when the user clicks on the submitbutton with the RenderScript Radiobutton selected.<br>
  * The message, beginning with the STARTPACKAGE tag, will contain the username and the hashed password. This is send
  * over TCP to the server, followed by the code from the code field in the app, which is send line per line. The end
  * of the message is indicated with the ENDPACKAGE tag.
  *  
  * @param username name of the user
  * @param passwd the password of the user
  */
	public void sendRenderscriptMessage(String username, String passwd)
	{
		dialog = new ProgressDialog(MainActivity.this);
		dialog.setMessage("Processing. Please wait...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		final Handler handlerUi = new Handler();

		ConsoleView.setText("");

		String message = CodeField.getText().toString();
		String lines[] = message.split("\\r?\\n");
		String strHash = createHash(passwd);

		Log.i("send after conversion",strHash);

		mTcpClient.sendMessage("STARTPACKAGE " + username + " " + strHash + " " + String.valueOf(android.os.Build.VERSION.SDK_INT)+ "\n");


		for(int i=0;i<lines.length;i++)
		{
			mTcpClient.sendMessage(lines[i]);
			Log.i("koen", lines[i]);							
		}
		//separator zodat de code en het ENDPACKAGE bericht niet aan elkaar kunnen hangen
		mTcpClient.sendMessage("\n");
		//wait some time
		handlerUi.postDelayed(new Runnable() {
			@Override
			public void run() {
				mTcpClient.sendMessage("ENDPACKAGE");
				Log.i("ENDPACKAGE","ENDPACKAGE");
			}

		},1000);
	}
	public class EditVideoTask	 extends AsyncTask<String, String, Long> {
		/*! \brief Opens the video file and calls the right RenderScript or OpenCL filter.
		*
		* This is the doInBackground function of an AsyncTask. It's build this way to show the user the app did not crash but it is processing a video.
		* If this would be a normal function, the screen will go black and the user would get no feedback.
		* The function will first call the publishProgress to show the user the app is busy and not crashing. 
		* Then it will open the video from the videoPath, check the frame length and create a FFmpegFrameRecorder to be able to save the image.
		* The grabbers frame will be converted into a Java bitmap and a RenderScript or OpenCL filter will be called. The filter to use will be specified by the createBoxes function.
		* Each frame will be saved in the recorder and saved to a file when all frames have been edited.
		*
		* @param message is not used
		* @return It will always return 0
		*/
		@Override
		protected Long doInBackground(String... message) {
			try {
			if(isRenderScript)
			{
					publishProgress("Load");
					FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath); 
					grabber.start();
					int LengthInFrames = 0;
					int counter = 0;
					while(true)
					{
						if(grabber.grab()==null) break;
						LengthInFrames++;
					}
					publishProgress("Start",String.valueOf(LengthInFrames));
					FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(savePath, grabber.getImageWidth(), grabber.getImageHeight());

					recorder.setFormat("mp4");
					recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
					recorder.setVideoBitrate(33000);
					recorder.setFrameRate(grabber.getFrameRate());				

					IplImage image = IplImage.create(grabber.getImageWidth(), grabber.getImageHeight(), IPL_DEPTH_8U, 4);
					IplImage frame2 = IplImage.create(image.width(), image.height(), IPL_DEPTH_8U, 4);
					Bitmap MyBitmap = Bitmap.createBitmap(frame2.width(), frame2.height(), Bitmap.Config.ARGB_8888);   
					recorder.start();

					grabber.setFrameNumber(0);
					while(true)
					{					
						image = grabber.grab();
						if(image==null)
						{
							break;
						}
						opencv_imgproc.cvCvtColor(image, frame2, opencv_imgproc.CV_BGR2RGBA);
						MyBitmap.copyPixelsFromBuffer(frame2.getByteBuffer());
						if(!isRuntime)
						{
							RenderScriptObject.setInputBitmap(MyBitmap);
							m.invoke(RenderScriptObject, null);
							MyBitmap = RenderScriptObject.getOutputBitmap();		            	
						}
						else if(isRuntime)
						{
							RenderScriptObject.setInputBitmap(MyBitmap);
							RenderScriptObject.RenderScriptTemplate();
							MyBitmap = RenderScriptObject.getOutputBitmap();							
						}
						MyBitmap.copyPixelsToBuffer(frame2.getByteBuffer());
						opencv_imgproc.cvCvtColor(frame2, image, opencv_imgproc.CV_RGBA2BGR);		            
						recorder.record(image);
						counter++;
						publishProgress(String.valueOf(counter));
					}
					recorder.stop();
					grabber.stop();	
					RenderScriptObject.saturationValue=-1;
					publishProgress("Done");		
			}
			else
			{
				OpenCLObject = new OpenCL(MainActivity.this,(ImageView)findViewById(R.id.ImageView2),new OpenCL.OnUpdateProcessBar() {
					@Override
					public void updateProcessBar(String message) {
							if(message.contains("Load")){
								publishProgress("Load");
							} else if(message.contains("Start")) {
								String[] frameCount = message.split("\\s+");
								publishProgress("Start",frameCount[1]);
							} else if(message.contains("Done")) {
								publishProgress("Done");
							} else publishProgress(message);
						}
				}); 
				//m.invoke(OpenCLObject,new Object[]{OpenCLVideoArguments});
				String[] bla = {"sharpen",null};
				OpenCLObject.OpenCLVideo(bla);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		/*! \brief This onProgressUpdate function is linked to the doInBackground function of the EditVideoTask task.
		* 	It is the link between the asynctask and the GUI
		* 
		* When publishProgress is called, this function will be called. It will update the GUI specified by the values argument.
		* @param values is a string list and is a way to controle the output of this function
		*/
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if(values[0]=="Done"){
				videoProcessDialog.dismiss();
				mediaControllerOut = new MediaController(MainActivity.this);
				mediaControllerOut.setAnchorView(Output_Video);
				videoOut = Uri.parse(new File(savePath).toString());
				Output_Video.setMediaController(mediaControllerOut);
				Output_Video.setVideoURI(videoOut);
			} else if(values[0]=="Start"){
				videoProcessDialog.dismiss();
				videoProcessDialog = new ProgressDialog(MainActivity.this);
				videoProcessDialog.setMessage("Editing the video. Please wait.");
				videoProcessDialog.setIndeterminate(false);
				videoProcessDialog.setCancelable(false);
				videoProcessDialog.setCanceledOnTouchOutside(false);
				videoProcessDialog.setMax(Integer.valueOf(values[1]));
				videoProcessDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				videoProcessDialog.show();
			} else if(values[0]=="Load") {
				videoProcessDialog = new ProgressDialog(MainActivity.this);
				videoProcessDialog.setMessage("Loading JavaCV library's.");	
				videoProcessDialog.setCancelable(false);
				videoProcessDialog.setCanceledOnTouchOutside(false);
				videoProcessDialog.show();
			} else {
				videoProcessDialog.setProgress(Integer.valueOf(values[0]));
			}
		}
	}
}
