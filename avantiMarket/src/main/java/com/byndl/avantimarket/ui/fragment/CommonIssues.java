/**
 * @brief       This class is used to show list of common issues faced by user.
 * @file        CommonIssues.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
 * @brief	This class is used to show list of common issues faced by user.
 * <br> Each list item is clickable and direct to appropriate link/page.
 * 
 */
public class CommonIssues extends BaseFragment {

	private ArrayList<CharSequence> mCommonIssuesItemsList;
	private ArrayAdapter<CharSequence> mCommonIssuesItemAdapter;
	private ListView mListView;
	private WebView wvArticle, videoArticle;
	private boolean isArticleDetailsFetched = false;
	private TextView mArticleTitle, mSearchResultsTitle;
	private LinearLayout commonIssue_articleDetails;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_issues_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		mSearchResultsTitle = (TextView)parentActivity.findViewById(R.id.search_results_title);
		commonIssue_articleDetails = (LinearLayout) parentActivity.findViewById(R.id.sv_commonIssue_articleDetails);
		if(getArguments() == null) {
			((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.common_issues).toUpperCase());
			mSearchResultsTitle.setVisibility(View.GONE);
			MixPanelEvents.pageView(getResources().getString(R.string.common_issues));
		}
		else {
			mSearchResultsTitle.setVisibility(View.VISIBLE);
			if(getArguments().getString("previousFragment").contains("HowToUsingTheMarket")) {
				((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
				setText(getString(R.string.using_the_market).toUpperCase());
				MixPanelEvents.pageView("Search - Using The Market");
			}
			else if(getArguments().getString("previousFragment").contains("HowToUsingTheApp")) {
				((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
				setText(getString(R.string.using_the_app).toUpperCase());
				MixPanelEvents.pageView("Search - Using The App");
			}
		}
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		
		mCommonIssuesItemsList = new ArrayList<CharSequence>();
		
		mListView = (ListView)parentActivity.findViewById(R.id.ListView_CommonIssues);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				fetchSelectedItem(position);
				if(getArguments() == null) {
					MixPanelEvents.pageView("Article Details - Common Issues");
				}
				else {
					if(getArguments().getString("previousFragment").contains("HowToUsingTheMarket")) {
						MixPanelEvents.pageView("Article Details - Search - Using The Market");
					}
					else if(getArguments().getString("previousFragment").contains("HowToUsingTheApp")) {
						MixPanelEvents.pageView("Article Details - Search - Using The App");
					}
				}
			}
		});
		
		mArticleTitle = (TextView)parentActivity.findViewById(R.id.tv_commonIssue_articleTitle);
//		mArticleDetail = (TextView)parentActivity.findViewById(R.id.tv_commonIssue_article);

//		wvArticle = (WebView)parentActivity.findViewById(R.id.wv_commonIssue_article);
		commonIssue_articleDetails.removeViewAt(0);
		wvArticle = new WebView(parentActivity);
		wvArticle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		wvArticle.setBackgroundColor(android.R.color.transparent);
		wvArticle.getSettings().setJavaScriptEnabled(true);
		wvArticle.getSettings().setBuiltInZoomControls(true);
		wvArticle.setWebChromeClient(new WebChromeClient());
		commonIssue_articleDetails.addView(wvArticle, 0);
		
		videoArticle = (WebView)parentActivity.findViewById(R.id.wv_video_article);
		videoArticle.getSettings().setJavaScriptEnabled(true);
		videoArticle.getSettings().setBuiltInZoomControls(true);
		videoArticle.setWebChromeClient(new WebChromeClient());

		setBackListenerforWebView();
		
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if(getArguments() == null) {
			if(AMUtility.mALCommonIssuesArticles == null) {
				AMUtility.sfArticleArray = null;
				apiGetArticleLink(parentActivity.getResources().getText(R.string.list_type_common_issues).toString(), null);
			}
			else {
				if(mCommonIssuesItemAdapter != null) {
					mCommonIssuesItemAdapter.clear();
				}
				mCommonIssuesItemAdapter = null;
				AMUtility.sfArticleArray = AMUtility.mALCommonIssuesArticles;
				updateUI();
			}
		}
		else {
			AMUtility.sfArticleArray = null;
			apiGetArticleLink(getArguments().getString("previousFragment"), getArguments().getString("searchString"));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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

		if(videoArticle != null &&
				AMUtility.sfAtricleDetails != null &&
				AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
				AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
			
			videoArticle.onResume();
		}
	}
	
	private void fetchSelectedItem(int pos)
	{
		Log.d("fetchSelectedItem() ", ":"+pos);
		String knowledgeArticleId = AMUtility.sfArticleArray.get(pos).mKnowledgeArticleId;
		if( ! knowledgeArticleId.equalsIgnoreCase("null")) {
			isArticleDetailsFetched = true;
			InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
					AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
			AMUtility.sfAtricleDetails = null;
			apiGetArticle(knowledgeArticleId);
		}
	}
	
	/**
	 * Launch Helpful How To screen from current screen.
	 * 
	 */
	private void launchHelpfulHowTo()
	{
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getHelpfulHowToFragment(), parentActivity.getHelpfulHowToFragment()
				.getTag());
		transaction.commit();
	}
	
