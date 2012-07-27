package com.rushfusion.mat.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rushfusion.mat.MATActivity;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.Cache;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class FilmClassPage extends BasePage {

	private static int currentPage = 1;
	private int totalPage = 0;
	
	private List<Movie> movies = new ArrayList<Movie>();
	private BaseAdapter ba;
	private DataParser parser;
//	private ImageLoadTask imageTask;

	private GridView gridView;
	private Button preBtn,nextBtn;
	private TextView pageIndex;

	static FilmClassPage page;
	private String mLoadUrl;

	public static FilmClassPage getInstance(Activity context, ViewGroup parent) {
		if (page == null) {
			page = new FilmClassPage(context, parent);
		}
		currentPage = 1;
		return page;
	}

	private FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		parser = DataParser.getInstance(context, "");
//		imageTask = new ImageLoadTask() ;
	}

	
	boolean isLoading = false;
	
	@Override
	public void loadPage(String url, int layoutId) {
		mLoadUrl = url;
		Log.d("MAT", url);
		loadPage(url, layoutId, new BasePage.onLoadingDataCallBack() {

			@Override
			public void onPrepare() {
				gridView = (GridView) contentView.findViewById(R.id.gridView_filmclass);
				ba = new MATAdapter();
				gridView.setAdapter(ba);
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Movie movie = movies.get(position);
						Intent i = new Intent(context,ItemDetailPage.class);
						i.putExtra("movieInfo",movie);
						context.startActivity(i);
					}
				});
				preBtn = (Button) contentView.findViewById(R.id.page_pre);
				nextBtn = (Button) contentView.findViewById(R.id.page_next);
				pageIndex = (TextView) contentView.findViewById(R.id.page_index);
				preBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						loadPrePage(mLoadUrl);
					}
				});
				nextBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						loadNextPage(mLoadUrl);
					}
				});
				
			}

			@Override
			public List<Movie> onExcute(String url) {
				return parser.getMovies(url);
			}

			@Override
			public void onFinished(List<Movie> result) {
				if(result!=null&&result.size()>0){
					movies = result;
					ba.notifyDataSetChanged();
					totalPage = (int)Math.ceil((double)parser.getTotal()/MATActivity.FILMCLASSPAGESIZE);
					updatePageIndex();
				}
				
				if(movies.size()>12)
					gridView.setOnScrollListener(new OnScrollListener() {
						
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {
							Log.i("MAT", "state-->"+scrollState);
							if(scrollState==OnScrollListener.SCROLL_STATE_FLING){
								if(view.getFirstVisiblePosition()==0){
									if(!isLoading)
										loadPrePage(mLoadUrl);
								}
								if(view.getLastVisiblePosition()==view.getCount()-1){
									if(!isLoading)
										loadNextPage(mLoadUrl);
								}
							}
						}
						
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {
							
						}


					});
			}
		});
	}
	
	private String getPageUrl(int currentPage,String url) {
		url = url.replaceAll("&page=(\\d)+", "") ;
		url = url.replaceAll("&pagesize=(\\d)+", "") ;
		return url+"&page="+currentPage+"&pagesize="+MATActivity.FILMCLASSPAGESIZE;
	}
	
	
	class MATAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return movies.size();
		}

		@Override
		public Object getItem(int position) {
			return movies.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = LayoutInflater.from(context).inflate(R.layout.page_film_class_item, null);
				holder = new ViewHolder();
				holder.thumb = (ImageView) convertView.findViewById(R.id.ItemIcon);
				holder.title = (TextView) convertView.findViewById(R.id.ItemTitle);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.thumb.setImageResource(R.drawable.film_bg_loading) ;
			ImageLoadTask imageTask = new ImageLoadTask() ;
			Log.d("cache", "mSoftBitmapCache:"+Cache.mSoftBitmapCache.size()) ;
			
			
			if(Cache.getBitmapFromCache(movies.get(position).getThumb())!=null) {
				Log.d("loadimage", "load from cache.....") ;
				holder.thumb.setImageBitmap(Cache.getBitmapFromCache(movies.get(position).getThumb())) ;
			}else{
				Log.d("loadimage", "load from net.....") ;
				imageTask.loadImage(holder.thumb, movies.get(position).getThumb(), new ImageLoadTask.ImageViewCallback1() {
					
					@Override
					public void callbak(ImageView view, Bitmap bm) {
						view.setImageBitmap(bm);
					}
					
				});
			}
			//holder.thumb.setTag(imageUrl) ;
			//ImageLoadTask.loadImageLimited(holder.thumb, imageUrl) ;
			holder.title.setText(movies.get(position).getName());
            return convertView;
		}
		
	}
	
	private void updatePageIndex() {
		// TODO Auto-generated method stub
		if(currentPage <= 1){
			preBtn.setEnabled(false);
		}else if(currentPage>=totalPage){
			nextBtn.setEnabled(false);
		}else{
			preBtn.setEnabled(true);
			nextBtn.setEnabled(true);
		}
		pageIndex.setText(currentPage+"/"+totalPage);
	}
	
	private void loadPrePage(String url) {
		if(currentPage<=1){
			return;
		}
		String prePageUrl = getPageUrl(--currentPage,url);
		Log.d("MAT", "currentPage->"+currentPage+"加载数据、、getTotal()-->"+parser.getTotal());
		Log.d("MAT", "loadNextUrl--->"+prePageUrl);
		
		loadData(prePageUrl, new BasePage.onLoadingDataCallBack() {
			
			@Override
			public void onPrepare() {
				updatePageIndex();
				//recycle() ;
				context.showDialog(MATActivity.DIALOG_LOADING);
				isLoading = true;
			}
			
			
			@Override
			public List<Movie> onExcute(String url) {
				return parser.getMovies(url);
			}
			
			@Override
			public void onFinished(List<Movie> result) {
				context.dismissDialog(MATActivity.DIALOG_LOADING);
				isLoading = false;
				if(result.size()<=0)return;
				movies.clear();
				movies = result;
				ba.notifyDataSetChanged();
				gridView.setSelection(0);
			}
		});
	}
	private void loadNextPage(String url) {
		if(currentPage>=totalPage){
			return;
		}
		String nextPageUrl = getPageUrl(++currentPage,url);
		Log.d("MAT", "currentPage->"+currentPage+"加载数据、、getTotal()-->"+parser.getTotal()+"--totalPage-->"+totalPage);
		Log.d("MAT", "loadNextUrl--->"+nextPageUrl);
		loadData(nextPageUrl, new BasePage.onLoadingDataCallBack() {
			
			@Override
			public void onPrepare() {
				updatePageIndex();
				//recycle() ;
				context.showDialog(MATActivity.DIALOG_LOADING);
				isLoading = true;
			}
			
			
			@Override
			public List<Movie> onExcute(String url) {
				return parser.getMovies(url);
			}
			
			@Override
			public void onFinished(List<Movie> result) {
				context.dismissDialog(MATActivity.DIALOG_LOADING);
				isLoading = false;
				if(result.size()<=0)return;
				Log.d("MAT","result.size--->"+result.size());
				movies.clear();
				movies = result;
				ba.notifyDataSetChanged();
				gridView.setSelection(0);
			}
		});
	}
	
	@Override
	public void onKill() {
		Log.d("MAT", "onKill--FilmClassPage");
	}

	
	
	class ViewHolder{
		private ImageView thumb;
		private TextView title;
	}
	
	/*public void recycle() {
		System.out.println("bitmap is recycle!");
		List<Bitmap> bitList = ImageLoadTask.bitList ;
		System.out.println("recycle head :"+bitList.size());
		for(int i=0; i<bitList.size(); i++) {
			Bitmap bitmap = bitList.get(i) ;
			if(bitmap!=null && !bitmap.isRecycled()) {
				bitmap.recycle() ;
				bitmap = null ;
			}
		}
		bitList.clear() ;
		System.out.println("recycle end :"+bitList.size());
		System.gc() ;
	}*/
}
