package com.eastelsoft.demo;

import io.vov.vitamio.player.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.demo.adapter.VideoAdapter;
import com.eastelsoft.demo.bean.VideoInfoBean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class VideoTestActivity extends Activity {
	
	private String[] AUDIO_URLS = {"http://221.181.100.53:8088/698001/20140926/16/2200050693/80254785/1000004305_000_47.3gp?sid=2200050693",
            "http://221.181.100.53:8088/698001/20140926/16/2200050693/80254784/1000004305_000_48.3gp?sid=2200050693",
            "http://221.181.100.53:8088/698001/20140926/16/2200050693/80254783/1000004305_000_49.3gp?sid=2200050693"};
	private String[] VIDEO_URLS = {"http://221.181.100.53:8088/698001/20141029/16/2200134111/80685065/1000006185_000_52.mp4.m3u8?sid=2200134111",
			   "http://221.181.100.53:8088/698001/20141029/16/2200134111/80685067/1000006185_000_54.mp4.m3u8?sid=2200134111",
			   "http://221.181.100.53:8088/698001/20141029/16/2200134111/80685067/1000006185_000_55.mp4.m3u8?sid=2200134111",
			   "http://221.181.100.53:8088/698001/20141029/16/2200134111/80685067/1000006185_000_56.mp4.m3u8?sid=2200134111",
			   "http://nvod.hoolo.tv/2014/1408/7245/6993/140872456993.ssm/140872456993.m3u8"};

	private ListView mListView;
	private VideoAdapter mAdapter;
	private List<VideoInfoBean> mData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videotest);
		
		initData();
		initView();
	}
	
	private void initView() {
		mAdapter = new VideoAdapter(this, mData);
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("mlist size :"+mData.size()+",curr : "+position);
				VideoInfoBean bean = mData.get(position);
				Intent intent = new Intent(VideoTestActivity.this, VideoPlayActivity.class);
				intent.putExtra("url", bean.url);
				startActivity(intent);
			}
		});
	}
	
	private void initData() {
		mData = new ArrayList<VideoInfoBean>();
		for (int i = 0; i < AUDIO_URLS.length; i++) {
			VideoInfoBean bean = new VideoInfoBean();
			bean.id = "和新闻测试音频 -- audio_"+i;
			bean.url = AUDIO_URLS[i];
			bean.type = "audio";
			mData.add(bean);
		}
		for (int i = 0; i < VIDEO_URLS.length; i++) {
			VideoInfoBean bean = new VideoInfoBean();
			bean.id = "和新闻测试视频 -- video_"+i;
			bean.url = VIDEO_URLS[i];
			bean.type = "video";
			mData.add(bean);
		}
	}
}
