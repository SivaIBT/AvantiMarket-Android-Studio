/**
 * @brief       Displays Credit/Debit cards available under User account 
 * @file        WaysToPayFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        09-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 
 * @brief Displays Credit/Debit cards available under User account
 *
 */
public class WaysToPayFragment extends BaseFragment {

	private ViewPager mViewPager;
	private TextView noCardsTextView;
	private Button deleteCard, addCard;
	private int creditCardsCount = 0;
	private CirclePageIndicator mIndicator = null; 
	private LinearLayout dotIndicatorContainer;
	private RelativeLayout creditCard1 = null, creditCard2 = null, creditCard3 = null;
	private float offSet = -1, pageScrollState = -1, finalState = -1, scrollDistance = 0;
	private GestureDetector detector = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ways_to_pay_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity) getActivity();
		((TextView) parentActivity.findViewById(R.id.m_navBarTitle))
		.setText(parentActivity.getResources().getString(
				R.string.ways_to_pay));
		creditCard1 = (RelativeLayout)parentActivity.findViewById(R.id.first_cc);
		creditCard2 = (RelativeLayout)parentActivity.findViewById(R.id.second_cc);
		creditCard3 = (RelativeLayout)parentActivity.findViewById(R.id.third_cc);
		setUpListeners();
		Bundle args = getArguments();
		if (args != null) {
			if(args.getBoolean("isTransactionComplete") &&
					args.getString("previousFragment") != null &&
					args.getString("previousFragment").contains("Denominations")) {
				apiGetBalance(false);
				showLastSavedCardDeleteConfirmationDialog();
			}
			else if(args.getBoolean("isTransactionFailed") &&
					args.getString("previousFragment") != null &&
					args.getString("previousFragment").contains("Denominations")) {
//				parentActivity.showSingleBtnDialog("", "Transaction Failed", "OK");
			}
			else if( ! args.getBoolean("isTransactionComplete") && 
					 ! args.getBoolean("isTransactionFailed") &&
					 ! args.getBoolean("isReturningFromDenomScreen") &&
					 (args.getString("previousFragment") != null) &&
					 args.getString("previousFragment").contains("Denominations")) {
//				parentActivity.showSingleBtnDialog("", "Transaction Cancelled", "OK");

			}
		}		
	}

	/**
	 * Updates Card Title as per Category (Primary or Not)
	 */
	private void updateCardTitle() {
/*		int primaryWayToPayIndex = AMPreferenceManager.getPrefInstance().getPrefs().getInt("primaryWayToPayIndex", 0);
		if(mViewPager.getCurrentItem() == primaryWayToPayIndex) {
			primaryTextView.setVisibility(View.VISIBLE);
		} else {
			primaryTextView.setVisibility(View.INVISIBLE);
		}
*/	}

	/**
	 * Sets up Listeners to accept/respond to User events
	 */
	private void setUpListeners() {
		dotIndicatorContainer = (LinearLayout) parentActivity.findViewById(R.id.dotIndicatorContainer);
		mViewPager = (ViewPager) parentActivity.findViewById(R.id.viewpager);
		CreditCardsAdapter adapter = new CreditCardsAdapter();
		mViewPager.setAdapter(adapter);
		setPageIndicator();

//		primaryTextView = ((TextView) parentActivity.findViewById(R.id.m_primaryLabel));
		noCardsTextView = ((TextView) parentActivity.findViewById(R.id.m_categoryLabel));
		
		deleteCard = (Button) parentActivity.findViewById(R.id.m_deleteCard);
		deleteCard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateCardCount();
				if(mALStoredCards.size() > 0) {
					showDeleteCardConfirmationDialog(mALStoredCards.get(mViewPager.getCurrentItem()).mIsAutoChargeCard);
				}
			}
		});

		addCard = (Button) parentActivity.findViewById(R.id.addCard);
		addCard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				launchReloadDenominations();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Avanti", "In onActivityResult of WaysToPayFragment");
		String resultStr;
		CreditCard scanResult = null;
		if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
			scanResult = data
					.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
			// Never log a raw card number. Avoid displaying it, but if
			// necessary use getRedactedCardNumber()
			resultStr = "Card Number: " + scanResult.getRedactedCardNumber()
					+ "\n";

			if (scanResult.isExpiryValid()) {
				resultStr += "Expiration Date: " + scanResult.expiryMonth + "/"
						+ scanResult.expiryYear + "\n";
			}

			if (scanResult.getCardType() != null) {
				resultStr += " " + scanResult.getCardType().name + " ";
			}
			if (scanResult.cvv != null) {
				// Never log or display a CVV
				resultStr += "CVV has " + scanResult.cvv.length()
						+ " digits.\n";
			}
			if (scanResult.postalCode != null) {
				resultStr += "Postal Code: " + scanResult.postalCode + "\n";
			}
		} else {
			resultStr = "Scan was canceled.";
		}
		Log.d("Avanti", "CC Scan Result: " + resultStr);
		if (scanResult != null) {
			Bundle args = new Bundle();
			args.putString("title",
					parentActivity.getResources().getString(R.string.add_card));
			args.putString("cardNumber", parentActivity.getResources()
					.getString(R.string.add_card));
			args.putString("cardNumber", scanResult.getRedactedCardNumber());
			args.putInt("expiryMonth", scanResult.expiryMonth);
			args.putInt("expiryYear", scanResult.expiryYear);
			args.putString("cardType", scanResult.getCardType().name);
			parentActivity.getAddCreditCardFragment().setArguments(args);
			FragmentTransaction transaction = parentActivity
					.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_block, parentActivity
					.getAddCreditCardFragment(), parentActivity
					.getAddCreditCardFragment().getTag());
			transaction.commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if(mALStoredCards == null) {
			apiGetStoredCards();
//			creditCardsCount = 1;
//			mViewPager.getAdapter().notifyDataSetChanged();
			noCardsTextView.setVisibility(View.VISIBLE);
		} else if(mALStoredCards.size() == 0) {
			noCardsTextView.setVisibility(View.VISIBLE);
			if(getArguments() == null) {
//				launchReloadDenominations();
			}
		}else {
			updateUI();
		}
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

		Bundle args = getArguments();
		if (args != null) {
			if(args.getString("cardNumber") != null) {
				String cardNumber = args.getString("cardNumber");
				if (cardNumber != null && cardNumber.length()>=4) {
					cardNumber = cardNumber.substring(cardNumber.length() - 4);
					final TextView dialogMessage = new TextView(parentActivity);
					dialogMessage.setText("Your card ending " + cardNumber
							+ " \n\r has been successfully updated.");
					showConfirmationDialog(dialogMessage.getText().toString());
				}
				args.putString("cardNumber", null);
			}
			return;
		}
		
