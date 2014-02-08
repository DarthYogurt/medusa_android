package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotifyPersonAdapter extends ArrayAdapter<User> {
	
	private Context context;
	private int layoutResourceId;
	private ArrayList<User> users;
	
	public NotifyPersonAdapter(Context context, int layoutResourceId, ArrayList<User> users) {
		super(context, layoutResourceId, users);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.users = users;
	}
	
	private static class ViewHolder {
		private TextView name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.notify_name);

            convertView.setTag(holder);
        } 
		else {
            holder = (ViewHolder)convertView.getTag();
        }

		String name = users.get(position).getName();
		
        holder.name.setText(name);

        return convertView;
	}
}
