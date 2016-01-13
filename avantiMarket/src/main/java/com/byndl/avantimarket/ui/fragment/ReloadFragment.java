/**
 * @brief       Enables users to Reload their Market cards by displaying Reload Denominations and available Credit/Debit cards
 * @file        ReloadFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        07-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;

/**
 * 
 * @brief Enables users to Reload their Market cards by displaying Reload Denominations and available Credit/Debit cards
 *
 */
public class ReloadFragment extends BaseFragment {

	private Button cancelButton, confirmButton;
	private TextView reloadAmountTextView, getBonusText, payWithText;
	private TextView reloadText[];
	private boolean isHomescreen = false, isRequestInitiated = false;
	private ViewFlipper mBalanceDisplayContainer;   
	private ImageButton tenButton, twentyButton, fiftyButton;
	private LinearLayout denominationContainer, creditCardsContainer, addWayToPay, selectedCard = null;
	private String selectedDenomination = "";
//	private TextView category, name;
	private String cardColor = "green";
	private BaseFragment context;
	private boolean visibility = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.reload_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		reloadText = new TextView[3];
		context = this;
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.reload));
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);

		if(getArguments() != null) {
			cardColor = getArguments().getString("color", "");
			isHomescreen = getArguments().getBoolean("isHomeScreen", false);
		}
		initialiseClassMembers();
		setListeners();
		updateBackground();
		
		if(getArguments() != null &&
				getArguments().getBoolean("isTransactionComplete") &&
				getArguments().getString("previousFragment") != null &&
				getArguments().getString("previousFragment").contains("Denominations")) {
			apiGetBalance(false);
			showLastSavedCardDeleteConfirmationDialog();
		}	
		
		else if(getArguments() != null &&
				getArguments().getBoolean("isTransactionFailed") &&
				getArguments().getString("previousFragment") != null &&
				getArguments().getBoolean("isReturningFromWebPage") &&
				(getArguments().getString("previousFragment") != null) &&
				getArguments().getString("previousFragment").contains("Denominations")) {
			parentActivity.showSingleBtnDialog("", "Transaction Failed", "OK");
		}

		else if(getArguments() != null &&
				! getArguments().getBoolean("isTransactionComplete") && 
				 ! getArguments().getBoolean("isTransactionFailed") &&
				 getArguments().getBoolean("isReturningFromWebPage") &&
				 (getArguments().getString("previousFragment") != null) &&
				 getArguments().getString("previousFragment").contains("Denominations")) {
			parentActivity.showSingleBtnDialog("", "Transaction Cancelled", "OK");
		}	
	}

	/**
	 * Initializes class members
	 */
	private void initialiseClassMembers() {
		mBalanceDisplayContainer = (ViewFlipper)parentActivity.findViewById(R.id.viewflipper);
		reloadAmountTextView = (TextView) parentActivity.findViewById(R.id.m_reloadLabel);
		getBonusText = (TextView) parentActivity.findViewById(R.id.m_getBonusText);
		payWithText = (TextView) parentActivity.findViewById(R.id.m_payWithText);
		confirmButton = (Button) parentActivity.findViewById(R.id.m_confirmButton);
		cancelButton = (Button) parentActivity.findViewById(R.id.m_cancelButton);
		denominationContainer = (LinearLayout) parentActivity.findViewById(R.id.m_denominationContainer);
		creditCardsContainer = (LinearLayout) parentActivity.findViewById(R.id.m_creditCardsContainer);
		addWayToPay = (LinearLayout) parentActivity.findViewById(R.id.m_addWayToPay);
		tenButton = (ImageButton) parentActivity.findViewById(R.id.m_tenCheckbox);
		twentyButton = (ImageButton) parentActivity.findViewById(R.id.m_twentyCheckbox);
		fiftyButton = (ImageButton) parentActivity.findViewById(R.id.m_fiftyCheckbox);
		
		reloadText[0] = (TextView) parentActivity.findViewById(R.id.m_reloadText1);
		reloadText[1] = (TextView) parentActivity.findViewById(R.id.m_reloadText2);
		reloadText[2] = (TextView) parentActivity.findViewById(R.id.m_reloadText3);
	}

	/**
	 * Sets up Listeners to accept/respond to User events
	 */
	private void setListeners() {
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int selectedCardIndex = creditCardsContainer.indexOfChild(selectedCard)/2;
				AMConstants.PRIMERY_CARD = mALStoredCards.get(selectedCardIndex).mCardId;
				isRequestInitiated = true;
				apiChargeCard(selectedDenomination, false);
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(creditCardsContainer.getVisibility() == View.VISIBLE) {
					updateCardsVisibility(null, null, false);
				}
				else {
					launchMarketCardsFragment();
				}
			}
		});

		tenButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(! tenButton.isSelected()) {
					updateCardsVisibility(parentActivity.getResources().getString(R.string.reload_ten), "10", true);
				}
			}
		});

		twentyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(! twentyButton.isSelected()) {
					updateCardsVisibility(parentActivity.getResources().getString(R.string.reload_twenty), "20", true);				
				}
			}
		});

		fiftyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(! fiftyButton.isSelected()) {
					updateCardsVisibility(parentActivity.getResources().getString(R.string.reload_fifty), "50", true);				
				}
			}
		});

		addWayToPay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString("previousFragment", "ReloadFragment");
				args.putBoolean("CreditCardsAvailability", true);
				args.putString("color", cardColor);
				parentActivity.getReloadDenominationsFragment().setArguments(args);
				
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getReloadDenominationsFragment(), parentActivity.getReloadDenominationsFragment().getTag());
				transaction.commit();
				context.apiGetGatewayParameters(selectedDenomination);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCardsFragment();
			}			
		});    	

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "ReloadF onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchMarketCardsFragment();
					return true;
				}
				return false;
			}
		});		

		if(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("reloadDenominations", null) == null) {
			apiGetReloadDenomination();
		}
		else {
			updateDenominationsAndBonuses(null);
		}
