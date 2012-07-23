package com.rushfusion.mat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.rushfusion.mat.page.FilmClassPage;
import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.page.SearchResultPage;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.HttpUtil;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.utils.ImageLoadTask.ImageViewCallback1;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	public static final int FILMCLASSPAGE = 1;
	public static final int FILMCLASSPAGESIZE = 24;
	
	public static final int DIALOG_EXIT = 0;
	public static final int DIALOG_CONNECTEDREFUSED = 1;
	public static final int DIALOG_LOADING = 2;
	public static final int DIALOG_CONDITIONBAR = 3;
	public static final int DIALOG_WIRELESS_SETTING = 4;
	public static final int DIALOG_ORIGIN_MENU = 5;
	public static final int DIALOG_SEARCH = 6;
	public static final int DIALOG_NO_SOURCE = 7;
	
	private ViewGroup parent;
	private ViewGroup menu;
	private ViewGroup conditionBar;
	private ViewGroup level2;
	private ViewGroup searchBar;
	private ImageView header_origin;
	
	
	private String currentOrigin="sina";
	private String currentCategory="";
	private String currentType="";
	private String currentYear="";
	private String currentArea="";
	private String currentSort="play";
	
	
	private List<String> categories;
	private HashMap<String,List<String>> conditions = new HashMap<String, List<String>>();
	private List<String> types;
	private List<String> years;
	private List<String> areas;
	
	private Resources res;
	
	private ImageLoadTask imageTask;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        res = getResources();
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
		level2 = (ViewGroup) findViewById(R.id.level_2);
		header_origin = (ImageView) findViewById(R.id.header_origin);
		header_origin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_ORIGIN_MENU);
			}
		});
		imageTask = new ImageLoadTask();
		initSearchBar();
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
		Button byPlay = (Button) findViewById(R.id.byPlay);
		Button byRecent = (Button) findViewById(R.id.byRecent);
		Button byScore = (Button) findViewById(R.id.byScore);
		Button byComment = (Button) findViewById(R.id.byComment);
		Button bySearch = (Button) findViewById(R.id.bySearch);
		Button byCondition = (Button) findViewById(R.id.byCondition);
		
		byPlay.setOnClickListener(this);
		byRecent.setOnClickListener(this);
		byScore.setOnClickListener(this);
		byComment.setOnClickListener(this);
		byCondition.setOnClickListener(this);
		bySearch.setOnClickListener(this);
		updateChooseBar(level2.findViewById(R.id.indicator_play));
	}


	private void initCategory(String origin) {
		parent.removeAllViews();
		final ViewGroup level1 = (ViewGroup) findViewById(R.id.level1);
		level1.removeAllViews();
		new AsyncTask<String, Void, List<String>>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				level2.setVisibility(View.GONE);
				showDialog(DIALOG_LOADING);
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
				if(result==null){
					handler.sendEmptyMessage(DIALOG_CONNECTEDREFUSED);
					return;
				}
				
				final Button shouye = new Button(MATActivity.this);
				setCategoryBtnStyle(shouye,res.getString(R.string.shouye));
				shouye.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						currentCategory = res.getString(R.string.shouye);
						initConditionBar();
						updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
						updateBackground(level1,shouye,R.drawable.btn_level1_selector,R.drawable.btn_selector_pressed);
					}
				});
				level1.addView(shouye);
				
				for(int i = 0;i<categories.size();i++){
					final Button btn = new Button(MATActivity.this);
					final String name = categories.get(i);
					btn.setText(name);
					setCategoryBtnStyle(btn, name);
					btn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							currentCategory = name;
							initConditionBar();
							currentType = "";
							currentArea = "";
							currentYear = "";
							currentSort = "";
							updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
							updateBackground(level1,btn,R.drawable.btn_level1_selector,R.drawable.btn_selector_pressed);
							updateChooseBar(null);
						}
					});
					level1.addView(btn);
				}
		    	initRecommendPage();
		    	updateBackground(level1,shouye,R.drawable.btn_level1_selector,R.drawable.btn_selector_pressed);
		    	initChooseBar();
				
			}
			
		}.execute(currentOrigin);
	}
	
	
	private void initConditionBar() {
		if(currentCategory.equals(res.getString(R.string.shouye))){
			level2.setVisibility(View.GONE);
			return;
		}else
			level2.setVisibility(View.VISIBLE);
		final ViewGroup typeView = (ViewGroup) conditionBar.findViewById(R.id.byType);
		final ViewGroup areaView = (ViewGroup) conditionBar.findViewById(R.id.byArea);
		final ViewGroup yearView = (ViewGroup) conditionBar.findViewById(R.id.byYear);
		
		new AsyncTask<String, Void, HashMap<String,List<String>>>() {
			
			@Override
			protected void onPreExecute() {
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
		String sourceUrl = "http://tvsrv.webhop.net:9061/source";
		menu = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.menu, null);//findViewById(R.id.menu);
		final ViewGroup sourceGroup = (ViewGroup) menu.findViewById(R.id.source_group);
		sourceGroup.removeAllViews();
		final ProgressBar progress = (ProgressBar) menu.findViewById(R.id.menu_progress);
		new AsyncTask<String, Void, List<Map<String,String>>>(){

			@Override
			protected void onPreExecute() {
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPostExecute(List<Map<String,String>> result) {
				progress.setVisibility(View.INVISIBLE);
				if(result==null||result.size()<=0){
					dismissDialog(DIALOG_ORIGIN_MENU);
					showDialog(DIALOG_NO_SOURCE);
					return;
				}
				for(int i=0;i<result.size();i++){
					ImageButton sourceBtn = new ImageButton(MATActivity.this);
					final String name = result.get(i).get("name");
					final String source = result.get(i).get("source");
					final String logoUrl = result.get(i).get("logo");
					setSourceButtonStyle(sourceBtn, name,logoUrl);
					sourceBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							changeDataByOriginName(source,name);
							dismissDialog(DIALOG_ORIGIN_MENU);
							imageTask.loadImage(header_origin, logoUrl, new ImageViewCallback1() {
								
								@Override
								public void callbak(ImageView view, Bitmap bm) {
									// TODO Auto-generated method stub
									view.setImageBitmap(bm);
								}
							});
						}
					});
					sourceGroup.addView(sourceBtn);
				}
				sourceGroup.getChildAt(0).requestFocus();
				
			}

			@Override
			protected List<Map<String,String>> doInBackground(String... params) {
				// TODO Auto-generated method stub
				return DataParser.getInstance(MATActivity.this,params[0]).getSource();
			}
			
		}.execute(sourceUrl);
		
	}

	private void addConditionButtons(final ViewGroup typeView,
			final ViewGroup areaView, final ViewGroup yearView,
			List<String> types, List<String> years, List<String> areas) {
		
		typeView.removeAllViews();
		areaView.removeAllViews();
		yearView.removeAllViews();
		
		final Button allType = new Button(this);
		final Button allYear = new Button(this);
		final Button allArea = new Button(this);
		
		setConditionBtnStyle(allType, "全部");
		setConditionBtnStyle(allYear, "全部");
		setConditionBtnStyle(allArea, "全部");
		
		allType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(typeView,allType,R.drawable.btn_condition,R.drawable.filter_selected);
				currentType = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allArea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(areaView,allArea,R.drawable.btn_condition,R.drawable.filter_selected);
				currentArea = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		allYear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateBackground(yearView,allYear,R.drawable.btn_condition,R.drawable.filter_selected);
				currentYear = "";
				updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			}
		});
		
		typeView.addView(allType);
		areaView.addView(allArea);
		yearView.addView(allYear);
		
		//=====================types====================
		if(types!=null)
		for(int i = 0;i<types.size();i++){
			final Button typeBtn = new Button(MATActivity.this);
			final String type = types.get(i);
			setConditionBtnStyle(typeBtn, type);
			typeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(typeView,typeBtn,R.drawable.btn_condition,R.drawable.filter_selected);
					currentType = type;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			typeView.addView(typeBtn);
		}
		
		//=====================areas====================
		if(areas!=null)
		for(int i = 0;i<areas.size();i++){
			final Button areaBtn = new Button(MATActivity.this);
			final String area = areas.get(i);
			setConditionBtnStyle(areaBtn, area);
			areaBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(areaView,areaBtn,R.drawable.btn_condition,R.drawable.filter_selected);
					currentArea = area;
					updatePage(currentCategory,currentType, currentArea, currentYear, currentSort);
				}
			});
			areaView.addView(areaBtn);
		}
		
		//=====================years====================
		if(years!=null)
		for(int i = 0;i<years.size();i++){
			final Button yearBtn = new Button(MATActivity.this);
			final String year = years.get(i);
			setConditionBtnStyle(yearBtn, year);
			yearBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateBackground(yearView,yearBtn,R.drawable.btn_condition,R.drawable.filter_selected);
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
	

	private void updateBackground(ViewGroup cdsView,Button destBtn,int selectorId,int selectedId) {
		for(int i =0;i<cdsView.getChildCount();i++){
			Button v = (Button) cdsView.getChildAt(i);
			v.setBackgroundResource(selectorId);
		}
		destBtn.setBackgroundResource(selectedId);
		if(destBtn.getText().equals(this.getResources().getString(R.string.shouye))) {
			level1.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.level_title_bg)) ;
		} else {
			level1.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.middle_menu_bg)) ;
		}
	}

	protected void setCategoryBtnStyle(Button btn, String name) {
		// TODO Auto-generated method stub
		btn.setText(name);
		btn.setTextSize(20);
		btn.setTextColor(res.getColor(R.color.white));
		btn.setBackgroundDrawable(res.getDrawable(R.drawable.btn_level1_selector));
	}
	
	private void setSourceButtonStyle(ImageButton btn,String name,String logourl) {
		btn.setLayoutParams(new LayoutParams(200,80));
		Bitmap bm = ImageLoadTask.loadBitmap(logourl) ;
		btn.setImageBitmap(zoomBitmap(bm, 180, 60));
	}
	
	private void setConditionBtnStyle(Button btn, String text) {
		btn.setText(text);
		btn.setTextSize(18);
		btn.setTextColor(res.getColor(R.color.white));
		btn.setBackgroundDrawable(res.getDrawable(R.drawable.btn_condition));
	}
	
	public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){    
        int width = bitmap.getWidth();    
        int height = bitmap.getHeight();    
        Matrix matrix = new Matrix();    
        float scaleWidht = ((float)w / width);    
        float scaleHeight = ((float)h / height);    
        matrix.postScale(scaleWidht, scaleHeight);    
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);    
        return newbmp;    
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
		System.gc();
		StringBuffer baseUrl = new StringBuffer("http://tvsrv.webhop.net:9061/query?source="+currentOrigin);
		if(!category.equals(""))baseUrl.append("&category="+category);
		if(!type.equals(""))baseUrl.append("&type="+type);
		if(!area.equals(""))baseUrl.append("&area="+area);
		if(!year.equals(""))baseUrl.append("&year="+year);
		if(!sort.equals(""))baseUrl.append("&sort="+sort);
		baseUrl.append("&page="+FILMCLASSPAGE+"&pagesize="+FILMCLASSPAGESIZE);
		final String url = baseUrl.toString();
		Log.d("MAT","LoadPage url==>"+url);
		if(currentCategory.equals(res.getString(R.string.shouye))){
			initRecommendPage();
		}else{
			//to other page...
			initFilmClassPage(url);
		}
		
	}
	
	private void initRecommendPage() {
		String recommendUrl = "http://tvsrv.webhop.net:9061/query?source="
				+currentOrigin+"&sort=play&page=1&pagesize=10";
		RecommendPage  recommendPage = RecommendPage.getInstance(this,parent);
		recommendPage.loadPage(recommendUrl,R.layout.page_recommend);
		PageCache.getInstance().set(R.layout.page_recommend, recommendPage);
	}

	private void initFilmClassPage(String url) {
		FilmClassPage page = FilmClassPage.getInstance(this,parent);
		page.loadPage(url, R.layout.page_film_class);
		PageCache.getInstance().set(R.layout.page_film_class, page);
	}	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.byPlay:
			updateChooseBar(level2.findViewById(R.id.indicator_play));
			currentSort = "play";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byComment:
			updateChooseBar(level2.findViewById(R.id.indicator_comment));
			currentSort = "comment";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);			
			break;
		case R.id.byScore:
			updateChooseBar(level2.findViewById(R.id.indicator_score));
			currentSort = "score";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byRecent:
			updateChooseBar(level2.findViewById(R.id.indicator_recent));
			currentSort = "recent";
			updatePage(currentCategory,currentType,currentArea,currentYear,currentSort);
			break;
		case R.id.byCondition:
			showDialog(DIALOG_CONDITIONBAR);
			break;
		case R.id.bySearch:
			showDialog(DIALOG_SEARCH);
			break;
			
		default:
			break;
		}
	}
	
	
	private void updateChooseBar(View indicator) {
		level2.findViewById(R.id.indicator_play).setBackgroundResource(R.drawable.red_normal);
		level2.findViewById(R.id.indicator_score).setBackgroundResource(R.drawable.red_normal);
		level2.findViewById(R.id.indicator_comment).setBackgroundResource(R.drawable.red_normal);
		level2.findViewById(R.id.indicator_recent).setBackgroundResource(R.drawable.red_normal);
		if(indicator!=null){
			indicator.setBackgroundResource(R.drawable.red_active);
		}
			
	}


	private void changeDataByOriginName(String origin,String originName) {
		currentOrigin = origin;
		initCategory(currentOrigin);
		PageCache.getInstance().release();
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
			builder.setTitle(res.getString(R.string.tip));
			builder.setMessage(res.getString(R.string.areyousure));
			builder.setPositiveButton(res.getString(R.string.sure), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(-1) ;
					//finish();
				}
			});
			builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(0);
				}
			});
			return builder.create();
		}else if(id==DIALOG_CONNECTEDREFUSED){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(res.getString(R.string.tip));
			builder.setIcon(R.drawable.nowifi_icon);
			builder.setMessage(currentOrigin+res.getString(R.string.noresponse));
			builder.setNegativeButton(res.getString(R.string.exit), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.setPositiveButton(res.getString(R.string.rechooseorigin), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showDialog(DIALOG_ORIGIN_MENU);
				}
			});
			return builder.create();
		}else if(id==DIALOG_WIRELESS_SETTING){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(res.getString(R.string.tip));
			builder.setIcon(R.drawable.nowifi_icon);
			builder.setMessage(res.getString(R.string.nonetwork));
			builder.setPositiveButton(res.getString(R.string.sure), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			return builder.create();
		}else if(id == DIALOG_LOADING){
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(res.getString(R.string.pleasewait));
			dialog.setCancelable(false);
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
		}else if(id==DIALOG_SEARCH){
			Dialog dialog = new Dialog(this,R.style.dialog);
			dialog.setContentView(searchBar);
			Window dialogWindow = dialog.getWindow();
	        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
	        lp.y = -100;
	        lp.alpha = 1.0f; 
	        dialogWindow.setAttributes(lp);
			return dialog;
		}else if(id == DIALOG_NO_SOURCE){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(res.getString(R.string.tip));
			builder.setIcon(R.drawable.nowifi_icon);
			builder.setMessage(res.getString(R.string.loadfailed));
			builder.setPositiveButton(res.getString(R.string.sure), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			return builder.create();
		}
		return null;
	}

	private String bywhat = "name";
	
	private void initSearchBar() {
		searchBar = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.page_search, null);
		final EditText keyEt = (EditText) searchBar.findViewById(R.id.mat_search_key);
		RadioGroup group = (RadioGroup) searchBar.findViewById(R.id.radioGroup1);
		Button search = (Button) searchBar.findViewById(R.id.mat_search_search);
		Button clean = (Button) searchBar.findViewById(R.id.mat_search_clean);
		Button back = (Button) searchBar.findViewById(R.id.mat_search_back);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String keywords =  keyEt.getText().toString(); 
				if(!TextUtils.isEmpty(keywords)){
					toSearchResultPage(getSearchUrl(bywhat,keywords),keywords);
					keyEt.getText().clear();
					dismissDialog(DIALOG_SEARCH);
				}else{
					Toast.makeText(MATActivity.this, MATActivity.this.getString(R.string.qingshuru), 1).show();
				}
			}
		});
		clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int length = keyEt.getText().toString().length();
				System.out.println("length::==>?"+ length);
				if(length-1>=0){
					keyEt.getText().delete(length-1, length);  
				}else{
					Toast.makeText(MATActivity.this, MATActivity.this.getString(R.string.meiyoushuru), 1).show();
				}
			}
		});
		clean.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				keyEt.setText("");
				return false;
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(DIALOG_SEARCH);
			}
		});
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.search_byName:
					bywhat = "name";
					break;
				case R.id.search_byDirector:
					bywhat = "directors";
					break;
				case R.id.search_byActors:
					bywhat = "artists";
					break;
				case R.id.search_byType:
					bywhat = "type";
					break;
				case R.id.search_byArea:
					bywhat = "area";
					break;
				case R.id.search_byYear:
					bywhat = "year";
					break;

				default:
					break;
				}
				
			}
		});
	}


	protected void toSearchResultPage(String searchUrl,String searchKey) {
		if(URLUtil.isValidUrl(searchUrl)){
			SearchResultPage page = new SearchResultPage(this, currentOrigin ,currentCategory,parent);
			page.setKey(searchKey);
			page.loadPage(searchUrl, R.layout.page_search_result);
		}else
			Toast.makeText(this, res.getString(R.string.invalidkey), 1).show();
	}


	protected String getSearchUrl(String bywhat, String keywords) {
		// TODO Auto-generated method stub
		keywords = keywords.trim().replace(" ", "");
		Pattern p = Pattern.compile("\\t|\r|\n");
		Matcher m = p.matcher(keywords);
		keywords = m.replaceAll("");
		String url = "http://tvsrv.webhop.net:9061/query?"
				+"source="+currentOrigin
				+"&page=1&pagesize="+FILMCLASSPAGESIZE
				+"&"+bywhat+"="+keywords;
		
		Log.d("MAT", "search url==>"+url);
		return url;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		PageCache.getInstance().release();
		super.onStop();
	}
	
}
