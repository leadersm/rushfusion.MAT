package com.rushfusion.mat.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.FlingGallery;
import com.rushfusion.mat.utils.ImageLoadTask;

public class RecommendPage extends BasePage implements OnTouchListener{
	
	FlingGallery fg;
	ViewGroup items;
	
	public RecommendPage(Activity context, ViewGroup parent) {
		super(context, parent);
	}

	public void loadPage(String url,int layoutId) {
		// TODO Auto-generated method stub
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
				init();
			}

			@Override
			public List<Map<String, String>> onExcute(String url) {
				// TODO Auto-generated method stub
				DataParser parser = DataParser.getInstance(context, "") ;
				return parser.get(url);
			}

			@Override
			public void onFinished(List<Map<String, String>> result) {
				// TODO Auto-generated method stub
				fg.setAdapter(new MyAdapter(result));
				Timer timer = new Timer();
				timer.schedule(task, 8000, 8000);
				for(int i=0;i<result.size();i++){
					items.addView(getView(result.get(i)));
				}
			}

		});
	}


	protected View getView(Map<String, String> map) {
		View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item2, null);
		ImageView image = (ImageView) v.findViewById(R.id.ItemIcon);
		TextView title = (TextView) v.findViewById(R.id.ItemTitle);
		image.setImageBitmap(ImageLoadTask.loadBitmap(map.get("thumb")));
		title.setText(map.get("name"));
		return v;
	}


	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			fg.moveNext();
			//gallery.movetonext?
		};
	};
	
	TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			h.sendEmptyMessage(1);
		}
	};
	
	protected void init() {
		// TODO Auto-generated method stub
        ViewGroup parent = (ViewGroup) contentView.findViewById(R.id.automoveview);
        fg = new FlingGallery(context);
        items = (ViewGroup) contentView.findViewById(R.id.items);
        parent.addView(fg);
        fg.setPaddingWidth(50);
		fg.setAnimationDuration(1500);
		fg.setIsGalleryCircular(true);
		fg.setOnTouchListener(this);
	}

	private class MyAdapter extends BaseAdapter{

		List<Map<String, String>> result;
		
		public MyAdapter(List<Map<String, String>> result) {
			// TODO Auto-generated constructor stub
			this.result = result;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			HashMap<String, String> map = (HashMap<String, String>) result.get(position);
			
			View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
			ImageView bigImage = (ImageView) v.findViewById(R.id.bigImage);
			TextView title = (TextView) v.findViewById(R.id.title);
			TextView director = (TextView) v.findViewById(R.id.director);
			TextView actors = (TextView) v.findViewById(R.id.actors);
			TextView timeLong = (TextView) v.findViewById(R.id.timelong);
			TextView info = (TextView) v.findViewById(R.id.info);
			
			return v;
		}
		
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return fg.onGalleryTouchEvent(event);
	}
}

