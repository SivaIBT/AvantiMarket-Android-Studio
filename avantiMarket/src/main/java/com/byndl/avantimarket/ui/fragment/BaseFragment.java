/**
 * @brief       This is the BASE class for all the screens shown after login; this has the common functionality
 * @file        BaseFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        24-Mar-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;
import java.util.Set;

import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMArticleDetailsDao;
import com.byndl.avantimarket.event.AMArticleListDao;
import com.byndl.avantimarket.event.AMCaseConsumerIssuesDao;
import com.byndl.avantimarket.event.AMCaseCreateFeedbackDao;
import com.byndl.avantimarket.event.AMChargeStoredCardDao;
import com.byndl.avantimarket.event.AMDeleteLastSavedCardDao;
import com.byndl.avantimarket.event.AMDeleteStoredCardDao;
import com.byndl.avantimarket.event.AMEventType;
import com.byndl.avantimarket.event.AMGetAutoRechargeSettingsDao;
import com.byndl.avantimarket.event.AMGetBalanceDao;
import com.byndl.avantimarket.event.AMGetGatewayParametersDao;
import com.byndl.avantimarket.event.AMReloadDenominationsDao;
import com.byndl.avantimarket.event.AMScanCodesDao;
import com.byndl.avantimarket.event.AMSpecialsDao;
import com.byndl.avantimarket.event.AMSpecialsDao.SpecialsDao;
import com.byndl.avantimarket.event.AMStoredCardDao;
import com.byndl.avantimarket.event.AMStoredCardDao.CardDao;
import com.byndl.avantimarket.event.AMSupportPicklistDao;
import com.byndl.avantimarket.interfaces.ICreateCase;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetHttpInfo;
import com.incedo.network.NetHttpUtils;
import com.incedo.network.NetworkManagerClient;
import com.incedo.network.neterror.InvalidArgException;

/**
 * 
 * @brief This is the BASE class for all the screens shown after login; this has the common functionality
 *
 */
public abstract class BaseFragment extends Fragment {

	protected MyAvantiActivity parentActivity;
	protected final String TAG = "AvantiMarket";
	protected InputMethodManager inputManager = null;

	protected static ArrayList<CardDao> mALStoredCards = null;
	public static ArrayList<SpecialsDao> mALSpecials = null;
	public static ArrayList<SpecialsDao> mALMyProgress = null;
	
