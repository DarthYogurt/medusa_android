package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;

public class FileManager {
	
	private static final String FILENAME_CHECKLISTS = "checklists.json";
	
	private Context context;
	HTTPGetRequest getRequest;
	JSONWriter writer;
	
	public FileManager(Context context) {
		this.context = context;
		this.writer = new JSONWriter(context);
	}

	
	
	public class UpdateChecklistFile extends Thread {
		
		private int groupId;
		
		public UpdateChecklistFile(int groupId) {
			this.groupId = groupId;
		}
		
		public void run() {
			String checklistsJsonString = "";
			try { checklistsJsonString = getRequest.getChecklists(groupId); }
			catch (MalformedURLException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
			
			try { writer.writeToInternal(FILENAME_CHECKLISTS, checklistsJsonString); } 
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public class UpdateStepFile extends Thread {
		
		private int checklistId;
		
		public UpdateStepFile(int checklistId) {
			this.checklistId = checklistId;
		}
		
		public void run() {
			String stepsJsonString = "";
			try { stepsJsonString = getRequest.getSteps(checklistId); }
			catch (MalformedURLException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			String filename = "cid" + Integer.toString(checklistId) + "_steps.json";
			try { writer.writeToInternal(filename, stepsJsonString); }
			catch (IOException e) { e.printStackTrace(); }
		}
	}
	
}
