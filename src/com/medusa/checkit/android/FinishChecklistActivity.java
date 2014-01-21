package com.medusa.checkit.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FinishChecklistActivity extends Activity {
	
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_DOUBLE = "double";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	
	private JSONWriter jsonWriter;
	private ImageHandler imageHandler;
	private ArrayList<Step> stepsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_checklist);
		getActionBar().setTitle("");
		
		stepsArray = getIntent().getParcelableArrayListExtra("steps");
		
		imageHandler = new ImageHandler(this);
		
		TextView mChecklistName = (TextView) findViewById(R.id.checklist_name);
		Button mBtnFinishChecklist = (Button) findViewById(R.id.finish_checklist);
		mChecklistName.setText(getChecklistName());
		
		ListView listView = (ListView)findViewById(R.id.finished_steps_listview);
        StepAdapter adapter = new StepAdapter(this, R.layout.listview_step_row, stepsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				intent.putExtra("steps", stepsArray);
				intent.putExtra("stepNum", position);
				startActivity(intent);
				finish();
			}
        });
		
        mBtnFinishChecklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				PostToServerThread post = new PostToServerThread();
				post.start();
			}
		});
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
			
			if (step.getType().equalsIgnoreCase(TYPE_BOOL)) {
				try { jsonWriter.writeStepBoolean(step.getId(), step.getYesOrNo()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_DOUBLE)) {
				try { jsonWriter.writeStepDouble(step.getId(), step.getValue()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_TEXT)) {
				try { jsonWriter.writeStepText(step.getId(), step.getText()); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) {
				try { jsonWriter.writeStepImage(step.getId(), step.getImageFilename()); } 
				catch (IOException e) { e.printStackTrace(); }
				imageHandler.addFilenameToArray(step.getChecklistId(), step.getOrder());
			}
		}
	}
	
	private class PostToServerThread extends Thread {
		@Override
		public void run() {
			try {
	        	jsonWriter = new JSONWriter(getApplicationContext());
				jsonWriter.startNewChecklist(getChecklistId());
				writeAllStepsToJSON();
				jsonWriter.finishNewChecklist();
			} catch (IOException e) { e.printStackTrace(); }
			
			HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
			post.createNewPost(); 
			post.addJSON(JSONWriter.CHECKLIST_FILENAME);
			if (imageHandler.getArrayList() != null) { post.addPictures(imageHandler.getArrayList()); }
			post.sendPost();
			
			Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
			startActivity(intent);
			finish();
		}
	}

}
