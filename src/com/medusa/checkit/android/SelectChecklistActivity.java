package com.medusa.checkit.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectChecklistActivity extends Activity {
	
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_CHECKLIST = "checklist";
	private static final String KEY_CHECKLIST_STEPS = "checklistSteps";
	private static final String KEY_CURRENT_STEP = "currentStep";
	private static final int GROUP_ID = 1;
	
	Context context;
	JSONReader reader;
	UpdateFiles updateFiles;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		getActionBar().setTitle("");
		
		context = getApplicationContext();
		reader = new JSONReader(context);
		
		checklistsArray = getIntent().getParcelableArrayListExtra(KEY_ALL_CHECKLISTS);
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, StepActivity.class);
				Checklist checklist = checklistsArray.get(position);
				createStepsArray(getStepsFilename(checklist.getId()));
				
				if (!stepsArray.isEmpty()) {
					checklist.setTimeStarted(getTimeStartedForChecklist());
					intent.putExtra(KEY_CHECKLIST, checklist);
					intent.putExtra(KEY_CHECKLIST_STEPS, stepsArray);
					intent.putExtra(KEY_CURRENT_STEP, 0);
					startActivity(intent);
				}
				else {
					Toast message = Toast.makeText(context, "No steps in checklist", Toast.LENGTH_SHORT);
					message.show();
				}
			}
        });
	}
	
	private class UpdateFiles extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	JSONWriter writer = new JSONWriter(context);
	    	
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
	    	Intent intent = new Intent(context, SelectChecklistActivity.class);
			intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
	    	startActivity(intent);
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
	
	private void createStepsArray(String filename) {
		try {
			reader.readFromInternal(filename);
			stepsArray = reader.getStepsArray();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private String getStepsFilename(int checklistId) {
		return "cid" + Integer.toString(checklistId) + "_steps.json";
	}
	
	private String getTimeStartedForChecklist() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_checklist, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_update:
			if (isNetworkAvailable()) {
				Toast updatingFiles = Toast.makeText(context, "Updating files", Toast.LENGTH_SHORT);
				updatingFiles.show();
				updateFiles = new UpdateFiles();
				updateFiles.execute();
			}
			else {
				Toast noNetwork = Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT);
				noNetwork.show();
			}
			return true;
		case R.id.action_notifications:
			Intent intent = new Intent(context, NotificationsActivity.class);
	    	startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
