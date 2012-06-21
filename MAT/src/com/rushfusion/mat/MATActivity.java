package com.rushfusion.mat;

import java.util.List;

import com.rushfusion.mat.video.db.MatDBManager;
import com.rushfusion.mat.video.entity.Movie;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.rushfusion.mat.page.RecommendPage;

public class MATActivity extends Activity {
	private Context mContext ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = MATActivity.this ;
        //ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        //initRecommendPage(parent);
        query() ;
    }
    
    public void query() {
    	MatDBManager dbManager = MatDBManager.getInstance(mContext) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllMovie(0,6) ;
    	Log.d("电影", movies.toString()) ;
    }

	private void initRecommendPage(ViewGroup parent) {
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage("url",R.layout.page_recommend);
	}

}
