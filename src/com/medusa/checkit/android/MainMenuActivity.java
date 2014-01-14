package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenuActivity extends Activity {
	
	Intent newChecklistIntent;
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		checklistsArray = (ArrayList<Checklist>) getIntent().getSerializableExtra("checklists");
        stepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
        
        for (int i = 0; i < checklistsArray.size(); i++) {
        	Checklist holder = checklistsArray.get(i);
        	Log.v("in main activity", holder.getName());
        }
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
//		newChecklistIntent = new Intent(this, NewChecklistActivity.class);
//		newChecklistIntent.putExtra("checklists", checklistsArray);
//		newChecklistIntent.putExtra("steps", stepsArray);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main_menu, menu);
//		return true;
//	}

}
