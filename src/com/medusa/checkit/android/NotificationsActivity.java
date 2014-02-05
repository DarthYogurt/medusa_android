package com.medusa.checkit.android;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class NotificationsActivity extends Activity {
	
	private static final String FILENAME_NOTIFICATIONS = "notifications.json";
	
	ListView listView;
	NotificationsAdapter adapter;
	private JSONReader reader;
	private ArrayList<Notification> notificationsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		getActionBar().setTitle("");
		
		listView = (ListView)findViewById(R.id.notifications_listview);
		
		reader = new JSONReader(getApplicationContext());
		
		notificationsArray = new ArrayList<Notification>();
		
		adapter = new NotificationsAdapter(getApplicationContext(), R.layout.listview_notification_row, notificationsArray);
        listView.setAdapter(adapter);

        if (Utilities.isNetworkAvailable(getApplicationContext())) { 
        	new UpdateNotifications().execute();
		}
    	else {
    		Toast.makeText(getApplicationContext(), "Network Error: No Connectivity", Toast.LENGTH_SHORT).show();
			
			if (Utilities.checkIfFileExists(getApplicationContext(), FILENAME_NOTIFICATIONS)) {
				createNotificationsArray();
			}
			else {
				Toast.makeText(getApplicationContext(), "No files found locally. Please connect to the internet and try again.", Toast.LENGTH_LONG).show();
			}
    	}
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notifications, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_update:
			if (Utilities.isNetworkAvailable(this)) {
				new UpdateNotifications().execute();
			}
			else {
				Toast.makeText(getApplicationContext(), "Network Error: No Connectivity", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void createNotificationsArray() {
		try { 
			reader.readFromInternal(FILENAME_NOTIFICATIONS);
			notificationsArray = reader.getNotificationsArray();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private class UpdateNotifications extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(NotificationsActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Updating notifications. Please wait.");
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	JSONWriter writer = new JSONWriter(getApplicationContext());
	    	
	    	// Updates local JSON file containing notifications
	    	String notificationsJsonString = "";
	    	notificationsJsonString = getRequest.getNotifications();
			
			try { writer.writeToInternal(FILENAME_NOTIFICATIONS, notificationsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
			createNotificationsArray();
			
			Log.v("SIZE", Integer.toString(notificationsArray.size()));
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
	    	
			adapter = new NotificationsAdapter(NotificationsActivity.this, R.layout.listview_notification_row, notificationsArray);
	        listView.setAdapter(adapter);

	        return;
	    }
	}
}
