//package com.medusa.checkit.android;
//
//import java.util.ArrayList;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.Menu;
//
//public class StepsFragmentActivity extends FragmentActivity {
//
//	private ViewPager pager;
//	private PagerAdapter pagerAdapter;
//	private ArrayList<Step> stepsArray;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_steps_fragment);
//		
//		stepsArray = getIntent().getParcelableArrayListExtra("steps");
//		
//		pager = (ViewPager)findViewById(R.id.steps_pager);
//		pagerAdapter = new StepsPagerAdapter(getSupportFragmentManager());
//        pager.setAdapter(pagerAdapter);
//		
//	}
//	
//	private class StepsPagerAdapter extends FragmentStatePagerAdapter {
//        public StepsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//        
//        @Override
//      public Fragment getItem(int position) {
//        	Step step = stepsArray.get(position);
//        	return StepFragment.newInstance(position, step);
//      }
//
//        @Override
//        public int getCount() {
//            return stepsArray.size();
//        }
//    }
//
////	@Override
////	public boolean onCreateOptionsMenu(Menu menu) {
////		// Inflate the menu; this adds items to the action bar if it is present.
////		getMenuInflater().inflate(R.menu.steps, menu);
////		return true;
////	}
//
//}
