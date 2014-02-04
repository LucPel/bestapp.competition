package com.ats.bestapp.savefoods.transformer;

import java.util.HashMap;

import com.ats.bestapp.savefoods.Constants;
import com.ats.bestapp.savefoods.data.User;

public class UserTransformer {

	public User createUser(HashMap<String, Object> commonsData){
		User user=new User();
		user.setUsername((String) commonsData.get(Constants.userNameSP));
		user.setNickname((String) commonsData.get(Constants.userNameSP));
		return user;
	}
}
