package com.medusa.checkit.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

public class ImageHandler {
	
	Context context;
	File directory;
	ArrayList<String> imageFilenamesArray;
	
	public ImageHandler(Context context) {
		this.context = context;
		this.imageFilenamesArray = null;
	}
	
	public static void compressImage(String filepath) {
		try {
			File file = new File(filepath);
			
		    // Decode image size
		    BitmapFactory.Options o = new BitmapFactory.Options();
		    o.inJustDecodeBounds = true;
		    BitmapFactory.decodeStream(new FileInputStream(file), null, o);

		    // The new size we want to scale to
		    final int REQUIRED_SIZE = 300;

		    // Find the correct scale value. It should be the power of 2.
		    int scale = 1;
		    while (o.outWidth/scale/2 >= REQUIRED_SIZE && o.outHeight/scale/2 >= REQUIRED_SIZE) {
		    	scale*=2;
		    }
		        
		    // Decode with inSampleSize
		    BitmapFactory.Options o2 = new BitmapFactory.Options();
		    o2.inSampleSize = scale;
		    Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
		    
	        // Save file
		    FileOutputStream fos = new FileOutputStream(file);
		    bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		    
		    try { fos.flush(); fos.close(); } 
		    catch (IOException e) { e.printStackTrace(); }
		    
		    bm.recycle();
		    System.gc();
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	public static void rotateImage(Context context, String filename) {
		try {
			ExifInterface exif = new ExifInterface(context.getExternalFilesDir(null) + "/" + filename);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			Log.v("ORIENTATION", Integer.toString(orientation));
		} catch (IOException e) { e.printStackTrace(); }
	
	public String getImageFilename(int checklistId, int stepOrder, boolean isExtra) {
		String filename = "";
		
		if (!isExtra) {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_" + Utilities.getTimeStampForFilename() + ".jpg";	
		}
		else {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_extra_" + Utilities.getTimeStampForFilename() + ".jpg";	
		}
		return filename;
	}
	
	public void addFilenameToArray(String filename) {	
		if (imageFilenamesArray == null) { imageFilenamesArray = new ArrayList<String>(); }
		imageFilenamesArray.add(filename);
		Log.i("ADDED IMAGE TO UPLOAD ARRAY", filename);
	}
	
	public ArrayList<String> getArrayList() {
		return imageFilenamesArray;
	}

}
