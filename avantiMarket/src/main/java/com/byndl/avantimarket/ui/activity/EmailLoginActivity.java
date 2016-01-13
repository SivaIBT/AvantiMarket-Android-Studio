/**
 * @brief       This is to login using the registered Email/Password combo.
 * @file        EmailLoginActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        25-Mar-2015
 * @copyright   incedo inc.
 * 
 */

package com.byndl.avantimarket.ui.activity;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.event.AMLoginDao;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * 
 * @brief This is to login using the registered Email/Password combo.
 * This will be shown to First time users (or) Signed out users
 *
 */
public class EmailLoginActivity extends BaseActivity {
	EditText emailField, passwordField;
	Button forgotPassword, login, notRegistered;
	private final String TAG = "AvantiMarket";
	private InputMethodManager inputManager = null;

	/**
	 * This handler will be passed to Network module for receiving callback	
	 */
	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.login_screen_layout);
		setUpListeners();
	}

	/**
	 * This sets up Listeners to accept and respond to user input
	 */
	private void setUpListeners() {
		emailField = (EditText) findViewById(R.id.m_emailTextField);
		passwordField = (EditText) findViewById(R.id.m_passwordTextField);
		forgotPassword = (Button) findViewById(R.id.m_forgotPasswordButton);
		notRegistered = (Button) findViewById(R.id.m_notRegisteredButton);
		login = (Button) findViewById(R.id.m_loginButton);

		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loginClicked();
			}
		});
		forgotPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context,
						ForgotPasswordActivity.class);
				startActivity(intent);
//				finish();
			}
		});
		notRegistered.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context,
						NotRegisteredActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * Validates Email/Password fields and initiates Authentication request  
	 */
	private void loginClicked() {
		if (emailField.getText().toString().isEmpty())
			showSingleBtnDialog("", "Please enter user credentials.", "OK");
		else if (passwordField.getText().toString().isEmpty())
			showSingleBtnDialog("", "Please enter user credentials.", "OK");
		else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
				emailField.getText().toString().trim()).matches()) {
			showSingleBtnDialog("", "Please enter valid email id.", "OK");
		} else {
			AMPreferenceManager.getPrefInstance().setUsername(
					emailField.getText().toString().trim());

			String authString = emailField.getText().toString().trim() + ":" + passwordField.getText().toString().trim();
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			AMPreferenceManager.getPrefInstance().setAuthHeader("Basic" + " " + new String(authEncBytes));
			apiLogin("");
		}
	}

	/**
	 * Launches UpdateProfileActivity
	 * @param aLoginDao Profile info returned by server
	 */
	public void launchEditProfile(AMLoginDao aLoginDao) {

		final Intent intent = new Intent(context, UpdateProfileActivity.class);
		intent.putExtra("firstname", aLoginDao.mFirstName);
		intent.putExtra("lastname", aLoginDao.mLastName);
		intent.putExtra("phoneNumber", aLoginDao.mPhoneNumber);
		intent.putExtra("contactEmail", aLoginDao.mContactEmail);
		intent.putExtra("pinstatus", aLoginDao.mRequestPin);
		intent.putExtra("password", passwordField.getText().toString().trim());
		intent.putExtra("pin", aLoginDao.mPin);
		startActivity(intent);
		finish();
	}

	/**
	 * Launches MyAvantiActivity
	 * @param aLoginDao Profile info returned by server
	 */
	public void launchMyAvanti(AMLoginDao aLoginDao) {

		if(! aLoginDao.mPin.isEmpty()) {
			encryptPassword(aLoginDao.mPin, passwordField.getText().toString().trim());
		}
		
		final Intent intent = new Intent(context, MyAvantiActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.m_scrollContainer).getBackground().setAlpha(100);
		if (emailField != null) {
			emailField.requestFocus();
		}

		setKeyBoardListener();
		AMApplication.activityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setProgressBarIndeterminateVisibility(false);
		AMApplication.activityPaused();
	}

	/**
	 * Sets up listener to show/hide keyboard as per user event	 
	 */
	private void setKeyBoardListener() {
		final ScrollView activityRootView = (ScrollView)findViewById(R.id.m_scrollContainer);
		activityRootView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP) {
					if(inputManager == null) {
						inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					}
					Log.d(TAG, "KeyBoardListener::Before HIDE");
					inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);				
				}
				return false;
			}
		});
	}
	
    public void setSuperProperties() {
        try {
            
        	if(! AMPreferenceManager.getPrefInstance().getPrefs().getString("FirstLaunchTimeStamp", "").isEmpty() &&
        			! AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("IsInstallTimeRecorded", false)) {
        		final JSONObject properties = new JSONObject();
        		properties.put("Timestamp", AMPreferenceManager.getPrefInstance().getPrefs().getString("FirstLaunchTimeStamp", ""));
        		AMUtility.getMixPanelInstance().track("Application Installed", properties);                	
        		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
        		editor.putBoolean("IsInstallTimeRecorded", true);
        		editor.commit();
        	}
        	
            final JSONObject properties = new JSONObject();
            properties.put("AMS ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", ""));
            properties.put("Operator ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", ""));
            properties.put("Location ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("locationId", ""));
            properties.put("Device Type", "Android "+getResources().getString(R.string.screen_type));
            properties.put("Device Class", getResources().getString(R.string.screen_type));
            AMUtility.getMixPanelInstance().registerSuperProperties(properties);

            MixPanelEvents.startSession();
        } catch (final JSONException e) {
            throw new RuntimeException("Could not encode hour first viewed as JSON");
        }
    }
    
    @Override
    protected void onDestroy() {
    	AMUtility.getMixPanelInstance().flush();
    	super.onDestroy();
    }
}
