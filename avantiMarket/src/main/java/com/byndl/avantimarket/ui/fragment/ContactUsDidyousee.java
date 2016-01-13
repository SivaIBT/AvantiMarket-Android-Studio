/**
 * @brief       This class is used to show addition items in contact us feature.
 * @file        ContactUsDidyousee.java
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.interfaces.ICreateCase;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.byndl.avantimarket.utils.MixPanelEvents;
import com.incedo.network.InitNetwork;

/**
 * @brief	This class is used to show addition items in contact us feature.
 * <br> Available items are clickable and direct to appropriate link/page.
 * 
 */
public class ContactUsDidyousee extends BaseFragment implements ICreateCase{
	
	private ArrayList<CharSequence> mItemsList;
	private ArrayAdapter<CharSequence> mItemAdapter = null;
	private ListView mListView;
	private Button mBtn_SendRequest;
	private WebView wvArticle, videoArticle;
	private boolean isArticleDetailsFetched = false;
	private TextView mArticleTitle;
	private LinearLayout didyousee_articleDetails;
	private boolean visibility = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_us_didyousee, container, false);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.contact_us).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		
		mItemsList = new ArrayList<CharSequence>();
		mBtn_SendRequest = ((Button)parentActivity.findViewById(R.id.btn_send_request));
		
		mListView = (ListView)parentActivity.findViewById(R.id.ListView_didyousee);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Log.d("onItemSelected() ", ":"+pos);
				fetchSelectedItem(pos);
				MixPanelEvents.pageView("Article Details - Did You See");
			}
		});
		
		didyousee_articleDetails = (LinearLayout) parentActivity.findViewById(R.id.sv_didyousee_articleDetails);
		mArticleTitle = (TextView)parentActivity.findViewById(R.id.tv_didyousee_articleTitle);
		wvArticle = new WebView(parentActivity);
		didyousee_articleDetails.removeViewAt(0);
		didyousee_articleDetails.addView(wvArticle, 0);
		videoArticle = (WebView)parentActivity.findViewById(R.id.wv_video_article);
		
		setBackListenerforWebView();
		
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		AMUtility.sfArticleArray = null;
		apiGetArticleLink(parentActivity.getResources().getText(R.string.list_type_didyousee).toString(), null);

	}

	@Override
	public void onResume() {
		super.onResume();
		
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchPreviousScreen();
			}			
		});

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchPreviousScreen();
					return true;
				}
				return false;
			}
		});

		mBtn_SendRequest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createCase();
			}
		});
		
