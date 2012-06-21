package com.rushfusion.mat.page;

import java.util.HashMap;

public class PageCache{
	
	
	private static PageCache cache;
	private HashMap<Integer,BasePage> data = null;
	
	private PageCache(){
		data = new HashMap<Integer, BasePage>();
	}
	
	public static PageCache getInstance(){
		if(cache==null){
			cache = new PageCache();
		}
		return cache;
	}
	
	public void set(int key,BasePage value){
		data.put(key, value);
	}
	
	public BasePage get(int key){
		return data.get(key);
	}
	
	
	public void release(){
		data = null;
		cache = null;		
	}
}