/**
 * @brief       Displays the registered Market Cards available under User account
 * @file        MarketCardsFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        03-Mar-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.Set;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMGetAutoRechargeSettingsDao;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 
 * @brief Displays the registered Market Cards available under User account
 *
 */
public class MarketCardsFragment extends BaseFragment {

	private ViewPager mViewPager;
//	private TextView primaryTextView, categoryTextView;
	private Button payButton, autoReloadButton, reloadButton;
	private int marketCardsCount = 0;
	private Set<String> scanCodeStrings = null;
	private GestureDetector detector = null;
	
	private String currentColor = "";
	private String currentCardName = "";
	private boolean isPrimary = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.market_cards_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity) getActivity();
		((TextView) parentActivity.findViewById(R.id.m_navBarTitle))
		.setText(parentActivity.getResources().getString(R.string.pay));

		setUpListeners();
	}

	/**
	 * 
	 * To capture "Single Tap" user event
	 *
	 */
	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			launchPayFragment();
			return super.onSingleTapConfirmed(e);
		}
	}

	/**
	 * Updates whether Market Card under Display is PRIMARY or not
	 */
	private void updateCardTitle() {
		String scanCode = (String)scanCodeStrings.toArray()[mViewPager.getCurrentItem()];
		Set<String> cardColorDetails = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(scanCode, null);
		
		if(parentActivity.primaryScanCode.equalsIgnoreCase(scanCode)) isPrimary = true;
		else isPrimary = false;
		
		String info = "";
		for(int i =0; i < cardColorDetails.size(); i++) {
			info = ((String)cardColorDetails.toArray()[i]); 

			if(info.startsWith("name")) {
				currentCardName = info.substring(4);
			}
			if(info.startsWith("back")) {
				if(info.contains(getResources().getString(R.string.green))) currentColor = getResources().getString(R.string.green);
				else if(info.contains(getResources().getString(R.string.dark_blue))) currentColor = getResources().getString(R.string.dark_blue);
				else currentColor = getResources().getString(R.string.sky_blue);
			}
		}
/*		if(isPrimary) {
			primaryTextView.setVisibility(View.VISIBLE);
		}
		else {
			primaryTextView.setVisibility(View.INVISIBLE);
		}
		categoryTextView.setText(currentCardName);
*/	}

	/**
	 * Sets up Listeners to accept/respond to user events
	 */
	private void setUpListeners() {
		payButton = (Button) parentActivity.findViewById(R.id.m_payButton);
		autoReloadButton = (Button) parentActivity.findViewById(R.id.m_autoReloadButton);
		reloadButton = (Button) parentActivity.findViewById(R.id.m_reloadButton);
//		updateInfoButton = (Button) parentActivity.findViewById(R.id.m_updateInfo);
//		primaryTextView = ((TextView) parentActivity.findViewById(R.id.m_primaryLabel));
//		categoryTextView = ((TextView) parentActivity.findViewById(R.id.m_categoryLabel));
		mViewPager = (ViewPager) parentActivity.findViewById(R.id.viewpager);
		MarketCardsAdapter adapter = new MarketCardsAdapter();
		mViewPager.setAdapter(adapter);
		setPageIndicator();
		payButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchPayFragment();
			}
		});

		autoReloadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				apiGetAutoRechargeSettings();
				parentActivity.showSingleBtnDialog("COMING SOON!", getString(R.string.go_to_mmc), "OK");
			}
		});

		reloadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				apiGetStoredCards();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		
		updateUI();
		parentActivity.setMenuDrawer();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "Ways2 onKey");
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchMyAvanti();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		getView().setOnKeyListener(null);
	}

	@Override
	public void onStop() {
		super.onStop();
		getView().setOnKeyListener(null);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// getView().setOnKeyListener(null);
	}

	/**
	 * Launches Home Screen
	 */
	private void launchMyAvanti() {
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getMyAvantiFragment(), parentActivity.getMyAvantiFragment()
				.getTag());
		transaction.commit();
	}

	/**
	 * Launches Scan Code (BarCode) Display screen
	 */
	private void launchPayFragment() {
		Bundle args = new Bundle();
		args.putInt("cardSelection", mViewPager.getCurrentItem());
		args.putString("color", currentColor);
		args.putBoolean("isPrimary", isPrimary);
		args.putString("title", currentCardName);

		parentActivity.getPayFragment().setArguments(args);
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getPayFragment(), parentActivity.getPayFragment().getTag());
		transaction.commit();
	}

	/**
	 * Updates the Balance being displayed
	 * @param userBalance
	 */
	public void updateBalance() {
		for (int i = 0; i < marketCardsCount; i++) {
			((Button) ((LinearLayout) ((RelativeLayout) mViewPager
					.getChildAt(i)).getChildAt(0)).getChildAt(0)).setText("$"
					+ AMUtility.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
		}
	}

	@Override
	public void updateUI() {
		// payButton.setText("$"+AMPreferenceManager.getPrefInstance().getPrefs().getString("updatedBalance",
		// ""));
		scanCodeStrings = AMPreferenceManager.getPrefInstance()
				.getPrefs().getStringSet("scanCodes", null);
		if (scanCodeStrings != null && (scanCodeStrings.size() > 0)) {
			Log.d(TAG, "MarketCardsFragment::updateUI(): "+AMUtility.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
			marketCardsCount = scanCodeStrings.size();
			String scanCode = (String)scanCodeStrings.toArray()[0];
			if(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(scanCode, null)
					== null) {
				parentActivity.showLoadingDialog();
			}
			else {
				mViewPager.getAdapter().notifyDataSetChanged();
				updateBalance();
			}
		}
		else {
			apiGetScanCodes(true);
			marketCardsCount = 1;
			mViewPager.getAdapter().notifyDataSetChanged();
		}
	}

	/**
	 * 
	 * Adapter that supports the ViewPager
	 *
	 */
	private class MarketCardsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return marketCardsCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((RelativeLayout) arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			RelativeLayout marketCard = null;
			switch (position) {
			case 0:
				marketCard = (RelativeLayout)parentActivity.findViewById(R.id.first_mc);
				break;
			case 1:
				marketCard = (RelativeLayout)parentActivity.findViewById(R.id.second_mc);
				break;
			case 2:
				marketCard = (RelativeLayout)parentActivity.findViewById(R.id.third_mc);
				break;

			default:
				break;
			}
			marketCard.setVisibility(View.VISIBLE);
			Button balanceButton = ((Button) ((LinearLayout) marketCard
					.getChildAt(0)).getChildAt(0));
			balanceButton.setText("$" + AMUtility
					.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
			if (detector == null) {
				detector = new GestureDetector(parentActivity,
						new SwipeGestureDetector());
			}
			balanceButton.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent event) {
					detector.onTouchEvent(event);
					return true;
				}
			});
			/*			if(marketCard.getParent() != null) {
				ViewGroup parent = (ViewGroup) marketCard.getParent();
				parent.removeView(marketCard);
			}
			 */			
			updateColor(balanceButton, position);
			try{
				((ViewPager) container).addView(marketCard);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			return marketCard;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			super.destroyItem(container, position, object);
		}

		@Override
		public void startUpdate(ViewGroup container) {
			Log.d(TAG, "PagerAdapter::startUpdate");
			super.startUpdate(container);
		}
		@Override
		public void finishUpdate(ViewGroup container) {
			Log.d(TAG, "PagerAdapter::finishUpdate");
			if (scanCodeStrings != null && (scanCodeStrings.size() > 0)) {
				updateCardTitle();
			}
			super.finishUpdate(container);
		}
	}

	/**
	 * Sets up DOT indicator below the Market Cards 
	 */
	private void setPageIndicator() {
		CirclePageIndicator mIndicator = (CirclePageIndicator) parentActivity.findViewById(R.id.indicator);
		((CirclePageIndicator) mIndicator).setRadius(parentActivity.getResources().getDimension(R.dimen.circle_indicator_radius));
		mIndicator.setViewPager(mViewPager);
		((CirclePageIndicator) mIndicator).setSnap(true);

		mIndicator
		.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrolled(int position,
					float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	/**
	 * Updates the Market Card background as per the Name/Color customization
	 * @param marketCard Card to be updated
	 * @param index Index of card to be updated
	 */
	private void updateColor(Button marketCard, int index) {
		if(scanCodeStrings!=null && scanCodeStrings.size()>0){
			String scanCode = (String)scanCodeStrings.toArray()[index];
			Set<String> cardColorDetails = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(scanCode, null);
			String info = "";
			for(int i =0; i < cardColorDetails.size(); i++) {
				info = ((String)cardColorDetails.toArray()[i]); 
				if(info.startsWith("back")) {
					if(info.contains(getResources().getString(R.string.green))) {
						marketCard.setBackgroundResource(R.drawable.mc_green_background);
					}
					else if(info.contains(getResources().getString(R.string.dark_blue))) {
						marketCard.setBackgroundResource(R.drawable.mc_green_background);
					}
					else {
						marketCard.setBackgroundResource(R.drawable.mc_green_background);
					}
				}
			}
		}
	}
	
	public void autoRechargeSettingsReceived(Object obj) {
		AMGetAutoRechargeSettingsDao autoRechargeSettings = (AMGetAutoRechargeSettingsDao)obj;
		if(autoRechargeSettings.mAutoRechargeEnabled &&
				! autoRechargeSettings.mCardID.isEmpty() &&
				! autoRechargeSettings.mReloadAmount.isEmpty()) {
			AMConstants.PRIMERY_CARD = autoRechargeSettings.mCardID;
			apiChargeCard(autoRechargeSettings.mReloadAmount, true);
		}
		else {
			parentActivity.showSingleBtnDialog("COMING SOON!", getString(R.string.go_to_mmc), "OK");
		}
	}
	
	public void storedCardsReceived(Object obj) {
		Bundle args = new Bundle();
		args.putInt("cardSelection", mViewPager.getCurrentItem());
		args.putString("color", currentColor);
		args.putString("title", currentCardName);
		args.putBoolean("isHomeScreen", false);
		parentActivity.getReloadFragment().setArguments(args);
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getReloadFragment(), parentActivity
				.getReloadFragment().getTag());
		transaction.commit();
	}
}
