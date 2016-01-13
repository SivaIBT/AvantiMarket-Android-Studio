/**
 * @brief       Data Object class for TRANSACTION_DETAIL response
 * @file        AMTransactionDetailDao.java
 * @version     1.18
 * @author      jyotiranjan.pradhan
 * @date        30-Jul-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief Data Object class for TRANSACTION_DETAIL response
 * 
 */
public class AMTransactionDetailDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795602840012744225L;
	private String mTransactionId;
	
	public String mId = "";
	public String mDate = "";
	public String mAmount = "";
	public String mTransactionType = "";
	public String mLocation = "";
	public String mKiosk = "";
	public String mMarketcard = "";
	public String mPoints = "";
	public String mTax = "";
	public String mBalance = "";
	public String mCardName = "";
	public String mCardType = "";

	public ArrayList<AMPurchasedProduct> mALProducts = new ArrayList<AMPurchasedProduct>();

	public AMTransactionDetailDao(String transactionId) {
		mTransactionId = transactionId;
	}
	
	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject transactionObj = (JSONObject) values;
			if (transactionObj != null) {
				this.mId = transactionObj.optString("transactionID");
				this.mDate = transactionObj.optString("transactionDate");
				this.mAmount = transactionObj
						.optString("transactionAmount");
				this.mTransactionType = transactionObj.optString("transactionType");
				this.mLocation = transactionObj.optString("location");
				this.mKiosk = transactionObj.optString("kiosk");
				this.mMarketcard = transactionObj.optString("marketCard");
				this.mPoints = transactionObj.optString("pointsEarned");
				this.mTax = transactionObj.optString("totalTax");
				this.mBalance = transactionObj.optString("balance");
				this.mCardName = transactionObj.optString("cardName");
				this.mCardType = transactionObj.optString("cardType");
				
				try {
					JSONArray prductArray = (JSONArray) transactionObj
							.getJSONArray("transactionItems");
					AMPurchasedProduct productObj;
					JSONObject productJSON;
					
					if (prductArray != null) {
						for (int j = 0; j < prductArray.length(); j++) {
							productJSON = (JSONObject) prductArray.get(j);
							productObj = new AMPurchasedProduct();
							productObj.setJSONValues(productJSON);
							this.mALProducts.add(productObj);
						}
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	@Override
	public void setXMLValues(String values) {
		// TODO Auto-generated method stub

	}

	@Override
	public Builder getRequestParams() {

		InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
				return null;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_PATH_TRANSACTION_DETAIL+"/"+mTransactionId;

	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				AMPreferenceManager.getPrefInstance().getAuthHeader());
		headers.put(AMConstants.HTTP_HEADER_ACCEPT,
				AMConstants.HTTP_HEADER_ACCEPT_VALUE);
		return headers;
	}

	@Override
	public List<NameValuePair> getRequestPutParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
