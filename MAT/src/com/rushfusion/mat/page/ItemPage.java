package com.rushfusion.mat.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class ItemPage extends BasePage {

	public ItemPage(Context context, ViewGroup parent) {
		super(context, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadPage(String url, int layoutId) {
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.VISIBLE);
			}
			
			@Override
			public boolean onExcute(String url) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onFinshed(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.INVISIBLE);
			}
		});
	}

}
