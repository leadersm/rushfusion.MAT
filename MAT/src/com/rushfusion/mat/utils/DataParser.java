package com.rushfusion.mat.utils;

import java.util.List;

import android.content.Context;

public class DataParser {
	
	
	private static String mSource;
	private static DataParser parser;
	private static Context mContext;
	private DataParser() {
		
	}
	
	public static DataParser getInstance(Context context,String source){
		mSource = source;
		if(parser==null){
			mContext = context;
			parser = new DataParser();
		}
		return parser;
	}
	
	public List<String> getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getYears() {
		// TODO Auto-generated method stub
		return null;
		
	}

	public List<String> getAreas() {
		// TODO Auto-generated method stub
		return null;
	}

}
