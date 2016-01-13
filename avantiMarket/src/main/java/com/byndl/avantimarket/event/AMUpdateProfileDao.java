/**
 * @brief       Data Object class for UPDATE_PROFILE response
 * @file        AMUpdateProfileDao.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        15-June-2015
 * @copyright   incedo
 * 
 */
package com.byndl.avantimarket.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief       Data Object class for UPDATE_PROFILE response
 */
public class AMUpdateProfileDao extends NetEventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7524178080934233090L;
	public boolean isSuccess = false;

	public String mFirstName = "";
	public String mLastName = "";
	public String mEmail = "";
	public String mPhoneNumber = "";
	
	private String mEditedFirstName = "";
	private String mEditedLastName = "";
	private String mEditedEmail = "";
	private String mEditedPhoneNumber = "";
	
	/**
	 * Parameterized constructor
	 * @param 
	 */
	public AMUpdateProfileDao(String firstName, String lastName, String email, String phoneNumber){
		this.mEditedFirstName = firstName;
		this.mEditedLastName = lastName;
		this.mEditedEmail = email;
		this.mEditedPhoneNumber = phoneNumber;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJSONValues(Object values) {
		isSuccess = true;
		if (values != null) {
			JSONObject updatedProfileObj = (JSONObject) values;
			if (updatedProfileObj != null) {
				this.mFirstName = updatedProfileObj.optString("firstName");
				this.mLastName = updatedProfileObj.optString("lastName");
				this.mPhoneNumber = updatedProfileObj.optString("phoneNumber");
				this.mEmail = updatedProfileObj.optString("email");

				Editor editor = AMPreferenceManager.getPrefInstance().getPrefs().edit();
				editor.putString("firstname", mFirstName);
				editor.putString("lastname", mLastName);
				editor.putString("contactEmail", mEmail);
				editor.putString("phoneNumber", mPhoneNumber);
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

		InitNetwork.setNetworkInit(AMConstants.BASE_URL, AMConstants.URL_PATH,
				AMConstants.TIME_OUT, AMConstants.SUCCESS_CODE);
		return null;
	}

	@Override
	public List<NameValuePair> getRequestPostParams() {
		return null;
	}

	@Override
	public String getURLPath() {
		// TODO Auto-generated method stub
		return AMConstants.URL_PATH_UPDATE_PROFILE;
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

	@Override
	public List<NameValuePair> getRequestPutParams() {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if(! mEditedFirstName.isEmpty())
			params.add(new BasicNameValuePair("firstName", mEditedFirstName));

		if(! mEditedLastName.isEmpty())
			params.add(new BasicNameValuePair("lastName", mEditedLastName));

		if(! mEditedEmail.isEmpty())
			params.add(new BasicNameValuePair("email", mEditedEmail));

		if(! mEditedPhoneNumber.isEmpty())
			params.add(new BasicNameValuePair("phoneNumber", mEditedPhoneNumber));

		return params;
	}

}
