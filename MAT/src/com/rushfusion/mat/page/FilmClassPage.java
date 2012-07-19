package com.rushfusion.mat.page;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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

public class FilmClassPage extends BasePage {

	private int currentPage = 1;
	private int pagesize = 18;
	private List<Movie> movies;
	private BaseAdapter ba;
	private DataParser parser;
	// ImageLoadTask imageTask;

	private GridView gridView;

	static FilmClassPage page;

	public static FilmClassPage getInstance(Activity context, ViewGroup parent) {
		if (page == null) {
			page = new FilmClassPage(context, parent);
		}
		return page;
	}

	private FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		parser = DataParser.getInstance(context, "");
	}

	@Override
	public void loadPage(final String url, int layoutId) {
		Log.d("MAT", url);
		loadPage(url, layoutId, new BasePage.onLoadingDataCallBack() {

			@Override
			public void onPrepare() {
				gridView = (GridView) contentView.findViewById(R.id.gridView_filmclass);
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "....", 1).show();
						Movie movie = movies.get(position);
						Intent i = new Intent(context,ItemDetailPage.class);
						i.putExtra("movieInfo",movie);
						context.startActivity(i);
					}
				});
			}

			@Override
			public List<Movie> onExcute(String url) {
				// TODO Auto-generated method stub
				// contentView.findViewById(R.id.image) ;
				return parser.getMovies(url);
			}

			@Override
			public void onFinished(List<Movie> result) {
				movies = result;
				if(result!=null&&result.size()>0){
					ba = new MATAdapter(movies);
					gridView.setAdapter(ba);
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
			return result.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
			ImageView thumb = (ImageView) v.findViewById(R.id.ItemIcon);
			TextView title = (TextView) v.findViewById(R.id.ItemTitle);
			ImageLoadTask imageTask = new ImageLoadTask() ;
			if(Cache.getBitmapFromCache(result.get(position).getThumb())!=null) {
				thumb.setImageBitmap(Cache.getBitmapFromCache(result.get(position).getThumb())) ;
			}else{
				imageTask.loadImage(thumb, result.get(position).getThumb(), new ImageLoadTask.ImageViewCallback1() {
					
					@Override
					public void callbak(ImageView view, Bitmap bm) {
						// TODO Auto-generated method stub
						view.setImageBitmap(bm);
					}
					
				});
			}
			title.setText(result.get(position).getName());
			return v;
		}
		
	}
	@Override
	public void onKill() {
		// TODO Auto-generated method stub
		Log.d("MAT", "onKill--FilmClassPage");
	}

}
