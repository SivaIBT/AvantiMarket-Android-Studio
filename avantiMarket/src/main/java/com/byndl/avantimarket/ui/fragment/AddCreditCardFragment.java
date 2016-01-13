/**
 * @brief       For updating an existing Credit/Debit Card info (or) For adding a new Credit/Debit Card 
 * @file        AddCreditCardFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        02-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * 
 * @brief For updating an existing Credit/Debit Card info (or) For adding a new Credit/Debit Card
 *
 */
public class AddCreditCardFragment extends BaseFragment {

	private EditText cardNumber, cardExpiry, zipEditText, cvv;
	private Button doneButton, cancelButton;
	InputMethodManager inputManager;
	private ImageButton makePrimaryButton;
	private TextView makePrimaryLabel;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.add_credit_card_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		inputManager = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		setUpListeners();
		update();
	}

	/**
	 * For updating the widgets with relevant info 
	 */
	private void update() {

		Bundle args = getArguments();
		if(args != null) {
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(args.getString("title"));
			Log.d(TAG, "cardNumber: "+args.getString("cardNumber"));
			cardNumber.setHint(args.getString("cardNumber"));

			if(args.getString("title").equalsIgnoreCase("UPDATE INFO")) {
				zipEditText.setHint(""+args.getInt("zip"));
			}
			int primaryWayToPayIndex = AMPreferenceManager.getPrefInstance().getPrefs().getInt("primaryWayToPayIndex", 0);
			if(args.getInt("wayToPayIndex", 0) == primaryWayToPayIndex) {
				makePrimaryButton.setSelected(true);
			}
		}
	}

	/**
	 * Sets up listeners to accept/respond to user events/input
	 */
	private void setUpListeners() {
		cardNumber = (EditText)parentActivity.findViewById(R.id.m_cardNumber);
		cardExpiry = (EditText)parentActivity.findViewById(R.id.m_cardExpiry);
		zipEditText = (EditText)parentActivity.findViewById(R.id.m_zipTextField);
		cvv = (EditText)parentActivity.findViewById(R.id.m_cvvTextField);
		doneButton = (Button)parentActivity.findViewById(R.id.m_saveButton);
		cancelButton = (Button)parentActivity.findViewById(R.id.m_cancel_Button);

		doneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				inputManager.hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				Bundle args = new Bundle();
				args.putString("cardNumber", cardNumber.getText().toString());
				if(makePrimaryButton.isSelected()) {
					int displayedWayToPayIndex = getArguments().getInt("wayToPayIndex", 0);
					Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
					editor.putInt("primaryWayToPayIndex", displayedWayToPayIndex);
					editor.commit();
				}
				else {
					int primaryWayToPayIndex = AMPreferenceManager.getPrefInstance().getPrefs().getInt("primaryWayToPayIndex", 0);
					if(getArguments().getInt("wayToPayIndex", 0) == primaryWayToPayIndex) {
						if(mALStoredCards != null && (mALStoredCards.size() > 1)) {
							if(primaryWayToPayIndex == 0) {
								primaryWayToPayIndex ++;
							}
							else {
								primaryWayToPayIndex --;
							}
							Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
							editor.putInt("primaryWayToPayIndex", primaryWayToPayIndex);
							editor.commit();
						}						
					}
				}
				parentActivity.getWaysToPayFragment().setArguments(args);
				launchWaysToPay();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				parentActivity.getWaysToPayFragment().setArguments(null);
				launchWaysToPay();
			}
		});

		makePrimaryLabel = (TextView)parentActivity.findViewById(R.id.m_makePrimaryLabel);
		makePrimaryLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(makePrimaryButton.isSelected()) makePrimaryButton.setSelected(false);
				else makePrimaryButton.setSelected(true);
				hideKeyBoard();
			}
		});

		makePrimaryButton = (ImageButton)parentActivity.findViewById(R.id.m_makePrimarySelection);
		makePrimaryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(makePrimaryButton.isSelected()) makePrimaryButton.setSelected(false);
				else makePrimaryButton.setSelected(true);
				hideKeyBoard();
			}
		});
		
		cardNumber.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {			}
			public void afterTextChanged(Editable s) {
				AfterTextChanged(cardNumber);
			}
		});
		cardExpiry.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {			}
			public void afterTextChanged(Editable s) {
				AfterTextChanged(cardExpiry);
			}
		});
		zipEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { 			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {			}
			public void afterTextChanged(Editable s) {
				AfterTextChanged(zipEditText);
			}
		});
		cvv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) {
				AfterTextChanged(cvv);
			}
		});
	}

	/**
	 * Validates the info entered by User
	 * @param field Modified field
	 */
    private void AfterTextChanged(EditText field)
    {
        if (field != null)
        {
        	if(field == cardNumber) {
        		if(cardNumber.getText().length() == 16)
        			hideKeyBoard();
        	}
        	if(field == cardExpiry) {
        		if(cardExpiry.getText().length() == 6)
        			hideKeyBoard();
        	}
        	if(field == zipEditText) {
        		if(zipEditText.getText().length() == 5)
        			hideKeyBoard();
        	}
        	if(field == cvv) {
        		if(cvv.getText().length() == 3)
        			hideKeyBoard();
        	}
        }
    }
	@Override
	public void onResume() {
		super.onResume();

		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchWaysToPay();
			}			
		});    	

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "AddCCf onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchWaysToPay();
					return true;
				}
				return false;
			}
		});		

		setKeyBoardListener();
	}

	/**
	 * Launches Credit Cards Display Screen
	 */
	private void launchWaysToPay() {
		hideKeyBoard();
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getWaysToPayFragment(), parentActivity.getWaysToPayFragment().getTag());
		transaction.commit();
	}

	@Override
	public void updateUI() {

	}

	/**
	 * Sets up listener to hide keyboard as per user input
	 */
	private void setKeyBoardListener() {
		final RelativeLayout activityRootView = (RelativeLayout)parentActivity.findViewById(R.id.activityRoot);
		activityRootView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyBoard();
				return false;
			}
		});
	}

	/**
	 * Hides Keyboard
	 */
	private void hideKeyBoard(){ 
		if(inputManager == null) {
			inputManager = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputManager.hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();	
	}

	@Override
	public void onPause() {
		super.onPause();
		hideKeyBoard();
	}
}
