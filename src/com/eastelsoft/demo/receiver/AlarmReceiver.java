package com.eastelsoft.demo.receiver;

import com.eastelsoft.demo.service.AlarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(Intent.ACTION_RUN);
		i.setClass(context, AlarmService.class);
		context.startService(i);
	}

}
