package com.medusa.checkit.android;

import android.content.Context;
import android.util.Log;

import java.io.*;

import com.google.gson.stream.JsonWriter;

public class JSONWriter {

	private static final String KEY_USER_ID = "userId";
	private static final String KEY_GROUP_ID = "groupId";
	private static final String KEY_CHECKLIST_ID = "checklistId";
	private static final String KEY_STEP_ID = "stepId";
	private static final String KEY_STEP_TYPE = "stepType";
	private static final String KEY_VALUE = "value";
	private static final String KEY_STEPS = "steps";
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_DOUBLE = "double";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	
	static final String FILENAME = "temp.json";
	static final String CHECKLIST_FILENAME = "new_checklist.json";
	
	Context context;
	FileOutputStream fos;
	JsonWriter checklistWriter;
	
	public JSONWriter(Context context) {
		this.context = context;
	}
	
	public void writeToInternal(String data) throws IOException {
		try {
			fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			Log.v("writeToJSON", FILENAME + " has been written");
		} catch (IOException e) { e.printStackTrace(); } 
		finally {
			try { fos.close(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public void startNewChecklist(int checklistId) throws IOException {		
		try {
			fos = context.openFileOutput(CHECKLIST_FILENAME, Context.MODE_PRIVATE);
			checklistWriter = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			Log.v("new file", CHECKLIST_FILENAME + " has been created");
		} catch (IOException e) { e.printStackTrace(); }
		
		try {
			checklistWriter.beginObject();
			checklistWriter.name(KEY_USER_ID).value(1);
			checklistWriter.name(KEY_GROUP_ID).value(1);
			checklistWriter.name(KEY_CHECKLIST_ID).value(checklistId);
			checklistWriter.name(KEY_STEPS);
			checklistWriter.beginArray();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void finishNewChecklist() throws IOException {
		try {
			checklistWriter.endArray();
			checklistWriter.endObject();
			checklistWriter.close();
			fos.close();
	    } catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepBoolean(int stepId, boolean result) throws IOException {
		try {
			checklistWriter.beginObject();
			checklistWriter.name(KEY_STEP_ID).value(stepId);
			checklistWriter.name(KEY_STEP_TYPE).value(TYPE_BOOL);
			if (result == true) { checklistWriter.name(KEY_VALUE).value("true"); }
			else { checklistWriter.name(KEY_VALUE).value("false"); }
			checklistWriter.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepDouble(int stepId, double result) throws IOException {
		try {
			checklistWriter.beginObject();
			checklistWriter.name(KEY_STEP_ID).value(stepId);
			checklistWriter.name(KEY_STEP_TYPE).value(TYPE_DOUBLE);
			checklistWriter.name(KEY_VALUE).value(result);
			checklistWriter.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepText(int stepId, String result) throws IOException {
		try {
			checklistWriter.beginObject();
			checklistWriter.name(KEY_STEP_ID).value(stepId);
			checklistWriter.name(KEY_STEP_TYPE).value(TYPE_TEXT);
			checklistWriter.name(KEY_VALUE).value(result);
			checklistWriter.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepImage(int stepId, String filename) throws IOException {
		try {
			checklistWriter.beginObject();
			checklistWriter.name(KEY_STEP_ID).value(stepId);
			checklistWriter.name(KEY_STEP_TYPE).value(TYPE_IMAGE);
			checklistWriter.name(KEY_VALUE).value(filename);
			checklistWriter.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
}
