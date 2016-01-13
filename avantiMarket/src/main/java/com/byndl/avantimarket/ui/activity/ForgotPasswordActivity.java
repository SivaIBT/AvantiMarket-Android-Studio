/**
 * @brief		This will be launched if user "Forgot Password" and wants to receive Password reset link       
 * @file        ForgotPasswordActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        26-Mar-2015
 * @copyright   incedo inc.
 * 
 */

package com.byndl.avantimarket.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMEventType;
import com.byndl.avantimarket.event.AMForgotPasswordDao;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.NetHttpInfo;
import com.incedo.network.NetHttpUtils;
import com.incedo.network.NetworkManagerClient;
import com.incedo.network.neterror.ApiErrorObj;
import com.incedo.network.neterror.InvalidArgException;

/**
 * 
 * @brief This will be launched if user "Forgot Password" and wants to receive Password reset link 
 *
 */
public class ForgotPasswordActivity extends BaseActivity {

	private EditText emailToResetPassword;
	private Button email_me, cancel;
	private InputMethodManager inputManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.forgot_password_layout);

		setUpListeners();
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (emailToResetPassword != null) {
			emailToResetPassword.requestFocus();
		}
		setKeyBoardListener();
	}

	/**
	 * This sets up Listeners to accept and respond to user input
	 */
	private void setUpListeners() {
		emailToResetPassword = (EditText)findViewById(R.id.m_emailToResetPassword);
		email_me = (Button)findViewById(R.id.m_emailMe);

		email_me.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(emailToResetPassword.getText().toString().isEmpty()) 
					showSingleBtnDialog("", "Please enter valid email address.", "OK");
				else if(! android.util.Patterns.EMAIL_ADDRESS.matcher(emailToResetPassword.getText().toString().trim()).matches()) {
					showSingleBtnDialog("", "Please enter valid email address.", "OK");
				}
				else {
					apiForgotPassword(emailToResetPassword.getText().toString());
				}
			}
		});
		
		cancel = (Button)findViewById(R.id.m_cancel_Button);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				final Intent intent = new Intent(context, EmailLoginActivity.class);
//				startActivity(intent);
				finish();
			}
		});
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
					inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);				
				}
				return false;
			}
		});
	}

	/**
	 * Invokes Network module and executes EVENT_FORGOT_PASSWORD request
	 */
	public void apiForgotPassword(String email){
		if( ! isRequestInProgress(AMEventType.EVENT_FORGOT_PASSWORD)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.PUT,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_FORGOT_PASSWORD,
						NetHttpUtils.HttpResposeDataModel.JSON), forgotPasswordHandler,
						this)).execute(new AMForgotPasswordDao(email));
				addToRequestsInProgress(AMEventType.EVENT_FORGOT_PASSWORD);
				showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Handler forgotPasswordHandler = new Handler() {

		public void handleMessage(Message msg) {
			dismissLoadingDialog();

			Log.d(TAG, "ForgotPasswordActivity HANDLER");
			switch (msg.what) {
			case AMEventType.EVENT_FORGOT_PASSWORD:
				removeFromRequestsInProgress(AMEventType.EVENT_FORGOT_PASSWORD);
				Object obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
//					MixPanelEvents.eventFailed(msg, "Forgot Password(Send Email) Fail");
					sendMixPanelEvent("Forgot Password(Send Email) Fail");
				} else {
					AMForgotPasswordDao mForgotPasswordDao = (AMForgotPasswordDao) obj;
					if(mForgotPasswordDao.isSuccess) {
						Log.d(TAG, "Forgot Password Request Success");
//						MixPanelEvents.eventSuccess(null, "Forgot Password(Send Email) Success");
						sendMixPanelEvent("Forgot Password(Send Email) Success");
						showResult(getResources().getString(R.string.reset_instructions));
					}
				}
				break;

			default:
				break;
			}
		};
	};
	
	protected void showResult(String aMessage) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comingsoon_dialog);
		dialog.setCancelable(false);
		TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_comingsoon_txtMsg);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_cominsoon_Yes);

		if (aMessage != null) {
			tvMsg.setText(aMessage);
		}

		btnOk.setText(getResources().getString(R.string.ok));
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
//				final Intent intent = new Intent(context, EmailLoginActivity.class);
//				startActivity(intent);
				finish();
			}
		});
		dialog.show();
	};
	
	private void sendMixPanelEvent(String result) {
		try {
    		JSONObject properties = new JSONObject();
            properties.put("Device Type", "Android "+getResources().getString(R.string.screen_type));
            properties.put("Device Class", getResources().getString(R.string.screen_type));
			properties.put("Timestamp", MixPanelEvents.getTimeStamp());
    		AMUtility.getMixPanelInstance().track(result, properties);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
