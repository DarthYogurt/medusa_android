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
		if (mStep.getType().equalsIgnoreCase("text")) { showTextElements(); }
		
        return view;
    }
	
	private void setStep(Step step) {
		mStep = step;
	}
	
	private void showBoolElements() {
		RelativeLayout mButtonContainer = (RelativeLayout) view.findViewById(R.id.button_container);
		Button mButtonYes = (Button) view.findViewById(R.id.button_yes);
		Button mButtonNo = (Button) view.findViewById(R.id.button_no);
		mButtonContainer.setVisibility(View.VISIBLE);
		
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
	
	private void showTextElements() {
		LinearLayout mEditTextContainer = (LinearLayout) view.findViewById(R.id.edit_text_container);
		final EditText mEditTextInput = (EditText) view.findViewById(R.id.edit_text_input);
		Button mButtonSubmit = (Button) view.findViewById(R.id.btn_submit);
		mEditTextContainer.setVisibility(View.VISIBLE);
		
		mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String text = mEditTextInput.getText().toString();
				mResult.setText(text);
				mStep.setText(text);
			}
		});
	}
}
