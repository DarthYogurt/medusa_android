package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StepAdapter extends ArrayAdapter<Step> {
	
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_DOUBLE = "double";
	private static final String TYPE_TEXT = "text";

	private Context context;
	private int layoutResourceId;
	private ArrayList<Step> steps;
	
	public StepAdapter(Context context, int layoutResourceId, ArrayList<Step> steps) {
		super(context, layoutResourceId, steps);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.steps = steps;
	}
	
	private static class ViewHolder {
		private TextView stepOrder;
		private TextView result;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.stepOrder = (TextView)convertView.findViewById(R.id.step_order);
            holder.result = (TextView)convertView.findViewById(R.id.result);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

		String stepOrder = Integer.toString(steps.get(position).getOrder()); 
		String result = null;
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_BOOL)) {
			if (steps.get(position).getYesOrNo()) { result = "Yes"; }
			else { result = "No"; }
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_DOUBLE)) {
			result = Double.toString(steps.get(position).getValue());
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_TEXT)) {
			result = steps.get(position).getText();
		}
		
		holder.stepOrder.setText(stepOrder);
        holder.result.setText(result);

        return convertView;
	}
}
