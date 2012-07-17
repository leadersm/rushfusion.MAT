package com.rushfusion.mat.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

public class Cache {
	public static int HARD_CACHE_CAPACITY = 30;
	public final static ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
	public final static HashMap<String, Bitmap> mHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				mSoftBitmapCache.put(eldest.getKey(),new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else {
				return false;
			}
		}

	};
	
	public static Cache cache = null ;
	private Cache() {}
	
	public static Cache newInstance() {
		if(cache==null) {
			cache = new Cache() ;
		}
		return cache ;
	}
	
	public static Bitmap getBitmapFromCache(String url) { 
        synchronized (mHardBitmapCache) { 
            final Bitmap bitmap =mHardBitmapCache.get(url); 
            if (bitmap != null) { 
                mHardBitmapCache.remove(url); 
                mHardBitmapCache.put(url,bitmap); 
                return bitmap; 
            } 
        } 
        SoftReference<Bitmap>bitmapReference = mSoftBitmapCache.get(url); 
        if (bitmapReference != null) { 
            final Bitmap bitmap =bitmapReference.get(); 
            if (bitmap != null) { 
                return bitmap; 
            } else { 
                mSoftBitmapCache.remove(url); 
            } 
        } 
        return null; 
    } 
	
	
}
