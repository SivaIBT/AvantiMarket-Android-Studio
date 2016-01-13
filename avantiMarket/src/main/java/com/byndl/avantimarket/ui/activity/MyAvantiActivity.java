/**
 * @brief       This is the holder/container class for all the screens that appear after successful login
 * @file        MyAvantiActivity.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        01-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.event.AMArticleDetailsDao;
import com.byndl.avantimarket.event.AMArticleListDao;
import com.byndl.avantimarket.event.AMCaseConsumerIssuesDao;
import com.byndl.avantimarket.event.AMCaseCreateFeedbackDao;
import com.byndl.avantimarket.event.AMChargeStoredCardDao;
import com.byndl.avantimarket.event.AMEventType;
import com.byndl.avantimarket.event.AMGetBalanceDao;
import com.byndl.avantimarket.event.AMReloadDenominationsDao;
import com.byndl.avantimarket.event.AMScanCodesDao;
import com.byndl.avantimarket.event.AMSetUserPinDao;
import com.byndl.avantimarket.event.AMSpecialsDao;
import com.byndl.avantimarket.event.AMSpecialsDao.SpecialsDao;
import com.byndl.avantimarket.event.AMStoredCardDao;
import com.byndl.avantimarket.event.AMSupportPicklistDao;
import com.byndl.avantimarket.interfaces.ICreateCase;
import com.byndl.avantimarket.storage.AMPrefKeys;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.adapter.NavigationDrawerAdapter;
import com.byndl.avantimarket.ui.fragment.AboutUsFragment;
import com.byndl.avantimarket.ui.fragment.AddCreditCardFragment;
import com.byndl.avantimarket.ui.fragment.BaseFragment;
import com.byndl.avantimarket.ui.fragment.CommonIssues;
import com.byndl.avantimarket.ui.fragment.ContactUsDidyousee;
import com.byndl.avantimarket.ui.fragment.ContactUsFragment;
import com.byndl.avantimarket.ui.fragment.ContactUsThankyou;
import com.byndl.avantimarket.ui.fragment.CurrentDiscountFragment;
import com.byndl.avantimarket.ui.fragment.EditResetFragment;
import com.byndl.avantimarket.ui.fragment.FeedbackFragment;
import com.byndl.avantimarket.ui.fragment.FeedbackThankyou;
import com.byndl.avantimarket.ui.fragment.HelpfulHowToFragment;
import com.byndl.avantimarket.ui.fragment.HistoryFragment;
import com.byndl.avantimarket.ui.fragment.MarketCardsFragment;
import com.byndl.avantimarket.ui.fragment.MyAccountFragment;
import com.byndl.avantimarket.ui.fragment.MyMarketFragment;
import com.byndl.avantimarket.ui.fragment.PayFragment;
import com.byndl.avantimarket.ui.fragment.ReloadDenominations;
import com.byndl.avantimarket.ui.fragment.ReloadFragment;
import com.byndl.avantimarket.ui.fragment.RewardsFragement;
import com.byndl.avantimarket.ui.fragment.SetUpMarketCardAccountFragment;
import com.byndl.avantimarket.ui.fragment.SpecialsContainerFragment;
import com.byndl.avantimarket.ui.fragment.SupportFragment;
import com.byndl.avantimarket.ui.fragment.UpdateMarketCardFragment;
import com.byndl.avantimarket.ui.fragment.UsingTheApp;
import com.byndl.avantimarket.ui.fragment.UsingTheMarket;
import com.byndl.avantimarket.ui.fragment.WaysToPayFragment;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetHttpInfo;
import com.incedo.network.NetHttpUtils;
import com.incedo.network.NetworkManagerClient;
import com.incedo.network.neterror.ApiErrorObj;
import com.incedo.network.neterror.InvalidArgException;

/**
 * 
 * @brief This is the holder/container class for all the screens that appear after successful login
 *
 */
public class MyAvantiActivity extends BaseActivity {

	private final String TAG = "AvantiMarket";
	private DrawerLayout m_drawer;
	private LinearLayout m_drawerListContainer;
	private ListView m_drawerList;
	private ActionBarDrawerToggle m_drawerToggle;
	private MyMarketFragment myAvantiFragment = null;
	private WaysToPayFragment waysToPayFragment = null;
	private MarketCardsFragment marketCardsFragment = null;
	private PayFragment payFragment = null;
	private ReloadFragment reloadFragment = null;
	private ReloadDenominations reloadDenominationsFragment = null;
	private AddCreditCardFragment addCreditCardFragment = null;
	private UpdateMarketCardFragment updateMarketCardFragment = null;
	private SupportFragment supportFragment = null;
	private HistoryFragment historyFragment = null;
	private MyAccountFragment myAccountFragment = null;
	private SpecialsContainerFragment myMarketSpecialsFragment = null;
	private AboutUsFragment aboutUsFragment = null;
	private EditResetFragment editResetFragment = null;
	private FragmentTransaction transaction = null;
	private Button signOutButton = null, myMarketSpecialsButton = null, aboutUs = null;
	private ImageView logoInMenu;
	private boolean isMenuLaunch = false;
	public String primaryScanCode = "";
	private boolean isTimerRepeat = false;

	private static String[] menu_items = { "PAY", "WAYS TO PAY",
		"ACCOUNT HISTORY", "MY ACCOUNT", "SUPPORT", "OFFERS" };

