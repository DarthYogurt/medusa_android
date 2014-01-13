package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainMenuActivity extends Activity {
	
	Intent newChecklistIntent;
	String allChecklistsJSONString;
	ArrayList<String[]> checklistsArray;
	ArrayList<String[]> stepsArray;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		checklistsArray = (ArrayList<String[]>) this.getIntent().getSerializableExtra("checklists");
        stepsArray = (ArrayList<String[]>) this.getIntent().getSerializableExtra("steps");
        
//		newChecklistIntent = new Intent(this, NewChecklistActivity.class);
//		newChecklistIntent.putExtra("checklists", checklistsArray);
//		newChecklistIntent.putExtra("steps", stepsArray);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
