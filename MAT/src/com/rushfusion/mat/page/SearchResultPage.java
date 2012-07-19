package com.rushfusion.mat.page;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rushfusion.mat.MATActivity;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;

public class SearchResultPage extends BasePage {
	private String mCurrentOrigin;
	private String mCurrentCategory;
	
	private String key = "";
	

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
	}
	

	public void loadPage(String url,int layoutId) {
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
			}
			
			@Override
			public List<Map<String, String>> onExcute(String url) {
				DataParser parser = DataParser.getInstance(context, "") ;
				return parser.get(url) ;
			}

			@Override
			public void onFinished(List<Map<String, String>> result) {
				if(result!=null&&result.size()>0){
				}else{
					String recommendUrl = "http://tvsrv.webhop.net:9061/query?source="
				+mCurrentOrigin+"&category="+mCurrentCategory+"&sort=play&page=1&pagesize=6";
					loadPage(recommendUrl, R.layout.page_search_noresult,new BasePage.onLoadingDataCallBack() {
						
						@Override
						public void onPrepare() {
							TextView key = (TextView) contentView.findViewById(R.id.search_failed_key);
							String msg = "非常抱歉，没有找到与\""+getKey()+"\"相关的内容。";
							SpannableStringBuilder style=new SpannableStringBuilder(msg);
							int a = msg.indexOf("\"")+1;
							int b = msg.lastIndexOf("\"");
							style.setSpan(new ForegroundColorSpan(Color.RED),a,b,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							key.setText(style);
							Button research = (Button) contentView.findViewById(R.id.search_result_researchBtn);
							research.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									context.showDialog(MATActivity.DIALOG_SEARCH);
								}
							});
						}
						@Override
						public List<Map<String, String>> onExcute(String url) {
							DataParser parser = DataParser.getInstance(context, "") ;
							return parser.get(url) ;
						}
						
						@Override
						public void onFinished(List<Map<String, String>> result) {
							if(result==null)return;
						}
						
					});
					
				}
			}
		});
	}
	

	@Override
	public void onKill() {
		Log.w("MAT", "onKill--SearchResultPage");
	}

	
}
