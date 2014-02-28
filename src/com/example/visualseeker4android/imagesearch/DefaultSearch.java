package com.example.visualseeker4android.imagesearch;

import com.example.visualseeker4android.R;
import com.example.visualseeker4android.R.id;
import com.example.visualseeker4android.R.layout;
import com.example.visualseeker4android.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

public class DefaultSearch extends Activity {
	
	
	Fragment searchResult = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default_search);

		searchResult = getFragmentManager().findFragmentById(R.id.search_result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.default_search, menu);
		searchResult.onCreateOptionsMenu(menu, getMenuInflater());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if( searchResult.onOptionsItemSelected(item) )return true;
		
		return super.onOptionsItemSelected(item);
	}
	
	

}
