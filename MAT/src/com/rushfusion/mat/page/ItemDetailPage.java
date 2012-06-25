package com.rushfusion.mat.page;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.video.entity.Movie;

public class ItemDetailPage extends BasePage {
	
	ImageView image;
	TextView name;
	TextView description;
	TextView socres;
	TextView length;
	TextView year;
	TextView directors;
	TextView artists;
	GridView episode;
	Movie movie = new Movie(1, 1, 80, 1364, 334, "movie", "栗洋的生活", "家庭;伦理", 2012, "栗洋", "栗洋;王英", "大陆", "栗洋一家的生活，以及一年以来的变故", "http://tp4.sinaimg.cn/2010098347/50/5600263031/1", "01:51:58", "http://v.iask.com/v_play_ipad.php?vid=59551380");
	
	public ItemDetailPage(Context context, ViewGroup parent) {
		super(context, parent);
	
	}

	@Override
	public void loadPage(String url, int layoutId) {
		// TODO Auto-generated method stub
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.VISIBLE);
			}
		
			@Override
			public boolean onExcute(String url) {
				image = (ImageView) contentView.findViewById(R.id.page_item_detail_image);
				name = (TextView) contentView.findViewById(R.id.page_item_detail_name);
				description = (TextView) contentView.findViewById(R.id.page_item_detail_description);
				socres = (TextView) contentView.findViewById(R.id.page_item_detail_socres);
				length = (TextView) contentView.findViewById(R.id.page_item_detail_length);
				year = (TextView) contentView.findViewById(R.id.page_item_detail_year);
				directors = (TextView) contentView.findViewById(R.id.page_item_detail_directors);
				artists = (TextView) contentView.findViewById(R.id.page_item_detail_artists);
				episode = (GridView) contentView.findViewById(R.id.page_item_detail_episode);
				image.setImageURI(Uri.parse(movie.getThumb()));
				name.setText(movie.getName());
				description.setText(movie.getDescription());
				socres.setText(movie.getScore());
				length.setText(movie.getLength());
				year.setText(movie.getYear());
				directors.setText(movie.getDirectors());
				artists.setText(movie.getArtists());
				
				 
				
				return false;
			}

			@Override
			public void onFinshed(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.INVISIBLE);
			}
		});
	}

}
