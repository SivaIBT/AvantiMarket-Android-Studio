/**
 * @brief       Home screen after successful login; provides frequent functionality just a tap away (Pay/Reload/CC/Rewards/History)
 * @file        MyMarketFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        04-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.Set;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;

/**
 * 
 * @brief Home screen after successful login; provides frequent functionality just a tap away (Pay/Reload/CC/Rewards/History)
 *
 */
public class MyMarketFragment extends BaseFragment {

	private Button payButton, reloadButton;
	private ImageButton historyButton, waysToPayButton, rewardsButton;
	private TextView firstName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.my_avanti_layout, container, false);
		return inflater.inflate(R.layout.my_avanti_layout_rewards, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.my_avanti));
		setUpListeners();
	}

	/**
	 * Sets up Listeners to accept/respond to user events
	 */
	private void setUpListeners() {
		payButton = (Button) parentActivity.findViewById(R.id.m_balanceButton);
		historyButton = (ImageButton) parentActivity.findViewById(R.id.m_historyButton);
		waysToPayButton = (ImageButton) parentActivity.findViewById(R.id.m_cardsButton);
		reloadButton = (Button) parentActivity.findViewById(R.id.m_reloadButton);
		rewardsButton = (ImageButton) parentActivity.findViewById(R.id.m_rewardsButton);

		firstName = (TextView) parentActivity.findViewById(R.id.m_nameLabel);
		String name = AMPreferenceManager.getPrefInstance().getPrefs().getString("firstname", "");
		if(name.length() > 0) {
			firstName.append(", "+name+"!");
		}

		payButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getMarketCardsFragment(), parentActivity.getMarketCardsFragment().getTag());
				transaction.commit();
			}
		});

		waysToPayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				apiGetStoredCards();
			}
		});

		if(rewardsButton != null) {
			rewardsButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					apiGetAllSpecials();
				}
			});			
		}

		reloadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				Set<String> cardColorDetails = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(parentActivity.primaryScanCode, null);
				if(cardColorDetails != null && (cardColorDetails.size() > 0)) {
					Bundle args = new Bundle();
					args.putBoolean("isPrimary", true);
					String info = "";
					for(int i =0; i < cardColorDetails.size(); i++) {
						info = ((String)cardColorDetails.toArray()[i]); 
						if(info.startsWith("name")) {
							args.putString("title", info.substring(4));
						}
						if(info.startsWith("back")) {
							if(info.contains(getResources().getString(R.string.green))) 
								args.putString("color", getResources().getString(R.string.green));
							else if(info.contains(getResources().getString(R.string.dark_blue))) 
								args.putString("color", getResources().getString(R.string.dark_blue));
							else 
								args.putString("color", getResources().getString(R.string.sky_blue));
						}
					}

					args.putBoolean("isHomeScreen", true);
					parentActivity.getReloadFragment().setArguments(args);
					transaction.replace(R.id.fragment_block, parentActivity.getReloadFragment(), parentActivity.getReloadFragment().getTag());
					transaction.commit();
				}
				else {
					Log.d(TAG, "FAILED to initialise Reload Fragment");
					Toast.makeText(parentActivity, "Please wait\n\rFetching Card Info ...", Toast.LENGTH_LONG).show();
					apiGetScanCodes(true);
				}
			}
		});
		
		historyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getHistoryFragment(), parentActivity.getHistoryFragment().getTag());
				transaction.commit();
			}
		});

	}

	@Override
	public void onResume() {
		Log.d(TAG, "In MyMarketFragment::onResume: "+mALStoredCards);
		super.onResume();
		parentActivity.primaryScanCode = AMPreferenceManager.getPrefInstance().getPrefs().getString("primaryScanCode", null);

		parentActivity.setMenuDrawer();
		updateBalance();
		if(AMConstants.BALANCE_AMOUNT > 0) {
			apiGetBalance(false);
		}
		else {
			apiGetBalance(true);
		}
		
/*		if(mALStoredCards == null) {
			apiGetStoredCards();
		}
*/
		Display display = parentActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		width = width-(2*(int)getResources().getDimension(R.dimen.regular_margin));
	}

	/**
	 * Updates Balance being displayed as per the latest response from Server
	 */
	public void updateBalance() {
		if (payButton != null && 
				AMConstants.BALANCE_AMOUNT > 0) {
			if(rewardsButton != null) {
				payButton.setText("$" + AMUtility
						.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
			}
			else {
				payButton.setText("BALANCE: $" + AMUtility
						.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
			}
		}
	}

	@Override
	public void updateUI() {
		if(rewardsButton != null) {
			payButton.setText("$"
					+ AMUtility.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
		}
		else {
			payButton.setText("BALANCE: $" + AMUtility
					.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
		}
	}
}
