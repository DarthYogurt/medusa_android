package com.medusa.checkit.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
	private LinearLayout notifyPersonContainer;
	private TextView notifyPerson;
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
		notifyPersonContainer = (LinearLayout) view.findViewById(R.id.notify_person_container);
		notifyPerson = (TextView) view.findViewById(R.id.notify_person);
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
		
		if (step.getTimeStarted().isEmpty()) { step.setTimeStarted(Utilities.getTimeStamp()); }
		
		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(numOfSteps));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { showNumberElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { showImageElements(); }
			
		// Show required extra note if not already finished
		if (!step.getIsAllFinished()) { updateReqExtrasMsg(); }
		
		// Show who will be notified
		if (!step.getNotifyUserName().isEmpty()) { showNotifyPerson(); }
		
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
				pwNotes.setWidth(550);
				pwNotes.setHeight(450);
				pwNotes.setContentView(pwViewNotes);
				pwNotes.setBackgroundDrawable(new BitmapDrawable());
				pwNotes.setAnimationStyle(R.style.AddNoteAnimation);
				pwNotes.showAtLocation(getActivity().findViewById(R.id.step_fragment), Gravity.CENTER, 0, -120);
				pwNotes.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() { 
						setExtraNote(); 
						getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				});
			    
			    noteInput.setText(step.getExtraNote());
			    
			    // Show keyboard when PopupWindow opens
			    showSoftKeyboard(noteInput);
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
					Toast.makeText(getActivity(), R.string.msg_starting_camera, Toast.LENGTH_SHORT).show();
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
				
				if (step.getIfBoolValueIs() != null && step.getIfBoolValueIs() == true) {
					showNotifyPersonList();
//					startRequiredExtra();
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
				
				if (step.getIfBoolValueIs() != null && step.getIfBoolValueIs() == false) {
					showNotifyPersonList();
//					startRequiredExtra();
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
			Toast.makeText(getActivity(), R.string.msg_starting_camera, Toast.LENGTH_SHORT).show();
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
		    		File file = new File(getActivity().getExternalFilesDir(null), step.getImageFilename());
					FileInputStream fis = new FileInputStream(file);
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
	
	private void startRequiredExtra() {
		if (step.getReqNote() && step.getReqPicture()) {
			if (!step.getIsReqNoteFinished()) {
				Toast message = Toast.makeText(getActivity(), R.string.toast_req_both, Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, 0, 0);
				message.show();
				btnAddNoteExtra.performClick();
				return;
			}
			if (!step.getIsReqPictureFinished()) {
        		Toast message = Toast.makeText(getActivity(), R.string.toast_req_both, Toast.LENGTH_SHORT);
    			message.setGravity(Gravity.CENTER, 0, 0);
    			message.show();
    			btnAddPictureExtra.performClick();
    			return;
        	}
        }
		
		if (step.getReqNote()) {
			if (!step.getIsReqNoteFinished()) {
				Toast message = Toast.makeText(getActivity(), R.string.toast_req_note, Toast.LENGTH_SHORT);
				message.setGravity(Gravity.CENTER, 0, 0);
				message.show();
				btnAddNoteExtra.performClick();
				return;
			}
			return;
        }
        
        if (step.getReqPicture()) {
        	if (!step.getIsReqPictureFinished()) {
        		Toast message = Toast.makeText(getActivity(), R.string.toast_req_image, Toast.LENGTH_SHORT);
    			message.setGravity(Gravity.CENTER, 0, 0);
    			message.show();
    			btnAddPictureExtra.performClick();
    			return;
        	}
        	return;
        }
	}
	
	private void updateReqExtrasMsg() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			if (step.getIfBoolValueIs() != null) {
				if (step.getYesOrNo() == step.getIfBoolValueIs()) {
					if (step.getReqNote() && step.getReqPicture()) {
						requiredExtrasMessage.setText("note & picture required");
						return;
					}
					if (step.getReqNote()) { 
						requiredExtrasMessage.setText("note required");
						return;
					}
					if (step.getReqPicture()) { 
						requiredExtrasMessage.setText("picture required");
						return;
					}
				}
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
				File file = new File(getActivity().getExternalFilesDir(null), step.getExtraImageFilename());
				FileInputStream fis = new FileInputStream(file);
				Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
				extraImage.setImageBitmap(imgFromFile);
				extraImage.invalidate();
				fis.close();
			} 
	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
	    	catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	private void showNotifyPersonList() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Person To Notify");

		ListView notifyPersonList = new ListView(getActivity());
		final ArrayList<User> usersArray = step.getUsers();
		NotifyPersonAdapter adapter = new NotifyPersonAdapter(getActivity(), R.layout.listview_notify_person_row, usersArray);
		notifyPersonList.setAdapter(adapter);

		builder.setView(notifyPersonList);
		final Dialog dialog = builder.create();

		dialog.show();
		
		notifyPersonList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog.dismiss();
				User user = usersArray.get(position);
				step.setNotifyUserId(user.getId());
				step.setNotifyUserName(user.getName());
				showNotifyPerson();
				startRequiredExtra();
			}
        });
	}
	
	private void showNotifyPerson() {
		notifyPersonContainer.setVisibility(View.VISIBLE);
		notifyPerson.setText(step.getNotifyUserName());
	}
	
	private void checkIfAllFinished() {
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			
			// Has a required extra if certain bool value is picked
			if (step.getIfBoolValueIs() != null && step.getIfBoolValueIs() == step.getYesOrNo()) {
				
				// Required note and picture
				if (step.getReqNote() && step.getReqPicture()) {
					if (step.getIsStepFinished() && step.getIsReqNoteFinished() && step.getIsReqPictureFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage(); 
						return;
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
						return;
					}
				}
				
				// Required note only
				if (step.getReqNote()) {
					if (step.getIsStepFinished() && step.getIsReqNoteFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage();
						return;
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
						return;
					}
				}
				
				// Required picture only
				if (step.getReqPicture()) {
					if (step.getIsStepFinished() && step.getIsReqPictureFinished()) { 
						step.setIsAllFinished(true);
						showFinishedImage(); 
						return;
					}
					else {
						step.setIsAllFinished(false);
						hideFinishedImage();
						return;
					}
				}
			}
			else {
				if (step.getIsStepFinished()) {
					step.setIsAllFinished(true);
					showFinishedImage(); 
					return;
				}
				else {
					step.setIsAllFinished(false);
					hideFinishedImage(); 
					return;
				}
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_NUMBER)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
				return;
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
				return;
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
				return;
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
				return;
			}
		}
		
		if (step.getType().equalsIgnoreCase(TYPE_IMAGE)) { 
			if (step.getIsStepFinished()) {
				step.setIsAllFinished(true);
				showFinishedImage(); 
				return;
			}
			else {
				step.setIsAllFinished(false);
				hideFinishedImage(); 
				return;
			}
		}
	}
	
	private void finishStep() {
		step.setIsStepFinished(true);
		step.setTimeFinished(Utilities.getTimeStamp());
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
		ImageHandler imageHandler = new ImageHandler(getActivity());
		String filename = "";
		
		switch (requestCode) {
		case REQUEST_PICTURE:
			filename = imageHandler.getImageFilename(step.getChecklistId(), step.getOrder(), false);
			step.setImageFilename(filename);
			break;
		case REQUEST_PICTURE_EXTRA:
			filename = imageHandler.getImageFilename(step.getChecklistId(), step.getOrder(), true);
			step.setExtraImageFilename(filename);
			break;
		default:
			break;
		}
		
		File file = new File(getActivity().getExternalFilesDir(null), filename);
		Uri outputFileUri = Uri.fromFile(file);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
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
				Log.i("IMAGE FILE WRITTEN", step.getImageFilename());
				
				ImageHandler.getOrientation(getActivity(), step.getImageFilename());
				
		    	finishStep();
		    	showResult();
		    	checkIfAllFinished();
		    	if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
		    }
			break;
			
		case REQUEST_PICTURE_EXTRA:
			if (resultCode == Activity.RESULT_OK) {
				Log.i("IMAGE FILE WRITTEN", step.getExtraImageFilename());
//				ImageHandler.compressImage(getActivity().getExternalFilesDir(null) + "/" + step.getExtraImageFilename());

				
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
	
	
	
	
	
	private void showSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private void hideSoftKeyboard() {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	}
	
	private void touchHandler(View view) {
	    // Set up touch listener for non-edittext views to hide keyboard.
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard();
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
	
}
