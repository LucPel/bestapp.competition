package com.ats.bestapp.savefoods.data.proxy;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;

public class UserProxy {
	
	private UserTransformer userTasformer;

	public UserProxy(){
		init();
	}
	
	public User getUser(String username,Context context) throws ParseException{
		//Parse.initialize(context, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
		ParseQuery<ParseObject> queryUser=ParseQuery.getQuery(Constants.userObject);
		queryUser.whereEqualTo("username", username);
		ParseObject userObj=queryUser.getFirst();
		return userTasformer.trasformUserFromParseObject(userObj);
	}
	
	public ParseObject getUserParseObject(String username) throws ParseException{
		ParseQuery<ParseObject> queryUser=ParseQuery.getQuery(Constants.userObject);
		queryUser.whereEqualTo("username", username);
		ParseObject userObj=queryUser.getFirst();
		return userObj;
	}
	
	public void saveNewUser(String username) throws ParseException{
		ParseObject userPO=userTasformer.createParseObjectUser(username);
		userPO.save();
	}
	
	private void init(){
		userTasformer=new UserTransformer();
	}
}
