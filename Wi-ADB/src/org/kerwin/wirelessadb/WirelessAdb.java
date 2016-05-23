package org.kerwin.wirelessadb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WirelessAdb extends Activity {

	public static final String PORT = "5555";
	public static final String MSG_TAG = "WIRELESSADB";
	
	private WifiManager wifiManager;
	private NotificationManager notificationManager;

	private static boolean state = false;
	private static final int START_NOTIFICATION_ID = 1;

	private View mainll;
	private ImageView iv;
	private long mExitTime;
	private TextView btntv;
	private TextView abouttv;
	private TextView labeltv;
	private TextView resulttv;
	private ProgressDialog spinner;
	private SharedPreferences sharedPreferences;
	
	private Handler mHandler;
    
    static class MyHandler extends Handler {  
        WeakReference<WirelessAdb> mActivity;  

        MyHandler(WirelessAdb activity) {  
                mActivity = new WeakReference<WirelessAdb>(activity);  
        }  

        @Override  
        public void handleMessage(Message msg) { 	//此方法在ui线程运行   
        	WirelessAdb theActivity = mActivity.get();  
        	((Vibrator) theActivity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(30);
        	theActivity.updateState();
        	theActivity.spinner.cancel();
        	theActivity.btntv.setEnabled(true);
        }  
    }
    
//    /**
//     * 获取状态栏高度
//     * @return
//     */
//    private int getStatusBarHeight(){
//    	Rect frame = new Rect();  
//    	getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
//    	int statusBarHeight = frame.top;  
//    	int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();  
//        int titleBarHeight = contentViewTop - statusBarHeight;
//        Log.v("test", "=-init-= statusBarHeight=" + statusBarHeight  
//                + " contentViewTop=" + contentViewTop + " titleBarHeight="  
//                + titleBarHeight); 
//        return statusBarHeight;
//    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		kitkatPpersonalizedSettings();
		setContentView(R.layout.main);
		mHandler = new MyHandler(this);
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this); 
		
		spinner = new ProgressDialog(WirelessAdb.this);
		mainll = findViewById(R.id.mainll);
		iv = (ImageView) findViewById(R.id.iv);
		btntv = (TextView) findViewById(R.id.btntv);
		abouttv = (TextView) findViewById(R.id.abouttv);
		labeltv = (TextView) findViewById(R.id.labeltv);
		resulttv = (TextView) findViewById(R.id.resulttv);

		sharedPreferences = getSharedPreferences("wireless", 0);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		btntv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				btntv.setEnabled(false);
				if (!state) {
					if(!checkBeforRun()){
						btntv.setEnabled(true);
						return;
					}	
					spinner.setMessage(getString(R.string.Turning_on));
					btntv.setText(R.string.oningtext);
					spinner.setCancelable(false);
					spinner.show();
				} else {
					spinner.setMessage(getString(R.string.Turning_off));
					btntv.setText(R.string.offingtext);
					spinner.setCancelable(false);
					spinner.show();
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500);
							if (!state) {
								adbStart();
							} else {
								adbStop();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						mHandler.obtainMessage().sendToTarget();
					}
				}).start();
				
			}
		});
		
		iv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				abouttv.setVisibility((abouttv.getVisibility() == android.view.View.VISIBLE) ? android.view.View.INVISIBLE : android.view.View.VISIBLE);
			}
		});
	}
	
	/**
	 * Android KITKAT 个性化设置
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void kitkatPpersonalizedSettings() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	// 如果不希望 APP 的内容被上拉到状态列 (Status bar) 的话，要记得在介面 (Layout) XML 档中，最外面的那层，要再加上一个属性 fitsSystemWindows 为 true
        	// 在介面的根层加入 android:fitsSystemWindows="true" 这个属性，这样就可以让内容介面从 Action Bar 下方开始。
        	// Translucent status bar
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        	// Translucent navigation bar
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
//        if(getActionBar() != null)	getActionBar().hide();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if ((System.currentTimeMillis() - mExitTime) > 2000) {
        		//提示连续按两次返回键退出程序
        		Toast.makeText(this, R.string.exitconfirm, Toast.LENGTH_SHORT).show();
        		mExitTime = System.currentTimeMillis();
        	} else {
        		WirelessAdb.this.finish();
        	}
        	return true;
        }
        return super.onKeyDown(keyCode, event);
	}
	
	private boolean checkBeforRun(){
		boolean tmpstate = false;
		if ((tmpstate = !hasRootPermission())) {
			//如果没有ROOT权限、提示获取ROOT权限
			rootDialog();
		}

		int wifistate = -1;
		if ((tmpstate = (wifistate = checkWifiState()) != 1)) {
			//如果WIFI未打开或未连接、提示打开并连接WIFI
			wifiDialog(wifistate);
		}
		return !tmpstate;
	}
	
	/**
	 * 检测和更新状态
	 * @param state	当前状态
	 */
	private void checkAndUpdate(boolean state){
		
		boolean tmpstate = checkBeforRun();
		
		try {
			if (state != (isProcessRunning("adbd"))) {
				//检测进程中是否已存在实例、如果进程中的状态和参数状态不一致则更新配置
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("state", state);
				WirelessAdb.state = state;
				editor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (state && !tmpstate) {
			try {
				//如果当前状态为打开、而ROOT或WIFI无效、则停止
				adbStop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//更新界面
		updateState();
	}
	
	/**
	 * 关于WIFI弹窗提示
	 * @param state 如果state=-1则提示WIFI已关闭(激活/退出)、否则提示WIFI未连接(连接/退出)
	 */
	private void wifiDialog(final int state) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(state == -1 ? R.string.no_wifi : R.string.disconnect_wifi))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.button_exit),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							WirelessAdb.this.finish();
						}
					})
			.setNegativeButton(state == -1 ? R.string.button_activate_wifi : R.string.button_connect_wifi,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (state == -1) {
								enableWiFi(true);
							} else {
								connectWiFi();
							}
							dialog.cancel();
						}
					});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.create();
		builder.setTitle(R.string.no_wifi_title);
		builder.show();
	}
	
	/**
	 * 关于ROOT权限弹窗提示
	 */
	private void rootDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.no_root))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.button_close),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							WirelessAdb.this.finish();
						}
					});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.create();
		builder.setTitle(R.string.no_root_title);
		builder.show();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		if (spinner.isShowing()) spinner.cancel();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				checkAndUpdate(sharedPreferences.getBoolean("state", false));
			}
		});
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		try {
			adbStop();
		} catch (Exception e) {
		}
		try {
			notificationManager.cancelAll();
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	/**
	 * 根据当前结果更新界面
	 */
	private void updateState() {
		if (state) {
			String result = null;
			labeltv.setText(R.string.onstatus);
			labeltv.setVisibility(android.view.View.VISIBLE);
			try {
				resulttv.setText((result = "adb connect " + getWifiIp(wifiManager)));
			} catch (Exception e) {
				resulttv.setText((result = "adb turn on faild."));
			}
			btntv.setText(R.string.offtext);
			resulttv.setTextColor(getResources().getColor(R.color.result));
			mainll.setBackgroundColor(getResources().getColor(R.color.onbg));
			showNotification(R.drawable.icon, result);
		} else {
			btntv.setText(R.string.ontext);
			resulttv.setText(R.string.offstatus);
			labeltv.setVisibility(android.view.View.INVISIBLE);
			resulttv.setTextColor(getResources().getColor(R.color.white));
			mainll.setBackgroundColor(getResources().getColor(R.color.offbg));
		}
	}

	/**
	 * 开启adb
	 * @return
	 */
	private boolean adbStart() {
		// Log.d(MSG_TAG, "adbStart()");
		try {

			setProp("service.adb.tcp.port", PORT);
			if (isProcessRunning("adbd")) {
				runRootCommand("stop adbd");
			}
			runRootCommand("start adbd");
			state = true;
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("state", state);
			editor.commit();
			
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 关闭adb
	 * @return
	 * @throws Exception
	 */
	private boolean adbStop() throws Exception {
		// Log.d(MSG_TAG, "adbStop()");
		try {
			setProp("service.adb.tcp.port", "-1");
			runRootCommand("stop adbd");
			runRootCommand("start adbd");
			state = false;
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("state", state);
			editor.commit();
			notificationManager.cancelAll();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 查看进程是否存在adb实例
	 * @param processName
	 * @return
	 * @throws Exception
	 */
	private static boolean isProcessRunning(String processName)
			throws Exception {
		// Log.d(MSG_TAG, "isProcessRunning("+processName+")");
		boolean running = false;
		Process process = null;
		process = Runtime.getRuntime().exec("ps");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.contains(processName)) {
				running = true;
				break;
			}
		}
		in.close();
		process.waitFor();
		return running;
	}

	/**
	 * 判断是否有root权限
	 * @return
	 */
	private static boolean hasRootPermission() {
		// Log.d(MSG_TAG, "hasRootPermission()");
		Process process = null;
		DataOutputStream os = null;
		boolean rooted = true;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			if (process.exitValue() != 0) {
				rooted = false;
			}
		} catch (Exception e) {
			Log.d(MSG_TAG, "hasRootPermission error: " + e.getMessage());
			rooted = false;
		} finally {
			if (os != null) {
				try {
					os.close();
					process.destroy();
				} catch (Exception e) {
					// nothing
				}
			}
		}
		return rooted;
	}

	/**
	 * 运行root命令
	 * @param command
	 * @return
	 */
	private static boolean runRootCommand(String command) {
		// Log.d(MSG_TAG, "runRootCommand("+command+")");
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d(MSG_TAG,
					"Unexpected error - Here is what I know: " + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				// nothing
			}
		}
		return true;
	}

	/**
	 * 设置环境变量
	 * @param property
	 * @param value
	 * @return
	 */
	private static boolean setProp(String property, String value) {
		// Log.d(MSG_TAG, "setProp("+property+","+value+")");
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("setprop " + property + " " + value + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	/**
	 * 得到WIFI连接IP
	 * @param wifiManager
	 * @return
	 */
	private String getWifiIp(WifiManager wifiManager) {
		int ip = wifiManager.getConnectionInfo().getIpAddress();
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}
	
	/**
	 * 调用WIFI设置去连接WIFI
	 */
	private void connectWiFi() {
		try {
			startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置WIFI状态
	 * @param enable
	 */
	private void enableWiFi(final boolean enable) {
		spinner.setMessage(getString(enable ? R.string.Turning_on_wifi : R.string.Turning_off_wifi));
		spinner.setCancelable(false);
		spinner.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				wifiManager.setWifiEnabled(enable);
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (checkWifiState() == -1) {
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						spinner.cancel();
					}
				});
			}
		}).start();
	}

	/**
	 * 检测WIFI状态
	 * @return
	 */
	private int checkWifiState() {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (!wifiManager.isWifiEnabled()) {
			return -1;
		} else if (wifiInfo.getNetworkId() == -1 || wifiInfo.getSSID() == null) {
			return 0;
		}
		return 1;
	}
	
	/**
	 * 显示通知栏
	 * @param icon
	 * @param text
	 */
	private void showNotification(int icon, String text) {
		Notification notifyDetails = new Notification.Builder(getApplicationContext())
			.setContentText(text).setContentTitle(getResources().getString(R.string.appfullname)).setSmallIcon(icon).setContentIntent(
					PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), WirelessAdb.class), 0)).build();
		notifyDetails.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
		notifyDetails.flags |= Notification.FLAG_NO_CLEAR; 		// 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notificationManager.notify(START_NOTIFICATION_ID, notifyDetails);
	}
	
}