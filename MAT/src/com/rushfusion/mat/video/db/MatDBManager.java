package com.rushfusion.mat.video.db;

import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.rushfusion.mat.video.entity.Movie;

public class MatDBManager {
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" ;  
	public static final String DB_NAME = "sina_video.db"; 
	private static MatDBManager dbManager = null ;
	public static final String TELEPLAY = "teleplay" ;	//电视剧
	public static final String MOVIE = "movie" ;	//电影
	private Context mContext;
	private SQLiteDatabase database;
	private DBOperate dbOperate ;
	
	public static MatDBManager getInstance(Context context) {
		if(dbManager==null) {
			dbManager = new MatDBManager(context) ;
		}
		return dbManager ;
	}
	private MatDBManager(Context context) {
		this.mContext = context;
		dbOperate = new DBOperate() ;
	}
	
	public SQLiteDatabase openDatabase() {
		String path = DB_PATH + mContext.getPackageName() + "/" + DB_NAME ;
		database = SQLiteDatabase.openOrCreateDatabase(path,null);
		return database ;
	}
	
	public void closeDatabase() {
		if(database.isOpen()) {
			database.close() ;
		}
	}
	
	/**
	 * 查询全部
	 * @param category 电影或者是电视剧    通过  MatDBManager.TELEPLAY 或者是 MatDBManager.MOVIE 来指定
	 * @param currentPage 当前第几页  从0开始
	 * @param count	每页显示多少条
	 * @return
	 */
	public List<Movie> getAll(String category, int currentPage, int count) {
		return dbOperate.queryAll(database,category,currentPage,count) ;
	}
	
	/**
	 * 通过年份查找
	 * @param category
	 * @param year 2012
	 * @param currentPage
	 * @param count
	 * @return
	 */
	public List<Movie> getAllByYear(String category, int year, int currentPage, int count) {
		return dbOperate.queryByYear(database, category, year, currentPage, count) ;
	}
	
	/**
	 * 通过类别查找
	 * @param category
	 * @param type 动作
	 * @param currentPage
	 * @param count
	 * @return
	 */
	public List<Movie> getAllByType(String category, String type, int currentPage, int count) {
		return dbOperate.queryByType(database, category, type, currentPage, count) ;
	}
	
	/**
	 * 通过年份和类别查找
	 * @param category
	 * @param type
	 * @param year
	 * @param currentPage
	 * @param count
	 * @return
	 */
	public List<Movie> getAllByYearAndType(String category, String type, int year, int currentPage, int count) {
		return dbOperate.queryByYearAndType(database, category, type, year, currentPage, count) ;
	}
	
	/**
	 * 获取所有分类
	 * @param category
	 * @return
	 */
	public List<String> getTypes(String category) {
		return dbOperate.getAllType(database, category) ;
	}
	
	public List<String> getArea(String category) {
		return dbOperate.getArea(database, category) ;
	}
	
	public List<Movie> getRecommend() {
		return dbOperate.getRecommend(database) ;
	}
	
}
