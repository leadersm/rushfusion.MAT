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
	 * 所有电影
	 * @param currentPage 当前页
	 * @param count	每页显示多少条
	 * @return
	 */
	public List<Movie> getAllMovie(int currentPage, int count) {
		return dbOperate.queryAllMovie(database,currentPage,count) ;
	}
	
	/**
	 * 所有电视剧
	 * @param currentPage 当前页
	 * @param count 每页显示多少条
	 * @return
	 */
	public List<Movie> getAllTeleplay(int currentPage, int count) {
		return dbOperate.queryAllTeleplay(database, currentPage, count) ;
	}
	
}
