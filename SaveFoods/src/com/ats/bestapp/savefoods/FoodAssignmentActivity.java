package com.ats.bestapp.savefoods;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.ats.bestapp.savefoods.R.id;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.parse.Parse;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FoodAssignmentActivity extends Activity{
	
	private Food food;
	private String logTag="FoodAssignmentActivity";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_assigment);
		init();
	    Log.d(logTag, JsonMapper.convertObject2String(food));
	    TextView name_food=(TextView) findViewById(R.id.food_name_label);
	    name_food.setText(food.getName()+" scade il "+food.getDueDate());
	    TextView id_food=(TextView) findViewById(R.id.food_id_label);
	    id_food.setText(food.getFoodId());
		ImageView imageView = (ImageView) findViewById(R.id.food_image);
			if(food.getImages()!=null && food.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(food.getImages().get(0).getImage());
				if(image!=null){
					imageView.setImageBitmap(Bitmap.createScaledBitmap(image, 150, 150, false));
				}
				else{
					imageView.setImageResource(R.drawable.logo_launcher);
				}
			}
			else{
				imageView.setImageResource(R.drawable.logo_launcher);
			}
			
		Spinner statusSpinner=(Spinner)findViewById(R.id.food_status_spinner);
		statusSpinner.setSelection(statusSpinnerPosition(food.getStatus()));
		Log.d(logTag, "Stato "+food.getStatus()+ "Position "+statusSpinnerPosition(food.getStatus()));
		statusSpinner.setOnItemSelectedListener(new FoodStatusSpinnerOnItemClickListener(food));
	}
	
	private int statusSpinnerPosition(String status){
		int status_int=0;
		if(status.equalsIgnoreCase(Constants.foodStatusDisponibile)) status_int=0;
		else if(status.equalsIgnoreCase(Constants.foodStatusInAssegnazione))status_int=1;
		else if(status.equalsIgnoreCase(Constants.foodStatusAssegnato))status_int=2;
		else if(status.equalsIgnoreCase(Constants.foodStatusScaduto))status_int=3;
		return status_int;
	}
	
	private void init(){
		Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);

	}
	
	public void onPause(){
		super.onPause();
		//Log.d(logTag, "Pause: "+food.getStatus());
	}
	
	public void onStop(){
		Log.d(logTag, "Stop: "+food.getStatus());
		setResultActivity();
		super.onStop();
		
	}
	
	public void onDestroy(){
		super.onDestroy();
		//Log.d(logTag, "Destroy: "+food.getStatus());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResultActivity();
	        	onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void setResultActivity(){
		Intent intent = new Intent();
		intent.putExtra(Constants.foodDetailSP, food);
		setResult(RESULT_OK, intent);
	}
}
