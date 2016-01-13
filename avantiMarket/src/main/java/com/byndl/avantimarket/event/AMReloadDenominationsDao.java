/**
 * @brief       Data Object class for RELOAD_DENOMINATIONS response
 * @file        AMReloadDenominationsDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        20-Apr-2015
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
 * @brief       Data Object class for RELOAD_DENOMINATIONS response
 */
public class AMReloadDenominationsDao extends NetEventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1491875543716831860L;
	
	public Set<String> mALDenominations = null;
	public HashMap<String, Integer> bonusHashMap = new HashMap<String, Integer>();
	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject reloadDenominationsObj = (JSONObject) values;
			JSONArray denominationArray = null;

			try {
				denominationArray = reloadDenominationsObj.optJSONArray("reloadDenomination");
				mALDenominations = new HashSet<String>(denominationArray.length());
				for (int j = 0; j < denominationArray.length(); j++) {
					mALDenominations.add(""+denominationArray.get(j));
				}

				JSONObject bonusObject = null;
				denominationArray = reloadDenominationsObj.optJSONArray("reloadDenominationsWithBonuses");
				for (int j = 0; j < denominationArray.length(); j++) {
					bonusObject = (JSONObject) denominationArray.get(j);
					bonusHashMap.put(bonusObject.optString("denomination"), bonusObject.optInt("bonusAmount"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		return AMConstants.URL_PATH_RELOAD_DENOMINATION;
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
