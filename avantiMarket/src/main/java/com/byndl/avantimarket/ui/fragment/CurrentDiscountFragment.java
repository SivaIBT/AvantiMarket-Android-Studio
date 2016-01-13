/**
 * @brief       This class is used to display available current discounts.
 * @file        CurrentDiscountFragment.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.R.color;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief	This class is used to display available current discounts.
 * 
 */
public class CurrentDiscountFragment extends BaseFragment{
	
	private String[] mCurrentOffer = {"Fresh Food Friday", "Saturday Special", "Starbucks"};
	private String[] mCurrentDiscount = {"50% off on All food items", 
			"10% Off on diet coke", 
			"20% Off all Starbucks Cold Drinks"};
	private String[] mDiscountAvail = {"Offer available 2:00 - 3:00 pm friday", 
			"Offer available this saturday", 
			"Offer Expires 5/03/2015)"};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.current_discount_fragment, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.current_discount).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);

		ListView CurrentDiscList = (ListView) parentActivity.findViewById(R.id.LV_CurrentDiscount);
		CurrentDiscList.setAdapter(new CurrentDiscountListAdapter(R.layout.current_discount_item,
				mCurrentDiscount));

	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchRewards();
			}			
		});
		
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchRewards();
					return true;
				}
				return false;
			}
		});
		
	}
	
	/**
	 * Launch Rewards screen from current screen.
	 * 
	 */
	private void launchRewards() {
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getRewardsFragement(), parentActivity.getRewardsFragement()
				.getTag());
		transaction.commit();
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @brief	This class is used to create a discount item view.
	 * The discount item contains offer, available discount and expire of offer.
	 * 
	 */
	public class CurrentDiscountListAdapter extends ArrayAdapter<String>{

		/**
		 *  Constructor
		 *  
		 * @param txtViewResourceId - Item view resource ID.
		 * @param objects - Objects to display.
		 */
		public CurrentDiscountListAdapter(int txtViewResourceId, String[] objects) {
			super(parentActivity, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = parentActivity.getLayoutInflater();
			View discountItem = inflater.inflate(R.layout.current_discount_item, parent,false);
			if(position==0)
			{
				View v = (View)discountItem.findViewById(R.id.v_custDiscSeperator);
				v.setBackgroundColor(color.black);
			}
			
			TextView currentOffer = (TextView) discountItem.findViewById(R.id.tv_currentOffer);
			currentOffer.setText(mCurrentOffer[position]);
			
			TextView currentDiscount = (TextView) discountItem.findViewById(R.id.tv_currentOfferDiscount);
			currentDiscount.setText(mCurrentDiscount[position]);
			
			TextView discountAvail = (TextView) discountItem.findViewById(R.id.tv_currentOfferAvailibility);
			discountAvail.setText(mDiscountAvail[position]);
			
			return discountItem;
		}
	}

}