	private HelpfulHowToFragment mHelpfulHowToFragment = null;
	private UsingTheMarket mUsingTheMarket = null;
	private UsingTheApp mUsingTheApp = null;
	private CommonIssues mCommonIssues = null;
	private ContactUsFragment mContactUsFragment = null;
	private ContactUsDidyousee mContactUsDidyousee = null;
	private ContactUsThankyou mContactUsThankyou = null;
	private FeedbackFragment mFeedbackFragment = null;
	private FeedbackThankyou mFeedbackThankyou = null;
	private RewardsFragement mRewardsFragement = null;
	private CurrentDiscountFragment mCurrentDiscountFragment = null;
	private SetUpMarketCardAccountFragment mSetUpMarketCardAccountFragment = null;
	
	private Timer timer;
	private TimerTask timerTask;

	final Handler timerHandler = new Handler();
	private ICreateCase mCreateCase;
	private static MyAvantiActivity instance = null;
	/**
	 * Will be forwarded to Network module to receive callbacks
	 */
	Handler myAvantiHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what != AMEventType.EVENT_GET_BALANCE) {
				dismissLoadingDialog();
			}
			
			if(msg.what == AMEventType.EVENT_GET_BALANCE) {
				List<Fragment> fragments = getSupportFragmentManager().getFragments();
				for (Fragment fragment : fragments) {
					if (fragment != null && fragment.isVisible() && fragment.toString().contains("MyMarketFragment")) {
						dismissLoadingDialog();
					}
				}
			}
			
			if(msg.obj.toString().contains("ApiErrorObj") &&
					((ApiErrorObj) msg.obj).getErrorMsg().contains("Unauthorized")) {
				previousSalesForceToken = AMUtility.SALES_FORCE_TOKEN;
				apiGetSalesForceToken();
			}
			
			Log.d(TAG, "MyAvantiActivity HANDLER");
			switch (msg.what) {
			case AMEventType.EVENT_GET_BALANCE:
				removeFromRequestsInProgress(AMEventType.EVENT_GET_BALANCE);
				if (msg.obj.toString().contains("ApiErrorObj"))
					Log.d(TAG, "In Handler:AMEventType.GET_BALANCE: "
							+ ((ApiErrorObj) msg.obj).getErrorCode() + " & "
							+ ((ApiErrorObj) msg.obj).getErrorMsg());
				else {
					if (((AMGetBalanceDao) msg.obj).getJsonResponse() != null) {
						saveBalanceAndNotifyFragment(msg.obj);
					} else {
						Log.d(TAG, "Handler EVENT_GET_BALANCE Response is NULL");
					}
				}
				break;

			case AMEventType.EVENT_GET_SCAN_CODES:
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_SCAN_CODES");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_SCAN_CODES);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					Set<String> scanCodeStrings = ((AMScanCodesDao) msg.obj).mALScanCodes;
					if (scanCodeStrings != null && (scanCodeStrings.size() > 0)) {
						Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
						editor.putStringSet("scanCodes", scanCodeStrings);
						editor.commit();
						List<Fragment> fragments = getSupportFragmentManager()
								.getFragments();
						for (Fragment fragment : fragments) {
							if (fragment != null && fragment.isVisible()) {
								((BaseFragment) fragment).updateUI();
							}
						}
						setNameAndColorToScanCodes();
					} else {
						Log.d(TAG,
								"Handler EVENT_GET_SCAN_CODES Response is NULL");
					}
				}
				dismissLoadingDialog();
				break;

			case AMEventType.EVENT_GET_CONSUMER_CATEGORIES:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_CONSUMER_CATEGORIES");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_CATEGORIES);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					Log.d(TAG, "SUCCESS for EVENT_GET_CONSUMER_CATEGORIES");
					AMUtility.sfConsumerCategories = new ArrayList<String>(((AMSupportPicklistDao) msg.obj).mALSupportPicklist);
					AMUtility.updateConsumerCategories = true;
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment) fragment).updateUI();
						}
					}
				}
				dismissLoadingDialog();
				break;
				
			case AMEventType.EVENT_GET_CONSUMER_ISSUES:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_CONSUMER_ISSUES");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_CONSUMER_ISSUES);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					Log.d(TAG, "SUCCESS for EVENT_GET_CONSUMER_ISSUES");
					AMUtility.sfConsumerIssues = new ArrayList<String>(((AMSupportPicklistDao) msg.obj).mALSupportPicklist);
					AMUtility.updateConsumerIssues = true;
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment) fragment).updateUI();
						}
					}
				}
				dismissLoadingDialog();
				break;
				
			case AMEventType.EVENT_GET_FEEDBACK_CATEGORIES:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_FEEDBACK_CATEGORIES");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_FEEDBACK_CATEGORIES);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					Log.d(TAG, "SUCCESS for EVENT_GET_FEEDBACK_CATEGORIES");
					AMUtility.sfFeedbackCategories = new ArrayList<String>(((AMSupportPicklistDao) msg.obj).mALSupportPicklist);
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment) fragment).updateUI();
						}
					}
				}
				dismissLoadingDialog();
				break;

			case AMEventType.EVENT_GET_ARTICLE_LINK:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_ARTICLE_LINK");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_ARTICLE_LINK);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible() && fragment.toString().contains("CommonIssues")) {
							MixPanelEvents.eventFailed(msg, "Article Search Fail");
						}
					}
				} else {
					Log.d(TAG, "SUCCESS for EVENT_GET_ARTICLE_LINK");

					AMArticleListDao articleDao = (AMArticleListDao) msg.obj;
					AMUtility.sfArticleArray = articleDao.getArticleArray();

					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment) fragment).updateUI();
						}
					}
					
					if(articleDao.mSearchKeyword != null) {
						if(articleDao.mListType.contains("UsingTheMarket")) {
							MixPanelEvents.eventSuccess(msg, "Article Search Success");
						}
						else if(articleDao.mListType.contains("UsingTheApp")) {
							MixPanelEvents.eventSuccess(msg, "Article Search Success");
						}
					}
				}
				dismissLoadingDialog();
				break;
				
			case AMEventType.EVENT_GET_ARTICLE:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_GET_ARTICLE");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_ARTICLE);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					Log.d(TAG, "SUCCESS for EVENT_GET_ARTICLE");
					/////////////////////////////////////
					AMUtility.sfAtricleDetails = (AMArticleDetailsDao) msg.obj;
					/////////////////////////////////////
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment) fragment).updateUI();
						}
					}
				}
				dismissLoadingDialog();
				break;
				
			case AMEventType.EVENT_POST_CONSUMER_ISSUE:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_POST_CONSUMER_ISSUE");
				removeFromRequestsInProgress(AMEventType.EVENT_POST_CONSUMER_ISSUE);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
					MixPanelEvents.eventFailed(msg, "ContactUs Case Register Fail");
				} else {
					Log.d(TAG, "SUCCESS for EVENT_POST_CONSUMER_ISSUE");
//					AMUtility.sfAtricleDetails = (AMCaseConsumerIssuesDao) msg.obj;
					String caseSFDCId = ((AMCaseConsumerIssuesDao) msg.obj).mCaseSFDCId;
					mCreateCase.OnCaseCreated(caseSFDCId);
					MixPanelEvents.eventSuccess(msg, "ContactUs Case Register Success");
				}
				dismissLoadingDialog();
				break;
				
				
			case AMEventType.EVENT_POST_FEEDBACK:
				InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				Log.d(TAG, "In Handler:AMEventType.EVENT_POST_FEEDBACK");
				removeFromRequestsInProgress(AMEventType.EVENT_POST_FEEDBACK);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
					MixPanelEvents.eventFailed(msg, "Submit Feedback Fail");
				} else {
					Log.d(TAG, "SUCCESS for EVENT_POST_FEEDBACK");
					String caseSFDCId = ((AMCaseCreateFeedbackDao) msg.obj).mCaseSFDCId;
					mCreateCase.OnCaseCreated(caseSFDCId);
					MixPanelEvents.eventSuccess(msg, "Submit Feedback Success");
				}
				dismissLoadingDialog();
				break;
				
			case AMEventType.EVENT_GET_STORED_CARDS:
				Log.d(TAG, "Handler EVENT_GET_STORED_CARDS");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_STORED_CARDS);
				Object obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					AMStoredCardDao mStoredCardDao = (AMStoredCardDao) obj;
					Log.d(TAG,
							"Handler EVENT_GET_STORED_CARDS result array lenght: "
									+ mStoredCardDao.getCards().size());
					List<Fragment> fragments = getSupportFragmentManager()
							.getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible() && 
								AMApplication.isActivityVisible()) {
							((BaseFragment) fragment).setStoredCards(mStoredCardDao);
							if(mStoredCardDao.getCards().size() > 0) {
								if(fragment.toString().contains("MyMarketFragment") ||
										isMenuLaunch) {
									getWaysToPayFragment().setArguments(null);
									FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
									transaction.replace(R.id.fragment_block, getWaysToPayFragment(), getWaysToPayFragment().getTag());
									transaction.commit();
								}
								else if(fragment.toString().contains("MarketCardsFragment")) {
									((MarketCardsFragment) fragment).storedCardsReceived(obj);
								}
								else {
									((BaseFragment) fragment).updateUI();
								}
							}
							else if(mStoredCardDao.getCards().size() == 0 && 
									(fragment.toString().contains("WaysToPayFragment") ||
											fragment.toString().contains("ReloadFragment"))) {
								((BaseFragment) fragment).updateUI();
							}
							else {
								Bundle args = new Bundle();
								args.putString("previousFragment", fragment.toString());
								args.putBoolean("isMenuLaunch", isMenuLaunch);
								args.putBoolean("isZeroCardsLaunch", true);
								getReloadDenominationsFragment().setArguments(args);
								FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
								transaction.replace(R.id.fragment_block, getReloadDenominationsFragment(), getReloadDenominationsFragment().getTag());
								transaction.commit();
							}
						}
					}
				}
				isMenuLaunch = false;

				break;

			case AMEventType.EVENT_CHARGE_STORED_CARD:
				removeFromRequestsInProgress(AMEventType.EVENT_CHARGE_STORED_CARD);
				obj = msg.obj;
				String result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					if (((ApiErrorObj) msg.obj).getErrorMsg()
							.equalsIgnoreCase("WebException")){
						
					}
					showNetworkErrorDialog(msg);
					MixPanelEvents.eventFailed(msg, "Reload Fail");
				} else {
					AMChargeStoredCardDao mChargeStoredCardDao = (AMChargeStoredCardDao) obj;
					if(mChargeStoredCardDao.isChargedSuccess) {
						result = "Your reload was successful!";
						apiGetBalance();
						showResultDialog(result);
						MixPanelEvents.eventSuccess(msg, "Reload Success");
					}
				}
				break;

			case AMEventType.EVENT_SET_USER_PIN:
				removeFromRequestsInProgress(AMEventType.EVENT_SET_USER_PIN);
				obj = msg.obj;
				result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					AMSetUserPinDao mSetUserPinDao = (AMSetUserPinDao) obj;
					if(mSetUserPinDao.isSuccess) {
						Log.d("SetUserPin Success : ", "true");
						result = "SetUserPin Success";
						showResultDialog(result);
					}
				}
				break;

			case AMEventType.EVENT_GET_AUTORECHARGE_SETTINGS:
				removeFromRequestsInProgress(AMEventType.EVENT_GET_AUTORECHARGE_SETTINGS);
				obj = msg.obj;
				result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					List<Fragment> fragments = getSupportFragmentManager().getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible() && fragment.toString().contains("MarketCards")) {
							((MarketCardsFragment) fragment).autoRechargeSettingsReceived(obj);
						}
					}
				}
				break;

			case AMEventType.EVENT_GET_GATEWAY_PARAMETERS:
				removeFromRequestsInProgress(AMEventType.EVENT_GET_GATEWAY_PARAMETERS);
				obj = msg.obj;
				result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					List<Fragment> fragments = getSupportFragmentManager().getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible() && fragment.toString().contains("ReloadDenominations")) {
							((ReloadDenominations)fragment).loadBankURL(obj);
						}
						else if(fragment != null && fragment.isVisible()){
							
						}
					}
				}
				break;

			case AMEventType.EVENT_GET_RELOAD_DENOMINATIONS:
				removeFromRequestsInProgress(AMEventType.EVENT_GET_RELOAD_DENOMINATIONS);
				obj = msg.obj;
				result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
					AMReloadDenominationsDao mReloadDenominationsDao = (AMReloadDenominationsDao) obj;
					
					// Save Denominations and Bonuses to Shared Preferences
					Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
					editor.putStringSet("reloadDenominations", (Set<String>) mReloadDenominationsDao.mALDenominations);
					for(int i=0; i<mReloadDenominationsDao.mALDenominations.size(); i++) {
						
						if(mReloadDenominationsDao.bonusHashMap.get(mReloadDenominationsDao.mALDenominations.toArray()[i])
								!= null) {
							editor.putInt(""+mReloadDenominationsDao.mALDenominations.toArray()[i], 
									mReloadDenominationsDao.bonusHashMap.get(mReloadDenominationsDao.mALDenominations.toArray()[i]));
						} 
						else {
							editor.putInt(""+mReloadDenominationsDao.mALDenominations.toArray()[i], 0);
						}
					}
					editor.commit();
					
					// Notify relevant fragment
					List<Fragment> fragments = getSupportFragmentManager().getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							if(fragment.toString().contains("ReloadDenominations")) {
								((ReloadDenominations)fragment).updateDenominationsAndBonuses(obj);
							}
							if(fragment.toString().contains("ReloadFragment")) {
								((ReloadFragment)fragment).updateDenominationsAndBonuses(obj);
							}
						}
					}
					
				}
				break;

			case AMEventType.EVENT_DELETE_STORED_CARD:
				removeFromRequestsInProgress(AMEventType.EVENT_DELETE_STORED_CARD);
				obj = msg.obj;
				result = "";
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
				} else {
//					showResultDialog("Deletion Success");
					List<Fragment> fragments = getSupportFragmentManager().getFragments();
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible()) {
							((BaseFragment)fragment).apiGetStoredCards();
						}
					}
				}
				break;

			case AMEventType.EVENT_POST_TEST:
				Log.d(TAG, "In Handler:AMEventType.EVENT_POST_TEST");
				break;

			case AMEventType.EVENT_GET_SPECIALS:
				Log.d(TAG, "Handler EVENT_GET_SPECIALS");
				removeFromRequestsInProgress(AMEventType.EVENT_GET_SPECIALS);
				obj = msg.obj;
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					showNetworkErrorDialog(msg);
					
					// Temp code for providing Specials Data till API is available ++
					
				    StringBuilder buf=new StringBuilder();
				    InputStream json;
				    try {
				    	json = getAssets().open("specials.txt");
				    	BufferedReader in=
				    			new BufferedReader(new InputStreamReader(json, "UTF-8"));
				    	String str;

				    	while ((str=in.readLine()) != null) {
				    		buf.append(str);
				    	}

				    	in.close();					
					    AMSpecialsDao mSpecialsDao = new AMSpecialsDao();
					    mSpecialsDao.setJSONValues(new JSONObject(buf.toString()));
					    informMainThread(mSpecialsDao);
				    } catch (Exception e) {
						Log.e("temp", "SpecialException: "+e.getLocalizedMessage());
					}
