package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuActivity extends Activity {
	
	static final String KEY_CHECKLIST_STEPS = "checklistSteps";
	static final String KEY_CURRENT_STEP = "currentStep";
	
	ArrayList<Checklist> checklistsArray;
	ArrayList<Step> stepsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		getActionBar().setTitle("");
		
		checklistsArray = getIntent().getParcelableArrayListExtra(SplashActivity.KEY_ALL_CHECKLISTS);
		stepsArray = getIntent().getParcelableArrayListExtra(SplashActivity.KEY_ALL_STEPS);
        
        ListView listView = (ListView)findViewById(R.id.checklist_listview);
        ChecklistAdapter adapter = new ChecklistAdapter(this, R.layout.listview_checklist_row, checklistsArray);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), StepActivity.class);
				Checklist checklist = checklistsArray.get(position);
				intent.putExtra(KEY_CHECKLIST_STEPS, getStepsForChecklist(checklist.getId()));
				intent.putExtra(KEY_CURRENT_STEP, 0);
				startActivity(intent);
				finish();
			}
        });
	}
	
	private ArrayList<Step> getStepsForChecklist(int checklistId) {
		ArrayList<Step> stepsForSelectedChecklist = new ArrayList<Step>();
		
		for (int i = 0; i < stepsArray.size(); i++) {
			Step step = stepsArray.get(i);
			if (checklistId == step.getChecklistId()) {
				stepsForSelectedChecklist.add(step);
			}
		}
		return stepsForSelectedChecklist;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main_menu, menu);
//		return true;
//	}

}
