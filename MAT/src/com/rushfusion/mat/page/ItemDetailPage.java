package com.rushfusion.mat.page;

import java.util.ArrayList;
import java.util.List;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.ItemDetailGridViewAdapter;
import com.rushfusion.mat.video.entity.Movie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemDetailPage extends Activity{
	ImageView image;
	TextView name;
	TextView description;
	TextView socres;
	TextView length;
	TextView year;
	TextView directors;
	TextView artists;
	GridView episode;

	ItemDetailGridViewAdapter gda;

	List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_item_detail);
		Movie movie = (Movie) getIntent().getSerializableExtra("movieInfo");
		
		image = (ImageView) findViewById(R.id.page_item_detail_image);
		name = (TextView) findViewById(R.id.page_item_detail_name);
		description = (TextView) findViewById(R.id.page_item_detail_description);
		socres = (TextView) findViewById(R.id.page_item_detail_socres);
		length = (TextView) findViewById(R.id.page_item_detail_length);
		year = (TextView) findViewById(R.id.page_item_detail_year);
		directors = (TextView) findViewById(R.id.page_item_detail_directors);
		artists = (TextView) findViewById(R.id.page_item_detail_artists);
		episode = (GridView) findViewById(R.id.page_item_detail_episode);
		
		
		image.setImageURI(Uri.parse(movie.getThumb()));
		image.setScaleType(ScaleType.FIT_XY);
		name.setText(movie.getName());
		description.setText(movie.getDescription());
		socres.setText(movie.getScore()+"");
		length.setText(movie.getLength());
		year.setText(movie.getYear()+"");
		directors.setText(movie.getDirectors());
		artists.setText(movie.getArtists());
		String[] str = movie.getUrl().split(";");
		list = new ArrayList<String>();
		for(String urlstr : str){
			System.out.println(urlstr);
			list.add(urlstr);
		}
		System.out.println(list.size());
		gda = new ItemDetailGridViewAdapter(this, list);
		episode.setAdapter(gda);
		episode.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 播放  163:http://163.letv.com/dianying/E7JRABGJ9/M7KS70Q27_mini.html

				String path = list.get(arg2);
				if(path.indexOf("html")==(path.length()-4)){
					Intent it = new Intent(Intent.ACTION_VIEW , Uri.parse(path));
					startActivity(it);
					
				}else{
					Intent it =  new Intent(ItemDetailPage.this, MediaPlayerShow.class);
					Bundle bd = new Bundle();
					bd.putString("url", list.get(arg2));
					it.putExtras(bd);
					startActivity(it);
				}

			}

		});
	}
	
	
	
}
