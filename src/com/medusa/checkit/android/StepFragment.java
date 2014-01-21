package com.medusa.checkit.android;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Fragment;
import android.os.Bundle;
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
	private TextView result;
	private Step step;
	
	static StepFragment newInstance(int position, Step step) {
		StepFragment fragment = new StepFragment();
		fragment.setStep(step);
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_step, container, false);
		
		TextView order = (TextView) view.findViewById(R.id.step_order);
		TextView name = (TextView) view.findViewById(R.id.step_name);
		result = (TextView) view.findViewById(R.id.result);
		Button btnFinishedChecklist = (Button) view.findViewById(R.id.btn_finish_checklist);

		order.setText(Integer.toString(step.getOrder()));
		name.setText(step.getName());
		
		if (step.getType().equalsIgnoreCase("bool")) { showBoolElements(); }
		if (step.getType().equalsIgnoreCase("double")) { showDoubleElements(); }
		if (step.getType().equalsIgnoreCase("text")) { showTextElements(); }
		
		btnFinishedChecklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		
        return view;
    }
	
	private void setStep(Step s) {
		step = s;
	}
	
	private void showBoolElements() {
		RelativeLayout boolContainer = (RelativeLayout) view.findViewById(R.id.bool_container);
		Button btnYes = (Button) view.findViewById(R.id.button_yes);
		Button btnNo = (Button) view.findViewById(R.id.button_no);
		boolContainer.setVisibility(View.VISIBLE);
		
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				result.setText("Yes");
				step.setYesOrNo(true);
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				result.setText("No");
				step.setYesOrNo(false);
			}
		});
	}
	
	private void showDoubleElements() {
		LinearLayout doubleContainer = (LinearLayout) view.findViewById(R.id.double_container);
		final EditText doubleInput = (EditText) view.findViewById(R.id.double_input);
		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_double);
		doubleContainer.setVisibility(View.VISIBLE);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = doubleInput.getText().toString();
				result.setText(input);
				step.setValue(Double.parseDouble(input));
			}
		});
	}
	
	private void showTextElements() {
		LinearLayout textContainer = (LinearLayout) view.findViewById(R.id.text_container);
		final EditText textInput = (EditText) view.findViewById(R.id.text_input);
		Button btnSubmit = (Button) view.findViewById(R.id.btn_submit_text);
		textContainer.setVisibility(View.VISIBLE);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = textInput.getText().toString();
				result.setText(input);
				step.setText(input);
			}
		});
	}
	
}
