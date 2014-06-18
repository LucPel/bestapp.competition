package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

//LUCPEL BranchLucPel
public class SearchFoodsActivity extends FragmentActivity{

	private String logTag="SearchFoodsActivity";
	private SharedPreferences settings;


	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private ArrayList<Food> foods;
	//private LocationListenerWrapper locListenerWrap;
	private HashMap<String, Object> commonsData;
	private SearchTableAdapter searchTableAdapter;
	private ProgressDialog searchProgressDialog;
	private GoogleMap map;
	private double currLatitude;
	private double currLongitude;
	private Double distanceToSearch=(double) 3;
	private int distanceFactors=3;
	private SFApplication  sfapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
		long start=System.currentTimeMillis();
		boolean timeout=false;
		
		if(sfapp.getCurrentLatitude()==0){
			while(sfapp.getCurrentLatitude()==0 && !timeout){
				long diff_timeout=System.currentTimeMillis()-start;
				Log.i(logTag, "Timeout "+diff_timeout);
				if(diff_timeout>3000) timeout=true;
			}
		}
		currLatitude=sfapp.getCurrentLatitude();
		currLongitude=sfapp.getCurrentLongitude();
		initMap();
		SFApplication app=(SFApplication) getApplicationContext();
		new GetUserFoodTask(app.getUserLoggedIn(),currLatitude,currLongitude).execute();
	}

	 @Override
	  protected void onResume() {
	    super.onResume();
	    //locListenerWrap.requestLocationUpdates();
	  }
	  
	  @Override
	  protected void onPause() {
	    super.onPause();
	    //locListenerWrap.removeUpdates();
	  }

	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.action_bar_search_food, menu);
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle presses on the action bar items
			switch (item.getItemId()) {
			case R.id.action_minusZoom:
				minusZoomSearch();
				return true;
			case R.id.action_plusZoom:
				plusZoomSearch();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}



	private void init(){
		//locListenerWrap=new LocationListenerWrapper(this);
		commonsData=new HashMap<String, Object>();
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		sfapp=(SFApplication) getApplication();
	}

	private void fillGrid(){
		GridView gridView = (GridView) findViewById(R.id.gridview);
		if(searchTableAdapter==null){
			
			searchTableAdapter=new SearchTableAdapter(this, foods,sfapp.getCurrentLatitude(),sfapp.getCurrentLongitude(),distanceToSearch);
		}
		else{
			searchTableAdapter.setFoods(foods);
		}
		gridView.setAdapter(searchTableAdapter);
		gridView.setOnScrollListener(searchTableAdapter);
/*		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Food foodSelected=(Food) searchTableAdapter.getItem(position);
				//Intent foodAss=new Intent(parent.getContext(),FoodDetailsActivity.class);
				Intent foodAss=new Intent(parent.getContext(),FoodAssignmentActivity.class);
				foodAss.putExtra(Constants.foodDetailSP, foodSelected);
				foodAss.putExtra(Constants.latitudeKey, currLatitude);
				foodAss.putExtra(Constants.longitudeKey, currLongitude);
				Log.d(logTag, JsonMapper.convertObject2String(foodSelected));
				startActivityForResult(foodAss, Constants.FOOD_DETAIL_REQUEST_CODE);
			}
		});*/
	}

	private void startDialogLoading() {
		searchProgressDialog = new ProgressDialog(this);
		searchProgressDialog.setMessage("Loading");
		searchProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
		searchProgressDialog.setCancelable(false);
		searchProgressDialog.show();
	}

	// ASYNC TASKS
	private class GetUserFoodTask extends
			AsyncTask<Void, Integer, ArrayList<Food>> {

		ParseObject userPO;
		double latitude;
		double longitude;

		public GetUserFoodTask(ParseObject userPO_i,double latitude_i,double longitude_i){
			userPO=userPO_i;	
			latitude=latitude_i;
			longitude=longitude_i;
		}

		@Override
		protected ArrayList<Food> doInBackground(Void... params) {
			try {
				Log.i(logTag, "Esecuzione query "+userPO);
				Log.d(logTag, "Latitude:" +latitude);
				foods = foodProxy.getFoods4Location(userPO,latitude,longitude,distanceToSearch,0);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return foods;
		}

	
		protected void onPostExecute(ArrayList<Food> foods_out) {
			searchProgressDialog.dismiss();
			fillGrid();
			showMap();
		}

		protected void onPreExecute() {
			startDialogLoading();
		}

	}

	private void initMap(){
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng food_point = new LatLng(currLatitude, currLongitude);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(food_point, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);	
	}
	
	private void showMap(){
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		for(Food food: foods){
			LatLng food_point = new LatLng(food.getLatitude(), food.getLongitude());
			map.addMarker(new MarkerOptions().position(food_point).title(food.getName()+"("+food.getType()+")"));
			// Move the camera instantly to hamburg with a zoom of 15.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(food_point, 15));
			// Zoom in, animating the camera.
			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);	
		}
		
	}

	public void openAssignment(View view){
		View parent=(View) view.getParent();
		TextView id_text=(TextView) parent.findViewById(R.id.grid_item_id);
		TextView position_text=(TextView) parent.findViewById(R.id.grid_item_position);
		Log.d(logTag, "ID FOOD: "+id_text.getText().toString());
		Log.d(logTag, "ID FOOD POSITION: "+position_text.getText().toString());
		Food foodSelected=(Food) searchTableAdapter.getItem(Integer.parseInt(position_text.getText().toString()));
		//Intent foodAss=new Intent(parent.getContext(),FoodDetailsActivity.class);
		Intent foodAss=new Intent(parent.getContext(),FoodAssignmentActivity.class);
		foodAss.putExtra(Constants.foodDetailSP, foodSelected);
		foodAss.putExtra(Constants.latitudeKey, currLatitude);
		foodAss.putExtra(Constants.longitudeKey, currLongitude);
		Log.d(logTag, JsonMapper.convertObject2String(foodSelected));
		startActivityForResult(foodAss, Constants.FOOD_DETAIL_REQUEST_CODE);
	}

	private void minusZoomSearch(){
		SFApplication app=(SFApplication) getApplicationContext();
		if(distanceToSearch.intValue()>Constants.minDistanceRange){
			distanceToSearch=Double.valueOf(distanceToSearch/distanceFactors);
			Log.d(logTag, "Minus Zoom "+distanceToSearch);
			searchTableAdapter.setDistanceToSearch(distanceToSearch);
			new GetUserFoodTask(app.getUserLoggedIn(),currLatitude,currLongitude).execute();
		}
		else{
			Toast.makeText(this, getResources().getString(R.string.minDistanceMessage), Toast.LENGTH_LONG)
			.show();
		}
	}
	
	private void plusZoomSearch(){
		SFApplication app=(SFApplication) getApplicationContext();
		if(distanceToSearch.intValue()<Constants.maxDistanceRange){
			distanceToSearch=Double.valueOf(distanceToSearch*distanceFactors);
			searchTableAdapter.setDistanceToSearch(distanceToSearch);
			Log.d(logTag, "Plus Zoom "+distanceToSearch);
			new GetUserFoodTask(app.getUserLoggedIn(),currLatitude,currLongitude).execute();
		}
		else{
			Toast.makeText(this, getResources().getString(R.string.maxDistanceMessage), Toast.LENGTH_LONG)
			.show();
		}
	}

}