package com.byndl.avantimarket.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.util.Log;

import com.byndl.avantimarket.event.AMArticleListDao;
import com.byndl.avantimarket.event.AMCaseConsumerIssuesDao;
import com.byndl.avantimarket.event.AMCaseCreateFeedbackDao;
import com.byndl.avantimarket.event.AMChargeStoredCardDao;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.incedo.network.neterror.ApiErrorObj;

public class MixPanelEvents {

	public static void eventFailed(Message msg, String eventDetail) {
        try {
    		final JSONObject properties = new JSONObject();
    		
    		int errorCode = ((ApiErrorObj) msg.obj).getErrorCode();
    		if(errorCode != -1) {
    			properties.put("Error Code", ((ApiErrorObj) msg.obj).getErrorCode());
    		}
    		
			String errorMessage = ((ApiErrorObj) msg.obj).getErrorMsg(); 
			if(errorMessage.contains("Unauthorized") || errorMessage.contains("Network")) {
				properties.put("Error Type", ((ApiErrorObj) msg.obj).getErrorMsg());
			}
			else {
				properties.put("Error Type", "Server Error");
			}
			
			properties.put("Timestamp", getTimeStamp());
			AMUtility.getMixPanelInstance().track(eventDetail, properties);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void eventSuccess(Message msg, String eventDetail) {
        try {
    		JSONObject properties = new JSONObject();
    		if(msg != null) {
    			properties = addCaseCreationProperties(msg, properties);
    		}
    		if(eventDetail.contains("Search")) {
    			properties = addSearchProperties(msg, properties);
    		}
    		if(eventDetail.contains("Reload")) {
    			properties = addReloadProperties(msg, properties);
    		}
			properties.put("Timestamp", getTimeStamp());
			AMUtility.getMixPanelInstance().track(eventDetail, properties);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pageView(String pageDetail) {
        try {
    		final JSONObject properties = new JSONObject();
    		properties.put("Page Name", pageDetail);
			properties.put("Timestamp", getTimeStamp());
			AMUtility.getMixPanelInstance().track("Page View", properties);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSSZ");
		return sdf.format(date);
	}
	
	private static JSONObject addCaseCreationProperties(Message msg, JSONObject properties) {
		try {
			if(msg.obj.toString().contains("ConsumerIssues")) {
				properties.put("Category Type", ((AMCaseConsumerIssuesDao) msg.obj).mCategory);
				properties.put("Issue Type", ((AMCaseConsumerIssuesDao) msg.obj).mIssue);
			}
			else if(msg.obj.toString().contains("Feedback")) {
				properties.put("Category Type", ((AMCaseCreateFeedbackDao) msg.obj).mCategory);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}

	private static JSONObject addSearchProperties(Message msg, JSONObject properties) {
		try {
			properties.put("Search Term", ((AMArticleListDao) msg.obj).mSearchKeyword);
			properties.put("Section", ((AMArticleListDao) msg.obj).mListType.replace("HowTo", ""));
//			properties.put("SearchResults", ((AMArticleListDao) msg.obj).getArticleArray());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}

	private static JSONObject addReloadProperties(Message msg, JSONObject properties) {
		try {
			properties.put("Reload Amount", ((AMChargeStoredCardDao) msg.obj).mReloadAmnt);
			if(((AMChargeStoredCardDao) msg.obj).mIsAuto) {
				properties.put("Reload Type", "Auto Reload");
			}
			else {
				properties.put("Reload Type", "One Time Relaod");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
	
	public static void startSession() {
    	if(AMPreferenceManager.getPrefInstance().getPrefs().getString("session", "").isEmpty()) {
    		String session = AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", "");
        	// Start the session
    		Log.d("Session Start: ", "SessionID: "+session);
    		AMUtility.getMixPanelInstance().timeEvent("Session: "+session);
    		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
    		editor.putString("session", session);
    		editor.commit();
    	}
	}
}
