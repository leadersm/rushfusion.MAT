package com.rushfusion.mat.video.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.rushfusion.mat.video.entity.Movie;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DBOperate {
	
	/**
	 * 查询所有
	 * @param database
	 * @param category 电影， 电视剧
	 * @param currentPage 当前页 从0开始
	 * @param count 每页显示多少条
	 * @return
	 */
	public List<Movie> queryAll(SQLiteDatabase database, String category, int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = '"+ category +"' limit "+startCount+","+endCount, null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), 
					c.getInt(5), c.getString(6), c.getString(7), c.getString(8), 
					c.getInt(9), c.getString(10), c.getString(11), c.getString(12), 
					c.getString(13), c.getString(14), c.getString(15), c.getString(16)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
	

	/**
	 * 按年份查 电视剧
	 * @param database
	 * @param currentPage
	 * @param count
	 * @return
	 */
	public List<Movie> queryByYear(SQLiteDatabase database, String category,
			int year, int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = '"+ category +"' and year = "+ year +" limit "+startCount+","+endCount, null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), 
					c.getInt(5), c.getString(6), c.getString(7), c.getString(8), 
					c.getInt(9), c.getString(10), c.getString(11), c.getString(12), 
					c.getString(13), c.getString(14), c.getString(15), c.getString(16)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
	
	/**
	 * 按照类型查
	 * @param database 
	 * @param category	电视剧，电影
	 * @param type	动作，喜剧
	 * @param year	2012
	 * @param currentPage 当前页 从0 开始
	 * @param count 每页显示多少条
	 * @return
	 */
	public List<Movie> queryByYearAndType(SQLiteDatabase database, String category,String type,
			int year, int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = '"+category+"' and year = "+ year +" and type like '%"+ type +"%'"+"limit "+startCount+","+endCount, null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), 
					c.getInt(5), c.getString(6), c.getString(7), c.getString(8), 
					c.getInt(9), c.getString(10), c.getString(11), c.getString(12), 
					c.getString(13), c.getString(14), c.getString(15), c.getString(16)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
	
	/**
	 * 
	 * @param database
	 * @param category
	 * @param type
	 * @param year
	 * @param currentPage
	 * @param count
	 * @return
	 */
	public List<Movie> queryByType(SQLiteDatabase database, String category,String type,
			int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = '"+category+"' and type like '%"+ type +"%'"+"limit "+startCount+","+endCount, null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), 
					c.getInt(5), c.getString(6), c.getString(7), c.getString(8), 
					c.getInt(9), c.getString(10), c.getString(11), c.getString(12), 
					c.getString(13), c.getString(14), c.getString(15), c.getString(16)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
	
	
	/**
	 * 
	 * @param database
	 * @param category  电影 或者 电视剧
	 * @return
	 */
	public List<String> getAllType(SQLiteDatabase database, String category) {
		List<String> types = new ArrayList<String>() ;
		Set<String> typeSet = new HashSet<String>() ;
		Cursor c = database.rawQuery("select type from movie where category = '"+ category +"' group by type", null);
		while(c.moveToNext()) {
			String str = c.getString(0) ;
			if(str.contains(";")) {
				String[] strs = str.split(";") ;
				for(int i=0; i<strs.length; i++) {
					typeSet.add(strs[i]) ;
				}
			} else {
				typeSet.add(str) ;
			}
		}
		for(Iterator<String> iterator = typeSet.iterator(); iterator.hasNext();) {
			types.add(iterator.next().toString()) ;
		}
		
		if (c != null && !c.isClosed())
			c.close();
		return types ;
	}
	
	/**
	 * 获取区域
	 * @param database
	 * @param category
	 * @return
	 */
	public List<String> getArea(SQLiteDatabase database, String category) {
		List<String> areas = new ArrayList<String>() ;
		Set<String> areaSet = new HashSet<String>() ;
		Cursor c = database.rawQuery("select area from movie where category = '"+ category +"'", null);
		while(c.moveToNext()) {
			areaSet.add(c.getString(0)) ;
		}
		for(Iterator<String> iterator = areaSet.iterator(); iterator.hasNext();) {
			areas.add(iterator.next().toString()) ;
		}
		
		if (c != null && !c.isClosed())
			c.close();
		return areas ;
	}
	
	public List<Movie> getRecommend(SQLiteDatabase database) {
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where year = 2012", null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), 
					c.getInt(5), c.getString(6), c.getString(7), c.getString(8), 
					c.getInt(9), c.getString(10), c.getString(11), c.getString(12), 
					c.getString(13), c.getString(14), c.getString(15), c.getString(16)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
	
	
}
