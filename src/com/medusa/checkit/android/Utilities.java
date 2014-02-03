package com.medusa.checkit.android;

import java.io.File;

import android.content.Context;
import android.util.Log;

public class Utilities {
	
	public static void deleteFile(Context context, String filename) {
		File file = new File(context.getFilesDir(), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.v("FILE DELETED", filename); }
	}
	
}
