package com.medusa.checkit.android;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utilities {
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static void deleteFile(Context context, String filename) {
		File file = new File(context.getFilesDir(), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.v("FILE DELETED", filename); }
	}
	
}
