package com.ats.bestapp.savefoods;

import java.util.HashMap;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

import android.app.Application;

public class SFApplication extends Application{

	private ParseObject userLoggedIn;
	private HashMap<String, Integer> categoriesIcon;
	
	public void onCreate(){
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);
		PushService.setDefaultPushCallback(this, FoodAssignmentActivity.class);
		// Save the current Installation to Parse.
		ParseInstallation.getCurrentInstallation().saveInBackground();
		setCategoriesIcon();
	}

	public ParseObject getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(ParseObject userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}
	
	private void setCategoriesIcon(){
		categoriesIcon=new HashMap<String, Integer>();
		categoriesIcon.put(Constants.cat_bibita, R.drawable.cat_bibite_icon);
		categoriesIcon.put(Constants.cat_carne, R.drawable.cat_carne_icon);
		categoriesIcon.put(Constants.cat_cereali, R.drawable.cat_cereali_icon);
		categoriesIcon.put(Constants.cat_crostaceo, R.drawable.cat_crostacei_icon);
		categoriesIcon.put(Constants.cat_farinaceo, R.drawable.cat_farinacei_icon);
		categoriesIcon.put(Constants.cat_frutta, R.drawable.cat_frutta_icon);
		categoriesIcon.put(Constants.cat_latticini, R.drawable.cat_latticini_icon);
		categoriesIcon.put(Constants.cat_legumi, R.drawable.cat_legumi_icon);
		categoriesIcon.put(Constants.cat_pesce, R.drawable.cat_pesce_icon);
		categoriesIcon.put(Constants.cat_verdura, R.drawable.cat_verdura_icon);
		categoriesIcon.put(Constants.cat_dolce, R.drawable.cat_dolci_icon);
	}
	
	public int getCategoryIcon(String catName){
		return categoriesIcon.get(catName);
	}
}
