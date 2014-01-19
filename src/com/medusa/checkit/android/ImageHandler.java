package com.medusa.checkit.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class ImageHandler {
	
	Context context;
	File directory;
	ArrayList<Bitmap> imagesArray;
	
	public ImageHandler(Context context) {
		this.context = context;
		this.imagesArray = null;
	}
	
	public void writeToFile(Bitmap b, int checklistId, int stepOrder) {
		String filename = "cid" + Integer.toString(checklistId) + "_so" + Integer.toString(stepOrder) + ".jpg";	
		
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} 
		catch (FileNotFoundException e) { Log.d("ERROR", "File not found: " + e.getMessage()); }
		catch (IOException e) { Log.d("ERROR", "Error accessing file: " + e.getMessage()); }
	}
	
	public String getFilename(int checklistId, int stepOrder) {
		return "cid" + Integer.toString(checklistId) + "_so" + Integer.toString(stepOrder) + ".jpg";
	}
	
//	public ArrayList<Bitmap> addToArrayList() {
//		
//	}
}
