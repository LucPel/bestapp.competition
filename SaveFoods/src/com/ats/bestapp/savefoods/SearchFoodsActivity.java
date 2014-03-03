package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

//LUCPEL
public class SearchFoodsActivity extends FragmentActivity{

	private String logTag="SearchFoodsActivity";
	private SharedPreferences settings;
	
	private LocationListenerWrapper locListenerWrap;
	
	private User	user;
	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private HashMap<String, Food> foods;
	private HomeTableAdapter homeTableAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
		
		try {
			user=userProxy.getUser(settings.getString(Constants.userNameSP, null), this);
				if(findViewById(R.id.grid_item_label)==null){
					foods=(HashMap<String, Food>) foodProxy.getFoods4Location(user.getUserId(),locListenerWrap,this);
					fillGrid();
				}				
		} catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		
		}catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		}catch (JsonParseException e) {
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

}