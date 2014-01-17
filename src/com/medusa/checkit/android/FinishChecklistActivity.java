package com.medusa.checkit.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class FinishChecklistActivity extends Activity {
	
	private JSONWriter jsonWriter;
	private ArrayList<Step> stepsArray;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_checklist);
		
		// TODO: change to parcelable instead of serializable
		stepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
		
		TextView checklistName = (TextView) findViewById(R.id.checklist_name);
		checklistName.setText(getChecklistName());
		
		ListView listView = (ListView)findViewById(R.id.finished_steps_listview);
        StepAdapter adapter = new StepAdapter(this, R.layout.listview_step_row, stepsArray);
        listView.setAdapter(adapter);
		
		try {
        	jsonWriter = new JSONWriter(this);
			jsonWriter.startNewChecklist(getChecklistId());
			writeAllStepsToJSON();
			jsonWriter.finishNewChecklist();
			HTTPPostThread post = new HTTPPostThread();
			post.start();
		} catch (IOException e) { e.printStackTrace(); }
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.finish_checklist, menu);
//		return true;
//	}
	
	private int getChecklistId() {
		Step step = stepsArray.get(0);
		return step.getChecklistId();
	}
	
	private String getChecklistName() {
		Step step = stepsArray.get(0);
		return step.getChecklistName();
	}
	
	private void writeAllStepsToJSON() {
		Step step;
		
		for (int i = 0; i < stepsArray.size(); i++) {
			step = stepsArray.get(i);
			
			if (step.getType().equalsIgnoreCase("bool")) {
				try { jsonWriter.writeStepBoolean(step.getId(), step.getYesOrNo()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase("double")) {
				try { jsonWriter.writeStepDouble(step.getId(), step.getValue()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase("text")) {
				try { jsonWriter.writeStepText(step.getId(), step.getText()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
		}
	}
	
	private class HTTPPostThread extends Thread {
		@Override
		public void run() {
			HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
			try { post.multipartPost(JSONWriter.CHECKLIST_FILENAME); } 
			catch (ClientProtocolException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
	}

}
