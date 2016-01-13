/**
 * @brief       This class contains info about how a user can setup market card account.
 * @file        SetUpMarketCardAccountFragment.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief	This class contains info about how a user can setup market card account.
 * 
 */
public class SetUpMarketCardAccountFragment extends BaseFragment{

	private ArrayList<String> mItemsList;
	private ArrayAdapter<String> mItemAdapter;
	private ListView mListView;
	WebView mWeb;
//	private webViewTask mWebViewTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setup_my_market_account, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.d("onActivityCreated", "onActivityCreated");
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).
			setText(parentActivity.getResources().getString(R.string.helpful_hints).toUpperCase());

		mItemsList = new ArrayList<String>();
		getListItems();
		
		mItemAdapter = new ArrayAdapter<String>(parentActivity, R.layout.support_list_item, mItemsList);
		mListView = (ListView)parentActivity.findViewById(R.id.LV_setupMarketCard);
		mListView.setAdapter(mItemAdapter);
		mListView.setClickable(false);
	}

	/**
	 * @brief	Class to display web view on screen
	 * 
	 */
    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
 
            view.loadUrl(url);
            return true;
 
        }
    }
	
	/**
	 * Get the steps to set up market card account
	 * 
	 */
	private void getListItems()
	{
		mItemsList.add("1. First get a market card.");
		mItemsList.add("2. Scan your market card at a kiosk.");
		mItemsList.add("3. Signup for web access.");
		mItemsList.add("4. Set your email and password");
		mItemsList.add("5. Sign in using your email and password");
	}
	
	@Override
	public void onResume() {
		
		Log.d("onResume", "1 ");
		int width = Integer.parseInt(parentActivity.getResources().getString(R.string.video_width));
		int height = Integer.parseInt(parentActivity.getResources().getString(R.string.video_height));
		
		String playVideo= "<html><body bgcolor=\"#339933\"><div align=\"center\"><iframe class=\"youtube-player\" type=\"text/html\" width=\""+width+"\" height=\""+height+"\" src=\"https://www.youtube.com/embed/zLOUdVUW1rQ\" frameborder=\"0\"> </div></html>";
		
		if (mWeb != null) 
		{
			mWeb.onResume();
        }
		else
		{
			mWeb = new WebView(parentActivity);
			mWeb = (WebView) parentActivity.findViewById(R.id.vv_setupAvantiAcc);
			
			mWeb.getSettings().setJavaScriptEnabled(true);
			mWeb.setVerticalScrollBarEnabled(false);
			mWeb.setHorizontalScrollBarEnabled(false);
			
			mWeb.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
						launchHelpfulHowTo();
						return true;
					}
					return false;
				}
			});

			mWeb.setWebChromeClient(new WebChromeClient());
			mWeb.loadData(playVideo, "text/html", "utf-8");
		}

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					launchHelpfulHowTo();
					return true;
				}
				return false;
			}
		});
		
		super.onResume();
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
	public void onPause() {
		if(mWeb!=null)
		{
			mWeb.onPause();
//			mWeb.stopLoading();
//			mWeb.destroy();
		}
        super.onPause();
	}
	
	@Override
	public void onDestroy() {
		Log.d("onDestroy", "onDestroy1 ");
		if(mWeb!=null)
		{
			mWeb.stopLoading();
			mWeb.destroy();
			mWeb = null;
		}
		super.onDestroy();
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		
	}
}
