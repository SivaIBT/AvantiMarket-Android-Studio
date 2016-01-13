package com.byndl.avantimarket.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.byndl.avantimarket.storage.AMPrefKeys;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.fragment.BaseFragment;
import com.byndl.avantimarket.utils.AMConstants;

public class AMApplication extends Application {
	static Context mContext;
	private final String TAG = "AvantiMarket";

	public void onCreate() {
		Log.d(TAG, "AMApplication::onCreate ");
		super.onCreate();
		com.incedo.network.InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);

		mContext = getApplicationContext();
		setFirstLaunchTimeStamp();
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String curVer = pInfo.versionName;
			curVer = curVer.substring(0, curVer.indexOf("("));
			curVer = curVer.trim();
			float currentVersion = Float.parseFloat(curVer);
			float previousVersion = Float.parseFloat(AMPreferenceManager.getPrefInstance().getPrefs().getString("appVersion", "0.0"));
			if(currentVersion > previousVersion) {
				clearInternalStoredContent(false);
			}
			Log.d(TAG, "Current and Previous versions: "+currentVersion+" & "+previousVersion);
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putString("appVersion", curVer);
			editor.commit();
		} catch (Exception e) {
			Log.d(TAG, "Exception while fetching versionName: "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	public static Context getAppContext() {
		return mContext;
	}

	public void clearInternalStoredContent(boolean isSignOut) {
		Log.d(TAG, "clearInternalStoredContent: "+isSignOut);
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();

		if(isSignOut) {
			editor.putString(AMPrefKeys.ENCRYPTED_PASSWORD_KEY, "");
			editor.putString("firstname", "");
			editor.commit();
		}

		BaseFragment.clearStoredCards();

		editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		editor.putStringSet("scanCodes", null);
		editor.commit();
	}

	private void setFirstLaunchTimeStamp() {
		if(AMPreferenceManager.getPrefInstance().getPrefs().getString("FirstLaunchTimeStamp", "").isEmpty()) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSSZ");
			String timeStamp = new String(sdf.format(date));
			Log.d(TAG, "Current TimeStamp: "+timeStamp);
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putString("FirstLaunchTimeStamp", timeStamp);
			editor.commit();
		}
	}

	public static boolean isActivityVisible() {
		return activityVisible;
	}  

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}

	private static boolean activityVisible;
}
