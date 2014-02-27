package com.denayer.ovsr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.denayer.ovsr.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
    private Uri mImageCaptureUri;
    private ImageButton Input_button;
    private ImageButton Output_button;
    private Bitmap bitmap   = null;
    private File file;
    
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    
    static boolean sfoundLibrary = true;
    
   
    static {
        try { 
        	System.load("/system/vendor/lib/libPVROCL.so");
        	 Log.i("Debug", "Libs Loaded");
      	
        	//System.loadLibrary("CLDeviceTest");  
        }
        catch (UnsatisfiedLinkError e) {
          sfoundLibrary = false;
        }
      }
	static boolean sfoundMyLibrary = true;  
	
	static {
	  try {
		  System.loadLibrary("OVSR");  
		  Log.i("Debug","My Lib Loaded!");
	  }
	  catch (UnsatisfiedLinkError e) {
	      sfoundMyLibrary = false;
	      Log.e("Debug", "Error log", e);
	  }
	}
    public static native int runOpenCL(Bitmap bmpIn, Bitmap bmpOut, int info[]);	
    
    //example camerafilter
    private native int cameraFilter(int w, int h, ByteBuffer input, ByteBuffer output, ByteBuffer prog);  
    
	final int info[] = new int[3]; // Width, Height, Execution time (ms)

    LinearLayout layout;
    Bitmap bmpOrig, bmpOpenCL, bmpNativeC;
    ImageView imageView;
    TextView textView;
    
    //example camerafilter
    int pictureWidth, pictureHeight;
    ByteBuffer inputBuffer, outputBuffer, progBuffer;
    byte[] pictureData;
    Bitmap output;  
    
	private void copyFile(final String f) {
		InputStream in;
		try {
			in = getAssets().open(f);
			final File of = new File(getDir("execdir",MODE_PRIVATE), f);
			
			final OutputStream out = new FileOutputStream(of);

			final byte b[] = new byte[65535];
			int sz = 0;
			while ((sz = in.read(b)) > 0) {
				out.write(b, 0, sz);
			}
			in.close();
			out.close();
		} catch (IOException e) {       
			e.printStackTrace();
		}
	}	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_main);
 
        final String [] items           = new String [] {"From Camera", "From Storage"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);
 
        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
			public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //external storage check
                    String SavePath;
                    String state = Environment.getExternalStorageState();

                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        // We can read and write the media
                        SavePath = Environment.getExternalStorageDirectory().toString();
                    } else {
                        // Something else is wrong. It may be one of many other states, but all we need
                        //  to know is we can neither read nor write
                        SavePath = Environment.getDataDirectory().toString();
                    }
                    
                    //save file
                    //SavePath = Environment.getExternalStorageDirectory().toString();
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
        
         
        //bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.brusigablommor);
        Input_button = (ImageButton)findViewById(R.id.imageButton1);
        Input_button.setImageBitmap(bitmap);
        Output_button = (ImageButton)findViewById(R.id.imageButton2);
        //Output_button.setImageBitmap(bitmap);
        
        //OpenCL Test
        copyFile("bilateralKernel.cl"); //copy cl kernel file from assets to /data/data/...assets

        //bmpOrig = bitmap;
//        bmpOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.brusigablommor);
//        info[0] = bmpOrig.getWidth();
//        info[1] = bmpOrig.getHeight();
//        bmpOpenCL = bitmap;
        //OpenCL Test
        
        //example camerafilter
        output = bitmap;
        inputBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        outputBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        output.copyPixelsToBuffer(inputBuffer);
        inputBuffer.rewind();
        //example camerafilter
        
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
        final String [] itemsEdgeBox           = new String [] {"Opencl", "Renderscript"};
        ArrayAdapter<String> adapterEdgeBox  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,itemsEdgeBox);
        AlertDialog.Builder builderEdgeBox     = new AlertDialog.Builder(this);
 
        builderEdgeBox.setTitle("Select Language");
        builderEdgeBox.setAdapter( adapterEdgeBox, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialogEdgeBox, int item ) {
                if (item == 0) {
                	//opencl
//                	Log.i("DEBUG","BEFORE runOpencl");
//                	runOpenCL(bmpOrig, bmpOpenCL, info);
//                	Log.i("DEBUG","AFTER runOpencl");
//                	Output_button.setImageBitmap(bmpOpenCL);
                	
                	//example camerfilter
                    int err = cameraFilter(pictureWidth, pictureHeight, inputBuffer, 
                                           outputBuffer, progBuffer);
                    output.copyPixelsFromBuffer(outputBuffer);
                    outputBuffer.rewind();
                    Output_button.setImageBitmap(output);
                } else {
                	//renderscipt
                }
            }
        } );
        final AlertDialog dialogEdgeBox = builderEdgeBox.create();
        
        (findViewById(R.id.EdgeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEdgeBox.show();
            }
        });
        
        ArrayAdapter<String> adapterSharpenBox  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,itemsEdgeBox);        
        AlertDialog.Builder builderSharpenBox     = new AlertDialog.Builder(this);
        builderSharpenBox.setTitle("Select Language");
        builderSharpenBox.setAdapter( adapterSharpenBox, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialogEdgeBox, int item ) {
                if (item == 0) {
                	//opencl
                } else {
                	//renderscipt
                }
            }
        } );
        final AlertDialog dialogSharpenBox = builderSharpenBox.create();
        
        (findViewById(R.id.SharpenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSharpenBox.show();
            }
        });
        ArrayAdapter<String> adapterMediaanBox  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,itemsEdgeBox);        
        AlertDialog.Builder builderMediaanBox     = new AlertDialog.Builder(this);
        builderMediaanBox.setTitle("Select Language");
        builderMediaanBox.setAdapter( adapterMediaanBox, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialogEdgeBox, int item ) {
                if (item == 0) {
                	//opencl
                } else {
                	//renderscipt
                }
            }
        } );
        final AlertDialog dialogMediaanBox = builderSharpenBox.create();
        
        (findViewById(R.id.MediaanButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	dialogMediaanBox.show();
            }
        });
        //einde choose box
	}
}