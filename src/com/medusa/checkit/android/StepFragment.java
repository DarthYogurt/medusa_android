package com.medusa.checkit.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StepFragment extends Fragment {
	
	private View view;
	private TextView mOrder;
	private TextView mName;
	private TextView mResult;
	private Step mStep;
	
	static StepFragment newInstance(int position, Step step) {
		StepFragment fragment = new StepFragment();
		fragment.setStep(step);
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		
		mOrder = (TextView) view.findViewById(R.id.step_order);
		mName = (TextView) view.findViewById(R.id.step_name);
		mResult = (TextView) view.findViewById(R.id.result);

		mOrder.setText(Integer.toString(mStep.getOrder()));
		mName.setText(mStep.getName());
		
		if (mStep.getType().equalsIgnoreCase("bool")) { showBoolElements(); }
		if (mStep.getType().equalsIgnoreCase("double")) { showDoubleElements(); }
		if (mStep.getType().equalsIgnoreCase("text")) { showTextElements(); }
		
        return view;
    }
	
	private void setStep(Step step) {
		mStep = step;
	}
	
	private void showBoolElements() {
		RelativeLayout mBoolContainer = (RelativeLayout) view.findViewById(R.id.bool_container);
		Button mButtonYes = (Button) view.findViewById(R.id.button_yes);
		Button mButtonNo = (Button) view.findViewById(R.id.button_no);
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
		LinearLayout mDoubleContainer = (LinearLayout) view.findViewById(R.id.double_container);
		final EditText mDoubleInput = (EditText) view.findViewById(R.id.double_input);
		Button mButtonSubmitDouble = (Button) view.findViewById(R.id.btn_submit_double);
		mDoubleContainer.setVisibility(View.VISIBLE);
		
		mButtonSubmitDouble.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mDoubleInput.getText().toString();
				mResult.setText(input);
				mStep.setText(input);
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout mTextContainer = (LinearLayout) view.findViewById(R.id.text_container);
		final EditText mTextInput = (EditText) view.findViewById(R.id.text_input);
		Button mButtonSubmitText = (Button) view.findViewById(R.id.btn_submit_text);
		mTextContainer.setVisibility(View.VISIBLE);
		
		mButtonSubmitText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mTextInput.getText().toString();
				mResult.setText(input);
				mStep.setText(input);
			}
		});
	}
}
