package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		private TextView stepName;
		private TextView result;
		private ImageView finishedStepImage;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.stepOrder = (TextView) convertView.findViewById(R.id.step_order);
            holder.stepName = (TextView) convertView.findViewById(R.id.step_name);
            holder.result = (TextView) convertView.findViewById(R.id.result);
            holder.finishedStepImage = (ImageView) convertView.findViewById(R.id.finished_step_img);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

		String stepOrder = Integer.toString(steps.get(position).getOrder()); 
		String stepName = steps.get(position).getName();
		String result = null;
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_BOOL)) {
			if (steps.get(position).getYesOrNo()) { result = "Yes"; }
			else { result = "No"; }
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_DOUBLE)) {
			result = Double.toString(steps.get(position).getNumber());
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_TEXT)) {
			result = steps.get(position).getText();
		}
		
		holder.stepOrder.setText(stepOrder);
		holder.stepName.setText(stepName);
        holder.result.setText(result);
        
        if (steps.get(position).getIsStepFinished()) { holder.finishedStepImage.setVisibility(View.VISIBLE); }
        else { holder.finishedStepImage.setVisibility(View.GONE); }

        return convertView;
	}
}
