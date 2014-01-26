package com.medusa.checkit.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;

public class ImageHandler {
	
	Context context;
	File directory;
	ArrayList<String> imageFilenamesArray;
	
	public ImageHandler(Context context) {
		this.context = context;
		this.imageFilenamesArray = null;
	}
	
	public String writeToFile(Bitmap b, int checklistId, int stepOrder) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");
		String timeStamp = sdf.format(new Date());
		
		String filename = "cid" + Integer.toString(checklistId) + 
						  "_so" + Integer.toString(stepOrder) + 
						  "_" + timeStamp + ".jpg";	
		
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
			Log.v("IMAGE FILE WRITTEN", filename);
		} 
		catch (FileNotFoundException e) { Log.d("ERROR", "File not found: " + e.getMessage()); }
		catch (IOException e) { Log.d("ERROR", "Error accessing file: " + e.getMessage()); }
		
		return filename;
	}
	
	public void addFilenameToArray(String filename) {	
		if (imageFilenamesArray == null) { imageFilenamesArray = new ArrayList<String>(); }
		imageFilenamesArray.add(filename);
		Log.v("ADDED IMAGE TO ARRAY", filename);
	}
	
	public ArrayList<String> getArrayList() {
		return imageFilenamesArray;
	}
}