//				    Log.e("Specials Data: ", buf.toString());
					// Temp code for providing Specials Data till API is available --
				    
				} else {
					informMainThread(obj);
				}

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.main_activity_screen_layout);

		m_drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		m_drawerListContainer = (LinearLayout) findViewById(R.id.left_drawer);
		m_drawerListContainer.setClickable(true);
		m_drawerList = (ListView) findViewById(R.id.left_drawer_list);

		setUpListeners();
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.nav_bar_layout);

		transaction = getSupportFragmentManager().beginTransaction();
		myAvantiFragment = new MyMarketFragment();
		transaction.replace(R.id.fragment_block, myAvantiFragment,
				myAvantiFragment.getTag());
		transaction.commit();
		
		WebView.setWebContentsDebuggingEnabled(true);
		instance = this;
	}

	/**
	 * Defines Action to be taken when a Menu item is selected
	 * @param sender
	 * @param position Position of the menu item selected/clicked 
	 */
	private void OnDrawerListItemClicked(Object sender, int position) {
		isMenuLaunch = false;
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		m_drawerList.setItemChecked(position, true);
		m_drawer.closeDrawer(m_drawerListContainer);
		switch (position) {
		case 0:
			transaction.replace(R.id.fragment_block, getMarketCardsFragment(),
					getMarketCardsFragment().getTag());
			transaction.commit();
			break;

		case 1:
			if(getWaysToPayFragment().isVisible()) return;
			
			isMenuLaunch = true;
			List<Fragment> fragments = getSupportFragmentManager()
					.getFragments();
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isVisible()) {
					((BaseFragment)fragment).apiGetStoredCards();
				}
			}
