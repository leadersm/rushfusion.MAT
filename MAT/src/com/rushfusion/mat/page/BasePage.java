package com.rushfusion.mat.page;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class BasePage {

	private boolean isLoading = false;
	private View contentView;
	private HashMap<Integer,BasePage> pageCache = new HashMap<Integer,BasePage>();
	
	private Context context;
	private ViewGroup parent;
	
	
	public BasePage(Context context,ViewGroup parent) {
		this.context = context;
		this.parent = parent;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public View getContentView() {
		return contentView;
	}

	public void setContentView(int layoutId) {
		parent.removeAllViews();
		View contentView ;
		if(pageCache.get(layoutId)!=null){
			contentView = pageCache.get(layoutId).getContentView();
		}else
			contentView = LinearLayout.inflate(context, layoutId, null);
		this.contentView = contentView;
		parent.addView(contentView);
	}

	public abstract void loadingData(String json,onLoadingDataCallBack callback);
	
	public interface onLoadingDataCallBack{
		public boolean onLoading();//show prograss 
		public boolean onFinshed();//hide ..
	}
	
	
	
	
	
	
	
	
	
	
	
}
