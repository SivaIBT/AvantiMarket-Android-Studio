/**
 * @brief		During first time login, after successful Authentication, this class allows users to set PIN for subsequent logins       
 * @file        UpdateProfileActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        27-Mar-2015
 * @copyright   incedo inc.
 * 
 */

package com.byndl.avantimarket.ui.activity;

import javax.crypto.SecretKey;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMUtility;

/**
 * 
 * @brief During first time login, after successful Authentication, this class allows users to set PIN for subsequent logins
 *
 */
public class UpdateProfileActivity extends BaseActivity {

	private EditText m_pinDigitOneTextField, m_pinDigitTwoTextField, m_pinDigitThreeTextField, m_pinDigitFourTextField,
	m_rePinDigitOneTextField, m_rePinDigitTwoTextField, m_rePinDigitThreeTextField, m_rePinDigitFourTextField;
	private LinearLayout enterPinLayout, reEnterPinLayout, enterPinRow, reEnterPinRow;
	private Button blankButton;
	private String enteredPin="", reEnteredPin="";
	private InputMethodManager inputManager;
	private String mFName = "";
	private String mLName = "";
	private String mPhNo = "";
	private String mEmail = "";

	private TextView registeredEmail = null;
	private EditText contactEmail = null;
	private EditText fNameTextView = null;
	private EditText lNameTextView = null;
	private EditText phNoTextView = null;
	private Dialog savedDialog = null;
	private boolean visibility = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 	
		setContentView(R.layout.update_profile_layout);

		getExtraValues(getIntent());
		setUpListeners();
		setField();
		enterPinLayout = (LinearLayout)findViewById(R.id.pin_fields_container);
		reEnterPinLayout = (LinearLayout)findViewById(R.id.re_pin_fields_container);
		
