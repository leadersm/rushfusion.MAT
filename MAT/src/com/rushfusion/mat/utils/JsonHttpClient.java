package com.rushfusion.mat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class JsonHttpClient {
	public static final String category_url = "" ;
	public static final String type_url = "" ;
	public static final String area_url = "" ;
	public static final String year_url = "" ;
	private HttpResponse mHttpResponse ;
	private static JsonHttpClient jsonHttpClient = null ;
	static Map<String,String> urlMap = null ;
	private JsonHttpClient() {
		if(urlMap==null) {
			initUrl() ;
		}
	}
	
	public static JsonHttpClient getInstance() {
		if(jsonHttpClient==null) {
			jsonHttpClient = new JsonHttpClient() ;
		}
		return jsonHttpClient ;
	}
	
	public static void initUrl() {
		if(urlMap==null) {
			urlMap = new HashMap<String, String>() ;
		}
		urlMap.put("simple", "http://10.0.2.2:8080/rushfusion/json/simple.json") ;
		urlMap.put("complex", "http://10.0.2.2:8080/rushfusion/json/complex.json") ;
	}


	public boolean connectServerByURL(String simple) {
		HttpGet httpRequest = new HttpGet(urlMap.get(simple)) ;
		try {
			HttpClient httpClient = new DefaultHttpClient() ;
			HttpResponse httpResponse= httpClient.execute(httpRequest) ;
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				mHttpResponse = httpResponse ;
				return true ;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<?> get(String simple) {
		InputStream inputSteam = getInputStreamFromUrl(urlMap.get(simple),simple) ;
		ParseJson parseJson = new ParseJson() ;
		//List<ParseJson.PageInf> userList = parseJson.getDownLoadJson(inputSteam) ;
		return null;
	}

	private InputStream getInputStreamFromUrl(String string, String simple) {
		InputStream inputStream = null ;
		try {
			if(connectServerByURL(simple)) {
				HttpEntity entity = mHttpResponse.getEntity() ;
				inputStream = entity.getContent() ;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
	
}
