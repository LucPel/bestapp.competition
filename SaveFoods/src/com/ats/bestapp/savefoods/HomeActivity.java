package com.ats.bestapp.savefoods;

import com.google.android.gms.plus.PlusClient;
import com.parse.Parse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp"); 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_addFood2Save:
	            openAddFoodActivity();
	            return true;
	        case R.id.action_searchFoods:
	            //composeMessage();
	            return true;
	        case R.id.action_addFoodRequest:
	            //composeMessage();
	            return true;
	        case R.id.action_settings:
	            //composeMessage();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void openAddFoodActivity(){
		Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        startActivity(intent);
	}
}