	@Override
	public void updateUI() {
		
		if(mCommonIssuesItemAdapter==null &&
				AMUtility.sfArticleArray != null)
		{
			if(getArguments() == null && 
					AMUtility.mALCommonIssuesArticles == null) {
				AMUtility.mALCommonIssuesArticles = new ArrayList<ArticleDetails>(AMUtility.sfArticleArray);
			}
			
			if(getArguments() != null &&
					AMUtility.sfArticleArray.size() == 0){
				mCommonIssuesItemsList.add("No result found.");
			}
			for(int i=0; i<AMUtility.sfArticleArray.size(); i++)
			{
				String item = AMUtility.sfArticleArray.get(i).mTitle;
				if(item.equalsIgnoreCase("null")) {
					mCommonIssuesItemsList.add(AMUtility.sfArticleArray.get(i).mResponseDetails);
				}
				else {
					SpannableString content = new SpannableString(item);
					content.setSpan(new UnderlineSpan(), 0, item.length(), 0);
					mCommonIssuesItemsList.add(content);
				}
			}

			mCommonIssuesItemAdapter = new ArrayAdapter<CharSequence>(parentActivity, R.layout.support_list_item, mCommonIssuesItemsList);
			mListView.setAdapter(mCommonIssuesItemAdapter);
		}
		else if(isArticleDetailsFetched)
		{
			if(AMUtility.sfAtricleDetails == null) return;
			Log.d("Show Details: ", "Common Issues: "+AMUtility.sfAtricleDetails.mArticleTitle);
			isArticleDetailsFetched = false;
			mListView.setVisibility(View.GONE);
			((RelativeLayout)parentActivity.findViewById(R.id.rl_commonIssue_articleDisplay)).setVisibility(View.VISIBLE);

			mArticleTitle.setText(AMUtility.sfAtricleDetails.mArticleTitle);
			
			if(AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
				AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) 
			{
				wvArticle.setVisibility(View.GONE);
				videoArticle.setVisibility(View.VISIBLE);
				videoArticle.getSettings().setLoadWithOverviewMode(true);
				videoArticle.getSettings().setUseWideViewPort(true);				
				videoArticle.loadDataWithBaseURL(null, AMUtility.sfAtricleDetails.mHtmlArticleDetails, "text/html", "utf-8", null);
				videoArticle.setBackgroundColor(Color.TRANSPARENT);
				videoArticle.resumeTimers();
			}
			else {
				videoArticle.setVisibility(View.GONE);
				wvArticle.setVisibility(View.VISIBLE);
				wvArticle.loadDataWithBaseURL(null, AMUtility.sfAtricleDetails.mHtmlArticleDetails, "text/html", "utf-8", null);
				wvArticle.setBackgroundColor(Color.TRANSPARENT);
				wvArticle.resumeTimers();
			}
			mSearchResultsTitle.setVisibility(View.GONE);
		}
	}
	
	private void launchPrevious() {
		if(((RelativeLayout)parentActivity.findViewById(R.id.rl_commonIssue_articleDisplay)).getVisibility() == View.VISIBLE)
		{
			// Hide WebView
			if(videoArticle != null &&
					AMUtility.sfAtricleDetails != null &&
					AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
					AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
				
				videoArticle.loadUrl("about:blank");
			}
			else {
				commonIssue_articleDetails.removeViewAt(0);
				wvArticle = new WebView(parentActivity);
				wvArticle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				wvArticle.getSettings().setJavaScriptEnabled(true);
				wvArticle.getSettings().setBuiltInZoomControls(true);
				wvArticle.setWebChromeClient(new WebChromeClient());
				commonIssue_articleDetails.addView(wvArticle, 0);
			}

			((RelativeLayout)parentActivity.findViewById(R.id.rl_commonIssue_articleDisplay)).setVisibility(View.GONE);
			
			// Show list view
			mListView.setVisibility(View.VISIBLE);
			if(getArguments() != null) {
				mSearchResultsTitle.setVisibility(View.VISIBLE);
			}
			else {
				mSearchResultsTitle.setVisibility(View.GONE);
			}
		}
		else
		{
			if(mCommonIssuesItemAdapter != null) {
				mCommonIssuesItemAdapter.clear();
			}
			mCommonIssuesItemAdapter = null;
			if(getArguments() == null) {
				AMUtility.mALUsingMarketArticles = null; 
				AMUtility.mALUsingAppArticles = null; 
				AMUtility.mALCommonIssuesArticles = null; 
				launchHelpfulHowTo();
			}
			else {
				if(getArguments().getString("previousFragment").contains("HowToUsingTheMarket")) {
					FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_block, parentActivity.getUsingTheMarketFragment(), parentActivity.getUsingTheMarketFragment().getTag());
					transaction.commit();
				}
				else if(getArguments().getString("previousFragment").contains("HowToUsingTheApp")) {
					FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_block, parentActivity.getUsingTheAppFragment(), parentActivity.getUsingTheAppFragment().getTag());
					transaction.commit();
				}
			}
		}
	}

	@Override
	public void onPause() {
		
		if(videoArticle != null &&
				AMUtility.sfAtricleDetails != null &&
				AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
				AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
			videoArticle.pauseTimers();			
			videoArticle.onPause();
		}
		super.onPause();
//		mArticleDetail.setOnTouchListener(null);	
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		if(videoArticle != null &&
				AMUtility.sfAtricleDetails != null &&
				AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
				AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
			videoArticle.saveState(outState);			
		}
		super.onSaveInstanceState(outState);
	}
	
	private void setBackListenerforWebView() {
		wvArticle.setOnKeyListener(new View.OnKeyListener() {
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

		videoArticle.setOnKeyListener(new View.OnKeyListener() {
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

	}
}
