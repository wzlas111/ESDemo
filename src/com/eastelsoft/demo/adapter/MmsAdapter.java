package com.eastelsoft.demo.adapter;

import java.util.List;

import com.eastelsoft.demo.R;
import com.eastelsoft.demo.bean.MmsInfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MmsAdapter extends BaseAdapter {

	private Context mContext;
	private List<MmsInfoBean> mList;

	public MmsAdapter(Context context,List<MmsInfoBean> pList) {
		mContext = context;
		mList = pList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_mms, parent, false);
			viewHolder.iv_avatar = (ImageView)convertView.findViewById(R.id.avatar);
			viewHolder.tv_sms_telephone = (TextView)convertView.findViewById(R.id.sms_telephone);
			viewHolder.tv_sms_sub_title = (TextView)convertView.findViewById(R.id.sms_subtitle);
			viewHolder.tv_sms_date = (TextView)convertView.findViewById(R.id.sms_date);
			viewHolder.iv_thumbnail_pic = (ImageView)convertView.findViewById(R.id.thumbnail_pic);
			viewHolder.v_content_downloaded = convertView.findViewById(R.id.content_downloaded);
			viewHolder.v_content_download_not = convertView.findViewById(R.id.content_download_not);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		MmsInfoBean bean = mList.get(position);
		viewHolder.tv_sms_telephone.setText(bean.telephone);
		viewHolder.tv_sms_date.setText(bean.date);
		viewHolder.tv_sms_sub_title.setText("主题:"+bean.title);
		if (bean.img != null && !"".equals(bean.img)) {
			viewHolder.iv_thumbnail_pic.setVisibility(View.VISIBLE);
			viewHolder.iv_thumbnail_pic.setImageBitmap(bean.img);
		} else {
			viewHolder.iv_thumbnail_pic.setVisibility(View.GONE);
		}
		if (bean.m_id != null && !"".equals(bean.m_id)) {
			viewHolder.v_content_downloaded.setVisibility(View.VISIBLE);
			viewHolder.v_content_download_not.setVisibility(View.GONE);
		} else {
			viewHolder.v_content_downloaded.setVisibility(View.GONE);
			viewHolder.v_content_download_not.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	public class ViewHolder {
		ImageView iv_avatar;
		TextView tv_sms_telephone;
		TextView tv_sms_sub_title;
		TextView tv_sms_date;
		TextView tv_sms_content;
		ImageView iv_thumbnail_pic;
		View v_content_downloaded;
		View v_content_download_not;
	}
}
