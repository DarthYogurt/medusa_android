package com.medusa.checkit.android;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HTTPGetRequest {
	
	private static final String BASE_URL = "http://dev.darthyogurt.com:8000/";
	private static final String CHECKLISTS_URL = "checklist/groupid/";
	private static final String STEPS_URL = "checklist/checklistid/";
	private static final String NOTIFICATIONS_URL = "getSlate/";
	private static final String FINISH_NOTIFICATION_URL = "checkOffSlate/";
	
	public String getString(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		String string = "";
		
		try { 
			HttpResponse response = client.execute(get);
			string = EntityUtils.toString(response.getEntity());
			
			int statusCode = response.getStatusLine().getStatusCode();
			Log.i("HTTP GET STATUS CODE", Integer.toString(statusCode));
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return string;
	}
	
	public String getChecklists(int groupId) {
		String url = BASE_URL + CHECKLISTS_URL + Integer.toString(groupId);
		String jsonString = "";
		jsonString = getString(url);
		return jsonString;
	}
	
	public String getSteps(int checklistId) {
		String url = BASE_URL + STEPS_URL + Integer.toString(checklistId);
		String jsonString = "";
		jsonString = getString(url);
		return jsonString;
	}
	
	public String getNotifications() {
		String url = BASE_URL + NOTIFICATIONS_URL;
		String jsonString = "";
		jsonString = getString(url);
		return jsonString;
	}
	
	public void finishNotification(int id) {
		String url = BASE_URL + FINISH_NOTIFICATION_URL + Integer.toString(id) + "/";
		getString(url);
	}
}
