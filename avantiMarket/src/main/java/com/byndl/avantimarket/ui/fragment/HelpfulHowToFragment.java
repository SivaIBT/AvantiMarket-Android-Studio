/**
 * @brief       This class is used to display Helpful how to screen.
 * @file        HelpfulHowToFragment.java
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * @brief	This class is used to display Helpful how to screen. 
 * <br> Contains three buttons to select the help category
 * <br> First button is for Using the market category
 * <br> Second button is for Using the app category
 * <br> Third button is for Common issues category
 */
public class HelpfulHowToFragment extends BaseFragment {

	private Button mBtnUsingTheMarket;
	private Button mBtnUsingTheApp;
	private Button mBtnCommonIssue;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.helpful_how_to_fragment_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.helpful_hints).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		
		mBtnUsingTheMarket = (Button)parentActivity.findViewById(R.id.BtnUsingTheMarket);
		mBtnUsingTheMarket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getUsingTheMarketFragment(), parentActivity.getUsingTheMarketFragment().getTag());
				transaction.commit();
			}
		});
		
		mBtnUsingTheApp = (Button)parentActivity.findViewById(R.id.BtnUsingTheApp);
		mBtnUsingTheApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getUsingTheAppFragment(), parentActivity.getUsingTheAppFragment().getTag());
				transaction.commit();
			}
		});
		
		mBtnCommonIssue = (Button)parentActivity.findViewById(R.id.BtnCommomIssue);
		mBtnCommonIssue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				parentActivity.getCommonIssuesFragment().setArguments(null);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getCommonIssuesFragment(), parentActivity.getCommonIssuesFragment().getTag());
				transaction.commit();

			}
		});
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchSupport();
			}			
		});

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchSupport();
					return true;
				}
				return false;
			}
		});
		
		MixPanelEvents.pageView(getResources().getString(R.string.helpful_hints));
	}
	
	/**
	 * Launch Support screen from current screen.
	 * 
	 */
	private void launchSupport() {
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getSupportFragment(), parentActivity.getSupportFragment()
				.getTag());
		transaction.commit();
	}
	
	@Override
	public void updateUI() {
		
	}

}
