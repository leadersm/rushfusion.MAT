package com.rushfusion.mat;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rushfusion.mat.page.FilmClassPage;
import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.utils.DataParser;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private static final int FilmClassPage = 1;
	private static final int FilmClassPageSize = 8;
	
	private ViewGroup parent;
	private View menu;
	private View conditionBar;
	private View chooseBar;
	
	private String currentOrigin="";
	private String currentCategory="";
	private String currentType="";
	private String currentYear="";
	private String currentArea="";
	private String currentSort="";
	
	
	private List<String> categories;
	private HashMap<String,List<String>> conditions = new HashMap<String, List<String>>();
	private List<String> types;
	private List<String> years;
	private List<String> areas;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
        editor = sp.edit();
        init();
    }
    
    
	private void init() {
		currentOrigin = "sina";//getLastWatchRecord().equals("")?"sina":getLastWatchRecord();
    	initMenu();
    	showMenu();
    	initCategory(currentOrigin);
    	currentCategory = "首页";//??
    	initChooseBar();
    	parent = (ViewGroup) findViewById(R.id.parent);
    	String url = "shouye url ???";
    	initRecommendPage(url);
    	updateHeaderInfo();
	}

	private void initChooseBar() {
		// TODO Auto-generated method stub
		chooseBar = findViewById(R.id.level_2);
		if(currentCategory.equals("首页"))
			chooseBar.setVisibility(View.GONE);
		else
			chooseBar.setVisibility(View.VISIBLE);
		Button byPlay = (Button) findViewById(R.id.byPlay);
		Button byRecent = (Button) findViewById(R.id.byRecent);
		Button byScore = (Button) findViewById(R.id.byScore);
		Button byComment = (Button) findViewById(R.id.byComment);
		Button byCondition = (Button) findViewById(R.id.byCondition);
		
		byPlay.setOnClickListener(this);
		byRecent.setOnClickListener(this);
		byScore.setOnClickListener(this);
		byComment.setOnClickListener(this);
		byCondition.setOnClickListener(this);
	}


	private void initCategory(String origin) {
		// TODO Auto-generated method stub
		final ViewGroup level1 = (ViewGroup) findViewById(R.id.level1);
		level1.removeAllViews();
		Button shouye = new Button(this);
		shouye.setText("首页");
		shouye.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentCategory = "首页";
				initConditionBar();
				while(types==null){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		level1.addView(shouye);

		new AsyncTask<String, Void, List<String>>(){

			@Override
			protected List<String> doInBackground(String... params) {
				// TODO Auto-generated method stub
				categories = DataParser.getInstance(MATActivity.this,params[0]).getCategory();
				return categories;
			}

			@Override
			protected void onPostExecute(List<String> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result!=null)
				for(int i = 0;i<categories.size();i++){
					Button btn = new Button(MATActivity.this);
					final String name = categories.get(i);
					btn.setText(name);
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							currentCategory = name;
							initConditionBar();
							while(types==null||areas==null||years==null){
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							currentType = types.contains(currentType)?currentType:"";
							currentArea = areas.contains(currentArea)?currentArea:"";
							currentYear = years.contains(currentYear)?currentYear:"";
							currentSort = "";
							updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
						}
					});
					level1.addView(btn);
				}
				
			}
			
		}.execute(currentOrigin);
	}


	private void initConditionBar() {
		// TODO Auto-generated method stub
		conditionBar = findViewById(R.id.conditionBar);
		if(currentCategory.equals("首页")){
			conditionBar.setVisibility(View.GONE);
			chooseBar.setVisibility(View.GONE);
		}else
			chooseBar.setVisibility(View.VISIBLE);
		final ViewGroup typeView = (ViewGroup) conditionBar.findViewById(R.id.byType);
		final ViewGroup areaView = (ViewGroup) conditionBar.findViewById(R.id.byArea);
		final ViewGroup yearView = (ViewGroup) conditionBar.findViewById(R.id.byYear);
		
		new AsyncTask<String, Void, HashMap<String,List<String>>>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if(types!=null)
				types.clear();
				if(years!=null)
				years.clear();
				if(areas!=null)
				areas.clear();
			}

			@Override
			protected HashMap<String,List<String>> doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				types = DataParser.getInstance(MATActivity.this,currentOrigin).getTypes(params[0]);
				years = DataParser.getInstance(MATActivity.this,currentOrigin).getYears(params[0]);
				areas = DataParser.getInstance(MATActivity.this,currentOrigin).getAreas(params[0]);
				conditions.put("type", types);
				conditions.put("year", years);
				conditions.put("area", areas);
				return conditions;
			}
			
			@Override
			protected void onPostExecute(HashMap<String,List<String>> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result!=null){
					List<String> types = result.get("type");
					List<String> years = result.get("year");
					List<String> areas = result.get("area");
					addConditionButtons(typeView, areaView, yearView, types, years, areas);
				}
			}
		}.execute(currentCategory);
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

	private void addConditionButtons(final ViewGroup typeView,
			final ViewGroup areaView, final ViewGroup yearView,
			List<String> types, List<String> years, List<String> areas) {
		
		typeView.removeAllViews();
		areaView.removeAllViews();
		yearView.removeAllViews();
		
		Button allType = new Button(this);
		Button allYear = new Button(this);
		Button allArea = new Button(this);
		
		allType.setText("全部");
		allYear.setText("全部");
		allArea.setText("全部");
		
		allType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentType = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allArea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentArea = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allYear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentYear = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		
		typeView.addView(allType);
		areaView.addView(allYear);
		yearView.addView(allArea);
		
		
		for(int i = 0;i<types.size();i++){
			Button cdtBtn = new Button(MATActivity.this);
			final String type = types.get(i);
			cdtBtn.setText(type);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentType = type;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			typeView.addView(cdtBtn);
		}
		
		for(int i = 0;i<areas.size();i++){
			Button cdtBtn = new Button(MATActivity.this);
			final String area = areas.get(i);
			cdtBtn.setText(area);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentArea = area;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			areaView.addView(cdtBtn);
		}
		
		for(int i = 0;i<years.size();i++){
			Button cdtBtn = new Button(MATActivity.this);
			final String year = years.get(i);
			cdtBtn.setText(year);
			cdtBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentYear = year;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			yearView.addView(cdtBtn);
		}
	}
	

	private void updateHeaderInfo() {
		TextView headerInfo = (TextView) findViewById(R.id.headerInfo);
		headerInfo.setText(currentOrigin+">"+currentCategory+">"+currentType);
	}

    
	/**
	 * 
	 * @param category
	 * @param type
	 * @param year
	 * @param area
	 * @param sort
	 */
	private void updatePage(String category,String type,String area,String year,String sort) {
		// TODO Auto-generated method stub
		StringBuffer baseUrl = new StringBuffer("http://tvsrv.webhop.net:9061/query?source="+currentOrigin);
		if(!category.equals(""))baseUrl.append("&category="+category);
		if(!type.equals(""))baseUrl.append("&type="+type);
		if(!area.equals(""))baseUrl.append("&area="+area);
		if(!year.equals(""))baseUrl.append("&year="+year);
		if(!sort.equals(""))baseUrl.append("&sort="+sort);
		baseUrl.append("&page="+FilmClassPage+"&pagesize="+FilmClassPageSize);
		String url = baseUrl.toString();
		Log.d("MAT","LoadPage url==>"+url);
		updateHeaderInfo();
		if(currentCategory.equals("首页")){
			initRecommendPage(url);
		}else{
			//to other page...
			initFilmClassPage(url);
		}
		
	}
	
	private void initRecommendPage(String url) {
		RecommendPage recommendPage = new RecommendPage(this,parent);
		recommendPage.loadPage(url,R.layout.page_recommend);
		recommendPage.setPageCache(recommendPage, R.layout.page_recommend);
		
	}

	private void initFilmClassPage(String url) {
		FilmClassPage page = new FilmClassPage(this, parent);
		page.loadPage(url, R.layout.page_film_class);
		page.setPageCache(page, R.layout.page_film_class);
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
		case R.id.byPlay:
			currentSort = "play";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byComment:
			currentSort = "comment";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);			
			break;
		case R.id.byScore:
			currentSort = "score";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byRecent:
			currentSort = "recent";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
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
		if(id==0){
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
		}else if(id==1){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("服务器无响应，请联系客服010-xxxxxxx");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			return builder.create();
		}else if(id == 2){
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("提示:");
			dialog.setMessage("正在加载中，请稍后");
			return dialog;
			
		}
		return null;
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
	
}
