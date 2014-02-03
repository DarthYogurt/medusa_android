package com.medusa.checkit.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {
	
	private static final String FILENAME_PREF = "preferences";
	private static final String KEY_UNPOSTED_JSON = "unPostedJson";
	private static final String KEY_UNPOSTED_IMAGES = "unPostedImages";
	
	private SharedPreferences sharedPref;
	private Editor prefEditor;
	
	public Preferences(Context context) {
		this.sharedPref = context.getSharedPreferences(FILENAME_PREF, Activity.MODE_PRIVATE);
		this.prefEditor = sharedPref.edit();
	}
	
	public String getUnPostedJson() {
		return sharedPref.getString(KEY_UNPOSTED_JSON, "");
	}
	
	public void saveUnPostedJson(String filename) {
		prefEditor.putString(KEY_UNPOSTED_JSON, filename);
		prefEditor.commit();
	}


}
