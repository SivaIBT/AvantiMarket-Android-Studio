package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.net.Uri.Builder;

import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMCaseCreateFeedbackDao  extends NetEventObject{

	private static final long serialVersionUID = 1351458106531790200L;
	private String mConsumerAmsId = "";
	public String mCategory = "";
	private String mDesc = "";
	public String mCaseSFDCId = "";

	public AMCaseCreateFeedbackDao(String consumerAmsId, String category, String desc)
	{
		this.mConsumerAmsId = consumerAmsId;
		this.mCategory = category;
		this.mDesc = desc;
	}
	
	@Override
	public void getString(StringBuffer buf) {
		
	}

	@Override
	public void setJSONValues(Object values) {
		if(values!=null)
			this.mCaseSFDCId = values.toString();
	}

	@Override
	public void setXMLValues(String values) {
		
	}

	@Override
	public Builder getRequestParams() {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);

		Builder getBuilder = new Builder();
		getBuilder.appendQueryParameter("consumerAmsId", mConsumerAmsId);
		getBuilder.appendQueryParameter("feedbackCategory", mCategory);
		getBuilder.appendQueryParameter("feedbackDetails", mDesc);
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
		// TODO Auto-generated method stub
		return AMConstants.URL_SF_CASE_CREATE_CONSUMER_FEEDBACK;
	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}

}
