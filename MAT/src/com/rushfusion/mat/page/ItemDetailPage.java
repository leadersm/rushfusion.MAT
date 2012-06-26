package com.rushfusion.mat.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.ItemDetailGridViewAdapter;
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

	ItemDetailGridViewAdapter gda;



	List<String> list;
	//Movie movie = new Movie(1, 1, 80, 1364, 334, "movie", "栗洋的生活", "家庭;伦理", 2012, "栗洋", "栗洋;王英", "大陆", "栗洋一家的生活，以及一年以来的变故", "http://tp4.sinaimg.cn/2010098347/50/5600263031/1", "01:51:58", "http://v.iask.com/v_play_ipad.php?vid=59551380");

	public ItemDetailPage(Activity context, ViewGroup parent) {
		super(context, parent);
	}

	@Override
	public void loadPage(String url, int layoutId) {
		// TODO Auto-generated method stub
		
	}

}
