package com.rushfusion.mat.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class RecommendPage extends BasePage{
	
	Gallery fg;
	TextView desc;
	ViewGroup items;
	static RecommendPage page;
	
	public static RecommendPage getInstance(Activity context, ViewGroup parent){
		if(page==null){
			page = new RecommendPage(context, parent);
		}
		return page;
	}
	
	private RecommendPage(Activity context, ViewGroup parent) {
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
				fg.setSelection(2);
				for(int i=0;i<result.size();i++){
					items.addView(getView(result.get(i)));
				}
			}

		});
	}


	protected View getView(final Map<String, String> map) {
		View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
		ImageView image = (ImageView) v.findViewById(R.id.ItemIcon);
		TextView title = (TextView) v.findViewById(R.id.ItemTitle);
		image.setImageBitmap(createMirrorImageWithOrigain(ImageLoadTask.loadBitmap(map.get("thumb"))));
		title.setText(map.get("name"));
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Movie movie = new Movie(Integer.parseInt(map.get("count")),Integer.parseInt(map.get("total")),Integer.parseInt(map.get("score")),
						Integer.parseInt(map.get("comment")),map.get("category"),map.get("name"),map.get("type"),Integer.parseInt(map.get("year")),
						map.get("directors"),map.get("artists"),map.get("area"),map.get("description"),
						map.get("thumb"),map.get("length"),map.get("url"),Integer.parseInt(map.get("play")),map.get("id"),Long.parseLong(map.get("recent"))) ;
				Intent intent = new Intent(context,ItemDetailPage.class) ;
				Bundle bundle = new Bundle() ;
				bundle.putSerializable("movieInfo", movie) ;
				intent.putExtras(bundle) ;
				context.startActivity(intent) ;	
			}
		});
		return v;
	}
	protected void init() {
        fg = (Gallery) contentView.findViewById(R.id.automoveview);//new FlingGallery(context);
        items = (ViewGroup) contentView.findViewById(R.id.items);
        desc = (TextView) contentView.findViewById(R.id.desc);
        fg.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String,String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				desc.setText(map.get("description"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
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
			return result.get(position);
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
			ImageView bigImage = new ImageView(context);
			bigImage.setLayoutParams(new Gallery.LayoutParams(900, 330));
			bigImage.setScaleType(ScaleType.FIT_XY);
			bigImage.setImageBitmap(ImageLoadTask.loadBitmap(map.get("thumb")));
			return bigImage;
		}
		
	}
	
	private Bitmap createMirrorImageWithOrigain(Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w + 10, h + 10,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		Paint paint = new Paint();
		paint.setColor(Color.DKGRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		canvas.drawRect(0, 0, w + 8, h + 8, paint);
		
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(2);
		canvas.drawRect(0, 0, w + 10, h + 10, paint);
		
		Shader linearGradient = new LinearGradient(0, 0, w*7/8, 0.01f,0x0000000f, 0xffffffff,
				Shader.TileMode.MIRROR);
		Shader linearGradient2 = new LinearGradient(0, 0, 0.01f, h,0x0000000f, 0xffffffff,
				Shader.TileMode.MIRROR);
		Shader composeShader = new ComposeShader(linearGradient2,linearGradient,PorterDuff.Mode.SCREEN);

		paint.reset();
		paint.setShader(composeShader);
		paint.setAlpha(0x30);

		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo((w + 10) * 7 / 8, 0);
		path.lineTo((w + 10) / 8, h + 10);
		path.lineTo(0, h + 10);
		path.close();
		canvas.drawPath(path, paint);
		
		
		canvas.drawBitmap(bitmap, 5, 5, null);
		
		paint.reset();
		paint.setColor(Color.WHITE);
		paint.setAlpha(0x20);

		Path path2 = new Path();
		path2.moveTo(0, 0);
		path2.lineTo((w + 10) * 7 / 8, 0);
		path2.lineTo((w + 10) / 8, h + 10);
		path2.lineTo(0, h + 10);
		path2.close();
		canvas.drawPath(path, paint);
		
		return bitmapWithReflection;
	}

	@Override
	public void onKill() {
		// TODO Auto-generated method stub
		Log.w("MAT", "onKill--RecommendPage");
	}
}

