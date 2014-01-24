package com.medusa.checkit.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	private ImageView imageResult;
	private Step step;
	
	static StepFragment newInstance() {
		StepFragment fragment = new StepFragment();
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		
		Bundle bundle = getArguments();
		step = bundle.getParcelable(KEY_CURRENT_STEP);
		int numOfSteps = bundle.getInt(KEY_NUM_OF_STEPS);
		
		TextView order = (TextView) view.findViewById(R.id.step_order);
		TextView orderMax = (TextView) view.findViewById(R.id.step_order_max);
		TextView name = (TextView) view.findViewById(R.id.step_name);
		
		imageResult = (ImageView) view.findViewById(R.id.result_image);
		
		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(numOfSteps));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { showDoubleElements(); }
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
		ImageView finishedStepImg = (ImageView) view.findViewById(R.id.finished_step_img);
		finishedStepImg.setVisibility(View.VISIBLE);

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
		
//		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { 
//			if (step.getIsStepFinished()) {
//				result.setText(Double.toString(step.getNumber()));
//			}
//			else { result.setText(""); }
//		}
//		
//		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
//			if (step.getIsStepFinished()) {
//				result.setText(step.getText());
//			}
//			else { result.setText(""); }
//		}
		
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { 
			ImageView resultImage = (ImageView) view.findViewById(R.id.result_image);
			resultImage.setVisibility(View.VISIBLE);
			
			if (step.getIsStepFinished()) {
		    	try {
					FileInputStream fis = getActivity().openFileInput(step.getImageFilename());
					Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
					fis.close();
					imageResult.setImageBitmap(imgFromFile);
					imageResult.invalidate();
				} 
		    	catch (FileNotFoundException e) { e.printStackTrace(); } 
		    	catch (IOException e) { e.printStackTrace(); }
			}
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
	
	private void showDoubleElements() {
//		LinearLayout doubleContainer = (LinearLayout) view.findViewById(R.id.double_container);
		EditText numberInput = (EditText) view.findViewById(R.id.result_number);
		numberInput.setVisibility(View.VISIBLE);
		numberInput.requestFocus();
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
//		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_double);
//		doubleContainer.setVisibility(View.VISIBLE);
		showResult();
		
//		btnSubmit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				try {
//					String input = doubleInput.getText().toString();
//					step.setNumber(Double.parseDouble(input));
//					finishStep();
//					showResult();
//					((StepActivity)getActivity()).goToNextStep();
//				} catch (NumberFormatException e) {
//					Toast error;
//					error = Toast.makeText(getActivity(), "No number entered", Toast.LENGTH_SHORT);
//					error.show();
//				}
//				
//			}
//		});
	}
	
	private void showTextElements() {
//		LinearLayout textContainer = (LinearLayout) view.findViewById(R.id.text_container);
		EditText textInput = (EditText) view.findViewById(R.id.result_text);
		textInput.setVisibility(View.VISIBLE);
		textInput.requestFocus();
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
//		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_text);
//		textContainer.setVisibility(View.VISIBLE);
//		showResult();
		
//		btnSubmit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				String input = textInput.getText().toString().trim();
//				if (!input.isEmpty()) {
//					step.setText(input);
//					finishStep();
//					showResult();
//					((StepActivity)getActivity()).goToNextStep();
//				}
//				else {
//					Toast error;
//					error = Toast.makeText(getActivity(), "No text entered", Toast.LENGTH_SHORT);
//					error.show();
//				}
//			}
//		});
	}
	
	private void showImageElements() {
		LinearLayout imageContainer = (LinearLayout) view.findViewById(R.id.image_container);
		Button btnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		imageContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivityForResult(intent, REQUEST_PICTURE);
				}
			}
		});
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String spokenText;
		
		// Handles picture taking after finished
	    if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
	    	Bundle extras = data.getExtras();
	    	Bitmap image = (Bitmap) extras.get("data");
	    	
	    	ImageHandler imageHandler = new ImageHandler(getActivity());
	    	imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder());
	    	step.setImageFilename(imageHandler.getFilename(step.getChecklistId(), step.getOrder()));
	    	
	    	finishStep();
	    	showResult();
	    }
	}
}
