package com.txdb.gpmanage.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtil {

	/**
	 * Must adapt the ip pattern
	 * @param ip
	 * @return
	 */
	public static boolean checkIPAddress(String ip) {
		String ipReg = "^(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)((\\d|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))$";
		Pattern ipPattern = Pattern.compile(ipReg);
		return ipPattern.matcher(ip).matches();
	}

	/**
	 * Port must be a number and among from 1 to 65535
	 * @param port
	 * @return
	 */
	public static boolean checkPort(String port) {
		try {
			int portNo = Integer.parseInt(port);
			return (portNo > 0 && portNo < 65536);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Username should not contains one more special characters.
	 * @param username
	 * @return
	 */
	public static boolean checkUsername(String username) {
		if (username.trim().length() <= 0)
			return false;
		String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
		Matcher matcher = Pattern.compile(regEx).matcher(username);
		return !matcher.find();
	}
}
