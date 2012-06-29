package com.rushfusion.mat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class HttpUtil {
	private HttpResponse mHttpResponse ;
	private static HttpUtil httpClient = null ;
	static Map<String,String> urlMap = null ;
	private static Activity mContext ;
	private HttpUtil() {
	}
	
	public static HttpUtil getInstance(Activity context) {
		mContext = context ;
		if(httpClient==null) {
			httpClient = new HttpUtil() ;
		}
		return httpClient ;
	}
	
	private static int NETWORK_CONNECT_TIMEOUT = 10000;
	private static int NETWORK_SO_TIMEOUT = 10000;
	/**
	 * 获取网络连接 200
	 * @param url
	 * @return
	 */
	public boolean connectServerByURL(String url) {
		HttpGet httpRequest = new HttpGet(url) ;
		try {
			HttpParams p = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(p, NETWORK_CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(p, NETWORK_SO_TIMEOUT);
			HttpClient httpClient = new DefaultHttpClient(p) ;
			
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
	
	
	public InputStream getInputStreamFromUrl1(String strUrl) {
		try {
			URL url=new URL(strUrl);
			HttpURLConnection http=(HttpURLConnection)url.openConnection();
			http.setConnectTimeout(NETWORK_CONNECT_TIMEOUT) ;
			int nRC=http.getResponseCode();
			InputStream is ;
			if(nRC==HttpURLConnection.HTTP_OK){
			    is = http.getInputStream();
			    return is ;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * 检查网络连接是否可用
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkEnabled(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if(nwi!=null){
			return nwi.isAvailable();
		}
		return false;
	}
	
}