	/**
	 * Invokes Network module and executes GET_STORED_CARDS request
	 */
	public void apiGetStoredCards() {
		// call loading dialog
		InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_STORED_CARDS)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_STORED_CARDS,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMStoredCardDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_STORED_CARDS);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_BALANCE request
	 */
	public void apiGetBalance(Boolean showLoadingDialog) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_BALANCE)) {
			try {
				Log.d("AvantiMarket", "getBalance()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_BALANCE, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMGetBalanceDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_BALANCE);
				if(showLoadingDialog) parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_SCAN_CODES request
	 */
	public void apiGetScanCodes(boolean showLoadingFlag) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_SCAN_CODES)) {
			try {
				Log.d("AvantiMarket", "getScanCodes()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_SCAN_CODES, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMScanCodesDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_SCAN_CODES);
				if(showLoadingFlag) parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
		else {
			if( ! parentActivity.isLoadingDialogShowing()) {
				parentActivity.showLoadingDialog();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_CONSUMER_CATEGORIES request
	 */
	public void apiGetConsumerCategories() {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_CONSUMER_CATEGORIES)) {
			String operatorId = AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", "");
			try {
				Log.d("AvantiMarket", "getConsumerCategories()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_CONSUMER_CATEGORIES, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMSupportPicklistDao(operatorId, null));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_CATEGORIES);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_GET_CONSUMER_CATEGORIES) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_GET_CONSUMER_CATEGORIES;
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Invokes Network module and executes GET_CONSUMER_ISSUES request
	 */
	public void apiGetConsumerIssues(String aCategory) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_CONSUMER_ISSUES)) {
			String operatorId = AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", "");
			try {
				Log.d("AvantiMarket", "getConsumerIssues()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_CONSUMER_ISSUES, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMSupportPicklistDao(operatorId, aCategory));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_ISSUES);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_GET_CONSUMER_ISSUES) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_GET_CONSUMER_ISSUES;
				parentActivity.currentRequestArguments = new String[1];
				parentActivity.currentRequestArguments[0] = aCategory;
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Invokes Network module and executes GET_FEEDBACK_CATEGORIES request
	 */
	public void apiGetFeedbackCategories() {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_FEEDBACK_CATEGORIES)) {
			try {
				Log.d("AvantiMarket", "getFeedbackCategories()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_FEEDBACK_CATEGORIES, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMSupportPicklistDao(null, null));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_FEEDBACK_CATEGORIES);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_GET_FEEDBACK_CATEGORIES) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_GET_FEEDBACK_CATEGORIES;
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Invokes Network module and executes GET_ARTICLE_LINK request
	 */
	public void apiGetArticleLink(String listType, String keyword) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_ARTICLE_LINK)) {
			try {
				Log.d("AvantiMarket", "apiGetArticleLink()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_ARTICLE_LINK, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMArticleListDao(listType, keyword));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_ARTICLE_LINK);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_GET_ARTICLE_LINK ||
						(parentActivity.currentRequestArguments != null &&
								parentActivity.currentRequestArguments[0] != listType)) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_GET_ARTICLE_LINK;
				parentActivity.currentRequestArguments = new String[2];
				parentActivity.currentRequestArguments[0] = listType;
				parentActivity.currentRequestArguments[1] = keyword;
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}

	
	/**
	 * Invokes Network module and executes GET_ARTICLE request
	 */
	public void apiGetArticle(String knowledgeArticleId) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_ARTICLE)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_ARTICLE,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMArticleDetailsDao(knowledgeArticleId));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_ARTICLE);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_GET_ARTICLE ||
						(parentActivity.currentRequestArguments != null &&
						parentActivity.currentRequestArguments[0] != knowledgeArticleId)) {
					parentActivity.numberOfRepeatitiveCalls = 0;
				}
				parentActivity.currentRequest = AMEventType.EVENT_GET_ARTICLE;
				parentActivity.currentRequestArguments = new String[1];
				parentActivity.currentRequestArguments[0] = knowledgeArticleId;
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void apiCaseConsumerIssues(String consumerAmsId, String category, String issue, String desc, ICreateCase createCase)
	{
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_POST_CONSUMER_ISSUE)) {
			parentActivity.setCreateCaseListner(createCase);
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_POST_CONSUMER_ISSUE,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMCaseConsumerIssuesDao(consumerAmsId, category, issue, desc));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_POST_CONSUMER_ISSUE);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_POST_CONSUMER_ISSUE) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_POST_CONSUMER_ISSUE;
				parentActivity.currentRequestArguments = new Object[5];
				parentActivity.currentRequestArguments[0] = consumerAmsId;
				parentActivity.currentRequestArguments[1] = category;
				parentActivity.currentRequestArguments[2] = issue;
				parentActivity.currentRequestArguments[3] = desc;
				parentActivity.currentRequestArguments[4] = createCase;
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void apiCaseCreateFeedback(String consumerAmsId, String category, String desc, ICreateCase createCase)
	{
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_POST_FEEDBACK)) {
			parentActivity.setCreateCaseListner(createCase);
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_POST_FEEDBACK,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMCaseCreateFeedbackDao(consumerAmsId, category, desc));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_POST_FEEDBACK);
				parentActivity.showLoadingDialog();
				if(parentActivity.currentRequest != AMEventType.EVENT_POST_FEEDBACK) parentActivity.numberOfRepeatitiveCalls = 0;
				parentActivity.currentRequest = AMEventType.EVENT_POST_FEEDBACK;
				parentActivity.currentRequestArguments = new Object[4];
				parentActivity.currentRequestArguments[0] = consumerAmsId;
				parentActivity.currentRequestArguments[1] = category;
				parentActivity.currentRequestArguments[2] = desc;
				parentActivity.currentRequestArguments[3] = createCase;
			} catch (InvalidArgException e) {
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * Invokes Network module and executes CHARGE_STORED_CARD request
	 */
	public void apiChargeCard(String selectedDenomination, boolean isAuto){
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_CHARGE_STORED_CARD)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.POST,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_CHARGE_STORED_CARD,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMChargeStoredCardDao(AMConstants.PRIMERY_CARD, selectedDenomination, isAuto));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_CHARGE_STORED_CARD);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_AUTORECHARGE_SETTINGS request
	 */
	public void apiGetAutoRechargeSettings() {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_AUTORECHARGE_SETTINGS)) {
			try {
				Log.d("AvantiMarket", "apiGetAutoRechargeSettings()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_AUTORECHARGE_SETTINGS, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMGetAutoRechargeSettingsDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_AUTORECHARGE_SETTINGS);
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_GATEWAY_PARAMETERS request
	 */
	public void apiGetGatewayParameters(String selectedDenomination) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_GATEWAY_PARAMETERS)) {
			try {
				Log.d("AvantiMarket", "apiGetGatewayParameters()");
				(new NetworkManagerClient(new NetHttpInfo(parentActivity, 
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS, 
						AMEventType.EVENT_GET_GATEWAY_PARAMETERS, 
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(), 
						parentActivity)).execute(new AMGetGatewayParametersDao(selectedDenomination));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_GATEWAY_PARAMETERS);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Invokes Network module and executes DELETE_STORED_CARD request
	 */
	public void apiDeleteStoredCard(String aCardId){
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_DELETE_STORED_CARD)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.DELETE,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_DELETE_STORED_CARD,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMDeleteStoredCardDao(aCardId));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_DELETE_STORED_CARD);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes DELETE_STORED_CARD request
	 */
	public void apiDeleteLastSavedCard(){
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_DELETE_STORED_CARD)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.DELETE,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_DELETE_STORED_CARD,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMDeleteLastSavedCardDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_DELETE_STORED_CARD);
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_RELOAD_DENOMINATIONS request
	 */
	public void apiGetReloadDenomination(){
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_RELOAD_DENOMINATIONS)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_RELOAD_DENOMINATIONS,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMReloadDenominationsDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_RELOAD_DENOMINATIONS);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves the "Credit Cards" info to a class member that can be accessed across all derived classes
	 * @param storedCardDao
	 */
	public void setStoredCards(AMStoredCardDao storedCardDao) {
		Log.d("AvantiMarket", "setStoredCards");
		mALStoredCards = new ArrayList<CardDao>(storedCardDao.getCards());
		Log.d("AvantiMarket", "setStoredCards: "+mALStoredCards.size());
	}

	/**
	 * Clears the saved "Credit Cards" info
	 */
	public static void clearStoredCards() {
		mALStoredCards = null;
	}

	protected void showLastSavedCardDeleteConfirmationDialog(){
		final Dialog dialog = new Dialog(parentActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.two_button_dialog);
		dialog.setCancelable(true);

		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText("Please Confirm");
		TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		tvMsg.setText(R.string.wish_to_save);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_Yes);
		btnOk.setText(R.string.save);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				apiGetStoredCards();
			}
		});
		Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
		btnNo.setText(R.string.no_thanks);
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				apiDeleteLastSavedCard();
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	/**
	 * Updates the UI as per the latest info fetched from Server
	 */
	public abstract void updateUI();
	
	protected void saveToInternalStorage(String key, Set<String> values) {
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		editor.putStringSet(key, values);
		editor.commit();
	}

	/**
	 * Invokes Network module and executes ALL_SPECIALS request
	 */
	public void apiGetAllSpecials() {
//		InitNetwork.setNetworkInit(AMConstants.SPECIALS_URL, AMConstants.URL_PATH,
//				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE, 2403);
		InitNetwork.setNetworkInit(AMConstants.SPECIALS_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_SPECIALS)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTP,
						AMEventType.EVENT_GET_SPECIALS,
						NetHttpUtils.HttpResposeDataModel.JSON), parentActivity.getHandler(),
						parentActivity)).execute(new AMSpecialsDao());
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_SPECIALS);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
