package com.medusa.checkit.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	Context context;
	Intent intent;
	String allChecklistsJSONString;
	String allStepsJSONString;
	ArrayList<String> allStepsJSONStringArray;
	ArrayList<String[]> checklistsArray;
	ArrayList<String[]> stepsArray;
	ArrayList<String[]> allStepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// Removes title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				
		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
				
		setContentView(R.layout.activity_splash);
		context = getApplicationContext();

		IntentLauncher launcher = new IntentLauncher();
		launcher.start();
	}

	private class IntentLauncher extends Thread {
		
		@Override
		public void run() {
			
			HTTPGetRequest getRequest = new HTTPGetRequest();
			try {
				
				JSONWriter writer = new JSONWriter(context);
				JSONReader reader = new JSONReader(context);
				
				allChecklistsJSONString = getRequest.getChecklists(1);
				
				writer.writeToInternal(allChecklistsJSONString);
				reader.readFromInternal(JSONWriter.FILENAME);
				
				checklistsArray = reader.getChecklistsArray();
				
				allStepsJSONStringArray = new ArrayList<String>();
				allStepsArray = new ArrayList<String[]>();
				
				// Adds JSON string of steps for each checklist into ArrayList
				String[] checklist;
				for (int i = 0; i < checklistsArray.size(); i++) { 
					checklist = checklistsArray.get(i);
					allStepsJSONString = getRequest.getSteps(Integer.parseInt(checklist[0]));
					allStepsJSONStringArray.add(allStepsJSONString);
				}
				
				// Adds all steps for all checklists into an ArrayList
				for (int i = 0; i < allStepsJSONStringArray.size(); i++) {
					writer.writeToInternal(allStepsJSONStringArray.get(i));
					reader.readFromInternal(JSONWriter.FILENAME); 
					stepsArray = reader.getStepsArray();
					
					for (int s = 0; s < stepsArray.size(); s++) {
						allStepsArray.add(stepsArray.get(s));
					}
				}
				
				for (int i = 0; i < allStepsArray.size(); i++) {
					Log.v("Checklist Steps", Arrays.toString(allStepsArray.get(i)));
				}
				
			} catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }

			// Passes checklist and step arrays to main menu
			intent = new Intent(context, MainMenuActivity.class);
			intent.putExtra("checklists", checklistsArray);
			intent.putExtra("steps", allStepsArray);
			
			// Start main menu activity
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
	}

}
