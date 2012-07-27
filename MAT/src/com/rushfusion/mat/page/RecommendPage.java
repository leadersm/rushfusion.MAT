package com.rushfusion.mat.page;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rushfusion.mat.MATActivity;
import com.rushfusion.mat.R;
import com.rushfusion.mat.utils.Cache;
import com.rushfusion.mat.utils.DataParser;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class RecommendPage extends BasePage {

	Gallery fg;
	TextView desc;
	ViewGroup items;
	private LinearLayout switchLiner;
	private String baseurl = "";
	DataParser parser;
	private Button play, score, comment, recent;

	private RecommendPage(Activity context, ViewGroup parent) {
		super(context, parent);
	}

	static RecommendPage page;

	public static RecommendPage getInstance(Activity context, ViewGroup parent) {
		if (page == null) {
			page = new RecommendPage(context, parent);
		}
		return page;
	}

	public void loadPage(String url, int layoutId) {
		parser = DataParser.getInstance(context, "");
		baseurl = url;
		url = url + "&sort=play";
		loadPage(url, layoutId, new BasePage.onLoadingDataCallBack() {

			@Override
			public void onPrepare() {
				init();
				//initBrief() ;
			}

			@Override
			public List<Movie> onExcute(String url) {
				return parser.getMovies(url);
			}

			@Override
			public void onFinished(List<Movie> result) {
				// TODO Auto-generated method stub
				//fg.setAdapter(new MyAdapter(result));
				//mResult = result; 
				
				//ImageAdapter adapter = new ImageAdapter(mContext, result);
		        //adapter.createReflectedImages();//创建倒影效果
		        /*fg.setFadingEdgeLength(0);
		        fg.setSpacing(-112); //图片之间的间距
		        fg.setAdapter(adapter);*/
		        //fg.setSelection(50);
				
				
				
				images = new ImageView[result.size()] ;
				items.removeAllViews() ;
				for(int i=0;i<result.size();i++){
					items.addView(getView(result.get(i),i));
				}
				// stopTimer() ;
				// startTimer() ;
			}
		});
	}

	ImageView[] images;

	protected void setImgBitmap(final Movie map, ImageView image, int index) {
		String imageUrl = map.getThumb();
		if (Cache.getBitmapFromCache(imageUrl) != null) {
			images[index].setImageBitmap(createMirrorImageWithOrigain(Cache
					.getBitmapFromCache(imageUrl)));
		} else {
			imageLoadTask2.loadImage(images[index], imageUrl, new ImageLoadTask.ImageViewCallback1() {
				
				public void callbak(ImageView view, Bitmap bm) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2 ;
					bm.getWidth() ;
					view.setImageBitmap(createMirrorImageWithOrigain(bm)) ;
				} ;
			}) ;
		}
	}

	private void initBrief() {
		// new AsyncTask<String, Void, List<String>>() {
		//
		// @Override
		// protected List<String> doInBackground(String... params) {
		// List<String> list =
		// (List<String>)XmlHttpClient.getInstance().get(params[0]) ;
		// return list;
		// }
		//
		// protected void onPostExecute(java.util.List<String> result) {
		// briefList = result ;
		// fg.setFadingEdgeLength(0);
		// fg.setSpacing(40); //图片之间的间距
		// //galleryFlow.setAdapter(adapter);
		// fg.setAdapter(new ImageAdapter(mContext, result)) ;
		//
		// switchLiner.removeAllViews() ;
		// for(int i=0; i<result.size(); i++) {
		// childImage = new ImageView(mContext) ;
		// childImage.setImageResource(R.drawable.image_switcher_btn) ;
		// switchLiner.addView(childImage) ;
		// }
		// fg.setSelection(currentPosition);
		// //fg.getResources() ;
		// };
		//
		// }.execute(XmlHttpClient.PATH) ;
	}

	protected View getView(final Movie movie, int index) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.page_recommend_item, null);
		images[index] = (ImageView) v.findViewById(R.id.ItemIcon);
		TextView title = (TextView) v.findViewById(R.id.ItemTitle);
		String imageUrl = movie.getThumb();
		if (Cache.getBitmapFromCache(imageUrl) != null) {
			images[index].setImageBitmap(createMirrorImageWithOrigain(Cache
					.getBitmapFromCache(imageUrl)));
		} else {
			Log.d("imageView", "images[index]:" + images[index]);
			imageLoadTask2.loadImage1(images[index], imageUrl,
					new ImageLoadTask.ImageViewCallback1() {

						public void callbak(ImageView view, Bitmap bm) {
							Log.d("imageView", view + "");
							view.setImageBitmap(createMirrorImageWithOrigain(bm));
						};
					});
		}
		title.setText(movie.getName());
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ItemDetailPage.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("movieInfo", movie);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return v;
	}

	private int currentPosition = 4;
	private int oldPosition = 4;
	// int mIndex = 0;
	ImageLoadTask imageLoadTask2 = new ImageLoadTask();;

	protected void init() {
		play = (Button) contentView.findViewById(R.id.play);
		score = (Button) contentView.findViewById(R.id.score);
		comment = (Button) contentView.findViewById(R.id.comment);
		recent = (Button) contentView.findViewById(R.id.recent);
		final ViewGroup group = (ViewGroup) contentView
				.findViewById(R.id.group_btns);
		updateBackground(group, play, R.drawable.btn_middle_selector,
				R.drawable.middle_menu_selector_highlight);
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBackground(group, play, R.drawable.btn_middle_selector,
						R.drawable.middle_menu_selector_highlight);

				loadData(baseurl + "&sort=play",
						new BasePage.onLoadingDataCallBack() {

							@Override
							public void onPrepare() {
								context.showDialog(MATActivity.DIALOG_LOADING);
							}

							@Override
							public void onFinished(List<Movie> result) {
								context.dismissDialog(MATActivity.DIALOG_LOADING);
								if (result != null) {
									items.removeAllViews();
									for (int i = 0; i < result.size(); i++) {
										items.addView(getView(result.get(i), i));
									}
								}
							}

							@Override
							public List<Movie> onExcute(String url) {
								return parser.getMovies(url);
							}
						});
			}
		});
		score.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBackground(group, score, R.drawable.btn_middle_selector,
						R.drawable.middle_menu_selector_highlight);
				loadData(baseurl + "&sort=score",
						new BasePage.onLoadingDataCallBack() {

							@Override
							public void onPrepare() {
								context.showDialog(MATActivity.DIALOG_LOADING);
							}

							@Override
							public void onFinished(List<Movie> result) {
								context.dismissDialog(MATActivity.DIALOG_LOADING);
								if (result != null) {
									items.removeAllViews();
									for (int i = 0; i < result.size(); i++) {
										items.addView(getView(result.get(i), i));
									}
								}
							}

							@Override
							public List<Movie> onExcute(String url) {
								return parser.getMovies(url);
							}
						});
			}
		});

		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBackground(group, comment,
						R.drawable.btn_middle_selector,
						R.drawable.middle_menu_selector_highlight);
				loadData(baseurl + "&sort=comment",
						new BasePage.onLoadingDataCallBack() {

							@Override
							public void onPrepare() {
								context.showDialog(MATActivity.DIALOG_LOADING);
							}

							@Override
							public void onFinished(List<Movie> result) {
								context.dismissDialog(MATActivity.DIALOG_LOADING);
								if (result != null) {
									items.removeAllViews();
									for (int i = 0; i < result.size(); i++) {
										items.addView(getView(result.get(i), i));
									}
								}
							}

							@Override
							public List<Movie> onExcute(String url) {
								return parser.getMovies(url);
							}
						});
			}
		});
		recent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBackground(group, recent, R.drawable.btn_middle_selector,
						R.drawable.middle_menu_selector_highlight);
				loadData(baseurl + "&sort=recent",
						new BasePage.onLoadingDataCallBack() {

							@Override
							public void onPrepare() {
								context.showDialog(MATActivity.DIALOG_LOADING);
							}

							@Override
							public void onFinished(List<Movie> result) {
								context.dismissDialog(MATActivity.DIALOG_LOADING);
								if (result != null) {
									items.removeAllViews();
									for (int i = 0; i < result.size(); i++) {
										items.addView(getView(result.get(i), i));
									}
								}
							}

							@Override
							public List<Movie> onExcute(String url) {
								return parser.getMovies(url);
							}
						});
			}
		});

		fg = (Gallery) contentView.findViewById(R.id.automoveview);// new
																	// FlingGallery(context);
		items = (ViewGroup) contentView.findViewById(R.id.items);
		items.removeAllViews();
		desc = (TextView) contentView.findViewById(R.id.desc);
		switchLiner = (LinearLayout) contentView.findViewById(R.id.switch_bg);
		switchLiner.removeAllViews();
		fg.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("test", "=============onItemSelected============");
				// Movie movie = (Movie)mResult.get(position%mResult.size()) ;
				// HashMap<String,String> map = (HashMap<String, String>)
				// parent.getItemAtPosition(position);
				/*
				 * if(movie==null) return ;
				 * desc.setText(movie.getDescription());
				 */
				if (briefList == null)
					return;
				Log.d("position", "onItemSelected position:" + position);
				Log.d("position", "mResult.size():" + briefList.size());
				Log.d("position", "position mResult.size()%:" + position
						% briefList.size());
				currentPosition = position;
				Log.d("position", "onItemSelected currentPosition:"
						+ currentPosition);
				Log.d("position", "onItemSelected oldPosition:" + oldPosition);
				Log.d("position", "===========================================");
				if (currentPosition > oldPosition) {
					int num = position % briefList.size();
					if (position % briefList.size() == 0) {
						num = briefList.size() - 1;
					} else {
						num = position % briefList.size() - 1;
					}
					if (currentPosition - oldPosition >= 2) {
						dismissBehindSwitch(num);
					}
					if (oldPosition % briefList.size() == briefList.size() - 1
							&& currentPosition - oldPosition >= 2) {
						dismissSwitch(briefList.size() - 1);
						showSwitch(currentPosition % briefList.size());
					} else {
						dismissSwitch(num);
						showSwitch(position % briefList.size());
					}
					oldPosition = currentPosition;
				} else {
					int num = position % briefList.size();
					if (briefList.size() - (position % briefList.size()) == 1) {
						num = 0;
					} else {
						num = position % briefList.size() + 1;
					}
					if (currentPosition - oldPosition <= -2) {
						dismissHeadSwitch(num);
					}

					if (currentPosition % briefList.size() == briefList.size() - 1
							&& currentPosition - oldPosition <= -2) {
						dismissSwitch(briefList.size() - 1);
						showSwitch(oldPosition % briefList.size());
					} else {
						showSwitch(position % briefList.size());
						dismissSwitch(num);
					}
					oldPosition = currentPosition;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("test", "=============onNothingSelected============");
			}
		});

		// ==================timer===================
		/*
		 * // mHandler = new Handler(){ // @Override // public void
		 * handleMessage(Message msg) { // switch (msg.what) { // case
		 * UPDATE_TEXTVIEW: // //flipper.showNext(); //
		 * fg.setSelection(currentPosition++) ; // break; // case UPDATE_DATA:
		 * // break ; // default: // break; // } // } // };
		 */
		/*
		 * new Thread(){ public void run() { //getCategories() ;
		 * if(mHandler!=null) mHandler.sendEmptyMessage(UPDATE_DATA) ; };
		 * }.start() ;
		 */
	}

	private void showSwitch(int position) {
		ImageView imageView = (ImageView) switchLiner.getChildAt(position);
		if (imageView != null)
			imageView.setImageResource(R.drawable.image_switcher_btn_selected);
	}

	private void dismissBehindSwitch(int num) {
		Log.d("position", "dismissBehindSwitch method ..........");
		for (int i = num; i >= 0; i--) {
			ImageView imageView = (ImageView) switchLiner.getChildAt(i);
			if (imageView != null)
				imageView.setImageResource(R.drawable.image_switcher_btn);
		}
	}

	private void dismissHeadSwitch(int num) {
		Log.d("position", "dismissBehindSwitch method ..........");
		for (int i = num; i < (mResult.size() - 1); i++) {
			ImageView imageView = (ImageView) switchLiner.getChildAt(i);
			if (imageView != null)
				imageView.setImageResource(R.drawable.image_switcher_btn);
		}
	}

	private void dismissSwitch(int position) {
		ImageView imageView = (ImageView) switchLiner.getChildAt(position);
		if (imageView != null)
			imageView.setImageResource(R.drawable.image_switcher_btn);
	}

	int mGalleryItemBackground;
	List<Movie> mResult;
	List<String> briefList;

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

		Shader linearGradient = new LinearGradient(0, 0, w * 7 / 8, 0.01f,
				0x0000000f, 0xffffffff, Shader.TileMode.MIRROR);
		Shader linearGradient2 = new LinearGradient(0, 0, 0.01f, h, 0x0000000f,
				0xffffffff, Shader.TileMode.MIRROR);
		Shader composeShader = new ComposeShader(linearGradient2,
				linearGradient, PorterDuff.Mode.SCREEN);

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

	// ==========================timer==========================//
	// private Timer mTimer = null;
	// private TimerTask mTimerTask = null;
	// private Handler mHandler = null;
	/*
	 * private static int delay = 2000; private static int period = 2000;
	 * private static final int UPDATE_TEXTVIEW = 0; private static final int
	 * UPDATE_DATA = UPDATE_TEXTVIEW + 1;
	 * 
	 * private void startTimer(){ if (mTimer == null) { mTimer = new Timer(); }
	 * if (mTimerTask == null) { mTimerTask = new TimerTask() {
	 * 
	 * @Override public void run() { sendMessage(UPDATE_TEXTVIEW); } }; }
	 * if(mTimer != null && mTimerTask != null ) mTimer.schedule(mTimerTask,
	 * delay, period); }
	 * 
	 * public void stopTimer(){ Log.d("kill", "stopTimer method...........") ;
	 * if (mTimer != null) { mTimer.cancel(); mTimer = null; } if (mTimerTask !=
	 * null) { mTimerTask.cancel(); mTimerTask = null; } }
	 */

	/*
	 * public void sendMessage(int id){ if (mHandler != null) { Log.d("message",
	 * "sendmessage...........") ; Message message = Message.obtain(mHandler,
	 * id); mHandler.sendMessage(message); } }
	 */

	@Override
	public void onKill() {
		Log.d("MAT", "onKill----RecommendPage");
		// stopTimer() ;
	}

	private void updateBackground(ViewGroup cdsView, Button destBtn,
			int selectorId, int selectedId) {
		for (int i = 0; i < cdsView.getChildCount(); i++) {
			Button v = (Button) cdsView.getChildAt(i);
			v.setBackgroundResource(selectorId);
		}
		destBtn.setBackgroundResource(selectedId);
	}

}
