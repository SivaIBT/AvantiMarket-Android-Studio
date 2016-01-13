package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.net.Uri.Builder;
import android.util.Log;

import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMCreateConsumerSfdcDao extends NetEventObject
{

	private static final long serialVersionUID = -7038930521667250041L;
	private String mFirstName = "";
	private String mLastName = "";
	private String mAmsId = "";
	private String mOperatorId = "";
	private String mPhone = "";
	private String mMobilePhone = "";
	private String mEmail = "";

	public String mConsumerSfdc = "";
	
	public AMCreateConsumerSfdcDao(String firstName, String lastName, String amsId, String operatorId, String phone, String mobilePhone, String email) 
	{
		this.mFirstName = firstName;
		this.mLastName = lastName;
		this.mAmsId = amsId;
		this.mOperatorId = operatorId;
		this.mPhone = phone;
		this.mMobilePhone = mobilePhone;
		this.mEmail = email;
}
	
	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}

	@Override
	public Builder getRequestParams() {
		
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);

		Builder getBuilder = new Builder();
		getBuilder.appendQueryParameter("firstName", mFirstName);
		getBuilder.appendQueryParameter("lastName", mLastName);
		getBuilder.appendQueryParameter("amsId", mAmsId);
		getBuilder.appendQueryParameter("operatorId", mOperatorId);
		getBuilder.appendQueryParameter("phone", mPhone);
		getBuilder.appendQueryParameter("mobilePhone", mMobilePhone);
		getBuilder.appendQueryParameter("email", mEmail);
		
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
	public void getString(StringBuffer arg0) {
		
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_SF_CREATE_CONSUMER_SFDC;
	}

	@Override
	public void setJSONValues(Object values) {
		if(values!=null)
			this.mConsumerSfdc = values.toString();
		Log.d("Consumer SFDC Created: ", values.toString());
	}

	@Override
	public void setXMLValues(String arg0) {
		
	}

}
