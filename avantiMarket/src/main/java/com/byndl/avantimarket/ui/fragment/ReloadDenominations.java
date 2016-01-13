/**
 * @brief       This class is used to show options available on support screen.
 * @file        SupportFragment.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMGetGatewayParametersDao;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * @brief	This class is used to show options available on support screen.
 * <br> First option is to get helpful hints to use app.
 * <br> Second option will go to Contact us screen.
 * <br> Third option is to give feedback.
 * 
 */
public class ReloadDenominations extends BaseFragment {

	private Button reloadButton[];
	private WebView secure_page_view;
	private String selectedDenomination = "";
	private boolean isTransactionFailed = false;
	private LinearLayout denominations_container, secure_page_container;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.reload_denominations_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		WebView.setWebContentsDebuggingEnabled(true);
		
		reloadButton = new Button[3];
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.ways_to_pay).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		denominations_container = (LinearLayout)parentActivity.findViewById(R.id.denominations_container);
		secure_page_container = (LinearLayout)parentActivity.findViewById(R.id.secure_page_container);
		secure_page_view = (WebView)parentActivity.findViewById(R.id.secure_page_view);
		secure_page_view.clearCache(true);

		setBackListenerforWebView();
//		setUpKeyboardListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchPreviousFragment(false);
			}			
		});    	
		
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "Support onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchPreviousFragment(false);
					return true;
				}
				return false;
			}
		});
		
		if(getArguments() == null) {
			denominations_container.setVisibility(View.VISIBLE);
			setListeners();
		}
		else {
			if(getArguments().getBoolean("CreditCardsAvailability")) {
				secure_page_container.setVisibility(View.VISIBLE);
			}
			else {
				denominations_container.setVisibility(View.VISIBLE);
				setListeners();
			}
		}
		
		if(denominations_container.getVisibility() == View.VISIBLE) {
			if(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("reloadDenominations", null) == null) {
				apiGetReloadDenomination();
			}
			else {
				updateDenominationsAndBonuses(null);
			}
		}

	}
	
	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}
	
	private void launchPreviousFragment(boolean isTransactionComplete){
		if(secure_page_container.getVisibility() == View.VISIBLE && 
				! isTransactionComplete) {
			if(getArguments() != null &&
					getArguments().getString("previousFragment").contains("Reload") &&
					mALStoredCards != null) {
				
				Bundle args = new Bundle();
				args.putString("previousFragment", "ReloadDenominations");
				args.putBoolean("isTransactionComplete", isTransactionComplete);
				args.putBoolean("isTransactionFailed", isTransactionFailed);
				args.putString("color", getArguments().getString("color", ""));
				args.putBoolean("isReturningFromWebPage", true);
				parentActivity.getReloadFragment().setArguments(args);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getReloadFragment(), parentActivity.getReloadFragment().getTag());
				transaction.commit();
			}
			else {
				secure_page_container.setVisibility(View.GONE);
				denominations_container.setVisibility(View.VISIBLE);
				if(isTransactionFailed) {
					parentActivity.showSingleBtnDialog("", "Transaction failed!", "OK");
				}
				if( ! isTransactionComplete && 
						 ! isTransactionFailed) {
					parentActivity.showSingleBtnDialog("", "Transaction Cancelled", "OK");
				}
			}
			return;
		}
		parentActivity.dismissLoadingDialog();
		
		Bundle args = new Bundle();
		args.putString("previousFragment", "ReloadDenominations");
		args.putBoolean("isTransactionComplete", isTransactionComplete);
		args.putBoolean("isTransactionFailed", isTransactionFailed);
		if(denominations_container.getVisibility() == View.VISIBLE) {
			args.putBoolean("isReturningFromDenomScreen", true);
		}
		isTransactionFailed = false;
		
		InputMethodManager inputManager = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);				

		if(isTransactionComplete) {
			if(		getArguments() == null ||
					getArguments().getBoolean("isMenuLaunch") ||
					getArguments().getString("previousFragment").contains("MyMarketFragment")) {
				parentActivity.getWaysToPayFragment().setArguments(args);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getWaysToPayFragment(), parentActivity.getWaysToPayFragment().getTag());
				transaction.commit();
			}
			else {
				args.putString("color", getArguments().getString("color", ""));
				parentActivity.getReloadFragment().setArguments(args);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getReloadFragment(), parentActivity.getReloadFragment().getTag());
				transaction.commit();
			}
		}
		else {
			if(	getArguments() == null ) {
				parentActivity.getWaysToPayFragment().setArguments(args);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getWaysToPayFragment(), parentActivity.getWaysToPayFragment().getTag());
				transaction.commit();
			}
			else if(getArguments().getString("previousFragment").contains("Reload")) {
				args.putString("color", getArguments().getString("color", ""));
				parentActivity.getReloadFragment().setArguments(args);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getReloadFragment(), parentActivity.getReloadFragment().getTag());
				transaction.commit();
			}
			else if(getArguments().getBoolean("isZeroCardsLaunch")) {
				returnBack(getArguments().getString("previousFragment"));
			}
		}
	}
	
	private void setListeners() {
		reloadButton[0] = (Button)parentActivity.findViewById(R.id.reload_ten);
		reloadButton[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedDenomination = "10";
				apiGetGatewayParameters(selectedDenomination);
			}
		});
		
		reloadButton[1] = (Button)parentActivity.findViewById(R.id.reload_twenty);
		reloadButton[1].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedDenomination = "20";
				apiGetGatewayParameters(selectedDenomination);
			}
		});
		
		reloadButton[2] = (Button)parentActivity.findViewById(R.id.reload_fifty);
		reloadButton[2].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedDenomination = "50";
				apiGetGatewayParameters(selectedDenomination);
			}
		});
	}

	public void loadBankURL(Object obj) {
		denominations_container.setVisibility(View.GONE);
		secure_page_container.setVisibility(View.VISIBLE);
		AMGetGatewayParametersDao gatewayParameters = (AMGetGatewayParametersDao)obj;
		secure_page_view.getSettings().setJavaScriptEnabled(true);
		secure_page_view.getSettings().setBuiltInZoomControls(true);
        String postData = "x_login=" + gatewayParameters.x_login 
        				+ "&x_show_form=" + gatewayParameters.x_show_form
						+ "&x_fp_sequence=" + gatewayParameters.x_fp_sequence
						+ "&x_fp_hash=" + gatewayParameters.x_fp_hash
						+ "&x_amount=" + gatewayParameters.x_amount
						+ "&x_currency_code=" + gatewayParameters.x_currency_code
						+ "&x_test_request=" + gatewayParameters.x_test_request
        				+ "&x_relay_response=" + gatewayParameters.x_relay_response
						+ "&donation_prompt=" + gatewayParameters.donation_prompt
						+ "&button_code=" + gatewayParameters.button_code
						+ "&mmc_operatorid=" + gatewayParameters.mmc_operatorid
						+ "&mmc_marketuserid=" + gatewayParameters.mmc_marketuserid
						+ "&mmc_transactionid=" + gatewayParameters.mmc_transactionid
						+ "&mmc_requesthost=" + gatewayParameters.mmc_requesthost
						+ "&mmc_save_card_info=" + gatewayParameters.mmc_save_card_info
						+ "&x_fp_timestamp=" + gatewayParameters.x_fp_timestamp;
        
        secure_page_view.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
            	
            	
            	if(url.contains("final_receipt")) {
                	parentActivity.dismissLoadingDialog();
            		launchPreviousFragment(true);
            		apiGetBalance(false);

            		try {
                		JSONObject properties = new JSONObject();
						properties.put("Reload Type", "One Time Relaod");
                		properties.put("Reload Amount", selectedDenomination);
	        			properties.put("Timestamp", MixPanelEvents.getTimeStamp());
	            		AMUtility.getMixPanelInstance().track("Reload Success", properties);
					} catch (JSONException e) {
						e.printStackTrace();
					}

            		return false;
            	}
            	else if(url.contains("payment_completion")) {
                	parentActivity.dismissLoadingDialog();
            		isTransactionFailed = true;
            		launchPreviousFragment(false);

                    try {
                		JSONObject properties = new JSONObject();
                		properties.put("Error Type", "BAMS Error");
            			properties.put("Timestamp", MixPanelEvents.getTimeStamp());
            			AMUtility.getMixPanelInstance().track("Reload Fail", properties);
            		} catch (JSONException e) {
            			e.printStackTrace();
            		}

                    return false;
            	}

            	else if(url.contains("/Account/Login")) {
                	parentActivity.dismissLoadingDialog();
            		launchPreviousFragment(false);
            		return false;
            	}

            	else {
            		return false;
            	}
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
            	super.onPageFinished(view, url);
            	parentActivity.dismissLoadingDialog();
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            	super.onPageStarted(view, url, favicon);
            }
        });
         
        secure_page_view.postUrl(gatewayParameters.x_url, EncodingUtils.getBytes(postData, "BASE64"));
        parentActivity.showLoadingDialog();
	}
	
	public void updateDenominationsAndBonuses(Object obj) {
		if(denominations_container.getVisibility() == View.VISIBLE) {
			Set<String> denominations = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("reloadDenominations", null);
			ArrayList<String> relDen = new ArrayList<String>(denominations);
			Collections.sort(relDen);
			if(denominations != null) {
				for(int i=denominations.size()-1; i>=0; i--) {
					
					reloadButton[i].setText("$"+relDen.get(i));
					
					float bonus = AMPreferenceManager.getPrefInstance().getPrefs().getInt((String) relDen.get(i), 0);
					if(bonus != 0) {
						reloadButton[i].append("\n\r$"+AMUtility.getTwoDecimals(""+bonus/100)+" Bonus");
					}
				}
			}
			else {
				
			}
		}
	}

	@Override
	public void onDestroy() {
		if(secure_page_view != null) {
			secure_page_view.destroy();
			secure_page_view = null;
		}
		super.onDestroy();
	}
	
	private void setBackListenerforWebView() {
		secure_page_view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchPreviousFragment(false);
					return true;
				}
				return false;
			}
		});
	}
	private void returnBack(String previousFragment) {
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		if(previousFragment.contains("MyMarketFragment")) {
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else if(previousFragment.contains("MarketCardsFragment")) {
//			transaction.replace(R.id.fragment_block, parentActivity.getMarketCardsFragment(), parentActivity.getMarketCardsFragment().getTag());
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else if(previousFragment.contains("HistoryFragment")) {
//			transaction.replace(R.id.fragment_block, parentActivity.getHistoryFragment(), parentActivity.getHistoryFragment().getTag());
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else if(previousFragment.contains("MyAccountFragment")) {
//			transaction.replace(R.id.fragment_block, parentActivity.getMyAccountFragment(), parentActivity.getMyAccountFragment().getTag());
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else if(previousFragment.contains("SupportFragment")) {
//			transaction.replace(R.id.fragment_block, parentActivity.getSupportFragment(), parentActivity.getSupportFragment().getTag());
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else if(previousFragment.contains("AboutUsFragment")) {
//			transaction.replace(R.id.fragment_block, parentActivity.getAboutUsFragment(), parentActivity.getAboutUsFragment().getTag());
			transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		}
		else {
			return;
		}
		transaction.commit();
	}
}
