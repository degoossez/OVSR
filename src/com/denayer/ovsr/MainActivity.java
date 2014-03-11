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
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;


public class MainActivity extends Activity {
    private Uri mImageCaptureUri;
    private ImageButton Input_button;
    private ImageButton Output_button;
    private Bitmap bitmap   = null;
    private File file;
    
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    private RadioButton RenderScriptButton;
    private RadioButton OpenCLButton;
    
    OpenCL OpenCLClass;
    RenderScript RenderScriptClass;
    
    //item in de lijst toevoegen voor nieuwe filters toe te voegen.
    private String [] itemsFilterBox           = new String [] {"Edge", "Inverse","Sharpen","Mediaan","Saturatie"};

	public void RenderScriptEdge()
	{
		Log.i("Testing","RenderScriptEdge");
	}
	public void OpenCLEdge()
	{
		Log.i("Testing","OpenCL werkt ook al.. Goed bezig Dries!");
	}    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_main);
        
        OpenCLClass = new OpenCL();
        RenderScriptClass = new RenderScript();
        
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
        	        String fileName = formatter.format(now) + ".jpg";
                    file = new File(SavePath, "OVSR"+fileName);
                    
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
        
         
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Input_button = (ImageButton)findViewById(R.id.imageButton1);
        Input_button.setImageBitmap(bitmap);
        Output_button = (ImageButton)findViewById(R.id.imageButton2);
        Output_button.setImageBitmap(Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888));
        
        /*
         * TODO
         * Functie's voor bitmaps Renderscript en OpenCL class aan te passen.
         * vb Renderscript.setBitmap(bitmap)
         */
        
        RenderScriptClass.setInputBitmap(bitmap);
        
        
        
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
				if(!RenderScriptButton.isChecked())
				{
					String FunctionName = "RenderScript" + itemsFilterBox[item];
					try {
						//MainActivity obj = new MainActivity();
						Method m = RenderScript.class.getMethod(FunctionName);
						try {
							m.invoke(RenderScriptClass, null);
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
					String FunctionName = "OpenCL" + itemsFilterBox[item];
					try {
						//MainActivity obj = new MainActivity();
						Method m = OpenCL.class.getMethod(FunctionName);
						try {
							m.invoke(OpenCLClass, null);
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
}