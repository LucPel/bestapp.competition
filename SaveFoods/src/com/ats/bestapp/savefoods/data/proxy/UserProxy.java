package com.ats.bestapp.savefoods.data.proxy;

import com.ats.bestapp.savefoods.Constants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;

public class UserProxy {

	public String getUserID(String username,Context context) throws ParseException{
		//Parse.initialize(context, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
		ParseQuery<ParseObject> queryUser=ParseQuery.getQuery(Constants.userObject);
		queryUser.whereEqualTo("username", username);
		ParseObject userObj=queryUser.getFirst();
		return userObj.getObjectId();
	}
	
	public ParseObject getUser(String username) throws ParseException{
		ParseQuery<ParseObject> queryUser=ParseQuery.getQuery(Constants.userObject);
		queryUser.whereEqualTo("username", username);
		ParseObject userObj=queryUser.getFirst();
		return userObj;
	}
}
