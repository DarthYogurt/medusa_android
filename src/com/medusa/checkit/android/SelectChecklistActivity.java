package com.medusa.checkit.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectChecklistActivity extends Activity {
	
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	private static final String KEY_ALL_CHECKLISTS = "allChecklists";
	private static final String KEY_CHECKLIST = "checklist";
	private static final String KEY_CHECKLIST_STEPS = "checklistSteps";
	private static final String KEY_CURRENT_STEP = "currentStep";
	private static final int GROUP_ID = 1;
	private static final int HTTP_RESPONSE_SUCCESS = 200;
	
	ListView listView;
	JSONReader reader;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_checklist);
		getActionBar().setTitle("");
		
		checklistsArray = getIntent().getParcelableArrayListExtra(KEY_ALL_CHECKLISTS);
		
		reader = new JSONReader(this);
        
        listView = (ListView)findViewById(R.id.checklist_listview);
        refreshList();
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SelectChecklistActivity.this, StepActivity.class);
				Checklist checklist = checklistsArray.get(position);
				createStepsArray(getStepsFilename(checklist.getId()));
				
				if (!stepsArray.isEmpty()) {
					checklist.setTimeStarted(Utilities.getTimeStamp());
					intent.putExtra(KEY_CHECKLIST, checklist);
					intent.putExtra(KEY_CHECKLIST_STEPS, stepsArray);
					intent.putExtra(KEY_CURRENT_STEP, 0);
					startActivity(intent);
				}
				else {
					Toast.makeText(SelectChecklistActivity.this, R.string.msg_no_steps, Toast.LENGTH_SHORT).show();
				}
			}
        });
	}
	
	private void refreshList() {
		ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
	}
	
	private void createChecklistArray() {
		try { 
			String jsonString = reader.readFromInternal(FILENAME_CHECKLISTS);
			checklistsArray = reader.getChecklistsArray(jsonString);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void createStepsArray(String filename) {
		try {
			String jsonString = reader.readFromInternal(filename);
			stepsArray = reader.getStepsArray(jsonString);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private String getStepsFilename(int checklistId) {
		return "cid" + Integer.toString(checklistId) + "_steps.json";
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
				new UpdateFilesThread().execute();
			}
			else {
				Toast.makeText(this, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_notifications:
			Intent intent = new Intent(this, NotificationsActivity.class);
	    	startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void checkForNonUploadedChecklists() {
		String[] savedFiles = this.fileList();
		
		for (int i = 0; i < savedFiles.length; i++) {
			String filename = savedFiles[i];
			
			if (filename.contains("finished")) {
				ArrayList<String> imgFilenames = new ArrayList<String>();
				
				try { 
					JSONReader reader = new JSONReader(this);
					String jsonString = reader.readFromInternal(filename);
					imgFilenames = reader.getImageFilenamesArray(jsonString);
				} 
				catch (IOException e) { e.printStackTrace(); }
				
				new PostToServerThread(filename, imgFilenames).execute();
			}
		}
	}
	
	private class UpdateFilesThread extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(SelectChecklistActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.msg_updating_files));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	// Updates local JSON file containing checklists
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	String checklistsJsonString = getRequest.getChecklists(GROUP_ID);
			
			JSONWriter writer = new JSONWriter(SelectChecklistActivity.this);
			try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
			createChecklistArray();
			
			// Updates JSON file of steps for each checklist into individual files 
			for (int i = 0; i < checklistsArray.size(); i++) { 
				Checklist checklistHolder = checklistsArray.get(i);
				String stepsJsonString = getRequest.getSteps(checklistHolder.getId());
				
				String filename = "cid" + Integer.toString(checklistHolder.getId()) + "_steps.json";
				try { writer.writeToInternal(filename, stepsJsonString); }
				catch (IOException e) { e.printStackTrace(); }
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	refreshList();
	    	checkForNonUploadedChecklists();
	        return;
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
			progressDialog = new ProgressDialog(SelectChecklistActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.msg_uploading_files));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
			if (Utilities.isNetworkAvailable(SelectChecklistActivity.this)) {
				final HTTPPostRequest post = new HTTPPostRequest(SelectChecklistActivity.this);
				post.createNewPost(); 
				post.addJSON(filename);
				if (!imgFilenames.isEmpty()) { post.addPictures(imgFilenames); }
				final int responseCode = post.sendPost();
				
				// Show message from upload
				runOnUiThread(new Runnable() {
					public void run() { showUploadMessage(responseCode); }
				});
				
				// Delete files if successfully uploaded
				if (responseCode == HTTP_RESPONSE_SUCCESS) {
					// Deletes checklist file after uploaded
					Utilities.deleteFileFromInternal(SelectChecklistActivity.this, filename);
					
					// Deletes images after uploaded
					if (!imgFilenames.isEmpty()) {
						for (int i = 0; i < imgFilenames.size(); i++) {
							String imgFilename = imgFilenames.get(i);
							Utilities.deleteFileFromExternal(SelectChecklistActivity.this, imgFilename);
						}
					}
				}
			}
			else {
				Toast.makeText(SelectChecklistActivity.this, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	        return;
	    }
	}
	
	private void showUploadMessage(int responseCode) {
		if (responseCode == HTTP_RESPONSE_SUCCESS) {
			Toast.makeText(this, R.string.msg_checklist_upload_success, Toast.LENGTH_SHORT).show();
		}
	}
	
}
