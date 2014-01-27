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
	private static final String KEY_NOTIFY_USER_ID = "notifyUserId";
	private static final String KEY_TIME_STARTED = "timeStarted";
	private static final String KEY_TIME_FINISHED = "timeFinished";
	private static final String KEY_STEPS = "steps";
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	
	static final String FILENAME = "temp.json";
	static final String CHECKLIST_FILENAME = "new_checklist.json";
	
	Context context;
	FileOutputStream fos;
	JsonWriter writer;
	
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
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			Log.v("new file", CHECKLIST_FILENAME + " has been created");
		} catch (IOException e) { e.printStackTrace(); }
		
		try {
			writer.beginObject();
			writer.name(KEY_USER_ID).value(1);
			writer.name(KEY_GROUP_ID).value(1);
			writer.name(KEY_CHECKLIST_ID).value(checklistId);
			writer.name(KEY_STEPS);
			writer.beginArray();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void finishNewChecklist() throws IOException {
		try {
			writer.endArray();
			writer.endObject();
			writer.close();
			fos.close();
	    } catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepBoolean(Step step) throws IOException {
		try {
			writer.beginObject();
			writer.name(KEY_STEP_ID).value(step.getId());
			writer.name(KEY_STEP_TYPE).value(TYPE_BOOL);
			writer.name(KEY_VALUE).value(step.getYesOrNo());
			if (checkToNotifyBool(step)) { writer.name(KEY_NOTIFY_USER_ID).value(step.getNotifyUserId()); }
			writer.name(KEY_TIME_STARTED).value(step.getTimeStarted());
			writer.name(KEY_TIME_FINISHED).value(step.getTimeFinished());
			writer.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepNumber(Step step) throws IOException {
		try {
			writer.beginObject();
			writer.name(KEY_STEP_ID).value(step.getId());
			writer.name(KEY_STEP_TYPE).value(TYPE_NUMBER);
			writer.name(KEY_VALUE).value(step.getNumber());
			if (checkToNotifyNumber(step)) { writer.name(KEY_NOTIFY_USER_ID).value(step.getNotifyUserId()); }
			writer.name(KEY_TIME_STARTED).value(step.getTimeStarted());
			writer.name(KEY_TIME_FINISHED).value(step.getTimeFinished());
			writer.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepText(Step step) throws IOException {
		try {
			writer.beginObject();
			writer.name(KEY_STEP_ID).value(step.getId());
			writer.name(KEY_STEP_TYPE).value(TYPE_TEXT);
			writer.name(KEY_VALUE).value(step.getText());
			writer.name(KEY_TIME_STARTED).value(step.getTimeStarted());
			writer.name(KEY_TIME_FINISHED).value(step.getTimeFinished());
			writer.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void writeStepImage(Step step) throws IOException {
		try {
			writer.beginObject();
			writer.name(KEY_STEP_ID).value(step.getId());
			writer.name(KEY_STEP_TYPE).value(TYPE_IMAGE);
			writer.name(KEY_VALUE).value(step.getImageFilename());
			writer.name(KEY_TIME_STARTED).value(step.getTimeStarted());
			writer.name(KEY_TIME_FINISHED).value(step.getTimeFinished());
			writer.endObject();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	private boolean checkToNotifyBool(Step step) {
		if (step.getIfValueTrue() != null && step.getIfValueTrue()) {
			if (step.getYesOrNo() == true) { return true; }
		}
		if (step.getIfValueFalse() != null && step.getIfValueFalse()) {
			if (step.getYesOrNo() == false ) { return true; }
		}
		return false;
	}
	
	private boolean checkToNotifyNumber(Step step) {
		if (step.getIfLessThan() != null && step.getNumber() < Double.valueOf(step.getIfLessThan())) {
			return true;
		}
		if (step.getIfEqualTo() != null && step.getNumber() == Double.valueOf(step.getIfEqualTo())) {
			return true;
		}
		if (step.getIfGreaterThan() != null && step.getNumber() > Double.valueOf(step.getIfGreaterThan())) {
			return true;
		}
		return false;
	}
	
}
