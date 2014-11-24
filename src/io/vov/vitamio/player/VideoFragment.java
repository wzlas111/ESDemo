package io.vov.vitamio.player;

import org.json.JSONObject;

import com.eastelsoft.demo.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.http.HttpMethod;
import io.vov.vitamio.http.HttpUtility;
import io.vov.vitamio.widget.VideoView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VideoFragment extends Fragment implements OnClickListener{
	
	private String mUrl = "";
	private String mVideoUrl = "";
	
	private boolean mDragging = false;
	private boolean isShowing = false;
	private long mDuration;
	private long mPosition = -1l;
	private int mOrientation = 0;//0:port;1:land
	
	private VideoView mVideoView;
	private Animation mAnimation;
	
	private View mController;
	private View mControllerTop;
	private View mControllerBottom;
	private ImageView mStartImg;
	private ImageButton mFullScreenBtn;
	private ImageButton mPlayPause;
	private Button mBackBtn;
	private SeekBar mSeekBar;
	private TextView mCurrTime;
	private TextView mTotalTime;
	private View mLoadingLayout;
	private ImageView mLoading;
	private TextView mLoadingPercent;
	
	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.player_fragment_videoplay, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mVideoView = (VideoView)view.findViewById(R.id.video_play_videoview);
		mStartImg = (ImageView)view.findViewById(R.id.video_play_start_img);
		mStartImg.setImageResource(R.drawable.detail_play_button);
		mStartImg.setOnClickListener(this);
		mController = view.findViewById(R.id.video_play_controller);
		mControllerTop = view.findViewById(R.id.mediacontroller_top);
		mControllerBottom = view.findViewById(R.id.mediacontroller_bottom);
		mFullScreenBtn = (ImageButton)view.findViewById(R.id.mediacontroller_fullscreen);
		mFullScreenBtn.setOnClickListener(this);
		mBackBtn = (Button)view.findViewById(R.id.mediacontroller_top_back);
		mBackBtn.setOnClickListener(this);
		mSeekBar = (SeekBar)view.findViewById(R.id.mediacontroller_seekbar);
		mCurrTime = (TextView)view.findViewById(R.id.mediacontroller_time_current);
		mTotalTime = (TextView)view.findViewById(R.id.mediacontroller_time_total);
		mPlayPause = (ImageButton)view.findViewById(R.id.mediacontroller_paly_pause);
		mPlayPause.setOnClickListener(this);
		mLoadingLayout = view.findViewById(R.id.loding_layout);
		mLoading = (ImageView)view.findViewById(R.id.loading);
		mLoadingPercent = (TextView)view.findViewById(R.id.loading_percent);
		mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.player_controller_progress);
		mLoading.setAnimation(mAnimation);
		
		setListener();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mVideoView.seekTo(mPosition);
		mPosition = -1l;
	}
	
	@Override
	public void onStop() {
		if (mVideoView.isPlaying()) {
			mVideoView.pause();
		}
		mPosition = (mDuration * mSeekBar.getProgress()) / 1000l;
		super.onStop();
	}
	
	private void setListener() {
		mVideoView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (isShowing) {
						hide();
					} else {
						show(5000);
					}
					break;
				}
				return true;
			}
		});
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				mHandler.sendEmptyMessageDelayed(0, 1000l);
			}
		});
		mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				System.out.println("buffering : percent -> "+percent);
				mLoadingPercent.setText(percent+"% 加载中");
				if (percent == 100) {
					hideLoading();
				}
			}
		});
		mVideoView.setOnInfoListener(new OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				switch (what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					showLoading();
					if (mp.isPlaying()) {
						mp.pause();
					}
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					mp.start();
					hideLoading();
					break;
				}
				return true;
			}
		});
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mStartImg.setVisibility(View.VISIBLE);
				mHandler.sendEmptyMessage(2);
			}
		});
		mVideoView.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mStartImg.setVisibility(View.VISIBLE);
				hideLoading();
				Toast.makeText(getActivity(), "sorry,这个视频暂时不能播放", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				long progress = seekBar.getProgress();
				long pos = mDuration * progress / 1000l;
				mVideoView.seekTo(pos);
				
				show(5000);
				mHandler.removeMessages(1);
				mDragging = false;
				mHandler.sendEmptyMessageDelayed(1, 1000l);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mDragging = true;
				show(60 *60 *1000);
				mHandler.removeMessages(1);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				if (!fromUser) {
					return;
				}
				long pos = mDuration * progress / 1000l;
				String currTime = generateTime(pos);
//				mVideoView.seekTo(pos);
				mCurrTime.setText(currTime);
			}
		});
	}
	
	private void show(int delayMillis) {
		if (!isShowing) {
			mController.setVisibility(View.VISIBLE);
		}
		mHandler.sendEmptyMessage(1);
		mHandler.removeMessages(2);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(2), delayMillis);
		isShowing = true;
	}
	
	private void hide() {
		if (isShowing) {
			mHandler.removeMessages(1);
			mController.setVisibility(View.GONE);
		}
		isShowing = false;
	}
	
	private void startPlay() {
		showLoading();
		mVideoView.setBufferSize(2 * 1024 * 1204);
		mVideoView.setVideoPath(mVideoUrl);
		mVideoView.requestFocus();
		mStartImg.setVisibility(View.GONE);
	}
	
	private void showLoading() {
		mLoadingLayout.setVisibility(View.VISIBLE);
		mAnimation.start();
	}
	
	private void hideLoading() {
		mLoadingLayout.setVisibility(View.GONE);
		mAnimation.cancel();
	}
	
	private void playAndPause() {
		if (mVideoView.isPlaying()) {
			mVideoView.pause();
			mPlayPause.setImageResource(R.drawable.detail_play_button);
		} else {
			mVideoView.start();
			mPlayPause.setImageResource(R.drawable.detail_pause_button);
		}
	}
	
	public void setVideoScale() {
		if (mVideoView != null) {
			mVideoView.setVideoLayout(1, 0.0f);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_play_start_img:
//			startPlay();
			new UrlAsyncTask().execute("");
			break;
		case R.id.mediacontroller_paly_pause:
			playAndPause();
			break;
		case R.id.mediacontroller_fullscreen:
			if (mOrientation == 0) {
				getActivity().setRequestedOrientation(0);
				mFullScreenBtn.setImageResource(R.drawable.detail_play_smallscreen);
				mControllerTop.setVisibility(View.VISIBLE);
				mOrientation = 1;
			} else {
				getActivity().setRequestedOrientation(1);
				mFullScreenBtn.setImageResource(R.drawable.detail_play_fullscreen);
				mControllerTop.setVisibility(View.GONE);
				mOrientation = 0;
			}
			break;
		case R.id.mediacontroller_top_back:
			getActivity().setRequestedOrientation(1);
			mFullScreenBtn.setImageResource(R.drawable.detail_play_fullscreen);
			mControllerTop.setVisibility(View.GONE);
			mOrientation = 0;
			break;
		default:
			break;
		}
	}
	
	private long updateProgress() {
		long curr = mVideoView.getCurrentPosition();
		long total = mVideoView.getDuration();
		mDuration = total;
		if (total > 0l) {
			long pos = 1000l * curr / total;
			mSeekBar.setProgress((int)pos);
		}
		mTotalTime.setText("/"+generateTime(total));
		mCurrTime.setText(generateTime(curr));
		return curr;
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				show(5000);
				break;
			case 1:
				long curr = updateProgress();
				if (!isShowing || mDragging) {
					return;
				}
				sendMessageDelayed(obtainMessage(1), 1000l-curr%1000l);
				break;
			case 2:
				hide();
				break;
			}
		}
	};
	
	private String generateTime(long time) {
		int i = (int)(time / 1000L);
	    int s = i % 60;
	    int m = i / 60 % 60;
	    int h = i / 3600;
	    if (h > 0)
	    {
	      Object[] changeTime = new Object[3];
	      changeTime[0] = Integer.valueOf(h);
	      changeTime[1] = Integer.valueOf(m);
	      changeTime[2] = Integer.valueOf(s);
	      return String.format("%02d:%02d:%02d", changeTime);
	    }
	    Object[] changeTime = new Object[2];
	    changeTime[0] = Integer.valueOf(m);
	    changeTime[1] = Integer.valueOf(s);
	    return String.format("%02d:%02d", changeTime);
	}
	
	private class UrlAsyncTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				System.out.println("murl : "+mUrl);
				String html = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, mUrl, null);
				System.out.println("return : "+html);
				html = Html.fromHtml(html).toString().substring(3);
				JSONObject obj = new JSONObject(html);
				System.out.println("rstp_url : "+obj.getString("rstp_url"));
				mVideoUrl = obj.getString("rstp_url");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			startPlay();
		}
		
	}
}
