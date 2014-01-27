package com.medusa.checkit.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StepFragment extends Fragment {
	
	private static final String KEY_CURRENT_STEP = "currentStep";
	private static final String KEY_NUM_OF_STEPS = "numOfSteps";
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	private static final int REQUEST_PICTURE = 1;
	
	private View view;
	EditText numberInput;
	EditText textInput;
	private Step step;
	
	static StepFragment newInstance() {
		StepFragment fragment = new StepFragment();
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		LinearLayout stepFragment = (LinearLayout) view.findViewById(R.id.step_fragment);
		touchHandler(stepFragment);
		
		Bundle bundle = getArguments();
		step = bundle.getParcelable(KEY_CURRENT_STEP);
		int numOfSteps = bundle.getInt(KEY_NUM_OF_STEPS);
		
		TextView order = (TextView) view.findViewById(R.id.step_order);
		TextView orderMax = (TextView) view.findViewById(R.id.step_order_max);
		TextView name = (TextView) view.findViewById(R.id.step_name);
		
		if (step.getTimeStarted().equalsIgnoreCase("")) { setTimeStartedForStep(); }
		
		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(numOfSteps));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { showNumberElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { showImageElements(); }
		
		showAddNoteButton();
		showAddPictureButton();
		
		showNextButton();
		if (step.getOrder() > 1 && step.getOrder() <= numOfSteps) { showPrevButton(); }
		
		if (step.getIsStepFinished()) { finishStep(); }
		
        return view;
    }
	
	private void finishStep() {
		step.setIsStepFinished(true);
		setTimeFinishedForStep();
		ImageView finishedStepImg = (ImageView) view.findViewById(R.id.finished_step_img);
		finishedStepImg.setVisibility(View.VISIBLE);
	}
	
	private void unFinishStep() {
		step.setIsStepFinished(false);
		step.setTimeFinished("");
		ImageView finishedStepImg = (ImageView) view.findViewById(R.id.finished_step_img);
		finishedStepImg.setVisibility(View.GONE);
	}
	
	private void showResult() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			TextView resultBool = (TextView) view.findViewById(R.id.result_bool);
			resultBool.setVisibility(View.VISIBLE);
			
			if (step.getIsStepFinished()) { 
				if (step.getYesOrNo() == true) { resultBool.setText("Yes"); }
				else { resultBool.setText("No"); }
			}
			else { resultBool.setText(""); }
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { 
			if (step.getIsStepFinished()) {
				numberInput.setText(Double.toString(step.getNumber()));
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (step.getIsStepFinished()) {
				textInput.setText(step.getText());
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { 
//			Display display = getActivity().getWindowManager().getDefaultDisplay();
//			int width = display.getWidth();
//			int height = display.getHeight();
			
			ImageView resultImage = (ImageView) view.findViewById(R.id.result_image);
			resultImage.setVisibility(View.VISIBLE);
			
//			resultImage.setMinimumWidth(width);
//			resultImage.setMinimumHeight(height);
//			resultImage.setMaxWidth(width);
//			resultImage.setMaxHeight(height);
			
			if (step.getIsStepFinished()) {
		    	try {
					FileInputStream fis = getActivity().openFileInput(step.getImageFilename());
					Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
					fis.close();
					resultImage.setImageBitmap(imgFromFile);
					resultImage.invalidate();
				} 
		    	catch (FileNotFoundException e) { e.printStackTrace(); } 
		    	catch (IOException e) { e.printStackTrace(); }
			}
		}
	}
	
	private void setEditTextResult() {
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) {
			numberInput.clearFocus();
			String input = numberInput.getText().toString();
			if (!input.isEmpty()) {
				step.setNumber(Double.parseDouble(input));
				finishStep();
				showResult();
			}
			else { unFinishStep(); }
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) {
			textInput.clearFocus();
			String input = textInput.getText().toString().trim();
			if (!input.isEmpty()) {
				step.setText(input);
				finishStep();
				showResult();
			}
			else { unFinishStep(); }
		}
	}
	
	private void showBoolElements() {
		RelativeLayout boolContainer = (RelativeLayout) view.findViewById(R.id.bool_container);
		ImageButton btnYes = (ImageButton) view.findViewById(R.id.button_yes);
		ImageButton btnNo = (ImageButton) view.findViewById(R.id.button_no);
		boolContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(true);
				finishStep();
				showResult();
				((StepActivity)getActivity()).goToNextStep();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(false);
				finishStep();
				showResult();
				((StepActivity)getActivity()).goToNextStep();
			}
		});
	}
	
	private void showNumberElements() {
		numberInput = (EditText) view.findViewById(R.id.result_number);
		numberInput.setVisibility(View.VISIBLE);
		
		if (!step.getIsStepFinished()) {
			numberInput.requestFocus();
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
		showResult();
	}
	
	private void showTextElements() {
		textInput = (EditText) view.findViewById(R.id.result_text);
		textInput.setVisibility(View.VISIBLE);
		
		if (!step.getIsStepFinished()) {
			textInput.requestFocus();
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
		showResult();
	}
	
	private void showImageElements() {
		LinearLayout imageContainer = (LinearLayout) view.findViewById(R.id.image_container);
		Button btnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		imageContainer.setVisibility(View.VISIBLE);
		showResult();
		
		if (!step.getIsStepFinished()) {
			Toast message = Toast.makeText(getActivity(), "Starting Camera", Toast.LENGTH_SHORT);
			message.show();
			NewPictureThread newPicture = new NewPictureThread();
			newPicture.start();
		}
		
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startCameraActivity();
			}
		});
	}
	
	private class NewPictureThread extends Thread {
		@Override
		public void run() {
			try { Thread.sleep(1000); startCameraActivity(); } 
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	private void startCameraActivity() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_PICTURE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Handles taken picture after finished
	    if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
	    	Bundle extras = data.getExtras();
	    	Bitmap image = (Bitmap) extras.get("data");
	    	
	    	ImageHandler imageHandler = new ImageHandler(getActivity());
	    	String filename = imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder());
	    	step.setImageFilename(filename);
	    	
	    	finishStep();
	    	showResult();
	    }
	}
	
	private void showAddNoteButton() {
		ImageButton btnAddNoteExtra = (ImageButton) view.findViewById(R.id.btn_add_note_extra);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View window = inflater.inflate(R.layout.add_note_extra, null, false);
		final int width = 600;
		final int height = 800;
		
		btnAddNoteExtra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupWindow pw = new PopupWindow(window, width, height, true);
				pw.showAtLocation(getActivity().findViewById(R.id.step_fragment), Gravity.CENTER, 0, 0);
			}
		});
	}
	
	private void showAddPictureButton() {
		ImageButton btnAddPictureExtra = (ImageButton) view.findViewById(R.id.btn_add_picture_extra);
		btnAddPictureExtra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
	}
	
	private void showNextButton() {
		ImageButton btnNext = (ImageButton) view.findViewById(R.id.btn_next);
		btnNext.setVisibility(View.VISIBLE);
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) { ((StepActivity)getActivity()).goToNextStep(); }
		});
	}

	private void showPrevButton() {
		ImageButton btnPrev = (ImageButton) view.findViewById(R.id.btn_prev);
		btnPrev.setVisibility(View.VISIBLE);
		
		btnPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) { ((StepActivity)getActivity()).goToPrevStep(); }
		});
	}
	
	private static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	private void touchHandler(View view) {
	    //Set up touch listener for non-edittext views to hide keyboard.
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(getActivity());
	        	    setEditTextResult();
	                return false;
	            }
	        });
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            touchHandler(innerView);
	        }
	    }
	}
	
	private void setTimeStartedForStep() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		step.setTimeStarted(now);
	}
	
	private void setTimeFinishedForStep() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String now = sdf.format(new Date());
		step.setTimeFinished(now);
	}
}
