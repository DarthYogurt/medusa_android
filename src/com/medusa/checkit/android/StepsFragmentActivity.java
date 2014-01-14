package com.medusa.checkit.android;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

public class StepsFragmentActivity extends FragmentActivity {

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steps_fragment);
		
		mPager = (ViewPager)findViewById(R.id.steps_pager);
        mPagerAdapter = new StepsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        @SuppressWarnings("unchecked")
		ArrayList<Step> stepsArray = (ArrayList<Step>) getIntent().getSerializableExtra("steps");
        
        for (int i = 0; i < stepsArray.size(); i++) {
        	Step step = stepsArray.get(i);
        	Log.v("this checklist steps", step.getName());
        }
		
	}
	
	private class StepsPagerAdapter extends FragmentStatePagerAdapter {
        public StepsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StepFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.steps, menu);
//		return true;
//	}

}
