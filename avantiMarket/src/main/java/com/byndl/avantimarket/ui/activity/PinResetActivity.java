/**
 * @brief       In case of "Forgot PIN", allows user to Reset PIN
 * @file        PinResetActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        31-Mar-2015
 * @copyright   incedo inc.
 * 
 */

package com.byndl.avantimarket.ui.activity;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMUtility;

/**
 * 
 * @brief In case of "Forgot PIN", allows user to Reset PIN
 *
 */
public class PinResetActivity extends BaseActivity {

	private final String TAG = "AvantiMarket";
	private EditText m_pinDigitOneTextField, m_pinDigitTwoTextField, m_pinDigitThreeTextField, m_pinDigitFourTextField,
						m_rePinDigitOneTextField, m_rePinDigitTwoTextField, m_rePinDigitThreeTextField, m_rePinDigitFourTextField;
	private LinearLayout enterPinLayout, reEnterPinLayout;
	private InputMethodManager inputManager;
	private String enteredPin="", reEnteredPin="";
	private EditText emailField, passwordField;
	private Button saveButton, cancelButton, forgotPassword;
	private SaveNewPinAsyncTask worker = null;
	private String firstHash = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 	
		setContentView(R.layout.pin_reset_layout);
		enterPinLayout = (LinearLayout)findViewById(R.id.pin_fields_container);
		reEnterPinLayout = (LinearLayout)findViewById(R.id.re_pin_fields_container);
		setTextChangeListenerForEditFields();

		emailField = (EditText)findViewById(R.id.m_emailTextField);
		passwordField = (EditText)findViewById(R.id.m_passwordTextField);

		setUpListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (emailField != null) {
			emailField.requestFocus();
		}
	}

	/**
	 * Sets up listeners to accept and respond to user input
	 */
	private void setUpListeners() {
		saveButton = (Button)findViewById(R.id.m_loginButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("AVANTI","Enterd and ReEntered are:"+enteredPin+" "+reEnteredPin);
				if(!enteredPin.isEmpty() && (enteredPin.length()==4) && enteredPin.equalsIgnoreCase(reEnteredPin)) {
					if(validateEmailPassword(emailField.getText().toString(), passwordField.getText().toString())) {
						worker = new SaveNewPinAsyncTask();
						worker.execute(enteredPin);
						showLoadingDialog();
//						saveNewPin(enteredPin);
					}
					else
						showSingleBtnDialog(
								getResources().getString(R.string.unauthorized),
								getResources().getString(R.string.check_credentials), null);
				}
				else {
					clearPinFields();

					if(emailField.getText().toString().isEmpty())
						showSingleBtnDialog("", "Please enter valid email id.", "OK");
					else if(passwordField.getText().toString().isEmpty())
						showSingleBtnDialog("", "Password should not be blank.", "OK");
					else if (enteredPin.isEmpty()) {
						showSingleBtnDialog("Reset Failed", "Please enter new PIN.", "OK");
					}
					else if(reEnteredPin.isEmpty()) {
						showSingleBtnDialog("Reset Failed", "Please retype new PIN.", "OK");
					}
					else if (enteredPin.length() != 4) {
						showSingleBtnDialog("Reset Failed", "PIN must be 4 digits.", "OK");
					}
					else if( ! enteredPin.equals(reEnteredPin)) {
						showSingleBtnDialog("Reset Failed", "The PIN's you entered don't match.", "OK");
					}
					enteredPin = "";
					reEnteredPin = "";
				}
			}
		});

		cancelButton = (Button)findViewById(R.id.m_cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context, PinLoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		forgotPassword = (Button) findViewById(R.id.m_forgot_password);
		forgotPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context,
						ForgotPasswordActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Acknowledges user that the PIN reset is successful
	 */
	protected void showSavedDialog() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				encryptPassword(firstHash, passwordField.getText().toString());
			}
		}).start();
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comingsoon_dialog);
		dialog.setCancelable(false);

		TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_comingsoon_txtMsg);
		tvMsg.setText(getResources().getString(R.string.pin_changed));

		Button btnOk = (Button) dialog.findViewById(R.id.btn_cominsoon_Yes);
		btnOk.setText(getResources().getString(R.string.ok));
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				final Intent intent = new Intent(context, MyAvantiActivity.class);
				startActivity(intent);
				finish();
			}
		});
		dialog.show();
	}

	/**
	 * Validates the PIN entered
	 * @param field Entered PIN
	 */
	private void AfterTextChanged(EditText field)
	{
		if (field != null)
		{
			Log.d("AVANTI", "AfterTextChanged()");
			if (field.getText().length() == 1)
			{
				if (field.getId() == m_pinDigitOneTextField.getId())
				{
					m_pinDigitTwoTextField.requestFocus();
				}
				else if (field.getId() == m_pinDigitTwoTextField.getId())
				{
					enteredPin = m_pinDigitOneTextField.getText().toString()+m_pinDigitTwoTextField.getText().toString();

					m_pinDigitThreeTextField.requestFocus();
				}
				else if (field.getId() == m_pinDigitThreeTextField.getId())
				{
					enteredPin = m_pinDigitOneTextField.getText().toString()+m_pinDigitTwoTextField.getText().toString()+
							m_pinDigitThreeTextField.getText().toString();

					m_pinDigitFourTextField.requestFocus();
				}
				else if (field.getId() == m_pinDigitFourTextField.getId())
				{
					enteredPin = m_pinDigitOneTextField.getText().toString()+m_pinDigitTwoTextField.getText().toString()+
							m_pinDigitThreeTextField.getText().toString()+m_pinDigitFourTextField.getText().toString();

					m_rePinDigitOneTextField.requestFocus();
				}
				else if (field.getId() == m_rePinDigitOneTextField.getId())
				{
					m_rePinDigitTwoTextField.requestFocus();
				}
				else if (field.getId() == m_rePinDigitTwoTextField.getId())
				{
					reEnteredPin = m_rePinDigitOneTextField.getText().toString()+m_rePinDigitTwoTextField.getText().toString();
					m_rePinDigitThreeTextField.requestFocus();
				}
				else if (field.getId() == m_rePinDigitThreeTextField.getId())
				{
					reEnteredPin = m_rePinDigitOneTextField.getText().toString()+m_rePinDigitTwoTextField.getText().toString()+
							m_rePinDigitThreeTextField.getText().toString();
					m_rePinDigitFourTextField.requestFocus();
				}
				else
				{
					m_rePinDigitFourTextField.clearFocus();

					reEnteredPin = m_rePinDigitOneTextField.getText().toString()+m_rePinDigitTwoTextField.getText().toString()+
							m_rePinDigitThreeTextField.getText().toString()+m_rePinDigitFourTextField.getText().toString();
					if(this.getCurrentFocus() != null)
						inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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

	/**
	 * Sets up Listeners to accept and respond to user entered PIN
	 */
	private void setTextChangeListenerForEditFields() {
		m_pinDigitOneTextField = (EditText) enterPinLayout.findViewById(R.id.m_pinDigitOneTextField);
		m_pinDigitTwoTextField = (EditText) enterPinLayout.findViewById(R.id.m_pinDigitTwoTextField);
		m_pinDigitThreeTextField = (EditText) enterPinLayout.findViewById(R.id.m_pinDigitThreeTextField);
		m_pinDigitFourTextField = (EditText) enterPinLayout.findViewById(R.id.m_pinDigitFourTextField);
		m_rePinDigitOneTextField = (EditText) reEnterPinLayout.findViewById(R.id.m_re_pinDigitOneTextField);
		m_rePinDigitTwoTextField = (EditText) reEnterPinLayout.findViewById(R.id.m_re_pinDigitTwoTextField);
		m_rePinDigitThreeTextField = (EditText) reEnterPinLayout.findViewById(R.id.m_re_pinDigitThreeTextField);
		m_rePinDigitFourTextField = (EditText) reEnterPinLayout.findViewById(R.id.m_re_pinDigitFourTextField);
		m_pinDigitOneTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_pinDigitOneTextField);
			}
		});
		m_pinDigitTwoTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
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
		m_rePinDigitOneTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_rePinDigitOneTextField);
			}
		});
		m_rePinDigitTwoTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_rePinDigitTwoTextField);
			}
		});
		m_rePinDigitThreeTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_rePinDigitThreeTextField);
			}
		});
		m_rePinDigitFourTextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(m_rePinDigitFourTextField);
			}
		});
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

	/**
	 * Clears Entered PIN
	 */
	private void clearPinFields() {
		m_pinDigitOneTextField.setText(""); 
		m_pinDigitTwoTextField.setText("");
		m_pinDigitThreeTextField.setText("");
		m_pinDigitFourTextField.setText("");
		m_rePinDigitOneTextField.setText("");
		m_rePinDigitTwoTextField.setText("");
		m_rePinDigitThreeTextField.setText("");
		m_rePinDigitFourTextField.setText("");
	}

	private boolean validateEmailPassword(String email, String password) {
		String authString = email + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		Log.d(TAG, "Saved Auth and TBD Auth in PinResetActivity: "+AMPreferenceManager.getPrefInstance().getAuthHeader()+" & "+new String(authEncBytes));			
		if(AMPreferenceManager.getPrefInstance().getAuthHeader().contains(new String(authEncBytes))) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		final Intent intent = new Intent(context, PinLoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	private class SaveNewPinAsyncTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String enteredPin = params[0];
			try {
				SecretKey pinHash;
				String reversedPin = new StringBuffer(enteredPin).reverse().toString();
				pinHash = AMUtility.generateKey(enteredPin.toCharArray(), (reversedPin+"avanti").getBytes());

				firstHash = android.util.Base64.encodeToString(pinHash.getEncoded(), android.util.Base64.NO_WRAP);
				return "true";
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "GenerateKeyException: "+e.getLocalizedMessage());
			}
			return "false";
		}

		@Override
		protected void onPostExecute(String result) {
			if(this.isCancelled()) 
			{
				dismissLoadingDialog();
				return;
			}
			if(result.contains("true")) {
				apiSetUserPin(firstHash, false);
			}
			else {
				dismissLoadingDialog();
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
		if(worker != null) worker.cancel(true);
		super.onPause();
	}
}
