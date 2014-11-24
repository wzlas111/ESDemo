package io.vov.vitamio.player;

import io.vov.vitamio.Vitamio;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class InitPlayerService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("=========> service start");
		try {
			new InitAsyncTask().execute("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class InitAsyncTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			System.out.println("=========>InitAsyncTask doInBackground");
			return Vitamio.initialize(InitPlayerService.this, getResources().getIdentifier("libarm", "raw", getPackageName()));
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				System.out.println("=========>InitAsyncTask video init success");
			} else {
				System.out.println("=========>InitAsyncTask video init fail");
			}
		}
		
	}

}
