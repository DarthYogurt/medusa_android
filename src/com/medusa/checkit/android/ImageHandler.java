package com.medusa.checkit.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class ImageHandler {
	
	Context context;
	File directory;
	String filename;
	
	public ImageHandler(Context context) {
		this.context = context;
		createDirectory();
	}
	
	private void createDirectory() {
		directory = new File(getPath());
		if (!(directory.exists() && directory.isDirectory())) {
			directory.mkdirs();
		}
	}
	
	public String getPath() {
		return context.getFilesDir() + "/images/";
	}
	
	public void setFilename(int checklistId, int stepOrder) {
		filename = "cid" + Integer.toString(checklistId) + "_so" + Integer.toString(stepOrder) + ".jpg"; 
	}
	
	public String getFilename(int checklistId, int stepOrder) {
		return "cid" + Integer.toString(checklistId) + "_so" + Integer.toString(stepOrder) + ".jpg";
	}
	
	public void writeToFile(Bitmap b) {
		File file = new File(directory, filename);
		
		try {
			FileOutputStream fos = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} 
		catch (FileNotFoundException e) { Log.d("ERROR", "File not found: " + e.getMessage()); }
		catch (IOException e) { Log.d("ERROR", "Error accessing file: " + e.getMessage()); }
	}
	
}
