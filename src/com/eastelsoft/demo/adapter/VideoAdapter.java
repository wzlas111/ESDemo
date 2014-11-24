package com.eastelsoft.demo.adapter;

import java.util.List;

import com.eastelsoft.demo.R;
import com.eastelsoft.demo.bean.VideoInfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<VideoInfoBean> mList;
	
	public VideoAdapter(Context context, List<VideoInfoBean> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_video, parent, false);
			viewHolder.iv_type = (ImageView)convertView.findViewById(R.id.video_type);
			viewHolder.tv_title = (TextView)convertView.findViewById(R.id.video_title);
			viewHolder.tv_source = (TextView)convertView.findViewById(R.id.video_source);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		VideoInfoBean bean = mList.get(position);
		if (bean.type.equals("audio")) {
			viewHolder.iv_type.setImageResource(R.drawable.camera_dark_off);
		} else {
			viewHolder.iv_type.setImageResource(R.drawable.camera_dark_on);
		}
		viewHolder.tv_title.setText(bean.id);
		
		return convertView;
	}
	
	public class ViewHolder {
		ImageView iv_type;
		TextView tv_title;
		TextView tv_source;
	}

}
