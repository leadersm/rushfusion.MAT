package com.rushfusion.mat.page;

import android.app.Activity;
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
	
	public void loadPage(String url,int layoutId,onLoadingDataCallBack callback){
		isLoading = true;
		setContentView(layoutId);
		context.showDialog(1);
		callback.onPrepare(progress);
		callback.onExcute(url);
		context.dismissDialog(1);
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
		 * attach your data to the view
		 * by use contentView.findViewById(R.id.xx);
		 * 
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
	
	
	
}
