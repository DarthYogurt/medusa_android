package com.medusa.checkit.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utilities {
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static boolean checkIfFileExists(Context context, String filename) {
		File file = context.getFileStreamPath(filename);
		return file.exists(); 
	}
	
	public static void deleteFileFromInternal(Context context, String filename) {
		File file = new File(context.getFilesDir(), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.i("FILE DELETED", filename); }
	}
	
	public static void deleteFileFromExternal(Context context, String filename) {
		File file = new File(context.getExternalFilesDir(null), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.i("FILE DELETED", filename); }
	}
	
	public static void copyFile(File src, File dst) {
	    FileChannel inChannel = null;
		try { inChannel = new FileInputStream(src).getChannel(); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		
	    FileChannel outChannel = null;
		try { outChannel = new FileOutputStream(dst).getChannel(); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	    
    	try { inChannel.transferTo(0, inChannel.size(), outChannel); } 
    	catch (IOException e) { e.printStackTrace(); }
    	finally {
    		if (inChannel != null) {
    			try { inChannel.close(); } 
    			catch (IOException e) { e.printStackTrace(); }
    		}
				
    		if (outChannel != null) {
	        	try { outChannel.close(); } 
	        	catch (IOException e) { e.printStackTrace(); }
	        }	
	    }
	}
	
	public static String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
	}
	
	public static String getTimeStampForFilename() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_HHmmss");
		String now = sdf.format(new Date());
		return now;
	}
	
}
