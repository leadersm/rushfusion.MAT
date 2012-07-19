package com.rushfusion.mat.page;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.video.entity.Movie;

public class FilmClassPage extends BasePage {
	
	DataParser parser;
	static FilmClassPage page;
	
	public static FilmClassPage getInstance(Activity context, ViewGroup parent){
		if(page==null){
			page = new FilmClassPage(context, parent);
		}
		return page;
	}
	
	private FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		parser = DataParser.getInstance(context, "");
	}
	
	
	@Override
	public void loadPage(String url, int layoutId) {
		Log.d("", url) ;
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public List<Movie> onExcute(String url) {
				// TODO Auto-generated method stub
				//contentView.findViewById(R.id.image) ;
				return  parser.getMovies(url) ;
			}

			@Override
			public void onFinished(List<Movie> result) {
			}
		});
	}
	

	@Override
	public void onKill() {
		// TODO Auto-generated method stub
		Log.d("MAT", "onKill--FilmClassPage");
	}
	
}
