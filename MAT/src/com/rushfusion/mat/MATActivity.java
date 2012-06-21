package com.rushfusion.mat;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.video.db.MatDBManager;
import com.rushfusion.mat.video.entity.Movie;

public class MATActivity extends Activity {
    /** Called when the activity is first created. */
	View menu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        menu = findViewById(R.id.menu);
        initRecommendPage(parent);
        
    }

	private void initRecommendPage(ViewGroup parent) {
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage("url?data?……TBD",R.layout.page_recommend);
		recommendPage.setPageCache(recommendPage, R.layout.page_recommend);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			showMenu();
			break;

		default:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	private void showMenu() {
		// TODO Auto-generated method stub
		if(menu.getVisibility()==View.VISIBLE)
			menu.setVisibility(View.GONE);
		else
			menu.setVisibility(View.VISIBLE);
	}

	
	
	
	
	
}
