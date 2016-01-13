package com.byndl.avantimarket.ui.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberUtils;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.event.AMChangePasswordDao;
import com.byndl.avantimarket.event.AMCheckConsumerSfdcDao;
import com.byndl.avantimarket.event.AMCreateConsumerSfdcDao;
import com.byndl.avantimarket.event.AMEventType;
import com.byndl.avantimarket.event.AMGetSalesForceTokenDao;
import com.byndl.avantimarket.event.AMLoginDao;
import com.byndl.avantimarket.event.AMOperatorInfoDao;
import com.byndl.avantimarket.event.AMSetUserPinDao;
import com.byndl.avantimarket.event.AMUpdateProfileDao;
import com.byndl.avantimarket.interfaces.ICreateCase;
import com.byndl.avantimarket.storage.AMPrefKeys;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.fragment.BaseFragment;
import com.byndl.avantimarket.ui.fragment.EditResetFragment;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetHttpInfo;
import com.incedo.network.NetHttpUtils;
import com.incedo.network.NetworkManagerClient;
import com.incedo.network.neterror.ApiErrorObj;
import com.incedo.network.neterror.InvalidArgException;

public class BaseActivity extends FragmentActivity {

	protected final String TAG = "AvantiMarket";
	private Set<Integer> requestsInProgress = new HashSet<Integer>(7);
	public ProgressDialog mLoadingDialog = null;
	public Activity context;
	private ProgressDialog mDialog;
	protected boolean isBackpressed = false;
	protected String previousSalesForceToken = "";
	public int numberOfRepeatitiveCalls = 0;
	public int currentRequest = -1;
	public Object[] currentRequestArguments = null;

