package com.medusa.checkit.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

public class GlobalMethods {
	
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
	
	public static boolean hasUnsentChecklist(Context context) {
		String[] savedFiles = context.fileList();
		
		for (int i = 0; i < savedFiles.length; i++) {
			String filename = savedFiles[i];			
			if (filename.contains("finished")) { return true; }
		}
		return false;
	}
	
	public static class UpdateFilesTask extends AsyncTask<Void, Void, Void> {
		
		private static final int GROUP_ID = 1;
		private static final String FILENAME_CHECKLISTS = "checklists.json";
		
		private Context context;
		private ProgressDialog progressDialog;
		
		public UpdateFilesTask(Context context) {
			this.context = context;
		}
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(context);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(context.getResources().getString(R.string.msg_updating_files));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	// Updates local JSON file containing checklists
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	String checklistsJsonString = getRequest.getChecklists(GROUP_ID);
			
			JSONWriter writer = new JSONWriter(context);
			try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
			ArrayList<Checklist> checklistsArray = new ArrayList<Checklist>();
			checklistsArray = createChecklistArray(context);
			
			// Updates JSON file of steps for each checklist into individual files 
			for (int i = 0; i < checklistsArray.size(); i++) { 
				Checklist checklist = checklistsArray.get(i);
				String stepsJsonString = getRequest.getSteps(checklist.getId());
				
				String filename = "cid" + Integer.toString(checklist.getId()) + "_steps.json";
				try { writer.writeToInternal(filename, stepsJsonString); }
				catch (IOException e) { e.printStackTrace(); }
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	        return;
	    }
	}
	
	public static ArrayList<Checklist> createChecklistArray(Context context) {
		final String FILENAME_CHECKLISTS = "checklists.json";
		
		ArrayList<Checklist> array = new ArrayList<Checklist>();
		try { 
			JSONReader reader = new JSONReader(context);
			String jsonString = reader.readFromInternal(FILENAME_CHECKLISTS);
			array = reader.getChecklistsArray(jsonString);
		} 
		catch (IOException e) { e.printStackTrace(); }
		return array;
	}
	
}
