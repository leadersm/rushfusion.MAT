package com.rushfusion.mat.page;

import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.jar.Attributes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.rushfusion.mat.R;

public class MediaPlayerShow extends Activity implements OnBufferingUpdateListener,OnVideoSizeChangedListener,OnCompletionListener,OnErrorListener,
														OnInfoListener,OnPreparedListener,OnSeekCompleteListener,Callback,MediaPlayerControl{
	String filePath;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	MediaPlayer mediaPlayer;
	MediaController controller;
	Display currentDisplay;
	int videoWidth = 0 ;
	int videoHeight = 0 ;
	int contiuePosition = 0;
	String movieId;
	boolean isContinue = false ;
	ProgressDialog pDialog ;
	int saveTime=0;
	String stateOf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playershow);
		surfaceView = (SurfaceView) findViewById(R.id.page_playershow_surfaceview);

		Intent it = getIntent();
		Bundle bd = it.getExtras();

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnVideoSizeChangedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		SharedPreferences prefs = getSharedPreferences("myDataStorage", MODE_PRIVATE);
		if(bd==null){
			Toast.makeText(this, "连接错误", 1000).show();
		}else{
			filePath = bd.getString("url") ;
			movieId = bd.getString("id");
			int testPosition = prefs.getInt(movieId, -1);
			if(testPosition != -1){
				System.out.println("进入为已播放的判断");
				isContinue = true;
				contiuePosition = testPosition;
			}
		}

		try {
			mediaPlayer.setDataSource(filePath);
		} catch (IllegalArgumentException e) {
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		} catch (IllegalStateException e) {
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		} catch (IOException e) {
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		}
		currentDisplay = getWindowManager().getDefaultDisplay(); 
		controller = new MediaController(this , false);
		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(controller.isShowing()){
					controller.hide();
				}else{
					controller.show();
				}
			}
		}) ;
	}


	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}
 

	@Override
	public int getCurrentPosition() {
		System.out.println(stateOf+"=========================");
		if(mediaPlayer!=null){
			if(mediaPlayer.isPlaying()){
				saveTime = mediaPlayer.getCurrentPosition();
				return saveTime;
			}
		}
		return 0;
	}

	@Override
	public int getDuration() {
		System.out.println(stateOf+"--------------------------");
		if(mediaPlayer!=null){
			if(mediaPlayer.isPlaying()){
				return mediaPlayer.getDuration();
			}
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
	}

	@Override
	public void seekTo(int pos) {
		
//		mediaPlayer.pause();
		pDialog.show();
		mediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		mediaPlayer.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		stateOf = "into surfacechanged";
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		stateOf = "into surfacecreated";
		mediaPlayer.setDisplay(holder);
		try {
			mediaPlayer.prepareAsync();
		} catch (IllegalStateException e) {
			System.out.println("surface准备中出错 ，错误信息 ："+e.toString()); 
		} 
		pDialog = new ProgressDialog(this);
		pDialog.setTitle("视频加载中，请稍后");
		pDialog.setCancelable(false);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stateOf = "into surfacedestroyed";
		if(mediaPlayer!=null){
//			saveTime = mediaPlayer.getCurrentPosition();
			mediaPlayer.release();
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
		stateOf = "into onvideosizechanged";
		videoWidth = mp.getVideoWidth();
		videoHeight = mp.getVideoHeight();
		if(videoWidth > currentDisplay.getWidth() || videoHeight > currentDisplay.getHeight()){
			float heightRatio = (float) videoHeight / (float) currentDisplay.getHeight();
			float widthRatio = (float) videoWidth / (float) currentDisplay.getWidth();
			if(heightRatio > 1 || widthRatio > 1){
				if(heightRatio > widthRatio){
					videoHeight = (int) Math.ceil((float)videoHeight / (float)heightRatio);
					videoWidth = (int) Math.ceil((float)videoWidth / (float)heightRatio);
				}else{
					videoHeight = (int) Math.ceil((float)videoHeight / (float)widthRatio);
					videoWidth = (int) Math.ceil((float)videoWidth / (float)widthRatio);
				}
			} 
			surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
		}else{
			surfaceHolder.setFixedSize(videoWidth, videoHeight);
		}
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		stateOf = "into seekcomplete";
		pDialog.cancel();
		mediaPlayer.start();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		stateOf = "into prepared";
		videoWidth = mp.getVideoWidth();
		videoHeight = mp.getVideoHeight();
		if(videoWidth > currentDisplay.getWidth() || videoHeight > currentDisplay.getHeight()){
			float heightRatio = (float) videoHeight / (float) currentDisplay.getHeight();
			float widthRatio = (float) videoWidth / (float) currentDisplay.getWidth();
			if(heightRatio > 1 || widthRatio > 1){
				if(heightRatio > widthRatio){
					videoHeight = (int) Math.ceil((float)videoHeight / (float)heightRatio);
					videoWidth = (int) Math.ceil((float)videoWidth / (float)heightRatio);
				}else{
					videoHeight = (int) Math.ceil((float)videoHeight / (float)widthRatio);
					videoWidth = (int) Math.ceil((float)videoWidth / (float)widthRatio);
				}
			}
			surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
		}else{
			surfaceHolder.setFixedSize(videoWidth, videoHeight); 
		}
		pDialog.cancel();
		controller.setMediaPlayer(this);
		controller.setAnchorView(this.findViewById(R.id.page_playershow_mainview));
		controller.setEnabled(true);
		controller.show();
		if(isContinue){
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage("是否从上次中断处继续播放？");
			dialog.setButton(Dialog.BUTTON_POSITIVE, "继续上次播放", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mediaPlayer.seekTo(contiuePosition);
//					Toast.makeText(MediaPlayerShow.this, "开始继续播放",500).show();
					System.out.println("开始继续播放");
				}
			});
			dialog.setButton(Dialog.BUTTON_NEGATIVE, "从头播放", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(MediaPlayerShow.this, "从头开始播放",500).show();
					System.out.println("从头开始播放");
				}
			});
			dialog.show();
		}else{
//			Toast.makeText(this, "从头开始播放",500).show();
			System.out.println("从头开始播放");
		}
		mp.start();
	}


	@Override
	protected void onPause() {
		stateOf = "into activity onpause";
		SharedPreferences prefs = getSharedPreferences("myDataStorage", MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(movieId,saveTime);
		editor.commit();
		System.out.println("已保存数据： "+movieId +":"+saveTime);
		super.onPause();
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		
//		Toast.makeText(this, "信息读取状态代码："+what, 500).show();
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
//		Toast.makeText(this, "错误代码："+what , 500).show();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		stateOf = "into completion";
		
		Toast.makeText(this, "播放完毕", 500).show();
//		mediaPlayer.release();
		finish();
	}


	@Override
	public int getBufferPercentage() {
		return 0;
	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_UP:
			if(!controller.isShowing()){
				controller.show();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(!controller.isShowing()){
				controller.show();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(!controller.isShowing()){
				controller.show();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(!controller.isShowing()){
				controller.show();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(!controller.isShowing()){
				controller.show();
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
