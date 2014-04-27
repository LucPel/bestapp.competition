package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
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
	private double currLatitude;
	private double currLongitude;

	
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
	    String um=food.getMeasurementunity();
		if(quantity==null || um==null) {
			quantity="1";
			um="Pz";
		}
	    TextView quantity_food=(TextView) findViewById(R.id.food_quantity_label);
	    quantity_food.setText(quantity+" "+um);
	    
	    TextView id_food=(TextView) findViewById(R.id.food_id_label);
	    id_food.setText(food.getFoodId());
		ImageView imageView = (ImageView) findViewById(R.id.food_image);
			if(food.getImages()!=null && food.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(food.getImages().get(0).getImage());
				if(image!=null){
					imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_x_size, Constants.standard_image_y_size, false));
				}
				else{
					imageView.setImageResource(R.drawable.food_no_image_icon);
				}
			}
			else{
				imageView.setImageResource(R.drawable.food_no_image_icon);
			}
			
			TextView owner_tw=(TextView)findViewById(R.id.food_owner_label);
			owner_tw.setText(Commons.getUsernameShow(food.getOwner().getUsername()));
			
			TextView textDistanceView = (TextView)findViewById(R.id.food_distance_label);
			float[] results=new float[10];
			double distance=0;
			Location.distanceBetween(food.getLatitude(), food.getLongitude(), currLatitude, currLongitude, results);
			distance=results[0];
			distance=distance*0.001;
			DecimalFormat df = new DecimalFormat("##.##");
			df.setRoundingMode(RoundingMode.DOWN);
			if(distance>0){
				textDistanceView.setText(df.format(distance)+"Km");
			}
			else{
				textDistanceView.setText("0Km");
			}
	}
	

	private void init(){
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
	    currLatitude=getIntent().getDoubleExtra(Constants.latitudeKey, 0);
	    currLongitude=getIntent().getDoubleExtra(Constants.longitudeKey, 0);
	    settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
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
	
	private void showMap(){
		LatLng food_point = new LatLng(food.getLatitude(), food.getLongitude());
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.addMarker(new MarkerOptions().position(food_point).title(food.getName()));
		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(food_point, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);	
	}
	
}
