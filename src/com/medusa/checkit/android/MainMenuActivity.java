package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuActivity extends Activity {
	
	private final static String KEY_CHECKLIST_ID = "Checklist Id";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		final ArrayList<Checklist> checklistsArray = (ArrayList<Checklist>) getIntent().getSerializableExtra("checklists");
		ArrayList<Step> stepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Checklist checklist = checklistsArray.get(position);
				Intent intent = new Intent(getApplicationContext(), StepsFragmentActivity.class);
				intent.putExtra(KEY_CHECKLIST_ID, checklist.getId());
				startActivity(intent);
			}
        });
        
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
