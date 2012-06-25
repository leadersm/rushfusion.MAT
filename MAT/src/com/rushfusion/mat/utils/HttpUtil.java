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

import android.content.Context;


public class HttpUtil {
	private HttpResponse mHttpResponse ;
	private static HttpUtil httpClient = null ;
	static Map<String,String> urlMap = null ;
	private static Context mContext ;
	private HttpUtil() {
	}
	
	public static HttpUtil getInstance(Context context) {
		mContext = context ;
		if(httpClient==null) {
			httpClient = new HttpUtil() ;
		}
		return httpClient ;
	}
	
	/**
	 * 获取网络连接 200
	 * @param url
	 * @return
	 */
	public boolean connectServerByURL(String url) {
		HttpGet httpRequest = new HttpGet(url) ;
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

	/**
	 * getInputStream
	 * @param url
	 * @return
	 */
	public InputStream getInputStreamFromUrl(String url) {
		InputStream inputStream = null ;
		try {
			if(connectServerByURL(url)) {
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
