/**
 * @brief       This class is used to display Thank you message to end user after successfully filing issue.
 * @file        ContactUsThankyou.java
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief	This class is used to display Thank you message to end user after successfully filing issue.
 * 
 */
public class ContactUsThankyou extends BaseFragment {
	
	private RelativeLayout mRL_Thankyou;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_us_thankyou, container, false);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.contact_us).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		
		mRL_Thankyou = ((RelativeLayout)parentActivity.findViewById(R.id.rl_thank_you));
		mRL_Thankyou.setBackgroundColor(parentActivity.getResources().getColor(R.color.orange));
		
		TextView tv_message = ((TextView)parentActivity.findViewById(R.id.tv_thankyou_msg));
		tv_message.setText(parentActivity.getResources().getString(R.string.contact_us_thankyou_speech));
		tv_message.setGravity(Gravity.CENTER);
		
		ImageView smily = ((ImageView)parentActivity.findViewById(R.id.iv_smily));
		smily.setImageResource(R.drawable.orange_smily);

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
	}
	
	/**
	 * Launch Support screen from current screen.
	 * 
	 */
	private void launchSupport() {
		parentActivity.getContactUsFragment().setArguments(null);
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getSupportFragment(), parentActivity.getSupportFragment()
				.getTag());
		transaction.commit();
	}
	
	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}

}
