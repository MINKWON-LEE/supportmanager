/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.utils
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 26.
 * description : 
 */
package com.mobigen.snet.supportagent.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * @author Hyeon-sik Jung
 *
 */
public class XmlToObjectConverter {
	
	static Logger logger = LoggerFactory.getLogger(XmlToObjectConverter.class);
	
	public static Object xmlToObject(Object object, String filePath) {
		
		try {
			XMLSerializer xmlSerializer = new XMLSerializer();
			File file = new File(filePath);
			
			if(file.isFile()){
				JSONObject jsonObject = (JSONObject) xmlSerializer.readFromFile(file);
				
				String jsonStr = jsonObject.toString();
				String replace = StringUtils.replace(jsonStr, "@", "");
				logger.debug(replace);
				Gson gson = new Gson();
				object = gson.fromJson(replace, object.getClass());
				
			}else{
				throw new Exception("Xml File Not found Exception!");
			}
		} catch (Exception e) {
			logger.error("XmlToObject Coverter Exception :: {}", e.getMessage(), e.getCause());
		}
		return object;
	}
	
	public static JsonElement xmlToJsonOjbect(String filePath){
		JsonElement json = null;
		try {
			XMLSerializer xmlSerializer = new XMLSerializer();
			File file = new File(filePath);
			
			if(file.isFile()){
				JSONObject jsonObject = (JSONObject) xmlSerializer.readFromFile(file);
				
				String jsonStr = jsonObject.toString();
				String replace = StringUtils.replace(jsonStr, "@", "");
//				logger.debug(replace);
				Gson gson = new Gson();
				json = gson.fromJson(replace, JsonElement.class);
//				object = gson.fromJson(replace, object.getClass());
			}else{
				throw new Exception("Xml File Not found Exception!");
			}
		} catch (Exception e) {
			logger.error("XmlToObject Coverter Exception :: {}", e.getMessage(), e.getCause());
		}
		
		return json;
	}
}
