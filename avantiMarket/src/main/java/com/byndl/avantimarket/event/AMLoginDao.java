/**
 * @brief       Data Object class for AUTHENTICATE response
 * @file        AMLoginDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        13-Apr-2015
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
 * @brief Data Object class for AUTHENTICATE response
 * 
 */
public class AMLoginDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795602840012744225L;
	private String mAuthString = "";
	
	public String mFirstName = "";
	public String mLastName = "";
	public boolean mRequestPhoneNo = false;
	public boolean mRequestPin = false;
	public String mContactEmail = "";
	public String mPhoneNumber = "";
	public String mPin = "";
	public String mAmsId = "";
	public String mOperatorId = "";
	public String mLocationId = "";

	public AMLoginDao(String authString) {
		mAuthString = authString;
	}
	
	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject loginObj = (JSONObject) values;
			if (loginObj != null) {
				this.mFirstName = loginObj.optString("firstName");
				this.mLastName = loginObj.optString("lastName");
				this.mRequestPhoneNo = loginObj.optBoolean("requestPhoneNumber");
				this.mRequestPin = loginObj.optBoolean("requestPIN");
				this.mPhoneNumber = loginObj.optString("phoneNumber");
				this.mPin = loginObj.optString("pin","");
				this.mPin = this.mPin.replace(" ", "");
				this.mContactEmail = loginObj.optString("email");
				this.mAmsId = loginObj.optString("ams_id");
				this.mOperatorId = loginObj.optString("operatorId");
				this.mLocationId = loginObj.optString("locationId");
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
		return AMConstants.URL_PATH_AUTH;

	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		if(mAuthString.isEmpty()) {
			headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
					AMPreferenceManager.getPrefInstance().getAuthHeader());
		}
		else {
			headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
					mAuthString);
		}
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
