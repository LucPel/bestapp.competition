package com.ats.bestapp.savefoods.trasformer;

import java.util.HashMap;

import android.util.Log;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.parse.ParseObject;

public class UserTransformer {

	private static final String logTag="UserTransformer";
	
	public User createUser(HashMap<String, Object> commonsData){
		User user=new User();
		user.setUsername((String) commonsData.get(Constants.userNameSP));
		user.setNickname((String) commonsData.get(Constants.userNameSP));
		return user;
	}
	
	public User trasformUserFromParseObject(ParseObject parseObject){
		User user=new User();
		Log.d(logTag,JsonMapper.convertObject2String(parseObject));
		user.setUserId(parseObject.getObjectId());
		user.setUsername(parseObject.getString(Constants.usernamePO));
		user.setNickname(parseObject.getString(Constants.nicknamePO));
		return user;
	}
	
	public ParseObject createParseObjectUser(User user){
		ParseObject userObj = new ParseObject(Constants.userObject);
		userObj.put(Constants.nicknamePO, user.getNickname());
		userObj.put(Constants.usernamePO, user.getUsername());
		return userObj;
	}
}
