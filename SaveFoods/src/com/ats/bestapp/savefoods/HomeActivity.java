package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.transformer.UserTransformer;
import com.google.android.gms.plus.PlusClient;
import com.parse.Parse;
import com.parse.ParseException;

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

	FoodProxy foodProxy;
	UserTransformer	userTrasformer;
	private HashMap<String, Object> commonsData;
	private UserProxy userProxy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		//Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp"); 
		init();
		SharedPreferences settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		commonsData=new HashMap<String, Object>();
		try {
			commonsData.put(Constants.userIdSP, userProxy.getUserID(settings.getString(Constants.userNameSP, null), this));
			commonsData.put(Constants.userNameSP, settings.getString(Constants.userNameSP, null));
			String userId=(String)commonsData.get(Constants.userIdSP);
			if(userId!=null && !userId.isEmpty()){
				settings.edit().putString(Constants.userIdSP, userId).commit();
				foodProxy.getFoods4User(userId, this);
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
	
	private void init(){
		foodProxy=new FoodProxy();
		userTrasformer=new UserTransformer();
		userProxy=new UserProxy();
		Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
	}
}
