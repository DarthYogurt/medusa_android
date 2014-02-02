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
	private static final String KEY_EXTRA_NOTE = "extraNote";
	private static final String KEY_EXTRA_IMAGE = "extraImage";
	private static final String KEY_TIME_STARTED = "timeStarted";
	private static final String KEY_TIME_FINISHED = "timeFinished";
	private static final String KEY_STEPS = "steps";
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	
	Context context;
	FileOutputStream fos;
	JsonWriter writer;
	
	public JSONWriter(Context context) {
		this.context = context;
	}
	
	public void writeToInternal(String filename, String data) throws IOException {
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
			Log.v("FILE CREATED", filename + " has been written");
		} 
		catch (IOException e) { e.printStackTrace(); } 
	}
	
	private String getFilename(int checklistId) {
		return "cid" + Integer.toString(checklistId) + "_finished.json";
	}
	
	public String startNewChecklist(Checklist checklist) throws IOException {		
		String filename = getFilename(checklist.getId());
		
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			Log.v("FINISHED CHECKLIST", filename + " has been created");
		} catch (IOException e) { e.printStackTrace(); }
		
		try {
			writer.beginObject();
			writer.name(KEY_USER_ID).value(1);
			writer.name(KEY_GROUP_ID).value(checklist.getGroupId());
			writer.name(KEY_CHECKLIST_ID).value(checklist.getId());
			writer.name(KEY_TIME_STARTED).value(checklist.getTimeStarted());
			writer.name(KEY_TIME_FINISHED).value(checklist.getTimeFinished());
			writer.name(KEY_STEPS);
			writer.beginArray();
		} catch (IOException e) { e.printStackTrace(); }
		
		return filename;
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
			if (!step.getExtraNote().isEmpty()) { writer.name(KEY_EXTRA_NOTE).value(step.getExtraNote()); }
			if (!step.getExtraImageFilename().isEmpty()) { writer.name(KEY_EXTRA_IMAGE).value(step.getExtraImageFilename()); }
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
			if (!step.getExtraNote().isEmpty()) { writer.name(KEY_EXTRA_NOTE).value(step.getExtraNote()); }
			if (!step.getExtraImageFilename().isEmpty()) { writer.name(KEY_EXTRA_IMAGE).value(step.getExtraImageFilename()); }
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
			if (!step.getExtraNote().isEmpty()) { writer.name(KEY_EXTRA_NOTE).value(step.getExtraNote()); }
			if (!step.getExtraImageFilename().isEmpty()) { writer.name(KEY_EXTRA_IMAGE).value(step.getExtraImageFilename()); }
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
			if (!step.getExtraNote().isEmpty()) { writer.name(KEY_EXTRA_NOTE).value(step.getExtraNote()); }
			if (!step.getExtraImageFilename().isEmpty()) { writer.name(KEY_EXTRA_IMAGE).value(step.getExtraImageFilename()); }
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
	
	public void logPost(String filename) {
		FileInputStream fis = null;
		try { fis = context.openFileInput(filename); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		StringBuffer fileContent = new StringBuffer("");

		byte[] buffer = new byte[1024];

		try {
			while (fis.read(buffer) != -1) {
			    fileContent.append(new String(buffer));
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
		
		Log.v("POST TO SERVER", fileContent.toString());
	}
	
}
