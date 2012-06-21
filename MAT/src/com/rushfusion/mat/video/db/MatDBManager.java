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
	
	public List<Movie> getAllMovie() {
		return dbOperate.queryAllMovie(database) ;
	}
	
}
