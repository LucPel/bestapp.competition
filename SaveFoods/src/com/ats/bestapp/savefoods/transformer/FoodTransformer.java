package com.ats.bestapp.savefoods.transformer;

import java.util.HashMap;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.User;

public class FoodTransformer {

	public Food transformInFood(View view,HashMap<String, Object> commonsData){
		EditText name=(EditText) view.findViewById(R.id.food_name_text);
		EditText description=(EditText) view.findViewById(R.id.food_description_text);
		EditText category=(EditText) view.findViewById(R.id.food_category_text);
		EditText date=(EditText) view.findViewById(R.id.food_due_date);
		Food food=new Food();
		food.setName(name.getText().toString());
		food.setDescription(description.getText().toString());
		food.setType(category.getText().toString());
		food.setStatus("Disponibile");
		food.setDueDate(date.getText().toString());
		food.setLatitude((Double)commonsData.get(Constants.latitudeKey));
		food.setLongitude((Double)commonsData.get(Constants.longitudeKey));
		User user=new User();
		user.setUsername((String) commonsData.get(Constants.userNameSP));
		food.setOwner(user);
		return food;
	}
	
	private String date2String(DatePicker date){
		String day=Integer.toString(date.getDayOfMonth());
		String month=Integer.toString(date.getMonth());
		String year=Integer.toString(date.getYear());
		return day+"-"+month+"-"+year;
	}
}
