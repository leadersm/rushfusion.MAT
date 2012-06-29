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
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rushfusion.mat.page.FilmClassPage;
import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.HttpUtil;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private static final int FilmClassPage = 1;
	private static final int FilmClassPageSize = 8;
	
	public static final int DIALOG_EXIT = 0;
	public static final int DIALOG_CONNECTEDREFUSED = 1;
	public static final int DIALOG_LOADING = 2;
	public static final int DIALOG_CONDITIONBAR = 3;
	public static final int DIALOG_WIRELESS_SETTING = 4;
	public static final int DIALOG_ORIGIN_MENU = 5;
	
	private ViewGroup parent;
	private View level2;
	private ViewGroup menu;
	private ViewGroup conditionBar;
	private ViewGroup chooseBar;
	
	private String currentOrigin="sina";
	private String currentCategory="首页";
	private String currentType="";
	private String currentYear="";
	private String currentArea="";
	private String currentSort="play";
	
	
	private List<String> categories;
	private HashMap<String,List<String>> conditions = new HashMap<String, List<String>>();
	private List<String> types;
	private List<String> years;
	private List<String> areas;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Resources res;
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
		parent = (ViewGroup) findViewById(R.id.parent);
		conditionBar = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.conditionbar, null);
		sp = getSharedPreferences("MatHistory",Context.MODE_WORLD_READABLE);
		editor = sp.edit();
		res = getResources();
		level2 = findViewById(R.id.level_2);
		level2.setVisibility(View.INVISIBLE);
	}
    
    Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case DIALOG_CONNECTEDREFUSED:
				showDialog(DIALOG_CONNECTEDREFUSED);
				break;

			default:
				break;
			}
    	};
    };
    
	private void initChooseBar() {
		chooseBar = (ViewGroup) findViewById(R.id.level_2);
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
		updateChooseBar(chooseBar.findViewById(R.id.indicator_play));
	}


	private void initCategory(String origin) {
		final ViewGroup level1 = (ViewGroup) findViewById(R.id.level1);
		level1.removeAllViews();

		new AsyncTask<String, Void, List<String>>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showDialog(DIALOG_LOADING);
				if(categories!=null){
					categories.clear();
				}
			}

			@Override
			protected List<String> doInBackground(String... params) {
				categories = DataParser.getInstance(MATActivity.this,params[0]).getCategory();
				return categories;
			}

			@Override
			protected void onPostExecute(List<String> result) {
				super.onPostExecute(result);
				dismissDialog(DIALOG_LOADING);
				level2.setVisibility(View.VISIBLE);
				if(result==null){
					handler.sendEmptyMessage(DIALOG_CONNECTEDREFUSED);
					return;
				}
				
				Button shouye = new Button(MATActivity.this);
				setCategoryBtnStyle(shouye,"首页");
				shouye.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						currentCategory = "首页";
						initConditionBar();
//						while(types==null){
//							try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
						updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
					}
				});
				level1.addView(shouye);
				
				for(int i = 0;i<categories.size();i++){
					Button btn = new Button(MATActivity.this);
					final String name = categories.get(i);
					btn.setText(name);
					setCategoryBtnStyle(btn, name);
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							currentCategory = name;
							initConditionBar();
//							while(types==null||areas==null||years==null){
//								try {
//									Thread.sleep(10);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
							currentType = "";
							currentArea = "";
							currentYear = "";
							currentSort = "play";
							updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
						}
					});
					level1.addView(btn);
				}
		    	String url = "shouye url ???";
		    	initRecommendPage(url);
		    	initChooseBar();
		    	updateHeaderInfo();
				
			}
			
		}.execute(currentOrigin);
	}
	
	
	private void initConditionBar() {
		if(currentCategory.equals("首页")){
			chooseBar.setVisibility(View.GONE);
		}else
			chooseBar.setVisibility(View.VISIBLE);
		final ViewGroup typeView = (ViewGroup) conditionBar.findViewById(R.id.byType);
		final ViewGroup areaView = (ViewGroup) conditionBar.findViewById(R.id.byArea);
		final ViewGroup yearView = (ViewGroup) conditionBar.findViewById(R.id.byYear);
		
		new AsyncTask<String, Void, HashMap<String,List<String>>>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				typeView.removeAllViews();
				yearView.removeAllViews();
				areaView.removeAllViews();
				if(types!=null)
				types.clear();
				if(years!=null)
				years.clear();
				if(areas!=null)
				areas.clear();
			}

			@Override
			protected HashMap<String,List<String>> doInBackground(String... params) {
				
				types = DataParser.getInstance(MATActivity.this,currentOrigin).getTypes(params[0]);
				years = DataParser.getInstance(MATActivity.this,currentOrigin).getYears(params[0]);
				areas = DataParser.getInstance(MATActivity.this,currentOrigin).getAreas(params[0]);
				while(types==null||years==null||areas==null){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				conditions.put("type", types);
				conditions.put("year", years);
				conditions.put("area", areas);
				return conditions;
			}
			
			@Override
			protected void onPostExecute(HashMap<String,List<String>> result) {
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
		menu = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.menu, null);//findViewById(R.id.menu);
		Button leshi = (Button) menu.findViewById(R.id.leshi);
		Button qiyi = (Button) menu.findViewById(R.id.qiyi);
		Button souhu = (Button) menu.findViewById(R.id.souhu);
		Button tudou = (Button) menu.findViewById(R.id.tudou);
		Button sina = (Button) menu.findViewById(R.id.sina);
		leshi.setOnClickListener(this);
		qiyi.setOnClickListener(this);
		souhu.setOnClickListener(this);
		tudou.setOnClickListener(this);
		sina.setOnClickListener(this);
	}

	private void addConditionButtons(final ViewGroup typeView,
			final ViewGroup areaView, final ViewGroup yearView,
			List<String> types, List<String> years, List<String> areas) {
		
		final Button allType = new Button(this);
		final Button allYear = new Button(this);
		final Button allArea = new Button(this);
		
		setConditionBtnStyle(allType, "全部");
		setConditionBtnStyle(allYear, "全部");
		setConditionBtnStyle(allArea, "全部");
		
		allType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(typeView,allType);
				currentType = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allArea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(areaView,allArea);
				currentArea = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allYear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(yearView,allYear);
				currentYear = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		
		typeView.addView(allType);
		areaView.addView(allArea);
		yearView.addView(allYear);
		
		//=====================types====================
		for(int i = 0;i<types.size();i++){
			final Button typeBtn = new Button(MATActivity.this);
			final String type = types.get(i);
			setConditionBtnStyle(typeBtn, type);
			typeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(typeView,typeBtn);
					currentType = type;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			typeView.addView(typeBtn);
		}
		
		//=====================areas====================
		for(int i = 0;i<areas.size();i++){
			final Button areaBtn = new Button(MATActivity.this);
			final String area = areas.get(i);
			setConditionBtnStyle(areaBtn, area);
			areaBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(areaView,areaBtn);
					currentArea = area;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			areaView.addView(areaBtn);
		}
		
		//=====================years====================
		for(int i = 0;i<years.size();i++){
			final Button yearBtn = new Button(MATActivity.this);
			final String year = years.get(i);
			setConditionBtnStyle(yearBtn, year);
			yearBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(yearView,yearBtn);
					currentYear = year;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			yearView.addView(yearBtn);
		}
		typeView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
		areaView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
		yearView.getChildAt(0).setBackgroundResource(R.drawable.filter_selected);
	}
	



	private void updateBackground(ViewGroup cdsView,Button destBtn) {
		for(int i =0;i<cdsView.getChildCount();i++){
			Button v = (Button) cdsView.getChildAt(i);
			v.setBackgroundResource(R.drawable.btn_condition);
		}
		destBtn.setBackgroundResource(R.drawable.filter_selected);
	}

	private void setCategoryBtnStyle(Button btn,String text) {
		btn.setText(text);
		btn.setTextSize(20);
		btn.setTextColor(res.getColor(R.color.white));
		btn.setBackgroundDrawable(res.getDrawable(R.drawable.btn_level1_selector));
	}
	
	private void setConditionBtnStyle(Button btn, String text) {
		btn.setText(text);
		btn.setTextSize(18);
		btn.setTextColor(res.getColor(R.color.white));
		btn.setBackgroundDrawable(res.getDrawable(R.drawable.btn_condition));
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
		case R.id.souhu:
//			updateLastWatchRecord("搜狐");
			changeDataByOriginName("sina");
			dismissDialog(DIALOG_ORIGIN_MENU);
			break;
		case R.id.sina:
//			updateLastWatchRecord("新浪");
			changeDataByOriginName("sina");
			dismissDialog(DIALOG_ORIGIN_MENU);
			break;
		case R.id.qiyi:
//			updateLastWatchRecord("奇艺");
			changeDataByOriginName("sina");
			dismissDialog(DIALOG_ORIGIN_MENU);
			break;
		case R.id.tudou:
//			updateLastWatchRecord("土豆");
			changeDataByOriginName("sina");
			dismissDialog(DIALOG_ORIGIN_MENU);
			break;
		case R.id.leshi:
//			updateLastWatchRecord("乐视");
			changeDataByOriginName("163");
			dismissDialog(DIALOG_ORIGIN_MENU);
			break;
		//==================================
		case R.id.byPlay:
			updateChooseBar(chooseBar.findViewById(R.id.indicator_play));
			currentSort = "play";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byComment:
			updateChooseBar(chooseBar.findViewById(R.id.indicator_comment));
			currentSort = "comment";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);			
			break;
		case R.id.byScore:
			updateChooseBar(chooseBar.findViewById(R.id.indicator_score));
			currentSort = "score";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byRecent:
			updateChooseBar(chooseBar.findViewById(R.id.indicator_recent));
			currentSort = "recent";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byCondition:
			showDialog(DIALOG_CONDITIONBAR);
			break;
		//==================================
			
		default:
			break;
		}
	}
	
	
	private void updateChooseBar(View indicator) {
		chooseBar.findViewById(R.id.indicator_play).setBackgroundResource(R.drawable.red_normal);
		chooseBar.findViewById(R.id.indicator_score).setBackgroundResource(R.drawable.red_normal);
		chooseBar.findViewById(R.id.indicator_comment).setBackgroundResource(R.drawable.red_normal);
		chooseBar.findViewById(R.id.indicator_recent).setBackgroundResource(R.drawable.red_normal);
		indicator.setBackgroundResource(R.drawable.red_active);
	}


	private void changeDataByOriginName(String origin) {
		currentOrigin = origin;
		currentCategory = "";
		currentType = "";
		currentArea = "";
		currentYear = "";
		initCategory(currentOrigin);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			showDialog(DIALOG_ORIGIN_MENU);
			break;
		case KeyEvent.KEYCODE_BACK:
			showDialog(DIALOG_EXIT);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==DIALOG_EXIT){
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
		}else if(id==DIALOG_CONNECTEDREFUSED){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage(currentOrigin+"服务器无响应，请联系客服010-xxxxxxx");
			builder.setNegativeButton("退出程序", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			builder.setPositiveButton("重选视频源", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showDialog(DIALOG_ORIGIN_MENU);
				}
			});
			return builder.create();
		}else if(id==DIALOG_WIRELESS_SETTING){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("网络没有连接，请检查您的网络！");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			return builder.create();
		}else if(id == DIALOG_LOADING){
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("提示:");
			dialog.setMessage("数据正在加载中，请稍后...");
//			dialog.setCancelable(false);
			return dialog;
			
		}else if(id==DIALOG_CONDITIONBAR){
			Dialog dialog = new Dialog(this,R.style.dialog);
			dialog.setContentView(conditionBar);
	        Window dialogWindow = dialog.getWindow();
	        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
//	        lp.x = 100; // 新位置X坐标
	        lp.y = -100; // 新位置Y坐标
	        lp.width = -1;
	        lp.height = 250;
	        lp.alpha = 0.7f; 
	        dialogWindow.setAttributes(lp);
	        return dialog;
		}else if(id==DIALOG_ORIGIN_MENU){
			initMenu();
			Dialog dialog = new Dialog(this,R.style.dialog);
			dialog.setContentView(menu);
			return dialog;
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onStop();
		PageCache.getInstance().release();
	}
	
    private String getLastWatchRecord() {
		return sp.getString("origin", "sina");
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
