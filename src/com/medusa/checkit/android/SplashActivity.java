package com.medusa.checkit.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_ALL_STEPS = "allSteps";

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
		
		ArrayList<Checklist> checklistsArray;
		ArrayList<Step> allStepsArray;
		
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
				
				// Adds JSON string of steps for each checklist into ArrayList<Checklist>
				Checklist checklistHolder;
				String stepsJSONString;
				ArrayList<String> allStepsJSONStringArray = new ArrayList<String>();
				for (int i = 0; i < checklistsArray.size(); i++) { 
					checklistHolder = checklistsArray.get(i);
					stepsJSONString = getRequest.getSteps(checklistHolder.getId());
					allStepsJSONStringArray.add(stepsJSONString);
				}
				
				// Adds all steps for all checklists into an ArrayList<Step>
				Step stepHolder;
				ArrayList<Step> stepsArray;
				allStepsArray = new ArrayList<Step>();
				for (int i = 0; i < allStepsJSONStringArray.size(); i++) {
					writer.writeToInternal(allStepsJSONStringArray.get(i));
					reader.readFromInternal(JSONWriter.FILENAME); 
					stepsArray = reader.getStepsArray();
					
					checklistHolder = checklistsArray.get(i);
					checklistHolder.setNumOfSteps(stepsArray.size());
					
					for (int s = 0; s < stepsArray.size(); s++) {
						stepHolder = stepsArray.get(s);
						allStepsArray.add(stepHolder);
					}
				}
				
				for (int i = 0; i < allStepsArray.size(); i++) {
					Step holder = allStepsArray.get(i);
					Log.v("ALL Steps", holder.getName());
				}
			} catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }

			Intent intent = new Intent(context, SelectChecklistActivity.class);
			intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
			intent.putExtra(KEY_ALL_STEPS, allStepsArray);
			
			startActivity(intent);
			finish();
		}
	}

}
