package com.rushfusion.mat;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rushfusion.mat.page.BasePage;
import com.rushfusion.mat.page.RecommendPage;

public class MATActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
        init(parent);
    }

	private void init(ViewGroup parent) {
		BasePage recommendPage = new RecommendPage(this,parent);
		recommendPage.setContentView(R.layout.main);
		recommendPage.loadingData("json ?? data?", new BasePage.onLoadingDataCallBack(){

			@Override
			public boolean onLoading() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onFinshed() {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}

}