//			getWaysToPayFragment().setArguments(null);
//			transaction.replace(R.id.fragment_block, getWaysToPayFragment(),
//					getWaysToPayFragment().getTag());
//			transaction.commit();
			break;

		case 2:
			transaction.replace(R.id.fragment_block, getHistoryFragment(),
					getHistoryFragment().getTag());
			transaction.commit();
			break;

		case 3:
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_block, getMyAccountFragment(),
					getMyAccountFragment().getTag());
			transaction.commit();
			break;

		case 4:
			if(AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("isScuFetched", false)) {
				transaction.replace(R.id.fragment_block, getSupportFragment(),
						getSupportFragment().getTag());
				transaction.commit();
			}
			else {
				String operatorId = AMPreferenceManager.getPrefInstance().getPrefs().getString("operatorId", "53901");
				apiGetOperatorInfoUsingOperatorId(operatorId, true);
			}
			
			break;

		case 5:
			List<Fragment> fragmentsList = getSupportFragmentManager()
			.getFragments();
			for (Fragment fragment : fragmentsList) {
				if (fragment != null && fragment.isVisible()) {
					((BaseFragment)fragment).apiGetAllSpecials();
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		m_drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (m_drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "In MyAvantiActivity::onResume");
		super.onResume();
		startTimer(true);
		primaryScanCode = AMPreferenceManager.getPrefInstance().getPrefs().getString("primaryScanCode", null);
		AMApplication.activityResumed();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		m_drawerToggle.syncState();
	}

	/**
	 * Get method for Home Screen after successful login
	 * @return Instance of Home Screen class
	 */
	public MyMarketFragment getMyAvantiFragment() {
		if (myAvantiFragment == null)
			myAvantiFragment = new MyMarketFragment();

		return myAvantiFragment;
	}

	/**
	 * Get method for "Credit/Debit Cards" Screen
	 * @return Instance of Credit/Debit Cards Screen class
	 */
	public WaysToPayFragment getWaysToPayFragment() {
		if (waysToPayFragment == null)
			waysToPayFragment = new WaysToPayFragment();

		return waysToPayFragment;
	}

	/**
	 * Get method for "Market Cards" Screen
	 * @return Instance of Market Cards Screen class
	 */
	public MarketCardsFragment getMarketCardsFragment() {
		if (marketCardsFragment == null)
			marketCardsFragment = new MarketCardsFragment();

		return marketCardsFragment;
	}

	/**
	 * Get method for "Scan Code Display" Screen
	 * @return Instance of Scan Code Screen class
	 */
	public PayFragment getPayFragment() {
		if (payFragment == null)
			payFragment = new PayFragment();

		return payFragment;
	}

	/**
	 * Get method for Reload Screen
	 * @return Instance of Reload Screen class
	 */
	public ReloadFragment getReloadFragment() {
		if (reloadFragment == null)
			reloadFragment = new ReloadFragment();

		return reloadFragment;
	}

	/**
	 * Get method for Reload Denominations Screen
	 * @return Instance of Reload Denominations Screen
	 */
	public ReloadDenominations getReloadDenominationsFragment() {
		if (reloadDenominationsFragment == null)
			reloadDenominationsFragment = new ReloadDenominations();

		return reloadDenominationsFragment;
	}

	/**
	 * Get method for "Add CC" Screen
	 * @return Instance of Add CC Screen class
	 */
	public AddCreditCardFragment getAddCreditCardFragment() {
		if (addCreditCardFragment == null)
			addCreditCardFragment = new AddCreditCardFragment();

		return addCreditCardFragment;
	}

	/**
	 * Get method for "Update Market Card" Screen
	 * @return Instance of Update Market Card Screen class
	 */
	public UpdateMarketCardFragment getUpdateMarketCardFragment() {
		if (updateMarketCardFragment == null)
			updateMarketCardFragment = new UpdateMarketCardFragment();

		return updateMarketCardFragment;
	}

	/**
	 * Get method for Support Screen 
	 * @return Instance of Support Screen class
	 */
	public SupportFragment getSupportFragment() {
		if (supportFragment == null)
			supportFragment = new SupportFragment();

		return supportFragment;
	}

	/**
	 * Get method for History Screen 
	 * @return Instance of History Screen class
	 */
	public HistoryFragment getHistoryFragment() {
		if (historyFragment == null)
			historyFragment = new HistoryFragment();

		return historyFragment;
	}


	/**
	 * Get method for MyAccount Screen 
	 * @return Instance of MyAccount Screen class
	 */
	public MyAccountFragment getMyAccountFragment() {
		if (myAccountFragment == null)
			myAccountFragment = new MyAccountFragment();

		return myAccountFragment;
	}

	/**
	 * Get method for MyMarketSpecials Screen 
	 * @return Instance of MyMarketSpecials Screen
	 */
	public SpecialsContainerFragment getMyMarketSpecialsFragment() {
		if (myMarketSpecialsFragment == null)
			myMarketSpecialsFragment = new SpecialsContainerFragment();

		return myMarketSpecialsFragment;
	}

	/**
	 * Get method for AboutUs Screen 
	 * @return Instance of AboutUs Screen class
	 */
	public AboutUsFragment getAboutUsFragment() {
		if (aboutUsFragment == null)
			aboutUsFragment = new AboutUsFragment();

		return aboutUsFragment;
	}

	/**
	 * Get method for EditReset Screen 
	 * @return Instance of EditReset Screen class
	 */
	public EditResetFragment getEditResetFragment() {
		if (editResetFragment == null)
			editResetFragment = new EditResetFragment();

		return editResetFragment;
	}

	/**
	 * Get method for "Helpful How To" Screen
	 * @return Instance of "Helpful How To" Screen class
	 */
	public HelpfulHowToFragment getHelpfulHowToFragment() {
		if (mHelpfulHowToFragment == null)
			mHelpfulHowToFragment = new HelpfulHowToFragment();

		return mHelpfulHowToFragment;
	}

	/**
	 * Get method for "Using The Market Card" Screen
	 * @return Instance of "Using The Market Card" Screen class
	 */
	public UsingTheMarket getUsingTheMarketFragment() {
		if (mUsingTheMarket == null)
			mUsingTheMarket = new UsingTheMarket();

		return mUsingTheMarket;
	}

	/**
	 * Get method for "Using the App" Screen
	 * @return Instance of "Using the App" Screen class
	 */
	public UsingTheApp getUsingTheAppFragment() {
		if (mUsingTheApp == null)
			mUsingTheApp = new UsingTheApp();

		return mUsingTheApp;
	}

	/**
	 * Get method for "Common Issues" Screen
	 * @return Instance of "Common Issues" Screen class
	 */
	public CommonIssues getCommonIssuesFragment() {
		if (mCommonIssues == null)
			mCommonIssues = new CommonIssues();

		return mCommonIssues;
	}

	/**
	 * Get method for "Contact Us" Screen
	 * @return Instance of "Contact Us" Screen class
	 */
	public ContactUsFragment getContactUsFragment() {
		if (mContactUsFragment == null)
			mContactUsFragment = new ContactUsFragment();

		return mContactUsFragment;
	}

	/**
	 * Get method for "Contact Us: Did you see" Screen
	 * @return Instance of "Contact Us: Did you see" Screen class
	 */
	public ContactUsDidyousee getContactUsDidyousee() {
		if (mContactUsDidyousee == null)
			mContactUsDidyousee = new ContactUsDidyousee();

		return mContactUsDidyousee;
	}

	/**
	 * Get method for "Contact Us: Thank you" Screen
	 * @return Instance of "Contact Us: Thank you" Screen class
	 */
	public ContactUsThankyou getContactUsThankyou() {
		if (mContactUsThankyou == null)
			mContactUsThankyou = new ContactUsThankyou();

		return mContactUsThankyou;
	}

	/**
	 * Get method for "Feedback" Screen
	 * @return Instance of "Feedback" Screen class
	 */
	public FeedbackFragment getFeedbackFragment() {
		if (mFeedbackFragment == null)
			mFeedbackFragment = new FeedbackFragment();

		return mFeedbackFragment;
	}

	/**
	 * Get method for "Feedback: Thank you" Screen
	 * @return Instance of "Feedback: Thank you" Screen class
	 */
	public FeedbackThankyou getFeedbackThankyou() {
		if (mFeedbackThankyou == null)
			mFeedbackThankyou = new FeedbackThankyou();

		return mFeedbackThankyou;
	}

	/**
	 * Get method for Rewards Screen
	 * @return Instance of Rewards Screen class
	 */
	public RewardsFragement getRewardsFragement()
	{
		if(mRewardsFragement == null)		{
			mRewardsFragement = new RewardsFragement();
		}

		return mRewardsFragement;
	}

	/**
	 * Get method for "Current Discount" Screen
	 * @return Instance of "Current Discount" Screen class
	 */
	public CurrentDiscountFragment getCurrentDiscountFragment()
	{
		if(mCurrentDiscountFragment == null)		{
			mCurrentDiscountFragment = new CurrentDiscountFragment();
		}

		return mCurrentDiscountFragment;
	}

	/**
	 * Get method for "Set Up MarketCard Account" Screen
	 * @return Instance of "Set Up MarketCard Account" Screen class
	 */
	public SetUpMarketCardAccountFragment getSetUpMarketCardAccountFragment()
	{
		if(mSetUpMarketCardAccountFragment == null)		{
			mSetUpMarketCardAccountFragment = new SetUpMarketCardAccountFragment();
		}

		return mSetUpMarketCardAccountFragment;
	}

	/**
	 * Sets menu icon and listener
	 */
	public void setMenuDrawer() {
		ImageButton menuButton = (ImageButton) findViewById(R.id.m_menuButton);
		menuButton.setImageResource(R.drawable.menu);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!m_drawer.isDrawerOpen(m_drawerListContainer)) {
					m_drawer.openDrawer(m_drawerListContainer);
				} else {
					m_drawer.closeDrawers();
				}
			}
		});
	}

	/**
	 * Sets up listeners to accept/respond to user events
	 */
	private void setUpListeners() {
		m_drawerList.setAdapter(new NavigationDrawerAdapter(this,
				R.layout.item_menu, menu_items));
		m_drawerList
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OnDrawerListItemClicked(null, position);
			}
		});
		m_drawerList.setDivider(null);

		m_drawerToggle = new ActionBarDrawerToggle(this, m_drawer,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); 
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); 
			}
		};

		m_drawer.setDrawerListener(m_drawerToggle);

