package com.rushfusion.mat;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.rushfusion.mat.page.RecommendPage;

public class MATActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        initRecommendPage(parent);
    }

	private void initRecommendPage(ViewGroup parent) {
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage("url?data?……TBD",R.layout.page_recommend);
		recommendPage.setPageCache(recommendPage, R.layout.page_recommend);
	}

}