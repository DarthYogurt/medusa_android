package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	private static final String FILENAME_PREFERENCES = "preferences";
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	private static final String KEY_UPDATED = "updated";
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_ALL_STEPS = "allSteps";
	private static final int GROUP_ID = 1;

	SharedPreferences prefs;
	Context context;
	HTTPGetRequest getRequest;
	JSONReader reader;
	JSONWriter writer;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> allStepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// Removes title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				
		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
				
		setContentView(R.layout.activity_splash);
//		prefs = this.getSharedPreferences(FILENAME_PREFERENCES, Context.MODE_PRIVATE);
//		prefs.edit().putBoolean(KEY_UPDATED, true).commit();
		
		context = getApplicationContext();
		getRequest = new HTTPGetRequest();
		reader = new JSONReader(context);
		writer = new JSONWriter(context);
		checklistsArray = new ArrayList<Checklist>();
		
		// Checks for existing checklists file locally, if not get from server
		if (checkIfFileExists(FILENAME_CHECKLISTS)) { 
			createChecklistArray();
			startActivity(); 
		}
		else { new UpdateFiles().execute(); }
	}

	private class UpdateFiles extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	
	    	updateChecklistFile(GROUP_ID);
	    	createChecklistArray();
			
			// Writes JSON of steps for each checklist into individual files 
			Checklist checklistHolder;
			for (int i = 0; i < checklistsArray.size(); i++) { 
				checklistHolder = checklistsArray.get(i);
				updateStepFile(checklistHolder.getId());
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
	
	private void updateChecklistFile(int groupId) {
		String checklistsJsonString = "";
		try { checklistsJsonString = getRequest.getChecklists(groupId); }
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		
		try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void updateStepFile(int checklistId) {
		String stepsJsonString = "";
		try { stepsJsonString = getRequest.getSteps(checklistId); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		
		String filename = "cid" + Integer.toString(checklistId) + "_steps.json";
		try { writer.writeToInternal(filename, stepsJsonString); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void startActivity() {
		Intent intent = new Intent(context, SelectChecklistActivity.class);
		intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
		
		startActivity(intent);
		finish();
	}
	
}
