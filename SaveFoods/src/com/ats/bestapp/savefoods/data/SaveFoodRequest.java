package com.ats.bestapp.savefoods.data;

import java.io.Serializable;

public class SaveFoodRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String foodType;
	private String foodName;
	private String endDateOfRequest;
	
	public String getFoodType() {
		return foodType;
	}
	public void setFoodType(String foodType) {
		this.foodType = foodType;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public String getEndDateOfRequest() {
		return endDateOfRequest;
	}
	public void setEndDateOfRequest(String endDateOfRequest) {
		this.endDateOfRequest = endDateOfRequest;
	}
	
}
