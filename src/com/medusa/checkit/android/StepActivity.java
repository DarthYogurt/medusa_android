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
	
	private ArrayList<Step> mStepsArray;
	private Step mStep;
	private TextView mResult;
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
		mResult = (TextView) findViewById(R.id.result);
		
		mOrder.setText(Integer.toString(mStep.getOrder()));
		mName.setText(mStep.getName());
		
		if (mStep.getType().equalsIgnoreCase("bool")) { showBoolElements(); }
		if (mStep.getType().equalsIgnoreCase("double")) { showDoubleElements(); }
		if (mStep.getType().equalsIgnoreCase("text")) { showTextElements(); }
		
		if (mStepNum == 0) { showNextButton(); }
		if (mStepNum > 0 && mStepNum < mStepsArray.size() - 1) { showNextButton(); showPrevButton(); }
		if (mStepNum == mStepsArray.size() - 1) { showFinishedButton(); }
	}
	
	private void showBoolElements() {
		RelativeLayout mBoolContainer = (RelativeLayout) findViewById(R.id.bool_container);
		Button mButtonYes = (Button) findViewById(R.id.button_yes);
		Button mButtonNo = (Button) findViewById(R.id.button_no);
		mBoolContainer.setVisibility(View.VISIBLE);
		
		mButtonYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mResult.setText("Yes");
				mStep.setYesOrNo(true);
			}
		});
		
		mButtonNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mResult.setText("No");
				mStep.setYesOrNo(false);
			}
		});
	}
	
	private void showDoubleElements() {
		LinearLayout mDoubleContainer = (LinearLayout) findViewById(R.id.double_container);
		final EditText mDoubleInput = (EditText) findViewById(R.id.double_input);
		Button mButtonSubmit = (Button) findViewById(R.id.btn_submit_double);
		mDoubleContainer.setVisibility(View.VISIBLE);
		
		mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mDoubleInput.getText().toString();
				mResult.setText(input);
				mStep.setValue(Double.parseDouble(input));
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout mTextContainer = (LinearLayout) findViewById(R.id.text_container);
		final EditText mTextInput = (EditText) findViewById(R.id.text_input);
		Button mButtonSubmit = (Button) findViewById(R.id.btn_submit_text);
		mTextContainer.setVisibility(View.VISIBLE);
		
		mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mTextInput.getText().toString();
				mResult.setText(input);
				mStep.setText(input);
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
