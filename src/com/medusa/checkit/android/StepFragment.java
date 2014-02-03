package com.medusa.checkit.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	private static final int REQUEST_PICTURE_EXTRA = 2;
	
	private View view;
	private View pwViewNotes;
	private View pwViewPicture;
	private PopupWindow pwNotes;
	private PopupWindow pwPicture;
	private EditText numberInput;
	private EditText textInput;
	private EditText noteInput;
	private ImageButton btnAddNoteExtra;
	private ImageButton btnAddPictureExtra;
	private Button btnTakePictureExtra;
	private ImageView finishedStepImg;
	private ImageButton btnNext;
	private ImageButton btnPrev;
	private TextView requiredExtrasMessage;
	private ImageView extraImage;
	private Step step;

	static StepFragment newInstance() {
		StepFragment fragment = new StepFragment();
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		pwViewNotes = inflater.inflate(R.layout.add_note_extra, null, false);
		pwViewPicture = inflater.inflate(R.layout.add_picture_extra, null, false);
		pwNotes = new PopupWindow(getActivity());
		pwPicture = new PopupWindow(getActivity());
		
		TextView order = (TextView) view.findViewById(R.id.step_order);
		TextView orderMax = (TextView) view.findViewById(R.id.step_order_max);
		TextView name = (TextView) view.findViewById(R.id.step_name);
		requiredExtrasMessage = (TextView) view.findViewById(R.id.required_extras);
		finishedStepImg = (ImageView) view.findViewById(R.id.finished_step_img);
		btnNext = (ImageButton) view.findViewById(R.id.btn_next);
		btnPrev = (ImageButton) view.findViewById(R.id.btn_prev);
		btnAddNoteExtra = (ImageButton) view.findViewById(R.id.btn_add_note_extra);
		btnAddPictureExtra = (ImageButton) view.findViewById(R.id.btn_add_picture_extra);
		btnTakePictureExtra = (Button) pwViewPicture.findViewById(R.id.btn_take_picture_extra);
		noteInput = (EditText) pwViewNotes.findViewById(R.id.add_note_edittext);
		extraImage = (ImageView) pwViewPicture.findViewById(R.id.extra_image);
		
		LinearLayout stepFragment = (LinearLayout) view.findViewById(R.id.step_fragment);
		touchHandler(stepFragment);
		
		Bundle bundle = getArguments();
		step = bundle.getParcelable(KEY_CURRENT_STEP);
		int numOfSteps = bundle.getInt(KEY_NUM_OF_STEPS);
		
		if (step.getTimeStarted().isEmpty()) { setTimeStartedForStep(); }
		
		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(numOfSteps));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { showNumberElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { showImageElements(); }
			
		// Click listener for Extra Note PopupWindow
		btnAddNoteExtra.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View view) {
				pwNotes.setTouchable(true);
				pwNotes.setFocusable(true);
				pwNotes.setOutsideTouchable(true);
				pwNotes.setTouchInterceptor(new OnTouchListener() {
			        public boolean onTouch(View v, MotionEvent event) {
			            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			            	pwNotes.dismiss();
			                return true;
			            }
			            return false;
			        }
			    });
				pwNotes.setWidth(500);
				pwNotes.setHeight(700);
				pwNotes.setContentView(pwViewNotes);
				pwNotes.setBackgroundDrawable(new BitmapDrawable());
				pwNotes.setAnimationStyle(R.style.AddNoteAnimation);
				pwNotes.showAtLocation(getActivity().findViewById(R.id.step_fragment), Gravity.CENTER, 0, 0);
				pwNotes.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() { setExtraNote(); }
				});
			    
			    noteInput.setText(step.getExtraNote());
			    
			    // Show keyboard when PopupWindow opens
			    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                imm.showSoftInput(noteInput, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		// Click listener for Extra Picture PopupWindow
		btnAddPictureExtra.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View view) {
				pwPicture.setTouchable(true);
				pwPicture.setFocusable(true);
				pwPicture.setOutsideTouchable(true);
				pwPicture.setTouchInterceptor(new OnTouchListener() {
			        public boolean onTouch(View v, MotionEvent event) {
			            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			            	pwPicture.dismiss();
			                return true;
			            }
			            return false;
			        }
			    });
				pwPicture.setWidth(500);
				pwPicture.setHeight(600);
				pwPicture.setContentView(pwViewPicture);
				pwPicture.setBackgroundDrawable(new BitmapDrawable());
				pwPicture.setAnimationStyle(R.style.AddNoteAnimation);
				pwPicture.showAtLocation(getActivity().findViewById(R.id.step_fragment), Gravity.CENTER, 0, 0);
				pwPicture.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() {  }
				});
			    
				showExtraPicture();
				
			    if (step.getExtraImageFilename().isEmpty()) {
					Toast message = Toast.makeText(getActivity(), "Starting Camera", Toast.LENGTH_SHORT);
					message.show();
					NewPictureThread newPicture = new NewPictureThread(REQUEST_PICTURE_EXTRA);
					newPicture.start();
				}
			}
		});
		
		// Click listener for Take Picture button in Extra Picture PopupWindow 
		btnTakePictureExtra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startCameraActivity(REQUEST_PICTURE_EXTRA);
			}
		});
		
		showNextButton();
		if (step.getOrder() > 1 && step.getOrder() <= numOfSteps) { showPrevButton(); }
		
		if (step.getIsAllFinished()) { showFinishedImage(); }
		
        return view;
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
				checkIfAllFinished();
				updateReqExtrasMsg();
				
				if (step.getIfBoolValueIs() == true) {
					RequiredExtrasDialogFrament dialog = new RequiredExtrasDialogFrament();
					dialog.show(getFragmentManager(), "requiredExtras");
				}
				
				if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(false);
				finishStep();
				showResult();
				checkIfAllFinished();
				updateReqExtrasMsg();
				
				if (step.getIfBoolValueIs() == false) {
					RequiredExtrasDialogFrament dialog = new RequiredExtrasDialogFrament();
					dialog.show(getFragmentManager(), "requiredExtras");
				}
				
				if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
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
			NewPictureThread newPicture = new NewPictureThread(REQUEST_PICTURE);
			newPicture.start();
		}
		
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startCameraActivity(REQUEST_PICTURE);
			}
		});
	}
	
	private void showResult() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			TextView resultBool = (TextView) view.findViewById(R.id.result_bool);
			resultBool.setVisibility(View.VISIBLE);
			
			if (step.getYesOrNo() != null) { 
				if (step.getYesOrNo() == true) { resultBool.setText("Yes"); }
				else { resultBool.setText("No"); }
			}
			else { resultBool.setText(""); }
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { 
			if (step.getNumber() != null) {
				numberInput.setText(Double.toString(step.getNumber()));
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (!step.getText().isEmpty()) {
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
			
			if (!step.getImageFilename().isEmpty()) {
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
				checkIfAllFinished();
			}
			else { unFinishStep(); checkIfAllFinished(); }
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) {
			textInput.clearFocus();
			String input = textInput.getText().toString().trim();
			if (!input.isEmpty()) {
				step.setText(input);
				finishStep();
				showResult();
				checkIfAllFinished();
			}
			else { unFinishStep(); checkIfAllFinished(); }
		}
	}
	
	private void updateReqExtrasMsg() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			if (step.getYesOrNo() == step.getIfBoolValueIs()) {
				if (step.getReqNote()) { 
					requiredExtrasMessage.setText("note required"); 
				}
				if (step.getReqPicture()) { 
					requiredExtrasMessage.setText("picture required"); 
				}
				if (step.getReqNote() && step.getReqPicture()) {
					requiredExtrasMessage.setText("note & picture required"); 
				}
			}
			else {
				requiredExtrasMessage.setText("");
			}
		}
	}
	
	private void setExtraNote() {
		String input = noteInput.getText().toString().trim();
		
		if (!input.isEmpty()) {
			step.setExtraNote(input);
			if (step.getReqNote()) { step.setIsReqNoteFinished(true); }
			checkIfAllFinished();
			if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
		}
		else {
			step.setExtraNote(input);
			if (step.getReqNote()) { step.setIsReqNoteFinished(false); }
			checkIfAllFinished(); 
		}
	}
	
	private void showExtraPicture() {
		if (step.getIsReqPictureFinished()) {
			try {
				FileInputStream fis = getActivity().openFileInput(step.getExtraImageFilename());
				Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
				extraImage.setImageBitmap(imgFromFile);
				extraImage.invalidate();
				fis.close();
			} 
	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
	    	catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	private void checkIfAllFinished() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			if (step.getIfBoolValueIs() != null && step.getIfBoolValueIs() == step.getYesOrNo()) {
				// Required note and picture
				if (step.getReqNote() && step.getReqPicture()) {
					if (step.getIsStepFinished() && step.getIsReqNoteFinished() && step.getIsReqPictureFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage(); 
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
					}
				}
				
				// Required note only
				if (step.getReqNote()) {
					if (step.getIsStepFinished() && step.getIsReqNoteFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage(); 
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
					}
				}
				
				// Required picture only
				if (step.getReqPicture()) {
					if (step.getIsStepFinished() && step.getIsReqPictureFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage(); 
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
					}
				}
			}
			else {
				if (step.getIsStepFinished()) {
					step.setIsAllFinished(true);
					showFinishedImage(); 
				}
				else {
					step.setIsAllFinished(false);
					hideFinishedImage(); 
				}
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
			}
		}
	}
	
	private void finishStep() {
		step.setIsStepFinished(true);
		setTimeFinishedForStep();
	}
	
	private void unFinishStep() {
		step.setIsStepFinished(false);
		step.setTimeFinished("");
	}
	
	private void showFinishedImage() {
		finishedStepImg.setVisibility(View.VISIBLE);
	}
	
	private void hideFinishedImage() {
		finishedStepImg.setVisibility(View.GONE);
	}
	
	private void showNextButton() {
		btnNext.setVisibility(View.VISIBLE);
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) { ((StepActivity)getActivity()).goToNextStep(); }
		});
	}

	private void showPrevButton() {
		btnPrev.setVisibility(View.VISIBLE);
		
		btnPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) { ((StepActivity)getActivity()).goToPrevStep(); }
		});
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
	
	private class NewPictureThread extends Thread {
		
		int requestCode;
		
		private NewPictureThread(int requestCode) {
			this.requestCode = requestCode;
		}
		
		@Override
		public void run() {
			try { Thread.sleep(1000); startCameraActivity(requestCode); } 
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	private void startCameraActivity(int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, requestCode);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
		    	Bundle extras = data.getExtras();
		    	Bitmap image = (Bitmap) extras.get("data");
		    	
		    	ImageHandler imageHandler = new ImageHandler(getActivity());
		    	String filename = imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder(), false);
		    	step.setImageFilename(filename);
		    	
		    	finishStep();
		    	showResult();
		    	checkIfAllFinished();
		    	if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
		    }
			break;
			
		case REQUEST_PICTURE_EXTRA:
			if (resultCode == Activity.RESULT_OK) {
		    	Bundle extras = data.getExtras();
		    	Bitmap image = (Bitmap) extras.get("data");
		    	
		    	ImageHandler imageHandler = new ImageHandler(getActivity());
		    	String filename = imageHandler.writeToFile(image, step.getChecklistId(), step.getOrder(), true);
		    	step.setExtraImageFilename(filename);
		    	
		    	if (step.getReqPicture()) { step.setIsReqPictureFinished(true); }
		    	showExtraPicture();
		    	checkIfAllFinished();
		    	if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
		    }
			break;
		
		default:
			break;
		}
	}
	
	private static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	private void touchHandler(View view) {
	    // Set up touch listener for non-edittext views to hide keyboard.
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(getActivity());
	        	    setEditTextResult();
	                return false;
	            }
	        });
	    }

	    // If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            touchHandler(innerView);
	        }
	    }
	}
	
	private class RequiredExtrasDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        if (step.getReqNote()) {
	        	builder.setMessage(R.string.dialog_req_note)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
						btnAddNoteExtra.performClick();
	        		}
	        	});
	        }
	        
	        if (step.getReqPicture()) {
	        	builder.setMessage(R.string.dialog_req_image)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
						btnAddPictureExtra.performClick();
	        		}
	        	});
	        }
	        
	        if (step.getReqNote() && step.getReqPicture()) {
	        	builder.setMessage(R.string.dialog_req_both)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
						dismiss();
	        		}
	        	});
	        }
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
}
