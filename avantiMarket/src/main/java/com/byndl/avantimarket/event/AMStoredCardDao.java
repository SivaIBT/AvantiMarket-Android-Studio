/**
 * @brief       Data Object class for GET_STORED_CARDS response
 * @file        AMStoredCardDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        16-Apr-2015
 * @copyright   incedo inc.
 * 
 */
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
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for GET_STORED_CARDS response
 */
public class AMStoredCardDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7365134448155627407L;
	private ArrayList<CardDao> mALStoredCards = new ArrayList<AMStoredCardDao.CardDao>();
	
	public ArrayList<CardDao> getCards() {
		return mALStoredCards;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		JSONArray cardsArray = (JSONArray) values;
		CardDao cardObj;
		JSONObject cardJSON;
		try {
			if (cardsArray != null) {
				for (int j = 0; j < cardsArray.length(); j++) {
					cardJSON = (JSONObject) cardsArray.get(j);
					cardObj = new CardDao();
					cardObj.setJSONValues(cardJSON);
					this.mALStoredCards.add(cardObj);
				}
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
		return AMConstants.URL_PATH_STORED_CARD;
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
	
	public class CardDao extends NetEventObject {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7533096944148350351L;

		public String mCardId = "";
		public String mCardType = "";
		public String mCardNo = "";
		public String mExpDate = "";
		public boolean mIsAutoChargeCard = false;

		@Override
		public void getString(StringBuffer buf) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setJSONValues(Object values) {
			if (values != null) {
				JSONObject cardObj = (JSONObject) values;
				if (cardObj != null) {
					this.mCardId = cardObj.optString("cardID");
					this.mCardType = cardObj.optString("cardType");
					this.mCardNo = cardObj.optString("lastFour");
					this.mExpDate = cardObj.optString("expDate");
					this.mIsAutoChargeCard = cardObj.optBoolean("isAutoChargeCard");
				}
			}

		}

		@Override
		public void setXMLValues(String values) {
			// TODO Auto-generated method stub

		}

		@Override
		public Builder getRequestParams() {
			// TODO Auto-generated method stub
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

	@Override
	public List<NameValuePair> getRequestPutParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
