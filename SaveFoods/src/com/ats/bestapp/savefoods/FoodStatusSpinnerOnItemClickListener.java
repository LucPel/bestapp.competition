package com.ats.bestapp.savefoods;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FoodStatusSpinnerOnItemClickListener implements OnItemSelectedListener{

	private String foodId;
	
	public FoodStatusSpinnerOnItemClickListener(String foodId){
		this.foodId=foodId;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position,
			long id) {
		Food food=new Food();
		food.setFoodId(foodId);
		food.setStatus(adapterView.getItemAtPosition(position).toString());
		FoodProxy foodProxy=new FoodProxy();
		foodProxy.updateFoodStatus(food);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
