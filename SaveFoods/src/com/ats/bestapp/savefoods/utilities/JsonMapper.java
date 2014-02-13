package com.ats.bestapp.savefoods.utilities;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonMapper {

	static public String convertObject2String(Object object){
		String jsonObj=null;
		try {
			jsonObj=new ObjectMapper().writeValueAsString(object);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	static public Object convertString2Object(String objString,Class classToConvert) throws JsonParseException, JsonMappingException, IOException{
		Object convertedObject=null;
		convertedObject=new ObjectMapper().readValue(objString, classToConvert);
		return convertedObject;
	}
}
