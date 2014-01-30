package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.transformer.FoodTransformer;
import com.parse.Parse;

public class AddFoodActivity extends FragmentActivity{

	private HashMap<String, Object> commonsData;
	private LocationListenerWrapper locListenerWrap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    initCategoryAutoComplete();
	    SharedPreferences settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		commonsData=new HashMap<String, Object>();
		commonsData.put(Constants.userNameSP, settings.getString(Constants.userNameSP, null));
		Calendar cal = Calendar.getInstance();
		EditText dueDate=(EditText)findViewById(R.id.food_due_date);
		dueDate.clearComposingText();
		String day=Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String month=Integer.toString(cal.get(Calendar.MONTH)+1);
		String year=Integer.toString(cal.get(Calendar.YEAR));
		dueDate.setText(day+"-"+month+"-"+year);
		locListenerWrap=new LocationListenerWrapper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}
	
	public void saveFood(View view){
		FoodProxy fproxy=new FoodProxy();
		FoodTransformer ftransformer=new FoodTransformer();
		try {
			commonsData.put(Constants.latitudeKey, locListenerWrap.getLatitude());
			commonsData.put(Constants.longitudeKey, locListenerWrap.getLongitude());
			fproxy.addFood(ftransformer.transformInFood(view.getRootView(), commonsData),this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
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
	
	private void initCategoryAutoComplete(){
		AutoCompleteTextView autocomplete = (AutoCompleteTextView) findViewById(R.id.food_category_text);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_dropdown_item_1line, 
        		new String[] {"farinaceo","carne","latticino","verdura"}
        	);
        autocomplete.setAdapter(adapter);
	}
	
	public void showDatePickerDialog(View view){
		DialogFragment newFragment = new DatePickerDialogFragment((EditText)view);
	    newFragment.show(getSupportFragmentManager(), "datePicker");
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
}
