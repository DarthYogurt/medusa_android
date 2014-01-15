package com.medusa.checkit.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepFragment extends Fragment {
	
	private TextView name;
	private Step mStep;
	
	static StepFragment newInstance(int position, Step step) {
		StepFragment fragment = new StepFragment();
		fragment.setStep(step);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("step", step);
//		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_step, container, false);
		
		name = (TextView) view.findViewById(R.id.step_name);
		setName();
        return view;
    }
	
	private void setName() {
		name.setText(mStep.getName());
	}
	
	private void setStep(Step step) {
		mStep = step;
	}
}
