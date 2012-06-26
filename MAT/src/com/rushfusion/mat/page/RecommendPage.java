package com.rushfusion.mat.page;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.ViewGroup;

public class RecommendPage extends BasePage {

	public RecommendPage(Activity context, ViewGroup parent) {
		super(context, parent);
	}

	public void loadPage(String url,int layoutId) {
		// TODO Auto-generated method stub
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<Map<String, String>> onExcute(String url) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void onFinished(List<Map<String, String>> result) {
				// TODO Auto-generated method stub
				
			}

		});
	}
}

