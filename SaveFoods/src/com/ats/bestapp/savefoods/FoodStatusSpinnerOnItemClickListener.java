package com.ats.bestapp.savefoods;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.parse.ParsePush;
import com.parse.PushService;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FoodStatusSpinnerOnItemClickListener implements OnItemSelectedListener{

	private Food food;
	
	public FoodStatusSpinnerOnItemClickListener(Food food){
		this.food=food;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position,
			long id) {
		String status=adapterView.getItemAtPosition(position).toString();
		food.setStatus(status);
		FoodProxy foodProxy=new FoodProxy();
		foodProxy.updateFoodStatus(food);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
