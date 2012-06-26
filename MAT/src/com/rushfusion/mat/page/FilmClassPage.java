package com.rushfusion.mat.page;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;

public class FilmClassPage extends BasePage {
	private static final int FILM_NUM = 8 ;
	private Context mContext ;
	private ViewGroup mParent ;
	private List<Map<String, String>> nodeList;
	protected ViewGroup filmItems[];
	protected int filmItemResIds[] = { R.id.item1, R.id.item2, R.id.item3, R.id.item4,
			R.id.item5, R.id.item6, R.id.item7, R.id.item8 };

	public FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		mContext = context ;
		mParent = parent ;
	}
	
	@Override
	public void loadPage(String url, int layoutId) {
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
				initPage() ;
			}
			
			@Override
			public List<Map<String, String>> onExcute(String url) {
				// TODO Auto-generated method stub
				//contentView.findViewById(R.id.image) ;
				String strUrl = url ;
				nodeList = DataParser.getInstance(context, "").get(strUrl);
				return nodeList ;
			}

			@Override
			public void onFinshed(List<Map<String, String>> result) {
				fillData(result) ;
			}
		});
	}
	
	private void initPage(){
		int size = FILM_NUM ;
		filmItems = new ViewGroup[size];
		for(int i = 0; i<size; i++){
			filmItems[i] = (ViewGroup)contentView.findViewById(filmItemResIds[i]);
			filmItems[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			
			filmItems[i].setVisibility(View.INVISIBLE) ;
			
			filmItems[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						//updateData() ;
					}
				}
			});
		}
	}
	
	private void updateData() {
		TextView filmName = (TextView)contentView.findViewById(R.id.field_filmname) ;
		TextView director = (TextView)contentView.findViewById(R.id.field_director) ;
		TextView actor = (TextView)contentView.findViewById(R.id.field_actor) ;
		TextView area = (TextView)contentView.findViewById(R.id.field_area) ;
		TextView introduction = (TextView)contentView.findViewById(R.id.field_introduction) ;
	}
	
	private void fillData(List<Map<String,String>> params) {
		int size = itemSize();
		for(int i=0; i<size; i++) {
			Map<String,String> nodeMap = params.get(i) ;
			ImageView itemIcon = (ImageView)filmItems[i].findViewById(R.id.ItemIcon) ;
			ImageLoadTask.imageLoad(itemIcon, nodeMap.get("thumb")) ;
			TextView itemTitle = (TextView)filmItems[i].findViewById(R.id.ItemTitle) ;
			itemTitle.setText(nodeMap.get("name")) ;
			filmItems[i].setVisibility(View.VISIBLE) ;
		}
	}
	
	protected int itemSize(){
		int size = FILM_NUM ;
		if(nodeList.size()<=FILM_NUM) {
			size = nodeList.size() ;
		}
		return size ;
	}
	
}
