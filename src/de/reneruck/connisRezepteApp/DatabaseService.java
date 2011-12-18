package de.reneruck.connisRezepteApp;

import android.content.Intent;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

public class DatabaseService extends OrmLiteBaseService<DBManager> {

	
	
	@Override
	public void onCreate() {

		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
