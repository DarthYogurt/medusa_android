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
		} catch (FileNotFoundException e) { e.printStackTrace(); } 
		finally {
			try { if (br != null) { br.close(); }
			} catch (IOException e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
		return checklistsArray;
	}
	
	public ArrayList<Step> getStepsArray() {
		ArrayList<Step> stepsArray = new ArrayList<Step>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArraySteps = jObject.getJSONArray("steps");
            JSONArray jArrayReq = jObject.getJSONArray("require");
            
            String name;
            boolean ifValueTrue;
            boolean ifValueFalse;
            double ifLessThan;
            double ifEqualTo;
            double ifGreaterThan;
            boolean reqText;
            boolean reqImage;
            int id;
            int notifyUserId;
            String type;
            int order;
            int checklistId;
            String checklistName;

            for (int i = 0; i < jArraySteps.length(); i++) {
            	name = jArraySteps.getJSONObject(i).getString("name");
            	
            	ifValueTrue = changeStringToBool(jArrayReq.getJSONObject(i).getString("ifValueTrue"));
            	ifValueFalse = changeStringToBool(jArrayReq.getJSONObject(i).getString("ifValueFalse"));
            	ifLessThan = Double.parseDouble(jArrayReq.getJSONObject(i).getString("ifLessThan"));
            	ifEqualTo = Double.parseDouble(jArrayReq.getJSONObject(i).getString("ifEqualTo"));
            	ifGreaterThan = Double.parseDouble(jArrayReq.getJSONObject(i).getString("ifGreaterThan"));
            	reqText = changeStringToBool(jArrayReq.getJSONObject(i).getString("reqText"));
            	reqImage = changeStringToBool(jArrayReq.getJSONObject(i).getString("reqImage"));
            	
            	id = Integer.parseInt(jArraySteps.getJSONObject(i).getString("id"));
            	notifyUserId = Integer.parseInt(jArraySteps.getJSONObject(i).getString("notifyUserId"));
                type = jArraySteps.getJSONObject(i).getString("type");
                order = Integer.parseInt(jArraySteps.getJSONObject(i).getString("order"));
                
                checklistId = Integer.parseInt(jObject.getString("checklistId"));
                checklistName = jObject.getString("checklistName");
                
                Step step = new Step(order, name, type, id, notifyUserId, ifValueTrue, ifValueFalse, 
                					 ifLessThan, ifEqualTo, ifGreaterThan, reqText, reqImage, 
                					 checklistId, checklistName);
                stepsArray.add(step);
            }
        } catch (Exception e) { e.printStackTrace(); }
		return stepsArray;
	}
	
	private boolean changeStringToBool(String s) {
		if (s.equalsIgnoreCase("true")) { return true; }
		else { return false; }
	}

}