/*		
		if(mALStoredCards != null &&
				mALStoredCards.size() == 0 &&
				! getArguments().getString("previousFragment", "").contains("Denominations")) {
			Bundle args = new Bundle();
			args.putString("previousFragment", "ReloadFragment");
			args.putBoolean("CreditCardsAvailability", false);
			args.putString("color", cardColor);
			parentActivity.getReloadDenominationsFragment().setArguments(args);

			FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_block, parentActivity.getReloadDenominationsFragment(), parentActivity.getReloadDenominationsFragment().getTag());
			transaction.commit();
		}
*/
		visibility = true;
	}

	/**
	 * Launches Market Cards Display Screen
	 */
	private void launchMarketCardsFragment() {
		if(AMApplication.isActivityVisible() &&
				visibility) {
			FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
			if(isHomescreen){
				transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
			} else {
				transaction.replace(R.id.fragment_block, parentActivity.getMarketCardsFragment(), parentActivity.getMarketCardsFragment().getTag());
			}
			transaction.commit();
		}
	}

	/**
	 * Updates Background color as per the Name/Color customization
	 */
	private void updateBackground() {
//		category = (TextView)parentActivity.findViewById(R.id.m_primaryLabel);
//		name = (TextView)parentActivity.findViewById(R.id.m_categoryLabel);

//		if(getArguments().getBoolean("isPrimary")) category.setVisibility(View.VISIBLE);
//		else category.setVisibility(View.INVISIBLE);
		
		if(getArguments().getString("title") != null) {
//			name.setText(getArguments().getString("title"));
		}

		// SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(parentActivity);
		String updatedBalance = "$"
				+ AMUtility.getRoundedBalance(AMConstants.BALANCE_AMOUNT);
		((Button)((LinearLayout)mBalanceDisplayContainer.getChildAt(0)).getChildAt(0)).setText(updatedBalance);
		if(cardColor.contains("green")) {
			((Button)((LinearLayout)mBalanceDisplayContainer.getChildAt(0)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));
		}

		else if(cardColor.contains("dark")) {
			((Button)((LinearLayout)mBalanceDisplayContainer.getChildAt(0)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));
		}

		else
			((Button)((LinearLayout)mBalanceDisplayContainer.getChildAt(0)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));
	}

	/**
	 * Updates Denomination and Credit/Debit cards visibility as per the User selection
	 * @param aSelectedDenomination Selected Denomination
	 * @param amount Selected Amount
	 * @param visibility 
	 */
	private void updateCardsVisibility(String aSelectedDenomination, String amount, boolean visibility) {
		if(visibility) {
			this.selectedDenomination = amount;
			reloadAmountTextView.append(": $"+amount);

			getBonusText.setVisibility(View.GONE);
			denominationContainer.setVisibility(View.GONE);

			payWithText.setVisibility(View.VISIBLE);
			addWayToPay.setVisibility(View.VISIBLE);

			if(mALStoredCards!= null && (mALStoredCards.size() > 0)) {
				creditCardsContainer.setVisibility(View.VISIBLE);
				for(int i=0; i<16; i=i+2) {
					if(i/2 < mALStoredCards.size()) {
						creditCardsContainer.getChildAt(i).setVisibility(View.VISIBLE);
						int displayedCardLength = ((TextView)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(1)).length();
						if(displayedCardLength < 16) {
							((TextView)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(1)).append(mALStoredCards.get(i/2).mCardNo);
							
							if(mALStoredCards.get(i/2).mCardType.contains("AMERICAN EXPRESS")
									|| mALStoredCards.get(i/2).mCardType.contains("AMEX")) { 
								((Button)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.amex));
							}
							if(mALStoredCards.get(i/2).mCardType.contains("VISA")) {
								((Button)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.visa));
							}
							if(mALStoredCards.get(i/2).mCardType.contains("MASTERCARD")
									|| mALStoredCards.get(i/2).mCardType.contains("MC")) { 
								((Button)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.master_card));
							}
							if(mALStoredCards.get(i/2).mCardType.contains("DISCOVER")) {
								((Button)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(0)).setBackground(parentActivity.getResources().getDrawable(R.drawable.discover));
							}
							
							creditCardsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									payWithCardSelected(v);
								}
							});
						}
					}
					else {
						if(i > 0) creditCardsContainer.getChildAt(i-1).setVisibility(View.GONE);
					}
				}
			}
		}
		else {
			this.selectedDenomination = "";
			reloadAmountTextView.setText(R.string.reload_amount);

			if(AMPreferenceManager.getPrefInstance().getPrefs().getInt("50", 0) > 0) {
				getBonusText.setVisibility(View.VISIBLE);
			}
			denominationContainer.setVisibility(View.VISIBLE);

			if(confirmButton.getVisibility() == View.VISIBLE) {
				selectedCard.getChildAt(2).setVisibility(View.GONE);
				confirmButton.setVisibility(View.GONE);			
			}

			if(mALStoredCards!= null && (mALStoredCards.size() > 0)) {
				for(int i=0; i<16; i=i+2) {
					if(i/2 < mALStoredCards.size()) {
						creditCardsContainer.getChildAt(i).setVisibility(View.GONE);
					}
					else {
						if(i > 0) creditCardsContainer.getChildAt(i-1).setVisibility(View.GONE);
					}
				}
			}

			payWithText.setVisibility(View.GONE);
			creditCardsContainer.setVisibility(View.GONE);
			addWayToPay.setVisibility(View.GONE);
		}
	}

	/**
	 * Updates visibility of the Credit/Debit Cards as per the User selection
	 * @param selectedChild Selected Credit/Debit Card
	 */
	private void payWithCardSelected(View selectedChild) {
		selectedCard = (LinearLayout)selectedChild;
		for(int i=0; i<16; i=i+2) {
			if(selectedCard != creditCardsContainer.getChildAt(i))
				creditCardsContainer.getChildAt(i).setVisibility(View.GONE);
		}
		selectedCard.getChildAt(2).setVisibility(View.VISIBLE);
		confirmButton.setVisibility(View.VISIBLE);
		addWayToPay.setVisibility(View.GONE);
	}

	@Override
	public void updateUI() {
		if(isRequestInitiated) {
			isRequestInitiated = false;
			launchMarketCardsFragment();
		}
		else {
			if(mALStoredCards!= null && (mALStoredCards.size() > 0) && (confirmButton.getVisibility()==View.INVISIBLE)) {
				creditCardsContainer.setVisibility(View.VISIBLE);
				for(int i=0; i<16; i=i+2) {
					if(i/2 < mALStoredCards.size()) {
						creditCardsContainer.getChildAt(i).setVisibility(View.VISIBLE);
						int displayedCardLength = ((TextView)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(1)).length();
						if(displayedCardLength < 16) {
							((TextView)((LinearLayout)creditCardsContainer.getChildAt(i)).getChildAt(1)).append(mALStoredCards.get(i/2).mCardNo);
							creditCardsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									payWithCardSelected(v);
								}
							});
						}
					}
					else {
						if(i > 0) {
							creditCardsContainer.getChildAt(i-1).setVisibility(View.GONE);
							creditCardsContainer.getChildAt(i).setVisibility(View.GONE);
						}
					}
				}
			}
		}
		((Button)((LinearLayout)mBalanceDisplayContainer.getChildAt(0)).getChildAt(0)).setText(
				"$"+ AMUtility.getRoundedBalance(AMConstants.BALANCE_AMOUNT));

	}
	
	public void updateDenominationsAndBonuses(Object obj) {
		Set<String> denominations = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("reloadDenominations", null);
		ArrayList<String> relDen = new ArrayList<String>(denominations);
		Collections.sort(relDen);
		if(denominations != null) {
			for(int i=denominations.size()-1; i>=0; i--) {
				
				reloadText[i].setText("$"+relDen.get(i));
				
				int bonus = AMPreferenceManager.getPrefInstance().getPrefs().getInt((String) relDen.get(i), 0);
				if(bonus != 0) {
					reloadText[i].append("\n\r(+"+bonus/100+" Bonus)");
				}
				else {
					if(((String) denominations.toArray()[i]).contains("50")) {
						getBonusText.setVisibility(View.GONE);
					}
				}
			}
		}
	}
	
	@Override
	public void onPause() {
		visibility = false;
		super.onPause();
	}
}
