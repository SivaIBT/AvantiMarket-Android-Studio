package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;
import android.util.Log;

import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMSupportPicklistDao extends NetEventObject
{
	private static final long serialVersionUID = -3603639886082656310L;
	private Object responseJSON = null;
	private String mOperatorAmsId = "";
	private String mCategory = "";
	public ArrayList<String> mALSupportPicklist = null;

	/**
	 * Parameterized constructor
	 * @param aOperatorAmsId
	 */
	public AMSupportPicklistDao(String aOperatorAmsId, String aCategory){
		this.mOperatorAmsId = aOperatorAmsId;
		this.mCategory = aCategory;
	}

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

		JSONArray jsonConsumerCategoriesArray = (JSONArray) responseJSON;
		Log.d("setJSONValues()", " Size: "+jsonConsumerCategoriesArray.length());
//		mALConsumerCategories = new HashSet<String>(jsonConsumerCategoriesArray.length());
		mALSupportPicklist = new ArrayList<String>();//(jsonConsumerCategoriesArray.length());
		try {
			for (int i = 0; i < jsonConsumerCategoriesArray.length(); i++) {
				
				mALSupportPicklist.add("" + jsonConsumerCategoriesArray.getString(i));
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
		
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		if( (this.mOperatorAmsId==null) && (this.mCategory==null) )
		{
			return null;
		}
		
		Builder getBuilder = new Builder();
		getBuilder.appendQueryParameter("operatorAmsId", mOperatorAmsId);
		if(this.mCategory!=null)
		{
			getBuilder.appendQueryParameter("category", mCategory);
		}
		return getBuilder;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLPath() {
		
		if( (this.mOperatorAmsId==null) && (this.mCategory==null) )
		{
			return AMConstants.URL_SF_CASE_FEEDBACK_CATEGORY;
		}
		
		if(mCategory!=null)
		{
			return AMConstants.URL_SF_CASE_CONSUMER_ISSUE_TYPE_CATEGORY;
		}
		else
		{
			return AMConstants.URL_SF_CASE_CONSUMER_REQ_CATEGORY;
		}
	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}

	@Override
	public List<NameValuePair> getRequestPutParams() {
		// TODO Auto-generated method stub
		return null;
	}
}
