package com.rushfusion.mat.page;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.rushfusion.mat.R;

public class MediaPlayerShow extends Activity implements OnBufferingUpdateListener,OnVideoSizeChangedListener,OnCompletionListener,OnErrorListener,OnInfoListener,OnPreparedListener,OnSeekCompleteListener,Callback,MediaPlayerControl{
	String filePath;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	MediaPlayer mediaPlayer;
	MediaController controller;
	Display currentDisplay;
	int videoWidth = 0 ;
	int videoHeight = 0 ;
	int contiuePosition = 0;
	boolean isContinue = false ;
	
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
			filePath = "http://v.iask.com/v_play_ipad.php?vid=33184708";
		}else{
			filePath = bd.getString("url") ;
			if(filePath.equals(prefs.getString("url", " "))){
				isContinue = true;
				contiuePosition = prefs.getInt("position", 0);
			}
		}
		
		try {
			mediaPlayer.setDataSource(filePath);
		} catch (IllegalArgumentException e) {
			Toast.makeText(this, "mediaplayer 设置数据中出错，错误信息："+e.toString(), 1000).show();
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		} catch (IllegalStateException e) {
			Toast.makeText(this, "mediaplayer 设置数据中出错，错误信息："+e.toString(), 1000).show();
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		} catch (IOException e) {
			Toast.makeText(this, "mediaplayer 设置数据中出错，错误信息："+e.toString(), 1000).show();
			System.out.println("mediaplayer 设置数据中出错，错误信息："+e.toString());
		}
		currentDisplay = getWindowManager().getDefaultDisplay(); 
		controller = new MediaController(this);
		surfaceView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(controller.isShowing()){
					System.out.println("隐藏前控制状态："+controller.isShowing());
					controller.hide();
					System.out.println("隐藏后控制状态："+controller.isShowing());
				}else{
					System.out.println("显示前控制原状态："+controller.isShowing());
					controller.show();
					System.out.println("显示后控制状态："+controller.isShowing());
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
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mediaPlayer.getDuration();
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
		mediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		mediaPlayer.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			Toast.makeText(this, "surface准备中出错 ，错误信息 ："+e.toString(), 1000).show();
			e.printStackTrace();
			System.out.println(e.toString()); 
		} catch (IOException e) {
			Toast.makeText(this, "surface准备中出错 ，错误信息 ："+e.toString(), 1000).show();
			e.printStackTrace();
		}
		Toast.makeText(this, "surface 准备中", 800).show();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mediaPlayer!=null){
			SharedPreferences prefs = getSharedPreferences("myDataStorage", MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString("url", filePath);
			editor.putInt("position",mediaPlayer.getCurrentPosition() );
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
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
		}
		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
//		surfaceHolder.setFixedSize(videoWidth, videoHeight);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Toast.makeText(this, "进入准备", 500).show();
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
		}
		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
//		surfaceHolder.setFixedSize(videoWidth, videoHeight); 
		controller.setMediaPlayer(this);
		controller.setAnchorView(this.findViewById(R.id.page_playershow_mainview));
		controller.setEnabled(true);
		controller.show();
		if(isContinue){
			Toast.makeText(this, "开始继续播放",500).show();
			mp.seekTo(contiuePosition);
		}else{
			Toast.makeText(this, "从头开始播放",500).show();
		}
		mp.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
//		Toast.makeText(this, "缓冲进度:"+percent+"%", 200).show();
		System.out.println( "缓冲进度:"+percent+"%");
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		Toast.makeText(this, "信息读取状态代码："+what, 500).show();
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(this, "错误代码："+what , 500).show();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Toast.makeText(this, "播放完毕", 500).show();
	}

	
	
	
}
