package com.ats.bestapp.savefoods;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import com.ats.bestapp.savefoods.R.id;
import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.SavingFoodAssignment;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.utilities.Commons;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.parse.Parse;
import com.parse.ParseException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class FoodDetailsActivity extends Activity{
	
	private Food food;
	private String logTag="FoodDetailsActivity";
	private SharedPreferences settings;
	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private CommentTableAdapter commentTableAdapter;
	private GoogleMap map;
	
	
	/*static final LatLng KIEL = new LatLng(53.551, 9.993);*/

	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_details);
		init();
	    Log.d(logTag, JsonMapper.convertObject2String(food));
	    setViewComponents();
	    showMap();
		
	}
	
	private void setViewComponents(){
		TextView name_food=(TextView) findViewById(R.id.food_name_label);
	    name_food.setText(food.getName());
	    TextView dueDate_food=(TextView) findViewById(R.id.food_dueDate_label);
	    dueDate_food.setText(Commons.convertToDate(food.getDueDate()));
	    TextView category_food=(TextView) findViewById(R.id.food_category_label);
	    category_food.setText("("+food.getType()+")");
	    
	    String quantity=food.getQuantity();
		if(quantity==null) quantity="1";
	    TextView quantity_food=(TextView) findViewById(R.id.food_quantity_label);
	    quantity_food.setText(quantity);
	    
	    TextView id_food=(TextView) findViewById(R.id.food_id_label);
	    id_food.setText(food.getFoodId());
		ImageView imageView = (ImageView) findViewById(R.id.food_image);
			if(food.getImages()!=null && food.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(food.getImages().get(0).getImage());
				if(image!=null){
					imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_size, Constants.standard_image_size, false));
				}
				else{
					imageView.setImageResource(R.drawable.food_no_image_icon);
				}
			}
			else{
				imageView.setImageResource(R.drawable.food_no_image_icon);
			}
			
		Spinner statusSpinner=(Spinner)findViewById(R.id.food_status_spinner);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.foodStatus, R.layout.assignment_spinner_item);
		adapter.setDropDownViewResource(R.layout.assignment_spinner_dropdown_list);
		statusSpinner.setAdapter(adapter);
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
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
	    settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	}
	
	private void showMap(){
		LatLng food_point = new LatLng(food.getLatitude(), food.getLongitude());
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		Marker marker = map.addMarker(new MarkerOptions().position(food_point).title(food.getName()));
		// Move the camera instantly to hamburg with a zoom of 15.
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(food_point, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);	
	}
	
	public void onPause(){
		super.onPause();
		//Log.d(logTag, "Pause: "+food.getStatus());
	}
	
	public void onStop(){
		super.onStop();
		
	}
	
	public void onDestroy(){
		super.onDestroy();
		//Log.d(logTag, "Destroy: "+food.getStatus());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_food_details, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    case R.id.action_foodAssignment:
			openFoodAssignmentActivity();
			return true;    
	    case android.R.id.home:
	        	setResultActivity();
	        	Log.d(logTag, "HomeBack: "+food.getStatus());
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
		
	public void openFoodAssignmentActivity(){
						
			Intent foodAss=new Intent(this,FoodAssignmentActivity.class);
			foodAss.putExtra(Constants.foodDetailSP, food);
			startActivityForResult(foodAss, Constants.FOOD_DETAIL_REQUEST_CODE);
	}	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(Constants.foodDetailSP, food);
		setResult(RESULT_OK, intent);
		Log.d(logTag, "OnBackPressed: "+food.getStatus());
	    finish();
	}
	
}
