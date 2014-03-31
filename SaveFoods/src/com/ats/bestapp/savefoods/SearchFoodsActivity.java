package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;

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

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private SearchTableAdapter searchTableAdapter;
	private ProgressDialog searchProgressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();

		String userid = settings.getString(Constants.userIdSP, null);
		if(findViewById(R.id.grid_item_label)==null){			
				new GetUserFoodTask(userid,locListenerWrap).execute();
		}


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}



	private void init(){
		locListenerWrap=new LocationListenerWrapper(this);
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);	
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

		String user;
		LocationListenerWrapper locListenerWrap;

		public GetUserFoodTask(String user_,LocationListenerWrapper locListenerWrap_){
			user=user_;
			locListenerWrap=locListenerWrap_;			
		}

		@Override
		protected ArrayList<Food> doInBackground(Void... params) {
			try {
				foods = foodProxy.getFoods4Location(user,locListenerWrap);
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