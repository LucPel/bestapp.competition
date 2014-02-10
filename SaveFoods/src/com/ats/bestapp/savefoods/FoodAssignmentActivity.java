package com.ats.bestapp.savefoods;

import com.ats.bestapp.savefoods.R.id;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.parse.Parse;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
		Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    food=(Food) getIntent().getSerializableExtra("food");
	    Log.d(logTag, "Stato "+food.getStatus());
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
		statusSpinner.setOnItemSelectedListener(new FoodStatusSpinnerOnItemClickListener(food.getFoodId()));
	}
	
	private int statusSpinnerPosition(String status){
		int status_int=0;
		if(status.equalsIgnoreCase(Constants.foodStatusDisponibile)) status_int=0;
		else if(status.equalsIgnoreCase(Constants.foodStatusInAssegnazione))status_int=1;
		else if(status.equalsIgnoreCase(Constants.foodStatusAssegnato))status_int=2;
		else if(status.equalsIgnoreCase(Constants.foodStatusScaduto))status_int=3;
		return status_int;
	}
}
