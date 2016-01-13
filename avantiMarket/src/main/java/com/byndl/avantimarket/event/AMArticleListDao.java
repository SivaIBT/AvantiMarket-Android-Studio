package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.byndl.avantimarket.utils.AMUtility;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

public class AMArticleListDao extends NetEventObject 
{
	private static final long serialVersionUID = -578455458828748416L;
	public String mListType;
	public String mSearchKeyword;

	ArrayList<ArticleDetails> mALArticleList = new ArrayList<ArticleDetails>();

	public AMArticleListDao(String listType, String searchKeyword)
	{
		this.mListType = listType;
		this.mSearchKeyword = searchKeyword;
	}
	
	public ArrayList<ArticleDetails> getArticleArray() 
	{
		return mALArticleList;
	}

	@Override
	public void getString(StringBuffer buf) {
		
	}

	@Override
	public void setJSONValues(Object values) {
		JSONArray articleArray = (JSONArray) values;
		ArticleDetails articleObj;
		JSONObject articleJSON;
		try {
			if (articleArray != null) {
				for (int j = 0; j < articleArray.length(); j++) {
					articleJSON = (JSONObject) articleArray.get(j);
					articleObj = new ArticleDetails();
					articleObj.setJSONValues(articleJSON);
					this.mALArticleList.add(articleObj);
				}
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void setXMLValues(String values) {
		
	}

	@Override
	public Builder getRequestParams() {
		InitNetwork.setNetworkInit(AMConstants.BASE_URL_SF, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		Builder getBuilder = new Builder();
		
		getBuilder.appendQueryParameter("listType", this.mListType);

		if(this.mSearchKeyword!=null)
		{
			getBuilder.appendQueryParameter("keyword", this.mSearchKeyword);
		}
		else
		{
			getBuilder.appendQueryParameter("keyword", "");
		}
		if(this.mListType.contains("DidYouSeeThis")) {
			getBuilder.appendQueryParameter("requestCategory", AMPreferenceManager.getPrefInstance().getPrefs().getString("categoryType", ""));
			getBuilder.appendQueryParameter("IssueType", AMPreferenceManager.getPrefInstance().getPrefs().getString("issueType", ""));
		}
		else {
			getBuilder.appendQueryParameter("requestCategory", "");
			getBuilder.appendQueryParameter("IssueType", "");
		}
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
		return AMConstants.URL_SF_ARTICLE_LIST;
	}

	@Override
	public HashMap<String, String> getHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put(AMConstants.HTTP_HEADER_AUTHORIZATION,
				"Bearer "+AMUtility.SALES_FORCE_TOKEN);
		
		return headers;
	}
	
	public class ArticleDetails extends NetEventObject 
	{

		private static final long serialVersionUID = -765143474037875532L;
		public String mId = "";
		public String mKnowledgeArticleId = "";
		public String mTitle = "";
		public String mResponseDetails = "";

		@Override
		public void getString(StringBuffer buf) {
			
		}

		@Override
		public void setJSONValues(Object values) {
			if (values != null) {
				JSONObject articleObj = (JSONObject) values;
				if (articleObj != null) {
					this.mId = articleObj.optString("id");
					this.mKnowledgeArticleId = articleObj.optString("KnowledgeArticleId");
					this.mTitle = articleObj.optString("Title");
					this.mResponseDetails = articleObj.optString("ResponseDetails");
				}
			}
		}

		@Override
		public void setXMLValues(String values) {
			
		}

		@Override
		public Builder getRequestParams() {
			return null;
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
			return null;
		}

		@Override
		public HashMap<String, String> getHeader() {
			return null;
		}
	}

}
