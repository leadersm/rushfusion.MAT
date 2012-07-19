package com.rushfusion.mat.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.Cache;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class RecommendPage extends BasePage{
	
	Gallery fg;
	TextView desc;
	ViewGroup items;
	private LinearLayout switchLiner ;
	private Context mContext ;
	private ImageView childImage ;
	private ViewGroup mParent ;
	private boolean isLoading = false ;
	
	
	private RecommendPage(Activity context, ViewGroup parent) {
		super(context, parent);
		mContext = context ;
		mParent = parent ;
	}
	
	static RecommendPage page;
	public static RecommendPage getInstance(Activity context, ViewGroup parent){
		if(page==null){
			page = new RecommendPage(context, parent);
		}
		return page;
	}

	
	

	public void loadPage(String url,int layoutId) {
		// TODO Auto-generated method stub
		loadPage(url, layoutId,new BasePage.onLoadingDataCallBack(){

			@Override
			public void onPrepare() {
				// TODO Auto-generated method stub
				isLoading = true ;
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
				//fg.setAdapter(new MyAdapter(result));
				mResult = result; 
				
				ImageAdapter adapter = new ImageAdapter(mContext, result);
		        //adapter.createReflectedImages();//创建倒影效果
		        fg.setFadingEdgeLength(0);
		        fg.setSpacing(-112); //图片之间的间距
		        fg.setAdapter(adapter);
		        //fg.setSelection(50);
				
				
				
				images = new ImageView[result.size()] ;
				items.removeAllViews() ;
				for(int i=0;i<result.size();i++){
					items.addView(getView(result.get(i),i));
				}
				switchLiner.removeAllViews() ;
				for(int i=0; i<result.size(); i++) {
					childImage = new ImageView(mContext) ;
					childImage.setImageResource(R.drawable.image_switcher_btn) ;
					switchLiner.addView(childImage) ;
				}
				//stopTimer() ;
				//startTimer() ;
				isLoading = false ;
			}

		});
	}
	
	ImageView[] images ;
	
	protected void setImgBitmap(final Map<String, String> map, ImageView image, int index) {
		String imageUrl = map.get("thumb") ;
		if(Cache.getBitmapFromCache(imageUrl)!=null) {
			images[index].setImageBitmap(createMirrorImageWithOrigain(Cache.getBitmapFromCache(imageUrl))) ;
		} else {
			imageLoadTask2.loadImage(images[index], imageUrl, new ImageLoadTask.ImageViewCallback1() {
				
				public void callbak(ImageView view, Bitmap bm) {
					view.setImageBitmap(createMirrorImageWithOrigain(bm)) ;
				} ;
			}) ;
		}
	}
	
	protected View getView(final Map<String, String> map, int index) {
		View v = LayoutInflater.from(context).inflate(R.layout.page_recommend_item, null);
		images[index] = (ImageView) v.findViewById(R.id.ItemIcon);
		TextView title = (TextView) v.findViewById(R.id.ItemTitle);
		String imageUrl = map.get("thumb") ;
		if(Cache.getBitmapFromCache(imageUrl)!=null) {
			images[index].setImageBitmap(createMirrorImageWithOrigain(Cache.getBitmapFromCache(imageUrl))) ;
		} else {
			Log.d("imageView", "images[index]:"+images[index]) ;
			imageLoadTask2.loadImage1(images[index], imageUrl, new ImageLoadTask.ImageViewCallback1() {
				
				public void callbak(ImageView view, Bitmap bm) {
					Log.d("imageView", view+"") ;
					view.setImageBitmap(createMirrorImageWithOrigain(bm)) ;
				} ;
			}) ;
		}
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

	private int currentPosition = 0 ;
	private int oldPosition = 0;
	//int mIndex = 0;
	ImageLoadTask imageLoadTask2 = new ImageLoadTask() ; ;
	protected void init() {
        fg = (Gallery) contentView.findViewById(R.id.automoveview);//new FlingGallery(context);
        items = (ViewGroup) contentView.findViewById(R.id.items);
        //items.removeAllViews() ;
        desc = (TextView) contentView.findViewById(R.id.desc);
        switchLiner = (LinearLayout)contentView.findViewById(R.id.switch_bg) ;
        //switchLiner.removeAllViews() ;
        fg.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("test", "=============onItemSelected============") ;
				// TODO Auto-generated method stub
				HashMap<String,String> map = (HashMap<String, String>)mResult.get(position%mResult.size()) ;
				//HashMap<String,String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				if(map==null) return ;
				desc.setText(map.get("description"));
				if(mResult==null) return ;
				Log.d("position", "onItemSelected position:"+position) ;
				Log.d("position", "mResult.size():"+mResult.size()) ;
				Log.d("position", "position mResult.size()%:"+position%mResult.size()) ;
				currentPosition = position ;
				Log.d("position", "onItemSelected currentPosition:"+currentPosition) ;
				Log.d("position", "onItemSelected oldPosition:"+oldPosition) ;
				Log.d("position", "===========================================") ;
				if(currentPosition>oldPosition) {
					int num = position%mResult.size() ;
					if(position%mResult.size()==0) {
						num = mResult.size() - 1 ;
					} else {
						num = position%mResult.size() - 1 ;
					}
					if(currentPosition-oldPosition>=2) {
						dismissBehindSwitch(num) ;
					}
					if(oldPosition%mResult.size()==mResult.size()-1 && currentPosition-oldPosition>=2) {
						dismissSwitch(mResult.size() - 1) ;
						showSwitch(currentPosition%mResult.size()) ;
					} else {
						dismissSwitch(num) ;
						showSwitch(position%mResult.size()) ;
					}
					oldPosition = currentPosition ;
				} else {
					int num = position%mResult.size() ;
					if(mResult.size()-(position%mResult.size())==1) {
						num = 0 ;
					} else {
						num = position%mResult.size()+1 ;
					}
					if(currentPosition-oldPosition<=-2) {
						dismissHeadSwitch(num) ;
					}
					
					if(currentPosition%mResult.size()==mResult.size()-1 && currentPosition-oldPosition<=-2) {
						dismissSwitch(mResult.size() - 1) ;
						showSwitch(oldPosition%mResult.size()) ;
					} else {
						showSwitch(position%mResult.size()) ;
						dismissSwitch(num) ;
					}
					oldPosition = currentPosition ;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Log.d("test", "=============onNothingSelected============") ;
			}
		});
        
        
        //==================timer===================
/*//        mHandler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				switch (msg.what) {
//				case UPDATE_TEXTVIEW:
//					//flipper.showNext();
//					fg.setSelection(currentPosition++) ;
//					break;
//				case UPDATE_DATA:
//					break ;
//				default:
//					break;
//				}
//			}
//		};
*/		
		/*new Thread(){
			public void run() {
				//getCategories() ;
				if(mHandler!=null) 
					mHandler.sendEmptyMessage(UPDATE_DATA) ;
			};
		}.start() ;*/
	}
	
	private void showSwitch(int position) {
		ImageView imageView = (ImageView)switchLiner.getChildAt(position) ;
		if(imageView!=null)
			imageView.setImageResource(R.drawable.image_switcher_btn_selected) ;
	}
	
	private void dismissBehindSwitch(int num) {
		Log.d("position", "dismissBehindSwitch method ..........") ;
		for(int i=num; i>=0; i--) {
			ImageView imageView = (ImageView)switchLiner.getChildAt(i) ;
			if(imageView!=null)
				imageView.setImageResource(R.drawable.image_switcher_btn) ;
		}
	}
	
	private void dismissHeadSwitch(int num) {
		Log.d("position", "dismissBehindSwitch method ..........") ;
		for(int i=num; i<(mResult.size()-1); i++) {
			ImageView imageView = (ImageView)switchLiner.getChildAt(i) ;
			if(imageView!=null)
				imageView.setImageResource(R.drawable.image_switcher_btn) ;
		}
	}
	
	private void dismissSwitch(int position) {
		ImageView imageView = (ImageView)switchLiner.getChildAt(position) ;
		if(imageView!=null)
			imageView.setImageResource(R.drawable.image_switcher_btn) ;
	}

	int mGalleryItemBackground;
	List<Map<String, String>> mResult;
	private class MyAdapter extends BaseAdapter{

		public MyAdapter(List<Map<String, String>> result) {
			// TODO Auto-generated constructor stub
			mResult = result ;
			TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mResult.get(position%mResult.size());
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			HashMap<String, String> map = (HashMap<String, String>) mResult.get(position%mResult.size());
			ImageView bigImage = new ImageView(context);
			bigImage.setLayoutParams(new Gallery.LayoutParams(900, 330));
			bigImage.setScaleType(ScaleType.FIT_XY);
			String imageUrl = map.get("thumb") ;
			//bigImage.setImageBitmap(ImageLoadTask.loadBitmap(map.get("thumb")));
			ImageLoadTask imageLoadTask = new ImageLoadTask() ;
			if(Cache.getBitmapFromCache(imageUrl)!=null) {
				bigImage.setImageBitmap(Cache.getBitmapFromCache(imageUrl)) ;
			} else {
				imageLoadTask.loadImage(bigImage, map.get("thumb"), new ImageLoadTask.ImageViewCallback1() {
					
					public void callbak(ImageView view, Bitmap bm) {
						view.setImageBitmap(bm) ;
					} ;
				}) ;
			}
			bigImage.setBackgroundResource(mGalleryItemBackground);
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
	
	
	//==========================timer==========================//
	//private Timer mTimer = null;
	//private TimerTask mTimerTask = null;
	//private Handler mHandler = null;
	/*private static int delay = 2000;  
	private static int period = 2000;  
	private static final int UPDATE_TEXTVIEW = 0;
	private static final int UPDATE_DATA = UPDATE_TEXTVIEW + 1;
	
	private void startTimer(){
		if (mTimer == null) {
			mTimer = new Timer();
		}
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					sendMessage(UPDATE_TEXTVIEW);
				}
			};
		}
		if(mTimer != null && mTimerTask != null )
			mTimer.schedule(mTimerTask, delay, period);
	}

	public void stopTimer(){
		Log.d("kill", "stopTimer method...........") ;
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}	
	}*/
	
	
	/*public void sendMessage(int id){
		if (mHandler != null) {
			Log.d("message", "sendmessage...........") ;
			Message message = Message.obtain(mHandler, id);   
			mHandler.sendMessage(message); 
		}
	}*/

	@Override
	public void onKill() {
		// TODO Auto-generated method stub
		Log.d("kill", "onKill method...........") ;
		//stopTimer() ;
	}
}