//		apiGetStoredCards();
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

	public void updateUI() {
		Log.d("AvantiMarket", "WaysToPayFragment:updateUI()"+mALStoredCards);
		if(mALStoredCards != null) {
			creditCardsCount = mALStoredCards.size(); 
			int previousCreditCardCount = AMPreferenceManager.getPrefInstance().getPrefs().getInt("previousCreditCardCount", -1); 
			if(previousCreditCardCount == -1) updateCardCount();
			
			if(previousCreditCardCount != -1 &&
					previousCreditCardCount != mALStoredCards.size()) {
				dotIndicatorContainer.removeView(mIndicator);
				setUpListeners();
				updateCardCount();
				mViewPager.setCurrentItem(0);
			}
			mViewPager.getAdapter().notifyDataSetChanged();
			if(mALStoredCards.size() == 0) noCardsTextView.setVisibility(View.VISIBLE);
			else noCardsTextView.setVisibility(View.INVISIBLE);
			Log.d(TAG, "In WaysToPayFragment::updateUI(): "+mALStoredCards.size());
		}
		else {
			noCardsTextView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * Adapter that supports Credit/Debit cards ViewPager
	 *
	 */
	private class CreditCardsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return creditCardsCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((RelativeLayout) arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			RelativeLayout creditCard = null;
			switch (position%3) {
			case 0:
				creditCard = creditCard1;
				break;
			case 1:
				creditCard = creditCard2;
				break;
			case 2:
				creditCard = creditCard3;
				break;

			default:
				break;
			}
			
			if(creditCard.getParent() != null) {
				ViewGroup parent = (ViewGroup) creditCard.getParent();
				parent.removeView(creditCard);
				container.removeView(creditCard);
			}
			creditCard.setVisibility(View.VISIBLE);
			LinearLayout cardInfoContainer = (LinearLayout) creditCard.getChildAt(0);

			if(mALStoredCards != null 
					&& mALStoredCards.size()>0) {
				((TextView)cardInfoContainer.getChildAt(0)).setText("**** **** **** "+mALStoredCards.get(position).mCardNo);
				String expiry = mALStoredCards.get(position).mExpDate;
				if(expiry.length() < 6) {
					expiry = "0"+expiry;
				}
				String expiryMonth = expiry.substring(0, 2);
				String expiryYear = expiry.substring(4, 6);
				((TextView)cardInfoContainer.getChildAt(1)).setText("Exp "+expiryMonth+"/"+expiryYear);
				((TextView)cardInfoContainer.getChildAt(2)).setText(mALStoredCards.get(position).mCardType);
				creditCard.getChildAt(1).setVisibility(View.VISIBLE);

				Button cardTypeLogo = (Button) creditCard.getChildAt(2);
				cardTypeLogo.setVisibility(View.VISIBLE);
				if(mALStoredCards.get(position).mCardType.contains("AMERICAN EXPRESS")
						|| mALStoredCards.get(position).mCardType.contains("AMEX")) { 
					cardTypeLogo.setBackground(getResources().getDrawable(R.drawable.amex));
				}
				else if(mALStoredCards.get(position).mCardType.contains("VISA")) { 
					cardTypeLogo.setBackground(getResources().getDrawable(R.drawable.visa));
				}
				else if(mALStoredCards.get(position).mCardType.contains("MASTERCARD")
						|| mALStoredCards.get(position).mCardType.contains("MC")) {
					cardTypeLogo.setBackground(getResources().getDrawable(R.drawable.master_card));
				}
				else if(mALStoredCards.get(position).mCardType.contains("DISCOVER")) {
					cardTypeLogo.setBackground(getResources().getDrawable(R.drawable.discover));
				}
				else {
					cardTypeLogo.setVisibility(View.INVISIBLE);
				}
					
			}

			creditCard.setVisibility(View.VISIBLE);
			try{
				((ViewPager) container).addView(creditCard);
			} catch (IllegalStateException e) {
				Log.d(TAG, "WaysToPay Exception: "+e.getLocalizedMessage());
				e.printStackTrace();
			}
			
			if(position == 0) {
				if (detector == null) {
					detector = new GestureDetector(parentActivity,
							new SwipeGestureDetector());
				}
				creditCard.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View arg0, MotionEvent event) {
						detector.onTouchEvent(event);
						return true;
					}
				});

			}
			return creditCard;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			
		}

		@Override
		public void startUpdate(ViewGroup container) {
			Log.d(TAG, "PagerAdapter::startUpdate");
			super.startUpdate(container);
		}
		@Override
		public void finishUpdate(ViewGroup container) {
			Log.d(TAG, "PagerAdapter::finishUpdate");
			if(mALStoredCards != null && (mALStoredCards.size() > 0)) {
				updateCardTitle();
			}
			super.finishUpdate(container);
		}
	}

	/**
	 * Sets DOT indicator below Credit/Debit Cards Display
	 */
	private void setPageIndicator() {
		if(dotIndicatorContainer.getChildCount() < 2) dotIndicatorContainer.addView(mIndicator);

		mIndicator = (CirclePageIndicator) parentActivity.findViewById(R.id.circleIndicator);
		((CirclePageIndicator) mIndicator).setRadius(parentActivity.getResources().getDimension(R.dimen.circle_indicator_radius));
		mIndicator.setViewPager(mViewPager);
		((CirclePageIndicator) mIndicator).setSnap(false);

		mIndicator
		.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrolled(int position,
					float positionOffset, int positionOffsetPixels) {
				if(pageScrollState == 1) offSet = positionOffset;
				finalState = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				pageScrollState = state;
				if(state == 0) {
					if(offSet == 0 && 
							finalState == 0 &&
							scrollDistance < 0) {
						launchReloadDenominations();
					}
					offSet = -1;
				}
			}
		});
	}	

	/**
	 * 
	 * Displays the result of "Update Card Info" in the form of Dialog
	 */
	private void showConfirmationDialog(String aMessage) {
		final Dialog dialog = new Dialog(parentActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comingsoon_dialog);
		dialog.setCancelable(false);
		TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_comingsoon_txtMsg);

		Button btnOk = (Button) dialog.findViewById(R.id.btn_cominsoon_Yes);

		if (aMessage != null) {
			tvMsg.setText(aMessage);
		}

		btnOk.setText(parentActivity.getResources().getString(R.string.ok));
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void launchReloadDenominations() {
		parentActivity.getReloadDenominationsFragment().setArguments(null);
		
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getReloadDenominationsFragment(), parentActivity.getReloadDenominationsFragment().getTag());
		transaction.commit();
	}

	protected void showDeleteCardConfirmationDialog(boolean isAutoRechargeCard){
		final Dialog dialog = new Dialog(parentActivity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.two_button_dialog);
		dialog.setCancelable(true);

		TextView tvTitle = (TextView) dialog.findViewById(R.id.txtTitle);
		tvTitle.setText("Please Confirm");
		TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_txtMsg);
		if(isAutoRechargeCard) {
			tvMsg.setText(R.string.delete_auto_recharge_card);
		}
		else {
			tvMsg.setText(R.string.delete_credit_card);
		}

		Button btnOk = (Button) dialog.findViewById(R.id.btn_Yes);
		btnOk.setText(R.string.ok);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				apiDeleteStoredCard(mALStoredCards.get(mViewPager.getCurrentItem()).mCardId);
			}
		});
		Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
		btnNo.setText(R.string.cancel_capital);
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void updateCardCount() {
		Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
		editor.putInt("previousCreditCardCount", mViewPager.getAdapter().getCount());
		editor.commit();
	}
	
	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			scrollDistance = distanceX;
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
}
