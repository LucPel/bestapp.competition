package com.ats.bestapp.savefoods.utilities;

public class Commons {

	public static String convertToDate(String datenf){
		return datenf.substring(6)+"-"+datenf.substring(4, 6)+"-"+datenf.substring(0, 4);
	}
	
	public static String getUsernameShow(String long_username){
		String userOwner=long_username;
		return userOwner.substring(0, long_username.indexOf("@"));
	}
}
