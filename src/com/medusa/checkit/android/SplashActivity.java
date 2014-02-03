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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	    	try { Thread.sleep(1000); } 
			catch (Exception e) { e.printStackTrace(); }
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	
	    	if (isNetworkAvailable()) { 
	    		Toast updatingFiles = Toast.makeText(context, "Updating files", Toast.LENGTH_SHORT);
	    		updatingFiles.show();
	    		new UpdateFiles().execute(); 
	    		
	    		checkForNonUploadedChecklists();
    		}
	    	else {
	    		Toast noNetwork = Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT);
	    		noNetwork.show();
				
				if (checkIfFileExists(FILENAME_CHECKLISTS)) {
					createChecklistArray();
					startActivity();
				}
				else {
					Toast noFileFound = Toast.makeText(context, "No files found locally. Please connect to the internet and try again.", Toast.LENGTH_LONG);
					noFileFound.show();
					finish();
				}
	    	}

	        return;
	    }
	}

	private class UpdateFiles extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	
	    	// Updates local JSON file containing checklists
	    	String checklistsJsonString = "";
	    	
			try { checklistsJsonString = getRequest.getChecklists(GROUP_ID); }
			catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
			
			try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
	    	createChecklistArray();
	    	
			// Updates JSON file of steps for each checklist into individual files 
			Checklist checklistHolder;
			String stepsJsonString = "";
			for (int i = 0; i < checklistsArray.size(); i++) { 
				checklistHolder = checklistsArray.get(i);

				try { stepsJsonString = getRequest.getSteps(checklistHolder.getId()); }
				catch (MalformedURLException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); }
				
				String filename = "cid" + Integer.toString(checklistHolder.getId()) + "_steps.json";
				try { writer.writeToInternal(filename, stepsJsonString); }
				catch (IOException e) { e.printStackTrace(); }
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	startActivity();
	        return;
	    }
	}
	
	private boolean checkIfFileExists(String filename) {
		File file = context.getFileStreamPath(filename);
		return file.exists(); 
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
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void checkForNonUploadedChecklists() {
		String[] savedFiles = getApplicationContext().fileList();
		
		for (int i = 0; i < savedFiles.length; i++) {
			String filename = savedFiles[i];
			if (filename.contains("finished")) {
				new PostToServerThread(filename).execute();
			}
		}
	}
	
	private class PostToServerThread extends AsyncTask<Void, Void, Void> {

		String filename;
		
		PostToServerThread(String filename) {
			this.filename = filename;
		}
		
	    protected Void doInBackground(Void... params) {
			if (isNetworkAvailable()) {
				final HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
				post.createNewPost(); 
				post.addJSON(filename);
//				if (imageHandler.getArrayList() != null) { post.addPictures(imageHandler.getArrayList()); }
				post.sendPost();
				
				runOnUiThread(new Runnable() {
					public void run() {
						showUploadMessage(post.getResponseCode());
					}
				});
				
				File fileToDelete = new File(getFilesDir(), filename);
				boolean deleted = fileToDelete.delete();
				if (deleted) { Log.v("CHECKLIST DELETED", filename); }
			}
//			else {
//				NetworkErrorDialogFrament dialog = new NetworkErrorDialogFrament();
//				dialog.show(getFragmentManager(), "networkError");
//			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	        return;
	    }
	}
	
	private void showUploadMessage(int responseCode) {
		if (responseCode == 200) {
			Toast.makeText(getApplicationContext(), "Checklist uploaded successfully!", Toast.LENGTH_SHORT).show();
		}
	}
	
}
