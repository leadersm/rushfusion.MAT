package com.rushfusion.mat.video.db;

import java.util.List;

import com.rushfusion.mat.video.entity.Movie;

import android.content.Context;
import android.util.Log;


public class TestDB {

	Context context ;
	
	public TestDB(){
		getArea(MatDBManager.MOVIE) ;
        queryAllMovie(MatDBManager.MOVIE, 0, 6) ;
        queryAllTeleplay(MatDBManager.TELEPLAY, 0, 6) ;
        queryAllByYear(MatDBManager.MOVIE, 2010, 0, 6) ;
        queryAllByType(MatDBManager.MOVIE, "动作", 0, 6) ;
        queryAllByYearAndType(MatDBManager.MOVIE, "动作", 2010, 0, 6) ;  //查询电影年份为2010年的动作片
	}
	
	public void getArea(String category) {
    	MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<String> types = dbManager.getArea(category) ;
    	Log.d("电影", types.toString()) ;
    }
	
    private void queryAllTeleplay(String name, int currentPage, int count) {
    	MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAll(name,currentPage,count) ;
    	Log.d("电影", movies.toString()) ;
	}

	public void queryAllMovie(String name, int currentPage, int count) {
    	MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAll(name,currentPage,count) ;
    	Log.d("电影", movies.toString()) ;
    }
	
	
	public void queryAllByYear(String name, int year, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByYear(name, year, currentPage, count) ;
    	Log.d("电影", movies.toString()) ;
	}
	
	public void queryAllByType(String name, String type, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByType(name, type, currentPage, count) ;
    	Log.d("电影", movies.toString()) ;
	}
	
	public void queryAllByYearAndType(String name, String type, int year, int currentPage, int count) {
		MatDBManager dbManager = MatDBManager.getInstance(context) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllByYearAndType(name, type, year, currentPage, count) ;
    	Log.d("电影", movies.toString()) ;
	}
}
