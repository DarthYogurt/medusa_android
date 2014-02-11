package com.medusa.checkit.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPGetRequest {
	
	private static final String BASE_URL = "http://dev.darthyogurt.com:8000/";
	private static final String CHECKLISTS_URL = "checklist/groupid/";
	private static final String STEPS_URL = "checklist/checklistid/";
	private static final String NOTIFICATIONS_URL = "getSlate/";
	private static final String FINISH_NOTIFICATION_URL = "checkOffSlate/";
	
	public String getJSONString(String url) throws MalformedURLException, IOException {
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
		
		String JSONString = "";
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
		String url = BASE_URL + CHECKLISTS_URL + Integer.toString(groupId);
		String jsonString = "";
		try { jsonString = getJSONString(url); } 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
	
	public String getSteps(int checklistId) {
		String url = BASE_URL + STEPS_URL + Integer.toString(checklistId);
		String jsonString = "";
		try { jsonString = getJSONString(url); } 
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
	
	public String getNotifications() {
		String url = BASE_URL + NOTIFICATIONS_URL;
		String jsonString = "";
		try { jsonString = getJSONString(url); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return jsonString;
	}
	
	public void finishNotification(int id) {
		String url = BASE_URL + FINISH_NOTIFICATION_URL + Integer.toString(id) + "/";
		try { getJSONString(url); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
}
