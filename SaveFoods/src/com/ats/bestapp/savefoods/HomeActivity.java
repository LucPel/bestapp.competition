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
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.google.android.gms.plus.PlusClient;
import com.parse.Parse;
import com.parse.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class HomeActivity extends Activity{

	private FoodProxy foodProxy;
	private User	user;
	private HashMap<String, Object> commonsData;
	private UserProxy userProxy;
	private SharedPreferences settings;
	private HomeTableAdapter homeTableAdapter;
	private HashMap<String, Food> foods;
	private String logTag="HomeActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		init();
		settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		commonsData=new HashMap<String, Object>();
		try {
			user=userProxy.getUser(settings.getString(Constants.userNameSP, null), this);
			commonsData.put(Constants.userIdSP, user.getUserId());
			commonsData.put(Constants.userNameSP, settings.getString(Constants.userNameSP, null));
			if(user.getUserId()!=null && !user.getUserId().isEmpty()){
				settings.edit().putString(Constants.userIdSP, user.getUserId()).commit();
				if(findViewById(R.id.grid_item_label)==null){
					foods=(HashMap<String, Food>) foodProxy.getFoods4User(user.getUserId(), this);
					fillGrid();
				}
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
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
		startActivityForResult(intent, Constants.ADD_FOOD_REQUEST_CODE);
	}
	
	private void init(){
		foodProxy=new FoodProxy();
		userProxy=new UserProxy();
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	private void fillGrid(){
		GridView gridView = (GridView) findViewById(R.id.gridview);
		if(homeTableAdapter==null){
			homeTableAdapter=new HomeTableAdapter(this, foods);
		}
		else{
			homeTableAdapter.setFoods(foods);
		}
		gridView.setAdapter(homeTableAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Food foodSelected=(Food) homeTableAdapter.getItem(position);
				Intent foodAss=new Intent(parent.getContext(),FoodAssignmentActivity.class);
				//settings.edit().putString(Constants.foodDetailSP, JsonMapper.convertObject2String(foodSelected)).apply();
				foodAss.putExtra(Constants.foodDetailSP, foodSelected);
				Log.d(logTag, JsonMapper.convertObject2String(foodSelected));
				startActivityForResult(foodAss, Constants.FOOD_DETAIL_REQUEST_CODE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		Log.d(logTag, "requestCode "+requestCode+" responseCode "+responseCode);
		if (requestCode == Constants.FOOD_DETAIL_REQUEST_CODE && (responseCode == RESULT_OK || responseCode==RESULT_CANCELED)) {
	        Food food=(Food) intent.getSerializableExtra(Constants.foodDetailSP);
	        if(food!=null){
	        	foods.remove(food.getFoodId());
				foods.put(food.getFoodId(), food);
				Log.d(logTag, "onActivityResult "+food.getStatus());
	        }
	    }
		else if (requestCode == Constants.ADD_FOOD_REQUEST_CODE && responseCode == Constants.ADD_FOOD_RESPONSE_CODE) {
			
				try {
					foods=(HashMap<String, Food>) foodProxy.getFoods4User(user.getUserId(), this);
					homeTableAdapter.notifyDataSetChanged();
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
				
		}
	}
	
}
