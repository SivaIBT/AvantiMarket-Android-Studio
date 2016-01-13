/**
 * @brief       Data Object class for SET_USER_PIN response
 * @file        AMSetUserPinDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        12-June-2015
 * @copyright   incedo
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
 * @brief       Data Object class for SET_USER_PIN response
 */
public class AMSetUserPinDao extends NetEventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7524178080934233090L;
	public boolean isSuccess = false;
	public String mNewPin = "";
	
	/**
	 * Parameterized constructor
	 * @param aNewPin
	 */
	public AMSetUserPinDao(String aNewPin){
		this.mNewPin = aNewPin;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		isSuccess = true;
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
		return null;
	}

	@Override
	public String getURLPath() {
		// TODO Auto-generated method stub
		return AMConstants.URL_PATH_SET_PIN;
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userPin", mNewPin));

		return params;
	}

}
