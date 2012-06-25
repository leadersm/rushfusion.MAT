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
import com.rushfusion.mat.utils.DataParser;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private View menu;
	private View conditionBar;
	
	
	private String currentOrigin="";
	private String currentLevel1="";
	private String currentLevel2="";
	
	List<String> level1s;
	List<String> level2s;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
        editor = sp.edit();
        init();
    }
    
    
	private void init() {
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
		level1s = DataParser.getInstance(this,"").getCategory();
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
					currentLevel2 = level2s.contains(currentLevel2)?currentLevel2:"";
					updatePage(currentLevel1,currentLevel2);
				}
			});
			level1.addView(btn);
		}
	}

	private void updatePage(String level1,String level2) {
		// TODO Auto-generated method stub
		String url = "www.xx.xx/xx?type="+currentLevel2+"&area=all&year=all";//根据level1 和level2 拼url、、
//		String url = "www.xx.xx/xx?type="+currentLevel2+"&area="+area+"&year=all";
		updateHeaderInfo();
		if(currentLevel1.equals("首页")){
			initRecommendPage();
		}else{
			//to other page...
		}
		
	}


	protected void updateLevel2(final String level1) {
		// TODO Auto-generated method stub
		Log.d("MAT", "currentLevel1==>"+level1);
		ViewGroup level2 = (ViewGroup) findViewById(R.id.level2);
		level2.removeAllViews();
		String param = "";
/*<<<<<<< HEAD
		if(currentLevel1.equals("电影")){
			param = MatDBManager.MOVIE;
		} 
		
		if(currentLevel1.equals("电视剧")) {
			param = MatDBManager.TELEPLAY ;
		}
		List<String> level2s = TestDB.getInstance(MATActivity.this).getLevel2(param);
=======
		level2s = DataParser.getInstance(this).getTypes();
>>>>>>> refs/remotes/origin/lsm
*/		for(int i = 0;i<level2s.size();i++){
			Button btn = new Button(this);
			final String name = level2s.get(i);
			btn.setText(name);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentLevel2 = name;
					updatePage(level1,currentLevel2);
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
					updatePage(currentLevel1,currentLevel2);
				}
			});
			areaView.addView(cdtBtn);
		}
		
		final List<String> years = getYears();
		if(years!=null)
		for(int i = 0;i<years.size();i++){
			Button cdtBtn = new Button(this);
			final String year = years.get(i);
			cdtBtn.setText(year);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String url = "www.xx.xx/xx?type="+currentLevel2+"&area=all&year="+year;
					//TestDB.getInstance(MATActivity.this).queryAllByYear(MatDBManager.MOVIE, year, 0, 6) ;
					updatePage(currentLevel1,currentLevel2);
				}
			});
			yearView.addView(cdtBtn);
		}
		
		
	}
	private List<String> getYears() {
		return DataParser.getInstance(this,"").getYears();
	}


	private List<String> getAreas() {
		return DataParser.getInstance(this,"").getAreas();
	}


	private void initMenu() {
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
		TextView headerInfo = (TextView) findViewById(R.id.headerInfo);
		headerInfo.setText(currentOrigin+">"+currentLevel1+">"+currentLevel2);
	}

    private String getLastWatchRecord() {
		return sp.getString("origin", "qiyi");
	}
    private void updateLastWatchRecord(String origin){
    	currentOrigin = origin;
    	new AsyncTask<String, String, String>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				updateHeaderInfo();
			}
			@Override
			protected String doInBackground(String... params) {
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
		switch (v.getId()) {
		case R.id.leshi:
			updateLastWatchRecord("乐视");
			changeDataByOriginName("leshi");
			break;
		case R.id.qiyi:
			updateLastWatchRecord("奇艺");
			changeDataByOriginName("qiyi");
			break;
		case R.id.souhu:
			updateLastWatchRecord("搜狐");
			changeDataByOriginName("souhu");
			break;
		case R.id.tudou:
			updateLastWatchRecord("土豆");
			changeDataByOriginName("tudou");
			break;
		case R.id.sina:
			updateLastWatchRecord("新浪");
			changeDataByOriginName("sina");
			break;
		//==================================
			
		default:
			break;
		}
	}
	
	
	private void changeDataByOriginName(String origin) {
		// TODO Auto-generated method stub
		initLevel1(origin);
		if(level1s.contains(currentLevel1))
			updateLevel2(currentLevel1);
		else
			updateLevel2(level1s.get(0));
		updatePage(currentLevel1,currentLevel2);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("确定退出吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissDialog(0);
			}
		});
		return builder.create();
	}

	private void showMenu() {
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
