package com.rushfusion.mat.page;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rushfusion.mat.R;

public abstract class BasePage {

	private boolean isLoading = false;
	public View contentView;
	
	private Context context;
	private ViewGroup parent;
	public ProgressBar progress;
	
	public BasePage(Context context,ViewGroup parent) {
		this.context = context;
		this.parent = parent;
		progress = (ProgressBar) parent.findViewById(R.id.content_progressbar);
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
	
	public void loadPage(String url,int layoutId,onLoadingDataCallBack callback){
		isLoading = true;
		setContentView(layoutId);
		callback.onPrepare(progress);
		callback.onExcute(url);
		callback.onFinshed(progress);
		isLoading = false;
	}
	
	public interface onLoadingDataCallBack{
		/**
		 * //show prograss
		 * @param progress
		 */
		public void onPrepare(ProgressBar progress);
		/**
		 * handle your data here   //loading data
		 * @param url
		 * @return
		 */
		public boolean onExcute(String url);
		/**
		 * //hide ..
		 * @param progress
		 */
		public void onFinshed(ProgressBar progress);
	}
	
	
	
	static class PageCache{
		int layoutId;
		BasePage page;
		static PageCache cache;
		HashMap<Integer,BasePage> data = null;
		
		private PageCache(){
			data = new HashMap<Integer, BasePage>();
		}
		
		public static PageCache getInstance(){
			if(cache==null){
				cache = new PageCache();
			}
			return cache;
		}
		
		public void set(int key,BasePage value){
			data.put(key, value);
		}
		
		public BasePage get(int key){
			return data.get(key);
		}
		
		
		public void release(){
			data = null;
			cache = null;		
		}
	}
	
	
	
	
	
	
	
}
