package com.eastelsoft.demo;

import com.eastelsoft.demo.util.SettingHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingTestActivity extends Activity {

	private Button mOpenBtn;
	private String mStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mStatus = SettingHelper.getMmsStatus(this);
		
		mOpenBtn = (Button)findViewById(R.id.is_open);
		if (mStatus.equals("open")) {
			mOpenBtn.setText("关闭");
		} else if(mStatus.equals("close")) {
			mOpenBtn.setText("打开");
		}
		mOpenBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStatus.equals("open")) {
					mStatus = "close";
					mOpenBtn.setText("打开");
				} else if(mStatus.equals("close")){
					mStatus = "open";
					mOpenBtn.setText("关闭");
				}
				SettingHelper.setMmsStatus(SettingTestActivity.this, mStatus);
			}
		});
	}
}
