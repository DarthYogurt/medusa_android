package com.medusa.checkit.android;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JSONReader {

	Context context;
	
	public JSONReader(Context context) {
		this.context = context;
	}
	
	public String readFromInternal(String filename) throws IOException {
		BufferedReader br = null;
		String jsonString = "";
		
		try {
			FileInputStream fis = context.openFileInput(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			jsonString = br.readLine();
			
			Log.i("JSON STRING", jsonString);
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); } 
		finally {
			try { if (br != null) { br.close(); } } 
			catch (IOException e) { e.printStackTrace(); }
		}
		return jsonString;
	}
	
	public ArrayList<Checklist> getChecklistsArray(String jsonString) {
		ArrayList<Checklist> checklistsArray = new ArrayList<Checklist>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("checklist");

            for (int i = 0; i < jArray.length(); i++) {
            	int groupId = Integer.parseInt(jObject.getString("groupId"));
            	int checklistId = Integer.parseInt(jArray.getJSONObject(i).getString("id"));
            	String checklistName = jArray.getJSONObject(i).getString("name");
            	int numOfSteps = Integer.parseInt(jArray.getJSONObject(i).getString("numOfSteps"));
                
                Checklist checklist = new Checklist(groupId, checklistId, checklistName, numOfSteps);
                checklistsArray.add(checklist);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return checklistsArray;
	}
	
	public ArrayList<Step> getStepsArray(String jsonString) {
		ArrayList<Step> stepsArray = new ArrayList<Step>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArraySteps = jObject.getJSONArray("steps");
            JSONArray jArrayUsers = jObject.getJSONArray("users");
            
            int checklistId = Integer.parseInt(jObject.getString("checklistId"));
            String checklistName = jObject.getString("checklistName");

            ArrayList<User> usersArray = new ArrayList<User>();
            for (int i = 0; i < jArrayUsers.length(); i++) {
            	int userId = Integer.parseInt(jArrayUsers.getJSONObject(i).getString("userId"));
            	String userName = jArrayUsers.getJSONObject(i).getString("name");
            	
            	User user = new User(userId, userName);
            	usersArray.add(user);
            }

            for (int i = 0; i < jArraySteps.length(); i++) {
            	int order = Integer.parseInt(jArraySteps.getJSONObject(i).getString("order"));
            	String name = jArraySteps.getJSONObject(i).getString("name");
            	String type = jArraySteps.getJSONObject(i).getString("type");
            	int id = Integer.parseInt(jArraySteps.getJSONObject(i).getString("id"));
            	boolean reqNote = jArraySteps.getJSONObject(i).getBoolean("requireText");
            	boolean reqImage = jArraySteps.getJSONObject(i).getBoolean("requireImage");
            	
            	Step step = new Step(order, name, type, id, checklistId, checklistName, usersArray, reqNote, reqImage);
            	
                if (jArraySteps.getJSONObject(i).has("ifValueTrue")) {
                	if (jArraySteps.getJSONObject(i).getBoolean("ifValueTrue")) {
                		step.setIfBoolValueIs(true);
                	}
                }
                if (jArraySteps.getJSONObject(i).has("ifValueFalse")) {
                	if (jArraySteps.getJSONObject(i).getBoolean("ifValueFalse")) {
                		step.setIfBoolValueIs(false);
                	}
                }
                if (jArraySteps.getJSONObject(i).has("ifLessThan")) {
                	step.setIfLessThan(jArraySteps.getJSONObject(i).getDouble("ifLessThan"));
                }
                if (jArraySteps.getJSONObject(i).has("ifEqualTo")) {
                	step.setIfEqualTo(jArraySteps.getJSONObject(i).getDouble("ifEqualTo"));
                }
                if (jArraySteps.getJSONObject(i).has("ifGreaterThan")) {
                	step.setIfGreaterThan(jArraySteps.getJSONObject(i).getDouble("ifGreaterThan"));
                }
                
                stepsArray.add(step);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return stepsArray;
	}
	
	public ArrayList<String> getImageFilenamesArray(String jsonString) {
		ArrayList<String> array = new ArrayList<String>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArraySteps = jObject.getJSONArray("steps");

            for (int i = 0; i < jArraySteps.length(); i++) {
            	String type = jArraySteps.getJSONObject(i).getString("stepType");
            	
            	if (type.equalsIgnoreCase("image")) {
            		array.add(jArraySteps.getJSONObject(i).getString("value"));
            	}
            	
                if (jArraySteps.getJSONObject(i).has("extraImage")) {
                	array.add(jArraySteps.getJSONObject(i).getString("extraImage"));
                }
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return array;
	}
	
	public ArrayList<Notification> getNotificationsArray(String jsonString) {
		ArrayList<Notification> notificationsArray = new ArrayList<Notification>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("slate");

            for (int i = 0; i < jArray.length(); i++) {
            	int slateId = Integer.parseInt(jArray.getJSONObject(i).getString("slateId"));
            	String userName = jArray.getJSONObject(i).getString("userName");
            	String checklist = jArray.getJSONObject(i).getString("checklist");
            	String stepName = jArray.getJSONObject(i).getString("stepName");
            	String notifyName = jArray.getJSONObject(i).getString("notifyName");
                String note = jArray.getJSONObject(i).getString("addNote");
                String imgFilename = jArray.getJSONObject(i).getString("addImage");
                if (!imgFilename.contains(".jpg")) { imgFilename = ""; }
                else { imgFilename = "http://" + imgFilename; }
                
                Notification notification = new Notification(slateId, userName, checklist, stepName, notifyName, note, imgFilename);
            	
                notificationsArray.add(notification);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return notificationsArray;
	}

}
