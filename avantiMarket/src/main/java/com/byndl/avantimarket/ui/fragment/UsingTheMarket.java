/**
 * @brief       This class is used to display items to help user for market card uses.
 * @file        UsingTheMarket.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMArticleListDao.ArticleDetails;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;

/**
 * @brief	This class is used to display items to help user for market card uses.
 * 
 */
public class UsingTheMarket extends BaseFragment implements OnTouchListener {

	private ArrayList<CharSequence> mMarketItemsList;
	private ArrayAdapter<CharSequence> mMarketItemAdapter = null;
	private ListView mListView;
	private EditText mSearchBox;
	private Button mBtnFind;
	private boolean isKeypadOn = false;
	private WebView wvArticle;
	private boolean isArticleDetailsFetched = false;
	private TextView mArticleTitle;
	private LinearLayout sv_utm_articleDetails;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.using_the_market_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.using_the_market).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		
		mMarketItemsList = new ArrayList<CharSequence>();
		mListView = (ListView)parentActivity.findViewById(R.id.ListView_UsingTheMarket);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				MixPanelEvents.pageView("Article Details - Using The Market");
				fetchSelectedItem(position);
//				switch(position)
//				{
//				case 0:
//					clearSearchBox();
//					FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
//					transaction.replace(R.id.fragment_block, parentActivity.getSetUpMarketCardAccountFragment(),
//							parentActivity.getSetUpMarketCardAccountFragment().getTag());
//					transaction.commit();
//
//					break;
//				}
			}
		});
		
		mSearchBox = (EditText)parentActivity.findViewById(R.id.et_usingTheMarketSearchBox);
		mSearchBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isKeypadOn)
				{
					hideKeypad();
				}
				else
				{
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(mSearchBox, InputMethodManager.SHOW_FORCED);
					isKeypadOn = true;
				}
			}
		});
		
		mBtnFind = (Button)parentActivity.findViewById(R.id.BtnFind_UsingTheMarket);
		mBtnFind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Find Button Clicked");
				hideKeypad();
				if(mSearchBox.getText().toString().trim().isEmpty()) {
					parentActivity.showSingleBtnDialog("", "Search Field Empty.", "OK");
				}
				else {
					Bundle args = new Bundle();
					args.putString("searchString", mSearchBox.getText().toString().trim());
					args.putString("previousFragment", "HowToUsingTheMarket");
					parentActivity.getCommonIssuesFragment().setArguments(args);
					
//					clearSearchBox();
					if(mMarketItemAdapter != null) {
						mMarketItemAdapter.clear();
					}
					mMarketItemAdapter = null;

					FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_block, parentActivity.getCommonIssuesFragment(), parentActivity.getCommonIssuesFragment().getTag());
					transaction.commit();
				}
			}
		});
		mBtnFind.setEnabled(false);
		
		mArticleTitle = (TextView)parentActivity.findViewById(R.id.tv_utm_articleTitle);
		sv_utm_articleDetails = (LinearLayout)parentActivity.findViewById(R.id.sv_utm_articleDetails);
		wvArticle = new WebView(parentActivity);
//		wvArticle = (WebView)parentActivity.findViewById(R.id.wv_utm_article);
//		mArticleDetail = (TextView)parentActivity.findViewById(R.id.tv_utm_article);
		
		if(AMUtility.mALUsingMarketArticles == null) {
			InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
					AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
			AMUtility.sfArticleArray = null;
			apiGetArticleLink(parentActivity.getResources().getText(R.string.list_type_using_the_market).toString(), null);
		}
		else {
			if(mMarketItemAdapter != null) {
				mMarketItemAdapter.clear();
			}
			mMarketItemAdapter = null;
			AMUtility.sfArticleArray = AMUtility.mALUsingMarketArticles;
			updateUI();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideKeypad();
				launchPrevious();
			}			
		});

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchPrevious();
					return true;
				}
				return false;
			}
		});
		
		mSearchBox.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mBtnFind.setEnabled(true);
				mBtnFind.setBackgroundResource(R.drawable.button_orange_grey_background);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mSearchBox.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((event.getAction() == KeyEvent.ACTION_UP)
						&& (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
					launchHelpfulHowTo();
					return true;
				}
				return false;
			}
		});
		setOnTouchListeners();

