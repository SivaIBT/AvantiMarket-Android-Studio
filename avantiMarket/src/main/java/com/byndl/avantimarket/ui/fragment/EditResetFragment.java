package com.byndl.avantimarket.ui.fragment;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.activity.StartActivity;
import com.byndl.avantimarket.utils.AMUtility;

public class EditResetFragment extends BaseFragment {

	private EditText firstField, firstField_PartTwo, secondField, thirdField;
	private Button doneButton, cancelButton;
	private View verticalSeparator;
	private String title, decryptedPassForPinValidation = "";
	private String modifiedFirstName = "", modifiedLastName = "", modifiedEmail = "", modifiedPhoneNumber = "";
	private Editor editor;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.edit_reset_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		parentActivity.context = (MyAvantiActivity)getActivity();
		title = getArguments().getString("title");
		Log.d(TAG, "Argument In EditResetFragment: "+title);
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(title.toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
	}

	@Override
	public void updateUI() {

	}

	public void profileUpdatedSuccessfully() {
		
//		editor.commit();

		showResult("Your profile information has been saved", false);
	}
	
	public void serverCallBack(boolean result) {
		if(result) {
			showResult("Your password was successfully updated!", true);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setUpListeners();

		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMyAccount();
			}			
		});    	

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchMyAccount();
					return true;
				}
				return false;
			}
		});		
	}

	private void launchMyAccount() {
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getMyAccountFragment(), parentActivity.getMyAccountFragment().getTag());
		transaction.commit();
	}

	private void setUpListeners() {
		firstField = (EditText) parentActivity.findViewById(R.id.m_firstField);
		firstField_PartTwo = (EditText) parentActivity.findViewById(R.id.m_firstField_PartTwo);
		secondField = (EditText) parentActivity.findViewById(R.id.m_secondField);
		thirdField = (EditText) parentActivity.findViewById(R.id.m_thirdField);
		doneButton = (Button) parentActivity.findViewById(R.id.m_saveButton);
		doneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeyBoard();
				if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.edit_personal_info))) {
					validateAndSavePersonalInfo();
				}
				if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.reset_pin))) {
					validateAndSavePin();
				}
				if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.reset_password))) {
					validateAndSavePassword();
				}
			}
		});
		cancelButton = (Button) parentActivity.findViewById(R.id.m_cancel_Button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMyAccount();
			}
		});
		verticalSeparator = parentActivity.findViewById(R.id.m_verticalSeparator);

		setHintTexts();
	}

	private void setHintTexts() {
		clearFields();
		if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.edit_personal_info))) {

			verticalSeparator.setVisibility(View.VISIBLE);
			firstField_PartTwo.setVisibility(View.VISIBLE);

			firstField.setText(AMPreferenceManager.getPrefInstance().getPrefs().getString("firstname", ""));
			firstField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

			firstField_PartTwo.setText(AMPreferenceManager.getPrefInstance().getPrefs().getString("lastname", ""));
			firstField_PartTwo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

			secondField.setText(AMPreferenceManager.getPrefInstance().getPrefs().getString("contactEmail", getResources().getString(R.string.contact_email)));
			secondField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			if(secondField.getText().toString().trim().isEmpty()) {
				secondField.setHint(getResources().getString(R.string.contact_email));
			}
			
			String phoneNumber = AMPreferenceManager.getPrefInstance().getPrefs().getString("phoneNumber", "Phone");
			thirdField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
			if(phoneNumber.isEmpty()) {
				thirdField.setHint("("+getResources().getString(R.string.phone)+")");
			}
			else {
				thirdField.setText(phoneNumber);
			}
			thirdField.setInputType(InputType.TYPE_CLASS_PHONE);
		}
		if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.reset_pin))) {

			firstField.setHint(getResources().getString(R.string.old_pin));
			firstField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			firstField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});

			secondField.setHint(getResources().getString(R.string.new_pin));
			secondField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			secondField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});

			thirdField.setHint("Retype "+getResources().getString(R.string.new_pin));
			thirdField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			thirdField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
		}
		if(title.equalsIgnoreCase(parentActivity.getResources().getString(R.string.reset_password))) {

		    InputFilter[] FilterArray = new InputFilter[1];
		    FilterArray[0] = new InputFilter.LengthFilter(51);
		    
		    firstField.setHint("Old "+getResources().getString(R.string.password));
			firstField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			firstField.setFilters(FilterArray);

			secondField.setHint("New "+getResources().getString(R.string.password));
			secondField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			secondField.setFilters(FilterArray);

			thirdField.setHint("Retype New "+getResources().getString(R.string.password));
			thirdField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			thirdField.setFilters(FilterArray);
		}
	}

	private void validateAndSavePersonalInfo() {
		if(	firstField.getText().toString().isEmpty()) {
			hideKeyBoard();
			parentActivity.showSingleBtnDialog("", getResources().getString(R.string.first_name_empty), "OK");
		}
		else if( firstField_PartTwo.getText().toString().isEmpty()) {
			hideKeyBoard();
			parentActivity.showSingleBtnDialog("", getResources().getString(R.string.last_name_empty), "OK");
		}
		else {
			editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			if( ! firstField.getText().toString().equals(AMPreferenceManager.getPrefInstance().getPrefs().getString("firstname", "")) ||
					! firstField_PartTwo.getText().toString().equals(AMPreferenceManager.getPrefInstance().getPrefs().getString("lastname", ""))) {

				if ( ! firstField.getText().toString().isEmpty()) {
					modifiedFirstName = firstField.getText().toString().trim();
					editor.putString("firstname", modifiedFirstName);
				}

				if ( ! firstField_PartTwo.getText().toString().isEmpty()) {
					modifiedLastName = firstField_PartTwo.getText().toString().trim();
					editor.putString("lastname", modifiedLastName);
				}
			}
			if( ! secondField.getText().toString().equals(AMPreferenceManager.getPrefInstance().getPrefs().getString("contactEmail", ""))) {
				if ( ! android.util.Patterns.EMAIL_ADDRESS.matcher(secondField.getText().toString().trim()).matches()) {
					if( ! secondField.getText().toString().trim().isEmpty()) {
						parentActivity.showSingleBtnDialog("", getString(R.string.valid_email), "OK");
						return;
					}
					else {
						editor.putString("contactEmail", modifiedEmail);
					}
				} 
				else {
					modifiedEmail = secondField.getText().toString().trim();
					editor.putString("contactEmail", modifiedEmail);
				}
			}
			if( ! PhoneNumberUtils.stripSeparators(thirdField.getText().toString()).equals
					(AMPreferenceManager.getPrefInstance().getPrefs().getString("phoneNumber", ""))) {
				if ( ! android.util.Patterns.PHONE.matcher(thirdField.getText().toString().trim()).matches() ||
						(thirdField.getText().toString().length() > 0 && thirdField.getText().toString().length() < 10)) {
					if( ! thirdField.getText().toString().trim().isEmpty()) {
						parentActivity.showSingleBtnDialog("", getString(R.string.phone_must), "OK");
						return;
					}
					else {
						editor.putString("phoneNumber", modifiedPhoneNumber);
					}
				} else {
					String readyToDial = PhoneNumberUtils.stripSeparators(thirdField.getText().toString());
					if(readyToDial.replace("0", "").length() == 0 ||
							! PhoneNumberUtils.isGlobalPhoneNumber(readyToDial) ||
							readyToDial.length() < 10) {
						if( ! thirdField.getText().toString().trim().isEmpty()) {
							parentActivity.showSingleBtnDialog("", getString(R.string.phone_must), "OK");
							return;
						}
						else {
							editor.putString("phoneNumber", modifiedPhoneNumber);
						}
					}
					else {
						modifiedPhoneNumber = PhoneNumberUtils.stripSeparators(thirdField.getText().toString().trim());
						editor.putString("phoneNumber", modifiedPhoneNumber);
					}
				}
			}
			Log.d(TAG, "Modified Fields: "+modifiedFirstName+", "+modifiedLastName+", "+modifiedEmail+", "+modifiedPhoneNumber);
//			if((modifiedFirstName+modifiedLastName+modifiedEmail+modifiedPhoneNumber).length() > 0) 
			{ 
				parentActivity.apiUpdateProfile(modifiedFirstName, modifiedLastName, modifiedEmail, modifiedPhoneNumber);
			}
		}
	}

	private void validateAndSavePin() {
		if (firstField.getText().toString().isEmpty()) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Please enter old PIN.", "OK");
		}
		else if (secondField.getText().toString().isEmpty()) {
			parentActivity.dismissLoadingDialog();
			parentActivity.showSingleBtnDialog("Reset Failed", "Please enter new PIN.", "OK");
		}
		else if(thirdField.getText().toString().isEmpty()) {
			parentActivity.dismissLoadingDialog();
			parentActivity.showSingleBtnDialog("Reset Failed", "Please retype new PIN.", "OK");
		}
		else if (secondField.getText().toString().length() != 4) {
			parentActivity.dismissLoadingDialog();
			parentActivity.showSingleBtnDialog("Reset Failed", "PIN must be 4 digits.", "OK");
		}
		else if( ! secondField.getText().toString().equals(thirdField.getText().toString())) {
			parentActivity.dismissLoadingDialog();
			parentActivity.showSingleBtnDialog("Reset Failed", "The PIN's you entered don't match.", "OK");
		}
		else if( firstField.getText().toString().equals(thirdField.getText().toString())) {
			parentActivity.dismissLoadingDialog();
			parentActivity.showSingleBtnDialog("Reset Failed", "New PIN entered is same as old PIN. Please try a different PIN.", "OK");
		}
		else {
			parentActivity.showLoadingDialog();
			ValidateOldPinAsyncTask runner = new ValidateOldPinAsyncTask();
			runner.execute(firstField.getText().toString());
		}
	}

	private void validateAndSavePassword() {
		if (firstField.getText().toString().isEmpty()) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Please enter old Password.", "OK");
		}
		else if (secondField.getText().toString().isEmpty()) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Please enter new Password.", "OK");
		}
		else if (thirdField.getText().toString().isEmpty()) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Please retype new Password.", "OK");
		}
		else if (secondField.getText().toString().length() < 8) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Password must be at least 8 characters.", "OK");
		}
		else if( ! secondField.getText().toString().equals(thirdField.getText().toString())) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Password fields don't match.", "OK");
		}
		else if( firstField.getText().toString().equals(thirdField.getText().toString())) {
			parentActivity.showSingleBtnDialog("Reset Failed", "New Password entered is same as old Password. Please try a different Password.", "OK");
		}
		else if( ! validateOldPassword(firstField.getText().toString().trim())) {
			parentActivity.showSingleBtnDialog("Reset Failed", "Please check old Password and try again.", "OK");
		}
		else {
			parentActivity.apiChangePassword(firstField.getText().toString().trim(), secondField.getText().toString().trim());
		}	
	}

	private void clearFields() {
		firstField.setText("");
		firstField_PartTwo.setText("");
		secondField.setText("");
		thirdField.setText("");
		modifiedFirstName = ""; 
		modifiedLastName = ""; 
		modifiedEmail = ""; 
		modifiedPhoneNumber = "";
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		hideKeyBoard();
	}

	/**
	 * Hides the Keyboard
	 */
	private void hideKeyBoard(){ 
		if(inputManager == null) {
			inputManager = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputManager.hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();	
	}

	private boolean validateOldPassword(String oldPassword) {
		String authString = AMPreferenceManager.getPrefInstance().getUsername() + ":" + oldPassword;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		Log.d(TAG, "Saved Auth and TBD Auth in PinResetActivity: "+AMPreferenceManager.getPrefInstance().getAuthHeader()+" & "+new String(authEncBytes));			
		if(AMPreferenceManager.getPrefInstance().getAuthHeader().contains(new String(authEncBytes))) {
			return true;
		}
		return false;
	}
	
	private void showResult(String aMessage, final boolean isPasswordChange) {

		final Dialog dialog = new Dialog(parentActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dailog_two_btn);
		dialog.setCancelable(false);
		
		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText(R.string.update_success);
		TextView tvMsg = (TextView) dialog.findViewById(R.id.txtMsg);
		if (aMessage != null) {
			tvMsg.setText(aMessage);
		}

		Button btnOk = (Button) dialog.findViewById(R.id.btnYes);
		btnOk.setText(getResources().getString(R.string.ok));
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				if(isPasswordChange) {
					parentActivity.clearInternalStoredContent(true);
					final Intent intent = new Intent(parentActivity,
							StartActivity.class);
					startActivity(intent);
					parentActivity.finish();
				}
				else {
					launchMyAccount();	
				}
			}
		});
		dialog.show();
	};
	
	private class ValidateOldPinAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String enteredPin = params[0];
			try {
				String reversedPin = new StringBuffer(enteredPin).reverse().toString();
				SecretKey pinHash = AMUtility.generateKey(enteredPin.toCharArray(), (reversedPin+"avanti").getBytes());

				//Code for HashHash ++
				String firstHash = android.util.Base64.encodeToString(pinHash.getEncoded(), android.util.Base64.NO_WRAP);
				String reversedfirstHash = new StringBuffer(firstHash).reverse().toString();
				SecretKey pinHashHash = AMUtility.generateKey(firstHash.toCharArray(), (reversedfirstHash+"avanti").getBytes());
				//Code for HashHash --

				decryptedPassForPinValidation = AMUtility.decrypt(AMPreferenceManager.getPrefInstance().getEncryptedPasswd(), pinHashHash);
				String authStringForPinValidation = AMPreferenceManager.getPrefInstance().getUsername() + ":" + decryptedPassForPinValidation;
				byte[] authEncBytes = Base64.encodeBase64(authStringForPinValidation.getBytes());
				Log.d(TAG, "Saved Auth and TBD Auth in EditResetFragment: "+AMPreferenceManager.getPrefInstance().getAuthHeader()+" & "+new String(authEncBytes));			
				if(AMPreferenceManager.getPrefInstance().getAuthHeader().contains(new String(authEncBytes))) {
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
			if(result.contains("false")) {
				parentActivity.dismissLoadingDialog();
				parentActivity.showSingleBtnDialog("Reset Failed", "Please check old PIN and try again.", "OK");
			}
			else {
				SaveNewPinAsyncTask runner = new SaveNewPinAsyncTask();
				runner.execute(secondField.getText().toString().trim());
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {

		}
	}
	
	private class SaveNewPinAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String enteredPin = params[0];
			try {
				String reversedPin = new StringBuffer(enteredPin).reverse().toString();
				SecretKey pinHash = AMUtility.generateKey(enteredPin.toCharArray(), (reversedPin+"avanti").getBytes());
				String firstHash = android.util.Base64.encodeToString(pinHash.getEncoded(), android.util.Base64.NO_WRAP);
				parentActivity.apiSetUserPin(firstHash, true);
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "GenerateKeyException: "+e.getLocalizedMessage());
			}

			if(parentActivity.isInternetAvailable()) {
				parentActivity.encryptPassword(enteredPin, decryptedPassForPinValidation);
				return "true";
			}
			return "false";
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.contains("true")){
				showResult("Your PIN was successfully updated.", false);
			}
			else {
				parentActivity.dismissLoadingDialog();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {

		}
	}
	
}
