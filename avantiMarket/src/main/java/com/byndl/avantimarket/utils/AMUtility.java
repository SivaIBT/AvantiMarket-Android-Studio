/**
 * @brief       Utilities class that provides APIs for generating OAuth, Double-to-String conversions etc.
 * @file        AMUtility.java
 * @version     1.10
 * @author      jyotiranjan.pradhan
 * @date        11-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

import com.byndl.avantimarket.app.AMApplication;
import com.byndl.avantimarket.event.AMArticleDetailsDao;
import com.byndl.avantimarket.event.AMArticleListDao.ArticleDetails;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * @brief       Utilities class that provides APIs for generating OAuth, Double-to-String conversions etc.
 */
public class AMUtility {

	private final static String TAG = "AvantiMarket";
	private static String algorithm = "AES";
	public static ArrayList<String> sfConsumerCategories = null;
	public static ArrayList<String> sfConsumerIssues = null;
	public static ArrayList<String> sfFeedbackCategories = null;
	public static boolean updateConsumerCategories = false;
	public static boolean updateConsumerIssues = false;
	
	public static ArrayList<ArticleDetails> mALUsingMarketArticles = null; 
	public static ArrayList<ArticleDetails> mALUsingAppArticles = null; 
	public static ArrayList<ArticleDetails> mALCommonIssuesArticles = null; 

	public static ArrayList<ArticleDetails> sfArticleArray;
	public static AMArticleDetailsDao sfAtricleDetails;

	public static String SALES_FORCE_TOKEN = "";
	public static final String MIXPANEL_TOKEN = "8b221ec6c5e9723b39cfc9032001e98c";
	private static MixpanelAPI mixpanel = null; 
	
/*	public static void setAuthBase64Value(String usrName, String password) {
		String authString = usrName + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		AMConstants.HTTP_HEADER_AUTHORIZATION_VALUE = "Basic" + " "
				+ new String(authEncBytes);
	}
*/
	public static String getRoundedBalance(double amount) {
		String newAmount = null;

		if (amount == 0d) {
			return "0.00";
		} else {
			newAmount = amount + "";
		}

		int dotIndex = newAmount.indexOf(".");

		if (dotIndex != -1)
		{
			if(dotIndex == newAmount.length() - 2) {
				newAmount = newAmount + "0";
			}
		}

		int decimalsCount = newAmount.length() - newAmount.lastIndexOf(".") - 1; 
		if(decimalsCount > 2) {
			newAmount = newAmount.substring(0, newAmount.length() - (decimalsCount - 2));
		}
		return newAmount;
	}

	public static String getTwoDecimals(String amount) {
		String newAmount = amount;

		int dotIndex = newAmount.indexOf(".");

		if (dotIndex != -1)
		{
			if(dotIndex == newAmount.length() - 2) {
				newAmount = newAmount + "0";
			}
		}

		return newAmount;
	}

	public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

		final int iterations = 4832; 

		int outputKeyLength = 1512;
		
		if(passphraseOrPin.length > 4) {
			outputKeyLength = 256;
		}

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
		return secretKey;
	}

	public static String encrypt(String storedPass, Key pinHash) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance(algorithm);
		c.init(Cipher.ENCRYPT_MODE, pinHash);
		byte[] inputBytes = storedPass.getBytes();
		String storedEncryptedPass = new String(Base64.encodeBase64(c.doFinal(inputBytes)));
		Log.d(TAG, "StoredEncryptedPass: "+storedEncryptedPass);
		return storedEncryptedPass;
	}
	
	public static String decrypt(String storedEncryptedPass, Key pinHash) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance(algorithm);
		c.init(Cipher.DECRYPT_MODE, pinHash);
		byte[] decrypt = c.doFinal(Base64.decodeBase64(storedEncryptedPass.getBytes()));
		String storedPass = new String(decrypt);
		return storedPass;
	}
	
	public static MixpanelAPI getMixPanelInstance() {
		if(mixpanel == null) {
			mixpanel = MixpanelAPI.getInstance(AMApplication.getAppContext(), MIXPANEL_TOKEN);
		}
		return mixpanel;
	}
}
