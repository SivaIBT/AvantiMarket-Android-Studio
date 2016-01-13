/**
 * @brief		This will be launched if user is "Not registered" and want to know the process       
 * @file        NotRegisteredActivity.java
 * @version     1.0.1
 * @author      siva.rajendhra
 * @date        30-Oct-2015
 * @copyright   incedo inc.
 * 
 */

package com.byndl.avantimarket.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * 
 * @brief This will be launched if user is "Not registered" and want to know the process 
 *
 */
public class NotRegisteredActivity extends BaseActivity {

	private ScrollView activityRootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.not_registered_layout);

		// Setting Action Bar ++
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.nav_bar_layout);
		((TextView)findViewById(R.id.m_navBarTitle)).setText(getResources().getString(R.string.how_to_register));
		ImageButton menuButton = (ImageButton)findViewById(R.id.m_menuButton);
		menuButton.setImageResource(R.drawable.arrow_left);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(context, EmailLoginActivity.class);
				startActivity(intent);
				finish();
			}			
		});    	
		// Setting Action Bar --

		activityRootView = (ScrollView)findViewById(R.id.m_scrollContainer);
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		activityRootView.setFocusableInTouchMode(true);
		activityRootView.requestFocus();
		activityRootView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					final Intent intent = new Intent(context, EmailLoginActivity.class);
					startActivity(intent);
					finish();
					
					return true;
				}
				return false;
			}
		});		
		
		MixPanelEvents.pageView(getResources().getString(R.string.how_to_register));
	}

}
