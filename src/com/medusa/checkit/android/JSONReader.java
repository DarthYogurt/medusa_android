package com.medusa.checkit.android;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	public ArrayList<String[]> getChecklistsArray() {
		ArrayList<String[]> checklistsArray = new ArrayList<String[]>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("checklist");
            String checklistId = null;
            String checklistName = null;
            
            for (int i = 0; i < jArray.length(); i++) {
            	checklistId = jArray.getJSONObject(i).getString("id");
                checklistName = jArray.getJSONObject(i).getString("name");
                checklistsArray.add(new String[] {checklistId, checklistName});
            }
            
            // Shows contents of checklistsArray
            for (int i = 0; i < checklistsArray.size(); i++) {
    			Log.v("Checklists Array", Arrays.toString(checklistsArray.get(i)));
    		}
            
        } catch (Exception e) { e.printStackTrace(); }
		return checklistsArray;
	}
	
	public ArrayList<String[]> getStepsArray() {
		ArrayList<String[]> stepsArray = new ArrayList<String[]>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("steps");
            String stepOrder = null;
            String stepName = null;
            String stepType = null;
            String stepId = null;
            String checklistId = null;
            String checklistName = null;

            for (int i = 0; i < jArray.length(); i++) {
                stepOrder = jArray.getJSONObject(i).getString("order");
                stepName = jArray.getJSONObject(i).getString("name");
                stepType = jArray.getJSONObject(i).getString("type");
                stepId = jArray.getJSONObject(i).getString("id");
                checklistId = jObject.getString("checklistId");
                checklistName = jObject.getString("checklistName");
                stepsArray.add(new String[] {stepOrder, stepName, stepType, stepId, checklistName, checklistId});
            }
            
            // Show contents of stepsArray
            for (int i = 0; i < stepsArray.size(); i++) {
    			Log.v("Steps Array", Arrays.toString(stepsArray.get(i)));
    		}
            
        } catch (Exception e) { e.printStackTrace(); }
		return stepsArray;
	}
	
}
