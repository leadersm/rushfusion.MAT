package com.rushfusion.mat.page;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.rushfusion.mat.R;
import com.rushfusion.mat.control.ConstructRequestXML;
import com.rushfusion.mat.control.ReceiveService;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.utils.ItemDetailGridViewAdapter;
import com.rushfusion.mat.video.entity.Movie;

public class ItemDetailPage extends Activity{
	private static final int PORT = 6806;
	ImageView image;
	TextView name;
	TextView description;
	TextView socres;
	TextView length;
	TextView year;
	TextView directors;
	TextView artists;
	GridView episode;
	Movie movie ;
	private View adapterView ;
	ItemDetailGridViewAdapter gda;
	DatagramSocket s ;

	List<String> list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.page_item_detail);
		movie = (Movie) getIntent().getSerializableExtra("movieInfo");
//		if(movie==null){
//			movie = new Movie(1, 21, 85, 3422, "teleplay", "栗洋的生活", "现代", 2012, "gustav", "wangying", "大陆", "sldhglnoengdinrkgndkrgnkdngrkdnskalnrnekl", "http://cache.mars.sina.com.cn/nd/movievideo/thumb/39/3739_mc.jpg", "2:03:02", "1|http://v.iask.com/v_play_ipad.php?vid=71014567^2|http://v.iask.com/v_play_ipad.php?vid=71014693^3|http://v.iask.com/v_play_ipad.php?vid=71063490^4|http://v.iask.com/v_play_ipad.php?vid=71064184^5|http://v.iask.com/v_play_ipad.php?vid=71115062^6|http://v.iask.com/v_play_ipad.php?vid=71114760^7|http://v.iask.com/v_play_ipad.php?vid=71169821^8|http://v.iask.com/v_play_ipad.php?vid=71163134^9|http://v.iask.com/v_play_ipad.php?vid=71233199^10|http://v.iask.com/v_play_ipad.php?vid=71232205^11|http://v.iask.com/v_play_ipad.php?vid=71284679^12|http://v.iask.com/v_play_ipad.php?vid=71284659^13|http://v.iask.com/v_play_ipad.php?vid=71340181^14|http://v.iask.com/v_play_ipad.php?vid=71341611^15|http://v.iask.com/v_play_ipad.php?vid=71395392^16|http://v.iask.com/v_play_ipad.php?vid=71395892^17|http://v.iask.com/v_play_ipad.php?vid=71451799^18|http://v.iask.com/v_play_ipad.php?vid=71453704^19|http://v.iask.com/v_play_ipad.php?vid=71496952^20|http://v.iask.com/v_play_ipad.php?vid=71497152^21|http://v.iask.com/v_play_ipad.php?vid=71538687^22|http://v.iask.com/v_play_ipad.php?vid=71539613^23|http://v.iask.com/v_play_ipad.php?vid=71604823^24|http://v.iask.com/v_play_ipad.php?vid=71606834^25|http://v.iask.com/v_play_ipad.php?vid=71655172^26|http://v.iask.com/v_play_ipad.php?vid=71655382^27|http://v.iask.com/v_play_ipad.php?vid=71698978^28|http://v.iask.com/v_play_ipad.php?vid=71699284^29|http://v.iask.com/v_play_ipad.php?vid=71757029^30|http://v.iask.com/v_play_ipad.php?vid=71756847^31|http://v.iask.com/v_play_ipad.php?vid=71777143^32|http://v.iask.com/v_play_ipad.php?vid=71777214^35|http://v.iask.com/v_play_ipad.php?vid=71817577^36|http://v.iask.com/v_play_ipad.php?vid=71817747^37|http://v.iask.com/v_play_ipad.php?vid=71817352", 12, 1555, 12);
//		}
		image = (ImageView) findViewById(R.id.page_item_detail_image);
		name = (TextView) findViewById(R.id.page_item_detail_name);
		description = (TextView) findViewById(R.id.page_item_detail_description);
		socres = (TextView) findViewById(R.id.page_item_detail_socres);
		length = (TextView) findViewById(R.id.page_item_detail_length);
		year = (TextView) findViewById(R.id.page_item_detail_year);
		directors = (TextView) findViewById(R.id.page_item_detail_directors);
		artists = (TextView) findViewById(R.id.page_item_detail_artists);
		episode = (GridView) findViewById(R.id.page_item_detail_episode);
