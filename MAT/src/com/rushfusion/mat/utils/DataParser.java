package com.rushfusion.mat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.util.Log;
public class DataParser {
	private String CATEGORY_URL = "http://tvsrv.webhop.net:9061/" ;
	private static final String TAG = "DataParser" ;
	private static String mSource ;
	private static DataParser parser;
	private static Activity mContext;
	private Map<String, String> url = null ;
	private int total;
	private DataParser() {
	}
	
	public static DataParser getInstance(Activity context, String source){
		mSource = source ;
		if(parser==null){
			mContext = context;
			parser = new DataParser();
		}
		return parser;
	}
	
	public List<String> getCategory() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = CATEGORY_URL+"category?source=" + mSource ;
		Log.d(TAG, "category url:"+strUrl) ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}

	public List<Map<String,String>> getSource() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = CATEGORY_URL+"source" ;
		Log.d(TAG, "Source url:"+strUrl) ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadSource(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}
	
	
	public List<String> getTypes(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = CATEGORY_URL+ "type?source=" + mSource + "&category=" + category ;
		Log.d(TAG, "type url:"+strUrl) ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}

	public List<String> getYears(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = CATEGORY_URL+ "year?source="  + mSource + "&category=" + category ;
		Log.d(TAG, "year url:"+strUrl) ;
		if(httpUtil.connectServerByURL(strUrl)) {
			List<String> list = new LinkedList<String>() ;
			List<String> listStr = loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
			if(listStr!=null) {
				Object[] str = listStr.toArray() ;
				for(int i=str.length-1; i>=0; i--) {
					list.add(str[i].toString()) ;
				}
				return list ;
			}
		}
		return null;
	}

	public List<String> getAreas(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = CATEGORY_URL+ "area?source="  + mSource + "&category=" + category ;
		Log.d(TAG, "area url:"+strUrl) ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}
	
	public List<Map<String,String>> get(String url) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext) ;
		String strUrl = url ;
		if(httpUtil.connectServerByURL(strUrl)) {
			return loadFileData(httpUtil.getInputStreamFromUrl(strUrl)) ;
		}
		return null;
	}
	
	
	public List<Map<String,String>> loadSource(InputStream inputSteam) {
		List<Map<String,String>> sourceList = null ;
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
			Log.d("JsonArray", itemsArray.toString()) ;
			sourceList = new ArrayList<Map<String,String>>() ;
			//dataList.add(total) ;
			for(int i=0; i<itemsArray.length(); i++) {
				JSONObject obj = new JSONObject(itemsArray.getString(i)) ;
				 Map<String,String> sourceMap = new HashMap<String, String>() ;
				 sourceMap.put("source",obj.get("source").toString()) ;
				 sourceMap.put("logo",obj.get("logo").toString()) ;
				 sourceMap.put("name",obj.get("name").toString()) ;
				 sourceList.add(sourceMap) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "IOException:"+e.getStackTrace()+"") ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "JSONException"+e.getStackTrace()+"") ;
		}
		return sourceList;
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
			//dataList.add(total) ;
			for(int i=0; i<itemsArray.length(); i++) {
				dataList.add(itemsArray.getString(i)) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "IOException:"+e.getStackTrace()+"") ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "JSONException"+e.getStackTrace()+"") ;
		}
		return dataList;
	}
	
	
	public List<Map<String,String>> loadFileData(InputStream inputSteam) {
		String[] PROPERTIES = {"total","score","comment","artists","name","area","play","count","length","recent","year","directors","thumb","url","type","id","description","category"} ;
		Map<String, String> nodeMap = null ;
		if(inputSteam==null)return null;
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>() ;
		try {
			StringBuilder builder = new StringBuilder() ;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputSteam)) ;
			for(String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
				builder.append(s) ;
			}
			Log.d("JSON", builder.toString()) ;
			JSONObject jsonObject = new JSONObject(builder.toString()) ;
			total = jsonObject.getInt("total");
			JSONArray itemsArray = jsonObject.getJSONArray("items") ;
			for(int i=0; i<itemsArray.length(); i++) {
				nodeMap = new HashMap<String, String>() ;
				JSONObject object = itemsArray.getJSONObject(i) ;
				for(int j=0; j<PROPERTIES.length; j++) {
					String str = object.getString(PROPERTIES[j]) ;
					nodeMap.put(PROPERTIES[j], str) ;
				}
				dataList.add(nodeMap) ;
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
	
	public int getTotal() {
		return total;
	}

	
}
