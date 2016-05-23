package org.kerwin.wirelessadb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Looper;
import android.util.DisplayMetrics;

/**
 * 在Application中统一捕获异常
 * 
 * @author tony
 * 
 */
public final class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	private static CrashHandler INSTANCE = new CrashHandler();
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Activity activity;

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Activity activity) {
		this.activity = activity;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable e) {
		if (!handleException(e) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, e);
		} else {
			// android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(10);
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				new AlertDialog.Builder(activity)
						.setTitle(R.string.error_title)
						.setCancelable(false)
						.setMessage(R.string.error)
						.setNeutralButton(R.string.button_close,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										System.exit(0);
									}
								}).create().show();
				Looper.loop();
			}
		}.start();
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @return true代表处理该异常，不再向上抛异常，
	 *         false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
	 *         简单来说就是true不会弹出那个错误提示框，false就会弹出
	 */
	private boolean handleException(final Throwable e) {
		if (e == null) {
			return true;
		}
		OutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ps.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
				.getDefault()).format(new Date()));
		ps.println(Build.MODEL + "/" + Build.VERSION.SDK_INT + "/"
				+ Build.VERSION.RELEASE + "/" + getDisplayScreenResolution());
		e.printStackTrace(ps);
		if (os != null && ps != null) {
			EmailUtil.dynaSendEmail("1603013767@qq.com", os.toString());
			try {
				ps.close();
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return true;
	}

	public String getDisplayScreenResolution() {
		int ver = Build.VERSION.SDK_INT;

		DisplayMetrics dm = new DisplayMetrics();
		android.view.Display display = activity.getWindowManager()
				.getDefaultDisplay();
		display.getMetrics(dm);

		int screen_w = dm.widthPixels;
		int screen_h = dm.heightPixels;

		if (ver == 13) {
			try {
				Method mt = display.getClass().getMethod("getRealHeight");
				screen_h = (Integer) mt.invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (ver > 13) {
			try {
				Method mt = display.getClass().getMethod("getRawHeight");
				screen_h = (Integer) mt.invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return screen_w + "*" + screen_h;
	}

}
