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
import android.os.AsyncTask;
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

public class HomeActivity extends Activity {

	private FoodProxy foodProxy;
	private HashMap<String, Object> commonsData;
	private UserProxy userProxy;
	private SharedPreferences settings;
	private HomeTableAdapter homeTableAdapter;
	private ArrayList<Food> foods;
	private String logTag = "HomeActivity";
	private ProgressDialog homeProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		init();
		commonsData = new HashMap<String, Object>();
		commonsData.put(Constants.userNameSP,
				settings.getString(Constants.userNameSP, null));
		String userid = settings.getString(Constants.userIdSP, null);
		if (userid != null && !userid.isEmpty()) {
			commonsData.put(Constants.userIdSP, userid);
			if (findViewById(R.id.grid_item_label) == null) {
				new GetUserFoodTask().execute(userid);
			}
		}
		else{
			User user=null;
			try {
				user = userProxy.getUser(
						settings.getString(Constants.userNameSP, null),
						this);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (user != null && !user.getUserId().isEmpty()) {
				settings.edit()
						.putString(Constants.userIdSP, user.getUserId())
						.commit();
				new GetUserFoodTask().execute(user.getUserId());
			}
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
			openSearchFoodsActivity();
			return true;
		case R.id.action_addFoodRequest:
			// composeMessage();
			return true;
		case R.id.action_settings:
			// composeMessage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openAddFoodActivity() {
		Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
		startActivityForResult(intent, Constants.ADD_FOOD_REQUEST_CODE);
	}

	// LUCPEL
	private void openSearchFoodsActivity() {
		Intent intent = new Intent(HomeActivity.this, SearchFoodsActivity.class);
		startActivity(intent);
	}

	private void init() {
		foodProxy = new FoodProxy();
		userProxy = new UserProxy();
		settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void fillGrid() {
		GridView gridView = (GridView) findViewById(R.id.gridview);
		if (homeTableAdapter == null) {
			homeTableAdapter = new HomeTableAdapter(this, foods);
		} else {
			homeTableAdapter.setFoods(foods);
		}
		gridView.setAdapter(homeTableAdapter);
		gridView.setOnScrollListener(homeTableAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Food foodSelected = (Food) homeTableAdapter.getItem(position);
				Intent foodAss = new Intent(parent.getContext(),
						FoodAssignmentActivity.class);
				// settings.edit().putString(Constants.foodDetailSP,
				// JsonMapper.convertObject2String(foodSelected)).apply();
				foodAss.putExtra(Constants.foodDetailSP, foodSelected);
				Log.d(logTag, JsonMapper.convertObject2String(foodSelected));
				startActivityForResult(foodAss,
						Constants.FOOD_DETAIL_REQUEST_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		Log.d(logTag, "requestCode " + requestCode + " responseCode "
				+ responseCode);
		if (requestCode == Constants.FOOD_DETAIL_REQUEST_CODE
				&& (responseCode == RESULT_OK || responseCode == RESULT_CANCELED)) {
			Food food = (Food) intent
					.getSerializableExtra(Constants.foodDetailSP);
			if (food != null) {
				Food c_food = null;
				for (int i = 0; i < foods.size(); i++) {
					c_food = foods.get(i);
					if (c_food.getFoodId().equalsIgnoreCase(food.getFoodId())) {
						foods.set(i, food);
					}
				}
				homeTableAdapter.setFoods(foods);
				homeTableAdapter.notifyDataSetChanged();
				Log.d(logTag, "onActivityResult " + food.getStatus());
			}
		} else if (requestCode == Constants.ADD_FOOD_REQUEST_CODE
				&& responseCode == Constants.ADD_FOOD_RESPONSE_CODE) {

			try {
				if (foods == null || foods.isEmpty()) {
					User user = userProxy.getUser(
							settings.getString(Constants.userNameSP, null),
							this);
					if (user != null && !user.getUserId().isEmpty()) {
						settings.edit()
								.putString(Constants.userIdSP, user.getUserId())
								.commit();
					}
				}
				foods = foodProxy.getFoods4User(settings.getString(
						Constants.userIdSP, null),0);
				if (homeTableAdapter == null) {
					homeTableAdapter = new HomeTableAdapter(this, foods);
				} else {
					homeTableAdapter.setFoods(foods);
				}
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

	private void startDialogLoading() {
		homeProgressDialog = new ProgressDialog(this);
		homeProgressDialog.setMessage("Loading");
		homeProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
		homeProgressDialog.setCancelable(false);
		homeProgressDialog.show();
	}

	// ASYNC TASKS
	private class GetUserFoodTask extends
			AsyncTask<String, Integer, ArrayList<Food>> {

		@Override
		protected ArrayList<Food> doInBackground(String... params) {
			try {
				foods = foodProxy.getFoods4User(params[0],0);
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
			homeProgressDialog.dismiss();
			fillGrid();
		}

		protected void onPreExecute() {
			startDialogLoading();
		}

	}
}
