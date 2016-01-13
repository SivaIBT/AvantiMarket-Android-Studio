package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.net.Uri.Builder;

import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMCaseConsumerIssuesDao extends NetEventObject
{

	private static final long serialVersionUID = -7038930521667250041L;
	private String mConsumerAmsId = "";
	public String mCategory = "";
	public String mIssue = "";
	private String mDesc = "";
	public String mCaseSFDCId = "";
	
	public AMCaseConsumerIssuesDao(String consumerAmsId, String category, String issue, String desc)
	{
		this.mConsumerAmsId = consumerAmsId;
		this.mCategory = category;
		this.mIssue = issue;
		this.mDesc = desc;
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
		getBuilder.appendQueryParameter("consumerAmsId", mConsumerAmsId);
		getBuilder.appendQueryParameter("requestCategory", mCategory);
		getBuilder.appendQueryParameter("issueType", mIssue);
		getBuilder.appendQueryParameter("issueDescription", mDesc);
		
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
		return AMConstants.URL_SF_CASE_CREATE_CONSUMER_ISSUE;
	}

	@Override
	public void setJSONValues(Object values) {
		if(values!=null)
			this.mCaseSFDCId = values.toString();
	}

	@Override
	public void setXMLValues(String arg0) {
		
	}

}
