package com.rushfusion.mat.movie.message;

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

import android.util.Log;

public class XmlHttpClient {
	public static final String PATH = "http://192.168.1.20/samba/test/cheng/path.xml" ;
	private HttpResponse mHttpResponse ;
	static Map<String,String> urlMap = null ;
	static private XmlHttpClient xmlHttpClient ;
	private XmlHttpClient() {
	}
	
	public static XmlHttpClient getInstance() {
		if(xmlHttpClient==null) {
			xmlHttpClient = new XmlHttpClient() ;
		}
		return xmlHttpClient ;
	}
	
	public boolean connectServerByURL(String url) {
		HttpGet httpRequest = new HttpGet(url) ;
		try {
			HttpClient httpClient = new DefaultHttpClient() ;
			HttpResponse httpResponse = httpClient.execute(httpRequest) ;
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				mHttpResponse = httpResponse ;
				return true ;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false ;
	}

	public List<?> get(String url) {
		Log.d("xLog", "request url:"+url) ;
		InputStream inputStream = getInputSreamFromUrl(url) ;
		Log.d("xLog", "inputStream:"+inputStream) ;
		PullParserXml handler = new PullParserXml();
		 List<String> userList = handler.getDownLoadUrlPortal(inputStream) ;
		return userList ;
	}
	
	private InputStream getInputSreamFromUrl(String url) {
		InputStream is = null;
		if(connectServerByURL(url)) {
			HttpEntity entity = mHttpResponse.getEntity() ;
			try {
				is = entity.getContent() ;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return is ;
	}
}
