package com.ats.bestapp.savefoods.data.proxy;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.User;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class FoodProxy {

	private static final String key="2JSAS_yhnVSLKX0FzhOOBrGcHLjhSlUU";
	
	public void addFood(Food food,Context context) throws JsonGenerationException, JsonMappingException, IOException, JSONException{
		Parse.initialize(context, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp"); 
		ParseObject bigObject = new ParseObject(Constants.foodObject);
		ObjectMapper mapper=new ObjectMapper();
		String jsonfood=mapper.writeValueAsString(food);
		JSONObject jsonObj = new JSONObject( jsonfood );
		bigObject.put(Constants.foodObject, jsonObj);
		bigObject.put(Constants.locationObject, new ParseGeoPoint(food.getLatitude(), food.getLongitude()));
		bigObject.saveInBackground();
	}
	
	
}
