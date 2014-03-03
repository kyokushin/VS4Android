package com.example.visualseeker4android.imagesearchactivity;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.R.id;
import com.example.visualseeker4android.R.layout;
import com.example.visualseeker4android.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainSearchActiviry extends FragmentActivity {
	FragmentPagerAdapter adapter;
	ViewPager pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default_search);

		adapter = new PagerAdapter(getSupportFragmentManager());
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.default_search, menu);
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		//if( searchResult.onOptionsItemSelected(item) )return true;
		
		int item_pos = pager.getCurrentItem();
		Fragment fragment = (Fragment)adapter.instantiateItem(pager, item_pos);
		fragment.onOptionsItemSelected(item);
		
		return super.onOptionsItemSelected(item);
	}
	
	class PagerAdapter extends FragmentPagerAdapter {
		//private final FragmentManager manager;
			
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			if( arg0 == 0 ){
				return new SearchResultFragment();
			}
			else if( arg0 == 1 ){
				return new QueryRankingFragment();
			}
			
			return null;
		}
	}

}
