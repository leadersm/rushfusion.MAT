package com.rushfusion.mat.page;


import com.rushfusion.mat.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VideoPlayer extends Activity implements SurfaceHolder.Callback,OnCompletionListener,OnSeekBarChangeListener,MediaPlayer.OnPreparedListener{
	private int current;
	private int videoWidth;
	private int videoHeight;
	private MediaPlayer player;
	private SurfaceView preview;
	private SurfaceHolder holder;
	private LinearLayout play_show;
	private LinearLayout play_control;
	private Surface surface;
	private TextView play_video_current;
	private TextView play_video_duration;
	private SeekBar play_video_seekbar;
	private Button btn_play_video;
	private Button btn_back_video;
	private Button btn_next_video;
	private boolean progressFlag=false;
	private boolean show=false;
	Handler handler_sent,handler_recive;
	Runnable run_sent,run_recive;
	ContentResolver resolver;
	Cursor cursor;
	HandlerThread ht;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.play_video);
		
		play_show=(LinearLayout)findViewById(R.id.page_playvideo_show);
		play_control=(LinearLayout)findViewById(R.id.page_playvideo_control);
		play_video_current=(TextView)findViewById(R.id.page_playvideo_current);
		play_video_seekbar=(SeekBar)findViewById(R.id.page_playvideo_seekbar);
		play_video_duration=(TextView)findViewById(R.id.page_playvideo_duration);
		btn_back_video=(Button)findViewById(R.id.page_playvideo_btn_back_video);
		btn_next_video=(Button)findViewById(R.id.page_playvideo_btn_next_video);
		btn_play_video=(Button)findViewById(R.id.page_playvideo_btn_play_video);
		preview=(SurfaceView)findViewById(R.id.page_playvideo_surface);
		ht=new HandlerThread("HTDemo");
		holder=preview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		play_video_seekbar.setOnSeekBarChangeListener(this);
		resolver=getContentResolver();
		Intent i=getIntent();
		ht.start();
		handler_sent = new Handler(ht.getLooper());
		run_sent=new Runnable() {
			
			public void run() {
				Message msg=handler_recive.obtainMessage();
				try{
					msg.arg1=player.getCurrentPosition();
					handler_recive.sendMessage(msg);
					handler_recive.postDelayed(run_sent, 100);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		handler_recive=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				current=msg.arg1;
				play_video_seekbar.setProgress(current);
				play_video_current.setText(timeToString(current));
			}
			
		};
		
		preview.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(show){
					play_control.setVisibility(View.GONE);
					show=false;
				}else{
					play_control.setVisibility(View.VISIBLE);
					show=true;
				}
			} 
		});
		btn_back_video.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!cursor.moveToPrevious()){
					cursor.moveToLast();
				}
				playVideo();
			}
		});
		btn_next_video.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!cursor.moveToNext()){
					cursor.moveToFirst();
				}
				playVideo();
			}
		});
		btn_play_video.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(player!=null&&player.isPlaying()){
					player.pause();
					btn_play_video.setText("Play");
				}else{
					player.start();
					btn_play_video.setText("Pasue");
				}
			}
		});
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		playVideo();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	@Override
	protected void onDestroy() {
		if(player!=null){
			handler_sent.removeCallbacks(run_sent);
			player.release();
		}
		super.onDestroy();
	}

	public void onCompletion(MediaPlayer mp) {
		if(!cursor.moveToPrevious()){
			
			cursor.moveToLast();
			playVideo();
			play_video_seekbar.setProgress(0);
			
		}else{
			handler_sent.removeCallbacks(run_sent);
			play_video_seekbar.setProgress(0);
			Toast.makeText(this, "影片播放结束,谢谢观赏", Toast.LENGTH_LONG).show();
		}
	}

	public void onPrepared(MediaPlayer mp) {
		videoHeight=player.getVideoHeight();
		videoWidth=player.getVideoWidth();
		play_video_current.setText("00:00");
		play_video_duration.setText(timeToString(player.getDuration()));
		if(videoHeight!=0 && videoWidth!=0){
			holder.setFixedSize(videoWidth, videoHeight);
			play_video_seekbar.setMax(player.getDuration());
			player.start();
			handler_sent.post(run_sent);
		}
	}
	
	

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		progressFlag=false;
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		player.seekTo(play_video_seekbar.getProgress());
		progressFlag=true;
	}
	
	private void playVideo() {
		String path="http://v.iask.com/v_play_ipad.php?vid=33184708";
		if(player==null){
			player=new MediaPlayer();
			player.setOnPreparedListener(this);
			player.setOnCompletionListener(this);
		}else{
			player.reset();
		}
		try {
			player.setDataSource(path);
			player.setDisplay(holder);
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String timeToString(int i){
		Long l=Long.valueOf(i);
		if(l<0){
			return "00:00";
		}
		StringBuffer sb=new StringBuffer();
		long m=l/(60000);
		long s=(l%60000)/1000;
		sb.append(m<10?"0"+m:m);
		sb.append(":");
		sb.append(s<10?"0"+s:s);
		return sb.toString();
	}
}
