package main.dipalza;

import android.app.Application;
import android.content.Context;

public class DipalzaApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		DipalzaApplication.context = getApplicationContext();
	}
	
	
    public static Context getAppContext() {
        return DipalzaApplication.context;
    }
}
