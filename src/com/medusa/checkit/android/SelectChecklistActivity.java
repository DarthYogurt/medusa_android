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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
	
	JSONReader reader;
	UpdateFiles updateFiles;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_checklist);
		getActionBar().setTitle("");
		
		reader = new JSONReader(this);
		
		checklistsArray = getIntent().getParcelableArrayListExtra(KEY_ALL_CHECKLISTS);
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SelectChecklistActivity.this, StepActivity.class);
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
					Toast.makeText(SelectChecklistActivity.this, "No steps in checklist", Toast.LENGTH_SHORT).show();
				}
			}
        });
	}
	
	private class UpdateFiles extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(SelectChecklistActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Updating files. Please wait.");
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	JSONWriter writer = new JSONWriter(SelectChecklistActivity.this);
	    	
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
	    	Intent intent = new Intent(SelectChecklistActivity.this, SelectChecklistActivity.class);
			intent.putExtra(KEY_ALL_CHECKLISTS, checklistsArray);
	    	startActivity(intent);
	    	finish();
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
			if (Utilities.isNetworkAvailable(this)) {
				updateFiles = new UpdateFiles();
				updateFiles.execute();
			}
			else {
				Toast.makeText(this, "No network connectivity", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_notifications:
			Intent intent = new Intent(this, NotificationsActivity.class);
	    	startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
