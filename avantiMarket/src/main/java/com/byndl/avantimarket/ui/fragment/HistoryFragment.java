package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMEventType;
import com.byndl.avantimarket.event.AMHistoryDao;
import com.byndl.avantimarket.event.AMPurchasedProduct;
import com.byndl.avantimarket.event.AMTransactionDetailDao;
import com.byndl.avantimarket.event.AMHistoryDao.TransactionDetails;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.HistoryAdapter;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.NetHttpInfo;
import com.incedo.network.NetHttpUtils;
import com.incedo.network.NetworkManagerClient;
import com.incedo.network.neterror.ApiErrorObj;
import com.incedo.network.neterror.InvalidArgException;

public class HistoryFragment extends BaseFragment {

	private int nextPage = 0;
	private ListView transactionsList = null;
	private LinearLayout transactionDetail = null, itemised_bill = null, footer;
	private RelativeLayout totalBillContainer = null, pointsEarnedContainer = null, balance_container = null;
	private ArrayList<TransactionDetails> transactionHistory = null;
	private TextView transaction_number, transaction_date, transaction_card_name, transaction_card_type, 
	transaction_type, total_bill_amount, points_earned, transaction_market_card, balance_amount;
	private WebView loadingAnimation = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.history_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		nextPage = 0;
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.account_history));
		initialiseClassMembers();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Sets up listeners to accept/respond to user events
	 */
	private void initialiseClassMembers() {
		transactionsList = (ListView) parentActivity.findViewById(R.id.transactions_list);
		transaction_number = (TextView) parentActivity.findViewById(R.id.transaction_number);
		transaction_date = (TextView) parentActivity.findViewById(R.id.transaction_date);
		transaction_card_name = (TextView) parentActivity.findViewById(R.id.transaction_card_name);
		transaction_market_card = (TextView) parentActivity.findViewById(R.id.transaction_market_card);
		transaction_card_type = (TextView) parentActivity.findViewById(R.id.transaction_card_type);
		transaction_type = (TextView) parentActivity.findViewById(R.id.transaction_type);
		total_bill_amount = (TextView) parentActivity.findViewById(R.id.total_bill_amount);
		balance_amount = (TextView) parentActivity.findViewById(R.id.balance_amount);
		points_earned = (TextView) parentActivity.findViewById(R.id.points_earned);

		itemised_bill = (LinearLayout) parentActivity.findViewById(R.id.itemised_bill);
		totalBillContainer = (RelativeLayout) parentActivity.findViewById(R.id.total_bill_container);
		balance_container = (RelativeLayout) parentActivity.findViewById(R.id.balance_container);
		pointsEarnedContainer = (RelativeLayout) parentActivity.findViewById(R.id.points_container);
		footer = (LinearLayout) parentActivity.getLayoutInflater().inflate(com.byndl.avantimarket.R.layout.loading_animation, null);
		loadingAnimation = (WebView) footer.findViewById(R.id.loading_anim);
		loadingAnimation.loadUrl("file:///android_asset/loading_spinner_white.gif");
		transactionsList.addFooterView(footer);
	}

	/**
	 * Defines Action to be taken when a Transaction item is selected
	 * @param sender
	 * @param position Position of the Transaction item selected/clicked 
	 */
	private void OnTransactionListItemClicked(Object sender, int position) {
		transactionsList.setItemChecked(position, true);
		HeaderViewListAdapter hvl = (HeaderViewListAdapter)transactionsList.getAdapter();
		HistoryAdapter historyAdapter = (HistoryAdapter)hvl.getWrappedAdapter();
		apiGetTransactionDetail(historyAdapter.getItem(position).mId);
	}

	@Override
	public void onResume() {
		super.onResume();
		parentActivity.setMenuDrawer();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "Support onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					if(transactionsList.getVisibility() == View.VISIBLE) {
						FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
						transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
						transaction.commit();
					}
					else {
						transactionsList.setVisibility(View.VISIBLE);
						transactionDetail.setVisibility(View.GONE);
						((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.account_history));
						parentActivity.setMenuDrawer();
					}
					return true;
				}
				return false;
			}
		});		

		getNextSetOfTransactions();
	}

	@Override
	public void updateUI() {

	}

	/**
	 * Invokes Network module and executes GET_HISTORY request for fetching transactions in the PageIndex provided
	 */
	private void apiGetHistory(int aCount, int aPageIndex) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_HISTORY)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_HISTORY,
						NetHttpUtils.HttpResposeDataModel.JSON), historyHandler,
						parentActivity)).execute(new AMHistoryDao(aCount, aPageIndex));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_HISTORY);
				if(aPageIndex == 0) parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes Network module and executes GET_TRANSACTION_DETAIL request for fetching transactions in the PageIndex provided
	 */
	private void apiGetTransactionDetail(String transactionId) {
		if( ! parentActivity.isRequestInProgress(AMEventType.EVENT_GET_TRANSACTION_DETAIL)) {
			try {
				(new NetworkManagerClient(new NetHttpInfo(parentActivity,
						NetHttpUtils.HttpCommand.GET,
						NetHttpUtils.HttpSchema.HTTPS,
						AMEventType.EVENT_GET_TRANSACTION_DETAIL,
						NetHttpUtils.HttpResposeDataModel.JSON), historyHandler,
						parentActivity)).execute(new AMTransactionDetailDao(transactionId));
				parentActivity.addToRequestsInProgress(AMEventType.EVENT_GET_TRANSACTION_DETAIL);
				parentActivity.showLoadingDialog();
			} catch (InvalidArgException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Will be passed to Network module to register for callback
	 */
	Handler historyHandler = new Handler() {
		public void handleMessage(Message msg) {
			parentActivity.dismissLoadingDialog();
			Log.d(TAG, "HistoryFragment::handleMessage");
			Object obj = msg.obj;
			switch (msg.what) {
			case AMEventType.EVENT_GET_HISTORY:
				parentActivity.removeFromRequestsInProgress(AMEventType.EVENT_GET_HISTORY);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					parentActivity.showNetworkErrorDialog(msg);
				} else {
					nextPage++;
					AMHistoryDao mHistoryDao = (AMHistoryDao) obj;
					transactionHistory = mHistoryDao.getTransactionsArray();
					Log.d(TAG,
							"Handler EVENT_GET_HISTORY result array length: "
									+ transactionHistory.size());

					if(transactionHistory.size() == 0) {
						nextPage = -1;
						if(transactionsList.getCount() == 0) {
							loadingAnimation.setVisibility(View.GONE);
							TextView loadingText = (TextView) footer.findViewById(R.id.loading_text);
							loadingText.setText("No transaction history in records.");
							
//							footer.setVisibility(View.GONE);
						}
					}

					if(transactionsList.getCount() == 0) {
						transactionsList.setAdapter(new HistoryAdapter(parentActivity,
								R.layout.history_item, transactionHistory));
						transactionsList.setDivider(null);
						transactionsList
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
								Log.d(TAG, "In onItemClick");
								OnTransactionListItemClicked(null, position);
							}
						});
					}
					else if(transactionsList.getCount() > 0) {
						HeaderViewListAdapter hvl = (HeaderViewListAdapter)transactionsList.getAdapter();
						HistoryAdapter historyAdapter = (HistoryAdapter)hvl.getWrappedAdapter();
						historyAdapter.addAll(transactionHistory);
						historyAdapter.notifyDataSetChanged();
					}
				}
				break;

			case AMEventType.EVENT_GET_TRANSACTION_DETAIL:
				parentActivity.removeFromRequestsInProgress(AMEventType.EVENT_GET_TRANSACTION_DETAIL);
				if (msg.obj.getClass().equals(ApiErrorObj.class)) {
					parentActivity.showNetworkErrorDialog(msg);
				} else {
					String transactionType = ((AMTransactionDetailDao)obj).mTransactionType;
					String transactionAmount = ((AMTransactionDetailDao)obj).mAmount;
					updateTitle(transactionType, transactionAmount);

					if(transactionDetail == null) {
						transactionDetail = (LinearLayout) parentActivity.findViewById(R.id.transaction_detail);
					}
					transactionsList.setVisibility(View.GONE);
					transactionDetail.setVisibility(View.VISIBLE);
					updateTransactionDetails((AMTransactionDetailDao)obj);
				}
				break;
			default:
				break;
			}
		};
	};

	private void updateTransactionDetails(AMTransactionDetailDao selectedTransaction) {
		resetTransactionDetails();
		transaction_number.append(selectedTransaction.mId);
		transaction_date.append(selectedTransaction.mDate);
		transaction_type.setText(selectedTransaction.mTransactionType);
		if(Integer.parseInt(selectedTransaction.mPoints) > 0) {
			points_earned.setText(selectedTransaction.mPoints);
		}
		else {
			pointsEarnedContainer.setVisibility(View.INVISIBLE);
		}

		LayoutInflater inflator = ((Activity)parentActivity).getLayoutInflater();

		if(selectedTransaction.mTransactionType.contains("Create")) {
			transaction_number.setVisibility(View.GONE);
			transaction_market_card.setVisibility(View.VISIBLE);
			transaction_market_card.append(selectedTransaction.mMarketcard);		}
		else {
			transaction_number.setVisibility(View.VISIBLE);
			transaction_market_card.setVisibility(View.GONE);
		}
		
		if(selectedTransaction.mCardType.length() != 0 &&
				(selectedTransaction.mTransactionType.contains("Recharge") ||
				selectedTransaction.mTransactionType.contains("AutoReload"))) {
			transaction_card_type.setVisibility(View.VISIBLE);
			transaction_card_type.append(selectedTransaction.mCardType);
		}
		else {
			transaction_card_type.setVisibility(View.GONE);
		}
		
		if( ! selectedTransaction.mTransactionType.contains("Create") &&
				! selectedTransaction.mTransactionType.contains("Consume")) {

			LinearLayout reloadAmount = (LinearLayout)inflator.inflate(R.layout.itemised_bill_element, null);
			((TextView)reloadAmount.findViewById(R.id.item_name)).setText("Amount");
			if(selectedTransaction.mAmount.isEmpty()) {
				((TextView)reloadAmount.findViewById(R.id.item_cost)).setText("$0.00");
			}
			else {
				((TextView)reloadAmount.findViewById(R.id.item_cost)).setText("$"+selectedTransaction.mAmount);
			}
			itemised_bill.addView(reloadAmount);
		}

		if(selectedTransaction.mTransactionType.contains("Consume")) {
			if(selectedTransaction.mAmount.contains("-")) {
				total_bill_amount.setText("$"+selectedTransaction.mAmount.replace("-", ""));
			}
			else {
				total_bill_amount.setText("$"+selectedTransaction.mAmount);
			}

			transaction_type.setText("Purchase:");
			// Adding Itemised Bill
			ArrayList<AMPurchasedProduct> items = selectedTransaction.mALProducts;
			for(int i=0; i< selectedTransaction.mALProducts.size(); i++) {
				LinearLayout item = (LinearLayout)inflator.inflate(R.layout.itemised_bill_element, null);
				((TextView)item.findViewById(R.id.item_name)).setText(items.get(i).mName);
				String price = items.get(i).mPrice;
				((TextView)item.findViewById(R.id.item_cost)).setText("$"+AMUtility.getRoundedBalance(Double.parseDouble(price)));
				itemised_bill.addView(item);
			}
			// Adding Tax component
			LinearLayout tax = (LinearLayout)inflator.inflate(R.layout.itemised_bill_element, null);
			((TextView)tax.findViewById(R.id.item_name)).setText(R.string.tax);
			if(selectedTransaction.mTax.isEmpty()) {
				((TextView)tax.findViewById(R.id.item_cost)).setText("$0.00");
			}
			else {
				((TextView)tax.findViewById(R.id.item_cost)).setText("$"+AMUtility.getRoundedBalance(Double.parseDouble(selectedTransaction.mTax)));
			}
			itemised_bill.addView(tax);
			LayoutParams params = (LayoutParams) tax.getLayoutParams();
			params.setMargins(0, parentActivity.getResources().getDimensionPixelSize(R.dimen.small_margin), 0, 0);
			tax.setLayoutParams(params);
		}
		else {
			totalBillContainer.setVisibility(View.GONE);
		}
		if(selectedTransaction.mTransactionType.contains("Create")) {
			balance_container.setVisibility(View.GONE);
		}
		else {
			if(selectedTransaction.mBalance.contains("-")) {
				balance_amount.setText("$"+selectedTransaction.mBalance.replace("-", ""));
			}
			else {
				balance_amount.setText("$"+selectedTransaction.mBalance);
			}
		}
	}

	private void resetTransactionDetails() {
		transaction_number.setText(R.string.transaction_number);
		transaction_date.setText(R.string.transaction_date);
		transaction_card_name.setText(R.string.transaction_card_name);
		transaction_market_card.setText(R.string.transaction_market_card);
		transaction_card_type.setText(R.string.transaction_card_type);
		totalBillContainer.setVisibility(View.VISIBLE);
		balance_container.setVisibility(View.VISIBLE);
		pointsEarnedContainer.setVisibility(View.VISIBLE);
		itemised_bill.removeAllViews();
	}
	
	private void updateTitle(String transactionType, String transactionAmount) {
		if(transactionAmount.contains("-")) {
			transactionAmount = transactionAmount.replace("-", "");
		}
		if(transactionType.contains("Recharge") && 
				! transactionType.contains("Decline")) {
			transactionType = "Reload";
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(transactionType+": $"+transactionAmount);
		}
		else if(transactionType.contains("AutoReload")) {
			if(transactionType.contains("MMC")) {
				transactionType = transactionType.replace("MMC", "");
			}
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(transactionType+": $"+transactionAmount);
		}
		else if(transactionType.contains("CreditBonus")) {
			transactionType = "Bonus";
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(transactionType+": $"+transactionAmount);
		}
		else if(transactionType.contains("Consume")) {
			transactionType = "Purchase";
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(transactionType+": $"+transactionAmount);
		}
		else {
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(transactionType+": $"+transactionAmount);
		}
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setImageResource(R.drawable.arrow_left);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				transactionsList.setVisibility(View.VISIBLE);
				transactionDetail.setVisibility(View.GONE);
				((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.account_history));
				parentActivity.setMenuDrawer();
			}			
		});    	
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		transactionDetail = null;
	}
	
	public void getNextSetOfTransactions() {
		if(nextPage != -1) {
			apiGetHistory(20, nextPage);
		}
		else {
			footer.setVisibility(View.GONE);
			transactionsList.removeFooterView(footer);
		}
	}
}
