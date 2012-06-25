package com.rushfusion.mat.utils;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class HttpUtil {
	public static final String TAG = "HttpUtil" ;
	private static int NETWORK_CONNECT_TIMEOUT = 20000;
	private static int NETWORK_SO_TIMEOUT = 20000;
	
	public static Bitmap loadBitmap(String url) {
		Log.d(TAG, "loadbitmap url:" + url);
		if (url == null || "".equals(url)) {
			return null;
		}

		HttpParams p = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(p, NETWORK_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(p, NETWORK_SO_TIMEOUT);

		DefaultHttpClient mHttpClient = new DefaultHttpClient(p);

		HttpGet mHttpGet = null;
		try {
			mHttpGet = new HttpGet(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		InputStream inputStream = null;
		HttpEntity resEntity = null;
		try {
			HttpResponse response = mHttpClient.execute(mHttpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.d(TAG, "loadbitmap statusCode:" + statusCode);
				return null;
			}
			resEntity = response.getEntity();
			inputStream = resEntity.getContent();

			Bitmap bmp = BitmapFactory.decodeStream(inputStream);
			inputStream.close();

			if (bmp == null) {
				Log.d(TAG,
						"BitmapFactory.decodeStream error,try to use other method.");
				return loadBitmap1(url);
			} else
				return bmp;
		} catch (OutOfMemoryError oe) {
			Log.i(TAG, "OutOfMemoryError : " + oe);
			return null;
		} catch (Exception e) {
			Log.i(TAG, "Exception : " + e);
			return null;
		}
	}
	
	public static Bitmap loadBitmap1(String url){
		if(url == null || "".equals(url)){
			return null;
		}
		
		HttpParams p = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(p,NETWORK_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(p,NETWORK_SO_TIMEOUT);
	
		DefaultHttpClient mHttpClient = new DefaultHttpClient(p);

		HttpGet mHttpGet = null;
		try{
			mHttpGet= new HttpGet(url);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		InputStream inputStream = null;
		HttpEntity resEntity = null; 
		try{
			HttpResponse response = mHttpClient.execute(mHttpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			resEntity = response.getEntity();
			inputStream = resEntity.getContent();
			int length = (int)resEntity.getContentLength();
			if(length<=0) return null;
			byte[] buffer = new byte[length+4096];
			
			if(buffer == null){
				inputStream.close();
				return null;
			}
			
			int n;
			int rlength = 0;
			while ((n = inputStream.read(buffer,rlength,40960)) >= 0) {
				rlength += n;
				if(rlength > length){
					buffer = null;
					inputStream.close();
					return null;
				}
			}
			inputStream.close();
			Bitmap bmp = BitmapFactory.decodeByteArray(buffer,0,rlength);
			buffer=null;
			return bmp;
			
		}catch(OutOfMemoryError oe){
			Log.i(TAG, "OutOfMemoryError : " + oe);
			return null;
		}catch(Exception e){
			Log.i(TAG, "Exception : " + e);
			return null;
		}
	}
	
	public static void imageLoad(ImageView view,String url){
		final String  mUrl = url;
		final ImageView mView= view;
		new Thread(new Runnable(){
			public void run() {
				Log.d("loadImage","murl:"+mUrl);
				final Bitmap bmp = loadBitmap(mUrl);
				if(bmp !=null){
					mView.post(new Runnable(){
						public void run() {
							mView.setImageBitmap(bmp);
							Log.d("loadImage","setImageDrawable");
						}});
				}
			}}).start();
	}
}
