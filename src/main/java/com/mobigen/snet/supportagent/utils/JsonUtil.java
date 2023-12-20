package com.mobigen.snet.supportagent.utils;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class JsonUtil {
    public HashMap<String, Object> convertMap(String jsonString){
           JsonObject param = null;
           try{
               param = JsonParser.parseString(jsonString).getAsJsonObject();
               if (param.isJsonNull()) return null;
           }catch (Exception e){
               return null;
           }

           HashMap<String, Object> returnData = new HashMap<>();
           Iterator it = param.keySet().iterator();

           while (it.hasNext()){
               String key = (String)it.next();
               Object object = param.get(key);

               //null 체크
               if(object == null || object instanceof JsonNull){
                   continue;
               }

               if(object instanceof JsonArray){
                   JsonArray jsonArray = (JsonArray)object;
                   jsonArrayToMap(key, jsonArray, returnData);
               }else if(object instanceof JsonObject){
                   //2 단계까지만 체크
                   HashMap<String, Object> mapData = new HashMap<>();
                   JsonObject obj = (JsonObject) object;
                   Iterator it2 = obj.keySet().iterator();
                   while (it2.hasNext()){
                       String key2 = (String)it2.next();
                       Object obj2 = obj.get(key2);

                       if(obj2 instanceof JsonNull){
                           continue;
                       }

                       if(obj2 instanceof JsonArray){
                           JsonArray jsonArray = (JsonArray)obj2;
                           jsonArrayToMap(key2, jsonArray, mapData);
                       }else if(obj2 instanceof JsonObject){
                           HashMap<String, Object> mapObject = new Gson().fromJson((JsonObject)((JsonObject) obj2).deepCopy(), HashMap.class);
                           mapData.put(key2, mapObject);
                       }else {
                           if(obj2 instanceof JsonPrimitive){
                               JsonPrimitive jsonPrimitive = (JsonPrimitive)obj2;
                               if(jsonPrimitive != null){
   //                                if(jsonPrimitive.isBoolean()){
   //                                    mapData.put(key2, jsonPrimitive.getAsBoolean());
   //                                }else if(jsonPrimitive.isNumber()){
   //                                    mapData.put(key2, jsonPrimitive.getAsNumber());
   //                                }else {
   //                                    mapData.put(key2, jsonPrimitive.getAsString());
   //                                }
                                   mapData.put(key2, jsonPrimitive.getAsString());
                               }
                           }else {
                               mapData.put(key2, obj2);
                           }
                       }
                   }
                   returnData.put(key, mapData);
               }else {
                   //string or object
                   if(object instanceof JsonPrimitive){
                       JsonPrimitive jsonPrimitive = (JsonPrimitive)object;
                       returnData.put(key, jsonPrimitive.getAsString());
                   }else{
                       returnData.put(key, object);
                   }
               }
           }
           return returnData;
       }

       private HashMap<String, Object> jsonArrayToMap(String key, JsonArray jsonArray, HashMap<String, Object> returnData){
           if(jsonArray != null && !jsonArray.isJsonNull()){
               List list = new ArrayList<>();
               for (int i=0; i<jsonArray.size(); i++){
                   JsonElement element = jsonArray.get(i);
                   if(element instanceof JsonNull){
                       continue;
                   }else if(element instanceof JsonArray) {
                       List list2 = new ArrayList();
                       JsonArray jsonArray2 = (JsonArray)element;
                       for (int j=0; j<jsonArray2.size(); j++){
                           if(jsonArray2.get(j) != null && !jsonArray2.get(j).isJsonNull()){
                               list2.add(jsonArray2.get(j).getAsString());
                           }else {
                               list2.add("");
                           }
                       }
                       list.add(list2);
                   }else if(element instanceof JsonPrimitive){
                       list.add(element.getAsString());
                   }else {
                       HashMap<String, Object> mapObject = new Gson().fromJson(element, HashMap.class);
                       list.add(mapObject);
                   }
               }
               returnData.put(key, list);
           }
           return returnData;
       }
}
