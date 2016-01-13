/**
 * @brief       This class is used to display Feedback screen.
 * @file        FeedbackFragment.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import android.content.Context;
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
import com.byndl.avantimarket.interfaces.ICreateCase;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.HintAdapter;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;

/**
 * @brief	This class is used to display Feedback screen.
 * <br> Dropdown contains feedback items.
 * <br> Description can be updated as per related feedback item.
 * 
 */
public class FeedbackFragment extends BaseFragment implements OnItemSelectedListener, OnTouchListener, ICreateCase{


	private Spinner mSpinnFeedback;
	private EditText mET_FeedbackDesc;
	private HintAdapter mAdapterFeedback;
	private Button mBtn_Submit;
	private boolean isKeypadOn = false;
	private View mView;
	private boolean mfirstTime = false;
	private boolean visibility = false;

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d("onCreateView", " onCreateView");
		mfirstTime = true;
		mView = inflater.inflate(R.layout.feedback_layout, container, false);
		return mView;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("onActivityCreated", " onActivityCreated");
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.your_feedback).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		initUI();
	}

	/**
	 * Register touch listeners for different UI controls on current screen.
	 */
	private void setOnTouchListeners()
	{
		mSpinnFeedback.setOnTouchListener(this);
		((LinearLayout)parentActivity.findViewById(R.id.rl_feedbackfragment)).setOnTouchListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("onResume", " onResume");

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

		if(AMUtility.sfFeedbackCategories == null) {
			InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
					AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
			apiGetFeedbackCategories();
		}
		else {
			updateUI();
		}

		int heightDiff = ((LinearLayout)parentActivity.findViewById(R.id.rl_feedbackfragment)).getRootView().getHeight();
		LayoutParams layoutParams = (LayoutParams) mET_FeedbackDesc.getLayoutParams();
		layoutParams.height = heightDiff/3;
		mET_FeedbackDesc.setLayoutParams(layoutParams);

		visibility = true;

		MixPanelEvents.pageView(getResources().getString(R.string.your_feedback));
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSpinnFeedback = null;

	}

	/**
	 * Launch Support screen from current screen.
	 */
	private void launchSupport() {

		mET_FeedbackDesc.getText().clear();
		FragmentTransaction transaction = parentActivity
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity
				.getSupportFragment(), parentActivity.getSupportFragment()
				.getTag());
		transaction.commit();
	}

	/**
	 * Get the items for Feedback dropdown from specified string array.
	 * 
	 */
	private void AddFeedbackItems()
	{
		if( ! AMUtility.sfFeedbackCategories.contains(parentActivity.getResources().getString(R.string.feedback_spinner_hint))) {
			AMUtility.sfFeedbackCategories.add(parentActivity.getResources().getString(R.string.feedback_spinner_hint));
		}
		String[] mStringArray = new String[AMUtility.sfFeedbackCategories.size()];

		mAdapterFeedback = new HintAdapter(parentActivity, AMUtility.sfFeedbackCategories.toArray(mStringArray),
				R.layout.multiline_spinner_dropdown_item);
		mAdapterFeedback.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		Log.d("onItemSelected", ""+pos);
		if(mfirstTime)
		{
			Log.d("onItemSelected", "First time");
			mfirstTime = false;
			pos = mAdapterFeedback.getCount();
			mSpinnFeedback.setSelection(mAdapterFeedback.getCount(), true);
		}

		if(pos == mAdapterFeedback.getCount())
		{
			((TextView) parent.getChildAt(0)).setTextColor(color.light_grey);
			mET_FeedbackDesc.setEnabled(false);
			mBtn_Submit.setEnabled(false);
		}
		else
		{
			mET_FeedbackDesc.setEnabled(true);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		mET_FeedbackDesc.setEnabled(false);
		mBtn_Submit.setEnabled(false);
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		Log.d("feedback updateUI()", "");
		if(mSpinnFeedback.getCount() == 0)
		{
			refreshUI();
		}
	}

	private void initUI()
	{
		mSpinnFeedback = ((Spinner)parentActivity.findViewById(R.id.spinner_feedback));

		mET_FeedbackDesc = ((EditText)parentActivity.findViewById(R.id.et_feedback_describe));
		mET_FeedbackDesc.setEnabled(false);

		mET_FeedbackDesc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isKeypadOn)
				{
					hideKeypad();
				}
				else
				{
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(mET_FeedbackDesc, InputMethodManager.SHOW_FORCED);
					isKeypadOn = true;
				}
			}
		});

		mBtn_Submit = ((Button)parentActivity.findViewById(R.id.btn_feedback_submit));
		mBtn_Submit.setEnabled(false);
		mBtn_Submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				createCaseFeedback();

			}
		});

		mET_FeedbackDesc.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((event.getAction() == KeyEvent.ACTION_UP)
						&& (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
					launchSupport();
					return true;
				}
				return false;
			}
		});

		mET_FeedbackDesc.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//				mBtn_Submit.setTextColor(R.color.dark_blue);
				mBtn_Submit.setEnabled(true);
				mBtn_Submit.setBackgroundResource(R.drawable.button_white_blue_background);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		setOnTouchListeners();
	}

	private void refreshUI()
	{
		if(AMUtility.sfFeedbackCategories != null) {
			AddFeedbackItems();

			mSpinnFeedback.setAdapter(mAdapterFeedback);
			mSpinnFeedback.setOnItemSelectedListener(this);
			mSpinnFeedback.setSelection(mAdapterFeedback.getCount(), true);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.spinner_feedback:
			hideKeypad();
			break;

		case R.id.rl_feedbackfragment:
			hideKeypad();
			break;

		case R.id.tv_feedback_title:
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
		.hideSoftInputFromWindow(mET_FeedbackDesc.getWindowToken(), 0);
		isKeypadOn = false;
	}

	private void createCaseFeedback()
	{
		String feedbackCategory = mSpinnFeedback.getSelectedItem().toString();
		String desc = mET_FeedbackDesc.getText().toString();
		Log.d("Feedback selected: ", feedbackCategory);
		Log.d("Feedback Desc: ", desc);
		String consumerAmsId = AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", "");
		Log.d("consumerAmsId: " , consumerAmsId);
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		apiCaseCreateFeedback(consumerAmsId, feedbackCategory, desc, this);
	}

	@Override
	public void OnCaseCreated(String caseSFDCID) {
		// TODO Auto-generated method stub
		if(! visibility) return;
		mET_FeedbackDesc.getText().clear();
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getFeedbackThankyou(), parentActivity.getFeedbackThankyou()
				.getTag());
		transaction.commit();
	}

	@Override
	public void onPause() {
		visibility = false;
		super.onPause();
	}
}
