package com.denayer.ovsr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private Uri mImageCaptureUri;
    private ImageButton Input_button;
    private ImageButton Output_button;
    private Bitmap bitmap   = null;
    private Bitmap outBitmap   = null;
    private File file;
    private String Filter;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int ACTIVITY_CHOOSE_FILE = 3;

    private Button SubmitButton;
    private RadioButton RenderScriptButton;
    private RadioButton OpenCLButton;
    private EditText CodeField;
    public TextView TimeView;
    private String fileName;
    private String CodeFieldCode;
    
    OpenCL OpenCLObject;
    RsScript RenderScriptObject;
    LogFile LogFileObject;   
    //item in de lijst toevoegen voor nieuwe filters toe te voegen.
    private String [] itemsFilterBox           = new String [] {"Edge", "Inverse","Sharpen","Mediaan","Saturatie","Blur"};
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SubmitButton=(Button) findViewById(R.id.submit_button);
        TimeView=(TextView)findViewById(R.id.timeview);
        CodeField=(EditText)findViewById(R.id.editText1);
        OpenCLObject = new OpenCL(MainActivity.this,(ImageButton)findViewById(R.id.imageButton2));
        RenderScriptObject = new RsScript(this,(ImageButton)findViewById(R.id.imageButton2),TimeView);
        LogFileObject = new LogFile(this);   
        
        RenderScriptButton = (RadioButton) findViewById(R.id.radioButton1);
        OpenCLButton = (RadioButton) findViewById(R.id.radioButton2);
        
        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);
 
        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
			public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //save file
                    String SavePath = Environment.getExternalStorageDirectory().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        	        Date now = new Date();
        	        String fileCameraName = formatter.format(now) + ".jpg";
                    file = new File(SavePath, "OVSR"+fileCameraName);
                    
                    mImageCaptureUri = Uri.fromFile(file);
 
                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
 
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
 
                    dialog.cancel();
                } else {
                    Intent intent = new Intent();
 
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );
 
        final AlertDialog dialog = builder.create();
 
        ((ImageButton) findViewById(R.id.imageButton1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        ((ImageButton) findViewById(R.id.imageButton2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  
            	//save the output bitmap to a file
            	File picDir = new File(Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + "/OpenCL/");
            	picDir = new File(Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + "/RenderScript/");
            	picDir.mkdirs(); //creates directory when needed
            	//fileName created after filter
            	String filePath = Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + File.separator + fileName + ".jpg";            	
				LogFileObject.writeToFile("		File saved to: " + filePath);
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
        }); 
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
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(CodeField.getText().toString()!=""){
					if(!RenderScriptButton.isChecked()) 
					{
						RenderScriptObject.codeFromFile(CodeField.getText().toString());
						if(RenderScriptObject.getOutputBitmap()!=null)
						{
						outBitmap = RenderScriptObject.getOutputBitmap();
						Output_button.setImageBitmap(RenderScriptObject.getOutputBitmap());
						}
						else createToast("Select image!",false);	
					}
					else 
					{
						if(OpenCLObject.getOpenCLSupport())
						{		
							OpenCLObject.codeFromFile(CodeField.getText().toString());	
							if(OpenCLObject.getBitmap()!=null)
							{
							outBitmap = OpenCLObject.getBitmap();
							Output_button.setImageBitmap(outBitmap);
							Log.i("Main","setImageBitmap done");
							}
							else createToast("Select image!",false);					
						}
						else createToast("No OpenCL support!",false);
					}
            	}
            	
            }
        });
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path     = "";
        if(requestCode == ACTIVITY_CHOOSE_FILE)
        {
	          Uri uri = data.getData();
	          String PathLoadFile = getRealPathFromURI(uri);
	          String FileContent = null;
	          Log.i("Debug",PathLoadFile);
	          FileContent = LogFileObject.readFromFile(PathLoadFile);
	          CodeField.setText(FileContent);
        }
        else if (requestCode == PICK_FROM_FILE) {
        	Log.i("Debug","PICK_FROM_FILE");
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery
 
            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager

            if (path != null) {
            	File f = new File(path);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x/2 - 15;
                int height = size.y/2 - 15;
                bitmap = decodeAndResizeFile(f,height,width);
                //bitmap  = BitmapFactory.decodeFile(path);               
            	}
        } else {
        	bitmap.recycle();
            path    = mImageCaptureUri.getPath();
            bitmap  = BitmapFactory.decodeFile(path);
            try {                
                FileOutputStream out = new FileOutputStream(file);
                int BHeight = bitmap.getHeight()/2;
                int BWidth = bitmap.getWidth()/2;                	
                bitmap = Bitmap.createScaledBitmap(bitmap, BWidth, BHeight, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                
                MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

    	     } catch (Exception e) {
    	            e.printStackTrace();
    	     }
        }
        if(requestCode != ACTIVITY_CHOOSE_FILE)
        {
        Log.i("Debug","Testing");
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x/2 - 15;
        int height = size.y/2 - 15;
        Log.i("Debug","Width: " + width + " " + "Height: " + height);
         
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Input_button = (ImageButton)findViewById(R.id.imageButton1);
        Input_button.setImageBitmap(bitmap);
        Output_button = (ImageButton)findViewById(R.id.imageButton2);
        Output_button.setImageBitmap(Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888));

        OpenCLObject.setBitmap(bitmap);
        RenderScriptObject.setInputBitmap(bitmap);
        }      
        System.gc();
    }
	public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
		Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);
 
        if (cursor == null) return null;
 
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
 
        cursor.moveToFirst();
 
        return cursor.getString(column_index);
    }
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
    	        //fileName = "RenderScript/" + itemsFilterBox[item] + formatter.format(now);
				if(!RenderScriptButton.isChecked())
				{		
	    	        fileName = "RenderScript/" + itemsFilterBox[item] + formatter.format(now);
					String FunctionName = "RenderScript" + itemsFilterBox[item];
					Filter = "RenderScript/" + itemsFilterBox[item];
					try {
						Method m = RsScript.class.getMethod(FunctionName);
						try {
							m.invoke(RenderScriptObject, null);
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
					if(RenderScriptObject.getOutputBitmap()!=null)
					{
					outBitmap = RenderScriptObject.getOutputBitmap();
					Output_button.setImageBitmap(RenderScriptObject.getOutputBitmap());
					}
					else
					{
						createToast("Select image!",false);	
					}
				}
				else
				{
					if(OpenCLObject.getOpenCLSupport())
					{		
		    	        fileName = "OpenCL/" + itemsFilterBox[item] + formatter.format(now);
						String FunctionName = "OpenCL" + itemsFilterBox[item];
						Filter = "OpenCL/" + itemsFilterBox[item];
						try {
							//MainActivity obj = new MainActivity();
							Method m = OpenCL.class.getMethod(FunctionName);
							try {
								m.invoke(OpenCLObject, null);
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
						if(OpenCLObject.getBitmap()!=null)
						{
						outBitmap = OpenCLObject.getBitmap();
						Output_button.setImageBitmap(outBitmap);
						}
						else
						{
		            	       createToast("Select image!",false);					
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
					if(!RenderScriptButton.isChecked()) Method="RenderScript";
					Log.i("Debug",fileName + ":" + TimeView.getText());
					LogFileObject.writeToFile("\n" + Method + " : " + fileName + " : " + TimeView.getText());
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
        //einde radio buttons
	}
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
	            Log.i("Debug","Pressed on history");
	            startHistoryActivity();
	            return true;
	        case R.id.Template:
	        	if(!RenderScriptButton.isChecked()) CodeField.setText(RenderScriptObject.getTemplate());
	        	else CodeField.setText(OpenCLObject.getTemplate());
	        case R.id.SaveF:
	        	String filePath = Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + File.separator + fileName + ".txt";            	
	        	LogFileObject.writeToFile("		Code file saved to: " + filePath);
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
	        	return true;
	        case R.id.LoadF:
		        Intent chooseFile;
		        Intent intent;
		        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		        chooseFile.setType("file/*");
		        intent = Intent.createChooser(chooseFile, "Choose a file");
		        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
		        return true;        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void startHistoryActivity()
	{
		Intent intent = new Intent(this,DisplayMessageActivty.class);
		startActivity(intent);
	}
	public static Bitmap decodeAndResizeFile(File f,int Req_Height, int Req_Width) {
	    try {
	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

	        // The new size we want to scale to
	        final int REQUIRED_SIZE = 70;

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
}