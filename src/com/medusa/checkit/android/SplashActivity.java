package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final int GROUP_ID = 1;

	Context context;
	HTTPGetRequest getRequest;
	JSONReader reader;
	JSONWriter writer;
	ArrayList<Checklist> checklistsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// Removes title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				
		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
				
		setContentView(R.layout.activity_splash);
		
		context = getApplicationContext();
		getRequest = new HTTPGetRequest();
		reader = new JSONReader(context);
		writer = new JSONWriter(context);
		checklistsArray = new ArrayList<Checklist>();
		
		new ShowLogo().execute();
	}
	
	private class ShowLogo extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	try { Thread.sleep(500); } 
			catch (Exception e) { e.printStackTrace(); }
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	
	    	if (Utilities.isNetworkAvailable(getApplicationContext())) { 
	    		new UpdateFiles().execute();
	    		checkForNonUploadedChecklists();
    		}
	    	else {
	    		Toast.makeText(context, "Network Error: No Connectivity", Toast.LENGTH_SHORT).show();
				
				if (Utilities.checkIfFileExists(getApplicationContext(), FILENAME_CHECKLISTS)) {
					createChecklistArray();
					startActivity();
				}
				else {
					Toast.makeText(context, "No files found locally. Please connect to the internet and try again.", Toast.LENGTH_LONG).show();
					finish();
				}
	    	}

	        return;
	    }
	}

	private class UpdateFiles extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(SplashActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Updating files. Please wait.");
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	
	    	// Updates local JSON file containing checklists
	    	String checklistsJsonString = "";
	    	
			checklistsJsonString = getRequest.getChecklists(GROUP_ID);
			
			try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
	    	createChecklistArray();
	    	
			// Updates JSON file of steps for each checklist into individual files 
			Checklist checklistHolder;
			String stepsJsonString = "";
			for (int i = 0; i < checklistsArray.size(); i++) { 
				checklistHolder = checklistsArray.get(i);

				stepsJsonString = getRequest.getSteps(checklistHolder.getId());
				
				String filename = "cid" + Integer.toString(checklistHolder.getId()) + "_steps.json";
				try { writer.writeToInternal(filename, stepsJsonString); }
				catch (IOException e) { e.printStackTrace(); }
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	startActivity();
	        return;
	    }
	}
	
	private void createChecklistArray() {
		try { 
			reader.readFromInternal(FILENAME_CHECKLISTS);
			checklistsArray = reader.getChecklistsArray();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void startActivity() {
		Intent intent = new Intent(context, SelectChecklistActivity.class);
		intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
		
		startActivity(intent);
		finish();
	}
	
	private void checkForNonUploadedChecklists() {
		String[] savedFiles = getApplicationContext().fileList();
		
		for (int i = 0; i < savedFiles.length; i++) {
			String filename = savedFiles[i];
			if (filename.contains("finished")) {
				JSONReader jReader = new JSONReader(context);
				ArrayList<String> imgFilenames = new ArrayList<String>();
				
				try { 
					jReader.readFromInternal(filename);
					imgFilenames = jReader.getImageFilenamesArray();
				} 
				catch (IOException e) { e.printStackTrace(); }
				
				new PostToServerThread(filename, imgFilenames).execute();
			}
		}
	}
	
	private class PostToServerThread extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		String filename;
		ArrayList<String> imgFilenames;
		
		PostToServerThread(String filename, ArrayList<String> imgFilenames) {
			this.filename = filename;
			this.imgFilenames = imgFilenames;
		}
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(SplashActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Uploading files. Please wait.");
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
			if (Utilities.isNetworkAvailable(getApplicationContext())) {
				final HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
				post.createNewPost(); 
				post.addJSON(filename);
				if (!imgFilenames.isEmpty()) { post.addPictures(imgFilenames); }
				post.sendPost();
				
				// Show message from upload
				runOnUiThread(new Runnable() {
					public void run() {
						showUploadMessage(post.getResponseCode());
					}
				});
				
				// Deletes checklist file after uploaded
				Utilities.deleteFile(getApplicationContext(), filename);
				
				// Deletes images after uploaded
				if (!imgFilenames.isEmpty()) {
					for (int i = 0; i < imgFilenames.size(); i++) {
						String imgFilename = imgFilenames.get(i);
						Utilities.deleteFile(getApplicationContext(), imgFilename);
					}
				}
			}
//			else {
//				NetworkErrorDialogFrament dialog = new NetworkErrorDialogFrament();
//				dialog.show(getFragmentManager(), "networkError");
//			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	        return;
	    }
	}
	
	private void showUploadMessage(int responseCode) {
		if (responseCode == 200) {
			Toast.makeText(getApplicationContext(), "Checklist uploaded successfully!", Toast.LENGTH_SHORT).show();
		}
	}
	
}
