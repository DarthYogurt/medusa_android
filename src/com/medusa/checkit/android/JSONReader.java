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
	String jsonString;
	
	public JSONReader(Context context) {
		this.context = context;
	}
	
	public void readFromInternal(String filename) throws IOException {
		BufferedReader br = null;
		
		try {
			FileInputStream fis = context.openFileInput(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			jsonString = br.readLine();
			
			Log.v("readFromJSON", jsonString);
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); } 
		finally {
			try { if (br != null) { br.close(); } } 
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public ArrayList<Checklist> getChecklistsArray() {
		ArrayList<Checklist> checklistsArray = new ArrayList<Checklist>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("checklist");
            int checklistId;
            String checklistName;
            int groupId;
            
            for (int i = 0; i < jArray.length(); i++) {
            	checklistId = Integer.parseInt(jArray.getJSONObject(i).getString("id"));
                checklistName = jArray.getJSONObject(i).getString("name");
                groupId = Integer.parseInt(jObject.getString("groupId"));
                Checklist checklist = new Checklist(checklistId, checklistName, groupId);
                checklistsArray.add(checklist);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return checklistsArray;
	}
	
	public ArrayList<Step> getStepsArray() {
		ArrayList<Step> stepsArray = new ArrayList<Step>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArraySteps = jObject.getJSONArray("steps");
            
            int order;
            String name;
            String type;
            int id;
            int notifyUserId;
            boolean reqText;
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
                reqText = jArraySteps.getJSONObject(i).getBoolean("requireText");
            	reqImage = jArraySteps.getJSONObject(i).getBoolean("requireImage");
            	
            	Step step = new Step(order, name, type, id, notifyUserId, checklistId, checklistName, reqText, reqImage);
            	
                if (jArraySteps.getJSONObject(i).has("ifValueTrue")) {
                	step.setIfValueTrue(jArraySteps.getJSONObject(i).getBoolean("ifValueTrue"));
                }
                if (jArraySteps.getJSONObject(i).has("ifValueFalse")) {
                	step.setIfValueFalse(jArraySteps.getJSONObject(i).getBoolean("ifValueFalse"));
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

}
