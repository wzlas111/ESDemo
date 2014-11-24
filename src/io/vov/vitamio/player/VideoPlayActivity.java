package io.vov.vitamio.player;

import com.eastelsoft.demo.R;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class VideoPlayActivity extends FragmentActivity {
	
	private VideoFragment mVideoFragment;
	private FrameLayout mVideoContent;
	private ScrollView mScrollView;
	
	private String mUrl = "http://wap.cmread.com/bbc/p/video_play.jsp?ftl_video=";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.player_activity_video);
		
//		getActionBar().hide();
		getDisplayMetrics();
		initView();
		
		Intent intent = getIntent();
		System.out.println("intent url : "+intent.getStringExtra("url"));
		mUrl = mUrl + intent.getStringExtra("url");
		
		initFragment();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mVideoFragment.setVideoScale();
		if (newConfig.orientation == 2) { //land
			mVideoContent.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.heightPixels, displayMetrics.widthPixels));
			mScrollView.setVisibility(View.GONE);
			getWindow().addFlags(1024);
		} else { //port
			mVideoContent.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.widthPixels*3/4));
			mScrollView.setVisibility(View.VISIBLE);
			getWindow().clearFlags(1024);
		}
	}
	
	private void initView() {
		mVideoContent = (FrameLayout)findViewById(R.id.video_content);
		mVideoContent.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.widthPixels*3/4));
		mScrollView = (ScrollView)findViewById(R.id.content_scrollview);
	}
	
	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		if (fm.findFragmentByTag(VideoFragment.class.getName()) == null) {
			mVideoFragment = new VideoFragment();
			mVideoFragment.setUrl(mUrl);
			transaction.replace(R.id.video_content, mVideoFragment, VideoFragment.class.getName());
		}
		transaction.commit();
	}
	
	private DisplayMetrics displayMetrics;
	public DisplayMetrics getDisplayMetrics() {
        if (displayMetrics != null) {
            return displayMetrics;
        } else {
        	Display display = this.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            this.displayMetrics = metrics;
            return metrics;
        }
    }
}
