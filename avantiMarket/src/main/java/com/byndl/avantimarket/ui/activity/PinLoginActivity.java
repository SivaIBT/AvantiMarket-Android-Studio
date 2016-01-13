/**
 * @brief       Allows user to login using the PIN
 * @file        PinLoginActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        30-Mar-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.activity;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * 
 * @brief Allows user to login using the PIN
 *
 */
public class PinLoginActivity extends BaseActivity {
	private EditText m_pinDigitOneTextField, m_pinDigitTwoTextField, m_pinDigitFourTextField, m_pinDigitThreeTextField;
	private InputMethodManager inputManager;
	private boolean isBackground = false;;
	private final String TAG = "AvantiMarket";
	private String enteredPin = "", decryptedPassForPinValidation = "", encodedAuthStringForPinValidation = "";
	private int pinLoginTrailCount = 1;
	private AsyncTaskRunnerPostAuthentication runnerPostAuthentication = null;
	private decyptionAsyncTaskRunner runner = null;
	private SecretKey enteredPinHash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.pin_login_screen_layout);
		isBackground = getIntent().getBooleanExtra("background", false);

		setUpListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(m_pinDigitOneTextField != null) {
			m_pinDigitOneTextField.requestFocus();
		}

//		getWindow().setSoftInputMode(
//				   WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		setKeyBoardListener();
		AMApplication.activityResumed();
	}

	private void setUpListeners() {
		Button forgotPin = (Button) findViewById(R.id.m_forgotPinButton);
		forgotPin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context, PinResetActivity.class);
				startActivity(intent);
				finish();
			}
		});
		m_pinDigitOneTextField = (EditText) findViewById(R.id.m_pinDigitOneTextField);
//		m_pinDigitOneTextField.setCallbackHandler(this);
		m_pinDigitTwoTextField = (EditText) findViewById(R.id.m_pinDigitTwoTextField);
//		m_pinDigitTwoTextField.setCallbackHandler(this);
		m_pinDigitThreeTextField = (EditText) findViewById(R.id.m_pinDigitThreeTextField);
//		m_pinDigitThreeTextField.setCallbackHandler(this);
		m_pinDigitFourTextField = (EditText) findViewById(R.id.m_pinDigitFourTextField);
