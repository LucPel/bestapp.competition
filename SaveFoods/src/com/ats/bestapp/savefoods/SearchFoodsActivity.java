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
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

//LUCPEL BranchLucPel
public class SearchFoodsActivity extends FragmentActivity{

	private String logTag="SearchFoodsActivity";
	private SharedPreferences settings;


	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private ArrayList<Food> foods;
	private LocationListenerWrapper locListenerWrap;
	private HashMap<String, Object> commonsData;
	private SearchTableAdapter searchTableAdapter;
	private ProgressDialog searchProgressDialog;
	private double currLatitude;
	private double currLongitude;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
		long start=System.currentTimeMillis();
		boolean timeout=false;
		if(locListenerWrap.getLatitude()==0){
			while(locListenerWrap.getLatitude()==0 && !timeout){
				long diff_timeout=System.currentTimeMillis()-start;
				Log.i(logTag, "Timeout "+diff_timeout);
				if(diff_timeout>3000) timeout=true;
			}
		}
		currLatitude=locListenerWrap.getLatitude();
		currLongitude=locListenerWrap.getLongitude();
		SFApplication app=(SFApplication) getApplicationContext();
		new GetUserFoodTask(app.getUserLoggedIn(),currLatitude,currLongitude).execute();
	}

	 @Override
	  protected void onResume() {
	    super.onResume();
	    locListenerWrap.requestLocationUpdates();
	  }
	  
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locListenerWrap.removeUpdates();
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}



	private void init(){
		locListenerWrap=new LocationListenerWrapper(this);
		commonsData=new HashMap<String, Object>();
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	}

	private void fillGrid(){
		GridView gridView = (GridView) findViewById(R.id.gridview);
		if(searchTableAdapter==null){
			searchTableAdapter=new SearchTableAdapter(this, foods,locListenerWrap.getLatitude(),locListenerWrap.getLongitude());
		}
		else{
			searchTableAdapter.setFoods(foods);
		}
		gridView.setAdapter(searchTableAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Food foodSelected=(Food) searchTableAdapter.getItem(position);
				Intent foodAss=new Intent(parent.getContext(),FoodDetailsActivity.class);
				foodAss.putExtra(Constants.foodDetailSP, foodSelected);
				foodAss.putExtra(Constants.latitudeKey, currLatitude);
				foodAss.putExtra(Constants.longitudeKey, currLongitude);
				Log.d(logTag, JsonMapper.convertObject2String(foodSelected));
				startActivityForResult(foodAss, Constants.FOOD_DETAIL_REQUEST_CODE);
			}
		});
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
				foods = foodProxy.getFoods4Location(userPO,latitude,longitude);
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
		}

		protected void onPreExecute() {
			startDialogLoading();
		}

	}



}