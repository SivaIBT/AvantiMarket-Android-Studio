package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMOperatorInfoDao extends NetEventObject{
	
	private static final long serialVersionUID = -3625995618875728145L;
	private String mOperatorId = "";
	public boolean showContactUs = false;
	
	public AMOperatorInfoDao(String operatorId)
	{
		this.mOperatorId = operatorId;
	}

	@Override
	public void getString(StringBuffer buf) {
		
	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject operatorInfoObj = (JSONObject) values;
			if (operatorInfoObj != null) {
				this.showContactUs = operatorInfoObj.optBoolean("Show_Contact_Us__c");
//				this.showContactUs = true;
				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putBoolean("showContactUsFlag", this.showContactUs);
				editor.commit();
			}
		}
	}

	@Override
	public void setXMLValues(String values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Builder getRequestParams() {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);

		Builder getBuilder = new Builder();
		getBuilder.appendQueryParameter("amsId", mOperatorId);

		return getBuilder;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		return null;
	}

	@Override
	public List<NameValuePair> getRequestPutParams() {
		return null;
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_SF_OPERATOR_INFO;
	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}

}
