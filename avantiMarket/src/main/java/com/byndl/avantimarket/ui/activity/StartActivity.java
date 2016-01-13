/**
 * @brief       This is the Launcher class. From here, EmailLoginActivity or PinLoginActivity will be launched
 * @file        StartActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        24-Mar-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.byndl.avantimarket.storage.AMPrefKeys;
import com.byndl.avantimarket.storage.AMPreferenceManager;

/**
 * @brief	This is the Launcher Activity. 
 * From here, EmailLoginActivity or PinLoginActivity will be launched
 */
public class StartActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		if (AMPreferenceManager.getPrefInstance().getPrefs().getString(AMPrefKeys.ENCRYPTED_PASSWORD_KEY, "").isEmpty()) {
			final Intent intent = new Intent(this, EmailLoginActivity.class);
			startActivity(intent);
		} else {
			Intent mIntent = getIntent();
			if(mIntent != null && mIntent.getBooleanExtra("isFromPin", false)) {
				finish();
			} else {
				if(AMPreferenceManager.getPrefInstance().getPrefs().getBoolean(AMPrefKeys.PASSWORD_LOCK, true)) {
					final Intent intent = new Intent(this, PinLoginActivity.class);
					startActivity(intent);
					finish();
				}
				else {
					final Intent intent = new Intent(this, MyAvantiActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}
		finish();
	}
}
