/**
 * @brief       Data Object class for GET_AUTO-RECHARGE_SETTINGS response
 * @file        AMGetAutoRechargeSettingsDao.java
 * @version     1.15
 * @author      jyotiranjan.pradhan
 * @date        09-Jul-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief Data Object class for GET_AUTO-RECHARGE_SETTINGS response
 * 
 */
public class AMGetAutoRechargeSettingsDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795602840012744225L;

	public boolean mAutoRechargeEnabled = false;
	public String mCardID = "";
	public String mReloadAmount = "";
	public String mReloadThreshold = "";
		
	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject autoRechargeObj = (JSONObject) values;
			if (autoRechargeObj != null) {
				this.mAutoRechargeEnabled = autoRechargeObj.optBoolean("autoRechargeEnabled");
				this.mCardID = autoRechargeObj.optString("cardID");
				this.mReloadAmount = autoRechargeObj.optString("reloadAmount");
				this.mReloadThreshold = autoRechargeObj.optString("reloadThreshold");
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
		return AMConstants.URL_PATH_GET_AUTORECHARGE_SETTINGS;

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
