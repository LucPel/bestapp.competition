package com.ats.bestapp.savefoods.utilities;

public class Commons {

	public static String convertToDate(String datenf){
		return datenf.substring(6)+"-"+datenf.substring(4, 6)+"-"+datenf.substring(0, 4);
	}
}
