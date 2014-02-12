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

public class NotificationsActivity extends Activity {
	
	private static final String FILENAME_NOTIFICATIONS = "notifications.json";
	
	private Preferences preferences;
	private ListView listView;
	private ArrayList<Notification> notificationsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		getActionBar().setTitle("");
		
		listView = (ListView)findViewById(R.id.notifications_listview);
		
		preferences = new Preferences(this);
		notificationsArray = new ArrayList<Notification>();
		
		refreshList();

        if (GlobalMethods.isNetworkAvailable(this)) { 
        	if (!preferences.getUnsentNotifications().isEmpty()) { new SendUnsentTask().execute(); }
        	else { new UpdateNotificationsTask().execute(); }
    	}
    	else {
    		Toast.makeText(this, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
			
			if (GlobalMethods.checkIfFileExists(this, FILENAME_NOTIFICATIONS)) {
				createNotificationsArray();
				refreshList();
			}
			else {
				Toast.makeText(this, R.string.msg_no_local_files, Toast.LENGTH_LONG).show();
			}
    	}
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (GlobalMethods.isNetworkAvailable(NotificationsActivity.this)) { 
					Notification notification = notificationsArray.get(position);
					new FinishNotificationTask(notification.getSlateId()).execute();
				}
		    	else {
		    		Notification notification = notificationsArray.get(position);
		    		String idAsString = Integer.toString(notification.getSlateId());
		    		
		    		ArrayList<String> unsentArray = new ArrayList<String>(preferences.getUnsentNotifications());
		    		
		    		if (!unsentArray.contains(idAsString)) {
		    			unsentArray.add(idAsString);
			    		preferences.setUnsentNotifications(unsentArray);
			    		Toast.makeText(NotificationsActivity.this, R.string.msg_unsent_notification, Toast.LENGTH_LONG).show();
		    		}
		    		else {
		    			Toast.makeText(NotificationsActivity.this, R.string.msg_already_logged, Toast.LENGTH_SHORT).show();
		    		}
		    	}
			}
        });
	}
	
	private void refreshList() {
		NotificationsAdapter adapter = new NotificationsAdapter(this, R.layout.listview_notification_row, notificationsArray);
        listView.setAdapter(adapter);
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
			if (GlobalMethods.isNetworkAvailable(this)) {
				if (!preferences.getUnsentNotifications().isEmpty()) { new SendUnsentTask().execute(); }
	        	else { new UpdateNotificationsTask().execute(); }
			}
			else { Toast.makeText(this, R.string.msg_network_error, Toast.LENGTH_SHORT).show(); }
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void createNotificationsArray() {
		try { 
			JSONReader reader = new JSONReader(this);
			String jsonString = reader.readFromInternal(FILENAME_NOTIFICATIONS);
			notificationsArray = reader.getNotificationsArray(jsonString);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private class UpdateNotificationsTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(NotificationsActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.msg_updating_notifications));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	// Updates local JSON file containing notifications
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	String notificationsJsonString = getRequest.getNotifications();
			
	    	JSONWriter writer = new JSONWriter(NotificationsActivity.this);
			try { writer.writeToInternal(FILENAME_NOTIFICATIONS, notificationsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
			
			createNotificationsArray();
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	progressDialog.dismiss();
			refreshList();
	        return;
	    }
	}
	
	private class FinishNotificationTask extends AsyncTask<Void, Void, Void> {

		int id;
		
		private FinishNotificationTask(int id) {
			this.id = id;
		}
		
	    protected Void doInBackground(Void... params) {
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
			getRequest.finishNotification(id);
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	new UpdateNotificationsTask().execute();
	        return;
	    }
	}
	
	private class SendUnsentTask extends AsyncTask<Void, Void, Void> {
		ArrayList<String> unsentArray = new ArrayList<String>();
		
		@Override
		protected void onPreExecute(){
			unsentArray = new ArrayList<String>(preferences.getUnsentNotifications());
		}
		
	    protected Void doInBackground(Void... params) {
	    	HTTPGetRequest getRequest = new HTTPGetRequest();
	    	
	    	for (int i = 0; i < unsentArray.size(); i++) {
	    		int id = Integer.parseInt(unsentArray.get(i));
	    		getRequest.finishNotification(id);
	    		unsentArray.remove(i);
	    	}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	Toast.makeText(NotificationsActivity.this, R.string.msg_unsent_notification_sent, Toast.LENGTH_SHORT).show();
	    	preferences.setUnsentNotifications(unsentArray);
	    	new UpdateNotificationsTask().execute();
	        return;
	    }
	}
	
}
