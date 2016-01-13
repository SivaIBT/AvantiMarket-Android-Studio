/**
 * @brief       Data Object class for GET_BALANCE response
 * @file        AMGetBalanceDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        14-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

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
 * @brief       Data Object class for GET_BALANCE response
 * 
 */
public class AMGetBalanceDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3603639886082656310L;
	private Object responseJSON = null;

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
	}

	public JSONObject getJsonResponse() {
		return (JSONObject) responseJSON;
	}

	public void setJSONValues(Object values) {
		responseJSON = values;

		if (values != null) {
			JSONObject obj = (JSONObject) values;
			if (obj != null) {
				try {
					AMConstants.BALANCE_AMOUNT = Double.parseDouble(obj
							.getString("userBalance"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
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
		return null;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_PATH_BALANCE;
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
