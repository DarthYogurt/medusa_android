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
	
	private ArrayList<Step> stepsArray;
	private Step step;
	private int stepNum;
	private TextView result;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step);
		
		// TODO: change to parcelable instead of serializable
		stepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
		stepNum = getIntent().getIntExtra("stepNum", 0);
		step = stepsArray.get(stepNum);
		
		TextView order = (TextView) findViewById(R.id.step_order);
		TextView orderMax = (TextView) findViewById(R.id.step_order_max);
		TextView name = (TextView) findViewById(R.id.step_name);
		result = (TextView) findViewById(R.id.result);
		
		order.setText(Integer.toString(step.getOrder()));
		orderMax.setText(Integer.toString(stepsArray.size()));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase(TYPE_BOOL)) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_DOUBLE)) { showDoubleElements(); }
		if (step.getType().equalsIgnoreCase(TYPE_TEXT)) { showTextElements(); }
		
		showNextButton();
		if (stepNum > 0 && stepNum < stepsArray.size() - 1) { showPrevButton(); }
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
		RelativeLayout boolContainer = (RelativeLayout) findViewById(R.id.bool_container);
		Button btnYes = (Button) findViewById(R.id.button_yes);
		Button btnNo = (Button) findViewById(R.id.button_no);
		boolContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(true);
				step.setIsStepFinished(true);
				showResult();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				step.setYesOrNo(false);
				step.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showDoubleElements() {
		LinearLayout doubleContainer = (LinearLayout) findViewById(R.id.double_container);
		final EditText doubleInput = (EditText) findViewById(R.id.double_input);
		Button btnSubmit = (Button) findViewById(R.id.btn_submit_double);
		doubleContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = doubleInput.getText().toString();
				step.setValue(Double.parseDouble(input));
				step.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout textContainer = (LinearLayout) findViewById(R.id.text_container);
		final EditText textInput = (EditText) findViewById(R.id.text_input);
		Button btnSubmit = (Button) findViewById(R.id.btn_submit_text);
		textContainer.setVisibility(View.VISIBLE);
		showResult();
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = textInput.getText().toString();
				step.setText(input);
				step.setIsStepFinished(true);
				showResult();
			}
		});
	}
	
	private void showNextButton() {
		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
		btnNext.setVisibility(View.VISIBLE);
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (stepNum == stepsArray.size() - 1) {
					Intent intent = new Intent(getApplicationContext(), FinishChecklistActivity.class);
					intent.putExtra("steps", stepsArray);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(getApplicationContext(), StepActivity.class);
					intent.putExtra("steps", stepsArray);
					intent.putExtra("stepNum", stepNum + 1);
					startActivity(intent);
					finish();
				}	
			}
		});
	}
	
	private void showPrevButton() {
		ImageButton btnPrev = (ImageButton) findViewById(R.id.btn_prev);
		btnPrev.setVisibility(View.VISIBLE);
		btnPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				intent.putExtra("steps", stepsArray);
				intent.putExtra("stepNum", stepNum - 1);
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
