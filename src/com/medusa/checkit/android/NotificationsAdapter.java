package com.medusa.checkit.android;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		private LinearLayout noteContainer;
		private TextView note;
		private ImageView image;
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
            holder.noteContainer = (LinearLayout)convertView.findViewById(R.id.notification_note_container);
            holder.note = (TextView)convertView.findViewById(R.id.notification_note);
            holder.image = (ImageView)convertView.findViewById(R.id.notification_image);

            convertView.setTag(holder);
        } 
		else { holder = (ViewHolder)convertView.getTag(); }

		String notifyName = notifications.get(position).getNotifyName();
		String checklist = notifications.get(position).getChecklist();
		String stepName = notifications.get(position).getStepName();
		String note = notifications.get(position).getNote();
		String imgUrl = notifications.get(position).getImgUrl();
		
        holder.notifyName.setText(notifyName);
        holder.checklist.setText(checklist);
        holder.stepName.setText(stepName);
        
        if (!note.isEmpty()) {
        	holder.noteContainer.setVisibility(View.VISIBLE);
        	holder.note.setText(note);
        }
        else { holder.noteContainer.setVisibility(View.GONE); }
        
        if (!imgUrl.isEmpty()) { 
        	holder.image.setVisibility(View.VISIBLE);
        	UrlImageViewHelper.setUrlDrawable(holder.image, imgUrl, R.drawable.placeholder); 
    	}
        else { holder.image.setVisibility(View.GONE); }
        
        return convertView;
	}
}
