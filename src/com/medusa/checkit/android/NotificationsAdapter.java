package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotificationsAdapter extends ArrayAdapter<Notification> {
	
	private Context context;
	private int layoutResourceId;
	private ArrayList<Notification> notifications;
	
	public NotificationsAdapter(Context context, int layoutResourceId, ArrayList<Notification> notifications) {
		super(context, layoutResourceId, notifications);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.notifications = notifications;
	}
	
	private static class ViewHolder {
		private TextView notifyName;
		private TextView checklist;
		private TextView stepName;
		private TextView note;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.notifyName = (TextView)convertView.findViewById(R.id.notify_name);
            holder.checklist = (TextView)convertView.findViewById(R.id.checklist_name);
            holder.stepName = (TextView)convertView.findViewById(R.id.step_name);
            holder.note = (TextView)convertView.findViewById(R.id.notification_note);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }

		String notifyName = notifications.get(position).getNotifyName();
		String checklist = notifications.get(position).getChecklist();
		String stepName = notifications.get(position).getStepName();
		String note = notifications.get(position).getNote();
		
        holder.notifyName.setText(notifyName);
        holder.checklist.setText(checklist);
        holder.stepName.setText(stepName);
        holder.note.setText(note);
        
        return convertView;
	}
}
