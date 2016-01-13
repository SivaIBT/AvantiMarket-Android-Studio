/**
 * @brief       Data Object class for DELETE_STORED_CARD response
 * @file        AMDeleteStoredCardDao.java
 * @version     1.16
 * @author      jyotiranjan.pradhan
 * @date        22-Jul-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for DELETE_STORED_CARD response
 */
public class AMDeleteStoredCardDao extends NetEventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7524178080934233090L;
	public boolean isDeletionSuccess = false;
	private String mCardId = ""; 

	/**
	 * Parameterized constructor
	 * @param aCardId
	 */
	public AMDeleteStoredCardDao(String aCardId){
		this.mCardId = aCardId;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		isDeletionSuccess = true;
	}

	@Override
	public void setXMLValues(String values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Builder getRequestParams() {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		Builder deleteParams = new Builder();
		deleteParams.appendQueryParameter("cardID", mCardId);

		return deleteParams;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		return null;
	}

	@Override
	public String getURLPath() {
		// TODO Auto-generated method stub
		return AMConstants.URL_PATH_DELETE_STORED_CARD;
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
