package com.ats.bestapp.savefoods.trasformer;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.ImageWrapper;
import com.ats.bestapp.savefoods.data.SavingFoodAssignment;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.ParseException;
import com.parse.ParseObject;

public class FoodTrasformer {

	private UserTransformer userTrasformer;
	private static final String logTag = "FoodTrasformer";

	public Food trasformInFood(View view, HashMap<String, Object> commonsData,
			ArrayList<byte[]> imagesByte) {
		EditText name = (EditText) view.findViewById(R.id.food_name_text);
		EditText description = (EditText) view
				.findViewById(R.id.food_description_text);
		EditText category = (EditText) view
				.findViewById(R.id.food_category_text);
		EditText date = (EditText) view.findViewById(R.id.food_due_date);
		Food food = new Food();
		food.setName(name.getText().toString());
		food.setDescription(description.getText().toString());
		food.setType(category.getText().toString());
		food.setStatus(Constants.foodStatusDisponibile);
		String day=date.getText().subSequence(0, 2).toString();
		String month=date.getText().subSequence(3, 5).toString();
		String year=date.getText().subSequence(6, date.getText().length()).toString();
		Log.d(logTag, "Converted Date: "+year+month+day);
		food.setDueDate(year+month+day);
		food.setLatitude((Double) commonsData.get(Constants.latitudeKey));
		food.setLongitude((Double) commonsData.get(Constants.longitudeKey));
		String userd = (String) commonsData.get(Constants.userIdSP);
		if (userd == null) {
			User user = new User();
			user.setUsername((String) commonsData.get(Constants.userNameSP));
			user.setNickname((String) commonsData.get(Constants.userNameSP));
			food.setOwner(user);
		}
		ArrayList<ImageWrapper> imagesWrapper = new ArrayList<ImageWrapper>();
		for (byte[] imageByte : imagesByte) {
			ImageWrapper imageWrapper = new ImageWrapper();
			imageWrapper.setImage(imageByte);
			imagesWrapper.add(imageWrapper);
		}
		food.setSavingFoodAssignment(new SavingFoodAssignment());
		Log.d(logTag, JsonMapper.convertObject2String(imagesWrapper));
		food.setImages(imagesWrapper);
		return food;
	}

	public Food trasformParseObjectToFood(ParseObject food)
			throws ParseException, JSONException {
		Food foodT = new Food();
		foodT.setFoodId(food.getObjectId());
		foodT.setName(food.getString(Constants.foodNamePO));
		foodT.setDescription(food.getString(Constants.foodDescritpionPO));
		foodT.setStatus(food.getString(Constants.foodStatusPO));
		foodT.setType(food.getString(Constants.foodCategoryPO));
		foodT.setLatitude(food.getParseGeoPoint(Constants.locationObject)
				.getLatitude());
		foodT.setLongitude(food.getParseGeoPoint(Constants.locationObject)
				.getLongitude());
		foodT.setDueDate(food.getString(Constants.foodDueDatePO));
		ParseObject userObj = food.getParseObject(Constants.foodOwnerPO);
		userObj.fetchIfNeeded();
		userTrasformer = new UserTransformer();
		User user = userTrasformer.trasformUserFromParseObject(userObj);
		foodT.setOwner(user);
		JSONArray images = food.getJSONArray(Constants.foodImagesPO);
		ArrayList<ImageWrapper> imagesWrapper = new ArrayList<ImageWrapper>();
		JSONObject jsonObjImage;
		String currentImage;
		if (images != null) {
			for (int i = 0; i < images.length(); i++) {
				jsonObjImage = (JSONObject) images.get(i);
				currentImage = jsonObjImage.getString("base64");
				ImageWrapper imgWrapper = new ImageWrapper();
				imgWrapper
						.setImage(Base64.decode(currentImage, Base64.DEFAULT));
				imagesWrapper.add(imgWrapper);
			}
			foodT.setImages(imagesWrapper);
		}
		foodT.setSavingFoodAssignment(new SavingFoodAssignment());
		JSONArray commentsArray = food
				.getJSONArray(Constants.foodAssigmentCommentPO);
		JSONObject currentComment;
		if (commentsArray != null) {
			for (int i = 0; i < commentsArray.length(); i++) {
				currentComment = commentsArray.getJSONObject(i);
				Comment comment = new Comment();
				comment.setMessage(currentComment
						.getString(Constants.foodAssigmentCommentTextPO));
				User usercomment = new User();
				usercomment.setUsername(currentComment
						.getString(Constants.foodAssigmentCommentUserPO));
				comment.setUser(usercomment);
				foodT.getSavingFoodAssignment().addComment(comment);
			}
		}
		Log.d(logTag, JsonMapper.convertObject2String(foodT));
		return foodT;
	}

}
