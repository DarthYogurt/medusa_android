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
	File imageDir;
	File file;
	
	public ImageHandler(Context context) {
		this.context = context;
	}
	
	public void setImageDir() {
		imageDir = new File(context.getFilesDir() + "/images/");
		imageDir.mkdirs();
	}
	
	public String getImageDir() {
		return context.getFilesDir() + "/images/";
	}
	
	public void setImageFile(int checklistId, int stepOrder) {
		String filename = "cid" + Integer.toString(checklistId) + "_sid" + Integer.toString(stepOrder) + ".jpg"; 
		file = new File(imageDir, filename);
	}
	
	public String getImageFile(int checklistId, int stepOrder) {
		return "cid" + Integer.toString(checklistId) + "_sid" + Integer.toString(stepOrder) + ".jpg";
	}
	
	public void writeToFile(Bitmap b) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} 
		catch (FileNotFoundException e) { Log.d("ERROR", "File not found: " + e.getMessage()); }
		catch (IOException e) { Log.d("ERROR", "Error accessing file: " + e.getMessage()); }
	}
}