	/**
	 * Displays "Loading ..." animation while a request is in progress in background
	 */
	public void showLoadingDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new ProgressDialog(this);
			mLoadingDialog.setMessage("Loading...");
			mLoadingDialog.setCancelable(false);
		}
		if( ! mLoadingDialog.isShowing()) {
			mLoadingDialog.show();
		}
	}

	/**
	 * Dismisses "Loading ..." animation 
	 */
	public void dismissLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	public boolean isLoadingDialogShowing() {
		if(mLoadingDialog != null) {
			return mLoadingDialog.isShowing();
		}
		else {
			return false;
		}
	}
	/**
	 * If GET_SCAN_CODES (or) GET_STORED_CARDS request fails, displays the reason for failure in the form of Dialog
	 * @param aTitle Title to be shown
	 * @param aMessage Message to be shown
	 * @param aBtnTxt Button text to be shown
	 */
	public void showSingleBtnDialog(final String aTitle, final String aMessage,
			final String aBtnTxt) {
		final Dialog dialog = new Dialog(BaseActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dailog_two_btn);
		dialog.setCancelable(false);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		TextView tvMsg = (TextView) dialog.findViewById(R.id.txtMsg);

		Button btnOk = (Button) dialog.findViewById(R.id.btnYes);

		if (aTitle != null && tvTitle != null) {
			tvTitle.setText(aTitle);
		}
		if (aMessage != null && tvMsg != null) {
			tvMsg.setText(aMessage);
			if(aMessage.contains("MyMarketCard.com")) {
				Linkify.addLinks(tvMsg, Linkify.WEB_URLS);
			}
		}

		if (aBtnTxt != null) {
			btnOk.setText(aBtnTxt);
		}

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		if( ! isFinishing()) {
			dialog.show();
		}
	}

	/**
	 * A Hash set is being maintained with "Requests in Progress".
	 * This API is to add a request to the Hash set.
	 * @param request Request to be added to Hash Set
	 */
	public void addToRequestsInProgress(Integer request) {
		if (!requestsInProgress.contains(request)) {
			requestsInProgress.add(request);
		}
	}

	/**
	 * A Hash set is being maintained with "Requests in Progress".
	 * This API is to remove a request from the Hash set.
	 * @param request Request to be removed from Hash Set
	 */
	public void removeFromRequestsInProgress(Integer request) {
		requestsInProgress.remove(request);
	}

	/**
	 * A Hash set is being maintained with "Requests in Progress".
	 * This API is to find out whether a specific request is already in Progress.
	 * @param request Request to be verified in Hash Set
	 */
	public boolean isRequestInProgress(Integer request) {
		if (requestsInProgress.contains(request)) {
			return true;
		}
		return false;
	}

	/**
	 * Displays the result of Reload request in the form of dialog
	 * @param aMessage Message to be displayed
	 */
	protected void showResultDialog(String aMessage) {
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
			}
		});
		dialog.show();
	}
	
	public void showNetworkErrorDialog(Message msg) {
		String errorMessage = ((ApiErrorObj) msg.obj).getErrorMsg();
		
		if(		errorMessage.contains("Not Found") ||
				errorMessage.contains("Unable to resolve") ||
				errorMessage.contains("Unauthorized") ||
				errorMessage.contains("json")) {
			return;
		}
		if (((ApiErrorObj) msg.obj).getErrorMsg()
				.equalsIgnoreCase("Network Error")){
			showSingleBtnDialog(
					((ApiErrorObj) msg.obj).getErrorMsg(),
					getResources().getString(
							R.string.network_error_msg), null);
		}
		else if (!errorMessage.equalsIgnoreCase("Server Error")){
			if(errorMessage.contains("peer certificate")) showSingleBtnDialog("Network Error", getString(R.string.check_internet), null);
			else if(errorMessage.contains("timed out")) showSingleBtnDialog("Network Error", getString(R.string.check_internet), null);
			else showSingleBtnDialog("", errorMessage, null);
		}
		else {
			showSingleBtnDialog(
					((ApiErrorObj) msg.obj).getErrorMsg(),
					getResources().getString(R.string.error_msg),
					null);
		}
	}
	
	public void encryptPassword(String pin, String password) {
		try {
			SecretKey pinHash = null;
			String firstHash = "";
			
			if(pin.length()>4) {
				firstHash = pin;
			}
			else {
				String reversedPin = new StringBuffer(pin).reverse().toString();
				pinHash = AMUtility.generateKey(pin.toCharArray(), (reversedPin+"avanti").getBytes());
//				BigInteger bi = new BigInteger(1, pinHash.getEncoded());
//				firstHash = String.format("%064x", bi);
				firstHash = Base64.encodeToString(pinHash.getEncoded(), Base64.NO_WRAP);
			}

			String reversedfirstHash = new StringBuffer(firstHash).reverse().toString();
			SecretKey pinHashHash = AMUtility.generateKey(firstHash.toCharArray(), (reversedfirstHash+"avanti").getBytes());
			
			String storedEncryptedPass = AMUtility.encrypt(password, pinHashHash);
			AMPreferenceManager.getPrefInstance().setEncryptedPasswd(storedEncryptedPass);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "GenerateKeyException: "+e.getLocalizedMessage());
		}
	}

	/**
	 * Invokes Network module and executes EVENT_SET_USER_PIN request
	 */
	public void apiSetUserPin(String newPin, boolean showLoading){

		if( ! isRequestInProgress(AMEventType.EVENT_SET_USER_PIN)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.PUT,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_SET_USER_PIN,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMSetUserPinDao(newPin));
				addToRequestsInProgress(AMEventType.EVENT_SET_USER_PIN);
				if(showLoading) {
					showLoadingDialog();
				}
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes EVENT_UPDATE_PROFILE request
	 */
	public void apiUpdateProfile(String firstName, String lastName, String email, String phoneNumber){
		if( ! isRequestInProgress(AMEventType.EVENT_UPDATE_PROFILE)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.PUT,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_UPDATE_PROFILE,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMUpdateProfileDao(firstName, lastName, email, phoneNumber));
				addToRequestsInProgress(AMEventType.EVENT_UPDATE_PROFILE);
				showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void apiChangePassword(String aOldPassword, String aNewPassword){
		if( ! isRequestInProgress(AMEventType.EVENT_CHANGE_PASSWORD)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.PUT,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_CHANGE_PASSWORD,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMChangePasswordDao(aOldPassword, aNewPassword));
				addToRequestsInProgress(AMEventType.EVENT_CHANGE_PASSWORD);
				showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_CONSUMER_SFDC request
	 */
	public void apiGetConsumerSfdcInfoUsingAmsId(String amsId) {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if( ! isRequestInProgress(AMEventType.EVENT_GET_CONSUMER_SFDC)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_CONSUMER_SFDC,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMCheckConsumerSfdcDao(amsId));
				addToRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_SFDC);
//				showLoadingDialog();
				currentRequest = AMEventType.EVENT_GET_CONSUMER_SFDC;
				currentRequestArguments = new Object[1];
				currentRequestArguments[0] = amsId;
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_SALES_FORCE_TOKEN request
	 */
	public void apiGetSalesForceToken() {
		if( ! isRequestInProgress(AMEventType.EVENT_GET_SALES_FORCE_TOKEN)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_SALES_FORCE_TOKEN,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMGetSalesForceTokenDao());
				addToRequestsInProgress(AMEventType.EVENT_GET_SALES_FORCE_TOKEN);
//				showLoadingDialog();
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}

	public void apiCreateConsumerSfdc(String firstName, String lastName, String amsId, String operatorId, String phone, String mobilePhone, String email) {
		if( ! isRequestInProgress(AMEventType.EVENT_CREATE_CONSUMER_SFDC)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_CREATE_CONSUMER_SFDC, 
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler, 
						this)).execute(new AMCreateConsumerSfdcDao(firstName, lastName, amsId, operatorId, phone, mobilePhone, email));
				addToRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_ISSUES);
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Invokes Network module and executes GET_OPERATOR_INFO request
	 */
	public void apiGetOperatorInfoUsingOperatorId(String operatorId, boolean showLoadingDialog) {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if( ! isRequestInProgress(AMEventType.EVENT_GET_OPERATOR_INFO)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_OPERATOR_INFO,
						NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler,
						this)).execute(new AMOperatorInfoDao(operatorId));
				addToRequestsInProgress(AMEventType.EVENT_GET_OPERATOR_INFO);
				if(showLoadingDialog) showLoadingDialog();
				currentRequest = AMEventType.EVENT_GET_OPERATOR_INFO;
				currentRequestArguments = new Object[2];
				currentRequestArguments[0] = operatorId;
				currentRequestArguments[1] = showLoadingDialog+"";
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isInternetAvailable() {
		try {
			ConnectivityManager conMgr = (ConnectivityManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo i = conMgr.getActiveNetworkInfo();
			if (i == null)
				return false;
			if (!i.isConnected())
				return false;
			if (!i.isAvailable())
				return false;
			return true;        
		} catch (Exception e) {
			return false;
		}
	}

	Handler baseActivityHandler = new Handler() {

		public void handleMessage(Message msg) {
			dismissLoadingDialog();

			Log.d(TAG, "UpdateProfileActivity HANDLER");
			switch (msg.what) {
			case AMEventType.EVENT_AUTHENTICATE:
				if(context.toString().contains("EmailLoginActivity")) {
					dismissSigningDialog();
				}
				Object obj = msg.obj;
				if (obj.getClass().equals(ApiErrorObj.class)) {
					if(context.toString().contains("PinLoginActivity")){
						dismissSigningDialog();
					}
					
					if (((ApiErrorObj) msg.obj).getErrorCode() == 401) {
						if(context.toString().contains("EmailLoginActivity")){
							showSingleBtnDialog(
									getResources().getString(R.string.unauthorized),
									getResources().getString(
											R.string.check_credentials), null);
						}
						else if(context.toString().contains("PinLoginActivity")){
							((PinLoginActivity)context).authenticationCallBack(false, "");
						}
					} else {
						showNetworkErrorDialog(msg);
					}

					if(context.toString().contains("EmailLoginActivity")){
						((EmailLoginActivity)context).setSuperProperties();
						MixPanelEvents.eventFailed(msg, "Sign In Fail");
					}

				} else {
					Log.d("First Name:", ((AMLoginDao) obj).mFirstName + ":"
							+ ((AMLoginDao) obj).mLastName + ":"
							+ ((AMLoginDao) obj).mRequestPhoneNo + ":"
							+ ((AMLoginDao) obj).mRequestPin);
					
					saveResponseToInternalMemory((AMLoginDao) obj);

					if(context.toString().contains("EmailLoginActivity")){
						String userName = AMPreferenceManager.getPrefInstance().getUsername();
						((EmailLoginActivity)context).setSuperProperties();
						if(AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("termsAcceptedBy"+userName.trim(), false)) {
							launchPostLoginScreen(obj);
						}
						else {
							showTermsAndConditions(obj);
						}
						MixPanelEvents.eventSuccess(null, "Sign In Success");
					}
					else if(context.toString().contains("PinLoginActivity")){
						((PinLoginActivity)context).authenticationCallBack(true, ((AMLoginDao)obj).mPin);
					}
				}
				break;

			case AMEventType.EVENT_SET_USER_PIN:
				removeFromRequestsInProgress(AMEventType.EVENT_SET_USER_PIN);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					if(context.getLocalClassName().contains("UpdateProfileActivity")) {
						MixPanelEvents.eventFailed(msg, "Account Setup Fail");
					}
					showNetworkErrorDialog(msg);
				} else {
					AMSetUserPinDao mSetUserPinDao = (AMSetUserPinDao) obj;
					if(mSetUserPinDao.isSuccess) {
						Log.d("SetUserPin Success : ", "true");
						if(context.getLocalClassName().contains("PinResetActivity")) {
							((PinResetActivity)context).showSavedDialog();
						}
						if(context.getLocalClassName().contains("UpdateProfileActivity")) {
							((UpdateProfileActivity)context).showSavedDialog();
							MixPanelEvents.eventSuccess(null, "Account Setup Success");
						}
					}
				}
				break;

			case AMEventType.EVENT_UPDATE_PROFILE:
				removeFromRequestsInProgress(AMEventType.EVENT_UPDATE_PROFILE);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					AMUpdateProfileDao mUpdateProfileDao = (AMUpdateProfileDao) obj;
					if(mUpdateProfileDao.isSuccess) {
						if(context.getLocalClassName().contains("UpdateProfileActivity")) {
							apiGetConsumerSfdcInfoUsingAmsId(AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", ""));
							((UpdateProfileActivity)context).showSavedDialog();
						}
						if(context.getLocalClassName().contains("MyAvantiActivity")) {
							List<Fragment> fragments = ((MyAvantiActivity)context).getSupportFragmentManager()
									.getFragments();
							for (Fragment fragment : fragments) {
								if (fragment != null && fragment.isVisible() && fragment.toString().contains("EditResetFragment")) {
									((EditResetFragment) fragment).profileUpdatedSuccessfully();
								}
							}
						}
					}
				}
				break;

			case AMEventType.EVENT_CHANGE_PASSWORD:
				removeFromRequestsInProgress(AMEventType.EVENT_CHANGE_PASSWORD);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					AMChangePasswordDao mChangePasswordDao = (AMChangePasswordDao) obj;
					if(mChangePasswordDao.mResult) {
						Log.d(TAG, "ChangePassword Success");
						List<Fragment> fragments = ((MyAvantiActivity)context).getSupportFragmentManager()
								.getFragments();
						for (Fragment fragment : fragments) {
							if (fragment != null && fragment.isVisible() &&
									fragment.toString().contains("EditResetFragment")) {
								((EditResetFragment)fragment).serverCallBack(true);
							}
						}
					}
					else {
						showResultDialog("Change Password Failed");
					}
				}
				break;

			case AMEventType.EVENT_GET_SALES_FORCE_TOKEN:
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_SALES_FORCE_TOKEN");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_SALES_FORCE_TOKEN);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					
//					showNetworkErrorDialog(msg);
				} else {
					AMGetSalesForceTokenDao mSalesForceTokenDao = (AMGetSalesForceTokenDao) obj;
					AMUtility.SALES_FORCE_TOKEN = mSalesForceTokenDao.mToken;
					
					if(currentRequest != -1) {
						reSendPreviousRequest(context);
					}
					else {
						if(context.toString().contains("EmailLoginActivity")) {
							apiGetConsumerSfdcInfoUsingAmsId(AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", ""));
						}
						if(context.toString().contains("PinLoginActivity")) {
							apiGetOperatorInfoUsingOperatorId(AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", ""), false);
						}
					}
				}
				break;

			case AMEventType.EVENT_GET_CONSUMER_SFDC:
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_CONSUMER_SFDC");
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				removeFromRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_SFDC);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					
//					showNetworkErrorDialog(msg);
					
					if(((ApiErrorObj) msg.obj).getErrorMsg().contains("Unauthorized")) {
						apiGetSalesForceToken();
					}
				} else {
					AMCheckConsumerSfdcDao mConsumerSfdcDao = (AMCheckConsumerSfdcDao) obj;
					apiGetOperatorInfoUsingOperatorId(AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", ""), false);
					if(mConsumerSfdcDao.mResult == false) {
						createConsumerSfdc();
					}
				}
				break;

			case AMEventType.EVENT_CREATE_CONSUMER_SFDC:
				Log.d(TAG, "In Handler:AMEventType.EVENT_CREATE_CONSUMER_SFDC");
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				removeFromRequestsInProgress(AMEventType.EVENT_CREATE_CONSUMER_SFDC);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					
//					showNetworkErrorDialog(msg);

					if(((ApiErrorObj) msg.obj).getErrorMsg().contains("Unauthorized")) {
						apiGetSalesForceToken();
					}
				} else {
					AMCreateConsumerSfdcDao mCreateSfdcDao = (AMCreateConsumerSfdcDao) obj;
					Log.d(TAG, "Consumer SFDC Created: "+mCreateSfdcDao.mConsumerSfdc);
				}
				break;

			case AMEventType.EVENT_GET_OPERATOR_INFO:
				Log.d(TAG, "In Handler:AMEventType.EVENT_OPERATOR_INFO");
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				removeFromRequestsInProgress(AMEventType.EVENT_GET_OPERATOR_INFO);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					if(AMApplication.isActivityVisible()) {
						
//						showNetworkErrorDialog(msg);

						if(((ApiErrorObj) msg.obj).getErrorMsg().contains("Unauthorized")) {
							apiGetSalesForceToken();
						}
					}
				} else {
					AMOperatorInfoDao mOperatorInfoDao = (AMOperatorInfoDao) obj;
					Log.d(TAG, "Contact Us Status: "+mOperatorInfoDao.showContactUs);
					Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
					editor.putBoolean("isScuFetched", true);
					editor.putBoolean("showContactUs", mOperatorInfoDao.showContactUs);
					editor.commit();
				}
				if(context.toString().contains("MyAvantiActivity") &&
						AMApplication.isActivityVisible()){
					MyAvantiActivity myAvantiActivity = (MyAvantiActivity)context;
					FragmentTransaction transaction = myAvantiActivity.getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.fragment_block, myAvantiActivity.getSupportFragment(),
							myAvantiActivity.getSupportFragment().getTag());
					transaction.commit();
				}
				break;

			default:
				break;
			}
		};
	};

	private void createConsumerSfdc()
	{
		String firstName = AMPreferenceManager.getPrefInstance().getPrefs().getString("firstname", "");
				String lastName = AMPreferenceManager.getPrefInstance().getPrefs().getString("lastname", "");
				String amsId  = AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", "");
				String operatorId = AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", "");
				String phone = AMPreferenceManager.getPrefInstance().getPrefs().getString("phoneNumber", "");
				String mobilePhone = AMPreferenceManager.getPrefInstance().getPrefs().getString("phoneNumber", "");
				String email = AMPreferenceManager.getPrefInstance().getUsername();

		if(		firstName != null && !firstName.isEmpty() &&
				lastName != null && !lastName.isEmpty() &&
						amsId != null && !amsId.isEmpty() &&
								phone != null && !phone.isEmpty() &&
										mobilePhone != null && !mobilePhone.isEmpty() &&
										amsId != null && !amsId.isEmpty() &&						
												email != null && !email.isEmpty())
		{
			InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
					AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
			apiCreateConsumerSfdc(firstName, lastName, amsId, operatorId, phone, mobilePhone, email);
		}
	}
	
	 	/**
	 * Invokes Network module and executes AUTHENTICATE request 
	 */
	protected void apiLogin(String authString) {
		try {
			(new NetworkManagerClient(new NetHttpInfo(this,
					NetHttpUtils.HttpCommand.GET, NetHttpUtils.HttpSchema.HTTPS,
					AMEventType.EVENT_AUTHENTICATE,
					NetHttpUtils.HttpResposeDataModel.JSON), baseActivityHandler, this))
					.execute(new AMLoginDao(authString));
			if(AMApplication.isActivityVisible()) showSigningDialog();
		} catch (InvalidArgException e) {
			e.printStackTrace();
			Log.d(TAG, "EmailLoginActivity::apiLogin() Exception: "+e.getLocalizedMessage());
		}
	}

	private void saveResponseToInternalMemory(AMLoginDao aLoginDao) {
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		editor.putString("firstname", aLoginDao.mFirstName);
		editor.putString("lastname", aLoginDao.mLastName);
		editor.putString("phoneNumber", PhoneNumberUtils.stripSeparators(aLoginDao.mPhoneNumber));
		editor.putString("contactEmail", aLoginDao.mContactEmail);
		editor.putString("amsId", aLoginDao.mAmsId);
		editor.putString("operatorId", aLoginDao.mOperatorId);
		editor.putString("locationId", aLoginDao.mLocationId);
		editor.commit();
//		apiGetConsumerSfdcInfoUsingAmsId(aLoginDao.mAmsId);
		
		currentRequest = -1;
		apiGetSalesForceToken();
		AMUtility.getMixPanelInstance().identify(aLoginDao.mAmsId);
	}

    public void showTermsAndConditions(final Object obj) {
    	
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.terms_and_conditions);
		dialog.setCancelable(false);

		WebView tnc = (WebView) dialog.findViewById(R.id.terms_and_conditions_webview);  
		tnc.loadUrl("file:///android_asset/eula.html");
		WebSettings settings = tnc.getSettings();
        settings.setDefaultTextEncodingName("Latin-1");
		
        TextView accepting = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		Button agree = (Button) dialog.findViewById(R.id.accept_button);
		Button cancel = (Button) dialog.findViewById(R.id.cancel_button);
		if(obj == null) {
			accepting.setVisibility(View.GONE);
			agree.setVisibility(View.GONE);
			cancel.setText(R.string.ok);
			cancel.setWidth(LayoutParams.WRAP_CONTENT);
		}

		agree.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				String userName = AMPreferenceManager.getPrefInstance().getUsername();
				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putBoolean("termsAcceptedBy"+userName.trim(), true);
				editor.commit();
				if(obj != null) {
					launchPostLoginScreen(obj);
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(obj != null) {
					showTermsCancelConfirmation(obj);
				}
			}
		});
    	dialog.show();
    }
    
    private void showTermsCancelConfirmation(final Object obj) {

    	final Dialog dialog = new Dialog(context);
    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialog.setContentView(R.layout.terms_cancel_confirmation);
    	dialog.setCancelable(false);

    	TextView message = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
    	message.setText(getString(R.string.acceptance_required));

    	Button agree = (Button) dialog.findViewById(R.id.accept_button);
    	Button cancel = (Button) dialog.findViewById(R.id.cancel_button);

    	agree.setOnClickListener(new OnClickListener() {			
    		@Override
    		public void onClick(View v) {
    			dialog.dismiss();
    			showTermsAndConditions(obj);
    		}
    	});

    	cancel.setOnClickListener(new OnClickListener() {			
    		@Override
    		public void onClick(View v) {
    			dialog.dismiss();
    			clearInternalStoredContent(true);
    		}
    	});
    	dialog.show();
    }

	/**
	 * Clears the internal memory in case of Signout (or) App Exit
	 * @param isSignOut whether the scenario is Signout (or) App Exit
	 */
	public void clearInternalStoredContent(boolean isSignOut) {

		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();

		if(isSignOut) {
			isBackpressed = true;
			editor.putString(AMPrefKeys.ENCRYPTED_PASSWORD_KEY, "");
			editor.putString("firstname", "");
			editor.putBoolean("isScuFetched", false);
			editor.putBoolean("background", false);
			editor.putString("amsId", "");
			editor.putString("operatorId", "");
			editor.putString("locationId", "");
			editor.commit();
			MixPanelEvents.eventSuccess(null, "Signout Success");
		}

		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if(fragments != null) {
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isVisible()) {
					BaseFragment.clearStoredCards();
				}
			}
		}

		editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		editor.putStringSet("scanCodes", null);
		editor.putStringSet("reloadDenominations", null);
		editor.putInt("previousCreditCardCount", -1);
		editor.putInt("issueCategoryIndex", -1);

		AMUtility.mALUsingMarketArticles = null; 
		AMUtility.mALUsingAppArticles = null; 
		AMUtility.mALCommonIssuesArticles = null; 
		AMUtility.sfFeedbackCategories = null;
		if(AMUtility.sfConsumerCategories != null) {
			clearContactUsIssueTypes();
			AMUtility.sfConsumerCategories = null;
		}
		
		if(! AMPreferenceManager.getPrefInstance().getPrefs().getString("session", "").isEmpty()) {
			//End the session
			Log.d("Session End: ", "SessionID: "+AMPreferenceManager.getPrefInstance().getPrefs().getString("session", ""));

			final JSONObject properties = new JSONObject();
			try {
				properties.put("AMS ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", ""));
				properties.put("Operator ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", ""));
				properties.put("Location ID", AMPreferenceManager.getPrefInstance().getPrefs().getString("locationId", ""));
				properties.put("Device Type", "Android "+getResources().getString(R.string.screen_type));
				properties.put("Device Class", getResources().getString(R.string.screen_type));
				properties.put("Timestamp", MixPanelEvents.getTimeStamp());
				AMUtility.getMixPanelInstance().registerSuperProperties(properties);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AMUtility.getMixPanelInstance().track("Session: "+AMPreferenceManager.getPrefInstance().getPrefs().getString("session", ""));
			AMUtility.getMixPanelInstance().clearSuperProperties();
			editor.putString("session", "");
		}
		editor.commit();
	}

	private void clearContactUsIssueTypes() {
		String[] mContactUsCategories = AMUtility.sfConsumerCategories.toArray(new String[AMUtility.sfConsumerCategories.size()]);
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		for(int i=0; i < mContactUsCategories.length; i++) {
			editor.putStringSet(mContactUsCategories[i], null);
		}
		editor.commit();
	}
	
	private void launchPostLoginScreen(Object obj) {
		if(((AMLoginDao)obj).mRequestPin){
			((EmailLoginActivity)context).launchEditProfile((AMLoginDao) obj);
		}
		else {
			((EmailLoginActivity)context).launchMyAvanti((AMLoginDao) obj);
		}
	}
	
	private void reSendPreviousRequest(Context context) {
		BaseFragment currentActiveFragment = null;
		if(context.toString().contains("MyAvantiActivity")){
			List<Fragment> fragments = ((MyAvantiActivity)context).getSupportFragmentManager()
					.getFragments();
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isVisible()) {
					currentActiveFragment = (BaseFragment)fragment;
				}
			}
		}

		if( ! previousSalesForceToken.contains(AMUtility.SALES_FORCE_TOKEN)) {
			numberOfRepeatitiveCalls ++;
			previousSalesForceToken = AMUtility.SALES_FORCE_TOKEN;
		}
		
		if(numberOfRepeatitiveCalls > 3) {
			numberOfRepeatitiveCalls = 0;
			if(currentRequest != AMEventType.EVENT_GET_CONSUMER_SFDC &&
				currentRequest != AMEventType.EVENT_GET_OPERATOR_INFO) {
				showSingleBtnDialog("Server Error", "Our server is currently busy.\n\rPlease try again later", "OK");
			}
			else if(currentRequest == AMEventType.EVENT_GET_OPERATOR_INFO) {
				if(((String)currentRequestArguments[1]).contains("true"))
					showSingleBtnDialog("Server Error", "Our server is currently busy.\n\rPlease try again later", "OK");
			}
			return;
		}
		
		switch (currentRequest) {
		case AMEventType.EVENT_GET_CONSUMER_CATEGORIES:
			currentActiveFragment.apiGetConsumerCategories();
			break;

		case AMEventType.EVENT_GET_CONSUMER_ISSUES:
			currentActiveFragment.apiGetConsumerIssues((String)currentRequestArguments[0]);
			break;

		case AMEventType.EVENT_GET_FEEDBACK_CATEGORIES:
			currentActiveFragment.apiGetFeedbackCategories();
			break;

		case AMEventType.EVENT_GET_ARTICLE_LINK:
			currentActiveFragment.apiGetArticleLink((String)currentRequestArguments[0], (String)currentRequestArguments[1]);
			break;

		case AMEventType.EVENT_GET_ARTICLE:
			currentActiveFragment.apiGetArticle((String)currentRequestArguments[0]);
			break;

		case AMEventType.EVENT_POST_CONSUMER_ISSUE:
			currentActiveFragment.apiCaseConsumerIssues((String)currentRequestArguments[0], (String)currentRequestArguments[1], (String)currentRequestArguments[2], (String)currentRequestArguments[3], (ICreateCase)currentRequestArguments[4]);
			break;

		case AMEventType.EVENT_POST_FEEDBACK:
			currentActiveFragment.apiCaseCreateFeedback((String)currentRequestArguments[0], (String)currentRequestArguments[1], (String)currentRequestArguments[2], (ICreateCase)currentRequestArguments[3]);
			break;

		case AMEventType.EVENT_GET_CONSUMER_SFDC:
			apiGetConsumerSfdcInfoUsingAmsId((String)currentRequestArguments[0]);
			break;

		case AMEventType.EVENT_GET_OPERATOR_INFO:
			if(((String)currentRequestArguments[1]).contains("true"))
				apiGetOperatorInfoUsingOperatorId((String)currentRequestArguments[0], true);
			if(((String)currentRequestArguments[1]).contains("false"))
				apiGetOperatorInfoUsingOperatorId((String)currentRequestArguments[0], false);
			break;

		default:
			break;
		}
	}

	public void showSigningDialog() {
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
			mDialog.setMessage("Signing...");
			mDialog.setCancelable(false);
		}
		if( ! mDialog.isShowing()) {
			mDialog.show();
		}
	}

	/**
	 * Dismisses "Signing ..." animation 
	 */
	public void dismissSigningDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

}
