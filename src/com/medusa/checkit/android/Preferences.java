package com.medusa.checkit.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Preferences {
	
	private static final String FILENAME_PREF = "preferences";
	private static final String KEY_UNSENT_NOTIFICATIONS = "unsentNotifications";
	
	private SharedPreferences sharedPref;
	private Editor prefEditor;
	
	public Preferences(Context context) {
		this.sharedPref = context.getSharedPreferences(FILENAME_PREF, Context.MODE_PRIVATE);
		this.prefEditor = sharedPref.edit();
	}
	
	public ArrayList<String> getUnsentNotifications() {
		Set<String> set = new HashSet<String>();
		set = sharedPref.getStringSet(KEY_UNSENT_NOTIFICATIONS, new HashSet<String>());
		ArrayList<String> arrayList = new ArrayList<String>(set);
		return arrayList;
	}
	
	public void setUnsentNotifications(ArrayList<String> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			String id = arrayList.get(i);
			Log.i("UNSENT NOTIFICATION", id);
		}
		
		Set<String> set = new HashSet<String>();
		set.addAll(arrayList);
		prefEditor.putStringSet(KEY_UNSENT_NOTIFICATIONS, set);
		prefEditor.commit();
		Log.i("UNSENT NOTIFICATIONS LIST", "SAVED IN PREFERENCES");
	}
	
	public void clearUnsentNotifications() {
		Set<String> set = new HashSet<String>();
		ArrayList<String> clear = new ArrayList<String>();
		set.addAll(clear);
		prefEditor.putStringSet(KEY_UNSENT_NOTIFICATIONS, set);
		prefEditor.commit();
		Log.i("UNSENT NOTIFICATIONS LIST", "CLEARED");
	}

}
