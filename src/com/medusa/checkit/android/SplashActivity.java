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
		
		ArrayList<String[]> checklistsArray;
		ArrayList<String[]> allStepsArray;
		
		@Override
		public void run() {
			
			try {
				HTTPGetRequest getRequest = new HTTPGetRequest();
				String allChecklistsJSONString = getRequest.getChecklists(1);
				
				JSONWriter writer = new JSONWriter(context);
				writer.writeToInternal(allChecklistsJSONString);
				
				JSONReader reader = new JSONReader(context);
				reader.readFromInternal(JSONWriter.FILENAME);
				checklistsArray = reader.getChecklistsArray();
				
				// Adds JSON string of steps for each checklist into ArrayList
				String[] checklistHolder;
				String allStepsJSONString;
				ArrayList<String> allStepsJSONStringArray = new ArrayList<String>();
				for (int i = 0; i < checklistsArray.size(); i++) { 
					checklistHolder = checklistsArray.get(i);
					allStepsJSONString = getRequest.getSteps(Integer.parseInt(checklistHolder[0]));
					allStepsJSONStringArray.add(allStepsJSONString);
				}
				
				// Adds all steps for all checklists into an ArrayList
				ArrayList<String[]> stepsArray;
				allStepsArray = new ArrayList<String[]>();
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
			Intent intent = new Intent(context, MainMenuActivity.class);
			intent.putExtra("checklists", checklistsArray);
			intent.putExtra("steps", allStepsArray);
			
			// Start main menu activity
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
	}

}
