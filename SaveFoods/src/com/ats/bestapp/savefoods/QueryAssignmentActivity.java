package com.ats.bestapp.savefoods;


import java.util.ArrayList;

import com.ats.bestapp.savefoods.data.Food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;

public class QueryAssignmentActivity extends FragmentActivity{
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
		
		final CheckBox checkQuery1 = (CheckBox) findViewById(R.id.checkQuery1);
		final CheckBox checkQuery2 = (CheckBox) findViewById(R.id.checkQuery2);
		final CheckBox checkQuery3 = (CheckBox) findViewById(R.id.checkQuery3);

		
		CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				if (button == checkQuery1 && checkQuery1.isChecked()) {					
					openSearchFoodsActivity();
				} else if (button == checkQuery2  && checkQuery2.isChecked()     ) {
					openSearchFoodsActivity();
				} else if (button == checkQuery3 && checkQuery3.isChecked() ) {
					openSearchFoodsActivity();						
				} else {
					//Log.w(CHECK_BOX_TEST_TAG,"No checkBox selected. Check the code!");
				}
		
			}

		};
	
	}
		
	private void openSearchFoodsActivity() {
		Intent intent = new Intent(QueryAssignmentActivity.this, SearchFoodsActivity.class);
		startActivity(intent);
	}

}
