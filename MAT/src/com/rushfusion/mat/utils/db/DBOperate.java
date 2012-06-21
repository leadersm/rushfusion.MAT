package com.rushfusion.mat.utils.db;

import java.util.ArrayList;
import java.util.List;

import com.rushfusion.mat.bean.Movie;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBOperate {
	
	/**
	 * 查询所有电影
	 * @param database
	 * @param currentPage 当前第几页
	 * @param count 每页显示多少条
	 * @return
	 */
	public List<Movie> queryAllMovie(SQLiteDatabase database, int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = 'movie' limit "+startCount+","+endCount, null);
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
	 * 查询所有电视剧
	 * @param database
	 * @param currentPage 当前第几页
	 * @param count 每页显示多少条
	 * @return
	 */
	public List<Movie> queryAllTeleplay(SQLiteDatabase database, int currentPage, int count) {
		int startCount = currentPage * count ;
		int endCount = (currentPage+1) * count;
		Log.d("limit++++++", "startCount="+startCount+";endCount="+endCount) ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie where category = 'teleplay' limit "+startCount+","+endCount, null);
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
