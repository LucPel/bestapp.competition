package com.ats.bestapp.savefoods.trasformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.R;
import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.ImageWrapper;
import com.ats.bestapp.savefoods.data.SavingFoodAssignment;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class FoodTrasformer {

	private UserTransformer userTrasformer;
	private static final String logTag = "FoodTrasformer";

	public ParseObject trasformInFood(View view, HashMap<String, Object> commonsData,
			ArrayList<byte[]> imagesByte,ParseObject user) {
		EditText name = (EditText) view.findViewById(R.id.food_name_text);
		EditText description = (EditText) view
				.findViewById(R.id.food_description_text);
		Spinner categorySpinner = (Spinner) view
				.findViewById(R.id.food_category_spinner);
		TextView catTextView = (TextView)categorySpinner.getSelectedView();
		EditText date = (EditText) view.findViewById(R.id.food_due_date);
		EditText quantityET = (EditText) view.findViewById(R.id.food_quantity_text);
		Spinner UMSpinner=(Spinner)view.findViewById(R.id.food_um_spinner);
		TextView umTextView = (TextView)UMSpinner.getSelectedView();
		String day=date.getText().subSequence(0, 2).toString();
		String month=date.getText().subSequence(3, 5).toString();
		String year=date.getText().subSequence(6, date.getText().length()).toString();
		Log.d(logTag, "Converted Date: "+year+month+day);
		ParseObject foodObj = new ParseObject(Constants.foodObject);
		JSONArray assigment=new JSONArray();
		String channel=(String) commonsData.get(Constants.foodChannelPO);
		if(channel==null || channel.trim().isEmpty()){
			foodObj.put(Constants.foodChannelPO,UUID.randomUUID().toString().replace("-", ""));
		}
		else{
			foodObj.put(Constants.foodChannelPO,channel);
		}
		foodObj.put(Constants.foodAssigmentCommentPO, assigment);
		foodObj.put(Constants.foodNamePO, name.getText().toString());
		String statusCurr=(String) commonsData.get(Constants.foodStatusPO);
		if(commonsData.get(Constants.foodStatusPO)==null || statusCurr.trim().isEmpty()){
			foodObj.put(Constants.foodStatusPO, Constants.foodStatusDisponibile);
		}
		else{
			foodObj.put(Constants.foodStatusPO, commonsData.get(Constants.foodStatusPO));
		}
		String foodID=(String) commonsData.get(Constants.foodIdPO);
		if(foodID!=null && !foodID.trim().isEmpty()){
			foodObj.setObjectId(foodID);
		}
		foodObj.put(Constants.foodCategoryPO, catTextView.getText().toString());
		foodObj.put(Constants.foodDescritpionPO, description.getText().toString());
		foodObj.put(Constants.foodDueDatePO, year+month+day);
		foodObj.put(Constants.foodQuantityPO, quantityET.getText().toString());
		foodObj.put(Constants.foodMeasurementUnitPO, umTextView.getText().toString());
		foodObj.put(Constants.locationObject, new ParseGeoPoint((Double) commonsData.get(Constants.latitudeKey), (Double) commonsData.get(Constants.longitudeKey)));
		foodObj.put(Constants.foodOwnerPO, user);
		JSONArray jsonArrayImages=new JSONArray();
		for (byte[] imageByte : imagesByte) {
			jsonArrayImages.put(imageByte);
		}
		foodObj.put(Constants.foodImagesPO, jsonArrayImages);
		return foodObj;
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
		foodT.setQuantity(food.getString(Constants.foodQuantityPO));
		foodT.setChannel(food.getString(Constants.foodChannelPO));
		foodT.setMeasurementunity(food.getString(Constants.foodMeasurementUnitPO));
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
				comment.setMessageTime(currentComment
						.getString(Constants.foodAssigmentCommentTimestampPO));
				foodT.getSavingFoodAssignment().addComment(comment);
			}
		}
		Log.d(logTag, JsonMapper.convertObject2String(foodT));
		return foodT;
	}

}
