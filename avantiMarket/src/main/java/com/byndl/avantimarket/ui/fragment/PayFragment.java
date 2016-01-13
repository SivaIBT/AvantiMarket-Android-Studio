/**
 * @brief       Displays Market Card Scan code (BarCode) for making payment
 * @file        PayFragment.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        05-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.fragment;

import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.UPCAWriter;

/**
 * 
 * @brief Displays Market Card Scan code (BarCode) for making payment
 *
 */
public class PayFragment extends BaseFragment {

	private ImageView mImageView = null, barcodeImage;
	private Bitmap mBitmap = null;
	private LinearLayout barCodeContainer;
	private String cardColor = "green";
	private String scanCode = "";
//	private TextView category, name;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pay_fragment_layout, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		parentActivity = (MyAvantiActivity)getActivity();
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.pay));
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
		barCodeContainer = (LinearLayout)parentActivity.findViewById(R.id.barcode_container);
		barcodeImage = (ImageView)parentActivity.findViewById(R.id.m_market_card);
		cardColor = getArguments().getString("color");
		// The argument should be replaced with the ScanCodes received from Server
		generateBarCode();
		if(scanCode != null && 
				scanCode.length() >= 11) {
			setBarCodeTitle(scanCode);
		}
		else {
			barCodeContainer.setBackgroundColor(android.R.color.transparent);
		}
		setUpListeners();
		updateBackground();
	}

	/**
	 * Generates BarCode from Market Card Scan Code
	 */
	private void generateBarCode(){
		Set<String> scanCodeStrings = AMPreferenceManager.getPrefInstance().getPrefs().getStringSet("scanCodes", null);
		if(scanCodeStrings != null && (scanCodeStrings.size() > 0)) {
			scanCode = (String)scanCodeStrings.toArray()[getArguments().getInt("cardSelection")];
			Log.d(TAG, "ScanCode fetched from Preferences: "+scanCode);
			if(scanCode.length()<11) return;
			
			// generating Bar Code
			com.google.zxing.Writer c9 = new UPCAWriter();
			try {
				BitMatrix bm = c9.encode(scanCode,BarcodeFormat.UPC_A,300, 300);
				mBitmap = Bitmap.createBitmap(300, 300, Config.ARGB_8888);

				for (int i = 0; i < 300; i++) {
					for (int j = 0; j < 300; j++) {

						mBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
					}
				}
			} catch (WriterException e) {
				e.printStackTrace();
			}
			if (mBitmap != null) {
				mImageView = new ImageView(parentActivity);
				mImageView.setImageBitmap(mBitmap);
			}
		}
		else {
			apiGetScanCodes(true);
		}
	}

	/**
	 * Sets BarCode title as per the Scan code
	 * @param scancode
	 */
	private void setBarCodeTitle(String scancode) {
		TextView barcodeText = (TextView)parentActivity.findViewById(R.id.m_barcode);
		TextView barcodeSequence = (TextView)parentActivity.findViewById(R.id.m_barcodeSequence);
		barcodeText.setText("ISBN ");
		StringBuilder barcodeBuilder = new StringBuilder(scancode);
		barcodeBuilder.insert(3, '-');
		barcodeBuilder.insert(5, '-');
		barcodeBuilder.insert(8, '-');
		//		barcodeBuilder.insert(15, '-');
		barcodeText.append(barcodeBuilder);
		barcodeImage.setImageBitmap(mBitmap);
		barcodeSequence.setText(scancode);
	}

	/**
	 * Sets up Listeners to accept/respond to User Events
	 */
	private void setUpListeners() {
//		category = (TextView)parentActivity.findViewById(R.id.m_primaryLabel);
//		name = (TextView)parentActivity.findViewById(R.id.m_categoryLabel);
		
/*		doneButton = (Button)parentActivity.findViewById(R.id.m_doneButton);
		doneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCards();
			}
		});
*/
		barCodeContainer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCards();
			}
		});	}

	/**
	 * Updates the background as per the Name/Color customization
	 */
	@SuppressLint("NewApi") 
	private void updateBackground() {
//		if(getArguments().getBoolean("isPrimary")) category.setVisibility(View.VISIBLE);
//		else category.setVisibility(View.INVISIBLE);

//		name.setText(getArguments().getString("title"));

		if(cardColor.contains("sky")) {
			((LinearLayout)barCodeContainer.getParent()).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));
		}

		else if(cardColor.contains("dark")) {
			((LinearLayout)barCodeContainer.getParent()).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));
		}

		else
			((LinearLayout)barCodeContainer.getParent()).setBackground(parentActivity.getResources().getDrawable(R.drawable.mc_green_background));

		((TextView) ((LinearLayout) barCodeContainer.getParent()).getChildAt(0))
				.setText("Balance: $" + AMUtility
						.getRoundedBalance(AMConstants.BALANCE_AMOUNT));

		Display display = parentActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getRealSize(size);
		barCodeContainer.setLayoutParams(new LinearLayout.LayoutParams(size.x/2, LayoutParams.WRAP_CONTENT));
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		parentActivity.stoptimertask();
	}
	
	@Override
	public void onDestroy() {
//		parentActivity.startTimer(false);
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		parentActivity.startTimer(true);
		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchMarketCards();
			}			
		});    	
		
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchMarketCards();
					return true;
				}
				return false;
			}
		});		
	}

	/**
	 * Launch MarketCards Display Screen
	 */
	private void launchMarketCards() {
		//		getBalance();
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getMarketCardsFragment(), parentActivity.getMarketCardsFragment().getTag());
		transaction.commit();
	}

	@Override
	public void updateUI() {
		// TODO Auto-generated method stub
		((TextView) ((LinearLayout) barCodeContainer.getParent()).getChildAt(0))
				.setText("Balance: $"
						+ AMUtility
								.getRoundedBalance(AMConstants.BALANCE_AMOUNT));
	}
}
