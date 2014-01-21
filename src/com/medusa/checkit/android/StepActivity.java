package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StepActivity extends Activity {
	
	static final String KEY_CURRENT_STEP = "currentStep";
	static final String KEY_NUM_OF_STEPS = "numOfSteps";
	
	DrawerLayout drawerLayout;
	ListView drawerListView;
	FragmentManager fragmentManager;
	StepFragment fragment;
	
	private ArrayList<Step> stepsArray;
	private Step step;
	private int numOfSteps;
	private int currentStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_navdrawer);
		getActionBar().setTitle("");
		
		stepsArray = getIntent().getParcelableArrayListExtra("steps");
		currentStep = 0;
		step = stepsArray.get(currentStep);
		numOfSteps = stepsArray.size();
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListView = (ListView) findViewById(R.id.drawer_listview);
		StepAdapter adapter = new StepAdapter(this, R.layout.listview_step_row, stepsArray);
		drawerListView.setAdapter(adapter);
		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
		
		fragmentManager = getFragmentManager();
		fragment = new StepFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_CURRENT_STEP, step);
		bundle.putInt(KEY_NUM_OF_STEPS, numOfSteps);
	    fragment.setArguments(bundle);
		fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).commit();
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	// Swaps fragments in the main content view
	private void selectItem(int position) {
	    // Create a new fragment and specify the step to show based on position
		currentStep = position;
		step = stepsArray.get(currentStep);
		StepFragment fragment = new StepFragment();
	    Bundle bundle = new Bundle();
	    bundle.putParcelable(KEY_CURRENT_STEP, step);
	    bundle.putInt(KEY_NUM_OF_STEPS, numOfSteps);
	    fragment.setArguments(bundle);

	    // Insert the fragment by replacing any existing fragment
	    fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).commit();

	    // Highlight the selected item and close the drawer
	    drawerListView.setItemChecked(position, true);
	    drawerLayout.closeDrawer(drawerListView);
	}