//		setOnTouchListener();
		MixPanelEvents.pageView(getResources().getString(R.string.using_the_market));
	}
	
	/**
	 * Launch Helpful How To screen from current screen.
	 * 
	 */
	private void launchHelpfulHowTo()
	{
		clearSearchBox();
		if(mMarketItemAdapter != null) {
			mMarketItemAdapter.clear();
		}
		mMarketItemAdapter = null;
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getHelpfulHowToFragment(), parentActivity.getHelpfulHowToFragment()
				.getTag());
		transaction.commit();
	}
	
	@Override
	public void updateUI() {

		if(mMarketItemAdapter==null &&
				AMUtility.sfArticleArray != null)
		{
			if(AMUtility.mALUsingMarketArticles == null) {
				AMUtility.mALUsingMarketArticles = new ArrayList<ArticleDetails>(AMUtility.sfArticleArray);	
			}
			
			for(int i=0; i<AMUtility.sfArticleArray.size(); i++)
			{
				String item = AMUtility.sfArticleArray.get(i).mTitle;
				SpannableString content = new SpannableString(item);
				content.setSpan(new UnderlineSpan(), 0, item.length(), 0);
				mMarketItemsList.add(content);
			}

			mMarketItemAdapter = new ArrayAdapter<CharSequence>(parentActivity, R.layout.support_list_item, mMarketItemsList);
			mListView.setAdapter(mMarketItemAdapter);
		}
		else if(isArticleDetailsFetched)
		{
			if(AMUtility.sfAtricleDetails == null) return;
			Log.d("Show Details: ", "USM: "+AMUtility.sfAtricleDetails.mArticleTitle);
			isArticleDetailsFetched = false;
			((RelativeLayout)parentActivity.findViewById(R.id.rl_utm_articleListDisplay)).setVisibility(View.GONE);
			((RelativeLayout)parentActivity.findViewById(R.id.rl_utm_articleDisplay)).setVisibility(View.VISIBLE);

			mArticleTitle.setText(AMUtility.sfAtricleDetails.mArticleTitle);
			
			sv_utm_articleDetails.removeViewAt(0);
			wvArticle = new WebView(parentActivity);
			wvArticle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			wvArticle.getSettings().setJavaScriptEnabled(true);
			wvArticle.getSettings().setBuiltInZoomControls(true);
			wvArticle.setWebChromeClient(new WebChromeClient());
			sv_utm_articleDetails.addView(wvArticle, 0);
			wvArticle.loadDataWithBaseURL(null, AMUtility.sfAtricleDetails.mHtmlArticleDetails, "text/html", "utf-8", null);
			wvArticle.setBackgroundColor(Color.TRANSPARENT);
			wvArticle.resumeTimers();
		}
	}
	
	private void fetchSelectedItem(int pos)
	{
		Log.d("fetchSelectedItem() ", ":"+pos);
		String knowledgeArticleId = AMUtility.sfArticleArray.get(pos).mKnowledgeArticleId;
		isArticleDetailsFetched = true;
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		AMUtility.sfAtricleDetails = null;
		apiGetArticle(knowledgeArticleId);
	}
	
	/**
	 * Register touch listeners for different UI controls on current screen.
	 * 
	 */
	private void setOnTouchListeners()
	{
		mListView.setOnTouchListener(this);
		((RelativeLayout)parentActivity.findViewById(R.id.rl_usingTheMarket)).setOnTouchListener(this);
		((TextView)parentActivity.findViewById(R.id.tv_usingTheMarket_title)).setOnTouchListener(this);
	}
	
	/**
	 * Hide soft keypad from the screen.
	 * 
	 */
	private void hideKeypad()
	{
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
		isKeypadOn = false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.d("onTouch", " touched");
		switch (v.getId()) {
		case R.id.rl_usingTheMarket:
			hideKeypad();
			break;
			
		case R.id.tv_usingTheMarket_title:
			hideKeypad();
			break;
			
		case R.id.ListView_UsingTheMarket:
			hideKeypad();
			break;
		}
		return false;
	}
	
	/**
	 * Clear the content of search box if any.
	 * 
	 */
	private void clearSearchBox()
	{
		if(mSearchBox!=null)
			mSearchBox.getText().clear();
	}

	@Override
	public void onPause() {
		super.onPause();
//		mArticleDetail.setOnTouchListener(null); 
	}
	
	private void launchPrevious() {
		if(((RelativeLayout)parentActivity.findViewById(R.id.rl_utm_articleDisplay)).getVisibility() == View.VISIBLE)
		{
			((RelativeLayout)parentActivity.findViewById(R.id.rl_utm_articleDisplay)).setVisibility(View.GONE);
			((RelativeLayout)parentActivity.findViewById(R.id.rl_utm_articleListDisplay)).setVisibility(View.VISIBLE);
		}
		else
		{
			AMUtility.mALUsingMarketArticles = null; 
			AMUtility.mALUsingAppArticles = null; 
			AMUtility.mALCommonIssuesArticles = null; 
			launchHelpfulHowTo();
		}
	}

}
