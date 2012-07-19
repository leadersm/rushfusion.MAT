package com.rushfusion.mat.page;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rushfusion.mat.R;
import com.rushfusion.mat.video.entity.Movie;

public abstract class BasePage {

	private boolean isLoading = false;
	public View contentView;
	
	public Activity context;
	private ViewGroup parent;
	public ProgressBar progress;
	
	public BasePage(Activity context,ViewGroup parent) {
		this.context = context;
		this.parent = parent;
		progress = (ProgressBar) context.findViewById(R.id.content_progressbar);
	}

	public boolean isLoading() {
		return isLoading;
	}

	public View getContentView() {
		return contentView;
	}

	public abstract void loadPage(String url,int layoutId);
	public abstract void onKill();
	
	
	private void setContentView(int layoutId) {
		View contentView ;
		parent.removeAllViews();
		if(PageCache.getInstance().get(layoutId)!=null){
			contentView = PageCache.getInstance().get(layoutId).getContentView();
		}else{
			contentView = LayoutInflater.from(context).inflate(layoutId, null);
		}
		this.contentView = contentView;
		parent.addView(contentView);
	}

	public void setPageCache(BasePage page,int layoutId){
		PageCache.getInstance().set(layoutId, page);
	}
	
	
	public AsyncTask<String, Void, List<Movie>> task ;
	
	public void loadPage(String url,int layoutId,final onLoadingDataCallBack callback){
		if(PageCache.getInstance().getLastPage()!=null)PageCache.getInstance().getLastPage().onKill();
		setContentView(layoutId);
		if(isLoading&&task!=null){
			task.cancel(true);
			task=null;
			isLoading = false;
		}else{
			task = new AsyncTask<String, Void, List<Movie>>(){
				
				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					isLoading = true;
					progress.setVisibility(View.VISIBLE);
					progress.bringToFront();
					callback.onPrepare();
				}

				@Override
				protected void onPostExecute(List<Movie> result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					isLoading = false;
					callback.onFinished(result);
					progress.setVisibility(View.INVISIBLE);
				}

				@Override
				protected List<Movie> doInBackground(String... params) {
					// TODO Auto-generated method stub
					return callback.onExcute(params[0]);
				}};
			task.execute(url);
		}
			
		
	}
	
	public void loadData(String url,final onLoadingDataCallBack callback){
		if(isLoading&&task!=null){
			return;
		}else{
			task = new AsyncTask<String, Void, List<Movie>>(){
				
				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					isLoading = true;
					callback.onPrepare();
				}

				@Override
				protected void onPostExecute(List<Movie> result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					isLoading = false;
					callback.onFinished(result);
				}

				@Override
				protected List<Movie> doInBackground(String... params) {
					// TODO Auto-generated method stub
					return callback.onExcute(params[0]);
				}};
			task.execute(url);
		}
	}
	
	public interface onLoadingDataCallBack{
		/**
		 * //show prograss
		 * @param progress
		 */
		public void onPrepare();
		/**
		 * handle your data here   //loading data
		 * attach your data to the view
		 * by use contentView.findViewById(R.id.xx);
		 * 
		 * @param url
		 * @return
		 */
		public List<Movie> onExcute(String url);
		/**
		 * //hide ..
		 * @param result 
		 * @param progress
		 */
		public void onFinished(List<Movie> result);
	}
	
	
	public void loadPage(String url,onLoadingDataCallBack cb){
		
	}
	
	
}
