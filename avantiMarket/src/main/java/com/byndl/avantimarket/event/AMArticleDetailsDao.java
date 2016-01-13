package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMArticleDetailsDao extends NetEventObject{
	
	private static final long serialVersionUID = -3625995618875728145L;
	private String mKnowledgeArticleId= "";
	public String mArticleTitle = "";
	public String mHtmlArticleDetails = "";
	
	public AMArticleDetailsDao(String knowledgeArticleId)
	{
		this.mKnowledgeArticleId = knowledgeArticleId;
	}

	@Override
	public void getString(StringBuffer buf) {
		
	}

	@Override
	public void setJSONValues(Object values) {
		
		JSONArray articleDetailsArray = (JSONArray) values;
		JSONObject detailsJSON;
		try {
			if (articleDetailsArray != null) {
				detailsJSON = (JSONObject) articleDetailsArray.get(0);
				this.mArticleTitle = detailsJSON.optString("Title");
				this.mHtmlArticleDetails = detailsJSON.optString("Body_Content__c");
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
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
		getBuilder.appendQueryParameter("KnowledgeArticleId", mKnowledgeArticleId);

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
		return AMConstants.URL_SF_ARTICLE_DETAILS;
	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}

}
