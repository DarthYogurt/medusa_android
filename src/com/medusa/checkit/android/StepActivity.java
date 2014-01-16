package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StepActivity extends Activity {
	
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_DOUBLE = "double";
	private static final String TYPE_TEXT = "text";
	
	private ArrayList<Step> mStepsArray;
	private Step mStep;
	private int mStepNum;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step);
		
		// TODO: change to parcelable instead of serializable
		mStepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
		mStepNum = getIntent().getIntExtra("stepNum", 0);
		mStep = mStepsArray.get(mStepNum);
		
		TextView mOrder = (TextView) findViewById(R.id.step_order);
		TextView mName = (TextView) findViewById(R.id.step_name);
		
		mOrder.setText(Integer.toString(mStep.getOrder()));
		mName.setText(mStep.getName());
		
		if (mStep.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (mStep.getType().equalsIgnoreCase(TYPE_DOUBLE)) { showDoubleElements(); }
		if (mStep.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		
		if (mStepNum == 0) { showNextButton(); }
		if (mStepNum > 0 && mStepNum < mStepsArray.size() - 1) { showNextButton(); showPrevButton(); }
		if (mStepNum == mStepsArray.size() - 1) { showPrevButton(); showFinishedButton(); }
	}
	
	private void showResult() {
		TextView mResult = (TextView) findViewById(R.id.result);
		
		if (mStep.getType().equalsIgnoreCase(TYPE_BOOL)) { 
			if (mStep.getIsStepFinished()) { 
				if (mStep.getYesOrNo() == true) { mResult.setText("Yes"); }
				else { mResult.setText("No"); }
			}
			else { mResult.setText(""); }
		}
		
		if (mStep.getType().equalsIgnoreCase(TYPE_DOUBLE)) { 
			if (mStep.getIsStepFinished()) {
				mResult.setText(Double.toString(mStep.getValue()));
			}
			else { mResult.setText(""); }
		}
		if (mStep.getType().equalsIgnoreCase(TYPE_TEXT)) { 
			if (mStep.getIsStepFinished()) {
				mResult.setText(mStep.getText());
			}
			else { mResult.setText(""); }
		}
	}
	
	private void showBoolElements() {
		RelativeLayout mBoolContainer = (RelativeLayout) findViewById(R.id.bool_container);
		Button mButtonYes = (Button) findViewById(R.id.button_yes);
		Button mButtonNo = (Button) findViewById(R.id.button_no);
		mBoolContainer.setVisibility(View.VISIBLE);
		showResult();
		
		mButtonYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mStep.setYesOrNo(true);
				mStep.setIsStepFinished(true);
				showResult();
			}
		});
		
		mButtonNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mStep.setYesOrNo(false);
				mStep.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showDoubleElements() {
		LinearLayout mDoubleContainer = (LinearLayout) findViewById(R.id.double_container);
		final EditText mDoubleInput = (EditText) findViewById(R.id.double_input);
		Button mButtonSubmit = (Button) findViewById(R.id.btn_submit_double);
		mDoubleContainer.setVisibility(View.VISIBLE);
		showResult();
		
		mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mDoubleInput.getText().toString();
				mStep.setValue(Double.parseDouble(input));
				mStep.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout mTextContainer = (LinearLayout) findViewById(R.id.text_container);
		final EditText mTextInput = (EditText) findViewById(R.id.text_input);
		Button mButtonSubmit = (Button) findViewById(R.id.btn_submit_text);
		mTextContainer.setVisibility(View.VISIBLE);
		showResult();
		
		mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mTextInput.getText().toString();
				mStep.setText(input);
				mStep.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showNextButton() {
		ImageButton mBtnNext = (ImageButton) findViewById(R.id.btn_next);
		mBtnNext.setVisibility(View.VISIBLE);
		mBtnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				intent.putExtra("steps", mStepsArray);
				intent.putExtra("stepNum", mStepNum + 1);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private void showPrevButton() {
		ImageButton mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
		mBtnPrev.setVisibility(View.VISIBLE);
		mBtnPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				intent.putExtra("steps", mStepsArray);
				intent.putExtra("stepNum", mStepNum - 1);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private void showFinishedButton() {
		Button mBtnFinishChecklist = (Button) findViewById(R.id.btn_finish_checklist);
		mBtnFinishChecklist.setVisibility(View.VISIBLE);
		mBtnFinishChecklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), FinishChecklistActivity.class);
				intent.putExtra("steps", mStepsArray);
				startActivity(intent);
				finish();
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.step, menu);
//		return true;
//	}

}
