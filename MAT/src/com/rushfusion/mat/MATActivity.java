package com.rushfusion.mat;

import java.util.List;

import com.rushfusion.mat.video.db.MatDBManager;
import com.rushfusion.mat.video.entity.Movie;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MATActivity extends Activity {
	private Context mContext ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = MATActivity.this ;
        query() ;
    }
    
    public void query() {
    	MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllMovie() ;
    	Log.d("电影", movies.toString()) ;
    }
}