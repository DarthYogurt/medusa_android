package com.medusa.checkit.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
	
	private Preferences preferences;
	private JSONWriter writer;
	private ImageHandler imageHandler;
	private Checklist checklist;
	private ArrayList<Step> stepsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_checklist);
		getActionBar().setTitle("");
		
		preferences = new Preferences(getApplicationContext());
		
		checklist = getIntent().getParcelableExtra(KEY_CHECKLIST);
		stepsArray = getIntent().getParcelableArrayListExtra(KEY_CHECKLIST_STEPS);
		
		imageHandler = new ImageHandler(this);
		
		TextView mChecklistName = (TextView) findViewById(R.id.checklist_name);
		Button mBtnFinishChecklist = (Button) findViewById(R.id.finish_checklist);
		mChecklistName.setText(checklist.getName());
		
		ListView listView = (ListView)findViewById(R.id.finished_steps_listview);
        ReviewStepsAdapter adapter = new ReviewStepsAdapter(this, R.layout.listview_review_steps, stepsArray);
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
				if (checkIfAllFinished()) {
					FinishChecklistDialogFrament dialog = new FinishChecklistDialogFrament();
					dialog.show(getFragmentManager(), "finished");
				}
				else {
					NotCompleteDialogFrament dialog = new NotCompleteDialogFrament();
					dialog.show(getFragmentManager(), "notComplete");
				}
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.finish_checklist, menu);
//		return true;
//	}
	
	private boolean checkIfAllFinished() {
		boolean finished = false;
		
		for (int i = 0; i < stepsArray.size(); i++) {
			Step step = stepsArray.get(i);
			if (!step.getIsAllFinished()) { finished = false; break; }
			else { finished = true; }
		}
		
		return finished;
	}

	private String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		return now;
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
			addExtraImagesToArray(step);
		}
	}
	
	private void addExtraImagesToArray(Step step) {
		if (!step.getExtraImageFilename().isEmpty()) {
			imageHandler.addFilenameToArray(step.getExtraImageFilename());
		}
	}
	
	private class PostToServerThread extends AsyncTask<Void, Void, Void> {
	    protected Void doInBackground(Void... params) {
	    	
	    	String filename = "";
	    	ArrayList<String> imgFilenames = null;
	    	
	    	try {
	        	writer = new JSONWriter(getApplicationContext());
				filename = writer.startNewChecklist(checklist);
				writeAllStepsToJSON();
				writer.finishNewChecklist();
				writer.logPost(filename);
			} catch (IOException e) { e.printStackTrace(); }
			
			if (isNetworkAvailable()) {
				final HTTPPostRequest post = new HTTPPostRequest(getApplicationContext());
				post.createNewPost(); 
				post.addJSON(filename);
				if (imageHandler.getArrayList() != null) {
					imgFilenames = new ArrayList<String>();
					imgFilenames = imageHandler.getArrayList();
					post.addPictures(imgFilenames);
				}
				post.sendPost();
				
				runOnUiThread(new Runnable() {
					public void run() {
						showUploadMessage(post.getResponseCode());
					}
				});
				
				// Deletes checklist file after uploaded
				Utilities.deleteFile(getApplicationContext(), filename);
				
				// Deletes images after uploaded
				if (imgFilenames != null) {
					for (int i = 0; i < imgFilenames.size(); i++) {
						String imgFilename = imgFilenames.get(i);
						Utilities.deleteFile(getApplicationContext(), imgFilename);
					}
				}
				
				Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
				startActivity(intent);
				finish();
			}
			else {
				NetworkErrorDialogFrament dialog = new NetworkErrorDialogFrament();
				dialog.show(getFragmentManager(), "networkError");
			}
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	        return;
	    }
	}
	
	private class NetworkErrorDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_network_error)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
	        			startActivity(intent);
	        			finish();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private class NotCompleteDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_not_complete)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
						dismiss();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private class FinishChecklistDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_finish)
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			checklist.setTimeFinished(getTimeStamp());
						PostToServerThread post = new PostToServerThread();
						post.execute();
	        		}
	        	})
	        	.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			dismiss();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void showUploadMessage(int responseCode) {
		if (responseCode == 200) {
			Toast.makeText(getApplicationContext(), "Checklist uploaded successfully!", Toast.LENGTH_SHORT).show();
		}
	}

}
