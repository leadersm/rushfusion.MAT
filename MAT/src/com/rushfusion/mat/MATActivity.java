package com.rushfusion.mat;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.rushfusion.mat.bean.Movie;
import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.utils.db.MatDBManager;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	View menu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init(getLastWatchRecord());
    }
	private void init(String name) {
		initDataByName(name);
    	initMenu();
    	initRecommendPage();//data?
    	updateHeaderInfo();
	}

	private void initMenu() {
		// TODO Auto-generated method stub
		menu = findViewById(R.id.menu);
		Button leshi = (Button) findViewById(R.id.leshi);
		Button qiyi = (Button) findViewById(R.id.qiyi);
		Button souhu = (Button) findViewById(R.id.souhu);
		Button tudou = (Button) findViewById(R.id.tudou);
		Button sina = (Button) findViewById(R.id.sina);
		leshi.setOnClickListener(this);
		qiyi.setOnClickListener(this);
		souhu.setOnClickListener(this);
		tudou.setOnClickListener(this);
		sina.setOnClickListener(this);
	}
	/**
	 * 根据上次网址名称查询相应数据库获得数据
	 * @param name
	 */
	private void initDataByName(String name) {
		// TODO Auto-generated method stub
//        query() ;
		
	}


	private void updateHeaderInfo() {
		// TODO Auto-generated method stub
		
	}

    private String getLastWatchRecord() {
		// TODO Auto-generated method stub
    	SharedPreferences sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
    	String result = sp.getString("name", "qiyi");
		return result;
	}
    private void updateLastWatchRecord(String name){
    	SharedPreferences sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putString("name", name);
    	editor.commit();
    }
	
	
	public void query() {
    	MatDBManager dbManager = MatDBManager.getInstance(this) ;
    	dbManager.openDatabase() ;
    	List<Movie> movies = dbManager.getAllMovie(0,6) ;
    	Log.d("电影", movies.toString()) ;
    }

	private void initRecommendPage() {
		ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage("url?data?……TBD",R.layout.page_recommend);
		recommendPage.setPageCache(recommendPage, R.layout.page_recommend);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.leshi:
			updateLastWatchRecord("leshi");
			break;
		case R.id.qiyi:
			updateLastWatchRecord("qiyi");
			break;
		case R.id.souhu:
			updateLastWatchRecord("souhu");
			break;
		case R.id.tudou:
			updateLastWatchRecord("tudou");
			break;
		case R.id.sina:
			updateLastWatchRecord("sina");
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			showMenu();
			break;
		case KeyEvent.KEYCODE_BACK:
			showDialog(0);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("确定退出吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dismissDialog(0);
			}
		});
		return builder.create();
	}

	private void showMenu() {
		// TODO Auto-generated method stub
		if(menu.getVisibility()==View.VISIBLE)
			menu.setVisibility(View.GONE);
		else
			menu.setVisibility(View.VISIBLE);
	}

	
	@Override
	protected void onPause() {
		super.onStop();
		PageCache.getInstance().release();
	}
	
}