/*		myMarketSpecialsButton = (Button) findViewById(R.id.my_market_specials);
		myMarketSpecialsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_drawer.closeDrawer(m_drawerListContainer);
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment fragment : fragments) {
					if (fragment != null && fragment.isVisible()) {
						((BaseFragment)fragment).apiGetAllSpecials();
					}
				}
			}
		});	
*/
		aboutUs = (Button) findViewById(R.id.m_about_us);
		aboutUs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_drawer.closeDrawer(m_drawerListContainer);
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, getAboutUsFragment(),
						getAboutUsFragment().getTag());
				transaction.commit();
			}
		});	

		signOutButton = (Button) findViewById(R.id.m_sign_out);
		signOutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismissLoadingDialog();
				mLoadingDialog = null;
				clearInternalStoredContent(true);
				final Intent intent = new Intent(getApplicationContext(),
						StartActivity.class);
				startActivity(intent);
				finish();
			}
		});

		logoInMenu = (ImageView) findViewById(R.id.m_logoImageView);
		logoInMenu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_drawer.closeDrawer(m_drawerListContainer);
				launchMyAvanti();
			}
		});
	}

	/**
	 * Launches Home screen
	 */
	private void launchMyAvanti() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_block, getMyAvantiFragment(),
				getMyAvantiFragment().getTag());
		transaction.commit();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		Log.d("Avanti", "In onActivityResult of MyAvantiActivity");
	}

	/**
	 * Get method for Handler instance
	 * @return Handler instance
	 */
	public Handler getHandler() {
		return myAvantiHandler;
	}


	/**
	 * Saves the Balance returned by Server
	 * @param balanceContainer Object returned by Server; this object contains Balance info
	 */
	private void saveBalanceAndNotifyFragment(Object balanceContainer) {
		try {
			String userBalance = ((AMGetBalanceDao) balanceContainer)
					.getJsonResponse().getString("userBalance");
			Log.d(TAG, "In Handler:AMEventType.GET_BALANCE: " + userBalance);

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = prefs.edit();
			editor.putString("updatedBalance", AMUtility
					.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
			editor.commit();

			List<Fragment> fragments = getSupportFragmentManager()
					.getFragments();
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isVisible()) {
					((BaseFragment) fragment).updateUI();
					Set<String> scanCodeStrings = prefs.getStringSet(
							"scanCodes", null);
					if (scanCodeStrings == null) {
						((BaseFragment) fragment).apiGetScanCodes(false);
					}
				}
			}
		} catch (JSONException e) {
			Log.d(TAG,
					"saveBalanceAndNotifyFragment Exception: "
							+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stoptimertask();
		dismissLoadingDialog();
		numberOfRepeatitiveCalls = 0;
		AMApplication.activityPaused();
		Log.d(TAG, "In MyAvantiActivity::onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		endMixPanelSession();
		Log.d(TAG, "In MyAvantiActivity::onStop");
		if(!isBackpressed && AMPreferenceManager.getPrefInstance().getPrefs().getBoolean(AMPrefKeys.PASSWORD_LOCK, true)
				&& !AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("background", false) ) {
//			Intent intent = new Intent(this, PinLoginActivity.class);
//			intent.putExtra("background", true);
//			startActivity(intent);
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putBoolean("background", true);
			editor.commit();
			isBackpressed = false;
			return;
		}

		if(AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("background", false)) {
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putBoolean("background", false);
			editor.commit();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if(AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("background", false)) {
			Intent intent = new Intent(this, PinLoginActivity.class);
			intent.putExtra("background", true);
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "In MyAvantiActivity::onBackPressed");
		isBackpressed = true;
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		AMUtility.getMixPanelInstance().flush();
		super.onDestroy();
		AMConstants.BALANCE_AMOUNT = 0d;
		clearInternalStoredContent(false);
		instance = null;
	}

	/**
	 * Adds Name/Color customization to the scan codes fetched from Server
	 */
	private void setNameAndColorToScanCodes() {
		Set<String> scanCodeStrings = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("scanCodes", null);
		Set<String> categoryNameColor = null;
		Editor editor = null;
		for(int i=0; i< scanCodeStrings.size(); i++) {

			switch (i) {
			case 0:
				categoryNameColor = new HashSet<String>(2);
				categoryNameColor.add("name"+"My Market Card");
				categoryNameColor.add("back"+getResources().getString(R.string.green));
				editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putStringSet((String)scanCodeStrings.toArray()[i], categoryNameColor);
				editor.commit();
				primaryScanCode = (String)scanCodeStrings.toArray()[i];
				editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putString("primaryScanCode", primaryScanCode);
				editor.commit();
				break;
			case 1:
				categoryNameColor = new HashSet<String>(2);
				categoryNameColor.add("name"+"My Travel Card");
				categoryNameColor.add("back"+getResources().getString(R.string.dark_blue));
				editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putStringSet((String)scanCodeStrings.toArray()[i], categoryNameColor);
				editor.commit();
				break;
			case 2:
				categoryNameColor = new HashSet<String>(2);
				categoryNameColor.add("name"+"My Backup Card");
				categoryNameColor.add("back"+getResources().getString(R.string.sky_blue));
				editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putStringSet((String)scanCodeStrings.toArray()[i], categoryNameColor);
				editor.commit();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Displays "Coming Soon!" dialog in case of unimplemented screens
	 * @param aMessage Message to be displayed
	 * @param aBtnTxt Button text to be displayed
	 */
	public void comingSoonDialog(String aMessage, String aBtnTxt) {
		final Dialog dialog = new Dialog(MyAvantiActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comingsoon_dialog);
		dialog.setCancelable(false);
		TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_comingsoon_txtMsg);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_cominsoon_Yes);

		if (aMessage != null) {
			tvMsg.setText(aMessage);
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

		dialog.show();
	}

	/**
	 * Invokes Network module and executes GET_BALANCE request
	 */
	private void apiGetBalance() {
		if (!this.isRequestInProgress(AMEventType.EVENT_GET_BALANCE)) {
			try {
				Log.d("AvantiMarket", "getBalance()");
				(new NetworkManagerClient(new NetHttpInfo(this,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_BALANCE,
						NetHttpUtils.HttpResposeDataModel.JSON),
						myAvantiHandler, this))
						.execute(new AMGetBalanceDao());
				this.addToRequestsInProgress(AMEventType.EVENT_GET_BALANCE);
				if(!isTimerRepeat) {
					stoptimertask();
				}
			} catch (InvalidArgException e) {
				Log.d("AvantiMarket", e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Initialises the Timer Task that ensures that the Balance displayed in app is in sync with the Balance available at Server
	 */
	private void initializeTimerTask() {

		timerTask = new TimerTask() {
			public void run() {

				timerHandler.post(new Runnable() {
					public void run() {
						apiGetBalance();
						Log.d("Timer Task : ", System.currentTimeMillis() + "");
					}
				});
			}
		};
	}

	/**
	 * Stops the Timer Task that ensures that the Balance displayed in app is in sync with the Balance available at Server
	 */
	public void stoptimertask() {
		Log.d("Timer Stopped : ", "Timer Stopped");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * Starts the Timer Task that ensures that the Balance displayed in app is in sync with the Balance available at Server
	 * @param isRepeat Frequency with which GET_BALANCE request should be sent
	 */
	public void startTimer(boolean isRepeat) {
		timer = new Timer();
		initializeTimerTask();
		if (isRepeat) {
			Log.d("Timre Mode", "true");
			timer.schedule(timerTask, 60000, 60000);
			isTimerRepeat = true;
		} else {
			Log.d("Timre Mode", "false");
			timer.schedule(timerTask, 60000);
			isTimerRepeat = false;
		}
	}

	public void setCreateCaseListner(ICreateCase createCase)
	{
		mCreateCase = createCase;
	}
	
	public static MyAvantiActivity getInstance() {
		return instance;
	}
	
	private void endMixPanelSession() {
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
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
			editor.putString("session", "");
		}
		editor.commit();
	}
	
	private void informMainThread(Object obj) {
		AMSpecialsDao mSpecialsDao = (AMSpecialsDao) obj;
		Log.d(TAG,
				"Handler EVENT_GET_SPECIALS result array length: "
						+ mSpecialsDao.getSpecials().size());
		List<Fragment> fragments = getSupportFragmentManager()
				.getFragments();
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible() && AMApplication.isActivityVisible()) {
				if(mSpecialsDao.getSpecials().size() > 0) {
					BaseFragment.mALSpecials = new ArrayList<SpecialsDao>(mSpecialsDao.getSpecials());
					BaseFragment.mALMyProgress = new ArrayList<SpecialsDao>(mSpecialsDao.getSpecials());
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_block, getMyMarketSpecialsFragment(), getMyMarketSpecialsFragment().getTag());
					transaction.commit();
				}
			}
		}		
	}
}
