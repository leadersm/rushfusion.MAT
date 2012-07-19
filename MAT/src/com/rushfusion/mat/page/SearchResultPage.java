package com.rushfusion.mat.page;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
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

import com.rushfusion.mat.MATActivity;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class SearchResultPage extends BasePage {
	private String mCurrentOrigin;
	private String mCurrentCategory;
	
	
	private String key = "";
	private int currentPage = 1;
	private int pagesize = 18;
	private List<Movie> movies;
	private BaseAdapter ba;
	private DataParser parser;
	ImageLoadTask imageTask;
	
	private GridView gridView;
	private TextView info;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			default:
				break;
			}
		};
	} ;
	
	public SearchResultPage(Activity context, String currentOrigin,String currentCategory,
			ViewGroup parent) {
		super(context, parent);
		mCurrentOrigin = currentOrigin;
		mCurrentCategory = currentCategory;
		parser = DataParser.getInstance(context, "");
		imageTask = new ImageLoadTask();
	}
	

	public void loadPage(final String url,int layoutId) {
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				gridView = (GridView) contentView.findViewById(R.id.searchResultGridView);
				info = (TextView) contentView.findViewById(R.id.resultInfo);
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			
			@Override
			public List<Movie> onExcute(String url) {
				return parser.getMovies(url) ;
			}

			@Override
			public void onFinished(List<Movie> result) {
				movies = result;
				if(result!=null&&result.size()>0){
					String msg = "搜索与\""+getKey()+"\"相关的内容，共计结果:"+parser.getTotal()+"个";
					info.setText(createInfoMessage(msg));
					ba = new MATAdapter(movies);
					gridView.setAdapter(ba);
				}else{
					String recommendUrl = "http://tvsrv.webhop.net:9061/query?source="
				+mCurrentOrigin+"&category="+mCurrentCategory+"&sort=play&page=1&pagesize=6";
					loadPage(recommendUrl, R.layout.page_search_noresult,new BasePage.onLoadingDataCallBack() {
						
						ViewGroup items;
						
						@Override
						public void onPrepare() {
							TextView key = (TextView) contentView.findViewById(R.id.search_failed_key);
							String msg = "非常抱歉，没有找到与\""+getKey()+"\"相关的内容。";
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
								imageTask.loadImage(thumb, result.get(i).getThumb(), new ImageLoadTask.ImageViewCallback1() {

									@Override
									public void callbak(ImageView view, Bitmap bm) {
										// TODO Auto-generated method stub
										view.setImageBitmap(bm);
									}
									
								});
								title.setText(result.get(i).getName());
								items.addView(v);
							}
							
						}
						
					});
					
				}
				if(movies.size()>12)
				gridView.setOnScrollListener(new OnScrollListener() {
					
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						// TODO Auto-generated method stub
						if(view.getLastVisiblePosition()==view.getCount()-1){
							if(currentPage>Math.ceil(parser.getTotal()/pagesize)){
								return;
							}
							if(!isLoadingNextPage)
								loadNextPage(url);
						}
					}

					boolean isLoadingNextPage = false;
					
					private void loadNextPage(final String url) {
						String nextPageUrl = getPageUrl(++currentPage,url);
						Log.d("MAT", "currentPage->"+currentPage+"加载数据、、getTotal()-->"+parser.getTotal()+"--getTotal/size-->"+Math.ceil(parser.getTotal()/pagesize));
						Log.d("MAT", "loadNextUrl--->"+nextPageUrl);
						loadData(nextPageUrl, new BasePage.onLoadingDataCallBack() {
							
							@Override
							public void onPrepare() {
								// TODO Auto-generated method stub
								context.showDialog(MATActivity.DIALOG_LOADING);
								isLoadingNextPage = true;
							}
							
							
							@Override
							public List<Movie> onExcute(String url) {
								// TODO Auto-generated method stub
								return parser.getMovies(url);
							}
							
							@Override
							public void onFinished(List<Movie> result) {
								// TODO Auto-generated method stub
								context.dismissDialog(MATActivity.DIALOG_LOADING);
								isLoadingNextPage = false;
								if(movies.size()<=0)return;
								movies.addAll(result);
								ba.notifyDataSetChanged();
							}
						});
					}

					private String getPageUrl(int currentPage,String url) {
						url = url.replaceAll("&page=(\\d)+", "") ;
						url = url.replaceAll("&pagesize=(\\d)+", "") ;
						return url+"&page="+currentPage+"&pagesize="+pagesize;
					}
				});
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
	
	@Override
	public void onKill() {
		Log.w("MAT", "onKill--SearchResultPage");
	}
	
	class MATAdapter extends BaseAdapter{
		List<Movie> result;
		public MATAdapter(List<Movie> result) {
			// TODO Auto-generated constructor stub
			this.result = result;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
			ImageView thumb = (ImageView) v.findViewById(R.id.ItemIcon);
			TextView title = (TextView) v.findViewById(R.id.ItemTitle);
			imageTask.loadImage(thumb, result.get(position).getThumb(), new ImageLoadTask.ImageViewCallback1() {

				@Override
				public void callbak(ImageView view, Bitmap bm) {
					// TODO Auto-generated method stub
					view.setImageBitmap(bm);
				}
				
			});
			title.setText(result.get(position).getName());
			return v;
		}
		
	}

	
}
