package com.rushfusion.mat.page;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import android.widget.Toast;

import com.rushfusion.mat.MATActivity;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.Cache;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class SearchResultPage extends BasePage {
	private String mCurrentOrigin;
	private String mCurrentCategory;
	
	
	private String key = "";
	private int currentPage = 1;
	private int totalPage;
	
	private List<Movie> movies = new ArrayList<Movie>();
	private BaseAdapter ba;
	private DataParser parser;
//	ImageLoadTask imageTask;
	
	private GridView gridView;
	private Button preBtn,nextBtn;
	private TextView pageIndex;
	private String mLoadUrl;
	
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
	public SearchResultPage(Activity context, String currentOrigin,String currentCategory,
			ViewGroup parent) {
		super(context, parent);
		mCurrentOrigin = currentOrigin;
		mCurrentCategory = currentCategory;
		parser = DataParser.getInstance(context, "");
//		imageTask = new ImageLoadTask();
	}
	

	public void loadPage(final String url,int layoutId) {
		mLoadUrl = url;
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				gridView = (GridView) contentView.findViewById(R.id.searchResultGridView);
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
				return parser.getMovies(url) ;
			}

			@Override
			public void onFinished(List<Movie> result) {
				if(result!=null&&result.size()>0){
					String msg = "在<"+mCurrentCategory+">中搜索与\""+getKey()+"\"相关的内容，共计结果:"+parser.getTotal()+"个";
					Toast.makeText(context, createInfoMessage(msg), 1).show();
					movies = result;
					ba.notifyDataSetChanged();
					totalPage = (int)Math.ceil((double)parser.getTotal()/MATActivity.FILMCLASSPAGESIZE);
					updatePageIndex();
				}else{
					String recommendUrl = "http://tvsrv.webhop.net:9061/query?source="
				+mCurrentOrigin+"&category="+mCurrentCategory+"&sort=play&page=1&pagesize=10";
					loadPage(recommendUrl, R.layout.page_search_noresult,new BasePage.onLoadingDataCallBack() {
						
						ViewGroup items;
						
						@Override
						public void onPrepare() {
							TextView key = (TextView) contentView.findViewById(R.id.search_failed_key);
							String msg = "非常抱歉，在<"+mCurrentCategory+">中没有找到与\""+getKey()+"\"相关的内容。";
							items = (ViewGroup) contentView.findViewById(R.id.gallery_search);
							key.setText(createInfoMessage(msg));
							Button research = (Button) contentView.findViewById(R.id.search_result_researchBtn);
							research.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									context.showDialog(MATActivity.DIALOG_SEARCH);
								}
							});
						}
						@Override
						public List<Movie> onExcute(String url) {
							return parser.getMovies(url) ;
						}
						
						@Override
						public void onFinished(List<Movie> result) {
							if(result==null)return;
							for(int i=0;i<result.size();i++){
								View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
								ImageView thumb = (ImageView) v.findViewById(R.id.ItemIcon);
								TextView title = (TextView) v.findViewById(R.id.ItemTitle);
								ImageLoadTask imageTask = new ImageLoadTask() ;
								v.setTag(result.get(i));
								imageTask.loadImage(thumb, result.get(i).getThumb(), new ImageLoadTask.ImageViewCallback1() {

									@Override
									public void callbak(ImageView view, Bitmap bm) {
										view.setImageBitmap(bm);
									}
									
								});
								title.setText(result.get(i).getName());
								v.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										Movie movie = (Movie) v.getTag();
										Intent i = new Intent(context,ItemDetailPage.class);
										i.putExtra("movieInfo",movie);
										context.startActivity(i);
									}
								});
								
								items.addView(v);
							}
							
						}
						
					});
					
				}
				if(movies.size()>12)
				gridView.setOnScrollListener(new OnScrollListener() {
					
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
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
	
	boolean isLoading = false;
	
	private void updatePageIndex() {
		// TODO Auto-generated method stub
		if(currentPage <= 1){
			preBtn.setEnabled(false);
		}else if(currentPage>=(int)Math.ceil((double)parser.getTotal()/MATActivity.FILMCLASSPAGESIZE)){
			nextBtn.setEnabled(false);
		}else{
			preBtn.setEnabled(true);
			nextBtn.setEnabled(true);
		}
		pageIndex.setText(currentPage+"/"+(int)Math.ceil((double)parser.getTotal()/MATActivity.FILMCLASSPAGESIZE));
	}
	
	private String getPageUrl(int currentPage,String url) {
		url = url.replaceAll("&page=(\\d)+", "") ;
		url = url.replaceAll("&pagesize=(\\d)+", "") ;
		return url+"&page="+currentPage+"&pagesize="+MATActivity.FILMCLASSPAGESIZE;
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
	
	
	private SpannableStringBuilder createInfoMessage(String msg) {
		SpannableStringBuilder style=new SpannableStringBuilder(msg);
		int a = msg.indexOf("\"")+1;
		int b = msg.lastIndexOf("\"");
		style.setSpan(new ForegroundColorSpan(Color.RED),a,b,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return style;
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
			ImageLoadTask imageTask = new ImageLoadTask() ;
			if(Cache.getBitmapFromCache(movies.get(position).getThumb())!=null) {
				holder.thumb.setImageBitmap(Cache.getBitmapFromCache(movies.get(position).getThumb())) ;
			}else{
				imageTask.loadImage(holder.thumb, movies.get(position).getThumb(), new ImageLoadTask.ImageViewCallback1() {
					
					@Override
					public void callbak(ImageView view, Bitmap bm) {
						// TODO Auto-generated method stub
						view.setImageBitmap(bm);
					}
					
				});
			}
			holder.title.setText(movies.get(position).getName());
            return convertView;
		}
		
	}
	
	class ViewHolder{
		private ImageView thumb;
		private TextView title;
	}
	
	@Override
	public void onKill() {
		Log.d("MAT", "onKill--SearchResultPage");
	}
	
}
