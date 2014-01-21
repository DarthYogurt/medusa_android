package com.medusa.checkit.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StepFragment extends Fragment {
	
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_DOUBLE = "double";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";
	private static final int REQUEST_PICTURE = 1;
	
	private View view;
	private TextView result;
	private Step step;
	
	static StepFragment newInstance() {
		StepFragment fragment = new StepFragment();
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		
		Bundle bundle = getArguments();
		step = bundle.getParcelable(StepActivity.KEY_CURRENT_STEP);
		int numOfSteps = bundle.getInt(StepActivity.KEY_NUM_OF_STEPS);
		
		TextView order = (TextView) view.findViewById(R.id.step_order);
		TextView orderMax = (TextView) view.findViewById(R.id.step_order_max);
		TextView name = (TextView) view.findViewById(R.id.step_name);
		result = (TextView) view.findViewById(R.id.result);

		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(numOfSteps));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_DOUBLE)) { showDoubleElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { showImageElements(); }
		
        return view;
    }
	
	private void setStep(Step s) {
		step = s;
	}
	
//	private void goToNextStep() {
//		if (stepNum == stepsArray.size() - 1) { goToFinishChecklist(); } 
//		else {
//			Intent intent = new Intent(getActivity(), StepActivity.class);
//			intent.putExtra("steps", stepsArray);
//			intent.putExtra("stepNum", stepNum + 1);
//			startActivity(intent);
//			finish();
//		}	
//	}
//	
//	private void goToPrevStep() {
//		Intent intent = new Intent(getActivity(), StepActivity.class);
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
	
	private void finishStep() {
		step.setIsStepFinished(true);
		ImageView finishedStepImg = (ImageView) view.findViewById(R.id.finished_step_img);
		finishedStepImg.setVisibility(View.VISIBLE);
	}
	
	private void showResult() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			if (step.getIsStepFinished()) { 
				if (step.getYesOrNo() == true) { result.setText("Yes"); }
				else { result.setText("No"); }
			}
			else { result.setText(""); }
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_DOUBLE)) { 
			if (step.getIsStepFinished()) {
				result.setText(Double.toString(step.getValue()));
			}
			else { result.setText(""); }
		}
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (step.getIsStepFinished()) {
				result.setText(step.getText());
			}
			else { result.setText(""); }
		}
	}
	
	private void showBoolElements() {
		RelativeLayout boolContainer = (RelativeLayout) view.findViewById(R.id.bool_container);
		Button btnYes = (Button) view.findViewById(R.id.button_yes);
		Button btnNo = (Button) view.findViewById(R.id.button_no);
		boolContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(true);
				finishStep();
				showResult();
//				goToNextStep();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(false);
				finishStep();
				showResult();
//				goToNextStep();
			}
		});
	}
	
	private void showDoubleElements() {
		LinearLayout doubleContainer = (LinearLayout) view.findViewById(R.id.double_container);
		final EditText doubleInput = (EditText) view.findViewById(R.id.double_input);
		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_double);
		doubleContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = doubleInput.getText().toString();
				step.setValue(Double.parseDouble(input));
				finishStep();
				showResult();
//				goToNextStep();
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout textContainer = (LinearLayout) view.findViewById(R.id.text_container);
		final EditText textInput = (EditText) view.findViewById(R.id.text_input);
		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_text);
		textContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = textInput.getText().toString();
				step.setText(input);
				finishStep();
				showResult();
//				goToNextStep();
			}
		});
	}
	
	private void showImageElements() {
		LinearLayout imageContainer = (LinearLayout) view.findViewById(R.id.image_container);
		Button btnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		imageContainer.setVisibility(View.VISIBLE);
		
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				takePicture();
			}
		});
	}
	
	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_PICTURE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String spokenText;
		
		// Handles picture taking after finished
	    if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
	    	Bundle extras = data.getExtras();
	    	Bitmap image = (Bitmap) extras.get("data");
	    	
	    	ImageView imageResult = (ImageView) view.findViewById(R.id.result_image);
	    	imageResult.setImageBitmap(image);
	    	
	    	ImageHandler imageHandler = new ImageHandler(getActivity());
	    	imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder());
	    	
	    	step.setImageFilename(imageHandler.getFilename(step.getChecklistId(), step.getOrder()));
	    	finishStep();
	    	
//	    	try {
//				FileInputStream fis = openFileInput(imageHandler.getFilename(step.getChecklistId(), step.getOrder()));
//				Bitmap decoded = BitmapFactory.decodeStream(fis);
//				fis.close();
//				imageResult.setImageBitmap(decoded);
//			} 
//	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
//	    	catch (IOException e) { e.printStackTrace(); }
	    }
	}
}
