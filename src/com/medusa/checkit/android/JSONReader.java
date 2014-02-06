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
            int checklistId;
            String checklistName;
            int numOfSteps;
            int groupId;
            
            for (int i = 0; i < jArray.length(); i++) {
            	checklistId = Integer.parseInt(jArray.getJSONObject(i).getString("id"));
                checklistName = jArray.getJSONObject(i).getString("name");
                numOfSteps = Integer.parseInt(jArray.getJSONObject(i).getString("numOfSteps"));
                groupId = Integer.parseInt(jObject.getString("groupId"));
                Checklist checklist = new Checklist(checklistId, checklistName, numOfSteps, groupId);
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
            
            int order;
            String name;
            String type;
            int id;
            int notifyUserId;
            boolean reqNote;
            boolean reqImage;
            int checklistId;
            String checklistName;

            for (int i = 0; i < jArraySteps.length(); i++) {
            	order = Integer.parseInt(jArraySteps.getJSONObject(i).getString("order"));
            	name = jArraySteps.getJSONObject(i).getString("name");
            	type = jArraySteps.getJSONObject(i).getString("type");
            	id = Integer.parseInt(jArraySteps.getJSONObject(i).getString("id"));
            	notifyUserId = Integer.parseInt(jArraySteps.getJSONObject(i).getString("notifyUserId"));
                checklistId = Integer.parseInt(jObject.getString("checklistId"));
                checklistName = jObject.getString("checklistName");
                reqNote = jArraySteps.getJSONObject(i).getBoolean("requireText");
            	reqImage = jArraySteps.getJSONObject(i).getBoolean("requireImage");
            	
            	Step step = new Step(order, name, type, id, notifyUserId, checklistId, checklistName, reqNote, reqImage);
            	
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
            
            int slateId;
        	String userName;
        	String checklist;
        	String stepName;
        	String notifyName;
        	String note;
        	String imgFilename;

            for (int i = 0; i < jArray.length(); i++) {
            	slateId = Integer.parseInt(jArray.getJSONObject(i).getString("slateId"));
            	userName = jArray.getJSONObject(i).getString("userName");
            	checklist = jArray.getJSONObject(i).getString("checklist");
            	stepName = jArray.getJSONObject(i).getString("stepName");
            	notifyName = jArray.getJSONObject(i).getString("notifyName");
                note = jArray.getJSONObject(i).getString("addNote");
                imgFilename = jArray.getJSONObject(i).getString("addImage");
                
                Notification notification = new Notification(slateId, userName, checklist, stepName, notifyName, note, imgFilename);
            	
                notificationsArray.add(notification);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return notificationsArray;
	}

}
