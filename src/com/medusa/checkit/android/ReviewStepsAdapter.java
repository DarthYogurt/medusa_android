package com.medusa.checkit.android;

import java.io.File;
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
		private LinearLayout resultImageContainer;
		private ImageView resultImageView;
		private LinearLayout extraNoteContainer;
		private TextView extraNoteTextView;
		private LinearLayout extraImageContainer;
		private ImageView extraImageView;
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
            holder.resultImageContainer = (LinearLayout) convertView.findViewById(R.id.result_image_container);
            holder.resultImageView = (ImageView) convertView.findViewById(R.id.result_image);
            holder.extraNoteContainer = (LinearLayout) convertView.findViewById(R.id.extra_note_container);
            holder.extraNoteTextView = (TextView) convertView.findViewById(R.id.extra_note_text);
            holder.extraImageContainer = (LinearLayout) convertView.findViewById(R.id.extra_image_container);
            holder.extraImageView = (ImageView) convertView.findViewById(R.id.extra_image);
            holder.finishedStepImageView = (ImageView) convertView.findViewById(R.id.finished_step_img);

            convertView.setTag(holder);
        } 
		else { holder = (ViewHolder)convertView.getTag(); }

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
			if (!steps.get(position).getImageFilename().isEmpty()) {
				holder.resultImageContainer.setVisibility(View.VISIBLE);
				try {
					File file = new File(context.getExternalFilesDir(null), steps.get(position).getImageFilename());
					FileInputStream fis = new FileInputStream(file);
					Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
					fis.close();
					holder.resultImageView.setImageBitmap(imgFromFile);
					holder.resultImageView.invalidate();
				} 
		    	catch (FileNotFoundException e) { e.printStackTrace(); } 
		    	catch (IOException e) { e.printStackTrace(); }
			}
			else { holder.resultImageContainer.setVisibility(View.GONE); }
		}
		
		if (!steps.get(position).getExtraNote().isEmpty()) {
			holder.extraNoteContainer.setVisibility(View.VISIBLE);
			holder.extraNoteTextView.setText(steps.get(position).getExtraNote());
		}
		else { holder.extraNoteContainer.setVisibility(View.GONE); }
		
		if (!steps.get(position).getExtraImageFilename().isEmpty()) {
			holder.extraImageContainer.setVisibility(View.VISIBLE);
			try {
				File file = new File(context.getExternalFilesDir(null), steps.get(position).getExtraImageFilename());
				FileInputStream fis = new FileInputStream(file);
				Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
				fis.close();
				holder.extraImageView.setImageBitmap(imgFromFile);
				holder.extraImageView.invalidate();
			} 
	    	catch (FileNotFoundException e) { e.printStackTrace(); } 
	    	catch (IOException e) { e.printStackTrace(); }
		}
		else { holder.extraImageContainer.setVisibility(View.GONE); }

        if (steps.get(position).getIsAllFinished()) { holder.finishedStepImageView.setVisibility(View.VISIBLE); }
        else { holder.finishedStepImageView.setVisibility(View.GONE); }
        
        return convertView;
	}
}
