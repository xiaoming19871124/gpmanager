package com.txdb.gpmanage.core.utils;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JsonUtils {

	public static String toJson(Object object) {
		return toJson(object, false);
	}

	public static String toJson(Object object, boolean ignoreNullValue) {
		JSONObject jsonObject;
		if (ignoreNullValue)
			jsonObject = JSONObject.fromObject(object, getJsonConfig());
		else
			jsonObject = JSONObject.fromObject(object);
		return jsonObject.toString();
	}

	public static String toJsonArray(Object object) {
		return toJsonArray(object, false);
	}

	public static String toJsonArray(Object object, boolean ignoreNullValue) {
		JSONArray jsonArray;
		if (ignoreNullValue)
			jsonArray = JSONArray.fromObject(object, getJsonConfig());
		else
			jsonArray = JSONArray.fromObject(object);
		return jsonArray.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> toCollection(String json, Class<E> clazz) {
		JSONArray jsonArray = JSONArray.fromObject(json);
		return (List<E>) JSONArray.toCollection(jsonArray, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String json, Class<T> clazz) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		return (T) JSONObject.toBean(jsonObject, clazz);
	}

	private static JsonConfig getJsonConfig() {
		JsonConfig jsonConfig = new JsonConfig();
//		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return value == null;
			}
		});
		return jsonConfig;
	}
}
