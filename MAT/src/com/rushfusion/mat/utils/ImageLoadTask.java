package com.rushfusion.mat.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageLoadTask {
	public static final String TAG = "HttpUtil" ;
	private static int NETWORK_CONNECT_TIMEOUT = 20000;
	private static int NETWORK_SO_TIMEOUT = 20000;
	private boolean falg = true ;
	
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
			mHttpGet.abort();
			return null;
		} catch (Exception e) {
			Log.i(TAG, "Exception : " + e);
			mHttpGet.abort();
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
			mHttpGet.abort() ;
			return null;
		}catch(Exception e){
			Log.i(TAG, "Exception : " + e);
			mHttpGet.abort() ;
			return null;
		}
	}
	
	public static void imageLoad(final ImageView view,String url){
		final String  mUrl = url;
		final ImageView mView= view;
		new Thread(new Runnable(){
			public void run() {
				Log.d("loadImage","murl:"+mUrl);
				final Bitmap bmp = loadBitmap(mUrl);
				if(bmp !=null && !bmp.isRecycled()){
					mView.post(new Runnable(){
						public void run() {
							mView.setImageBitmap(bmp);
							Log.d("loadImage","setImageDrawable");
						}});
				}
			}}).start();
	}
	
	static Map<String,Bitmap> urlMap = new HashMap<String, Bitmap>() ;
	public static void imageLoad1(ImageView view,String url){
		final String  mUrl = url;
		final ImageView mView= view;
		new Thread(new Runnable(){
			public void run() {
				Log.d("loadImage","murl:"+mUrl);
				if(!urlMap.isEmpty()) {
					urlMap.clear() ;
				}
				final Bitmap bmp = loadBitmap(mUrl);
				urlMap.put(mUrl, bmp) ;
				if(bmp !=null){
					mView.post(new Runnable(){
						public void run() {
							mView.setImageBitmap(urlMap.get(mUrl));
							Log.d("loadImage","setImageDrawable");
						}});
				}
			}}).start();
	}
	
	public static String IMAGE_URL_KEY = "_url_";
	private static String getUrlFromViewTag(View view){
		String url = null;
		Object ob = view.getTag();
		if(ob != null){
			if(ob instanceof String){
				url = (String)ob;
			}else if(ob instanceof HashMap){
				url = (String)((HashMap<String,Object>)ob).get(IMAGE_URL_KEY);
			}
		}
		return url;
	}
	
	public static void loadImageLimited(final ImageView view,final String url){
		loadImageLimited(view,url,null); 
	}
	
	static int nMaxThreadNum = 6;
	static int nThreadLoadImage = 0;
	static Object mLoadImageLock = new Object();
	
	public static void loadImageLimited(final ImageView view,final String url,final ImageViewCallback cb){

		final String  mUrl = url;
		final ImageView mView= view;

		if(url == null)
			return;

		synchronized(mLoadImageLock) {
			if(nThreadLoadImage > nMaxThreadNum){
				Log.d("TAG","loadImageLimited postDelayed this request. url:"+mUrl);
				view.postDelayed(new Runnable(){
					@Override
					public void run() {
						loadImageLimited(view,url,cb);
					}},2000);
				return;
			}
		}
		
		new Thread(new Runnable(){
			public void run() {

				Boolean loop = true;
				int try_time = 20;
				while(loop){
					synchronized(mLoadImageLock) {
						if(nThreadLoadImage < nMaxThreadNum){
							loop = false;
							break;
						}
					}
					try_time --;
					if(try_time == 0){
						break;
					}
	                try {
						Log.d("TAG","loadImageLimited checking nThread:"+nThreadLoadImage + " v:"+mView.getVisibility()+"url:"+mUrl);
						String attachedUrl = getUrlFromViewTag(mView);
						if(!mUrl.equals(attachedUrl)){
							Log.d("TAG","loadImageLimited the url is outtime. oldUrl:"+mUrl+" newUrl:" + url);
							return;
						}
	                    Thread.sleep(500);
	                    
	                } catch (InterruptedException e) {
	                }
				}

				String attachedUrl = getUrlFromViewTag(mView);
				if(!mUrl.equals(attachedUrl)){
					return;
				}

				Log.d("TAG","loadImageLimited begin load nThread:"+nThreadLoadImage + " v:"+mView.getVisibility());

				synchronized(mLoadImageLock) {
					nThreadLoadImage ++;
				}
				try {
					final Bitmap bmp = loadBitmap(mUrl);
					Log.d("TAG","loadImageLimited loading the image murl:" + mUrl);
					if(bmp !=null&&!bmp.isRecycled()){
						attachedUrl = getUrlFromViewTag(mView);
						if(mUrl.equals(attachedUrl)){
							mView.post(new Runnable(){
								public void run() {
									/* to see whether the activity is show ? */
									if(cb != null){
										cb.callbak(mView, mUrl, bmp);
									}else{
										if (mView.getWindowVisibility() == View.VISIBLE) {
											if(!bmp.isRecycled())mView.setImageBitmap(bmp);
											Log.d("TAG","loadImageLimited update image v:" + mView + "from:"+mUrl);
										} else {
											Log.d("TAG","loadImageLimited update image cancled");
										}
									}
								}});
						}else{
							Log.d("TAG","loadImageLimited error 1 url:" + mUrl);
						}
					}else{
						Log.d("TAG","loadImageLimited error 3 url:" + mUrl);
					}
				}catch(Exception e2){
					Log.d("TAG","loadImageLimited error 4 url:"+mUrl);
					e2.printStackTrace();
				}finally{
					synchronized(mLoadImageLock) {
						nThreadLoadImage --;
						Log.d("TAG","finally method execute....");
					}
				}
			}}).start();
	}
	
	private static final int LOAD_IMAGE_TASK = 10 ;
	private static final int LOAD_IMAGEVIEW_TASK = LOAD_IMAGE_TASK + 10 ;
	private Handler handler ;
	public ImageLoadTask() {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if(msg.what == LOAD_IMAGE_TASK) {
					Bitmap bm = (Bitmap)msg.obj ;
					Log.d("mImageView", mImageView+"") ;
					mCallback.callbak(mImageView, bm) ;
				}
				if(msg.what == LOAD_IMAGEVIEW_TASK) {
					String imageUrl = (String)msg.obj ;
					//Log.d("mImageView", mImageView+"") ;
					Bitmap bm = Cache.mHardBitmapCache.get(imageUrl) ;
					if(bm!=null)
						mCallback.callbak(imageMap.get(imageUrl), bm) ;
				}
			};
		} ;
	}
	
	private ImageViewCallback1 mCallback ;
	private ImageView mImageView ;
	public void loadImage(ImageView imageView, final String imageUrl, ImageViewCallback1 callback) {
		Log.d("uuu", imageView+"") ;
		this.mCallback = callback ;
		this.mImageView = imageView ;
		//Cache.checkCachIsRelease() ;
		new Thread(){
			public void run() {
				Bitmap bitmap = loadBitmap(imageUrl) ;
				Log.d("ImageLoadTask", bitmap+"") ;
				if(Cache.mHardBitmapCache!=null) {
					Cache.mHardBitmapCache.put(imageUrl, bitmap) ;
				}
				Message msg = new Message() ;
				msg.obj = bitmap ;
				msg.what = LOAD_IMAGE_TASK ;
				handler.sendMessage(msg) ;
			};
		}.start() ;
	}
	
	private Map<String, ImageView> imageMap = new HashMap<String, ImageView>() ;
	public void loadImage1(ImageView imageView, final String imageUrl, ImageViewCallback1 callback) {
		Log.d("uuu", imageView+"") ;
		this.mCallback = callback ;
		//this.mImageView = imageView ;
		imageMap.put(imageUrl, imageView) ;
		new Thread(){
			public void run() {
				Bitmap bitmap = loadBitmap(imageUrl) ;
				Cache.mHardBitmapCache.put(imageUrl, bitmap) ;
				Message msg = new Message() ;
				msg.obj = imageUrl ;
				msg.what = LOAD_IMAGEVIEW_TASK ;
				handler.sendMessage(msg) ;
			};
		}.start() ;
	}
	
	public interface  ImageViewCallback1 {
		public void callbak(ImageView view,Bitmap bm);
	};
	
	public interface  ImageViewCallback {
		public void callbak(ImageView view,String url,Bitmap bm);
	};
	
	
	
	
	
	
	
	
	
	
	
	
}
