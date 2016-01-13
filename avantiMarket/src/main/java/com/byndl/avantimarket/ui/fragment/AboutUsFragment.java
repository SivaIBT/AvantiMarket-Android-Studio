/**
 * @brief       This class is used to show "About us" screen.
 * @file        AboutUsFragment.java
 * @version     1.18
 * @author      siva.rajendhra
 * @date        03-Aug-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.MixPanelEvents;

/**
 * @brief	This class is used to show "AboutUs" screen.
 * 
 */
public class AboutUsFragment extends BaseFragment {

	private ScrollView versionContainer;
	private LinearLayout webViewContainer;
	private Button avantiMarkets;
	private WebView avantiWebView;
	private TextView version_number;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.about_us_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.about).toUpperCase());

		intialiseMembers();
	}

	@Override
	public void onResume() {
		super.onResume();
		parentActivity.setMenuDrawer();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "AboutUs onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					if(webViewContainer.getVisibility() == View.VISIBLE) {
						webViewContainer.setVisibility(View.GONE);
						versionContainer.setVisibility(View.VISIBLE);
						avantiMarkets.setVisibility(View.VISIBLE);
						version_number.setVisibility(View.VISIBLE);
					}
					else {
						FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
						transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
						transaction.commit();
					}
					return true;
				}
				return false;
			}
		});

		MixPanelEvents.pageView(getResources().getString(R.string.about_us));
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
	}

	private void intialiseMembers() {
		webViewContainer = (LinearLayout)parentActivity.findViewById(R.id.avanti_webview_container);
		versionContainer = (ScrollView)parentActivity.findViewById(R.id.m_versionContainer);

		avantiWebView = (WebView)parentActivity.findViewById(R.id.avanti_webview);
		avantiWebView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					if(webViewContainer.getVisibility() == View.VISIBLE) {
						webViewContainer.setVisibility(View.GONE);
						versionContainer.setVisibility(View.VISIBLE);
						avantiMarkets.setVisibility(View.VISIBLE);
						version_number.setVisibility(View.VISIBLE);
						return true;
					}
				}
				return false;
			}
		});

		avantiMarkets = (Button)parentActivity.findViewById(R.id.m_avantiMarkets);
		avantiMarkets.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				versionContainer.setVisibility(View.GONE);
				avantiMarkets.setVisibility(View.GONE);
				version_number.setVisibility(View.GONE);
				webViewContainer.setVisibility(View.VISIBLE);
				avantiWebView.getSettings().setJavaScriptEnabled(true);
				avantiWebView.getSettings().setBuiltInZoomControls(true);
				avantiWebView.setWebViewClient(new WebViewClient(){
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						return false;
					}
				});
				avantiWebView.loadUrl("http://www.avantimarkets.com/");
//				avantiWebView.loadUrl("file:///android_asset/anim.gif");
			}
		});

		try {
			version_number = (TextView)parentActivity.findViewById(R.id.version_number);

			PackageInfo pInfo;
			pInfo = parentActivity.getPackageManager().getPackageInfo(parentActivity.getPackageName(), 0);
			String curVer = pInfo.versionName;
//			curVer = curVer.substring(0, curVer.indexOf(" "));
			version_number.setText("v."+curVer.trim());
			version_number.setGravity(Gravity.CENTER_HORIZONTAL);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
