package com.ats.bestapp.savefoods.data;

public class Food {

	private String name;
	private String description;
	private String dueDate;
	private String status;
	private String type;
	private User owner;
	private double latitude;
	private double longitude;
	private SavingFoodAssignment savingFoodAssignment;
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public SavingFoodAssignment getSavingFoodAssignment() {
		return savingFoodAssignment;
	}
	public void setSavingFoodAssignment(SavingFoodAssignment savingFoodAssignment) {
		this.savingFoodAssignment = savingFoodAssignment;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
