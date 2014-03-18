package com.denayer.ovsr;

import java.io.File;
import java.io.FileOutputStream;
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

    private RadioButton RenderScriptButton;
    private RadioButton OpenCLButton;
    public TextView TimeView;
    private String fileName;
    
    OpenCL OpenCLObject;
    RsScript RenderScriptObject;
    LogFile LogFileObject;    
    //item in de lijst toevoegen voor nieuwe filters toe te voegen.
    private String [] itemsFilterBox           = new String [] {"Edge", "Inverse","Sharpen","Mediaan","Saturatie","Blur"};
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TimeView=(TextView)findViewById(R.id.timeview);
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
            	String filePath = Environment.getExternalStorageDirectory() + File.separator + android.os.Environment.DIRECTORY_DCIM + File.separator + fileName;            	
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
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path     = "";
 
        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery
 
            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager
 
            if (path != null)
                bitmap  = BitmapFactory.decodeFile(path);
        } else {
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
    	        fileName = itemsFilterBox[item] + formatter.format(now) + ".jpg";
				if(!RenderScriptButton.isChecked())
				{				
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void startHistoryActivity()
	{
		Intent intent = new Intent(this,DisplayMessageActivty.class);
		startActivity(intent);
	}

}