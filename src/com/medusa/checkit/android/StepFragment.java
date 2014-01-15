package com.medusa.checkit.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StepFragment extends Fragment {
	
	private TextView mOrder;
	private TextView mName;
	private Button mButtonYes;
	private Button mButtonNo;
	private Step mStep;
	
	static StepFragment newInstance(int position, Step step) {
		StepFragment fragment = new StepFragment();
		fragment.setStep(step);
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_step, container, false);
		
		mOrder = (TextView) view.findViewById(R.id.step_order);
		mName = (TextView) view.findViewById(R.id.step_name);
		mButtonYes = (Button) view.findViewById(R.id.button_yes);
		mButtonNo = (Button) view.findViewById(R.id.button_no);
		
		setOrder();
		setName();
		
		if (mStep.getType().equalsIgnoreCase("bool")) {
			showYesNoButtons();
		}
		
		
		
        return view;
    }
	
	private void setStep(Step step) {
		mStep = step;
	}
	
	private void setOrder() {
		mOrder.setText(Integer.toString(mStep.getOrder()));
	}
	
	private void setName() {
		mName.setText(mStep.getName());
	}
	
	private void showYesNoButtons() {
		mButtonYes.setVisibility(View.VISIBLE);
		mButtonNo.setVisibility(View.VISIBLE);
	}
	
}
