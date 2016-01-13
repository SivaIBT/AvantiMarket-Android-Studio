/**
 * @brief       Adapter class for List of items in Side Menu
 * @file        NavigationDrawerAdapter.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        02-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byndl.avantimarket.event.AMHistoryDao.TransactionDetails;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.fragment.HistoryFragment;

/**
 * @brief       Adapter class for List of items in History screen
 */
public class HistoryAdapter extends ArrayAdapter<TransactionDetails>
{
	private int NORMAL_VIEW_TYPE = 1;
	private MyAvantiActivity context;
	
	public HistoryAdapter(Context context, int resource, ArrayList<TransactionDetails> objects) 
	{
		super(context, resource, objects);
		this.context = (MyAvantiActivity) context; 
	}

	public int ViewTypeCount()
	{
		return 1; 
	}

	public boolean IsEnabled(int position)
	{
		if(getItem(position) == null || getItem(position).mTransactionType.contains("CreateCard"))
		{
			return false;
		}
		else
		{
			return super.isEnabled(position);
		}
	}

	public  View getView(int position, View convertView, ViewGroup parent)
	{
		String date = getItem(position).mDate;
		date = date.substring(0, date.indexOf(" "));
		
		String transactionAmount = getItem(position).mAmount;
		if(Double.parseDouble(transactionAmount) == 0) transactionAmount = "0";
		
		String transactionType = getItem(position).mTransactionType;

		if(transactionType.contains("Recharge") && 
				! transactionType.contains("Decline")) {
			transactionType = "Reload";
		}
		if(transactionType.contains("AutoReload")) {
			transactionType = transactionType.replace("MMC", "");
		}
		if(transactionType.contains("Decline")) {
//			transactionType = transactionType.replace("MMC", "");
		}
		if(transactionType.contains("Consume")) {
			transactionType = "Purchase";
		}
		if(transactionType.contains("CreditBonus")) {
			transactionType = "Bonus";
		}
		if(transactionAmount.contains("-")) {
			transactionAmount = transactionAmount.replace("-", "");
		}
		if(convertView == null) {
			LayoutInflater inflator = ((Activity)context).getLayoutInflater();
			convertView = inflator.inflate(com.byndl.avantimarket.R.layout.history_item, null);
			((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.history_list_item_text)).setText(date+": "+transactionType+" $"+transactionAmount);
		}
		else {
			((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.history_list_item_text)).setText(date+": "+transactionType+" $"+transactionAmount);
		}
		
		if(position == getCount()-1) {
			getNextSetOfTransactions();
		}
		
		return convertView;
//		return super.getView(position, convertView, parent);
	}

	public int GetItemViewType(int position)
	{
		return NORMAL_VIEW_TYPE;
	}
	
	private void getNextSetOfTransactions() {
		List<Fragment> fragments = context.getSupportFragmentManager().getFragments();
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible() 
					&& fragment.toString().contains("HistoryFragment")) {
				((HistoryFragment) fragment).getNextSetOfTransactions();
			}
		}
	}
}