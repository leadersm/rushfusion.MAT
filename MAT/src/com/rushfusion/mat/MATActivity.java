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
	private String currentCategory="";
	private String currentType="";
	
	List<String> categories;
	List<String> types;
	
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
		currentOrigin = "sina";//getLastWatchRecord().equals("")?"sina":getLastWatchRecord();
    	initMenu();
    	initCategory(currentOrigin);
    	initChooseBar();
    	initConditionBar();
    	currentCategory = "首页";//??
    	initRecommendPage();
    	updateHeaderInfo();
	}

	private void initChooseBar() {
		// TODO Auto-generated method stub
		Button byRecent = (Button) findViewById(R.id.byRecent);
		Button byScore = (Button) findViewById(R.id.byScore);
		Button byComment = (Button) findViewById(R.id.byComment);
		Button byCondition = (Button) findViewById(R.id.byCondition);
		byRecent.setOnClickListener(this);
		byScore.setOnClickListener(this);
		byComment.setOnClickListener(this);
		byCondition.setOnClickListener(this);
	}


	private void initCategory(String origin) {
		// TODO Auto-generated method stub
		ViewGroup level1 = (ViewGroup) findViewById(R.id.level1);
		level1.removeAllViews();
		categories = DataParser.getInstance(this,currentOrigin).getCategory();
		for(int i = 0;i<categories.size();i++){
			Button btn = new Button(this);
			final String name = categories.get(i);
			btn.setText(name);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			    	new AsyncTask<String, String, String>(){

						@Override
						protected String doInBackground(String... params) {
							currentCategory = params[0];
							initConditionBar();
							currentType = types.contains(currentType)?currentType:"";
							return currentType;
						}
						@Override
						protected void onPostExecute(String result) {
							super.onPostExecute(result);
							updatePage(currentCategory,result, null, null, null);
						}
						
					}.execute(name);
					
				}
			});
			level1.addView(btn);
		}
	}

	/**
	 * 
	 * @param category
	 * @param type
	 * @param year
	 * @param area
	 * @param sort
	 */
	private void updatePage(String category,String type,String year,String area,String sort) {
		// TODO Auto-generated method stub
		String url = "http://tvsrv.webhop.net:9061/query?source="+currentOrigin+"&category="+category+"&area="+area+"&sort="+sort+"&page=1&pagesize=8";
		updateHeaderInfo();
		if(currentCategory.equals("首页")){
			initRecommendPage();
		}else{
			//to other page...
		}
		
	}

	private void initConditionBar() {
		// TODO Auto-generated method stub
		conditionBar = findViewById(R.id.conditionBar);
		ViewGroup typeView = (ViewGroup) conditionBar.findViewById(R.id.byType);
		ViewGroup areaView = (ViewGroup) conditionBar.findViewById(R.id.byArea);
		ViewGroup yearView = (ViewGroup) conditionBar.findViewById(R.id.byYear);
		
		typeView.removeAllViews();
		areaView.removeAllViews();
		yearView.removeAllViews();
		
		types = DataParser.getInstance(this,currentOrigin).getTypes();
		if(types!=null)
			for(int i = 0;i<types.size();i++){
				Button btn = new Button(this);
				final String name = types.get(i);
				btn.setText(name);
				btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						currentType = name;
						updatePage(currentCategory,currentType, null, null, null);
					}
				});
				typeView.addView(btn);
			}
		
		final List<String> areas = DataParser.getInstance(this,currentOrigin).getAreas();
		if(areas!=null)
		for(int i = 0;i<areas.size();i++){
			Button cdtBtn = new Button(this);
			final String area = areas.get(i);
			cdtBtn.setText(area);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					updatePage(currentCategory,null, null, area, null);
				}
			});
			areaView.addView(cdtBtn);
		}
		
		final List<String> years = DataParser.getInstance(this,currentOrigin).getYears();
		if(years!=null)
		for(int i = 0;i<years.size();i++){
			Button cdtBtn = new Button(this);
			final String year = years.get(i);
			cdtBtn.setText(year);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					updatePage(currentCategory,null, year, null, null);
				}
			});
			yearView.addView(cdtBtn);
		}
		
		
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
		headerInfo.setText(currentOrigin+">"+currentCategory+">"+currentType);
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
		View level2 = findViewById(R.id.level_2);
		level2.setVisibility(View.GONE);
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage("url?data?……TBD",R.layout.page_recommend);
		recommendPage.setPageCache(recommendPage, R.layout.page_recommend);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leshi:
//			updateLastWatchRecord("乐视");
			changeDataByOriginName("leshi");
			break;
		case R.id.qiyi:
//			updateLastWatchRecord("奇艺");
			changeDataByOriginName("qiyi");
			break;
		case R.id.souhu:
//			updateLastWatchRecord("搜狐");
			changeDataByOriginName("souhu");
			break;
		case R.id.tudou:
//			updateLastWatchRecord("土豆");
			changeDataByOriginName("tudou");
			break;
		case R.id.sina:
//			updateLastWatchRecord("新浪");
			changeDataByOriginName("sina");
			break;
		//==================================
		case R.id.byRecent:
			
			break;
		case R.id.byComment:
			
			break;
		case R.id.byScore:
			
			break;
		case R.id.byCondition:
			showConditionBar();
			break;
		//==================================
			
		default:
			break;
		}
	}
	
	
	private void showConditionBar() {
		// TODO Auto-generated method stub
		if(conditionBar.getVisibility()==View.VISIBLE){
			conditionBar.setVisibility(View.INVISIBLE);
		}else
			conditionBar.setVisibility(View.VISIBLE);
	}


	private void changeDataByOriginName(String origin) {
		// TODO Auto-generated method stub
		initCategory(origin);
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
