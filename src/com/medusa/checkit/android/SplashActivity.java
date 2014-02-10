package com.medusa.checkit.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// Removes title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				
		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		setContentView(R.layout.activity_splash);
		
		new ShowLogo().execute();
	}
	
	private class ShowLogo extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	try { Thread.sleep(1000); } 
			catch (Exception e) { e.printStackTrace(); }
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	Intent intent = new Intent(SplashActivity.this, SelectChecklistActivity.class);
	    	startActivity(intent);
			finish();
	        return;
	    }
	}
	
}
