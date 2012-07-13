package com.rushfusion.mat.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rushfusion.mat.R;

public class Service_WebViewPlayer extends Activity {

	private WebView wv;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewplayer);
		Intent i = getIntent();
		String url = i.getStringExtra("url");
		wv = (WebView) findViewById(R.id.webView1);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setPluginsEnabled(true);
		wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.loadUrl(url);
		wv.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(wv!=null)
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
	        wv.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(wv!=null){
			wv.pauseTimers();
			wv.stopLoading();
			wv.destroy();
		}
		finish();
		super.onPause();
	}
}
