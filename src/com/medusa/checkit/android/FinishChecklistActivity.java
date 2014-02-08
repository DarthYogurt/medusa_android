package com.medusa.checkit.android;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
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
		mChecklistName.setText(checklist.getName());
		
		ListView listView = (ListView)findViewById(R.id.finished_steps_listview);
        ReviewStepsAdapter adapter = new ReviewStepsAdapter(this, R.layout.listview_review_steps, stepsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(FinishChecklistActivity.this, StepActivity.class);
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, StepActivity.class);
		intent.putExtra(KEY_CHECKLIST, checklist);
		intent.putExtra(KEY_CHECKLIST_STEPS, stepsArray);
		intent.putExtra(KEY_CURRENT_STEP, 0);
		startActivity(intent);
		finish();
	}
	
	private boolean checkIfAllFinished() {
		boolean finished = false;
		
		for (int i = 0; i < stepsArray.size(); i++) {
			Step step = stepsArray.get(i);
			if (!step.getIsAllFinished()) { finished = false; break; }
			else { finished = true; }
		}
		
		return finished;
	}	
	
	private void writeAllStepsToJSON() {
		for (int i = 0; i < stepsArray.size(); i++) {
			Step step = stepsArray.get(i);
			
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
		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(FinishChecklistActivity.this);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.msg_uploading_files));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
	    protected Void doInBackground(Void... params) {
	    	String filename = "";
	    	ArrayList<String> imgFilenames = null;
	    	
	    	try {
	        	writer = new JSONWriter(FinishChecklistActivity.this);
				filename = writer.startNewChecklist(checklist);
				writeAllStepsToJSON();
				writer.finishNewChecklist();
				writer.logPost(filename);
			} catch (IOException e) { e.printStackTrace(); }
			
			if (Utilities.isNetworkAvailable(FinishChecklistActivity.this)) {
				final HTTPPostRequest post = new HTTPPostRequest(FinishChecklistActivity.this);
				post.createNewPost(); 
				post.addJSON(filename);
				if (imageHandler.getArrayList() != null) {
					imgFilenames = new ArrayList<String>();
					imgFilenames = imageHandler.getArrayList();
					post.addPictures(imgFilenames);
				}
				post.sendPost();
				
				// Show message from upload
				runOnUiThread(new Runnable() {
					public void run() {
						showUploadMessage(post.getResponseCode());
					}
				});
				
				// Deletes checklist file after uploaded
				Utilities.deleteFileFromInternal(FinishChecklistActivity.this, filename);
				
				// Deletes images after uploaded
				if (imgFilenames != null) {
					for (int i = 0; i < imgFilenames.size(); i++) {
						String imgFilename = imgFilenames.get(i);
						Utilities.deleteFileFromExternal(FinishChecklistActivity.this, imgFilename);
					}
				}
				
				Intent intent = new Intent(FinishChecklistActivity.this, SplashActivity.class);
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
	    	progressDialog.dismiss();
	        return;
	    }
	}
	
	private class NetworkErrorDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(FinishChecklistActivity.this);
	        builder.setMessage(R.string.dialog_network_error)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			Intent intent = new Intent(FinishChecklistActivity.this, SplashActivity.class);
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
	        AlertDialog.Builder builder = new AlertDialog.Builder(FinishChecklistActivity.this);
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
	        AlertDialog.Builder builder = new AlertDialog.Builder(FinishChecklistActivity.this);
	        builder.setMessage(R.string.dialog_finish)
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			checklist.setTimeFinished(Utilities.getTimeStamp());
	        			new PostToServerThread().execute();
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
	
	private void showUploadMessage(int responseCode) {
		if (responseCode == 200) {
			Toast.makeText(this, R.string.msg_checklist_upload_success, Toast.LENGTH_SHORT).show();
		}
	}

}
