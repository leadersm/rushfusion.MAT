package com.rushfusion.mat.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;

public class FilmClassPage extends BasePage {
	private static final int FILM_NUM = 8 ;
	private Context mContext ;
	private ViewGroup mParent ;
	//private View mContentView ;
	protected ViewGroup filmItems[];
	protected int filmItemResIds[] = { R.id.item1, R.id.item2, R.id.item3, R.id.item4,
			R.id.item5, R.id.item6, R.id.item7, R.id.item8 };

	public FilmClassPage(Activity context, ViewGroup parent) {
		super(context, parent);
		mContext = context ;
		mParent = parent ;
		//mContentView = getContentView() ;
	}
	
	/*public void initView() {
		loadPage("http://tvsrv.webhop.net:9061/query?source=sina&category=movie&area=&sort=play&page=1&pagesize=8",R.layout.page_film_class) ;
	}
*/
	@Override
	public void loadPage(String url, int layoutId) {
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.VISIBLE);
				initPage() ;
			}
			
			@Override
			public boolean onExcute(String url) {
				// TODO Auto-generated method stub
				//contentView.findViewById(R.id.image) ;
				new AsyncTask<String, Void, List<Map<String,String>>>(){

					@Override
					protected List<Map<String, String>> doInBackground(
							String... params) {
						String strUrl = params[0] ;
						List<Map<String, String>> nodeList = DataParser.getInstance(context, "").get(strUrl) ;
						return nodeList ;
					}
					
					protected void onPostExecute(List<Map<String,String>> params) {
						fillData(params) ;
					};
					
				}.execute(url) ;
				return false;
			}

			@Override
			public void onFinshed(ProgressBar progress) {
				// TODO Auto-generated method stub
				progress.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	private void initPage(){
		int size = itemSize();
		filmItems = new ViewGroup[size];
		for(int i = 0; i<size; i++){
			filmItems[i] = (ViewGroup)contentView.findViewById(filmItemResIds[i]);
			filmItems[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			
			filmItems[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						updateData() ;
					}
				}
			});
		}
	}
	
	private void updateData() {
		//TextView textView = (TextView)contentView.findViewById(R.id.) ;
	}
	
	private void fillData(List<Map<String,String>> params) {
		int size = itemSize();
		for(int i=0; i<size; i++) {
			Map<String,String> nodeMap = params.get(i) ;
			ImageView itemIcon = (ImageView)filmItems[i].findViewById(R.id.ItemIcon) ;
			ImageLoadTask.imageLoad(itemIcon, nodeMap.get("thumb")) ;
			TextView itemTitle = (TextView)filmItems[i].findViewById(R.id.ItemTitle) ;
			itemTitle.setText(nodeMap.get("name")) ;
		}
	}
	
	protected int itemSize(){
		return FILM_NUM;
	}
	
}
