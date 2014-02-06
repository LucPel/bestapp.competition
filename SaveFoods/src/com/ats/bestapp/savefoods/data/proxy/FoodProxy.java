package com.ats.bestapp.savefoods.data.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.ImageWrapper;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FoodProxy {

	private UserTransformer userTrasformer;
	private FoodTrasformer foodTrasformer;
	private static final String logTag="FoodProxy";
	
	public FoodProxy(){
		userTrasformer=new UserTransformer();
		foodTrasformer=new FoodTrasformer();
	}
	
	public void addFood(Food food,ParseObject user) throws JsonGenerationException, JsonMappingException, IOException, JSONException{
		//Parse.initialize(context, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp"); 
		ParseObject foodObj = new ParseObject(Constants.foodObject);
		if(user==null){
			foodObj.put("owner", userTrasformer.createParseObjectUser(food.getOwner()));
		}
		else{
			foodObj.put("owner", user);
		}
		foodObj.put("name", food.getName());
		foodObj.put("status", food.getStatus());
		foodObj.put("type", food.getType());
		foodObj.put("description", food.getDescription());
		foodObj.put("dueDate", food.getDueDate());
		foodObj.put(Constants.locationObject, new ParseGeoPoint(food.getLatitude(), food.getLongitude()));
		JSONArray jsonArrayImages=new JSONArray();
		for(ImageWrapper wrapper: food.getImages()){
			jsonArrayImages.put(wrapper.getImage());
		}
		foodObj.put("images", jsonArrayImages);
		Log.d(logTag, JsonMapper.convertObject2String(foodObj));
		foodObj.saveInBackground();
	}
	
	public List<Food> getFoods4User(String user,Context context) throws ParseException, JsonParseException, JsonMappingException, JsonGenerationException, IOException, JSONException{
		ArrayList<Food> foods=new ArrayList<Food>();
		//Parse.initialize(context, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp"); 
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.foodObject);
		ParseQuery<ParseObject> queryUser=ParseQuery.getQuery(Constants.userObject);
		ParseObject userObj=queryUser.get(user);
		query.whereEqualTo(Constants.foodOwnerPO, userObj);
		ArrayList<ParseObject> parseFoods=(ArrayList<ParseObject>) query.find();
		Log.d(logTag, String.valueOf(parseFoods.size()));
		ObjectMapper mapper=new ObjectMapper();
		for(ParseObject food : parseFoods){
			//Food foodvalue=mapper.readValue(mapper.writeValueAsString(food), Food.class);
			Log.d(logTag, mapper.writeValueAsString(food));
			foods.add(foodTrasformer.trasformParseObjectToFood(food));
		}
		return foods;
	}
	
	
}