//		image.setImageURI(Uri.parse(movie.getThumb()));
		ImageLoadTask.imageLoad(image, movie.getThumb());
		image.setScaleType(ScaleType.FIT_XY);
		name.setText(movie.getName());
		description.setText(movie.getDescription());
		socres.setText(movie.getScore()+"");
		length.setText(movie.getLength());
		year.setText(movie.getYear()+"");
		directors.setText(movie.getDirectors());
		artists.setText(movie.getArtists());
		list = new ArrayList<String>();
		if(movie.getUrl().indexOf("^")==-1){
			list.add(movie.getUrl()); 
		}else{
			String[] str = movie.getUrl().split("\\^");
			for(String urlstr : str){
				String[] str2 = urlstr.split("\\|");
				list.add(str2[1]);
			}
		}
		gda = new ItemDetailGridViewAdapter(this, list);
		episode.setAdapter(gda);
		episode.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 播放  163:http://163.letv.com/dianying/E7JRABGJ9/M7KS70Q27_mini.html
				adapterView=arg1 ;
				adapterView.setTag(arg2) ;
				showDialog() ;

			}

		});
	}
	
	private Dialog dialog ;
	public void showDialog() {
		dialog = new Dialog(this) ;
		View view = getLayoutInflater().inflate(R.layout.choice_dialog, null) ;
		dialog.setContentView(view) ;
		dialog.show() ;
		View tv_play = view.findViewById(R.id.tv_play) ;
		tv_play.setOnClickListener(ChoiceListener) ;
		View mobile_play = view.findViewById(R.id.mobile_play) ;
		mobile_play.setOnClickListener(ChoiceListener) ;
	}
	
	private void dismissDialog() {
		dialog.dismiss() ;
	}
	
	View.OnClickListener ChoiceListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) adapterView.getTag() ;
			String path = list.get(position);
			if(v.getId()==R.id.tv_play) {
				/*SharedPreferences preferences = getSharedPreferences("server_ip", 0) ;
				String IP = preferences.getString("IP", "") ;
				Log.d("IP", "IP:"+IP) ;
				try {
					s = ReceiveService.getSocket() ;
					byte[] data = ConstructRequestXML.PlayReq(1, IP, movie.getName(), 0, path);
					InetAddress stbIp = InetAddress.getByName(IP);
					DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
					s.send(p);
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				dismissDialog() ;
				Intent intent = new Intent(ItemDetailPage.this,ScreenControl.class) ;
				Bundle bundle = new Bundle() ;
				bundle.putString("viodeUrl", path) ;
				bundle.putString("name", movie.getName()) ;
				bundle.putString("duration", movie.getLength()) ;
				intent.putExtras(bundle) ;
				startActivity(intent) ;
				
			}
			if(v.getId()==R.id.mobile_play) {
				Log.d("ViodPlay", "viodPlay url:" + path) ;
				dismissDialog() ;
				if(path.indexOf("html")==(path.length()-4)){
					Intent it = new Intent(Intent.ACTION_VIEW , Uri.parse(path));
					startActivity(it);
					
				}else{
					Intent it =  new Intent(ItemDetailPage.this, MediaPlayerShow.class);
//					Intent it =  new Intent(ItemDetailPage.this, VideoPlayer.class);
					Bundle bd = new Bundle();
					bd.putString("url", list.get(position));
					bd.putString("id", movie.getId()+""+position);
					it.putExtras(bd);
					startActivity(it);
				}
			}
		}
	};
	
	
}
