package com.rushfusion.mat.page;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rushfusion.mat.R;

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
	
	private void setContentView(int layoutId) {
		parent.removeAllViews();
		View contentView ;
		if(PageCache.getInstance().get(layoutId)!=null){
			contentView = PageCache.getInstance().get(layoutId).getContentView();
		}else
			contentView = LayoutInflater.from(context).inflate(layoutId, null);
		this.contentView = contentView;
		parent.addView(contentView);
	}

	public void setPageCache(BasePage page,int layoutId){
		PageCache.getInstance().set(layoutId, page);
	}
	
	private onLoadingDataCallBack callback;
	
	public AsyncTask<String, Void, List<Map<String, String>>> task = new AsyncTask<String, Void, List<Map<String, String>>>(){
		
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
		protected void onPostExecute(List<Map<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			isLoading = false;
			callback.onFinished(result);
			progress.setVisibility(View.INVISIBLE);
		}

		@Override
		protected List<Map<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return callback.onExcute(params[0]);
		}};
	
	public void loadPage(String url,int layoutId,final onLoadingDataCallBack callback){
		setContentView(layoutId);
		this.callback = callback;
		if(isLoading){
			task.cancel(true);
		}else{
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
		public List<Map<String, String>> onExcute(String url);
		/**
		 * //hide ..
		 * @param result 
		 * @param progress
		 */
		public void onFinished(List<Map<String,String>> result);
	}
	
	
	
}
