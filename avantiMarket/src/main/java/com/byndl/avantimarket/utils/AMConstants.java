package com.byndl.avantimarket.utils;

public class AMConstants {
	
	//Base url
	public static final String BASE_URL = "az-stage-003-mobile.mykioskworld.com";
	//Use this if common for all api
	public static final String URL_PATH = "";
	//Connection timeout
	public static final int TIME_OUT = 30;
	//Api success code
	public static final int SUCCESS_CODE = 0;

	// url paths
	public static final String URL_PATH_AUTH = "api/v1/authenticate";
	public static final String URL_PATH_BALANCE = "api/v1/getBalance";
	public static final String URL_PATH_CHARGED_CARD = "api/v1/chargeStoredCard";
	public static final String URL_PATH_HISTORY = "api/v1/getHistory";
	public static final String URL_PATH_RELOAD_DENOMINATION = "api/v1/getReloadDenominations";
	public static final String URL_PATH_STORED_CARD = "api/v1/getStoredCards";
	public static final String URL_PATH_SCAN_CODES = "api/v1/getScanCodes";
	public static final String URL_PATH_ADD_CREDIT_CARD = "api/v1/addStoredCard";
	public static final String URL_PATH_SET_PIN = "api/v1/setUserPin";
	public static final String URL_PATH_FORGOT_PASSWORD = "api/v1/resetPassword";
	public static final String URL_PATH_UPDATE_PROFILE = "api/v1/updateProfile";
	public static final String URL_PATH_CHANGE_PASSWORD = "api/v1/changePassword";
	public static final String URL_PATH_GET_AUTORECHARGE_SETTINGS = "api/v1/getAutoRechargeSettings";
	public static final String URL_PATH_GET_GATEWAY_PARAMETERS = "api/v1/GetGGE4Parameters/";
	public static final String URL_PATH_DELETE_LAST_SAVED_CARD = "api/v1/deleteLastSavedStoredCard";
	public static final String URL_PATH_DELETE_STORED_CARD = "api/v1/deleteStoredCard";
	public static final String URL_PATH_TRANSACTION_DETAIL = "api/v1/getHistoryDetails";
	public static final String URL_PATH_GET_SALES_FORCE_TOKEN = "api/v1/GetSalesForceToken";
	public static final String URL_PATH_ADD_MARKET_CARD = "api/v1/";
	public static final String URL_PATH_DELETE_MARKET_CARD = "api/v1/";
	
	public static String PRIMERY_CARD = "";
	
	//Used for http header
	public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static String HTTP_HEADER_ACCEPT_VALUE = "application/json";
	
	//Current balance amount
	public static double BALANCE_AMOUNT = 0d;
	
	public static final String BASE_URL_SF = "byndlsfapi-integration.byndl.com";
	public static final String URL_SF_CASE_CONSUMER_REQ_CATEGORY = "api/v1_0/Picklist/Case_ConsumerRequestCategory";
	public static final String URL_SF_CASE_CONSUMER_ISSUE_TYPE_CATEGORY = "api/v1_0/Picklist/Case_ConsumerIssueType";
	public static final String URL_SF_CASE_FEEDBACK_CATEGORY = "api/v1_0/Picklist/Case_FeedbackCategory";
	public static final String URL_SF_ARTICLE_LIST = "/api/v1_0/Article/List";
	public static final String URL_SF_ARTICLE_DETAILS = "api/v1_0/Article";
	public static final String URL_SF_CASE_CREATE_CONSUMER_ISSUE = "api/v1_0/Case/Create";
	public static final String URL_SF_CASE_CREATE_CONSUMER_FEEDBACK = "api/v1_0/Case/CreateFeedback";
	public static final String URL_SF_CHECK_CONSUMER_SFDC = "api/v1_0/Consumer";
	public static final String URL_SF_CREATE_CONSUMER_SFDC = "api/v1_0/Consumer/Create";
	public static final String URL_SF_OPERATOR_INFO = "api/v1_0/Operator/ContactUs";
	
	public static final boolean IS_MIX_PANEL_REQUIRED = false;

	public static final String SPECIALS_URL = "6238510f.ngrok.com";
	public static final String URL_PATH_SPECIALS = "test/api/v1.0/rewards";
}
