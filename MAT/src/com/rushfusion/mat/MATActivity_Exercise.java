package com.rushfusion.mat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.rushfusion.mat.control.ReceiveService;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.HttpUtil;

public class MATActivity_Exercise extends Activity implements OnClickListener{
	private static final int FilmClassPage = 1;
	private static final int FilmClassPageSize = 8;
	
	public static final int DIALOG_EXIT = 0;
	public static final int DIALOG_CONNECTEDREFUSED = 1;
	public static final int DIALOG_LOADING = 2;
	public static final int DIALOG_CONDITIONBAR = 3;
	public static final int DIALOG_WIRELESS_SETTING = 4;
	public static final int DIALOG_ORIGIN_MENU = 5;
	public static final int DIALOG_SEARCH  = 6;
	public static final int DIALOG_NO_SOURCE = 7;
	
	private ViewGroup parent;
	private ViewGroup menu;
	private ViewGroup conditionBar;
	private ViewGroup level2;
	private ViewGroup searchBar;
	
	private String currentOrigin = "sina";
	private String currentOriginName = "新浪视频";
	private String currentCategory = "首页";
	private String currentType = "";
	private String currentYear = "";
	private String currentArea = "";
	private String currentSort = "play";
	private String currentSortInfo = "";
	
	private List<String> categories;
	private List<String> types;
	private List<String> years;
	private List<String> areas;
	private HashMap<String, List<String>> conditions = new HashMap<String, List<String>>();
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(HttpUtil.checkNetworkEnabled(this)){
			init();
			showDialog(DIALOG_ORIGIN_MENU);
		}else{
			showDialog(DIALOG_WIRELESS_SETTING);
		}
	}
	private void init() {
		// TODO Auto-generated method stub
		parent = (ViewGroup)findViewById(R.id.parent);
		conditionBar = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.conditionbar, null);
		level2 = (ViewGroup)findViewById(R.id.level_2);
		res = getResources();
		sp = getSharedPreferences("matHistory", Context.MODE_WORLD_WRITEABLE);
		editor = sp.edit();
		initSearchBar();
		updateHeaderInfo();
		searchSTBs();
	}
	private void searchSTBs() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,ReceiveService.class);
		startService(i);
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case DIALOG_CONNECTEDREFUSED:
				showDialog(DIALOG_CONNECTEDREFUSED);
				break;
			default:
				break;
			}
		}
	};
	
	public void initChooseBar(){
		Button byPlay = (Button)findViewById(R.id.byPlay);
		Button byRecent = (Button)findViewById(R.id.byRecent);
		Button byScore = (Button)findViewById(R.id.byScore);
		Button byComment = (Button)findViewById(R.id.byComment);
		Button bySearch = (Button)findViewById(R.id.bySearch);
		Button byCondition = (Button)findViewById(R.id.byCondition);
		
		byPlay.setOnClickListener(this);
		byRecent.setOnClickListener(this);
		byScore.setOnClickListener(this);
		byComment.setOnClickListener(this);
		bySearch.setOnClickListener(this);
		byCondition.setOnClickListener(this);
		updateChooseBar(level2.findViewById(R.id.indicator_play));
	}
	
	public void initCategory(String origin){
		parent.removeAllViews();
		final ViewGroup level1 = (ViewGroup)findViewById(R.id.level1);
		level1.removeAllViews();
		new AsyncTask<String, Void, List<String>>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				level2.setVisibility(View.GONE);
				showDialog(DIALOG_LOADING);
			}

			@Override
			protected List<String> doInBackground(String... params) {
				// TODO Auto-generated method stub
				categories = DataParser.getInstance(MATActivity_Exercise.this,params[0]).getCategory();
				return categories;
			}

			@Override
			protected void onPostExecute(List<String> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				dismissDialog(DIALOG_LOADING);
				if(result == null){
					handler.sendEmptyMessage(DIALOG_CONNECTEDREFUSED);
					return;
				}
				Button shouye = new Button(MATActivity_Exercise.this);
				setCategoryBtnStyle(shouye,"首页");
				shouye.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						currentCategory = "首页";
						initConditionBar();
						currentSortInfo = "";
						updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
					}
				});
				level1.addView(shouye);
				for(int i=0;i<categories.size();i++){
					Button btn = new Button(MATActivity_Exercise.this);
					final String name = categories.get(i);
					btn.setText(name);
					setCategoryBtnStyle(btn, name);
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							currentCategory = name;
							initConditionBar();
							currentType = "";
							currentArea = "";
							currentYear = "";
							currentSort = "play";
							currentSortInfo = "热播";
							updatePage(currentCategory, currentType, currentArea, currentArea, currentSort);
						}
					});
					level1.addView(btn);
				}
				String url = "shouyeUrl ???";
				initRecommendPage(url);
				initChooseBar();
				updateHeaderInfo();
			}
		}.execute(currentOrigin);
	}
	
	protected void initRecommendPage(String url) {
		// TODO Auto-generated method stub
		
	}
	private void initConditionBar() {
		// TODO Auto-generated method stub
		if(currentCategory.equals("首页")){
			level2.setVisibility(View.GONE);
			return;
		}else{
			level2.setVisibility(View.VISIBLE);
		}
		final ViewGroup typeView = (ViewGroup)conditionBar.findViewById(R.id.byType);
		final ViewGroup yearView = (ViewGroup)conditionBar.findViewById(R.id.byYear);
		final ViewGroup areaView = (ViewGroup)conditionBar.findViewById(R.id.byArea);
		
		new AsyncTask<String, Void, HashMap<String, List<String>>>(){
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if(types != null){
					types.clear();
				}if(years != null){
					years.clear();
				}if(areas != null){
					areas.clear();
				}
			}
			
			@Override
			protected HashMap<String, List<String>> doInBackground(
					String... params) {
				types = DataParser.getInstance(MATActivity_Exercise.this, currentOrigin).getTypes(params[0]);
				years = DataParser.getInstance(MATActivity_Exercise.this, currentOrigin).getYears(params[0]);
				areas = DataParser.getInstance(MATActivity_Exercise.this, currentOrigin).getAreas(params[0]);
				conditions.put("type", types);
				conditions.put("year", years);
				conditions.put("area", areas);
				return conditions;
			}

			@Override
			protected void onPostExecute(HashMap<String, List<String>> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					List<String> types = result.get("type");
					List<String> years = result.get("year");
					List<String> areas = result.get("area");
					addConditionButtons(typeView,yearView,areaView,types,years,areas);
				}
			}

			
		}.execute(currentCategory);
	}
	
	public void initMenu(){
		String sourceUrl = "http://tvsrv.webhop.net:9061/source";
		menu = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.menu, null);
		final ViewGroup sourceGroup = (ViewGroup)menu.findViewById(R.id.source_group);
		sourceGroup.removeAllViews();
		final ProgressBar progress = (ProgressBar)menu.findViewById(R.id.menu_progress);
		new AsyncTask<String, Void, List<Map<String, String>>>(){
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				progress.setVisibility(View.VISIBLE);
			}
			@Override
			protected List<Map<String, String>> doInBackground(String... params) {
				// TODO Auto-generated method stub
				return DataParser.getInstance(MATActivity_Exercise.this, params[0]).getSource();
			}
			@Override
			protected void onPostExecute(List<Map<String, String>> result) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.INVISIBLE);
				if(result == null || result.size() <= 0){
					dismissDialog(DIALOG_ORIGIN_MENU);
					showDialog(DIALOG_NO_SOURCE);
					return;
				}
				for(int i=0;i<result.size();i++){
					ImageButton sourceBtn = new ImageButton(MATActivity_Exercise.this);
					final String name = result.get(i).get("name");
					final String source = result.get(i).get("source");
					final String logoUrl = result.get(i).get("logo");
					setSourceButtonSytle(sourceBtn,name,logoUrl);
					sourceBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							changeDataByOriginName(source,name);
							dismissDialog(DIALOG_ORIGIN_MENU);
						}
					});
					sourceGroup.addView(sourceBtn);
				}
				sourceGroup.getChildAt(0).requestFocus();
			}
		}.execute(sourceUrl);
	}
	private void changeDataByOriginName(String source,
			String name) {
		// TODO Auto-generated method stub
		
	}
	
	protected void setSourceButtonSytle(ImageButton sourceBtn, String name,
			String source) {
		// TODO Auto-generated method stub
		
	}
	protected void addConditionButtons(final ViewGroup typeView, final ViewGroup yearView,
			final ViewGroup areaView, List<String> types, List<String> years,
			List<String> areas) {
		// TODO Auto-generated method stub
		typeView.removeAllViews();
		yearView.removeAllViews();
		areaView.removeAllViews();
		
		final Button allType = new Button(this);
		final Button allYear = new Button(this);
		final Button allArea = new Button(this);
		
		setConditionBtnStyle(allType,"全部");
		setConditionBtnStyle(allYear,"全部");
		setConditionBtnStyle(allArea,"全部");
		
		allType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateBackground(typeView,allType);
				currentType = "";
				updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
			}
		});
		allYear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateBackground(yearView, allYear);
				currentYear = "";
				updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
			}
		});
		allArea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateBackground(areaView, allArea);
				currentArea = "";
				updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
			}
		});
		
		typeView.addView(allType);
		yearView.addView(allYear);
		areaView.addView(allArea);
		
		for(int i=0;i<types.size();i++){
			final Button typeBtn = new Button(MATActivity_Exercise.this);
			final String type = types.get(i);
			setConditionBtnStyle(typeBtn, type);
			typeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					updateBackground(typeView, typeBtn);
					currentType = type;
					updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
				}
			});
			typeView.addView(typeBtn);
		}
		
		for(int i=0;i<years.size();i++){
			final Button yearBtn = new Button(MATActivity_Exercise.this);
			final String year = years.get(i);
			setConditionBtnStyle(yearBtn, year);
			yearBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					updateBackground(yearView, yearBtn);
					currentYear = year;
					updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
				}
			});
			yearView.addView(yearBtn);
		}
		
		for(int i=0;i<areas.size();i++){
			final Button areaBtn = new Button(MATActivity_Exercise.this);
			final String area = areas.get(i);
			setConditionBtnStyle(areaBtn, area);
			areaBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					updateBackground(areaView, areaBtn);
					currentArea = area;
					updatePage(currentCategory, currentType, currentArea, currentYear, currentSort);
				}
			});
			areaView.addView(areaBtn);
		}
		
		typeView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
		yearView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
		areaView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
		
		
	}
	protected void updateBackground(ViewGroup view, Button destBtn) {
		// TODO Auto-generated method stub
		for(int i=0;i<view.getChildCount();i++){
			Button btn = (Button)view.getChildAt(i);
			btn.setBackgroundResource(R.drawable.btn_condition);
		}
		destBtn.setBackgroundResource(R.drawable.filter_selected);
	}
	private void setConditionBtnStyle(Button allType, String string) {
		// TODO Auto-generated method stub
		
	}
	private void updatePage(String currentCategory,String currentType, String currentArea,String currentYear, String currentSort) {
		// TODO Auto-generated method stub
		
	}
	
	protected void setCategoryBtnStyle(Button btn, String name) {
		// TODO Auto-generated method stub
		btn.setText(name);
		btn.setTextSize(20);
		btn.setTextColor(res.getColor(Color.WHITE));
		btn.setBackgroundDrawable(res.getDrawable(R.drawable.btn_level1_selector));
	}
	private void updateChooseBar(View findViewById) {
		// TODO Auto-generated method stub
		
	}
	private void updateHeaderInfo() {
		// TODO Auto-generated method stub
		
	}
	private void initSearchBar() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
