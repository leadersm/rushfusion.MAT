package com.rushfusion.mat.page;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.rushfusion.mat.R;
import com.rushfusion.mat.control.ConstructRequestXML;
import com.rushfusion.mat.control.MscpDataParser;
import com.rushfusion.mat.control.STB;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class ScreenControl extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "ScreenControl" ;
	private static final int PORT = 6806;
	private static final int DIALOG_NETWORK = 0;
	private static final int DIALOG_PROGRESS = 1;
	
	
	private TextView mIp;
	private Button searchBtn, clearBtn;
	private LayoutInflater inflater;
	private String localIp = "";

	private List<STB> stbs = new ArrayList<STB>();
	private LinearLayout stblist;
	private Handler handler;
	private DatagramSocket s = null;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
		Bundle bundle = getIntent().getExtras() ;
		viodeUrl = bundle.getString("viodeUrl");
		fileName = bundle.getString("name");
		duration = bundle.getString("duration");
		init();
	}

	private void init() {
		if (checkNetworking(this)) {
			try {
				s = new DatagramSocket(PORT);
				sp = getSharedPreferences("interact_title",MODE_WORLD_WRITEABLE);
				editor = sp.edit();
				findByIds();
				Thread mReceiveThread = new Thread(updateThread);
				mReceiveThread.start();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		} else
			showDialog(DIALOG_NETWORK);
	}

	/**
	 * 检查网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetworking(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if (nwi != null) {
			return nwi.isAvailable();
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NETWORK:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("网络连接提示");
			builder.setMessage("当前没有可用网络，是否设置?")
					.setCancelable(false)
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Settings.ACTION_WIRELESS_SETTINGS);
									startActivity(intent);
									init();
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							});
			return builder.create();
		case DIALOG_PROGRESS:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("提示!");
			dialog.setMessage("正在搜索,请稍后...");
			return dialog;
		default:
			break;
		}
		return null;
	}
	
	private void searchSTBIP() {
		searchBtn.setEnabled(false);
		showDialog(DIALOG_PROGRESS);
		localIp = getLocalIpAddress();// "192.168.2.xxx";
		final String destIp = localIp.substring(0,localIp.lastIndexOf(".") + 1);
		System.out.println("destIp---->" + destIp);
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 2; i < 255; i++) {
					if (!localIp.equals(destIp + i))
						search(destIp + i);
				}
				dismissDialog(DIALOG_PROGRESS);
			}
		}).start();
	}
	
	private void findByIds() {
		inflater = LayoutInflater.from(this);
		mIp = (TextView) findViewById(R.id.mIp);
		searchBtn = (Button) findViewById(R.id.search);
		clearBtn = (Button) findViewById(R.id.clear);
		stblist = (LinearLayout) findViewById(R.id.list);
		String currentIp = getLocalIpAddress() ;
		Log.d(TAG, currentIp) ;
		mIp.setText("本机ip-->" + currentIp);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				STB stb = (STB) msg.obj;
				stblist.addView(getView(stb));
			}

			public View getView(STB stb) {
				ViewHolder holder = new ViewHolder();
				View view = inflater.inflate(R.layout.stbitem, null);
				holder.name = (TextView) view.findViewById(R.id.stbName);
				holder.ip = (TextView) view.findViewById(R.id.stbIp);
				holder.title = (EditText) view.findViewById(R.id.title);
				holder.play = (Button) view.findViewById(R.id.play);
				holder.pause = (Button) view.findViewById(R.id.pause);
				holder.stop = (Button) view.findViewById(R.id.stop);
				holder.seekBar = (SeekBar) view.findViewById(R.id.seekBar) ;
				holder.init(stb);
				return view;
			}
			
		};
		
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchSTBIP() ;
			}
		});
		
		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchBtn.setEnabled(true);
				stblist.removeAllViews();
				stbs.clear();
			}
		});

		searchSTBIP() ;
	}

	public void search(String destip) {
		try {
			InetAddress stbIp = InetAddress.getByName(destip);
			byte[] data = ConstructRequestXML.SearchReq("123456", getLocalIpAddress());
			DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
			s.send(p);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	Runnable updateThread = new Runnable() {
		public void run() {
			System.out.println("the receive-thread is running");
			startReceive();
		}
	};
	private String viodeUrl;
	private String fileName;
	private String duration;

	protected void startReceive() {
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (true) {
				s.receive(packet);
				if (packet.getLength() > 0) {
					String str = new String(buffer, 0, packet.getLength());
					System.out.println("receive-->" + str);
					MscpDataParser.getInstance().init(this);
					MscpDataParser.getInstance().parse(packet,
							new MscpDataParser.CallBack() {
								@Override
								public void onParseCompleted(
										HashMap<String, String> map) {
									if (map != null) {
										System.out.println("IP===>"+ map.get("IP"));
										if (!map.get("IP").equals("null")&& !map.get("IP").equals(localIp)) {
											STB stb = new STB(map.get("IP"),"test", "test", "test","test");
											if (!checkStbIsExist(stb)){
												stbs.add(stb);
												Message msg = new Message();
												msg.what = 1;
												msg.obj = stb;
												handler.sendMessageDelayed(msg, 200);
											}
										}
									}
								}
								private boolean checkStbIsExist(STB stb) {
									// TODO Auto-generated method stub
									for (STB temp : stbs) {
										if (temp.getIp().equals(stb.getIp()))
											return true;
									}
									return false;
								}

								@Override
								public void onError(int code, String desc) {

								}
							});
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (s != null)
			s.close();
	}
	
	private class ViewHolder {

		TextView name;
		TextView ip;
		EditText title;
		Button play,pause,stop;
		SeekBar seekBar ;
		
		public void init(final STB stb) {
			name.setText(stb.getUsername());
			ip.setText(stb.getIp());
			title.setText(sp.getString("title"+stb.getIp(), fileName));
			play.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d(TAG, "----------play-----------") ;
					Log.d(TAG, fileName) ;
					Log.d(TAG, viodeUrl) ;
					try {
						byte[] data = ConstructRequestXML.PlayReq(1, stb.getIp(), fileName, 1000,viodeUrl);
						InetAddress stbIp = InetAddress.getByName(stb.getIp());
						DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
						s.send(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			pause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						byte[] data = ConstructRequestXML.PauseReq(1, viodeUrl);
						InetAddress stbIp = InetAddress.getByName(stb.getIp());
						DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
						s.send(p);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						byte[] data = ConstructRequestXML.StopReq(1, viodeUrl) ;
						InetAddress stbIp = InetAddress.getByName(stb.getIp());
						DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
						s.send(p);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			////////
			seekBar.setMax(100) ;
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				private int seekPosition;

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					try {
						Log.d(TAG, "=======onStopTrackingTouch======") ;
						Log.d(TAG, "seekPosition:"+seekPosition) ;
						byte[] data = ConstructRequestXML.SeekReq(1, viodeUrl, seekPosition) ;
						InetAddress stbIp = InetAddress.getByName(stb.getIp());
						DatagramPacket p = new DatagramPacket(data, data.length, stbIp,ConstructRequestXML.STB_PORT);
						s.send(p);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if(fromUser) {
						seekPosition = seekBar.getProgress();
					}
				}
			}) ;
			
		}
		
		
		
	}

}
