package com.medusa.checkit.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPGetRequest {
	
	private static final String BASE_URL = "http://dev.darthyogurt.com:8000/";
	private static final String CHECKLISTS_URL = "checklist/groupid/";
	private static final String STEPS_URL = "checklist/checklistid/";
	private static final String NOTIFICATIONS_URL = "getSlate/";
	private static final String FINISH_NOTIFICATION_URL = "checkOffSlate/";
	
	public String getJSONString(String url) {
		URLConnection connection;
		InputStream response = null;
		String contentType = null;
		try {
			connection = new URL(url).openConnection();
			response = connection.getInputStream();
			contentType = connection.getHeaderField("Content-Type");
		} 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }

		String charset = "UTF-8";
		for (String param : contentType.replace(" ", "").split(";")) {
			if (param.startsWith("charset=")) {
				charset = param.split("=", 2)[1];
				break;
			}
		}
		
		String jsonString = "";
		if (charset != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(response, charset));
			} 
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			
			try {
				try {
					for (String line; (line = reader.readLine()) != null;) { jsonString = line; }
				} 
				catch (IOException e) { e.printStackTrace(); }
			}
			finally {
				try { reader.close(); } catch (IOException logOrIgnore) {}
			}
		}
		return jsonString;
	}
	
	public String getChecklists(int groupId) {
		String url = BASE_URL + CHECKLISTS_URL + Integer.toString(groupId);
		String jsonString = "";
		jsonString = getJSONString(url);
		return jsonString;
	}
	
	public String getSteps(int checklistId) {
		String url = BASE_URL + STEPS_URL + Integer.toString(checklistId);
		String jsonString = "";
		jsonString = getJSONString(url);
		return jsonString;
	}
	
	public String getNotifications() {
		String url = BASE_URL + NOTIFICATIONS_URL;
		String jsonString = "";
		jsonString = getJSONString(url);
		return jsonString;
	}
	
	public void finishNotification(int id) {
		String url = BASE_URL + FINISH_NOTIFICATION_URL + Integer.toString(id) + "/";
		getJSONString(url);
	}
}
