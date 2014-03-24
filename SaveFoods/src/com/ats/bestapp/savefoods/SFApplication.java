package com.ats.bestapp.savefoods;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.app.Application;

public class SFApplication extends Application{

	public void onCreate(){
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);
		PushService.setDefaultPushCallback(this, FoodAssignmentActivity.class);
		// Save the current Installation to Parse.
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
}
