/**
 * @brief       Data Object class for GET_GATEWAY_PARAMETERS response
 * @file        AMGetGatewayParametersDao.java
 * @version     1.15
 * @author      jyotiranjan.pradhan
 * @date        13-Jul-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.event;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.net.Uri.Builder;

import com.byndl.avantimarket.storage.AMPreferenceManager;
import com.byndl.avantimarket.utils.AMConstants;
import com.incedo.network.InitNetwork;
import com.incedo.network.NetEventObject;

/**
 * @brief Data Object class for GET_GATEWAY_PARAMETERS response
 * 
 */
public class AMGetGatewayParametersDao extends NetEventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795602840012744225L;

	public String x_url = "";
	public String x_login = "";
	public String x_show_form = "";
	public String x_fp_sequence = "";
	public String x_fp_hash = "";
	public String x_amount = "";
	public String x_currency_code = "";
	public String x_test_request = "";
	public String x_relay_response = "";
	public String donation_prompt = "";
	public String button_code = "";
	public String mmc_operatorid = "";
	public String mmc_marketuserid = "";
	public String mmc_transactionid = "";
	public String mmc_requesthost = "";
	public String mmc_save_card_info = "";
	public String x_fp_timestamp = "";

	private String mSelectedDenomination;
	/**
	 * Parameterized constructor
	 * @param aselectedDenomination
	 */
	public AMGetGatewayParametersDao(String aSelectedDenomination){
		this.mSelectedDenomination = aSelectedDenomination;
	}

	@Override
	public void getString(StringBuffer buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJSONValues(Object values) {
		if (values != null) {
			JSONObject gatewayParametersObj = (JSONObject) values;
			if (gatewayParametersObj != null) {
				this.x_url = gatewayParametersObj.optString("x_url");
				this.x_login = gatewayParametersObj.optString("x_login");
				this.x_show_form = gatewayParametersObj.optString("x_show_form");
				this.x_fp_sequence = (String)gatewayParametersObj.opt("x_fp_sequence");
				this.x_fp_hash = gatewayParametersObj.optString("x_fp_hash");
				this.x_amount = gatewayParametersObj.optString("x_amount","");
				this.x_currency_code = gatewayParametersObj.optString("x_currency_code");
				this.x_test_request = ""+gatewayParametersObj.opt("x_test_request");
				this.x_relay_response = gatewayParametersObj.optString("x_relay_response");
				this.donation_prompt = gatewayParametersObj.optString("donation_prompt");
				this.button_code = gatewayParametersObj.optString("button_code");
				this.mmc_operatorid = ""+gatewayParametersObj.opt("mmc_operatorid");
				this.mmc_marketuserid = ""+gatewayParametersObj.opt("mmc_marketuserid");
				this.mmc_transactionid = gatewayParametersObj.optString("mmc_transactionid");
				this.mmc_requesthost = gatewayParametersObj.optString("mmc_requesthost");
				this.mmc_save_card_info = gatewayParametersObj.optString("mmc_save_card_info");
				this.x_fp_timestamp = gatewayParametersObj.optString("x_fp_timestamp");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLPath() {
		return AMConstants.URL_PATH_GET_GATEWAY_PARAMETERS + this.mSelectedDenomination;

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
		return null;
	}

}
