/**
 * @brief       Enables user to Add/Modify Name/Color/Category customization to Market cards
 * @file        UpdateMarketCardFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        07-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * 
 * @brief Enables user to Add/Modify Name/Color/Category customization to Market cards
 *
 */
public class UpdateMarketCardFragment extends BaseFragment {

	private String cardColor = "green";
	private Button doneButton, cancelButton;
	private ImageButton makePrimary, greenSelectionButton, darkBlueSelectionButton, skyBlueSelectionButton;
	private String selectedColor = "";
	private EditText cardName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.update_market_card_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.update_info));
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		if(inputManager == null) {
			inputManager = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		}

		setUpListeners();
		cardColor = getArguments().getString("color");
		updateCardDetails();
	}

	public void updateCardDetails() {
		if(getArguments().getBoolean("isPrimary")) makePrimary.setSelected(true);
		else makePrimary.setSelected(false);

		cardName.setText("");
		cardName.setHint(getArguments().getString("name"));

		Bundle args = getArguments();
		if(args != null) {
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(args.getString("title"));
		}

		if(cardColor.contains("green")) {
			greenSelectionButton.setSelected(true);
		}

		else if(cardColor.contains("dark")) {
			darkBlueSelectionButton.setSelected(true);
		}

		else {
			skyBlueSelectionButton.setSelected(true);
		}

	}

	/**
	 * Sets up Listeners to accept/respond to User events
	 */
	private void setUpListeners() {
		cardName = (EditText)parentActivity.findViewById(R.id.m_nameOfCard);
		cardName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	inputManager.showSoftInput(cardName, InputMethodManager.SHOW_IMPLICIT);
		    		cardName.setHint(getArguments().getString("name"));
		        }
		    }
		});
		
		cardName.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
					launchMarketCardsFragment();
					return true;
				}
				return false;
			}
		});		

		cardName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		makePrimary = (ImageButton)parentActivity.findViewById(R.id.m_makePrimaryCheckbox);
		makePrimary.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeyBoard();
				if(makePrimary.isSelected()) makePrimary.setSelected(false);
				else makePrimary.setSelected(true);
			}
		});

		greenSelectionButton = (ImageButton)parentActivity.findViewById(R.id.m_greenCheckbox);
		greenSelectionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeyBoard();
				if(! greenSelectionButton.isSelected()) {
					resetSelection();
					greenSelectionButton.setSelected(true);
				}
			}
		});

		darkBlueSelectionButton = (ImageButton)parentActivity.findViewById(R.id.m_darkBlueCheckbox);
		darkBlueSelectionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeyBoard();
				if(! darkBlueSelectionButton.isSelected()) {
					resetSelection();
					darkBlueSelectionButton.setSelected(true);
				}
			}
		});
		skyBlueSelectionButton = (ImageButton)parentActivity.findViewById(R.id.m_skyBlueCheckbox);
		skyBlueSelectionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeyBoard();
				if(! skyBlueSelectionButton.isSelected()) {
					resetSelection();
					skyBlueSelectionButton.setSelected(true);
				}
			}
		});

		doneButton = (Button)parentActivity.findViewById(R.id.m_doneButton);
		doneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveNameColorCategory();
				launchMarketCardsFragment();
			}
		});

		cancelButton = (Button)parentActivity.findViewById(R.id.m_cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCardsFragment();
			}
		});
	}

	/**
	 * Resets the Color selection
	 */
	private void resetSelection() {
		greenSelectionButton.setSelected(false);
		darkBlueSelectionButton.setSelected(false);
		skyBlueSelectionButton.setSelected(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCardsFragment();
			}			
		});    	

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "ReloadF onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchMarketCardsFragment();
					return true;
				}
				return false;
			}
		});		

		setKeyBoardListener();
		cardName.requestFocus();
	}

	/**
	 * Launches Market Cards Display Fragment
	 */
	private void launchMarketCardsFragment() {
		hideKeyBoard();
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getMarketCardsFragment(), parentActivity.getMarketCardsFragment().getTag());
		transaction.commit();
	}

	/**
	 * Sets up Listener to Hide KeyBoard as per User event
	 */
	private void setKeyBoardListener() {
		final LinearLayout activityRootView = (LinearLayout)parentActivity.findViewById(R.id.activityRoot);
		activityRootView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyBoard();
				return false;
			}
		});
	}

	/**
	 * Hides the Keyboard
	 */
	private void hideKeyBoard(){ 
		inputManager.hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();	
	}

	@Override
	public void onPause() {
		super.onPause();
		hideKeyBoard();
	}

	/**
	 * Saves the Name/Color selection of User
	 */
	private void saveNameColorCategory() {
		if((cardName.getText().toString().length() > 0) ||
				isCardColorUpdated()) {
			Set<String> categoryNameColor = new HashSet<String>(2);
			if(cardName.getText().toString().length() > 0) {
				categoryNameColor.add("name"+cardName.getText().toString());
			}
			else {
				categoryNameColor.add("name"+getArguments().getString("name"));
			}
			categoryNameColor.add("back"+selectedColor);
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putStringSet(getArguments().getString("scanCode"), categoryNameColor);
			editor.commit();
		}
		
		if((getArguments().getBoolean("isPrimary") != makePrimary.isSelected())
				&& (!getArguments().getBoolean("isPrimary"))) {
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putString("primaryScanCode", getArguments().getString("scanCode"));
			editor.commit();
			parentActivity.primaryScanCode = getArguments().getString("scanCode");
		}
	}

	/**
	 * Verifies whether Card color is updated or not
	 * @return
	 */
	private boolean isCardColorUpdated() {
		if(greenSelectionButton.isSelected()) selectedColor = getResources().getString(R.string.green); 
		if(darkBlueSelectionButton.isSelected()) selectedColor = getResources().getString(R.string.dark_blue);
		if(skyBlueSelectionButton.isSelected()) selectedColor = getResources().getString(R.string.sky_blue); 
		
		if(cardColor.contains("green")) {
			if(! greenSelectionButton.isSelected())
				return true;  
		}

		else if(cardColor.contains("dark")) {
			if(! darkBlueSelectionButton.isSelected())
				return true;  
		}

		else {
			if(! skyBlueSelectionButton.isSelected())
				return true;  
		}
		return false;
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}
}
