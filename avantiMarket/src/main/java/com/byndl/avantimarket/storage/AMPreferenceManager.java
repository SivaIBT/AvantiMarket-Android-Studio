/**
 * @brief       Provides Shared Preferences relevant functionality used across the application
 * @file        AMPreferenceManager.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        11-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.storage;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.byndl.avantimarket.app.AMApplication;

/**
 * @brief       Provides Shared Preferences relevant functionality used across the application
 */
public class AMPreferenceManager {

	private static SharedPreferences prefs = null;
	private static AMPreferenceManager mAMPreferenceManager;

	private AMPreferenceManager() {

	}

	public synchronized static AMPreferenceManager getPrefInstance() {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(AMApplication
					.getAppContext());
		}

		if (mAMPreferenceManager == null) {
			mAMPreferenceManager = new AMPreferenceManager();
		}

		return mAMPreferenceManager;
	}

	public String getUsername() {
		return PreferenceManager.getDefaultSharedPreferences(
				AMApplication.getAppContext()).getString(AMPrefKeys.EMAIL_KEY,
						"");
	}

/*	public String getPasswd() {
		return PreferenceManager.getDefaultSharedPreferences(
				AMApplication.getAppContext()).getString(
						AMPrefKeys.PASSWORD_KEY, "");
	}
*/
	public void setUsername(String userName) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(
				AMApplication.getAppContext()).edit();
		editor.putString(AMPrefKeys.EMAIL_KEY, userName);
		editor.commit();
	}

/*	public void setPasswd(String userPassword) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(
				AMApplication.getAppContext()).edit();
		editor.putString(AMPrefKeys.PASSWORD_KEY, userPassword);
		editor.commit();
		AMUtility.setAuthBase64Value(getUsername(), getPasswd());
	}
*/
	public String getAuthHeader() {
		return prefs.getString(AMPrefKeys.AUTH_BASE64_KEY, "");
	}

	public void setAuthHeader(String authHeader) {
		Editor editor = prefs.edit();
		editor.putString(AMPrefKeys.AUTH_BASE64_KEY, authHeader);
		editor.commit();
	}

	public String getEncryptedPasswd() {
		return prefs.getString(AMPrefKeys.ENCRYPTED_PASSWORD_KEY, "");
	}

	public void setEncryptedPasswd(String encryptedPassword) {
		Editor editor = prefs.edit();
		editor.putString(AMPrefKeys.ENCRYPTED_PASSWORD_KEY, encryptedPassword);
		editor.commit();
	}

/*	private String getJsonFormat() {
		return "json";
	}
*/
	public SharedPreferences getPrefs() {
		return prefs;
	}

/*	public Builder getDefaultParam() {
		Builder builder = new Builder();
		builder.appendQueryParameter("userName", getUsername());
		builder.appendQueryParameter("userPassword", getPasswd());
		builder.appendQueryParameter("format", getJsonFormat());
		return builder;
	}
*/
	}
