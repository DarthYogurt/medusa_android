package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChecklistAdapter extends ArrayAdapter<Checklist> {
	
	private Context context;
	private int layoutResourceId;
	private ArrayList<Checklist> checklists;
	
	public ChecklistAdapter(Context context, int layoutResourceId, ArrayList<Checklist> checklists) {
		super(context, layoutResourceId, checklists);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.checklists = checklists;
	}
	
	private static class ViewHolder {
		private TextView checklistName;
		private TextView numOfSteps;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.checklistName = (TextView)convertView.findViewById(R.id.checklist_name);
            holder.numOfSteps = (TextView)convertView.findViewById(R.id.num_of_steps);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }

		String name = checklists.get(position).getName();
		String numOfSteps = Integer.toString(checklists.get(position).getNumOfSteps());
		
        holder.checklistName.setText(name);
        holder.numOfSteps.setText(numOfSteps);

        return convertView;
	}
}
