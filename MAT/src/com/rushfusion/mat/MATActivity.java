package com.rushfusion.mat;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.video.db.MatDBManager;
import com.rushfusion.mat.video.db.TestDB;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private View menu;
	private View conditionBar;
	private static String currentOrigin="";
	private static String currentLevel1="";
	private static String currentLevel2="";
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }
    
    
	private void init() {
		sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
		editor = sp.edit();
		currentOrigin = getLastWatchRecord();
    	initMenu();
    	initLevel1(currentOrigin);
    	initConditionBar();
    	initRecommendPage();//data?
    	updateHeaderInfo();
	}

	private void initLevel1(String origin) {
		// TODO Auto-generated method stub
		ViewGroup level1 = (ViewGroup) findViewById(R.id.level1);
		level1.removeAllViews();
		List<String> level1s = TestDB.getInstance(this).getLevel1(origin);
		for(int i = 0;i<level1s.size();i++){
			Button btn = new Button(this);
			final String name = level1s.get(i);
			btn.setText(name);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentLevel1 = name;
					updateLevel2(currentLevel1);
					String url = "www.x.x/xx?type=all&area=all&year=all";
					updatePage(url);
					
				}
			});
			level1.addView(btn);
		}
	}

	private void updatePage(String url) {
		updateHeaderInfo();
		// TODO Auto-generated method stub
		if(currentLevel1.equals("首页")){
			initRecommendPage();
		}else{
			//to other page...
		}
		
	}


	protected void updateLevel2(String currentLevel1) {
		// TODO Auto-generated method stub
		Log.d("MAT", "currentLevel1==>"+currentLevel1);
		ViewGroup level2 = (ViewGroup) findViewById(R.id.level2);
		level2.removeAllViews();
		String param = "";
		if(currentLevel1.equals("电影")){
			param = MatDBManager.MOVIE;
		} 
		
		if(currentLevel1.equals("电视剧")) {
			param = MatDBManager.TELEPLAY ;
		}
		List<String> level2s = TestDB.getInstance(MATActivity.this).getLevel2(param);
		for(int i = 0;i<level2s.size();i++){
			Button btn = new Button(this);
			final String name = level2s.get(i);
			btn.setText(name);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentLevel2 = name;
					String url = "www.xx.xx/xx?type="+currentLevel2+"&area=all&year=all";
					updatePage(url);
				}
			});
			level2.addView(btn);
		}
	}


	private void initConditionBar() {
		// TODO Auto-generated method stub
		conditionBar = findViewById(R.id.conditionBar);
		ViewGroup areaView = (ViewGroup) conditionBar.findViewById(R.id.byArea);
		ViewGroup yearView = (ViewGroup) conditionBar.findViewById(R.id.byYear);
		areaView.removeAllViews();
		yearView.removeAllViews();
		
		final List<String> areas = getAreas();
		if(areas!=null)
		for(int i = 0;i<areas.size();i++){
			Button cdtBtn = new Button(this);
			final String area = areas.get(i);
			cdtBtn.setText(area);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String url = "www.xx.xx/xx?type="+currentLevel2+"&area="+area+"&year=all";
					//TestDB.getInstance(MATActivity.this).queryAllByType(MatDBManager.MOVIE, area, 0, 6) ;
					updatePage(url);
				}
			});
			areaView.addView(cdtBtn);
		}
		
		final List<Integer> years = getYears();
		if(years!=null)
		for(int i = 0;i<years.size();i++){
			Button cdtBtn = new Button(this);
			final int year = years.get(i);
			cdtBtn.setText(year);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String url = "www.xx.xx/xx?type="+currentLevel2+"&area=all&year="+year;
					//TestDB.getInstance(MATActivity.this).queryAllByYear(MatDBManager.MOVIE, year, 0, 6) ;
					updatePage(url);
				}
			});
			yearView.addView(cdtBtn);
		}
		
		
	}
	private List<Integer> getYears() {
		// TODO Auto-generated method stub
		return null;
	}


	private List<String> getAreas() {
		// TODO Auto-generated method stub
		return null;
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


	private void updateHeaderInfo() {
		// TODO Auto-generated method stub
		TextView headerInfo = (TextView) findViewById(R.id.headerInfo);
		headerInfo.setText(currentOrigin+">"+currentLevel1+">"+currentLevel2);
	}

    private String getLastWatchRecord() {
		// TODO Auto-generated method stub
		return sp.getString("origin", "qiyi");
	}
    private void updateLastWatchRecord(String origin){
    	currentOrigin = origin;
    	new AsyncTask<String, String, String>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				updateHeaderInfo();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				editor.putString("origin", params[0]);
				editor.commit();
				return null;
			}
		}.execute(origin);
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
			updateLastWatchRecord("乐视");
			changeUrlByOriginName("leshi");
			break;
		case R.id.qiyi:
			updateLastWatchRecord("奇艺");
			changeUrlByOriginName("qiyi");
			break;
		case R.id.souhu:
			updateLastWatchRecord("搜狐");
			changeUrlByOriginName("souhu");
			break;
		case R.id.tudou:
			updateLastWatchRecord("土豆");
			changeUrlByOriginName("tudou");
			break;
		case R.id.sina:
			updateLastWatchRecord("新浪");
			changeUrlByOriginName("sina");
			break;
		//==================================
			
		default:
			break;
		}
	}
	
	
	private void changeUrlByOriginName(String string) {
		// TODO Auto-generated method stub
		
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
