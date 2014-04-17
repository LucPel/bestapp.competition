package com.ats.bestapp.savefoods.data.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ats.bestapp.savefoods.LocationListenerWrapper;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.ImageWrapper;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class FoodProxy {

	private UserTransformer userTrasformer;
	private FoodTrasformer foodTrasformer;
	private static final String logTag="FoodProxy";
	private static final int maxFoods=6;
	
	public FoodProxy(){
		userTrasformer=new UserTransformer();
		foodTrasformer=new FoodTrasformer();
	}
	
	public void addFood(ParseObject food) throws JsonGenerationException, JsonMappingException, IOException, JSONException{
		food.saveInBackground();
	}
	
	public ArrayList<Food> getFoods4User(ParseObject user,int skippableItems) throws ParseException, JsonParseException, JsonMappingException, JsonGenerationException, IOException, JSONException{
		ArrayList<Food> foods=new ArrayList<Food>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.foodObject);
		String[] statusList = {Constants.foodStatusScaduto, Constants.foodStatusAssegnato};
		query.whereEqualTo(Constants.foodOwnerPO, user).whereNotContainedIn(Constants.foodStatusPO, Arrays.asList(statusList)).orderByAscending(Constants.foodDueDatePO).setLimit(maxFoods);
		query.setSkip(skippableItems);
		long start=System.currentTimeMillis();
		ArrayList<ParseObject> parseFoods=(ArrayList<ParseObject>) query.find();
		Log.d(logTag, String.valueOf(System.currentTimeMillis()-start));
		Log.d(logTag, String.valueOf(parseFoods.size()));
		ObjectMapper mapper=new ObjectMapper();
		for(ParseObject food : parseFoods){
			//Food foodvalue=mapper.readValue(mapper.writeValueAsString(food), Food.class);
			Log.d(logTag, mapper.writeValueAsString(food));
			foods.add(foodTrasformer.trasformParseObjectToFood(food));
		}
		return foods;
	}
	
	public Food getFood(String foodId) throws ParseException, JSONException{
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.foodObject);
		query.whereEqualTo("objectId", foodId);
		ArrayList<ParseObject> parseFoods=(ArrayList<ParseObject>) query.find();
		return foodTrasformer.trasformParseObjectToFood(parseFoods.get(0));
		
	}
	
	public void updateFoodStatus(Food food){
		Log.d(logTag, JsonMapper.convertObject2String(food));
		ParseObject foodPO=new ParseObject(Constants.foodObject);
		foodPO.setObjectId(food.getFoodId());
		foodPO.put(Constants.foodStatusPO, food.getStatus());
		foodPO.saveInBackground();
	}
	
	public void addCommentToAssigment(Food food) throws JSONException{
		ParseObject foodPO=new ParseObject(Constants.foodObject);
		foodPO.setObjectId(food.getFoodId());
		ArrayList<Comment> commentList=(ArrayList<Comment>) food.getSavingFoodAssignment().getConversation();
		JSONArray commentArray=new JSONArray();
		for(Comment currComment : commentList){
			JSONObject jsonComment=new JSONObject();
			jsonComment.put(Constants.foodAssigmentCommentTextPO, currComment.getMessage());
			jsonComment.put(Constants.foodAssigmentCommentUserPO, currComment.getUser().getUsername());
			commentArray.put(jsonComment);
		}
		foodPO.put(Constants.foodAssigmentCommentPO, commentArray);
		foodPO.saveInBackground();
	}
	
	//LUCPEL
	public ArrayList<Food> getFoods4Location(ParseObject user,Double latitude,Double longitude) 
				throws ParseException, JsonParseException, JsonMappingException, JsonGenerationException, IOException, JSONException{
			
			ArrayList<Food> foods=new ArrayList<Food>();		
			ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
			ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.foodObject);	
			String[] statusList = {Constants.foodStatusScaduto, Constants.foodStatusAssegnato};
			/* 
			 * La query estrae i cinque foods vicino all'utente 
			   che non abbiano come user l'utente stesso e che non siano scaduti.  
			 */
			//.whereNear("location", userLocation)
			if(latitude==0 || longitude==0){
				query.whereNotEqualTo(Constants.foodOwnerPO, user)
				.whereNotContainedIn(Constants.foodStatusPO, Arrays.asList(statusList)).orderByAscending(Constants.foodDueDatePO);
			}
			else{
				query.whereWithinKilometers(Constants.locationObject, userLocation, 3)
				.whereNotEqualTo(Constants.foodOwnerPO, user)
				.whereNotContainedIn(Constants.foodStatusPO, Arrays.asList(statusList)).orderByAscending(Constants.foodDueDatePO);
			}
			query.setLimit(maxFoods);
			ArrayList<ParseObject> parseFoods=(ArrayList<ParseObject>) query.find();
			Log.d(logTag, String.valueOf(parseFoods.size()));
			ObjectMapper mapper=new ObjectMapper();
			for(ParseObject food : parseFoods){
				Log.d(logTag, mapper.writeValueAsString(food));
				foods.add(foodTrasformer.trasformParseObjectToFood(food));
			}
			return foods;
		}
}
