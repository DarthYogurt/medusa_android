package com.medusa.checkit.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepFragment extends Fragment {
	
	private TextView name;
	
	static StepFragment newInstance(int position) {
		StepFragment fragment = new StepFragment();
		Bundle args = new Bundle();
//		
//		args.putInt(position, position);
//		fragment.setArguments(args);
		
		return(fragment);
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_step, container, false);
		
		name = (TextView) view.findViewById(R.id.checklist_name);
		
        // Inflate the layout for this fragment
        return view;
    }
}
