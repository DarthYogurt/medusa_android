package com.medusa.checkit.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	
	private static final String KEY_CHECKLIST = "checklist";
	private static final String KEY_CHECKLIST_STEPS = "checklistSteps";
	private static final String KEY_CURRENT_STEP = "currentStep";
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	
	private JSONWriter writer;
	private ImageHandler imageHandler;
	private Checklist checklist;
	private ArrayList<Step> stepsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_checklist);
		getActionBar().setTitle("");
		
		checklist = getIntent().getParcelableExtra(KEY_CHECKLIST);
		stepsArray = getIntent().getParcelableArrayListExtra(KEY_CHECKLIST_STEPS);
		
		imageHandler = new ImageHandler(this);
		
		TextView mChecklistName = (TextView) findViewById(R.id.checklist_name);
		Button mBtnFinishChecklist = (Button) findViewById(R.id.finish_checklist);
		mChecklistName.setText(getChecklistName());
		
		ListView listView = (ListView)findViewById(R.id.finished_steps_listview);
        StepAdapter adapter = new StepAdapter(this, R.layout.listview_review_steps, stepsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				intent.putExtra(KEY_CHECKLIST, checklist);
				intent.putExtra(KEY_CHECKLIST_STEPS, stepsArray);
				intent.putExtra(KEY_CURRENT_STEP, position);
				startActivity(intent);
				finish();
			}
        });
		
        mBtnFinishChecklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setTimeFinishedForChecklist();
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
	
	private void setTimeFinishedForChecklist() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		checklist.setTimeFinished(now);
	}
	
	private void writeAllStepsToJSON() {
		Step step;
		
		for (int i = 0; i < stepsArray.size(); i++) {
			step = stepsArray.get(i);
			
			if (step.getType().equalsIgnoreCase(TYPE_BOOL)) {
				try { writer.writeStepBoolean(step); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) {
				try { writer.writeStepNumber(step); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_TEXT)) {
				try { writer.writeStepText(step); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) {
				try { writer.writeStepImage(step); } 
				catch (IOException e) { e.printStackTrace(); }
				imageHandler.addFilenameToArray(step.getImageFilename());
			}
		}
	}
	
	private class PostToServerThread extends Thread {
		String filename;
		
		@Override
		public void run() {
			try {
	        	writer = new JSONWriter(getApplicationContext());
				filename = writer.startNewChecklist(checklist);
				writeAllStepsToJSON();
				writer.finishNewChecklist();
				writer.logPost(filename);
			} catch (IOException e) { e.printStackTrace(); }
			
			HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
			post.createNewPost(); 
			post.addJSON(filename);
			if (imageHandler.getArrayList() != null) { post.addPictures(imageHandler.getArrayList()); }
			post.sendPost();
			
			Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
			startActivity(intent);
			finish();
		}
	}

}