//		m_pinDigitFourTextField.setCallbackHandler(this);

		m_pinDigitOneTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { 

			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { 

			}
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_pinDigitOneTextField);
			}
		});
		m_pinDigitTwoTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { 

			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { 

			}
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_pinDigitTwoTextField);
			}
		});
		m_pinDigitThreeTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_pinDigitThreeTextField);
			}
		});
		m_pinDigitFourTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_pinDigitFourTextField);
			}
		});
	}
	
	/**
	 * Validates the PIN entered
	 * @param field Entered PIN
	 */
	private void AfterTextChanged(EditText field)
	{
		if (field != null)
		{
			Log.d(TAG, "AfterTextChanged()");
			if (field.getText().length() == 1)
			{
				if (field.getId() == m_pinDigitOneTextField.getId())
				{
					m_pinDigitTwoTextField.requestFocus();
				}
				else if (field.getId() == m_pinDigitTwoTextField.getId())
				{
					m_pinDigitThreeTextField.requestFocus();
				}
				else if (field.getId() == m_pinDigitThreeTextField.getId())
				{
					m_pinDigitFourTextField.requestFocus();
				}
				else
				{
					m_pinDigitFourTextField.clearFocus();
					if(this.getCurrentFocus() != null)
						inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

					enteredPin = m_pinDigitOneTextField.getText().toString()+m_pinDigitTwoTextField.getText().toString()+
							m_pinDigitThreeTextField.getText().toString()+m_pinDigitFourTextField.getText().toString();
					validate();
				}
			}
			else if (field.getText().length() > 1)
			{
				String oldText = field.getText().toString();
				field.setText("");
				field.setText(oldText.substring(0, 1));
			}
		}
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
	public void onBackPressed() {
		super.onBackPressed();

		final Intent intent = new Intent(this, StartActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra("isFromPin", true);
		startActivity(intent);
	}

	/**
	 * Clears Entered PIN
	 */
	private void clearPinFields() {
		m_pinDigitOneTextField.setText(""); 
		m_pinDigitTwoTextField.setText("");
		m_pinDigitThreeTextField.setText("");
		m_pinDigitFourTextField.setText("");
		pinLoginTrailCount ++;
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
					inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);				
				}
				return false;
			}
		});
	}

	/**
	 * Callback provided by Custom Edit Field
	 */
	public void backPressedOnEmptyEditField() {
		EditText currentFocusField = (EditText)this.getCurrentFocus();

		if(currentFocusField == m_pinDigitTwoTextField) {
			m_pinDigitOneTextField.requestFocus();
			m_pinDigitOneTextField.moveCursorToVisibleOffset();
		}
		if(currentFocusField == m_pinDigitThreeTextField) {
			m_pinDigitTwoTextField.requestFocus();
			m_pinDigitOneTextField.moveCursorToVisibleOffset();
		}
		if(currentFocusField == m_pinDigitFourTextField) {
			m_pinDigitThreeTextField.requestFocus();
			m_pinDigitOneTextField.moveCursorToVisibleOffset();
		}
	}

	private void validate() {

		if(context.getCurrentFocus() != null)
			inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

		clearPinFields();
		showSigningDialog();
        runner = new decyptionAsyncTaskRunner();
        runner.execute(enteredPin);
	}
	
	@Override
	protected void onDestroy() {
		AMUtility.getMixPanelInstance().flush();
		super.onDestroy();
	}
	
	public void authenticationCallBack(boolean authenticationResult, final String pinAtServer) {
		if(authenticationResult && 
				AMApplication.isActivityVisible()) {
			if(! pinAtServer.isEmpty()) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						encryptPassword(pinAtServer, decryptedPassForPinValidation);
					}
				}).start();
			}
			else {
				dismissSigningDialog();
				showPinModifiedDialog();
				return;
			}

			if(pinAtServer.length() == 4 &&
					! enteredPin.equalsIgnoreCase(pinAtServer)) {
				dismissSigningDialog();
				showPinModifiedDialog();
				return;
			}
			
	        runnerPostAuthentication = new AsyncTaskRunnerPostAuthentication();
	        runnerPostAuthentication.execute(pinAtServer);

		}
		else {
			 if(AMApplication.isActivityVisible()) {
				 showPasswordModifiedDialog();
			 }
			 sendRetryMixpanelEvent();
		}
	}

	protected void showPasswordModifiedDialog(){
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.two_button_dialog);
		dialog.setCancelable(true);

		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText("Please Confirm");
		TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		tvMsg.setText(R.string.confirm_signout);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_Yes);
		btnOk.setText(R.string.ok);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				clearInternalStoredContent(true);
				if(MyAvantiActivity.getInstance() != null) {
					MyAvantiActivity.getInstance().finish();
				}
				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putBoolean("background", false);
				editor.commit();
				final Intent intent = new Intent(getApplicationContext(),
						StartActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
		btnNo.setText(R.string.cancel_capital);
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void showPinModifiedDialog() {

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.two_button_dialog);
		dialog.setCancelable(false);

		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText("Please Confirm");
		TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		tvMsg.setText(R.string.pin_modified);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_Yes);
		btnOk.setText(R.string.relogin);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();

				if(MyAvantiActivity.getInstance() != null) {
					MyAvantiActivity.getInstance().finish();
				}
				clearInternalStoredContent(true);

				final Intent intent = new Intent(getApplicationContext(),
						StartActivity.class);
				startActivity(intent);
				finish();
			}
		});
		btnOk.setVisibility(View.GONE);
		
		Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
		btnNo.setText(R.string.reset_pin);
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				final Intent intent = new Intent(context, PinResetActivity.class);
				startActivity(intent);
				finish();
			}
		});
		btnNo.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
				getResources().getDimensionPixelOffset(R.dimen.regular_btn_height)));
		btnNo.setPadding(getResources().getDimensionPixelOffset(R.dimen.regular_margin), btnNo.getPaddingTop(), 
				getResources().getDimensionPixelOffset(R.dimen.regular_margin), btnNo.getPaddingBottom());
		dialog.show();
	}

	private class decyptionAsyncTaskRunner extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				String reversedPin = new StringBuffer(enteredPin).reverse().toString();
				enteredPinHash = AMUtility.generateKey(enteredPin.toCharArray(), (reversedPin+"avanti").getBytes());
				
				String firstHash = android.util.Base64.encodeToString(enteredPinHash.getEncoded(), android.util.Base64.NO_WRAP);
				
				String reversedfirstHash = new StringBuffer(firstHash).reverse().toString();
				SecretKey pinHashHash = AMUtility.generateKey(firstHash.toCharArray(), (reversedfirstHash+"avanti").getBytes());
				//Code for HashHash --
				
				decryptedPassForPinValidation = AMUtility.decrypt(AMPreferenceManager.getPrefInstance().getEncryptedPasswd(), pinHashHash);
				String authStringForPinValidation = AMPreferenceManager.getPrefInstance().getUsername() + ":" + decryptedPassForPinValidation;
				byte[] authEncBytes = Base64.encodeBase64(authStringForPinValidation.getBytes());
				encodedAuthStringForPinValidation = new String(authEncBytes);
				Log.d(TAG, "Saved Auth and TBD Auth in PinLoginActivity: "+AMPreferenceManager.getPrefInstance().getAuthHeader()+" & "+encodedAuthStringForPinValidation);			
				if(AMPreferenceManager.getPrefInstance().getAuthHeader().contains(encodedAuthStringForPinValidation)) {
					return "true";
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "GenerateKeyException: "+e.getLocalizedMessage());
			}
			return "false";
		}

		@Override
		protected void onPostExecute(String result) {
			if(this.isCancelled()) return;
			
			if(result.contains("true") && AMApplication.isActivityVisible()) {
				apiLogin("Basic" + " " + encodedAuthStringForPinValidation);
			}
			else {
				dismissSigningDialog();
				if(pinLoginTrailCount < 4 && 
						AMApplication.isActivityVisible()) {
					showSingleBtnDialog("Login Failure", "Please check your PIN\n\rand try again.", "OK");
				}
				else {
					if(AMApplication.isActivityVisible()) showPinModifiedDialog();
				}
				sendRetryMixpanelEvent();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {

		}
	}
	
	private class AsyncTaskRunnerPostAuthentication extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String pinAtServer = params[0];
	        if(pinAtServer.length() > 4) {
				try {
					String enteredPinInHexFormat = android.util.Base64.encodeToString(enteredPinHash.getEncoded(), android.util.Base64.NO_WRAP);
					
					if( ! enteredPinInHexFormat.equalsIgnoreCase(pinAtServer)) {
						dismissSigningDialog();
						return "false";
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, "GenerateKeyException: "+e.getLocalizedMessage());
				}
			}
			return "true";
		}

		@Override
		protected void onPostExecute(String result) {
			dismissSigningDialog();
			if(this.isCancelled()) return;
			
			if(result.contains("true")  && 
					AMApplication.isActivityVisible()) {
				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putBoolean("background", false);
				editor.commit();
				if (isBackground) {
					finish();
				} else {
					MixPanelEvents.eventSuccess(null, "Pin Login Success");
					final Intent intent = new Intent(context, MyAvantiActivity.class);
					startActivity(intent);
					MixPanelEvents.startSession();
					finish();
				}
			}
			else {
				if( AMApplication.isActivityVisible()) {
					showPinModifiedDialog();
				}
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {

		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		dismissSigningDialog();
		super.onPause();
		if(runner != null) runner.cancel(true);
		if(runnerPostAuthentication != null) runnerPostAuthentication.cancel(true);
		AMApplication.activityPaused();
	}
	
	private void sendRetryMixpanelEvent() {
//		MixPanelEvents.eventSuccess(null, "Pin Retry: Attempt_"+pinLoginTrailCount);
		try {
			JSONObject properties = new JSONObject();
			properties.put("Error Message", "Pin Mismatch");
			properties.put("Attempt", pinLoginTrailCount-1);
			AMUtility.getMixPanelInstance().track("Pin Retry", properties);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
