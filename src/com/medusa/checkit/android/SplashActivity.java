package com.medusa.checkit.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_ALL_STEPS = "allSteps";
	private static final int GROUP_ID = 1;

	Context context;
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
		context = getApplicationContext();
		checklistsArray = new ArrayList<Checklist>();
		
		// Read existing local JSON first, if not existing, get checklists from server
		readChecklistsFromFile();
		if (checklistsArray.isEmpty()) {
			new GetChecklistsFromServer().execute();
		}

//		IntentLauncher launcher = new IntentLauncher();
//		launcher.start();
	}

	private void readChecklistsFromFile() {
		JSONReader reader = new JSONReader(context);
		try { 
			reader.readFromInternal(FILENAME_CHECKLISTS);
			checklistsArray = reader.getChecklistsArray();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public class GetChecklistsFromServer extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	String allChecklistsJSONString = "";
			HTTPGetRequest getRequest = new HTTPGetRequest();
			try { allChecklistsJSONString = getRequest.getChecklists(GROUP_ID); }
			catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
			
			JSONWriter writer = new JSONWriter(context);
			try { writer.writeToInternal(FILENAME_CHECKLISTS, allChecklistsJSONString); } 
			catch (IOException e) { e.printStackTrace(); }
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	readChecklistsFromFile();

			Intent intent = new Intent(context, SelectChecklistActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
//			intent.putExtra(KEY_ALL_STEPS, allStepsArray);
			
			context.startActivity(intent);
			finish();
	        return;
	    }
	}
	
//	private class IntentLauncher extends Thread {
//		@Override
//		public void run() {
//			
//			try {
//				
//				// Adds JSON string of steps for each checklist into ArrayList<Checklist>
//				Checklist checklistHolder;
//				String stepsJSONString;
//				ArrayList<String> allStepsJSONStringArray = new ArrayList<String>();
//				for (int i = 0; i < checklistsArray.size(); i++) { 
//					checklistHolder = checklistsArray.get(i);
//					stepsJSONString = getRequest.getSteps(checklistHolder.getId());
//					allStepsJSONStringArray.add(stepsJSONString);
//				}
//				
//				// Adds all steps for all checklists into an ArrayList<Step>
//				Step stepHolder;
//				ArrayList<Step> stepsArray;
//				allStepsArray = new ArrayList<Step>();
//				for (int i = 0; i < allStepsJSONStringArray.size(); i++) {
//					writer.writeToInternal("steps.json", allStepsJSONStringArray.get(i));
//					reader.readFromInternal("steps.json"); 
//					stepsArray = reader.getStepsArray();
//					
//					checklistHolder = checklistsArray.get(i);
//					checklistHolder.setNumOfSteps(stepsArray.size());
//					
//					for (int s = 0; s < stepsArray.size(); s++) {
//						stepHolder = stepsArray.get(s);
//						allStepsArray.add(stepHolder);
//					}
//				}
//				
//				for (int i = 0; i < allStepsArray.size(); i++) {
//					Step holder = allStepsArray.get(i);
//					Log.v("ALL Steps", holder.getName());
//				}
//			} catch (MalformedURLException e) { e.printStackTrace(); } 
//			catch (IOException e) { e.printStackTrace(); }
//
//			Intent intent = new Intent(context, SelectChecklistActivity.class);
//			intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
//			intent.putExtra(KEY_ALL_STEPS, allStepsArray);
//			
//			startActivity(intent);
//			finish();
//		}
//	}
	
	
	

}
