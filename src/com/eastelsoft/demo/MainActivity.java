package com.eastelsoft.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.concentriclivers.mms.com.android.internal.telephony.Phone;
import com.concentriclivers.mms.com.android.mms.transaction.TransactionSettings;
import com.concentriclivers.mms.com.android.mms.util.DownloadManager;

import io.vov.vitamio.player.InitPlayerService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button mVideoBtn;
	private Button mMmsBtn;
	private Button mSettingBtn;
	private Button mTestBtn;

	private PendingIntent alarmIntent;
	private AlarmManager alarmManager;
	
	private String m_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		m_id = intent.getStringExtra("m_id"); 
		
		setContentView(R.layout.activity_main);

		initView();
		initAlarm();
		startService(new Intent(this, InitPlayerService.class));
		DownloadManager.init(getApplicationContext());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume : m_id ->"+m_id);
//		Toast.makeText(this, "m_id = "+m_id, Toast.LENGTH_SHORT).show();
	}

	private void initAlarm() {
		Intent intent = new Intent("com.wangzl.action.alarm");
		alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		if (alarmManager != null) {
			alarmManager.cancel(alarmIntent);
		}
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 600 * 6 * 1000, alarmIntent);
	}

	private void initView() {
		mVideoBtn = (Button) findViewById(R.id.video_test);
		mMmsBtn = (Button) findViewById(R.id.mms_test);
		mSettingBtn = (Button) findViewById(R.id.setting_test);
		mTestBtn = (Button) findViewById(R.id.just_test);
		mVideoBtn.setOnClickListener(this);
		mMmsBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mTestBtn.setOnClickListener(this);
	}

	private ConnectivityManager mConnMgr;
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.video_test:
			intent.setClass(this, VideoTestActivity.class);
			startActivity(intent);
			break;
		case R.id.mms_test:
			intent.setClass(this, MmsTestActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_test:
			intent.setClass(this, SettingTestActivity.class);
			startActivity(intent);
			break;
		case R.id.just_test:
			mConnMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			mHandler.sendEmptyMessage(0);
		}
	}

	//http://211.140.12.234:192/Xidi52
	private void test() {
		TransactionSettings settings = new TransactionSettings(this,null);
		ensureRouteToHost("http://211.140.12.234:193/0-pOD2", settings);
	}

	private void ensureRouteToHost(String url, TransactionSettings settings) {
		ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		int inetAddr;
		if (settings.isProxySet()) {//
			String proxyAddr = settings.getProxyAddress();
			System.out.println("test : "+connMgr.getActiveNetworkInfo());
			NetworkInfo ni = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_MMS);
			System.out.println("test : ni "+(ni == null ? false : ni.isAvailable()));
			System.out.println("test : "+proxyAddr);
			inetAddr = lookupHost(proxyAddr);
			System.out.println("test : "+inetAddr);
			if (inetAddr == -1) {
				System.out.println("Cannot establish route for " + url+ ": Unknown host");
			} else {
				if (!connMgr.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_MMS, inetAddr)) {
					System.out.println("Cannot establish route to proxy "+ inetAddr);
				}
			}
		} else {
			Uri uri = Uri.parse(url);
			inetAddr = lookupHost(uri.getHost());
			System.out.println("test : "+inetAddr);
			if (inetAddr == -1) {
				System.out.println("Cannot establish route for " + url+ ": Unknown host");
			} else {
				if (!connMgr.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_MMS, inetAddr)) {
					System.out.println("Cannot establish route to "+ inetAddr + " for " + url);
				}
			}
		}
	}

	public static int lookupHost(String hostname) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			return -1;
		}
		byte[] addrBytes;
		int addr;
		addrBytes = inetAddress.getAddress();
		addr = ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16)
				| ((addrBytes[1] & 0xff) << 8) | (addrBytes[0] & 0xff);
		return addr;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				int result = beginMMS();
				System.out.println("test : result -> "+result);
				if (result == Phone.APN_ALREADY_ACTIVE) {
					test();
					return;
				}
				mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
				break;
			}
		}
	};
	
	private int beginMMS() {
		int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, Phone.APN_TYPE_MMS);
		return result;
	}
	
}
