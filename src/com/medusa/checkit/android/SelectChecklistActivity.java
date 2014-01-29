package com.medusa.checkit.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectChecklistActivity extends Activity {
	
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_CHECKLIST = "checklist";
	private static final String KEY_CHECKLIST_STEPS = "checklistSteps";
	private static final String KEY_CURRENT_STEP = "currentStep";
	
	JSONReader reader;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		getActionBar().setTitle("");
		
		reader = new JSONReader(getApplicationContext());
		
		checklistsArray = getIntent().getParcelableArrayListExtra(KEY_ALL_CHECKLISTS);
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				Checklist checklist = checklistsArray.get(position);
				createStepsArray(getStepsFilename(checklist.getId()));
				checklist.setTimeStarted(getTimeStartedForChecklist());
				intent.putExtra(KEY_CHECKLIST, checklist);
				intent.putExtra(KEY_CHECKLIST_STEPS, stepsArray);
				intent.putExtra(KEY_CURRENT_STEP, 0);
				startActivity(intent);
				finish();
			}
        });
	}
	
	private void setNumOfStepsView() {
		
	}
	
	private String getStepsFilename(int checklistId) {
		return "cid" + Integer.toString(checklistId) + "_steps.json";
	}
	
	private void createStepsArray(String filename) {
		try {
			reader.readFromInternal(filename);
			stepsArray = reader.getStepsArray();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private String getTimeStartedForChecklist() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
	}
	
//	public class GetJsonFromServer extends AsyncTask<Void, Void, Void> {
//
//	    protected Void doInBackground(Void... params) {
//
////			// Adds all steps for all checklists into an ArrayList<Step>
////			Step stepHolder;
////			ArrayList<Step> stepsArray;
////			allStepsArray = new ArrayList<Step>();
////			for (int i = 0; i < allStepsJSONStringArray.size(); i++) {
////				writer.writeToInternal("steps.json", allStepsJSONStringArray.get(i));
////				reader.readFromInternal("steps.json"); 
////				stepsArray = reader.getStepsArray();
////				
////				checklistHolder = checklistsArray.get(i);
////				checklistHolder.setNumOfSteps(stepsArray.size());
////				
////				for (int s = 0; s < stepsArray.size(); s++) {
////					stepHolder = stepsArray.get(s);
////					allStepsArray.add(stepHolder);
////				}
////			}
////			
////			for (int i = 0; i < allStepsArray.size(); i++) {
////				Step holder = allStepsArray.get(i);
////				Log.v("ALL Steps", holder.getName());
////			}
//			
//	        return null;
//	    }
//
//	    protected void onPostExecute(Void result) {
//	    	super.onPostExecute(result);
//	    	startActivity();
//	        return;
//	    }
//	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main_menu, menu);
//		return true;
//	}

//	private class IntentLauncher extends Thread {
//		
//		ArrayList<Checklist> checklistsArray;
//		ArrayList<Step> allStepsArray;
//		
//		@Override
//		public void run() {
//			
//			try {
//				HTTPGetRequest getRequest = new HTTPGetRequest();
////				String allChecklistsJSONString = getRequest.getChecklists(1);
//				
//				JSONWriter writer = new JSONWriter(context);
////				writer.writeToInternal(FILENAME_CHECKLISTS, allChecklistsJSONString);
//				
//				JSONReader reader = new JSONReader(context);
//				reader.readFromInternal(FILENAME_CHECKLISTS);
//				checklistsArray = reader.getChecklistsArray();
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
