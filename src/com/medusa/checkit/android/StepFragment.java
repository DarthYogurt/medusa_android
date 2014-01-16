package com.medusa.checkit.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StepFragment extends Fragment {
	
	private TextView mOrder;
	private TextView mName;
	private TextView mResult;
	private RelativeLayout mButtonContainer;
	private LinearLayout mEditTextContainer;
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
		mResult = (TextView) view.findViewById(R.id.result);
		mButtonContainer = (RelativeLayout) view.findViewById(R.id.button_container);
		mEditTextContainer = (LinearLayout) view.findViewById(R.id.edit_text_container);
		mButtonYes = (Button) view.findViewById(R.id.button_yes);
		mButtonNo = (Button) view.findViewById(R.id.button_no);
		
		mOrder.setText(Integer.toString(mStep.getOrder()));
		mName.setText(mStep.getName());
		
		if (mStep.getType().equalsIgnoreCase("bool")) { 
			mButtonContainer.setVisibility(View.VISIBLE);
		}
		if (mStep.getType().equalsIgnoreCase("text")) { 
			mEditTextContainer.setVisibility(View.VISIBLE);
		}
		
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
		
        return view;
    }
	
	private void setStep(Step step) {
		mStep = step;
	}
	
}
