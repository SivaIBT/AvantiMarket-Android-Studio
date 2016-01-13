/**
 * @brief       This class is used to display MyMarket Specials (Discounts and Promotions).
 * @file        MyMarketSpecialsFragment.java
 * @version     1.1
 * @author      siva.rajendhra
 * @date        06-Nov-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.SpecialsContainerAdapter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * @brief	This class is used to display MyMarket Specials (Discounts and Promotions) 
 * <br> Contains four Tabs to select the category
 * <br> First Tab displays All Specials
 * <br> Second Tab displays In Progress Specials
 * <br> Third Tab displays Expiring Soon Specials
 * <br> Fourth Tab displays Just Added Specials
 */
public class SpecialsContainerFragment extends BaseFragment {

	private View mView = null;
	private SpecialsContainerAdapter specialsAdapter;
	private TabPageIndicator specialsTabIndicator;
	private ViewPager specialsPager;
	private boolean isNewLaunch = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.specials_container_fragment, container, false);

		specialsTabIndicator = (TabPageIndicator) mView.findViewById(R.id.specials_tab_indicator);
		specialsPager = (ViewPager) mView.findViewById(R.id.specials_pager);

		specialsAdapter = new SpecialsContainerAdapter(getChildFragmentManager());
		specialsPager.setAdapter(specialsAdapter);
		specialsTabIndicator.setViewPager(specialsPager);
		return mView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.my_market_specials).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		isNewLaunch = true;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchDashBoard();
			}			
		});

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchDashBoard();
					return true;
				}
				return false;
			}
		});
		
		LinearLayout tabsContainer = (LinearLayout)specialsTabIndicator.getChildAt(0);
		for(int i=0; i<4; i++) {
			((TextView)tabsContainer.getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.semi_regular_text_size));
		}
		
		if(isNewLaunch) specialsPager.setCurrentItem(0);
	}
	
	/**
	 * Launch DashBoard screen from current screen.
	 * 
	 */
	private void launchDashBoard() {
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		transaction.commit();
	}
	
	@Override
	public void updateUI() {
		
	}

	@Override
	public void onPause() {
		super.onPause();
		isNewLaunch = false;
	}
}
