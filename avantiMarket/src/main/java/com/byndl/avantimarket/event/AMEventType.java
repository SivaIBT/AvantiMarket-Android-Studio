package com.byndl.avantimarket.event;

public class AMEventType {
	
	public static final int EVENT_POST_TEST = -1;
	public static final int EVENT_AUTHENTICATE = 0;
	public static final int EVENT_GET_BALANCE = 1;
	public static final int EVENT_GET_SCAN_CODES = 2;
	public static final int EVENT_GET_STORED_CARDS = 3;
	public static final int EVENT_GET_RELOAD_DENOMINATIONS = 4;
	public static final int EVENT_GET_HISTORY = 5;
	public static final int EVENT_ADD_STORED_CARD = 6;
	public static final int EVENT_CHARGE_STORED_CARD = 7;
	public static final int EVENT_DELETE_STORED_CARD = 8;
	public static final int EVENT_SET_USER_PIN = 9;
	public static final int EVENT_FORGOT_PASSWORD = 10;
	public static final int EVENT_UPDATE_PROFILE = 11;
	public static final int EVENT_CHANGE_PASSWORD = 12;
	public static final int EVENT_GET_AUTORECHARGE_SETTINGS = 13;
	public static final int EVENT_GET_GATEWAY_PARAMETERS = 14;
	public static final int EVENT_DELETE_LAST_SAVED_CARD = 15;
	public static final int EVENT_GET_TRANSACTION_DETAIL = 16;
	public static final int EVENT_GET_SPECIALS = 17;
	
	// SFDC API Events
	public static final int EVENT_GET_CONSUMER_CATEGORIES = 100;
	public static final int EVENT_GET_CONSUMER_ISSUES = 101;
	public static final int EVENT_GET_FEEDBACK_CATEGORIES = 102;
	public static final int EVENT_POST_CONSUMER_ISSUE = 103;
	public static final int EVENT_POST_FEEDBACK = 104;
	public static final int EVENT_GET_ARTICLE_LINK = 105;
	public static final int EVENT_GET_ARTICLE = 106;
	public static final int EVENT_GET_CONSUMER_SFDC = 107;
	public static final int EVENT_CREATE_CONSUMER_SFDC = 108;
	public static final int EVENT_GET_OPERATOR_INFO = 109;
	public static final int EVENT_GET_SALES_FORCE_TOKEN = 110;
	
}
