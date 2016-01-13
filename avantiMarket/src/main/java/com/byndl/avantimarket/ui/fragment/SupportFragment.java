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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * @brief	This class is used to show options available on support screen.
 * <br> First option is to get helpful hints to use app.
 * <br> Second option will go to Contact us screen.
 * <br> Third option is to give feedback.
 * 
 */
public class SupportFragment extends BaseFragment {

	private Button mBtnHelpfulHints;
	private Button mBtnContactUs;
	private Button mBtnYourFeedback;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.support_fragment_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.support).toUpperCase());
	
		mBtnHelpfulHints = (Button)parentActivity.findViewById(R.id.BtnHelpfulHints);
		mBtnHelpfulHints.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getHelpfulHowToFragment(), parentActivity.getHelpfulHowToFragment().getTag());
				transaction.commit();
			}
		});
		
		mBtnContactUs= (Button)parentActivity.findViewById(R.id.BtnContactUs);
		mBtnContactUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				parentActivity.getContactUsFragment().setArguments(null);
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getContactUsFragment(), parentActivity.getContactUsFragment().getTag());
				transaction.commit();
			}
		});
		if(! AMPreferenceManager.getPrefInstance().getPrefs().getBoolean("showContactUsFlag", false)) {
			mBtnContactUs.setVisibility(View.GONE);
		}
		
		
		mBtnYourFeedback = (Button)parentActivity.findViewById(R.id.BtnYourFeedback);
		mBtnYourFeedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getFeedbackFragment(), parentActivity.getFeedbackFragment().getTag());
				transaction.commit();
			}
		});

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
					FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
					transaction.commit();
					return true;
				}
				return false;
			}
		});
		
		MixPanelEvents.pageView(getResources().getString(R.string.support));
	}
	
	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}
}
