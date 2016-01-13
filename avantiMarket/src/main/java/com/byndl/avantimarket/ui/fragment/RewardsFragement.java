/**
 * @brief       This class is used to display Rewards screen.
 * @file        RewardsFragement.java
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief	This class is used to display available rewards to the user.
 * 
 */
public class RewardsFragement extends BaseFragment{

	private String[] mItemNames = {"Lays", "Pepsi", "Snickers"};
	private String[] mItemPromotion = {"Buy 2 get 1 free (Ends: 5/10/2015)", 
			"Buy 3 get 1 free (Ends: 5/08/2015)", 
			"Buy 5 get 1 free (Ends: 5/03/2015)"};
	private int[] mItemReward = {3,2,4};
	
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.rewards_fragment, container, false);
	}

	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.rewards).toUpperCase());
		
		ListView rewardsList = (ListView) parentActivity.findViewById(R.id.LV_rewardsList);
		rewardsList.setAdapter(new RewardsListAdapter(R.layout.rewards_item,
				mItemNames));
		
	}
	
	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
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
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchMyAvanti();
					return true;
				}
				return false;
			}
		});
		
		RelativeLayout currentDiscountButton = (RelativeLayout)parentActivity.findViewById(R.id.rl_bottomCurrentDiscount);
		currentDiscountButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentTransaction transaction = parentActivity
						.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity
						.getCurrentDiscountFragment(), parentActivity.getCurrentDiscountFragment()
						.getTag());
				transaction.commit();
			}
		});
	}
	
	/**
	 * Launch main screen from current screen.
	 * 
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
	 * @brief	This class is used to create a rewards item view.
	 * The rewards item contains item name, promotion and available rewards points.
	 * 
	 */
	public class RewardsListAdapter extends ArrayAdapter<String>{
		
		/**
		 *  Constructor
		 *  
		 * @param txtViewResourceId - Item view resource ID.
		 * @param objects - Objects to display.
		 */
		public RewardsListAdapter(int txtViewResourceId, String[] objects) {
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
			View rewardItem = inflater.inflate(R.layout.rewards_item, parent,false);
			
			TextView itemName = (TextView) rewardItem.findViewById(R.id.txt_rewardsItemName);
			itemName.setText(mItemNames[position]);
			
			TextView itemPromotion = (TextView) rewardItem.findViewById(R.id.txt_rewardsItemPromotion);
			itemPromotion.setText(mItemPromotion[position]);
			
			SeekBar seekBar = (SeekBar) rewardItem.findViewById(R.id.seekBar_rewards);
			seekBar.setProgress(mItemReward[position]);
//			seekBar.setEnabled(false);
			
			seekBar.setOnTouchListener(new OnTouchListener(){
				  @Override
				  public boolean onTouch(View v, MotionEvent event) {
				    return true;
				 }
				});
			
			final TextView seekBarValue = (TextView) rewardItem.findViewById(R.id.tv_rewardsSeekBarValue);
			seekBarValue.setText(Integer.toString(mItemReward[position])+ "/" + seekBar.getMax());
			
//			  seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//				  int progress = 0;
//				  
//				  @Override
//				  public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
//					  progress = progresValue;
//				  }
//				
//				  @Override
//				  public void onStartTrackingTouch(SeekBar seekBar) {
//				  }
//				
//				  @Override
//				  public void onStopTrackingTouch(SeekBar seekBar) {
//					  seekBarValue.setText(progress + "/" + seekBar.getMax());
//				  }
//			   });

			return rewardItem;
		}
	}
}