//		setOnTouchListener();
		
		if(videoArticle != null &&
				AMUtility.sfAtricleDetails != null &&
				AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
				AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
			
			videoArticle.onResume();
		}
		
		visibility = true;
		MixPanelEvents.pageView("Did You See");
	}
	
	/**
	 * Launch Contact us screen from current screen.
	 * 
	 */
	private void launchContactUs()
	{

		if(mItemAdapter != null) {
			mItemAdapter.clear();
		}
		mItemAdapter = null;
		
		Bundle args = new Bundle();
		args.putString("description", getArguments().getString("description"));
		args.putInt("categoryIndex", getArguments().getInt("categoryIndex"));
		args.putInt("issueIndex", getArguments().getInt("issueIndex"));
		parentActivity.getContactUsFragment().setArguments(args);

		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getContactUsFragment(), parentActivity.getContactUsFragment()
				.getTag());
		transaction.commit();
	}
	
	private void fetchSelectedItem(int pos)
	{
		Log.d("fetchSelectedItem() ", ":"+pos);
		String knowledgeArticleId = AMUtility.sfArticleArray.get(pos).mKnowledgeArticleId;
		//Post a request for Article
		isArticleDetailsFetched = true;
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		AMUtility.sfAtricleDetails = null;
		apiGetArticle(knowledgeArticleId);
	}

	@Override
	public void updateUI() {
		
		if(mItemAdapter==null &&
				AMUtility.sfArticleArray != null)
		{
			for(int i=0; i<AMUtility.sfArticleArray.size(); i++)
			{
				String item = AMUtility.sfArticleArray.get(i).mTitle;
				SpannableString content = new SpannableString(item);
				content.setSpan(new UnderlineSpan(), 0, item.length(), 0);
				mItemsList.add(content);
			}
			mItemAdapter = new ArrayAdapter<CharSequence>(parentActivity, R.layout.support_list_item, mItemsList);
			mListView.setAdapter(mItemAdapter);
			
		}
		else if(isArticleDetailsFetched)
		{
			if(AMUtility.sfAtricleDetails == null) return;
			Log.d("Show Details: ", AMUtility.sfAtricleDetails.mArticleTitle);
			isArticleDetailsFetched = false;
			((RelativeLayout)parentActivity.findViewById(R.id.rl_didyousee_articleListDisplay)).setVisibility(View.GONE);
			((RelativeLayout)parentActivity.findViewById(R.id.rl_didyousee_articleDisplay)).setVisibility(View.VISIBLE);
			mBtn_SendRequest.setVisibility(View.GONE);
			mArticleTitle.setText(AMUtility.sfAtricleDetails.mArticleTitle);
			
			if(AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
					AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) 
				{
				videoArticle = (WebView)parentActivity.findViewById(R.id.wv_video_article);
				wvArticle.setVisibility(View.GONE);
				videoArticle.setVisibility(View.VISIBLE);
				videoArticle.getSettings().setJavaScriptEnabled(true);
				videoArticle.getSettings().setBuiltInZoomControls(true);
				videoArticle.setWebChromeClient(new WebChromeClient());
				videoArticle.getSettings().setLoadWithOverviewMode(true);
				videoArticle.getSettings().setUseWideViewPort(true);				
				videoArticle.loadDataWithBaseURL(null, AMUtility.sfAtricleDetails.mHtmlArticleDetails, "text/html", "utf-8", null);
				videoArticle.setBackgroundColor(Color.TRANSPARENT);
				videoArticle.resumeTimers();
				}
			else {
				didyousee_articleDetails.removeViewAt(0);
				wvArticle = new WebView(parentActivity);
				videoArticle.setVisibility(View.GONE);
				wvArticle.setVisibility(View.VISIBLE);
				wvArticle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				wvArticle.getSettings().setJavaScriptEnabled(true);
				wvArticle.getSettings().setBuiltInZoomControls(true);
				wvArticle.setWebChromeClient(new WebChromeClient());
				wvArticle.loadDataWithBaseURL(null, AMUtility.sfAtricleDetails.mHtmlArticleDetails, "text/html", "utf-8", null);
				wvArticle.setBackgroundColor(Color.TRANSPARENT);
				wvArticle.resumeTimers();
				didyousee_articleDetails.addView(wvArticle, 0);
			}
		
		}
	}

	private void createCase()
	{
		String caseCategory = getArguments().getString("categoryType");
		String caseIssue = getArguments().getString("issueType");
		String desc = getArguments().getString("description");
		String consumerAmsId = AMPreferenceManager.getPrefInstance().getPrefs().getString("amsId", "");
		Log.d("consumerAmsId: " , consumerAmsId+" \n\r"+caseCategory+" \n\r"+caseIssue+" \n\r"+desc);

		if(		caseCategory != null && !caseCategory.isEmpty() &&
				caseIssue != null && !caseIssue.isEmpty() &&
				desc != null && !desc.isEmpty() &&
				consumerAmsId != null && !consumerAmsId.isEmpty())
		{
			InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
					AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
			apiCaseConsumerIssues(consumerAmsId,caseCategory, caseIssue, desc, this);
		}
	}

	@Override
	public void OnCaseCreated(String caseSFDCID) 
	{
		if(! visibility) return;
		Log.d(TAG, "OnCaseCreated: "+caseSFDCID);
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getContactUsThankyou(), parentActivity.getContactUsThankyou()
				.getTag());
		transaction.commit();
	}
	
	private void launchPreviousScreen() {
		if(((RelativeLayout)parentActivity.findViewById(R.id.rl_didyousee_articleDisplay)).getVisibility() == View.VISIBLE)
		{
			if(videoArticle != null &&
					AMUtility.sfAtricleDetails != null &&
					AMUtility.sfAtricleDetails.mArticleTitle.contains("Embedded Video") &&
					AMUtility.sfAtricleDetails.mHtmlArticleDetails.contains("youtube")) {
				
				videoArticle.loadUrl("about:blank");
			}

			((RelativeLayout)parentActivity.findViewById(R.id.rl_didyousee_articleDisplay)).setVisibility(View.GONE);
			((RelativeLayout)parentActivity.findViewById(R.id.rl_didyousee_articleListDisplay)).setVisibility(View.VISIBLE);
			mBtn_SendRequest.setVisibility(View.VISIBLE);
		}
		else
		{
			launchContactUs();
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
		visibility = false;
		super.onPause();
//		mArticleDetail.setOnTouchListener(null);
	}

	private void setBackListenerforWebView() {
		wvArticle.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchPreviousScreen();
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
					launchPreviousScreen();
					return true;
				}
				return false;
			}
		});
	}
}
