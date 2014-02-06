package com.medusa.checkit.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageHandler {
	
	Context context;
	File directory;
	ArrayList<String> imageFilenamesArray;
	
	public ImageHandler(Context context) {
		this.context = context;
		this.imageFilenamesArray = null;
	}
	
	public static void compressAndScaleImage(File file) {
		try {
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
		        
		    //Decode with inSampleSize
		    BitmapFactory.Options o2 = new BitmapFactory.Options();
		    o2.inSampleSize = scale;
		    Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
		    
		    FileOutputStream fos = new FileOutputStream(file);
		    image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		    
		    try { 
		    	fos.flush(); 
		    	fos.close(); 
	    	} 
		    catch (IOException e) { e.printStackTrace(); }
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	public String writeToFile(Bitmap b, int checklistId, int stepOrder, boolean isExtra) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_HHmmss");
		String timeStamp = sdf.format(new Date());
		String filename = "";
		
		if (!isExtra) {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_" + timeStamp + ".jpg";	
		}
		else {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_extra_" + timeStamp + ".jpg";	
		}
		
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
			Log.i("IMAGE FILE WRITTEN", filename);
		} 
		catch (FileNotFoundException e) { Log.d("ERROR", "File not found: " + e.getMessage()); }
		catch (IOException e) { Log.d("ERROR", "Error accessing file: " + e.getMessage()); }
		
		return filename;
	}
	
	public String getImageFilename(int checklistId, int stepOrder, boolean isExtra) {
		String filename = "";
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_HHmmss");
		String timeStamp = sdf.format(new Date());
		
		if (!isExtra) {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_" + timeStamp + ".jpg";	
		}
		else {
			filename = "cid" + Integer.toString(checklistId) + 
					  "_so" + Integer.toString(stepOrder) + 
					  "_extra_" + timeStamp + ".jpg";	
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