		enterPinRow = (LinearLayout)findViewById(R.id.m_pinTextFieldsContainer);
		reEnterPinRow = (LinearLayout)findViewById(R.id.m_re_pinTextFieldsContainer);
		blankButton = (Button) findViewById(R.id.m_blank_Button);
		setTextChangeListenerForEditFields();
	}

	/**
	 * Retrieves the profile info forwarded by previous activity
	 * @param intent Intent that started this activity; this holds the info forwarded by previous activity 
	 */
	private void getExtraValues(Intent intent) {
		mEmail = intent.getStringExtra("contactEmail");
		mFName = intent.getStringExtra("firstname");
		mLName = intent.getStringExtra("lastname");
		mPhNo = intent.getStringExtra("phoneNumber");

	}

	/**
	 * Updates the UI fields with relevant info 
	 */
	private void setField() {
		if (contactEmail != null && mEmail != null && 
				! mEmail.equalsIgnoreCase(AMPreferenceManager.getPrefInstance().getUsername())) {
			contactEmail.setText(mEmail);
		}
		if (fNameTextView != null && mFName != null) {
			fNameTextView.setText(mFName);
		}
		if (lNameTextView != null && mLName != null) {
			lNameTextView.setText(mLName);
		}
		if (phNoTextView != null && (! mPhNo.isEmpty())
				&& (! mPhNo.equalsIgnoreCase("false"))) {
			phNoTextView.setText(mPhNo);
			mPhNo = phNoTextView.getText().toString().trim();
		}
	}

	/**
	 * This sets up Listeners to accept and respond to user input
	 */

	private void setUpListeners() {

		registeredEmail = (TextView) findViewById(R.id.m_registeredEmailLabel);
		registeredEmail.setText(AMPreferenceManager.getPrefInstance().getUsername());
		
		contactEmail = (EditText) findViewById(R.id.m_contactEmailLabel);
		contactEmail.setHint(R.string.contact_email);
		
		fNameTextView = (EditText) findViewById(R.id.m_firstnameLabel);
		fNameTextView.setHint(R.string.first_name);
		fNameTextView.setInputType(InputType.TYPE_CLASS_TEXT);

		lNameTextView = (EditText) findViewById(R.id.m_lastnameLabel);
		lNameTextView.setHint(R.string.last_name);
		lNameTextView.setInputType(InputType.TYPE_CLASS_TEXT);

		phNoTextView = (EditText) findViewById(R.id.m_phoneLabel);
		phNoTextView.setHint(R.string.phone);
		phNoTextView.setInputType(InputType.TYPE_CLASS_PHONE);
		phNoTextView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		phNoTextView.requestFocus();

		Button save = (Button)findViewById(R.id.m_saveButton);
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("AVANTI","Enterd and ReEntered are:"+enteredPin+" "+reEnteredPin);
				
				if(fNameTextView.getText().toString().trim().isEmpty()) {
					showSingleBtnDialog("", getResources().getString(R.string.first_name_empty), "OK");
					return;
				}

				if(lNameTextView.getText().toString().trim().isEmpty()) {
					showSingleBtnDialog("", getResources().getString(R.string.last_name_empty), "OK");
					return;
				}

				if(getIntent().getBooleanExtra("pinstatus", true) &&
						!enteredPin.isEmpty() && 
						(enteredPin.length()==4) && 
						enteredPin.equalsIgnoreCase(reEnteredPin)) {
					if( ! phNoTextView.getText().toString().isEmpty() && 
							(phNoTextView.getText().toString().length() >= 10)) {
						if (! android.util.Patterns.PHONE.matcher(phNoTextView.getText().toString().trim()).matches()) {
							showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
							return;
						}
						else {
							String readyToDial = PhoneNumberUtils.stripSeparators(phNoTextView.getText().toString().trim());
							if(readyToDial.replace("0", "").length() == 0 ||
									! PhoneNumberUtils.isGlobalPhoneNumber(readyToDial) ||
									readyToDial.length() < 10) {
								showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
								return;
							}
						}
					}
					else {
						if(phNoTextView.getText().toString().trim().isEmpty()) {
//							showSingleBtnDialog("Update Failed", "Phone number is required", "OK");
						}
						else {
							showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
							return;
						}
					}

//					showSavedDialog();
					String firstName = "", lastName = "", email = "", phoneNumber = "";
					Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();

					if(! mFName.equals(fNameTextView.getText().toString().trim()) &&
							! fNameTextView.getText().toString().trim().isEmpty()) {
						firstName = fNameTextView.getText().toString().trim();
						editor.putString("firstname", firstName);
						mFName = firstName;
					}
					
					if(! mLName.equals(lNameTextView.getText().toString().trim()) &&
							! lNameTextView.getText().toString().trim().isEmpty()) {
						lastName = lNameTextView.getText().toString().trim();
						editor.putString("lastname", lastName);
					}
					
					if(! mEmail.equals(contactEmail.getText().toString().trim())) {
						email = contactEmail.getText().toString().trim();
						if ( ! android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
							if( ! email.isEmpty()) {
								showSingleBtnDialog("", getString(R.string.valid_email), "OK");
								return;
							}
							else {
								editor.putString("contactEmail", email);
							}
						} 
						else {
							editor.putString("contactEmail", email);
						}
					}
					
					if(! mPhNo.equals(phNoTextView.getText().toString().trim())) {
						phoneNumber = PhoneNumberUtils.stripSeparators(phNoTextView.getText().toString().trim());
						editor.putString("phoneNumber", phoneNumber);
					}
					
			        showLoadingDialog();
			        AsyncTaskRunner runner = new AsyncTaskRunner();
			        runner.execute(enteredPin);
			        
					if(isInternetAvailable()) {
//						editor.commit();
					}
					else {
						return;
					}

					Log.d(TAG, "Before Updation: "+firstName+" "+lastName+" "+email+" "+phoneNumber);
					
//					if((firstName+lastName+email+phoneNumber).length() > 0) 
					{ 
						apiUpdateProfile(firstName, lastName, email, phoneNumber);
					}
				}
				else if(! getIntent().getBooleanExtra("pinstatus", true)) {
					if(! getIntent().getStringExtra("pin").isEmpty()) {
						encryptPassword(getIntent().getStringExtra("pin"), getIntent().getStringExtra("password"));
					}
					excludePinAndValidateOtherFields();
				}
				else {
					clearPinFields();
					m_pinDigitOneTextField.requestFocus();
					if (enteredPin.isEmpty()) {
						showSingleBtnDialog("Update Failed", "Please enter new PIN.", "OK");
					}
					else if(reEnteredPin.isEmpty()) {
						showSingleBtnDialog("Update Failed", "Please retype new PIN.", "OK");
					}
					else if (enteredPin.length() != 4) {
						showSingleBtnDialog("Update Failed", "PIN must be 4 digits.", "OK");
					}
					else if( ! enteredPin.equals(reEnteredPin)) {
						showSingleBtnDialog("Update Failed", "PIN fields don't match, please try again.", "OK");
					}
					
					enteredPin = "";
					reEnteredPin = "";
				}
			}
		});

		Button cancel = (Button)findViewById(R.id.m_cancel_Button);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showCancelConfirmationDialog();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		final Intent intent = new Intent(context, EmailLoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Acknowledges the user that the PIN is saved
	 */
	protected void showSavedDialog() {
		String infoSaved = getResources().getString(R.string.info_saved);
		if(mFName != null && !mFName.isEmpty()) {
			infoSaved = infoSaved.replaceFirst("!", " "+mFName+",");
		}
		
		if(savedDialog != null &&
				savedDialog.isShowing()) return;
		
		savedDialog = new Dialog(this);
		savedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		savedDialog.setContentView(R.layout.custom_dailog_two_btn);
		savedDialog.setCancelable(false);

		TextView tvTitle = (TextView) savedDialog.findViewById(R.id.txtTitle);
		tvTitle.setText(R.string.update_success);
		TextView tvMsg = (TextView) savedDialog.findViewById(R.id.txtMsg);
		tvMsg.setText(infoSaved);

		Button btnOk = (Button) savedDialog.findViewById(R.id.btnYes);
		btnOk.setText(getResources().getString(R.string.ok));
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				savedDialog.dismiss();
				final Intent intent = new Intent(context, MyAvantiActivity.class);
				startActivity(intent);
				finish();
			}
		});
		if(visibility) savedDialog.show();
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
	 * Sets up Listeners to accept/respond to User input in PIN fields
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

	@Override
	protected void onResume() {
		super.onResume();
		setKeyBoardListener();
		if(mPhNo.isEmpty()) {
			phNoTextView.requestFocus();
		}
		else {
			m_pinDigitOneTextField.requestFocus();
		}
		
		if( ! getIntent().getBooleanExtra("pinstatus", true)) {
			enterPinRow.setVisibility(View.GONE);
			reEnterPinRow.setVisibility(View.GONE);
			blankButton.setVisibility(View.GONE);
		}
		
		visibility = true;
	}

	/**
	 * Sets up listener to show/hide keyboard as per user event	 
	 */
	private void setKeyBoardListener() {
		final ScrollView activityRootView = (ScrollView)findViewById(R.id.m_scrollContainer);
		activityRootView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP) {
					inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);				
				}
				return false;
			}
		});
	}

	private void showCancelConfirmationDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.two_button_dialog_reverse_order);
		dialog.setCancelable(true);

		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText("Update Failed");
		TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		tvMsg.setText(R.string.cancel_confirmation);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_Yes);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				final Intent intent = new Intent(context, EmailLoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

	        String reversedPin = new StringBuffer(enteredPin).reverse().toString();
			SecretKey pinHash = null;
			try {
				pinHash = AMUtility.generateKey(enteredPin.toCharArray(), (reversedPin+"avanti").getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			} 

			String firstHash = Base64.encodeToString(pinHash.getEncoded(), Base64.NO_WRAP);
			
			if(isInternetAvailable()) {
				encryptPassword(enteredPin, getIntent().getStringExtra("password"));
			}
			return firstHash;
		}

		@Override
		protected void onPostExecute(String result) {
			if(isInternetAvailable()) {
				apiSetUserPin(result, false);					
			}
			else {
				apiSetUserPin(result, true);					
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {

		}
	}
	
	public void pinUpdatedCallback() {
		showSavedDialog();
	}
	
	private void excludePinAndValidateOtherFields() {

		if( ! phNoTextView.getText().toString().isEmpty() && 
				(phNoTextView.getText().toString().length() >= 10)) {
			if (! android.util.Patterns.PHONE.matcher(phNoTextView.getText().toString().trim()).matches()) {
				showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
				return;
			}
			else {
				String readyToDial = PhoneNumberUtils.stripSeparators(phNoTextView.getText().toString().trim());
				if(readyToDial.replace("0", "").length() == 0 ||
						! PhoneNumberUtils.isGlobalPhoneNumber(readyToDial) ||
						readyToDial.length() < 10) {
					showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
					return;
				}
			}
		}
		else {
			if(phNoTextView.getText().toString().trim().isEmpty()) {
				showSingleBtnDialog("Update Failed", "Phone number is required", "OK");
			}
			else {
				showSingleBtnDialog("Update Failed", getString(R.string.phone_must), "OK");
			}
			return;
		}

//		showSavedDialog();
		String firstName = "", lastName = "", email = "", phoneNumber = "";
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();

		if(! mFName.equals(fNameTextView.getText().toString().trim()) &&
				! fNameTextView.getText().toString().trim().isEmpty()) {
			firstName = fNameTextView.getText().toString().trim();
			editor.putString("firstname", firstName);
			mFName = firstName;
		}
		
		if(! mLName.equals(lNameTextView.getText().toString().trim()) &&
				! lNameTextView.getText().toString().trim().isEmpty()) {
			lastName = lNameTextView.getText().toString().trim();
			editor.putString("lastname", lastName);
		}
		
		if(! mEmail.equals(contactEmail.getText().toString().trim())) {
			email = contactEmail.getText().toString().trim();
			if ( ! android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
				if( ! email.isEmpty()) {
					showSingleBtnDialog("", getString(R.string.valid_email), "OK");
					return;
				}
				else {
					editor.putString("contactEmail", email);
				}
			} 
			else {
				editor.putString("contactEmail", email);
			}
		}
		
		if(! mPhNo.equals(phNoTextView.getText().toString().trim()) &&
				! phNoTextView.getText().toString().trim().isEmpty()) {
			phoneNumber = PhoneNumberUtils.stripSeparators(phNoTextView.getText().toString().trim());
			editor.putString("phoneNumber", phoneNumber);
		}
		
		if(isInternetAvailable()) {
//			editor.commit();
		}
		else {
			return;
		}

		Log.d(TAG, "Before Updation: "+firstName+" "+lastName+" "+email+" "+phoneNumber);
		
		if((firstName+lastName+email+phoneNumber).length() > 0) { 
			apiUpdateProfile(firstName, lastName, email, phoneNumber);
		}
		else {
//			showSavedDialog();
		}
	}
	
	@Override
	protected void onPause() {
		visibility = false;
		super.onPause();
	}
}
