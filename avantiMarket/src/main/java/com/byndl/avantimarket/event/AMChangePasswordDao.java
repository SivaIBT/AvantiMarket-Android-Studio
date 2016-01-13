/**
 * @brief       Data Object class for CHANGE_PASSWORD response
 * @file        AMChangePasswordDao.java
 * @version     1.11
 * @author      jyotiranjan.pradhan
 * @date        24-June-2015
 * @copyright   incedo
 * 
 */
package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.util.Log;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for CHANGE_PASSWORD response
 */
public class AMChangePasswordDao extends NetEventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7524178080934233090L;

	public boolean mResult = false;
	
	private String mOldPassword = "";
	private String mNewPassword = "";
	
	/**
	 * Parameterized constructor
	 * @param 
	 */
	public AMChangePasswordDao(String aOldPassword, String aNewPassword){
		this.mOldPassword = aOldPassword;
		this.mNewPassword = aNewPassword;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		JSONObject resultObject = (JSONObject) values;
		try {
			mResult = resultObject.getBoolean("result");
		} catch (JSONException e) {
			Log.d("AvantiMarket", "AMChangePasswordDao::setJSONValues Exception: "+e.getLocalizedMessage());
			e.printStackTrace();
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
		return null;
	}

	@Override
	public String getURLPath() {
		// TODO Auto-generated method stub
		return AMConstants.URL_PATH_CHANGE_PASSWORD;
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

		if(! mOldPassword.isEmpty())
			params.add(new BasicNameValuePair("oldPassword", mOldPassword));

		if(! mNewPassword.isEmpty())
			params.add(new BasicNameValuePair("newPassword", mNewPassword));

		return params;
	}

}
