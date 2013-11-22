package com.wilddynamos.bookapp.ws.remote;

import com.wilddynamos.bookapp.ws.remote.action.WaitingRequest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationCenter extends Service {
	
	@Override
	public void onCreate() {
		System.out.println("Create");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 new WaitingRequest(this).start();  
		 System.out.println("Service Started");
		 return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
