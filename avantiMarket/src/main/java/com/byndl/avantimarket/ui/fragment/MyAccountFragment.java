package com.byndl.avantimarket.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

public class MyAccountFragment extends BaseFragment {

	private LinearLayout container;
	private Button doneButton, cancelButton;
	private RelativeLayout editInfo, resetPassword, resetPin, terms;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.my_account_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText((parentActivity.getResources().getString(R.string.my_account)).toUpperCase());
	}

	private void initialiseClassMembers() {
		editInfo = (RelativeLayout)parentActivity.findViewById(R.id.edit_personal_info);
		((TextView)((LinearLayout)editInfo.getChildAt(0)).getChildAt(0)).setText(R.string.edit_personal_info);

		resetPassword = (RelativeLayout)parentActivity.findViewById(R.id.reset_password);
		((TextView)((LinearLayout)resetPassword.getChildAt(0)).getChildAt(0)).setText(R.string.reset_password);

		resetPin = (RelativeLayout)parentActivity.findViewById(R.id.reset_pin);
		((TextView)((LinearLayout)resetPin.getChildAt(0)).getChildAt(0)).setText(R.string.reset_pin);
		resetPin.getChildAt(1).setVisibility(View.INVISIBLE);

		terms = (RelativeLayout)parentActivity.findViewById(R.id.terms);
		((TextView)((LinearLayout)terms.getChildAt(0)).getChildAt(0)).setText(R.string.terms);
		terms.getChildAt(1).setVisibility(View.INVISIBLE);

		int childHeight = parentActivity.getResources().getDimensionPixelSize(R.dimen.regular_textview_height);
		container = (LinearLayout) parentActivity.findViewById(R.id.m_container);
		for(int index=1; index < container.getChildCount(); index++) {
			if(container.getChildAt(index).toString().contains("RelativeLayout")) {
				RelativeLayout child = (RelativeLayout)container.getChildAt(index);
				ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
				layoutParams.height = childHeight;
				child.setLayoutParams(layoutParams);	
				child.setBackground(parentActivity.getResources().getDrawable(R.drawable.button_white_green_background));
			}
			else Log.d(TAG, "Child Class: "+container.getChildAt(index).toString());
		}
		resetPassword.setSelected(true);
		
		doneButton = (Button) parentActivity.findViewById(R.id.m_doneButton);
		cancelButton = (Button) parentActivity.findViewById(R.id.m_cancelButton);
	}

	@Override
	public void updateUI() {

	}

	@Override
	public void onResume() {
		super.onResume();
		parentActivity.setMenuDrawer();
		initialiseClassMembers();
		setUpListeners();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "MyAccountFragment onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchMyAvanti();
					return true;
				}
				return false;
			}
		});		
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
	 * Launches Home Screen
	 */
	private void launchEditReset() {
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getEditResetFragment(), parentActivity.getEditResetFragment()
				.getTag());
		transaction.commit();
	}

	private void setUpListeners() {
		editInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString("title", parentActivity.getResources().getString(R.string.edit_personal_info));
				parentActivity.getEditResetFragment().setArguments(args);
				Log.d(TAG, "Argument In MyAccountFragment: "+parentActivity.getEditResetFragment().getArguments().getString("title"));
				launchEditReset();
			}
		});
		resetPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString("title", parentActivity.getResources().getString(R.string.reset_password));
				parentActivity.getEditResetFragment().setArguments(args);
				launchEditReset();
			}
		});
		resetPin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString("title", parentActivity.getResources().getString(R.string.reset_pin));
				parentActivity.getEditResetFragment().setArguments(args);
				launchEditReset();
			}
		});
		terms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				parentActivity.showTermsAndConditions(null);		
			}
		});
		
		doneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMyAvanti();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMyAvanti();
			}
		});
	}
}
