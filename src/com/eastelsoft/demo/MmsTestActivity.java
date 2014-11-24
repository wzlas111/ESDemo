package com.eastelsoft.demo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.concentriclivers.mms.com.android.mms.transaction.Transaction;
import com.concentriclivers.mms.com.android.mms.transaction.TransactionBundle;
import com.concentriclivers.mms.com.android.mms.transaction.TransactionService;
import com.eastelsoft.demo.adapter.MmsAdapter;
import com.eastelsoft.demo.bean.MmsInfoBean;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MmsTestActivity extends Activity {
	
	private ListView mListView;
	private MmsAdapter mAdapter;
	private List<MmsInfoBean> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mmstest);
		
		mList = new ArrayList<MmsInfoBean>();
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new MmsAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MmsInfoBean bean = mList.get(position);
//				Intent intent = new Intent(this, MMsDetailActivity.class);
//				intent.putExtra("html", bean.html);
//				startActivity(intent);
				Toast.makeText(MmsTestActivity.this, "彩信uri:content://mms/inbox/"+bean.id+",地址url:"+bean.url, Toast.LENGTH_SHORT).show();
				System.out.println("彩信uri:content://mms/inbox/"+bean.id+",地址url:"+bean.url);
				if (bean.m_id != null && !"".equals(bean.m_id)) {
					return;
				}
				Intent intent = new Intent(MmsTestActivity.this, TransactionService.class);
                intent.putExtra(TransactionBundle.URI, Uri.parse("content://mms/inbox/"+bean.id).toString());
                intent.putExtra(TransactionBundle.TRANSACTION_TYPE,
                        Transaction.RETRIEVE_TRANSACTION);
				// TDH
                startService(intent);
			}
		});
		
		new InitDataAsyncTask(this).execute("");
	}
	
	private class InitDataAsyncTask extends AsyncTask<String, Integer, Boolean> {

		private Context mContext;
		private ContentResolver resolver;
		
		public InitDataAsyncTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			mList = new ArrayList<MmsInfoBean>();
			try {
				resolver = mContext.getContentResolver();
				Cursor cursor = resolver.query(Uri.parse("content://mms/inbox"), new String[] {"_id", "date", "sub", "m_type", "m_id", "ct_l"}, null, null, " date desc ");
				while(cursor.moveToNext()) {
					MmsInfoBean bean = new MmsInfoBean();
		            bean.id = cursor.getString(0);  
		            bean.date = dateFormat.format(new Date(cursor.getLong(1) * 1000));  
		            String title = cursor.getString(2);
		            String m_type = cursor.getString(3);
		            bean.m_id = cursor.getString(4);
		            bean.url = cursor.getString(5);
		            if (title != null && !"".equals(title)) {
		            	bean.title = new String(title.getBytes("iso-8859-1"),"UTF-8");
					} else {
						bean.title = "无";
					}
		            
		            //读取联系人
		            Uri addrUri = Uri.parse("content://mms/"+bean.id+"/addr");
		            Cursor addrCursor = resolver.query(addrUri, null, "msg_id="+bean.id, null, null);
		            if (addrCursor.moveToNext()) {
						String number = addrCursor.getString(addrCursor.getColumnIndex("address"));
						bean.telephone = number;
		            }
		            addrCursor.close();
		            
		            System.out.println("mms data -> m_type:"+m_type+", telephone:"+bean.telephone);
		            
//		            if (!bean.telephone.equals("10658000")) {
//						continue;
//					}
		            
		            //读取附件
		            StringBuffer sb = new StringBuffer();
		            Cursor subCursor = resolver.query(Uri.parse("content://mms/part"), null, "mid='"+bean.id+"'", null, null);
		            while(subCursor.moveToNext()) {
		            	String _id = subCursor.getString(subCursor.getColumnIndex("_id"));
		            	String mid = subCursor.getString(subCursor.getColumnIndex("mid"));
		            	String ct = subCursor.getString(subCursor.getColumnIndex("ct"));
		            	String text = subCursor.getString(subCursor.getColumnIndex("text"));
		            	String _data = subCursor.getString(subCursor.getColumnIndex("_data"));
//		            	sb.append("_id:"+_id+"|"+"mid:"+mid+"|"+"ct:"+ct+"|"+"_data:"+_data+"|"+"text:"+text+";;;;");
		            	if ("image/jpeg".equals(ct) || "image/bmp".equals(ct) || "image/gif".equals(ct)
								|| "image/png".equals(ct) || "image/jpg".equals(ct)) {
		            		if (bean.img == null) {
		            			bean.img = getImage(_id);
							}
		            		sb.append("<image src='"+_data+"'>");
						} else if ("text/plain".equals(ct)) {
							sb.append(text);
						} 
		            }
		            StringBuffer html = new StringBuffer();
		            html.append(sb.toString());
		            bean.html = html.toString();
		            mList.add(bean);
		            subCursor.close();
				}
				cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter = new MmsAdapter(MmsTestActivity.this, mList);
			mListView.setAdapter(mAdapter);
			
		}
		
		private Bitmap getImage(String _id) {
			Uri partURI = Uri.parse("content://mms/part/" + _id ); 
	        InputStream is = null; 
	        Bitmap bitmap=null;
	        try { 
	            is = resolver.openInputStream(partURI); 
	            bitmap = BitmapFactory.decodeStream(is);
	        }catch (IOException e) { 
	            e.printStackTrace();  
	        }finally{ 
	            if (is != null){ 
	                try { 
	                    is.close(); 
	                }catch (IOException e){
	                }
	            } 
	        }
	        return bitmap;
		}
	}
}
