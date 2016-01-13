/**
 * @brief       Data Object class for ALL SPECIALS response
 * @file        AMSpecialsDao.java
 * @version     1.2
 * @author      siva.rajendhra
 * @date        17-Nov-2015
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

import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for ALL SPECIALS response
 */
public class AMSpecialsDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6829907184111786739L;
	/**
	 * 
	 */
	private ArrayList<SpecialsDao> mALSpecials = new ArrayList<AMSpecialsDao.SpecialsDao>();
	
	public ArrayList<SpecialsDao> getSpecials() {
		return mALSpecials;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		JSONObject obj = (JSONObject) values;
		JSONArray specialsArray = null;
		try {
			specialsArray = (JSONArray) obj.getJSONArray("rewards");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SpecialsDao specialsObj;
		JSONObject specialsJSON;
		try {
			if (specialsArray != null) {
//				Log.e("temp", "specialsArray.length(): "+specialsArray.length());
				for (int j = 0; j < specialsArray.length(); j++) {
					specialsJSON = (JSONObject) specialsArray.get(j);
					specialsObj = new SpecialsDao();
					specialsObj.setJSONValues(specialsJSON);
					this.mALSpecials.add(specialsObj);
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
		return null;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getURLPath() {
		return AMConstants.URL_PATH_SPECIALS;
	}
	
	@Override
	public HashMap<String, String> getHeader() {
		return null;
	}
	
	public class SpecialsDao extends NetEventObject {

		private static final long serialVersionUID = -1895226636473073680L;
		/**
		 * 
		 */

		public String mProdName = "";
		public String mOfferDesc = "";
		public String mStartDate = "";
		public String mEndDate = "";
		public String mMessage = "";
		public String mCurrentAmount = "";
		public String mRequiredAmount = "";
		public String mId = "";

		@Override
		public void getString(StringBuffer buf) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setJSONValues(Object values) {
			if (values != null) {
				JSONObject specialsObj = (JSONObject) values;
				if (specialsObj != null) {
					this.mProdName = specialsObj.optString("ProdName");
					this.mOfferDesc = specialsObj.optString("OfferDesc");
					this.mStartDate = specialsObj.optString("StartDate");
					this.mEndDate = specialsObj.optString("EndDate");
					this.mMessage = specialsObj.optString("Message");
					this.mCurrentAmount = specialsObj.optString("CurrentAmount");
					this.mRequiredAmount = specialsObj.optString("RequiredAmount");
					this.mId = specialsObj.optString("id");
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
