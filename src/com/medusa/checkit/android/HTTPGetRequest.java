package com.medusa.checkit.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class HTTPGetRequest {
	
	private static final String BASE_URL = "http://dev.darthyogurt.com:8000/";
	private static final String CHECKLISTS_URL = "checklist/groupid/";
	private static final String STEPS_URL = "checklist/checklistid/";
	private static final String NOTIFICATIONS_URL = "getSlate/";
	
	public String getJSONString(String url) throws MalformedURLException, IOException {
		String JSONString = "";
		String charset = "UTF-8";
		URLConnection connection = new URL(url).openConnection();
		InputStream response = connection.getInputStream();
		String contentType = connection.getHeaderField("Content-Type");
		for (String param : contentType.replace(" ", "").split(";")) {
			if (param.startsWith("charset=")) {
				charset = param.split("=", 2)[1];
				break;
			}
		}
		
		if (charset != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
			try {
				for (String line; (line = reader.readLine()) != null;) {
					JSONString = line;
				}
			}
			finally {
				try { reader.close(); } catch (IOException logOrIgnore) {}
			}
		}
		return JSONString;
	}
	
	public String getChecklists(int groupId) {
		String listOfChecklistsURL = BASE_URL + CHECKLISTS_URL + Integer.toString(groupId);
		String jsonString = "";
		try { jsonString = getJSONString(listOfChecklistsURL); } 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
	
	public String getSteps(int checklistId) {
		String checklistStepsURL = BASE_URL + STEPS_URL + Integer.toString(checklistId);
		String jsonString = "";
		try { jsonString = getJSONString(checklistStepsURL); } 
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
	
	public String getNotifications() {
		String notificationsURL = BASE_URL + NOTIFICATIONS_URL;
		String jsonString = "";
		try { jsonString = getJSONString(notificationsURL); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
}
