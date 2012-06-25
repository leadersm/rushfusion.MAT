package com.rushfusion.mat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class DataParser {
	
	private static String mSource ;
	private static DataParser parser;
	private static Context mContext;
	private Map<String, String> url = null ;
	private DataParser() {
		if(url==null)
			initUrl() ;
	}
	
	public static DataParser getInstance(Context context, String source){
		mSource = source ;
		if(parser==null){
			mContext = context;
			parser = new DataParser();
		}
		return parser;
	}
	
	private void initUrl() {
		if(url==null) {
			url = new HashMap<String, String>() ;
		}
		url.put("category", "http://tvsrv.webhop.net:9061/category?source="+mSource) ;
		url.put("type", "http://tvsrv.webhop.net:9061/type?source="+mSource+"&category=movie") ;
		url.put("year", "http://tvsrv.webhop.net:9061/year?source="+mSource+"&category=movie") ;
		url.put("area", "http://tvsrv.webhop.net:9061/area?source="+mSource+"&category=movie") ;
	}
	
	public List<String> getCategory() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = url.get("category") ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}

	public List<String> getTypes() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = url.get("type") ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}

	public List<String> getYears() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = url.get("year") ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}

	public List<String> getAreas() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = url.get("area") ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}
	
	public List<String> loadData(InputStream inputSteam) {
		List<String> dataList = null ;
		try {
			StringBuilder builder = new StringBuilder() ;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputSteam)) ;
			for(String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
				builder.append(s) ;
			}
			Log.d("JSON", builder.toString()) ;
			JSONObject jsonObject = new JSONObject(builder.toString()) ;
			String total = jsonObject.getString("total") ;
			JSONArray itemsArray = jsonObject.getJSONArray("items") ;
			dataList = new ArrayList<String>() ;
			dataList.add(total) ;
			for(int i=0; i<itemsArray.length(); i++) {
				dataList.add(itemsArray.getString(i)) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
}
