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

public class DrawerListViewAdapter extends ArrayAdapter<Step> {

	private Context context;
	private int layoutResourceId;
	private ArrayList<Step> steps;
	
	public DrawerListViewAdapter(Context context, int layoutResourceId, ArrayList<Step> steps) {
		super(context, layoutResourceId, steps);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.steps = steps;
	}
	
	private static class ViewHolder {
		private TextView stepOrder;
		private TextView stepName;
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
            holder.finishedStepImage = (ImageView) convertView.findViewById(R.id.finished_step_img);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

		String stepOrder = Integer.toString(steps.get(position).getOrder()); 
		String stepName = steps.get(position).getName();
		
		holder.stepOrder.setText(stepOrder);
		holder.stepName.setText(stepName);
        
        if (steps.get(position).getIsStepFinished()) { holder.finishedStepImage.setVisibility(View.VISIBLE); }

        return convertView;
	}
	
}
