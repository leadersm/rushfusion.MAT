package com.rushfusion.mat.video.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.rushfusion.mat.video.entity.Movie;


public class TestDB {

	static Context mContext ;
	static TestDB test;
	
	private TestDB(){
//		getLevel2(MatDBManager.TELEPLAY) ;
//        queryAllMovie(MatDBManager.MOVIE, 0, 6) ;
//        queryAllTeleplay(MatDBManager.TELEPLAY, 0, 6) ;
//        queryAllByYear(MatDBManager.MOVIE, 2010, 0, 6) ;
//        queryAllByType(MatDBManager.MOVIE, "动作", 0, 6) ;
//        queryAllByYearAndType(MatDBManager.MOVIE, "动作", 2010, 0, 6) ;  //查询电影年份为2010年的动作片
	}
	public static TestDB getInstance(Context context){
		mContext = context;
		if(test==null){
			test = new TestDB();
		}
		return test;
	}
	
	public List<String> getLevel2(String level1Name) {
		MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
		dbManager.openDatabase() ;
		List<String> types = dbManager.getTypes(level1Name) ;
		Log.d("getLevel2", types.toString()) ;
		dbManager.closeDatabase();
		return types;
	}

	public List<String> getLevel1(String origin) {
//    	MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
//    	dbManager.openDatabase() ;
//    	List<String> types = dbManager.getTypes(origin) ;
//    	return types;
		
		List<String> types = new ArrayList<String>();
		types.add("首页");
		types.add("电影");
		types.add("电视剧");
		types.add("综艺");
		types.add("动漫");
		
		types.add("……");
//		dbManager.closeDatabase();
		return types; 
    }
	
    public void queryAllTeleplay(String name, int currentPage, int count) {
    	MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAll(name,currentPage,count) ;
    	Log.d("queryAllTeleplay", movies.toString()) ;
		dbManager.closeDatabase();
	}

	public List<Movie> queryAllMovie(String name, int currentPage, int count) {
    	MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAll(name,currentPage,count) ;
    	Log.d("queryAllMovie", movies.toString()) ;
    	dbManager.closeDatabase();
    	return movies;
    }
	
	
	public void queryAllByYear(String name, int year, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByYear(name, year, currentPage, count) ;
    	Log.d("queryAllByYear", movies.toString()) ;
    	dbManager.closeDatabase();
	}
	
	public void queryAllByType(String name, String type, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByType(name, type, currentPage, count) ;
    	Log.d("queryAllByType", movies.toString()) ;
    	dbManager.closeDatabase();
	}
	
	public void queryAllByYearAndType(String name, String type, int year, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByYearAndType(name, type, year, currentPage, count) ;
    	Log.d("queryAllByYearAndType", movies.toString()) ;
    	dbManager.closeDatabase();
	}
}
