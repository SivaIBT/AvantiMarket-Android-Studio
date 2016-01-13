/**
 * @brief       Data Object class for GET_SCAN_CODES response
 * @file        AMScanCodesDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * @brief       Data Object class for GET_SCAN_CODES response
 */
public class AMScanCodesDao extends NetEventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3603639886082656310L;
	private Object responseJSON = null;
	public Set<String> mALScanCodes = null;

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	public JSONObject getJsonResponse() {
		// TODO Auto-generated method stub
		return (JSONObject) responseJSON;
	}

	public void setJSONValues(Object values) {
		// TODO Auto-generated method stub
		responseJSON = values;

		JSONArray jsonScanCodesArray = (JSONArray) responseJSON;
		mALScanCodes = new HashSet<String>(jsonScanCodesArray.length());
		try {
			for (int i = 0; i < jsonScanCodesArray.length(); i++) {
				mALScanCodes.add("" + jsonScanCodesArray.getJSONObject(i).get("scanCode"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONArray getJsonArray() {
		return (JSONArray) responseJSON;
	}

	@Override
	public void setXMLValues(String values) {
		// TODO Auto-generated method stub

	}

	@Override
	public Builder getRequestParams() {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		Builder getBuilder = new Builder();
		getBuilder.appendQueryParameter("userName", "indiaone@avantilab.org");
		getBuilder.appendQueryParameter("userPassword", "12345678");
		getBuilder.appendQueryParameter("format", "json");
		return getBuilder;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_PATH_SCAN_CODES;
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
