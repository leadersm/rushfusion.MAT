package com.rushfusion.mat.page;

import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class FilmClassPage extends BasePage {
	private static final int FILM_NUM = 8 ;
	private Context mContext ;
	private ViewGroup mParent ;
	private String mUrl ;
	private int currentPage = 1 ;
	private int pageSize = 0 ;
	private int total ;
	private List<Map<String, String>> nodeList;
	protected ViewGroup filmItems[];
	protected int filmItemResIds[] = { R.id.item1, R.id.item2, R.id.item3, R.id.item4,
			R.id.item5, R.id.item6, R.id.item7, R.id.item8 };
	private TextView page_indext;
	private String loadTag = "first" ;
	public boolean loading = false;
	public boolean updating = false;
	
	int LOADPAGEPOST_DELAY_TIME = 300;
	int UPDATEPAGEPOST_DELAY = 300;

	private static final int UPDATE_DATA = 10 ;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_DATA:
				Map<String,String> map = (Map<String, String>) msg.obj ;
				updateData(map) ;
				
				break;

			default:
				break;
			}
		};
	} ;
	public FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		mContext = context ;
		mParent = parent ;
	}
	
	@Override
	public void loadPage(String url, int layoutId) {
		mUrl = url ;
		Log.d("", mUrl) ;
		Log.d("childCount", mParent.getChildCount()+"") ;
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
				loading = true ;
				initFilm() ;
			}
			
			@Override
			public List<Map<String, String>> onExcute(String url) {
				// TODO Auto-generated method stub
				//contentView.findViewById(R.id.image) ;
				String strUrl = url ;
				DataParser parser = DataParser.getInstance(context, "") ;
				nodeList = parser.get(strUrl);
				total = parser.getTotal() ;
				return nodeList ;
			}

			@Override
			public void onFinished(List<Map<String, String>> result) {
				loading = false ;
				initPage() ;
				fillData(result) ;
				updateArrow() ;
			}
		});
	}
	
	private void initFilm(){
		int size = FILM_NUM ;
		filmItems = new ViewGroup[size];
		for(int i = 0; i<size; i++){
			filmItems[i] = (ViewGroup)contentView.findViewById(filmItemResIds[i]);
			filmItems[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Map<String,String> map = (Map<String, String>)v.getTag() ;
					Movie movie = new Movie(Integer.parseInt(map.get("count")),
							                Integer.parseInt(map.get("total")),
							                Integer.parseInt(map.get("score")),
							                Integer.parseInt(map.get("comment")),
							                map.get("category"),
							                map.get("name"),
							                map.get("type"),
							                Integer.parseInt(map.get("year")),
							                map.get("directors"),
							                map.get("artists"),
							                map.get("area"),
							                map.get("description"),
							                map.get("thumb"),
							                map.get("length"),
							                map.get("url"),
							                Integer.parseInt(map.get("play")),
							                Integer.parseInt(map.get("id")),
							                Integer.parseInt(map.get("recent"))) ;
					Intent intent = new Intent(mContext,ItemDetailPage.class) ;
					Bundle bundle = new Bundle() ;
					bundle.putSerializable("movieInfo", movie) ;
					intent.putExtras(bundle) ;
					mContext.startActivity(intent) ;
				}
			});
			
			filmItems[i].setVisibility(View.INVISIBLE) ;
			
			ImageView imageView = (ImageView)filmItems[i].findViewById(R.id.ItemIcon) ;
			imageView.setImageDrawable(null);
			imageView.destroyDrawingCache() ;
			imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.film_bg_loading)) ;
			
			filmItems[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						Map<String,String> map = (Map<String, String>) v.getTag() ;
						if(map!=null) {
							Message message = new Message() ;
							message.obj = map ;
							message.what = UPDATE_DATA ;
							handler.sendMessageDelayed(message, 300) ;
							updateData(map) ;
						}
					}
				}
			});
		}
		
		filmItems[0].requestFocus() ;
		contentView.findViewById(R.id.item1).setNextFocusLeftId(R.id.item1) ;
		contentView.findViewById(R.id.item4).setNextFocusRightId(R.id.item5) ;
		contentView.findViewById(R.id.item5).setNextFocusLeftId(R.id.item4) ;
		
		contentView.findViewById(R.id.item8).setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( event.getAction() == KeyEvent.ACTION_DOWN){
				
					if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
						if(!loading && !updating)
							nextPage();
						return true;
					}
				}
				return false;
			}
		});
		
		contentView.findViewById(R.id.item1).setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( event.getAction() == KeyEvent.ACTION_DOWN){
					if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
						if(!loading && !updating)
							prevPage();
						return true;
					}
				}
				return false;
			}
		});
		page_indext = (TextView)contentView.findViewById(R.id.field_page_index);
	}
	
	private void initPage() {
		int size = FILM_NUM ;
		//currentPage = total> currentPage ? currentPage:currentPage ;
		if(total==0) 
			currentPage = total ;
		Log.d("total", total+"") ;
		if(loadTag.equals("first")) {
			if(total % size == 0) {
				pageSize = total / size ;
			} else {
				pageSize = (total / size) + 1 ;
			}
		}
		updatePageState() ;
	}
	
	private void nextPage() {
		loadTag = "next" ;
		if(currentPage>=pageSize)
			return ;
		currentPage++ ;
		mUrl = getLoadUrl(currentPage,FILM_NUM) ;
		Log.d("next", mUrl) ;
		//mParent.removeAllViews() ;
		//loadPage(url,R.layout.page_film_class) ;
		loadItemPage() ;
	}
	
	private void prevPage() {
		loadTag = "previous" ;
		if(currentPage==1) 
			return ;
		currentPage-- ;
		mUrl = getLoadUrl(currentPage,FILM_NUM) ;
		Log.d("next", mUrl) ;
		loadItemPage() ;
		
	}
	
	private void loadItemPage() {
		if(loading || updating)
			return;
		contentView.removeCallbacks(loadPagePostRunnable);
		updatePageState() ;
		contentView.postDelayed(loadPagePostRunnable,LOADPAGEPOST_DELAY_TIME);
	}
	
	Runnable loadPagePostRunnable = new Runnable(){
		@Override
		public void run() {
			loadPage(mUrl,R.layout.page_film_class) ;
			PageCache.getInstance().set(R.layout.page_film_class, FilmClassPage.this);
		}};
	
	private void updateData(Map<String,String> map) {
		//Map<String, String> map = nodeList.get(index) ;
		TextView filmName = (TextView)contentView.findViewById(R.id.field_filmname) ;
		filmName.setText(map.get("name")) ;
		TextView director = (TextView)contentView.findViewById(R.id.field_director) ;
		director.setText(mContext.getString(R.string.director) + ":" +map.get("directors")) ;
		TextView actor = (TextView)contentView.findViewById(R.id.field_actor) ;
		actor.setText(mContext.getString(R.string.actor) + ":" +map.get("artists")) ;
		TextView area = (TextView)contentView.findViewById(R.id.field_area) ;
		area.setText(mContext.getString(R.string.area) + ":" +map.get("area")) ;
		TextView introduction = (TextView)contentView.findViewById(R.id.field_introduction) ;
		introduction.setText(mContext.getString(R.string.introduction) + ":" +map.get("description")) ;
	}
	
	public void updatePageState() {
		page_indext.setText(currentPage+"/"+pageSize+mContext.getString(R.string.page)) ;
	}
	
	private void fillData(List<Map<String,String>> params) {
		updating = true ;
		int size = itemSize();
		for(int i=0; i<size; i++) {
			Map<String,String> nodeMap = params.get(i) ;
			ImageView itemIcon = (ImageView)filmItems[i].findViewById(R.id.ItemIcon) ;
			//ImageLoadTask.imageLoad1(itemIcon, nodeMap.get("thumb")) ;
			String imageUrl = nodeMap.get("thumb") ;
			itemIcon.setTag(imageUrl) ;
			ImageLoadTask.loadImageLimited(itemIcon, imageUrl) ;
			TextView itemTitle = (TextView)filmItems[i].findViewById(R.id.ItemTitle) ;
			itemTitle.setText(nodeMap.get("name")) ;
			filmItems[i].setTag(params.get(i)) ;
			filmItems[i].setVisibility(View.VISIBLE) ;
		}
		if("first".equals(loadTag) || "previous".equals(loadTag)) 
			filmItems[0].requestFocus() ;
		else 
			filmItems[params.size()-1].requestFocus() ;
		
		updating = false ;
	}
	
	protected int itemSize(){
		int size = FILM_NUM ;
		if(nodeList.size()<=FILM_NUM) {
			size = nodeList.size() ;
		}
		return size ;
	}
	
	public String getLoadUrl(int currentPage, int pagesize) {
		mUrl = mUrl.replaceAll("&page=(\\d)+", "") ;
		mUrl = mUrl.replaceAll("&pagesize=(\\d)+", "") ;
		return mUrl+"&page="+currentPage+"&pagesize="+pagesize;
	}
	
	private void updateArrow() {
		if(currentPage <= 1){
			contentView.findViewById(R.id.arrow_left_film_class).setBackgroundResource(R.drawable.arrow_left_film_class_disable) ;
		}else{
			contentView.findViewById(R.id.arrow_left_film_class).setBackgroundResource(R.drawable.arrow_left_film_class_enable) ;
		}
	 
		if(total <= FILM_NUM){
			contentView.findViewById(R.id.arrow_right_film_class).setBackgroundResource(R.drawable.arrow_right_film_class_disable) ;
		}else{
			contentView.findViewById(R.id.arrow_right_film_class).setBackgroundResource(R.drawable.arrow_right_film_class_enable) ;
		}
	}
	
}
