/**
 * @brief       Data Object class for CHARGE_STORED_CARD response
 * @file        AMChargeStoredCardDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        17-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for CHARGE_STORED_CARD response
 */
public class AMChargeStoredCardDao extends NetEventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7524178080934233090L;
	public boolean isChargedSuccess = false;
	public String mCardId = "";
	public String mReloadAmnt = "";
	public boolean mIsAuto = false;
	
	//Default constructor
//	public AMChargeStoredCardDao() {
//		// TODO Auto-generated constructor stub
//	}
	
	/**
	 * Parameterized constructor
	 * @param aCardId
	 * @param aAmount
	 */
	public AMChargeStoredCardDao(String aCardId, String aAmount, boolean isAuto){
		this.mCardId = aCardId;
		this.mReloadAmnt = aAmount;
		this.mIsAuto = isAuto;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		isChargedSuccess = true;
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cardID", mCardId));
		params.add(new BasicNameValuePair("reloadAmount", mReloadAmnt));

		return params;
	}

	@Override
	public String getURLPath() {
		// TODO Auto-generated method stub
		return AMConstants.URL_PATH_CHARGED_CARD;
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