//	private void goToNextStep() {
//		if (stepNum == stepsArray.size() - 1) { goToFinishChecklist(); } 
//		else {
//			Intent intent = new Intent(getApplicationContext(), StepActivity.class);
//			intent.putExtra("steps", stepsArray);
//			intent.putExtra("stepNum", stepNum + 1);
//			startActivity(intent);
//			finish();
//		}	
//	}
//	
//	private void goToPrevStep() {
//		Intent intent = new Intent(getApplicationContext(), StepActivity.class);
//		intent.putExtra("steps", stepsArray);
//		intent.putExtra("stepNum", stepNum - 1);
//		startActivity(intent);
//		finish();
//	}
//	
//	private void goToFinishChecklist() {
//		Intent intent = new Intent(getApplicationContext(), FinishChecklistActivity.class);
//		intent.putExtra("steps", stepsArray);
//		startActivity(intent);
//		finish();
//	}
//	
//	private void finishStep() {
//		step.setIsStepFinished(true);
//		ImageView finishedStepImg = (ImageView) findViewById(R.id.finished_step_img);
//		finishedStepImg.setVisibility(View.VISIBLE);
//	}
//	
//	private void showResult() {
//		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
//			if (step.getIsStepFinished()) { 
//				if (step.getYesOrNo() == true) { result.setText("Yes"); }
//				else { result.setText("No"); }
//			}
//			else { result.setText(""); }
//		}
//		
//		if (step.getType().equalsIgnoreCase(TYPE_DOUBLE)) { 
//			if (step.getIsStepFinished()) {
//				result.setText(Double.toString(step.getValue()));
//			}
//			else { result.setText(""); }
//		}
//		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
//			if (step.getIsStepFinished()) {
//				result.setText(step.getText());
//			}
//			else { result.setText(""); }
//		}
//	}
//	
//	private void showBoolElements() {
//		RelativeLayout boolContainer = (RelativeLayout) findViewById(R.id.bool_container);
//		Button btnYes = (Button) findViewById(R.id.button_yes);
//		Button btnNo = (Button) findViewById(R.id.button_no);
//		boolContainer.setVisibility(View.VISIBLE);
//		showResult();
//		
//		btnYes.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				step.setYesOrNo(true);
//				finishStep();
//				showResult();
//				goToNextStep();
//			}
//		});
//		
//		btnNo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				step.setYesOrNo(false);
//				finishStep();
//				showResult();
//				goToNextStep();
//			}
//		});
//	}
//	
//	private void showDoubleElements() {
//		LinearLayout doubleContainer = (LinearLayout) findViewById(R.id.double_container);
//		final EditText doubleInput = (EditText) findViewById(R.id.double_input);
//		Button btnSubmit = (Button) findViewById(R.id.btn_submit_double);
//		doubleContainer.setVisibility(View.VISIBLE);
//		showResult();
//		
//		btnSubmit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				String input = doubleInput.getText().toString();
//				step.setValue(Double.parseDouble(input));
//				finishStep();
//				showResult();
//				goToNextStep();
//			}
//		});
//	}
//	
//	private void showTextElements() {
//		LinearLayout textContainer = (LinearLayout) findViewById(R.id.text_container);
//		final EditText textInput = (EditText) findViewById(R.id.text_input);
//		Button btnSubmit = (Button) findViewById(R.id.btn_submit_text);
//		textContainer.setVisibility(View.VISIBLE);
//		showResult();
//		
//		btnSubmit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				String input = textInput.getText().toString();
//				step.setText(input);
//				finishStep();
//				showResult();
//				goToNextStep();
//			}
//		});
//	}
//	
//	private void showImageElements() {
//		LinearLayout imageContainer = (LinearLayout) findViewById(R.id.image_container);
//		Button btnTakePicture = (Button) findViewById(R.id.btn_take_picture);
//		imageContainer.setVisibility(View.VISIBLE);
//		
//		btnTakePicture.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				takePicture();
//			}
//		});
//	}
//	
//	private void showNextButton() {
//		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
//		btnNext.setVisibility(View.VISIBLE);
//		
//		btnNext.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) { goToNextStep(); }
//		});
//	}
//	
//	private void showPrevButton() {
//		ImageButton btnPrev = (ImageButton) findViewById(R.id.btn_prev);
//		btnPrev.setVisibility(View.VISIBLE);
//		
//		btnPrev.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) { goToPrevStep(); }
//		});
//	}
//	
//	private void takePicture() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		if (intent.resolveActivity(getPackageManager()) != null) {
//			startActivityForResult(intent, REQUEST_PICTURE);
//		}
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		String spokenText;
//		
//		// Handles picture taking after finished
//	    if (requestCode == REQUEST_PICTURE && resultCode == RESULT_OK) {
//	    	Bundle extras = data.getExtras();
//	    	Bitmap image = (Bitmap) extras.get("data");
//	    	
//	    	ImageView imageResult = (ImageView) findViewById(R.id.result_image);
//	    	imageResult.setImageBitmap(image);
//	    	
//	    	ImageHandler imageHandler = new ImageHandler(this);
//	    	imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder());
//	    	
//	    	step.setImageFilename(imageHandler.getFilename(step.getChecklistId(), step.getOrder()));
//	    	finishStep();
//	    	
//	    	try {
//				FileInputStream fis = openFileInput(imageHandler.getFilename(step.getChecklistId(), step.getOrder()));
//				Bitmap decoded = BitmapFactory.decodeStream(fis);
//				fis.close();
//				imageResult.setImageBitmap(decoded);
//			} 
//	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
//	    	catch (IOException e) { e.printStackTrace(); }
//	    }
//		
//		// Handles speech recording to text after finished
//	    if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
//	        List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//	        spokenText = results.get(0);
//	        
//	        if (currentStepType.equalsIgnoreCase(STEPTYPE_TEXT)) {
//	        	stepValues[currentStepOrder] = spokenText;
//	        	currentCard.setFootnote("Result: " + spokenText);
//				adapter.notifyDataSetChanged();
//	        }
//	        
//	        if (currentStepType.equalsIgnoreCase(STEPTYPE_DOUBLE)) {
//	        	spokenText.replaceAll(" ", "");
//	        	Double converted = Double.parseDouble(spokenText);
//	        	stepValues[currentStepOrder] = converted;
//	        	currentCard.setFootnote("Result: " + spokenText);
//				adapter.notifyDataSetChanged();
//	        }
//	    }
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.step, menu);
//		return true;
//	}

}
