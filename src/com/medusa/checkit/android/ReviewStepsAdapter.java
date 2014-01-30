package com.medusa.checkit.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReviewStepsAdapter extends ArrayAdapter<Step> {
	
	private static final String TYPE_BOOL = "bool";
	private static final String TYPE_NUMBER = "number";
	private static final String TYPE_TEXT = "text";
	private static final String TYPE_IMAGE = "image";

	private Context context;
	private int layoutResourceId;
	private ArrayList<Step> steps;
	
	public ReviewStepsAdapter(Context context, int layoutResourceId, ArrayList<Step> steps) {
		super(context, layoutResourceId, steps);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.steps = steps;
	}
	
	private static class ViewHolder {
		private TextView stepOrderView;
		private TextView stepNameView;
		private TextView resultTextView;
		private ImageView resultImageView;
		private LinearLayout notesContainer;
		private TextView notesTextView;
		private ImageView finishedStepImageView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.stepOrderView = (TextView) convertView.findViewById(R.id.step_order);
            holder.stepNameView = (TextView) convertView.findViewById(R.id.step_name);
            holder.resultTextView = (TextView) convertView.findViewById(R.id.result_text);
            holder.resultImageView = (ImageView) convertView.findViewById(R.id.result_image);
            holder.notesContainer = (LinearLayout) convertView.findViewById(R.id.notes_container);
            holder.notesTextView = (TextView) convertView.findViewById(R.id.notes_text);
            holder.finishedStepImageView = (ImageView) convertView.findViewById(R.id.finished_step_img);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

		String stepOrder = Integer.toString(steps.get(position).getOrder()); 
		holder.stepOrderView.setText(stepOrder);
		
		String stepName = steps.get(position).getName();
		holder.stepNameView.setText(stepName);
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_BOOL)) {
			String result = "";
			if (steps.get(position).getIsStepFinished()) {
				if (steps.get(position).getYesOrNo()) { result = "Yes"; }
				else { result = "No"; }
			}
			holder.resultTextView.setVisibility(View.VISIBLE);
			holder.resultTextView.setText(result);
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_NUMBER)) {
			String result = "";
			if (steps.get(position).getIsStepFinished()) {
				result = Double.toString(steps.get(position).getNumber());
			}
			holder.resultTextView.setVisibility(View.VISIBLE);
			holder.resultTextView.setText(result);
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_TEXT)) {
			String result = "";
			if (steps.get(position).getIsStepFinished()) {
				result = steps.get(position).getText();
			}
			holder.resultTextView.setVisibility(View.VISIBLE);
			holder.resultTextView.setText(result);
		}
		
		if (steps.get(position).getType().equalsIgnoreCase(TYPE_IMAGE)) {
			holder.resultImageView.setVisibility(View.VISIBLE);
			try {
				FileInputStream fis = context.openFileInput(steps.get(position).getImageFilename());
				Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
				fis.close();
				holder.resultImageView.setImageBitmap(imgFromFile);
				holder.resultImageView.invalidate();
			} 
	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
	    	catch (IOException e) { e.printStackTrace(); }
		}
		
		if (!steps.get(position).getExtraNote().isEmpty()) {
			holder.notesContainer.setVisibility(View.VISIBLE);
			holder.notesTextView.setText(steps.get(position).getExtraNote());
		}
		else { holder.notesContainer.setVisibility(View.GONE); }

        if (steps.get(position).getIsStepFinished()) { holder.finishedStepImageView.setVisibility(View.VISIBLE); }
        else { holder.finishedStepImageView.setVisibility(View.GONE); }
        
        return convertView;
	}
}
