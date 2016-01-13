package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMPurchasedProduct extends NetEventObject {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3313951352342471962L;

	public String mName = "";
	public String mPrice = "";

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject productObj = (JSONObject) values;
			if (productObj != null) {
				this.mName = productObj.optString("productName");
				this.mPrice = productObj.optString("productPrice");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NameValuePair> getRequestPutParams() {
		// TODO Auto-generated method stub
		return null;
	}
}
