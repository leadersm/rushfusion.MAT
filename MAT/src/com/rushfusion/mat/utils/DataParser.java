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

import com.rushfusion.mat.video.entity.Movie;

public class DataParser {
	private String CATEGORY_URL = "http://tvsrv.webhop.net:9061/";
	private static final String TAG = "DataParser";
	private static String mSource;
	private static DataParser parser;
	private static Activity mContext;
	private Map<String, String> url = null;
	private int total;

	private DataParser() {
	}

	public static DataParser getInstance(Activity context, String source) {
		mSource = source;
		if (parser == null) {
			mContext = context;
			parser = new DataParser();
		}
		return parser;
	}

	public List<String> getCategory() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = CATEGORY_URL + "category?source=" + mSource;
		Log.d(TAG, "category url:" + strUrl);
		if (httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl));
		}
		return null;
	}

	public List<Map<String, String>> getSource() {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = CATEGORY_URL + "source";
		Log.d(TAG, "Source url:" + strUrl);
		if (httpUtil.connectServerByURL(strUrl)) {
			return loadSource(httpUtil.getInputStreamFromUrl(strUrl));
		}
		return null;
	}

	public List<String> getTypes(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = CATEGORY_URL + "type?source=" + mSource + "&category="
				+ category;
		Log.d(TAG, "type url:" + strUrl);
		if (httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl));
		}
		return null;
	}

	public List<String> getYears(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = CATEGORY_URL + "year?source=" + mSource + "&category="
				+ category;
		Log.d(TAG, "year url:" + strUrl);
		if (httpUtil.connectServerByURL(strUrl)) {
			List<String> list = new LinkedList<String>();
			List<String> listStr = loadData(httpUtil
					.getInputStreamFromUrl(strUrl));
			if (listStr != null) {
				Object[] str = listStr.toArray();
				for (int i = str.length - 1; i >= 0; i--) {
					list.add(str[i].toString());
				}
				return list;
			}
		}
		return null;
	}

	public List<String> getAreas(String category) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = CATEGORY_URL + "area?source=" + mSource + "&category="
				+ category;
		Log.d(TAG, "area url:" + strUrl);
		if (httpUtil.connectServerByURL(strUrl)) {
			return loadData(httpUtil.getInputStreamFromUrl(strUrl));
		}
		return null;
	}

	public List<Movie> getMovies(String url) {
		HttpUtil httpUtil = HttpUtil.getInstance(mContext);
		String strUrl = url;
		if (httpUtil.connectServerByURL(strUrl)) {
			return loadFileData(httpUtil.getInputStreamFromUrl(strUrl));
		}
		return null;
	}

	public List<Map<String, String>> loadSource(InputStream inputSteam) {
		List<Map<String, String>> sourceList = null;
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputSteam));
			for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
					.readLine()) {
				builder.append(s);
			}
			Log.d("JSON", builder.toString());
			JSONObject jsonObject = new JSONObject(builder.toString());
			String total = jsonObject.getString("total");
			JSONArray itemsArray = jsonObject.getJSONArray("items");
			Log.d("JsonArray", itemsArray.toString());
			sourceList = new ArrayList<Map<String, String>>();
			// dataList.add(total) ;
			for (int i = 0; i < itemsArray.length(); i++) {
				JSONObject obj = new JSONObject(itemsArray.getString(i));
				Map<String, String> sourceMap = new HashMap<String, String>();
				sourceMap.put("source", obj.get("source").toString());
				sourceMap.put("logo", obj.get("logo").toString());
				sourceMap.put("name", obj.get("name").toString());
				sourceList.add(sourceMap);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "IOException:" + e.getStackTrace() + "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "JSONException" + e.getStackTrace() + "");
		}
		return sourceList;
	}

	public List<String> loadData(InputStream inputSteam) {
		List<String> dataList = null;
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputSteam));
			for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
					.readLine()) {
				builder.append(s);
			}
			Log.d("JSON", builder.toString());
			JSONObject jsonObject = new JSONObject(builder.toString());
			String total = jsonObject.getString("total");
			JSONArray itemsArray = jsonObject.getJSONArray("items");
			dataList = new ArrayList<String>();
			// dataList.add(total) ;
			for (int i = 0; i < itemsArray.length(); i++) {
				dataList.add(itemsArray.getString(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "IOException:" + e.getStackTrace() + "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "JSONException" + e.getStackTrace() + "");
		}
		return dataList;
	}

	public List<Movie> loadFileData(InputStream inputSteam) {
		Movie movie;
		if (inputSteam == null)
			return null;
		List<Movie> dataList = new ArrayList<Movie>();
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputSteam));
			for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
					.readLine()) {
				builder.append(s);
			}
			Log.d("JSON", builder.toString());
			JSONObject obj = new JSONObject(builder.toString());
			total = obj.getInt("total");
			JSONArray itemsArray = obj.getJSONArray("items");
			for (int i = 0; i < itemsArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) itemsArray.get(i);
				int count = 0, total = 0, score = 0, comment = 0, year = 0, play = 0;
				String category = "", name = "", type = "", directors = "", artists = "", area = "", description = "", thumb = "", length = "", url = "", id = "";
				long recent = 0;

				if (!jsonObject.isNull("count"))
					count = jsonObject.getInt("count");
				if (!jsonObject.isNull("total"))
					total = jsonObject.getInt("total");
				if (!jsonObject.isNull("score"))
					score = jsonObject.getInt("score");
				if (!jsonObject.isNull("comment"))
					comment = jsonObject.getInt("comment");
				if (!jsonObject.isNull("category"))
					category = jsonObject.getString("category");
				if (!jsonObject.isNull("name"))
					name = jsonObject.getString("name");
				if (!jsonObject.isNull("type"))
					type = jsonObject.getString("type");
				if (!jsonObject.isNull("year"))
					year = jsonObject.getInt("year");
				if (!jsonObject.isNull("directors"))
					directors = jsonObject.getString("directors");
				if (!jsonObject.isNull("artists"))
					artists = jsonObject.getString("artists");
				if (!jsonObject.isNull("area"))
					area = jsonObject.getString("area");
				if (!jsonObject.isNull("description"))
					description = jsonObject.getString("description");
				if (!jsonObject.isNull("thumb"))
					thumb = jsonObject.getString("thumb");
				if (!jsonObject.isNull("length"))
					length = jsonObject.getString("length");
				if (!jsonObject.isNull("url"))
					url = jsonObject.getString("url");
				if (!jsonObject.isNull("play"))
					play = jsonObject.getInt("play");
				if (!jsonObject.isNull("id"))
					id = jsonObject.getString("id");
				if (!jsonObject.isNull("recent"))
					recent = jsonObject.getLong("recent");
				movie = new Movie(count, total, score, comment, category, name,
						type, year, directors, artists, area, description,
						thumb, length, url, play, id, recent);
				dataList.add(movie);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	public int getTotal() {
		return total;
	}

}
