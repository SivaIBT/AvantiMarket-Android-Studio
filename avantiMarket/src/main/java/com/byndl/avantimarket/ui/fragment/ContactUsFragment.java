/**
 * @brief       This class is used to display Contact Us screen.
 * @file        ContactUsFragment.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.R.color;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.HintAdapter;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;

/**
 * @brief	This class is used to display Contact Us screen.
 * <br> First dropdown contains support items.
 * <br> Second dropdown contains related issue items.
 * <br> Description can be updated as per related issue faced.
 * 
 */
public class ContactUsFragment extends BaseFragment implements OnItemSelectedListener, OnTouchListener{
	
	private Spinner mSpinnSupport;
	private Spinner mSpinnIssue;
	private EditText mET_Describe;
	private Button mBtn_Next;
	private String[] mDummyItem = {"", "Select Specific Issue"};

	private HintAdapter mAdapterSupport;
	private HintAdapter mAdapterIssue;
	private boolean isKeypadOn = false;
	private boolean mfirstTime = false;
	
	private long previousCallTimeStamp = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mfirstTime = true;
		return inflater.inflate(R.layout.contact_us_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("ContactUsFrag", "onActivityCreated");
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.contact_us).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
//		refreshUI();
		previousCallTimeStamp = -1;
		if(getArguments() != null) {
			Log.d(TAG, "Selections: "+getArguments().getInt("categoryIndex")+" "+getArguments().getInt("issueIndex"));
			
			mfirstTime = false;
			AMUtility.updateConsumerCategories = true;
			updateUI();
			mSpinnSupport.setSelection(getArguments().getInt("categoryIndex"));
		}
		else {
			mET_Describe = ((EditText)parentActivity.findViewById(R.id.et_describe));
			cleanupView();
			
			if(AMUtility.sfConsumerCategories == null) {
				InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
						AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				apiGetConsumerCategories();
			}
			else {
				mfirstTime = true;
				AMUtility.updateConsumerCategories = true;
				updateUI();
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("ContactUsFrag", "OnResume");
		
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
		
		int heightDiff = ((LinearLayout)parentActivity.findViewById(R.id.rl_contactUsFragment)).getRootView().getHeight();
		LayoutParams layoutParams = (LayoutParams) mET_Describe.getLayoutParams();
		layoutParams.height = heightDiff/3;
		mET_Describe.setLayoutParams(layoutParams);

		MixPanelEvents.pageView(getResources().getString(R.string.contact_us));
	}
	
	/**
	 * Used to refresh current display screen.
	 * 
	 */
	private void refreshUI()
	{
		mSpinnSupport = ((Spinner)parentActivity.findViewById(R.id.spinner_support));
		AddSupportItems();
		
		mSpinnSupport.setAdapter(mAdapterSupport);
		mSpinnSupport.setOnItemSelectedListener(this);
		
		mSpinnIssue = ((Spinner)parentActivity.findViewById(R.id.spinner_issue));
		mSpinnIssue.setOnItemSelectedListener(this);
		
		mET_Describe = ((EditText)parentActivity.findViewById(R.id.et_describe));
		mET_Describe.setEnabled(false);
		mET_Describe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isKeypadOn)
				{
					hideKeypad();
				}
				else
				{
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(mET_Describe, InputMethodManager.SHOW_FORCED);
					isKeypadOn = true;
				}
				
			}
		});
		
		mBtn_Next = ((Button)parentActivity.findViewById(R.id.btn_next));
		mBtn_Next.setEnabled(false);
		mBtn_Next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putInt("issueCategoryIndex", mSpinnIssue.getSelectedItemPosition());
				editor.putString("categoryType", mSpinnSupport.getSelectedItem().toString());
				editor.putString("issueType", mSpinnIssue.getSelectedItem().toString());
				editor.commit();
				
				Bundle args = new Bundle();
				args.putString("categoryType", mSpinnSupport.getSelectedItem().toString());
				args.putString("issueType", mSpinnIssue.getSelectedItem().toString());
				args.putString("description", mET_Describe.getText().toString());
				args.putInt("categoryIndex", mSpinnSupport.getSelectedItemPosition());
				args.putInt("issueIndex", mSpinnIssue.getSelectedItemPosition());
				Log.d("consumerAmsId: " , mSpinnSupport.getSelectedItem().toString()+" \n\r"
				+mSpinnIssue.getSelectedItem().toString()+" \n\r"
						+mET_Describe.getText().toString());
				parentActivity.getContactUsDidyousee().setArguments(args);
				cleanupView();
				
				FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_block, parentActivity.getContactUsDidyousee(), parentActivity.getContactUsDidyousee()
						.getTag());
				transaction.commit();
			}
		});
		
		mAdapterIssue = new HintAdapter(getActivity(),mDummyItem,
				R.layout.multiline_spinner_dropdown_item);
		mAdapterIssue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnIssue.setAdapter(mAdapterIssue);
		mSpinnIssue.setSelection(mAdapterIssue.getCount(),true);
		
		if(getArguments() == null) {
			
			mSpinnSupport.setSelection(mAdapterSupport.getCount(), true);
		}
		setOnTouchListeners();
		
		mET_Describe.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((event.getAction() == KeyEvent.ACTION_UP)
						&& (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
					launchSupport();
					return true;
				}
				return false;
			}
		});
		
		mET_Describe.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				mBtn_Next.setEnabled(true);
				mBtn_Next.setBackgroundResource(R.drawable.button_white_orange_background);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	/**
	 * Launch Support screen from current screen.
	 * 
	 */
	private void launchSupport() {
		cleanupView();
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getSupportFragment(), parentActivity.getSupportFragment()
				.getTag());
		transaction.commit();
	}
	
	
	/**
	 * Clear the data available at description box.
	 * 
	 */
	private void cleanupView()
	{
		if(mET_Describe!=null) {
			mET_Describe.setText("");
			mET_Describe.getText().clear();
		}
	}
	
	/**
	 * Get the items for Support dropdown from specified string array.
	 * 
	 */
	private void AddSupportItems()
	{
//		mAdapterSupport = new HintAdapter(getActivity(), parentActivity.getResources().getStringArray(R.array.support_spinner_items),
//				R.layout.multiline_spinner_dropdown_item);
		if(getArguments() == null &&
				! AMUtility.sfConsumerCategories.contains(parentActivity.getResources().getString(R.string.support_spinner_hint))) {
			AMUtility.sfConsumerCategories.add(parentActivity.getResources().getString(R.string.support_spinner_hint));
		}
		String[] mStringArray = new String[AMUtility.sfConsumerCategories.size()];
		
		mAdapterSupport = new HintAdapter(getActivity(), AMUtility.sfConsumerCategories.toArray(mStringArray),
				R.layout.multiline_spinner_dropdown_item);
		mAdapterSupport.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(parent.getId()==R.id.spinner_support)
		{
			if(mfirstTime)
			{
				Log.d("onItemSelected", "First time");
				mfirstTime = false;
				position = mAdapterSupport.getCount();
				Log.d("position", "First time: " + position);
				mSpinnSupport.setSelection(mAdapterSupport.getCount(), true);
				mSpinnIssue.setSelection(mAdapterIssue.getCount(),true);
			}
		}
		switch (parent.getId()) {
		case R.id.spinner_support:
			Log.d("Support Spinner", "spinner support selected "+System.currentTimeMillis());
			
			if(previousCallTimeStamp == -1 ||
					System.currentTimeMillis() - previousCallTimeStamp > 1000) {
				previousCallTimeStamp = System.currentTimeMillis();
			}
			else {
				previousCallTimeStamp = System.currentTimeMillis();
				return;
			}
			
			if(position == mSpinnSupport.getCount())
			{
				((TextView) parent.getChildAt(0)).setTextColor(color.light_grey);
				mSpinnIssue.setEnabled(false);
				mET_Describe.setEnabled(false);
				mBtn_Next.setEnabled(false);
				mBtn_Next.setBackgroundResource(R.color.light_grey);
			}
			else
			{
				Log.d("Support Item Selected: ", mSpinnSupport.getSelectedItem().toString());
				if(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(mSpinnSupport.getSelectedItem().toString(), null) == null) {
					InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
							AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
					apiGetConsumerIssues(mSpinnSupport.getSelectedItem().toString());
				}
				else {
					AMUtility.sfConsumerIssues = new ArrayList<String>(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(mSpinnSupport.getSelectedItem().toString(), null));
					AMUtility.updateConsumerIssues = true;
					updateUI();
				}
			}

			break;

		case R.id.spinner_issue:
			Log.d("Issue Spinner", "spinner Issue selected");
			if(position == mSpinnIssue.getCount())
			{
				((TextView) parent.getChildAt(0)).setTextColor(color.light_grey);
				mET_Describe.setEnabled(false);
				mBtn_Next.setEnabled(false);
				mBtn_Next.setBackgroundResource(R.color.light_grey);
			}
			else
			{
				Log.d("Issue Item Selected: ", mSpinnIssue.getSelectedItem().toString());
				mET_Describe.setEnabled(true);
				if(getArguments() != null) {
					mET_Describe.setText(getArguments().getString("description"));
				}
			}
			break;
		}
	}
	
	/**
	 * Register touch listeners for different UI controls on current screen.
	 * 
	 */
	private void setOnTouchListeners()
	{
		mSpinnSupport.setOnTouchListener(this);
		mSpinnIssue.setOnTouchListener(this);
		((LinearLayout)parentActivity.findViewById(R.id.rl_contactUsFragment)).setOnTouchListener(this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		if(AMUtility.updateConsumerCategories)
		{
			AMUtility.updateConsumerCategories = false;
			refreshUI();
		}
		else if(AMUtility.updateConsumerIssues)
		{
			if(AMPreferenceManager.getPrefInstance().getPrefs().getStringSet(mSpinnSupport.getSelectedItem().toString(), null) == null) {
				Set<String> issuesSet = new HashSet<String>(AMUtility.sfConsumerIssues);
				saveToInternalStorage(mSpinnSupport.getSelectedItem().toString(), issuesSet);
			}
			AMUtility.updateConsumerIssues = false;
			refreshIssueSpinner();
		}
		
	}

	private void refreshIssueSpinner()
	{
		AMUtility.sfConsumerIssues.add(parentActivity.getResources().getString(R.string.issue_spinner_hint));
		String[] mStringArray = new String[AMUtility.sfConsumerIssues.size()];
		mAdapterIssue = new HintAdapter(getActivity(), AMUtility.sfConsumerIssues.toArray(mStringArray),
				R.layout.multiline_spinner_dropdown_item);
		mAdapterIssue.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		mSpinnIssue.setAdapter(mAdapterIssue);
		mSpinnIssue.setEnabled(true);
		
		if(getArguments() == null ||
				 AMPreferenceManager.getPrefInstance().getPrefs().getInt("issueCategoryIndex", -1) == -1) {
			mSpinnIssue.setSelection(mAdapterIssue.getCount(),true);
		}
		else {
			mSpinnIssue.setSelection(getArguments().getInt("issueIndex"));		
			Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
			editor.putInt("issueCategoryIndex", -1);
			editor.commit();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.d("onTouch", " touched");
		switch (v.getId()) {
		case R.id.spinner_issue:
			hideKeypad();
			break;
			
		case R.id.spinner_support:
			hideKeypad();
			break;

		case R.id.rl_contactUsFragment:
			hideKeypad();
			break;
			
		case R.id.tv_Title:
			hideKeypad();
			break;
		}
		return false;
	}
	
	/**
	 * Hide soft keypad from the screen.
	 * 
	 */
	private void hideKeypad()
	{
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		isKeypadOn = false;
	}
	
	@Override
	public void onPause() {
		hideKeypad();
		hideSpinnerDropDown(mSpinnIssue);
		super.onPause();
	}
	
	/**
	 * Hides a spinner's drop down.
	 */
	public static void hideSpinnerDropDown(Spinner spinner) {
	    try {
	        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
	        method.setAccessible(true);
	        method.invoke(spinner);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
